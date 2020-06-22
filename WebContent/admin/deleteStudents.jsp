<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<jsp:include page="../header.jsp" />

<div class="container">
    <div class="row">
        <div class="col"></div>
        <div class="col-md-8">
			<form action="deletestudents" method="post">
				<input type="hidden" name="action" value="delete" />
				<table class="table table-sm">
					<input type="button" class="btn btn-link" value="Back to List" onclick="window.location.href='${context}/admin';" />
	                <thead class="thead-dark">
	                    <tr>
	                        <th scope="col">
	                        	<input type="checkbox" name="students"/>
	                        </th>
	                        <th scope="col">First Name</th>
	                        <th scope="col">Last Name</th>
	                        <th scope="col">Email</th>
	                    </tr>
	                </thead>
	                <tbody>
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
	            <input type="submit" class="form-control" value="Delete" />
	    	</form>
        </div>
        <div class="col"></div>
    </div>
</div>

<script>

	let checkboxAllCheckbox = document.getElementsByName("students");
	checkboxAllCheckbox[0].onchange = selectAllCheckbox;
	
	function selectAllCheckbox() {
		let inputsCheckbox = document.getElementsByName("student");
		
		for (var i=0; i<inputsCheckbox.length; i++) {
			if (!checkboxAllCheckbox[0].checked) {
				inputsCheckbox[i].checked = false;
			} else {
				inputsCheckbox[i].checked = true;
			}
		}
	}
 	
</script>

<jsp:include page="../footer.jsp" />

















