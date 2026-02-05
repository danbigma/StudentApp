<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="page">
  <div class="page-header">
    <div>
      <h1 class="page-title">Dashboard</h1>
      <p class="page-subtitle">Student administration overview</p>
    </div>
    <div class="toolbar">
      <button class="btn btn-primary" data-modal-open="addStudentModal">Add Student</button>
      <a class="btn btn-outline" href="${context}/admin/deletestudents">Delete Students</a>
      <form action="${context}/admin/seed" method="post">
        <input type="hidden" name="_csrf" value="${csrfToken}" />
        <input type="hidden" name="count" value="100" />
        <button type="submit" class="btn btn-outline" data-confirm="Seed 100 demo students?">Seed Demo</button>
      </form>
    </div>
  </div>

  <div class="grid grid-3">
    <div class="card">
      <div class="card-body kpi">
        <span class="kpi-label">Total Students</span>
        <span class="kpi-value">${num}</span>
      </div>
    </div>
    <div class="card">
      <div class="card-body kpi">
        <span class="kpi-label">App Status</span>
        <span class="badge js-health">--</span>
      </div>
    </div>
    <div class="card">
      <div class="card-body kpi">
        <span class="kpi-label">DB Status</span>
        <span class="badge js-db">--</span>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header">
      <div>
        <h3 class="card-title">Students</h3>
        <p class="muted" style="margin:4px 0 0 0;">Live table with quick actions</p>
      </div>
      <div class="toolbar">
        <input id="studentSearch" type="text" class="input" placeholder="Search by name or email" style="width:260px;" />
        <select id="studentsPageSize" class="select" style="width:90px;">
          <option value="5">5</option>
          <option value="10" selected>10</option>
          <option value="25">25</option>
          <option value="50">50</option>
        </select>
      </div>
    </div>

    <div class="card-body">
      <canvas id="studentsChart" height="100" aria-label="Students overview chart"></canvas>
    </div>

    <div class="table-wrap">
      <table id="studentsTable" class="table">
        <thead>
          <tr>
            <th data-sort="first">First Name</th>
            <th data-sort="last">Last Name</th>
            <th data-sort="email">Email</th>
            <th class="text-right" style="width:140px;">Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="s" items="${studentList}">
            <tr id="student-row-${s.id}">
              <td>${s.firstName}</td>
              <td>${s.lastName}</td>
              <td>${s.email}</td>
              <td class="text-right">
                <div style="display:flex; gap:8px; justify-content:flex-end;">
                  <button class="btn btn-outline btn-sm" data-modal-open="editStudentModal" title="Edit"
                          data-id="${s.id}" data-first="${s.firstName}" data-last="${s.lastName}" data-email="${s.email}">Edit</button>
                  <form class="js-delete-student" action="${context}/admin" method="post" data-id="${s.id}">
                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="studentId" value="${s.id}" />
                    <button type="submit" class="btn btn-danger btn-sm" title="Delete">Delete</button>
                  </form>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty studentList}">
            <tr><td colspan="4" class="text-center muted">No data</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>

    <div class="card-body">
      <nav aria-label="Students pagination">
        <ul id="studentsPagination" class="pagination"></ul>
      </nav>
    </div>
  </div>
</div>

<div class="modal" id="addStudentModal" aria-hidden="true">
  <div class="modal-backdrop" data-modal-close></div>
  <div class="modal-dialog">
    <div class="modal-header">
      <h3 class="card-title">Add Student</h3>
      <button class="modal-close" type="button" data-modal-close>&times;</button>
    </div>
    <form action="${context}/admin?action=add" method="post">
      <input type="hidden" name="_csrf" value="${csrfToken}" />
      <div class="modal-body stack">
        <label class="stack">
          <span class="muted">First Name</span>
          <input type="text" class="input" id="firstName" name="firstName" required />
        </label>
        <label class="stack">
          <span class="muted">Last Name</span>
          <input type="text" class="input" id="lastName" name="lastName" required />
        </label>
        <label class="stack">
          <span class="muted">Email</span>
          <input type="email" class="input" id="email" name="email" required />
        </label>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Student</button>
      </div>
    </form>
  </div>
</div>

<div class="modal" id="editStudentModal" aria-hidden="true">
  <div class="modal-backdrop" data-modal-close></div>
  <div class="modal-dialog">
    <div class="modal-header">
      <h3 class="card-title">Edit Student</h3>
      <button class="modal-close" type="button" data-modal-close>&times;</button>
    </div>
    <form action="${context}/admin?action=update" method="post">
      <input type="hidden" name="_csrf" value="${csrfToken}" />
      <input type="hidden" id="editStudentId" name="studentId" />
      <div class="modal-body stack">
        <label class="stack">
          <span class="muted">First Name</span>
          <input type="text" class="input" id="editFirstName" name="firstName" required />
        </label>
        <label class="stack">
          <span class="muted">Last Name</span>
          <input type="text" class="input" id="editLastName" name="lastName" required />
        </label>
        <label class="stack">
          <span class="muted">Email</span>
          <input type="email" class="input" id="editEmail" name="email" required />
        </label>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline" data-modal-close>Cancel</button>
        <button type="submit" class="btn btn-primary">Save Changes</button>
      </div>
    </form>
  </div>
</div>

<jsp:include page="../footer.jsp" />
