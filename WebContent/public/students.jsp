<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container py-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Students</h3>
    <span class="text-muted">Public listing</span>
  </div>

  <div id="cardsGrid" class="row g-3">
    <c:forEach var="s" items="${students}">
      <div class="col-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card h-100">
          <div class="card-body">
            <h5 class="card-title mb-1">${s.firstName} ${s.lastName}</h5>
            <p class="text-muted small mb-3">${s.email}</p>
            <span class="badge text-bg-secondary">ID ${s.id}</span>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>

  <div class="text-center mt-4">
    <button id="loadMore" class="btn btn-primary">
      <span class="spinner-border spinner-border-sm me-2 d-none" role="status" aria-hidden="true"></span>
      Load more
    </button>
  </div>
</div>

<script>
(() => {
  const btn = document.getElementById('loadMore');
  const grid = document.getElementById('cardsGrid');
  if (!btn || !grid) return;

  let next = parseInt('${nextOffset}', 10) || 0;
  const limit = 12;
  const spinner = btn.querySelector('.spinner-border');

  const escapeHtml = (t) => {
    const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return String(t || '').replace(/[&<>"']/g, (m) => map[m]);
  };

  const createCardHtml = (s) => `
    <div class="col-12 col-sm-6 col-md-4 col-lg-3">
      <div class="card h-100">
        <div class="card-body">
          <h5 class="card-title mb-1">${escapeHtml(s.firstName + ' ' + s.lastName)}</h5>
          <p class="text-muted small mb-3">${escapeHtml(s.email)}</p>
          <span class="badge text-bg-secondary">ID ${s.id}</span>
        </div>
      </div>
    </div>`;

  const loadMoreStudents = async () => {
    btn.disabled = true;
    spinner?.classList.remove('d-none');

    try {
      const response = await fetch(`${context}/students?format=json&offset=${next}&limit=${limit}`, { credentials: 'same-origin' });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const json = await response.json();

      if (!json?.items?.length) {
        btn.textContent = 'No more students';
        return;
      }

      grid.insertAdjacentHTML('beforeend', json.items.map(createCardHtml).join(''));
      next = json.nextOffset ?? (next + json.items.length);
      btn.disabled = false;
    } catch (error) {
      console.error('Failed to load more students:', error);
      btn.textContent = 'Load failed';
    } finally {
      spinner?.classList.add('d-none');
    }
  };

  btn.addEventListener('click', loadMoreStudents);
})();
</script>

<jsp:include page="../footer.jsp" />
