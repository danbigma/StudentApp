# StudentApp Architecture (Servlets + JSP)

## Goal
Clean layered architecture while keeping Servlets + JSP for delivery speed and maintainability.

## Layers
1. `controller` (`com.studentapp.controller`)
- Accept/parse HTTP requests.
- Build HTTP responses, redirects and view rendering.
- No SQL and minimal business rules.

2. `service` (`com.studentapp.student.service`)
- Business use cases.
- Input validation and business constraints.
- Calls repository interfaces.

3. `repository` (`com.studentapp.student.repository`)
- Data persistence abstraction (`StudentRepository`).
- JDBC implementation (`JdbcStudentRepository`).

4. `db/jdbc` (existing)
- SQL and low-level DB access.
- Retained to avoid breaking changes; used by repository implementation.

## Flow
HTTP request -> Servlet controller -> Service -> Repository -> DB

## Migration strategy
- Keep current pages and endpoints.
- Move logic incrementally from controllers to services.
- Add tests at service layer first.

## Current status
- Student module (`service` + `repository`) implemented.
- Main student servlets migrated to service layer.
