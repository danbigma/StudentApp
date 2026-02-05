<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container py-4">
  <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <div>
      <h3 class="mb-0">Dashboard</h3>
      <small class="text-muted">Student administration</small>
    </div>
    <div class="d-flex flex-wrap gap-2">
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addStudentModal">Add Student</button>
      <a class="btn btn-outline-secondary" href="${context}/admin/deletestudents">Delete Students</a>
      <form action="${context}/admin/seed" method="post" class="d-inline">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <input type="hidden" name="count" value="100" />
        <button type="submit" class="btn btn-outline-secondary" data-confirm="Seed 100 demo students?">Seed Demo</button>
      </form>
    </div>
  </div>

  <div class="row g-3 mb-3">
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-body">
          <div class="text-muted">Total Students</div>
          <div class="display-6 fw-semibold">${num}</div>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-body">
          <div class="text-muted">App Status</div>
          <span class="badge text-bg-secondary js-health">--</span>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-body">
          <div class="text-muted">DB Status</div>
          <span class="badge text-bg-secondary js-db">--</span>
        </div>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header d-flex flex-wrap justify-content-between align-items-center gap-2">
      <h5 class="mb-0">Students</h5>
      <div class="d-flex align-items-center gap-2">
        <input id="studentSearch" type="text" class="form-control form-control-sm" placeholder="Search by name or email" style="width:260px;" />
        <select id="studentsPageSize" class="form-select form-select-sm" style="width:90px;">
          <option value="5">5</option>
          <option value="10" selected>10</option>
          <option value="25">25</option>
          <option value="50">50</option>
        </select>
      </div>
    </div>

    <div class="card-body pt-3">
      <canvas id="studentsChart" height="100" aria-label="Students overview chart"></canvas>
    </div>

    <div class="table-responsive">
      <table id="studentsTable" class="table table-hover mb-0">
        <thead>
          <tr>
            <th data-sort="first">First Name</th>
            <th data-sort="last">Last Name</th>
            <th data-sort="email">Email</th>
            <th class="text-end" style="width:140px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="s" items="${studentList}">
            <tr id="student-row-${s.id}">
              <td>${s.firstName}</td>
              <td>${s.lastName}</td>
              <td>${s.email}</td>
              <td class="text-end">
                <div class="d-inline-flex align-items-center gap-2" role="group" aria-label="Actions">
                  <button class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#editStudentModal" title="Edit"
                          data-id="${s.id}" data-first="${s.firstName}" data-last="${s.lastName}" data-email="${s.email}">
                    <i class="bi bi-pencil-square"></i>
                  </button>
                  <form class="js-delete-student d-inline" action="${context}/admin" method="post" data-id="${s.id}">
                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="studentId" value="${s.id}" />
                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                      <i class="bi bi-trash"></i>
                    </button>
                  </form>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty studentList}">
            <tr><td colspan="4" class="text-center text-muted">No data</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>

    <div class="card-body py-2">
      <nav aria-label="Students pagination">
        <ul id="studentsPagination" class="pagination pagination-sm justify-content-end my-0"></ul>
      </nav>
    </div>
  </div>
</div>

<div class="modal fade" id="addStudentModal" tabindex="-1" aria-labelledby="addStudentLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="addStudentLabel">Add Student</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form action="${context}/admin?action=add" method="post">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <div class="modal-body">
          <div class="row g-2">
            <div class="col-md-6">
              <label for="firstName" class="form-label">First Name</label>
              <input type="text" class="form-control" id="firstName" name="firstName" required />
            </div>
            <div class="col-md-6">
              <label for="lastName" class="form-label">Last Name</label>
              <input type="text" class="form-control" id="lastName" name="lastName" required />
            </div>
          </div>
          <div class="mt-2">
            <label for="email" class="form-label">Email</label>
            <input type="email" class="form-control" id="email" name="email" required />
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Student</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="editStudentModal" tabindex="-1" aria-labelledby="editStudentLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editStudentLabel">Edit Student</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form action="${context}/admin?action=update" method="post">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <input type="hidden" id="editStudentId" name="studentId" />
        <div class="modal-body">
          <div class="row g-2">
            <div class="col-md-6">
              <label for="editFirstName" class="form-label">First Name</label>
              <input type="text" class="form-control" id="editFirstName" name="firstName" required />
            </div>
            <div class="col-md-6">
              <label for="editLastName" class="form-label">Last Name</label>
              <input type="text" class="form-control" id="editLastName" name="lastName" required />
            </div>
          </div>
          <div class="mt-2">
            <label for="editEmail" class="form-label">Email</label>
            <input type="email" class="form-control" id="editEmail" name="email" required />
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Changes</button>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="../footer.jsp" />
