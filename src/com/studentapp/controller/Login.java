package com.studentapp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.studentapp.config.AppConfig;
import com.studentapp.security.RateLimiter;
import com.studentapp.web.BaseServlet;
import com.studentapp.web.Web;

@WebServlet("/login")
public class Login extends BaseServlet {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(Login.class);
	
    private final AppConfig cfg = AppConfig.get();

    private String getClientIp(HttpServletRequest request) {
        String[] headerCandidates = new String[] {
            "X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP", "True-Client-IP", "X-Client-IP", "X-Cluster-Client-IP"
        };
        for (String h : headerCandidates) {
            String v = request.getHeader(h);
            if (v != null && !v.trim().isEmpty()) {
                String first = v.split(",")[0].trim();
                if (!first.isEmpty() && !"unknown".equalsIgnoreCase(first)) {
                    return first;
                }
            }
        }
        return request.getRemoteAddr();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Muestra la página de login cuando se accede a /login vía GET
		forward(request, response, Web.Views.LOGIN);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

        // get request parameters for username and password
        String usrname = request.getParameter(Web.Params.LOGIN);
        String pasword = request.getParameter(Web.Params.PASSWORD);
        boolean savesession = request.getParameter(Web.Params.SAVE_SESSION) != null;

        try {
            String clientIp = getClientIp(request);
            String key = usrname + "|" + clientIp;
            if (RateLimiter.isBlocked(key)) {
                long wait = RateLimiter.secondsUntilUnlock(key);
                log.warn("Login blocked for user=" + usrname + " ip=" + clientIp + " waitSec=" + wait);
                forward(request, response, Web.Views.LOGIN);
                return;
            }

            log.info("Login attempt for user: " + usrname + " ip=" + clientIp);
            String expectedUser = cfg.getAdminUser();
            String expectedPass = cfg.getAdminPassword();
            if (expectedUser.equals(usrname) && expectedPass.equals(pasword)) {
				// get the old session and invalidate
				HttpSession oldSession = request.getSession(false);
				if (oldSession != null) {
					oldSession.invalidate();
				}
				// generate a new session
				HttpSession newSession = request.getSession(true);

                int ttl = savesession ? cfg.getSessionTimeoutLongSeconds() : cfg.getSessionTimeoutShortSeconds();
                newSession.setMaxInactiveInterval(ttl);
                newSession.setAttribute(Web.Session.USERNAME, usrname);

                Cookie message = new Cookie(Web.Cookies.WELCOME_MESSAGE, "Welcome");
                message.setHttpOnly(true);
                if (cfg.isCookieForceSecure() || request.isSecure()) {
                    message.setSecure(true);
                }
                response.addCookie(message);
                RateLimiter.onSuccess(key);
                redirect(request, response, "/admin");
            } else {
                RateLimiter.onFailure(key);
                forward(request, response, Web.Views.LOGIN);
            }
        } catch (Exception e) {
            log.error("Login error", e);
            request.setAttribute(Web.Attrs.FLASH_ERROR, "An unexpected error occurred during login.");
            forward(request, response, Web.Views.LOGIN);
        }
	}
}
