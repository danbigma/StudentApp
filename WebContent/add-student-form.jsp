<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

<div class="container">
	<div class="row">
		<div class="col"></div>
		<div class="col-md-8">

	<h3>Add new student</h3>
		
		<form action="studentController" method="GET">
		
			<input type="hidden" name="command" value="add" />
			
			<table class="thead-dark">
				<tbody>
					<tr>
						<td><label>First name:</label></td>
						<td><input type="text" class="form-control" name="firstName" /></td>
					</tr>
					<tr>
						<td><label>Last name:</label></td>
						<td><input type="text" class="form-control" name="lastName" /></td>
					</tr>
					<tr>
						<td><label>Email:</label></td>
						<td><input type="text" class="form-control" name="email" /></td>
					</tr>
					<tr>
						<td><label></label></td>
						<td><input type="submit" class="form-control" value="Save"/></td>
					</tr>
				</tbody>
			</table>
		</form>
		
		<div style="clear: both;"></div>
		
		<p>
			<a href="studentController">Back to List</a>
		</p>
		</div>
		<div class="col"></div>
	</div>
</div>

<jsp:include page="footer.jsp" />











