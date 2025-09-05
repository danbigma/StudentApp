package com.studentapp.db;

import java.sql.SQLException;

public class DatabaseOperationException extends SQLException {
    public DatabaseOperationException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public DatabaseOperationException(String reason) {
        super(reason);
    }
}
