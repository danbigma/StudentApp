<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="context" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <meta name="csrf-token" content="${csrfToken}" />
  <meta name="app-context" content="${context}" />
  <meta name="current-route" content="${activeMenu}" />

  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" crossorigin="anonymous" />
  <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css" crossorigin="anonymous" />
  <link rel="stylesheet" href="https://cdn.datatables.net/buttons/2.4.1/css/buttons.bootstrap5.min.css" crossorigin="anonymous" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.10.0/dist/sweetalert2.min.css" crossorigin="anonymous" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" crossorigin="anonymous" />
</head>
<body>
<nav class="navbar navbar-expand-lg bg-body-tertiary border-bottom">
  <div class="container-fluid">
    <c:choose>
      <c:when test="${empty sessionScope.username}">
        <a class="navbar-brand" href="${context}/students">StudentApp</a>
      </c:when>
      <c:otherwise>
        <a class="navbar-brand" href="${context}/admin?action=dashboard">StudentApp</a>
      </c:otherwise>
    </c:choose>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain" aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarMain">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <c:if test="${not empty sessionScope.username}">
          <li class="nav-item"><a class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}" href="${context}/admin?action=dashboard">Dashboard</a></li>
          <li class="nav-item"><a class="nav-link ${activeMenu == 'delete' ? 'active' : ''}" href="${context}/admin/deletestudents">Delete Students</a></li>
          <li class="nav-item"><a class="nav-link ${activeMenu == 'clientinfo' ? 'active' : ''}" href="${context}/admin/clientInformation">Client Info</a></li>
        </c:if>
      </ul>

      <div class="d-flex align-items-center gap-2">
        <span class="badge text-bg-secondary js-health">--</span>
        <span class="badge text-bg-secondary js-db">--</span>
        <c:if test="${empty sessionScope.username}">
          <a class="btn btn-sm btn-outline-primary" href="${context}/login">Login</a>
        </c:if>
        <c:if test="${not empty sessionScope.username}">
          <form action="${context}/logout" method="post" class="m-0">
            <input type="hidden" name="_csrf" value="${csrfToken}" />
            <button type="submit" class="btn btn-sm btn-outline-secondary">Logout</button>
          </form>
        </c:if>
      </div>
    </div>
  </div>
</nav>

<div aria-live="polite" aria-atomic="true" style="position: fixed; top: 70px; right: 16px; z-index: 1080;">
  <div id="toastContainer"></div>
  <div id="flashData" data-success="${flashSuccess}" data-error="${flashError}" hidden></div>
</div>
