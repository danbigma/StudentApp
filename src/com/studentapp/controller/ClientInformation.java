package com.studentapp.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.studentapp.web.BaseServlet;
import com.studentapp.web.Web;

@WebServlet(name = "clientInformation", urlPatterns = { "/admin/clientInformation" })
public class ClientInformation extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public ClientInformation() {
		super();
	}

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long startNanos = System.nanoTime();
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        HashMap<String, String> infoList = new HashMap<>();

        // Metadatos útiles de request
        extractRequestMeta(request, infoList, requestId);
        // Cliente (IP real y basicos)
        extractClientInfo(request, infoList);
        // Servidor
        extractServerInfo(request, infoList);
        // Headers
        extractHeaderInfo(request, infoList);
        // Parámetros (todos)
        extractParametersInfo(request, infoList);
        // Cookies
        extractCookiesInfo(request, infoList);
        // Sesión
        extractSessionInfo(request, infoList);
        // Locales
        extractLocaleInfo(request, infoList);
        // JVM/host
        extractJvmInfo(infoList);

        // Servlet Context info
        ServletContext servletContext = request.getServletContext();
        String realPath = servletContext.getRealPath("/");
        infoList.put("realPath", realPath);

        long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
        infoList.put("processingTimeMs", String.valueOf(durationMs));

        // Agregar infoList al request
        request.setAttribute(Web.Attrs.INFO_LIST, infoList);
        request.setAttribute("activeMenu", "clientinfo");

        forward(request, response, Web.Views.CLIENT_INFORMATION);
    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

    private void extractClientInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        infoList.put("remoteAddr", request.getRemoteAddr());
        infoList.put("remoteHost", request.getRemoteHost());
        infoList.put("remoteUser", request.getRemoteUser());
        infoList.put("clientIpEffective", resolveClientIp(request));
        String userAgent = request.getHeader("User-Agent");
        infoList.put("userAgent", userAgent);
        infoList.put("isMobileUA", String.valueOf(isMobileUserAgent(userAgent)));
    }

    private void extractServerInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        infoList.put("serverName", request.getServerName());
        infoList.put("serverPort", String.valueOf(request.getServerPort()));
        infoList.put("scheme", request.getScheme());
        infoList.put("protocol", request.getProtocol());
        infoList.put("secure", String.valueOf(request.isSecure()));
    }

    private void extractHeaderInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            infoList.put("header:" + header, request.getHeader(header));
        }
    }

    private void extractParametersInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            String[] values = request.getParameterValues(name);
            String joined = values == null ? null : Stream.of(values).collect(Collectors.joining(","));
            infoList.put("param:" + name, joined);
        }
        infoList.put("queryString", request.getQueryString());
        infoList.put("requestURL", request.getRequestURL().toString());
        infoList.put("requestURI", request.getRequestURI());
        infoList.put("contextPath", request.getContextPath());
        infoList.put("servletPath", request.getServletPath());
        infoList.put("method", request.getMethod());
    }

    private void extractCookiesInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                infoList.put("cookie:" + c.getName(), c.getValue());
            }
        }
    }

    private void extractSessionInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            infoList.put("session:id", session.getId());
            infoList.put("session:creationTime", formatEpochMillis(session.getCreationTime()));
            infoList.put("session:lastAccessedTime", formatEpochMillis(session.getLastAccessedTime()));
            infoList.put("session:maxInactiveIntervalSec", String.valueOf(session.getMaxInactiveInterval()));
        } else {
            infoList.put("session", "none");
        }
    }

    private void extractLocaleInfo(HttpServletRequest request, HashMap<String, String> infoList) {
        Locale primary = request.getLocale();
        String locales = Collections.list(request.getLocales()).stream()
                .map(Locale::toLanguageTag)
                .collect(Collectors.joining(","));
        infoList.put("locale:primary", primary == null ? "" : primary.toLanguageTag());
        infoList.put("locale:accepted", locales);
    }

    private void extractJvmInfo(HashMap<String, String> infoList) {
        Runtime rt = Runtime.getRuntime();
        infoList.put("jvm:name", System.getProperty("java.runtime.name"));
        infoList.put("jvm:version", System.getProperty("java.runtime.version"));
        infoList.put("os:name", System.getProperty("os.name"));
        infoList.put("os:arch", System.getProperty("os.arch"));
        infoList.put("mem:maxMB", String.valueOf(rt.maxMemory() / (1024 * 1024)));
        infoList.put("mem:totalMB", String.valueOf(rt.totalMemory() / (1024 * 1024)));
        infoList.put("mem:freeMB", String.valueOf(rt.freeMemory() / (1024 * 1024)));
        infoList.put("timezone", TimeZone.getDefault().getID());
    }

    private void extractRequestMeta(HttpServletRequest request, HashMap<String, String> infoList, String requestId) {
        infoList.put("requestId", requestId);
        infoList.put("receivedAt", DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .withZone(ZoneId.systemDefault())
                .format(Instant.now()));
        String referer = request.getHeader("Referer");
        infoList.put("referer", referer);
        infoList.put("contentType", String.valueOf(request.getContentType()));
        infoList.put("contentLength", String.valueOf(request.getContentLength()));
        infoList.put("characterEncoding", String.valueOf(request.getCharacterEncoding()));
    }

    private String resolveClientIp(HttpServletRequest request) {
        // Busca IP real detrás de proxies/CDN
        String[] headerCandidates = new String[] {
                "X-Forwarded-For",
                "X-Real-IP",
                "CF-Connecting-IP",
                "True-Client-IP",
                "X-Client-IP",
                "X-Cluster-Client-IP"
        };
        for (String h : headerCandidates) {
            String v = request.getHeader(h);
            if (v != null && !v.trim().isEmpty()) {
                // X-Forwarded-For puede traer lista separada por comas
                String first = v.split(",")[0].trim();
                if (!first.isEmpty() && !"unknown".equalsIgnoreCase(first)) {
                    return first;
                }
            }
        }
        return request.getRemoteAddr();
    }

    private boolean isMobileUserAgent(String ua) {
        if (ua == null) return false;
        String s = ua.toLowerCase();
        return s.contains("mobile") || s.contains("iphone") || s.contains("android") || s.contains("ipad");
    }

    private String formatEpochMillis(long epochMillis) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(epochMillis));
    }
}
