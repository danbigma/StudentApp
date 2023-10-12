<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<!-- Agrega un mensaje de error en caso de problemas -->
			<c:if test="${empty infoList}">
				<div class="alert alert-danger" role="alert">Ha ocurrido un
					error al obtener la información del cliente.</div>
			</c:if>

			<!-- Botón de navegación -->
			<jsp:include page="nav-menu.jsp" />

			<table class="table table-striped">
				<thead class="thead-dark">
					<tr>
						<th scope="col">Nombre</th>
						<th scope="col">Valor</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="tempInfoList" items="${infoList}">
						<tr>
							<td>${tempInfoList.key}</td>
							<td>${tempInfoList.value}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
