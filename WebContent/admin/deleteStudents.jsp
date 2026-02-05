<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container py-4">
  <div class="card">
    <div class="card-header d-flex justify-content-between align-items-center gap-2 flex-wrap">
      <h5 class="mb-0">Delete Students</h5>
      <div class="d-flex gap-2 align-items-center">
        <input id="bulkSearch" class="form-control form-control-sm" type="text" placeholder="Search" style="width:220px;" />
        <select id="bulkPageSize" class="form-select form-select-sm" style="width:90px;">
          <option value="10" selected>10</option>
          <option value="25">25</option>
          <option value="50">50</option>
        </select>
        <span id="selectedCountBadge" class="badge text-bg-secondary">0 selected</span>
        <button type="submit" form="bulkDeleteForm" id="buttonDelete" class="btn btn-sm btn-danger" disabled>Delete</button>
      </div>
    </div>
    <div class="card-body p-0">
      <form id="bulkDeleteForm" action="deletestudents" method="post" data-confirm="Delete selected students?">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <input type="hidden" name="action" value="delete" />
        <div class="table-responsive">
          <table id="bulkTable" class="table table-hover table-sm mb-0">
            <thead>
              <tr>
                <th class="text-center" style="width:48px;"><input class="form-check-input" type="checkbox" name="students" aria-label="Select all" /></th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email</th>
              </tr>
            </thead>
            <tbody id="bulkTableBody">
              <c:forEach var="tempStudent" items="${studentList}">
                <tr>
                  <td class="text-center"><input class="form-check-input" type="checkbox" name="student" value="${tempStudent.id}" aria-label="Select row" /></td>
                  <td>${tempStudent.firstName}</td>
                  <td>${tempStudent.lastName}</td>
                  <td>${tempStudent.email}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="../footer.jsp" />
