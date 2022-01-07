// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003, 2004, 2005
// Hewlett-Packard Development Company, L.P.
// Protected as an unpublished work.
// All rights reserved.
//
// The computer program listings, specifications and
// documentation herein are the property of Compaq Computer
// Corporation and successor entities such as Hewlett-Packard
// Development Company, L.P., or a third party supplier and
// shall not be reproduced, copied, disclosed, or used in whole
// or in part for any reason without the prior express written
// permission of Hewlett-Packard Development Company, L.P.
//
// @ @ @ END COPYRIGHT @ @ @

package com.tandem.t4jdbc;

import java.sql.SQLException;
import java.sql.SQLWarning;

public abstract class SQLMXHandle {
	SQLWarning sqlWarning_;

	public void clearWarnings() throws SQLException {
		sqlWarning_ = null;
	}

	public SQLWarning getWarnings() throws SQLException {
		return sqlWarning_;
	}

	void setSQLWarning(T4Properties t4props, String messageId,
			Object[] messageArguments) {
		SQLWarning sqlWarningLeaf = SQLMXMessages.createSQLWarning(t4props,
				messageId, messageArguments);
		if (sqlWarning_ == null) {
			sqlWarning_ = sqlWarningLeaf;
		} else {
			sqlWarning_.setNextWarning(sqlWarningLeaf);
		}
	}

	// Method used by JNI layer to set the warning
	void setSqlWarning(SQLWarning sqlWarning) {
		if (sqlWarning_ == null) {
			sqlWarning_ = sqlWarning;
		} else {
			sqlWarning_.setNextWarning(sqlWarning);
		}
	}

	// Method added to check if the connection had any errors
	// This calls the abstract method closeErroredConnection()
	void performConnectionErrorChecks(SQLException se) {
		if (se instanceof SQLMXException) {
			SQLMXException sqlmx_e = (SQLMXException) se;
			if (sqlmx_e.messageId.equals(ERROR_SOCKET_WRITE_ERROR)
					|| sqlmx_e.messageId.equals(ERROR_SOCKET_READ_ERROR)
					|| sqlmx_e.messageId.equals(ERROR_SOCKET_IS_CLOSED_ERROR)
					|| sqlmx_e.messageId.equals(ERROR_INVALID_CONNECTION)
					|| sqlmx_e.messageId.equals(ERROR_IDS_08_S01)
					|| sqlmx_e.messageId.equals(IDS_S1_T00) ||
					//start sol 10-080812-5141 R3.0 
					(sqlmx_e.getErrorCode() == -8841))
			//End sol 10-080812-5141 R3.0
			{
				closeErroredConnection(sqlmx_e);
			}
		}
	}

	abstract void closeErroredConnection(SQLMXException se);

	static final String ERROR_IDS_08_S01 = new String("ids_08_s01");

	static final String ERROR_INVALID_CONNECTION = new String(
			"invalid_connection");

	static final String ERROR_SOCKET_WRITE_ERROR = new String(
			"socket_write_error");

	static final String ERROR_SOCKET_READ_ERROR = new String(
			"socket_read_error");

	static final String ERROR_SOCKET_IS_CLOSED_ERROR = new String(
			"socket_is_closed_error");

	static final String IDS_S1_T00 = new String("ids_s1_t00");
}
