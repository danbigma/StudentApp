<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<jsp:include page="../header.jsp" />

<div class="container">

    <div class="row">
        <div class="col"></div>
        <div class="col-md-8">
			<!-- put new button: Add Student -->
			<jsp:include page="nav-menu.jsp" />
				
			<table class="table table-sm">

                <thead class="thead-dark">
                    <tr>
                        <th scope="col">First Name</th>
                        <th scope="col">Last Name</th>
                        <th scope="col">Email</th>
                        <th scope="col">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="tempStudent" items="${studentList}">

                        <!-- set up a link for each student -->
                        <c:url var="tempLink" value="admin">
                            <c:param name="action" value="load" />
                            <c:param name="studentId" value="${tempStudent.id}" />
                        </c:url>

                        <!--  set up a link to delete a student -->
                        <c:url var="deleteLink" value="admin">
                            <c:param name="action" value="delete" />
                            <c:param name="studentId" value="${tempStudent.id}" />
                        </c:url>

                        <tr>
                            <td>${tempStudent.firstName}</td>
                            <td>${tempStudent.lastName}</td>
                            <td>${tempStudent.email}</td>
                            <td><a href="${tempLink}">Update</a> | <a
                                href="${deleteLink}"
                                onclick="if(!confirm('Are you sure you want to delete this student?'))">
                                    Delete</a></td>
                        </tr>

                    </c:forEach>
                </tbody>

            </table>
        </div>
        <div class="col"></div>
    </div>

</div>


<jsp:include page="../footer.jsp" />