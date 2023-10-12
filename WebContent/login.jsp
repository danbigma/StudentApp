<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="row mt-5 mb-4">
        <div class="col">
            <h3 class="text-center">Welcome to StudentAPP</h3>
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col-md-3">
            <form action="login" method="post">
                <div class="form-group">
                    <label for="login">Login</label>
                    <input class="form-control" type="text" name="login" value="" />
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input class="form-control" type="password" name "password" value="" />
                </div>
                <div class="form-group">
                    <input type="checkbox" name="savesession" value="true" id="savesession" />
                    <label for="savesession">Save session</label>
                </div>
                <input type="submit" class="form-control btn btn-primary" value="Enter" />
            </form>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
