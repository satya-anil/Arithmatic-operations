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

class odbc_SQLSvc_InitializeDialogue_exc_ {
	int exception_nr;

	int exception_detail;

	String ParamError;

	ERROR_DESC_LIST_def SQLError;

	ERROR_DESC_LIST_def InvalidUser;

	String clientErrorText;

	static final int odbc_SQLSvc_InitializeDialogue_ParamError_exn_ = 1;

	static final int odbc_SQLSvc_InitializeDialogue_InvalidConnection_exn_ = 2;

	static final int odbc_SQLSvc_InitializeDialogue_SQLError_exn_ = 3;

	static final int odbc_SQLSvc_InitializeDialogue_SQLInvalidHandle_exn_ = 4;

	static final int odbc_SQLSvc_InitializeDialogue_SQLNeedData_exn_ = 5;

	static final int odbc_SQLSvc_InitializeDialogue_InvalidUser_exn_ = 6;

	static final int SQL_PASSWORD_EXPIRING = 8857;

	static final int SQL_PASSWORD_GRACEPERIOD = 8837;

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buf, String addr,
			InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException, SQLException {
		exception_nr = buf.extractInt();
		exception_detail = buf.extractInt();

		String temp0 = Integer.toString(exception_nr);
		String temp1 = Integer.toString(exception_detail);

		switch (exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
			break;
		case odbc_SQLSvc_InitializeDialogue_SQLError_exn_:
			SQLError = new ERROR_DESC_LIST_def();
			SQLError.extractFromByteArray(buf, ic);
			break;
		case odbc_SQLSvc_InitializeDialogue_InvalidUser_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_28_000", null);
		case odbc_SQLSvc_InitializeDialogue_ParamError_exn_:
			ParamError = ic.decodeBytes(buf.extractString(), 1);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_program_error", ParamError, addr);
		case odbc_SQLSvc_InitializeDialogue_InvalidConnection_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_08_s01", null);

			// Rao tells me that the next two are not used, so they are
			// commented out.
			// case odbc_SQLSvc_InitializeDialogue_SQLInvalidHandle_exn_:
			// break;
			// case odbc_SQLSvc_InitializeDialogue_SQLNeedData_exn_:
			// break;

		default:
			clientErrorText = "unknown_initialize_dialogue_reply_error";
			break;
		}
	}
}
