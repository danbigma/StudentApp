<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div class="page" style="max-width: 420px; margin: 32px auto;">
  <div class="card">
    <div class="card-header">
      <h3 class="card-title">Sign in</h3>
      <span class="muted">Admin access</span>
    </div>
    <div class="card-body">
      <form action="login" method="post" class="stack">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <label class="stack">
          <span class="muted">User</span>
          <input id="login" class="input" type="text" name="login" />
        </label>
        <label class="stack">
          <span class="muted">Password</span>
          <input id="password" class="input" type="password" name="password" />
        </label>
        <label style="display:flex; align-items:center; gap:8px;">
          <input type="checkbox" name="savesession" value="true" />
          <span class="muted">Remember session</span>
        </label>
        <button type="submit" class="btn btn-primary">Sign in</button>
      </form>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />
