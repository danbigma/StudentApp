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
  <meta name="color-scheme" content="light dark" />

  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Sora:wght@400;600;700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="${context}/css/ui.css" />
</head>
<body>
<div class="app">
  <header class="topbar">
    <c:choose>
      <c:when test="${empty sessionScope.username}">
        <a class="brand" href="${context}/students">
          <span class="brand-dot"></span>
          <span>StudentApp</span>
        </a>
      </c:when>
      <c:otherwise>
        <a class="brand" href="${context}/admin?action=dashboard">
          <span class="brand-dot"></span>
          <span>StudentApp</span>
        </a>
      </c:otherwise>
    </c:choose>

    <div class="topbar-actions">
      <span class="badge js-health">--</span>
      <span class="badge js-db">--</span>
      <button type="button" class="btn btn-outline btn-sm" data-theme-toggle>Dark</button>
      <c:if test="${empty sessionScope.username}">
        <a class="btn btn-primary btn-sm" href="${context}/login">Login</a>
      </c:if>
      <c:if test="${not empty sessionScope.username}">
        <form action="${context}/logout" method="post">
          <input type="hidden" name="_csrf" value="${csrfToken}" />
          <button type="submit" class="btn btn-outline btn-sm">Logout</button>
        </form>
      </c:if>
    </div>
  </header>

  <div class="app-body">
    <c:if test="${not empty sessionScope.username}">
      <aside class="sidebar">
        <h4>Admin</h4>
        <a class="js-nav-route" data-route="dashboard" href="${context}/admin?action=dashboard">Dashboard</a>
        <a class="js-nav-route" data-route="delete" href="${context}/admin/deletestudents">Delete Students</a>
        <a class="js-nav-route" data-route="clientinfo" href="${context}/admin/clientInformation">Client Info</a>
        <h4>Actions</h4>
        <form action="${context}/admin/seed" method="post">
          <input type="hidden" name="_csrf" value="${csrfToken}" />
          <input type="hidden" name="count" value="100" />
          <button type="submit" class="btn btn-outline btn-sm" data-confirm="Seed 100 demo students?">Seed Demo</button>
        </form>
      </aside>
    </c:if>

    <main class="content">
      <div id="toastContainer" class="toast-container"></div>
      <div id="flashData" data-success="${flashSuccess}" data-error="${flashError}" hidden></div>
