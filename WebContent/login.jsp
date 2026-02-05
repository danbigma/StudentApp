<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">Login</h5>
        </div>
        <div class="card-body">
          <form action="login" method="post">
            <input type="hidden" name="_csrf" value="${csrfToken}" />
            <div class="mb-3">
              <label for="login" class="form-label">User</label>
              <input id="login" class="form-control" type="text" name="login" />
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">Password</label>
              <input id="password" class="form-control" type="password" name="password" />
            </div>
            <div class="form-check mb-3">
              <input class="form-check-input" type="checkbox" name="savesession" value="true" id="savesession" />
              <label class="form-check-label" for="savesession">Remember session</label>
            </div>
            <button type="submit" class="btn btn-primary w-100">Sign in</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />
