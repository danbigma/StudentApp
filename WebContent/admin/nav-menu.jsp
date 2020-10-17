<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<div class="container">

    <div class="row">
        <div class="col-md-12">
        	<!-- put new button: Add Student -->
			<input type="button" class="btn btn-link" value="List"
				onclick="window.location.href='${context}/admin';" />
			<!-- put new button: Add Student -->
			
			<input type="button" class="btn btn-link" value="Add Student"
				onclick="window.location.href='${context}/admin/add-student-form.jsp';" />
				
			<input type="button" class="btn btn-link" value="Delete various"
				onclick="window.location.href='${context}/admin/deletestudents';" />
				
			<input type="button" class="btn btn-link" value="Client Information"
				onclick="window.location.href='${context}/admin/clientInformation';" />
			
			<input type="button" class="btn btn-link" value="Logout"
				onclick="window.location.href='${context}/logout';" />
				
        </div>
    </div>

</div>
