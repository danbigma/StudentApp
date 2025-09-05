<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<div class="container">

    <div class="row">
        <div class="col-md-12">
        	<!-- put new button: Add Student -->
			<a class="btn btn-link" href="${context}/admin">List</a>
			<a class="btn btn-link" href="${context}/admin?action=dashboard">Dashboard</a>
			<!-- put new button: Add Student -->
			
			<a class="btn btn-link" href="${context}/admin/add-student-form.jsp">Add Student</a>
				
			<a class="btn btn-link" href="${context}/admin/deletestudents">Delete various</a>
				
			<a class="btn btn-link" href="${context}/admin/clientInformation">Client Information</a>
			
			<form id="seedForm" action="${context}/admin/seed" method="post" style="display:inline; margin-left:8px;">
				<input type="hidden" name="_csrf" value="${csrfToken}" />
				<input type="hidden" name="count" value="100" />
				<button type="submit" class="btn btn-link" data-confirm="Seed 100 demo students?">Seed demo data</button>
			</form>

            <form id="logoutForm" action="${context}/logout" method="post" style="display:inline;">
                <input type="hidden" name="_csrf" value="${csrfToken}" />
                <button type="submit" class="btn btn-link">Logout</button>
            </form>
				
        </div>
    </div>

</div>
