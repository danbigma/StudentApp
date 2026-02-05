package com.studentapp.web;

public final class Web {
    private Web() {}

    public static final class Attrs {
        public static final String STUDENT_LIST = "studentList";
        public static final String NUM = "num";
        public static final String INFO_LIST = "infoList";
        public static final String CSRF_TOKEN = "csrfToken";
        public static final String FLASH_SUCCESS = "flashSuccess";
        public static final String FLASH_ERROR = "flashError";
        private Attrs() {}
    }

    public static final class Session {
        public static final String USERNAME = "username";
        public static final String CSRF_TOKEN = "_csrf.session";
        private Session() {}
    }

    public static final class Cookies {
        public static final String WELCOME_MESSAGE = "message";
        private Cookies() {}
    }

    public static final class Params {
        public static final String STUDENT_ID = "studentId";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String EMAIL = "email";
        public static final String STUDENT_CHECKBOX = "student"; // for bulk delete
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
        public static final String SAVE_SESSION = "savesession";
        public static final String CSRF = "_csrf";
        private Params() {}
    }

    public static final class Views {
        public static final String DELETE_STUDENTS = "/admin/deleteStudents.jsp";
        public static final String LOGIN = "/login.jsp";
        public static final String CLIENT_INFORMATION = "/admin/clientinformation.jsp";
        public static final String DASHBOARD = "/admin/dashboard.jsp";
        private Views() {}
    }
}
