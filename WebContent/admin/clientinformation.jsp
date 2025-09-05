<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="context" value="${pageContext.request.contextPath}" />
<jsp:include page="../header.jsp" />

<div class="container my-3">
  <c:if test="${empty infoList}">
    <div class="alert alert-danger" role="alert">Ha ocurrido un error al obtener la información del cliente.</div>
  </c:if>

  <div class="row">
    <div class="col-md-12">
      <div class="card shadow-sm mb-3">
        <div class="card-body d-flex justify-content-between align-items-center">
          <h3 class="mb-0">Client Information</h3>
          <div>
            <small class="text-muted mr-2">Request ID:</small>
            <code id="requestIdVal">${infoList['requestId']}</code>
            <button id="copyRequestIdBtn" class="btn btn-sm btn-outline-primary ml-2" data-toggle="tooltip" title="Copy Request ID" aria-label="Copy Request ID"><i class="bi bi-clipboard"></i><span class="sr-only">Copy</span></button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-6 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Request</strong></div>
        <div class="card-body p-0">
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
    <div class="col-md-6 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Cliente</strong></div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <tbody>
              <tr><td>remoteAddr</td><td>${infoList['remoteAddr']}</td></tr>
              <tr><td>remoteHost</td><td>${infoList['remoteHost']}</td></tr>
              <tr><td>remoteUser</td><td>${infoList['remoteUser']}</td></tr>
              <tr><td>clientIpEffective</td><td>${infoList['clientIpEffective']}</td></tr>
              <tr><td>userAgent</td><td>${infoList['userAgent']}</td></tr>
              <tr><td>isMobileUA</td><td>${infoList['isMobileUA']}</td></tr>
              <tr><td>locale:primary</td><td>${infoList['locale:primary']}</td></tr>
              <tr><td>locale:accepted</td><td>${infoList['locale:accepted']}</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-6 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Servidor</strong></div>
        <div class="card-body p-0">
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
    <div class="col-md-6 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Sesión</strong></div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'session:')}">
                  <tr><td>${e.key}</td><td>${e.value}</td></tr>
                </c:if>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-6 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Cookies</strong></div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'cookie:')}">
                  <tr><td>${e.key}</td><td>${e.value}</td></tr>
                </c:if>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="col-md-6 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>JVM / Host</strong></div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'jvm:') || fn:startsWith(e.key, 'os:') || fn:startsWith(e.key, 'mem:')}">
                  <tr><td>${e.key}</td><td>${e.value}</td></tr>
                </c:if>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-12 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Headers</strong></div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'header:')}">
                  <tr><td>${e.key}</td><td>${e.value}</td></tr>
                </c:if>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-12 mb-3">
      <div class="card shadow-sm h-100">
        <div class="card-header"><strong>Parámetros</strong></div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <tbody>
              <c:forEach var="e" items="${infoList}">
                <c:if test="${fn:startsWith(e.key, 'param:')}">
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
