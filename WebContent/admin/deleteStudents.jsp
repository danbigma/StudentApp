<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="page">
  <div class="page-header">
    <div>
      <h1 class="page-title">Delete Students</h1>
      <p class="page-subtitle">Bulk delete with quick filters</p>
    </div>
    <div class="toolbar">
      <input id="bulkSearch" class="input" type="text" placeholder="Search" style="width:220px;" />
      <span id="selectedCountBadge" class="badge">0 selected</span>
      <button type="submit" form="bulkDeleteForm" id="buttonDelete" class="btn btn-danger" disabled>Delete</button>
    </div>
  </div>

  <div class="card">
    <div class="card-header">
      <h3 class="card-title">Student List</h3>
    </div>
    <div class="card-body" style="padding:0;">
      <form id="bulkDeleteForm" action="deletestudents" method="post" data-confirm="Delete selected students?">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <input type="hidden" name="action" value="delete" />
        <div class="table-wrap">
          <table id="bulkTable" class="table">
            <thead>
              <tr>
                <th style="width:48px;"><input type="checkbox" name="students" aria-label="Select all" /></th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email</th>
              </tr>
            </thead>
            <tbody id="bulkTableBody">
              <c:forEach var="tempStudent" items="${studentList}">
                <tr>
                  <td><input type="checkbox" name="student" value="${tempStudent.id}" aria-label="Select row" /></td>
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
