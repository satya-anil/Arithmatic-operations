// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003-2007
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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;

class odbc_SQLSvc_SetConnectionOption_exc_ {
	int exception_nr;

	int exception_detail;

	String ParamError;

	SQLWarningOrError[] errorList;

	static final int odbc_SQLSvc_SetConnectionOption_ParamError_exn_ = 1;

	static final int odbc_SQLSvc_SetConnectionOption_InvalidConnection_exn_ = 2;

	static final int odbc_SQLSvc_SetConnectionOption_SQLError_exn_ = 3;

	static final int odbc_SQLSvc_SetConnectionOption_SQLInvalidHandle_exn_ = 4;

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buf, String addr,
			InterfaceConnection ic) throws SQLException,
			UnsupportedCharsetException, CharacterCodingException {
		exception_nr = buf.extractInt();
		exception_detail = buf.extractInt();

		String temp0 = Integer.toString(exception_nr);
		String temp1 = Integer.toString(exception_detail);

		switch (exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
			break;
		case odbc_SQLSvc_SetConnectionOption_SQLError_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_unknown_reply_error", temp0, temp1);
		case odbc_SQLSvc_SetConnectionOption_ParamError_exn_:
			ParamError = ic.decodeBytes(buf.extractString(), 1);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_program_error", ParamError, addr);
		case odbc_SQLSvc_SetConnectionOption_InvalidConnection_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_08_s01", null);
		case odbc_SQLSvc_SetConnectionOption_SQLInvalidHandle_exn_:
			// SOL-10-060915-9077 - Server does not return message for
			// SQLInvalidHandle
			// throw SQLMXMessages.createSQLException(null, locale, "ids_08_s01",
			// null);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"autocommit_txn_in_progress", null);
		default:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_unknown_reply_error", temp0, temp1);
		}
	}
}
