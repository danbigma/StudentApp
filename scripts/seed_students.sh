#!/usr/bin/env bash
set -euo pipefail

# Inserts random demo students into the MySQL database.
# Usage: scripts/seed_students.sh [COUNT]

COUNT=${1:-100}

# Defaults mirror docker-compose.yml
DB_HOST=${DB_HOST:-db}
DB_PORT=${DB_PORT:-3306}
DB_NAME=${DB_NAME:-studentdb}
DB_USER=${DB_USER:-user}
DB_PASSWORD=${DB_PASSWORD:-user_password}

SQL=$(cat <<SQL
-- Seed ${COUNT} random students
WITH RECURSIVE seq(n) AS (
  SELECT 1
  UNION ALL
  SELECT n+1 FROM seq WHERE n < ${COUNT}
)
INSERT INTO student(first_name, last_name, email)
SELECT
  fn AS first_name,
  ln AS last_name,
  CONCAT(
    LOWER(REPLACE(fn,' ','')), '.', LOWER(REPLACE(ln,' ','')), '+', LPAD(n,3,'0'), '@',
    JSON_UNQUOTE(JSON_EXTRACT(domains, CONCAT('$.', FLOOR(RAND()*JSON_LENGTH(domains)))))
  ) AS email
FROM seq
JOIN (
  SELECT JSON_ARRAY(
    'Liam','Olivia','Noah','Emma','Ava','Sophia','Isabella','Mia','Lucas','Mateo',
    'Sofia','Valentina','Camila','Daniel','Diego','Elena','Lucia','Maria','Juan','Nicolas'
  ) AS firsts
) f ON 1=1
JOIN (
  SELECT JSON_ARRAY(
    'Garcia','Martinez','Lopez','Hernandez','Gonzalez','Rodriguez','Perez','Sanchez','Ramirez','Torres',
    'Flores','Rivera','Gomez','Diaz','Vazquez','Romero','Suarez','Molina','Navarro','Castro'
  ) AS lasts
) l ON 1=1
JOIN (
  SELECT JSON_ARRAY('gmail.com','outlook.com','yahoo.com','example.com') AS domains
) d ON 1=1
CROSS JOIN (
  SELECT 
    JSON_UNQUOTE(JSON_EXTRACT(firsts, CONCAT('$.', FLOOR(RAND()*JSON_LENGTH(firsts))))) AS fn,
    JSON_UNQUOTE(JSON_EXTRACT(lasts, CONCAT('$.', FLOOR(RAND()*JSON_LENGTH(lasts))))) AS ln,
    domains
  FROM f, l, d
) names;
SQL
)

run_with_docker() {
  # Prefer `docker compose`, fallback to `docker-compose`.
  if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    echo "[seed] Using docker compose to reach DB container..." >&2
    printf "%s" "$SQL" | docker compose exec -T db mysql -u"$DB_USER" -p"$DB_PASSWORD" -h "$DB_HOST" -P "$DB_PORT" "$DB_NAME"
  elif command -v docker-compose >/dev/null 2>&1; then
    echo "[seed] Using docker-compose to reach DB container..." >&2
    printf "%s" "$SQL" | docker-compose exec -T db mysql -u"$DB_USER" -p"$DB_PASSWORD" -h "$DB_HOST" -P "$DB_PORT" "$DB_NAME"
  else
    return 1
  fi
}

run_locally() {
  if ! command -v mysql >/dev/null 2>&1; then
    echo "[seed] mysql client not found. Install it or run via docker compose." >&2
    return 1
  fi
  echo "[seed] Using local mysql client to reach ${DB_HOST}:${DB_PORT}/${DB_NAME}..." >&2
  printf "%s" "$SQL" | mysql -u"$DB_USER" -p"$DB_PASSWORD" -h "$DB_HOST" -P "$DB_PORT" "$DB_NAME"
}

if ! run_with_docker; then
  run_locally
fi

echo "[seed] Inserted ${COUNT} students." >&2

