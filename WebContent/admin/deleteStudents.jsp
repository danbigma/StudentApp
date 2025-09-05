<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<jsp:include page="../header.jsp" />

<div class="container my-3">
  <div class="row">
    <div class="col-md-12">
      <div class="card shadow-sm">
        <div class="card-header d-flex justify-content-between align-items-center">
          <div>
            <h5 class="mb-0">Delete Multiple Students</h5>
            <small class="text-muted">Select the records to remove and confirm</small>
          </div>
          <div class="form-inline">
            <input id="bulkSearch" class="form-control form-control-sm mr-2" type="text" placeholder="Search..." />
            <button type="submit" form="bulkDeleteForm" id="buttonDelete" class="btn btn-sm btn-danger" disabled data-toggle="tooltip" title="Delete selected" aria-label="Delete selected"><i class="bi bi-trash"></i><span class="sr-only">Delete selected</span></button>
          </div>
        </div>
        <div class="card-body p-0">
          <form id="bulkDeleteForm" action="deletestudents" method="post" data-confirm="Delete selected students?">
            <input type="hidden" name="_csrf" value="${csrfToken}" />
            <input type="hidden" name="action" value="delete" />
            <table id="bulkTable" class="table table-hover table-sm mb-0">
              <thead class="thead-light">
                <tr>
                  <th scope="col" style="width:36px;">
                    <input type="checkbox" name="students"/>
                  </th>
                  <th scope="col">First Name</th>
                  <th scope="col">Last Name</th>
                  <th scope="col">Email</th>
                </tr>
              </thead>
              <tbody id="bulkTableBody">
                <c:forEach var="tempStudent" items="${studentList}">
                  <tr>
                    <td scope="col">
                      <input type="checkbox" name="student" value="${tempStudent.id}" />
                    </td>
                    <td>${tempStudent.firstName}</td>
                    <td>${tempStudent.lastName}</td>
                    <td>${tempStudent.email}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>


<jsp:include page="../footer.jsp" />









