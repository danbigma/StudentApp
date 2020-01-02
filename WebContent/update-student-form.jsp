<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

<div class="container">
	<div class="row">
		<div class="col"></div>
		<div class="col-md-8">
			<form action="studentController" method="get">
			
				<input type="hidden" name="command" value="update" />
				<input type="hidden" name="studentId" value="${THE_STUDENT.id}" />

				<table class="thead-dark">
					<input type="button" class="btn btn-link" value="Back to List" onclick="window.location.href='studentController';" />
					<tbody>
						<tr>
							<div class="form-group">
								<td><label for="firstName">First name</label></td>
								<td><input class="form-control" type="text" name="firstName" value="${THE_STUDENT.firstName}" /></td>
							</div>
						</tr>

						<tr>
							<td><label for="lastName">Last name:</label></td>
							<td><input class="form-control" type="text" name="lastName"
								value="${THE_STUDENT.lastName}" /></td>
						</tr>

						<tr>
							<td><label for="email">Email:</label></td>
							<td><input class="form-control" type="text" name="email"
								value="${THE_STUDENT.email}" /></td>
						</tr>

						<tr>
							<td><label></label></td>
							<td><input type="submit" class="form-control"
								value="Save" /></td>
						</tr>

					</tbody>
				</table>
			</form>

			<div style="clear: both;"></div>
		</div>
		<div class="col"></div>
	</div>
</div>

<jsp:include page="footer.jsp" />