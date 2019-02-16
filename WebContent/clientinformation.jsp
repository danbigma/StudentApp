<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

<div class="container">

    <div class="row">
        <div class="col"></div>
        <div class="col-md-8">
        
        	<jsp:include page="databasecounter.jsp" />

			<!-- put new button: Add Student -->

			<input type="button" class="btn btn-link" value="Add Student"
				onclick="window.location.href='studentController';" />
				
			<input type="button" class="btn btn-link" value="Delete various"
				onclick="window.location.href='/StudentApp/deletestudents';" />
				
			<input type="button" class="btn btn-link" value="Client Information"
				onclick="window.location.href='/StudentApp/clientInformation';" />

			<table class="table">
			  <tbody>
			    <tr>
			      <th scope="row">1</th>
			      <td>Mark</td>
			      <td>Otto</td>
			      <td>@mdo</td>
			    </tr>
			  </tbody>
			</table>
        </div>
        <div class="col"></div>
    </div>

</div>


<jsp:include page="footer.jsp" />