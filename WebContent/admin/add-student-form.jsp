<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<jsp:include page="../header.jsp" />

<div class="container">
	<div class="row">
		<div class="col"></div>
		<div class="col-md-8">

			<h3>Add new student</h3>
			<p>
				<a href="${context}/admin">Back to List</a>
			</p>
			<form action="${context}/admin" method="POST">

				<input type="hidden" name="action" value="add" />

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
							<td><input type="submit" class="form-control" value="Save" /></td>
						</tr>
					</tbody>
				</table>
			</form>

			<div style="clear: both;"></div>
		</div>
		<div class="col"></div>
	</div>
</div>

<jsp:include page="../footer.jsp" />











