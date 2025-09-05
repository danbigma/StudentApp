<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container my-3">

  <div class="row mt-3">
    <div class="col-md-4 mb-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <h5 class="card-title">Total Students</h5>
          <p class="display-4 mb-0">${num}</p>
        </div>
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <h5 class="card-title">App Status</h5>
          <span id="healthStatus" class="badge badge-secondary">--</span>
        </div>
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <div class="card shadow-sm">
        <div class="card-body">
          <h5 class="card-title">DB Status</h5>
          <span id="dbStatus" class="badge badge-secondary">--</span>
        </div>
      </div>
    </div>
  </div>

  <div class="row mt-2">
    <div class="col-md-12">
      <div class="card shadow-sm">
        <div class="card-header d-flex justify-content-between align-items-center">
          <div>
            <h5 class="mb-0">Students</h5>
            <small class="text-muted">Manage your students — search, sort and add new records</small>
          </div>
          <button class="btn btn-sm btn-primary" data-toggle="modal" data-target="#addStudentModal" data-toggle2="tooltip" title="Add Student" aria-label="Add Student">
            <i class="bi bi-person-plus-fill"></i>
          </button>
        </div>
        <div class="card-body">
          <div class="form-row mt-1 align-items-center">
            <div class="col-sm-6 my-1">
              <input id="studentSearch" type="text" class="form-control" placeholder="Search by name or email..."/>
            </div>
            <div class="col-auto my-1">
              <label class="mr-2 mb-0" for="studentsPageSize">Rows</label>
              <select id="studentsPageSize" class="custom-select custom-select-sm">
                <option value="5">5</option>
                <option value="10" selected>10</option>
                <option value="25">25</option>
                <option value="50">50</option>
              </select>
            </div>
          </div>
        </div>
        <div class="card-body pt-0">
          <canvas id="studentsChart" height="100" aria-label="Students overview chart"></canvas>
        </div>
        <div class="card-body pt-0 pb-0">
          <table id="studentsTable" class="table table-hover mb-0">
            <thead class="thead-light">
              <tr>
                <th data-sort="first">First Name</th>
                <th data-sort="last">Last Name</th>
                <th data-sort="email">Email</th>
                <th style="width:120px;">Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="s" items="${studentList}">
                <tr id="student-row-${s.id}">
                  <td>${s.firstName}</td>
                  <td>${s.lastName}</td>
                  <td>${s.email}</td>
                  <td>
                    <div class="btn-group btn-group-sm" role="group" aria-label="Actions">
                      <button class="btn btn-outline-secondary" data-toggle="modal" data-target="#editStudentModal" data-toggle2="tooltip" title="Edit" aria-label="Edit"
                            data-id="${s.id}" data-first="${s.firstName}" data-last="${s.lastName}" data-email="${s.email}"><i class="bi bi-pencil-square"></i><span class="sr-only">Edit</span></button>
                      <form class="js-delete-student" action="${context}/admin" method="post" data-id="${s.id}" style="display:inline;">
                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                        <input type="hidden" name="action" value="delete" />
                        <input type="hidden" name="studentId" value="${s.id}" />
                        <button type="submit" class="btn btn-outline-danger" data-toggle="tooltip" title="Delete" aria-label="Delete"><i class="bi bi-trash"></i><span class="sr-only">Delete</span></button>
                      </form>
                    </div>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty studentList}">
                <tr><td colspan="3" class="text-center text-muted">No data</td></tr>
              </c:if>
            </tbody>
          </table>
          <nav aria-label="Students pagination">
            <ul id="studentsPagination" class="pagination pagination-sm pagination-modern justify-content-end my-2"></ul>
          </nav>
        </div>
      </div>
    </div>
</div>

<!-- Add Student Modal (improved styles) -->
<div class="modal fade" id="addStudentModal" tabindex="-1" role="dialog" aria-labelledby="addStudentLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h5 class="modal-title" id="addStudentLabel">Add Student</h5>
          <small class="text-muted">Create a new student record</small>
        </div>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form action="${context}/admin?action=add" method="post">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group col-md-6">
              <label for="firstName">First Name</label>
              <input type="text" class="form-control" id="firstName" name="firstName" placeholder="Jane" required />
              <small class="form-text text-muted">Given name</small>
            </div>
            <div class="form-group col-md-6">
              <label for="lastName">Last Name</label>
              <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Doe" required />
              <small class="form-text text-muted">Family name</small>
            </div>
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" class="form-control" id="email" name="email" placeholder="jane.doe@example.com" required />
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Student</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Student Modal -->
<div class="modal fade" id="editStudentModal" tabindex="-1" role="dialog" aria-labelledby="editStudentLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h5 class="modal-title" id="editStudentLabel">Edit Student</h5>
          <small class="text-muted">Update student information</small>
        </div>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form action="${context}/admin?action=update" method="post">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <input type="hidden" id="editStudentId" name="studentId" />
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group col-md-6">
              <label for="editFirstName">First Name</label>
              <input type="text" class="form-control" id="editFirstName" name="firstName" required />
            </div>
            <div class="form-group col-md-6">
              <label for="editLastName">Last Name</label>
              <input type="text" class="form-control" id="editLastName" name="lastName" required />
            </div>
          </div>
          <div class="form-group">
            <label for="editEmail">Email</label>
            <input type="email" class="form-control" id="editEmail" name="email" required />
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Changes</button>
        </div>
      </form>
    </div>
  </div>
</div>
</div>

<jsp:include page="../footer.jsp" />
