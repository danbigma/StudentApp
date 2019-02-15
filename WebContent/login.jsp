<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

	<div class="container">
		<div class="row">
		<div class="col"></div>
			<div class="col"></div>
			<div class="col-md-3">
				<form action="login" method="post">
					<div class="form-group">
						<label for="login">Login</label> 
						<input class="form-control" type="text" name="login" value="" />
					</div>
					<div class="form-group">
						<label for="password">Password</label>
						<input class="form-control" type="password" name="password" value="" />
					</div>
					<input type="submit" class="form-control btn btn-primary" value="Enter" />
				</form>
			</div>
			<div class="col"></div>
			<div class="col"></div>
		</div>
	</div>

<jsp:include page="footer.jsp" />