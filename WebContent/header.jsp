<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	

<c:set var="context" value="${pageContext.request.contextPath}" />


<!doctype html>
<html lang="en">
<head>
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="csrf-token" content="${csrfToken}" />
<meta name="app-context" content="${context}" />

<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
	integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
	crossorigin="anonymous">
<!-- DataTables + SweetAlert2 CSS -->
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css" crossorigin="anonymous" />
<link rel="stylesheet" href="https://cdn.datatables.net/buttons/2.4.1/css/buttons.dataTables.min.css" crossorigin="anonymous" />
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.10.0/dist/sweetalert2.min.css" crossorigin="anonymous" />
<!-- Bootstrap Icons -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" crossorigin="anonymous" />
<!-- Select2 CSS -->
<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
<link rel="stylesheet" href="${context}/css/theme.css" />
<link rel="stylesheet" href="${context}/css/style.css" />
<link rel="stylesheet" href="${context}/css/header.css" />

</head>
<body>
<c:set var="context" value="${pageContext.request.contextPath}" />
<!-- App Loading Overlay -->
<div id="appLoader" class="loading-overlay" role="status" aria-live="polite" aria-busy="true">
  <div class="text-center">
    <div class="spinner-border text-primary" role="status" aria-hidden="true"></div>
    <div class="mt-2 text-muted">Loading…</div>
  </div>
  </div>
<nav class="navbar navbar-expand-lg navbar-light navbar-modern sticky-top shadow-sm">
  <div class="container-fluid">
    <c:choose>
      <c:when test="${not empty sessionScope.username}">
        <a class="navbar-brand brand-logo" href="${context}/admin?action=dashboard">
          <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
            <defs><linearGradient id="g1" x1="0" x2="1"><stop offset="0%" stop-color="#4facfe"/><stop offset="100%" stop-color="#00f2fe"/></linearGradient></defs>
            <circle cx="12" cy="12" r="10" fill="url(#g1)"/>
          </svg>
          <span class="text">StudentApp</span>
        </a>
      </c:when>
      <c:otherwise>
        <a class="navbar-brand brand-logo" href="${context}/login.jsp">
          <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
            <defs><linearGradient id="g1" x1="0" x2="1"><stop offset="0%" stop-color="#4facfe"/><stop offset="100%" stop-color="#00f2fe"/></linearGradient></defs>
            <circle cx="12" cy="12" r="10" fill="url(#g1)"/>
          </svg>
          <span class="text">StudentApp</span>
        </a>
      </c:otherwise>
    </c:choose>

    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarMain" aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarMain">
      <ul class="navbar-nav mr-auto">
        <c:if test="${not empty sessionScope.username}">
          <li class="nav-item"><a class="nav-link" href="${context}/admin?action=dashboard" data-toggle="tooltip" title="Dashboard" aria-label="Dashboard"><i class="bi bi-speedometer2"></i></a></li>
          
          <li class="nav-item"><a class="nav-link" href="${context}/admin/deletestudents" data-toggle="tooltip" title="Delete various" aria-label="Delete various"><i class="bi bi-check2-square"></i></a></li>
          <li class="nav-item"><a class="nav-link" href="${context}/admin/clientInformation" data-toggle="tooltip" title="Client Info" aria-label="Client Info"><i class="bi bi-info-circle"></i></a></li>
          <li class="nav-item">
            <form id="seedFormHeader" action="${context}/admin/seed" method="post" class="form-inline my-2 my-lg-0">
              <input type="hidden" name="_csrf" value="${csrfToken}" />
              <input type="hidden" name="count" value="100" />
              <button type="submit" class="btn btn-link nav-link p-0" data-confirm="Seed 100 demo students?" data-toggle="tooltip" title="Seed demo" aria-label="Seed demo"><i class="bi bi-lightning-charge"></i><span class="sr-only">Seed demo</span></button>
            </form>
          </li>
        </c:if>
      </ul>

      <div class="navbar-text d-flex align-items-center">
        <div class="mr-3">
          <div class="theme-switch">
            <input type="checkbox" id="themeSwitch" aria-label="Toggle dark mode" />
            <label class="switch" for="themeSwitch">
              <span class="icon sun" aria-hidden="true">☀</span>
              <span class="icon moon" aria-hidden="true">🌙</span>
              <span class="thumb"></span>
            </label>
          </div>
        </div>
        <span class="status-label">App:</span>
        <span id="healthStatus" class="status-pill mr-2">--</span>
        <span class="status-label">DB:</span>
        <span id="dbStatus" class="status-pill">--</span>
        <c:if test="${not empty sessionScope.username}">
          <form id="logoutHeader" action="${context}/logout" method="post" class="form-inline ml-3">
            <input type="hidden" name="_csrf" value="${csrfToken}" />
            <button type="submit" class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" title="Logout" aria-label="Logout"><i class="bi bi-box-arrow-right"></i><span class="sr-only">Logout</span></button>
          </form>
        </c:if>
      </div>
    </div>
  </div>
</nav>

<!-- Toast container (top-right) -->
<div aria-live="polite" aria-atomic="true" style="position: fixed; top: 70px; right: 16px; z-index: 1080;">
  <div id="toastContainer"></div>
  <!-- Flash data from server -->
  <div id="flashData" data-success="${flashSuccess}" data-error="${flashError}" hidden></div>
  </div>
