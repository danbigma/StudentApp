<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<jsp:include page="../header.jsp" />

<div class="container">

	<div class="row">
		<div class="col-md-12">

			<!-- put new button: Add Student -->

			<table class="table table-sm">
				<input type="button" class="btn btn-link" value="Back to List"
					onclick="window.location.href='${context}/admin';" />

				<thead class="thead-dark">
					<tr>
						<th scope="col">Name</th>
						<th scope="col">Value</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="tempInfoList" items="${ infoList }">
						<tr>
							<td>${ tempInfoList.key }</td>
							<td>${ tempInfoList.value }</td>
						</tr>
					</c:forEach>
				</tbody>

			</table>
		</div>
	</div>

</div>