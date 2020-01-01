<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

<div class="container">

	<div class="row">
		<div class="col"></div>
		<div class="col-md-8">

			<!-- put new button: Add Student -->

			<table class="table">

				<thead class="thead-dark">
					<tr>
						<th scope="col">Name</th>
						<th scope="col"></th>
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
		<div class="col"></div>
	</div>

</div>