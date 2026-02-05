<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="page">
  <div class="page-header">
    <div>
      <h1 class="page-title">Students</h1>
      <p class="page-subtitle">Public directory</p>
    </div>
  </div>

  <div class="grid grid-4" id="cardsGrid">
    <c:forEach var="s" items="${students}">
      <div class="card">
        <div class="card-body">
          <h3 class="card-title">${s.firstName} ${s.lastName}</h3>
          <p class="muted">${s.email}</p>
          <span class="badge">ID ${s.id}</span>
        </div>
      </div>
    </c:forEach>
  </div>

  <div class="text-center">
    <button id="loadMore" class="btn btn-primary">
      <span class="spinner" style="display:none;" aria-hidden="true"></span>
      <span>Load more</span>
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
  const spinner = btn.querySelector('.spinner');

  const escapeHtml = (t) => {
    const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return String(t || '').replace(/[&<>"']/g, (m) => map[m]);
  };

  const createCardHtml = (s) => `
    <div class="card">
      <div class="card-body">
        <h3 class="card-title">${escapeHtml(s.firstName + ' ' + s.lastName)}</h3>
        <p class="muted">${escapeHtml(s.email)}</p>
        <span class="badge">ID ${s.id}</span>
      </div>
    </div>`;

  const loadMoreStudents = async () => {
    btn.disabled = true;
    spinner.style.display = 'inline-block';

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
      spinner.style.display = 'none';
    }
  };

  btn.addEventListener('click', loadMoreStudents);
})();
</script>

<jsp:include page="../footer.jsp" />
