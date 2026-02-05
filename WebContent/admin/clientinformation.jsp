<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container py-4">
  <c:if test="${empty infoList}">
    <div class="alert alert-danger" role="alert">Ha ocurrido un error al obtener la información del cliente.</div>
  </c:if>

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Client Information</h3>
    <div>
      <small class="text-muted me-2">Request ID:</small>
      <code id="requestIdVal">${infoList['requestId']}</code>
      <button id="copyRequestIdBtn" class="btn btn-sm btn-outline-primary ms-2" type="button">Copy</button>
    </div>
  </div>

  <div class="row g-3">
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Request</div>
        <div class="table-responsive">
          <table class="table table-sm mb-0">
            <tbody>
              <tr><td>receivedAt</td><td>${infoList['receivedAt']}</td></tr>
              <tr><td>method</td><td>${infoList['method']}</td></tr>
              <tr><td>requestURL</td><td><code>${infoList['requestURL']}</code></td></tr>
              <tr><td>requestURI</td><td>${infoList['requestURI']}</td></tr>
              <tr><td>contextPath</td><td>${infoList['contextPath']}</td></tr>
              <tr><td>servletPath</td><td>${infoList['servletPath']}</td></tr>
              <tr><td>queryString</td><td><code>${infoList['queryString']}</code></td></tr>
              <tr><td>referer</td><td>${infoList['referer']}</td></tr>
              <tr><td>contentType</td><td>${infoList['contentType']}</td></tr>
              <tr><td>contentLength</td><td>${infoList['contentLength']}</td></tr>
              <tr><td>characterEncoding</td><td>${infoList['characterEncoding']}</td></tr>
              <tr><td>processingTimeMs</td><td>${infoList['processingTimeMs']}</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="col-md-6">
      <div class="card mb-3">
        <div class="card-header">Client</div>
        <div class="table-responsive">
          <table class="table table-sm mb-0">
            <tbody>
              <tr><td>remoteAddr</td><td>${infoList['remoteAddr']}</td></tr>
              <tr><td>remoteHost</td><td>${infoList['remoteHost']}</td></tr>
              <tr><td>remoteUser</td><td>${infoList['remoteUser']}</td></tr>
              <tr><td>clientIpEffective</td><td>${infoList['clientIpEffective']}</td></tr>
              <tr><td>userAgent</td><td>${infoList['userAgent']}</td></tr>
              <tr><td>isMobileUA</td><td>${infoList['isMobileUA']}</td></tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card">
        <div class="card-header">Server</div>
        <div class="table-responsive">
          <table class="table table-sm mb-0">
            <tbody>
              <tr><td>serverName</td><td>${infoList['serverName']}</td></tr>
              <tr><td>serverPort</td><td>${infoList['serverPort']}</td></tr>
              <tr><td>scheme</td><td>${infoList['scheme']}</td></tr>
              <tr><td>protocol</td><td>${infoList['protocol']}</td></tr>
              <tr><td>secure</td><td>${infoList['secure']}</td></tr>
              <tr><td>realPath</td><td><code>${infoList['realPath']}</code></td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="row g-3 mt-1">
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Session / Cookies</div>
        <div class="table-responsive">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'session:') || fn:startsWith(e.key, 'cookie:')}">
                  <tr><td>${e.key}</td><td>${e.value}</td></tr>
                </c:if>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Headers / Params</div>
        <div class="table-responsive">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'header:') || fn:startsWith(e.key, 'param:')}">
                  <tr><td>${e.key}</td><td><code>${e.value}</code></td></tr>
                </c:if>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="../footer.jsp" />
