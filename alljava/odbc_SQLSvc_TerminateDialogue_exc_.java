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

class odbc_SQLSvc_TerminateDialogue_exc_ {
	int exception_nr;

	int exception_detail;

	String ParamError;

	ERROR_DESC_LIST_def SQLError;

	static final int odbc_SQLSvc_TerminateDialogue_ParamError_exn_ = 1;

	static final int odbc_SQLSvc_TerminateDialogue_InvalidConnection_exn_ = 2;

	static final int odbc_SQLSvc_TerminateDialogue_SQLError_exn_ = 3;

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buffer1, String addr,
			InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException, SQLException {
		exception_nr = buffer1.extractInt();
		exception_detail = buffer1.extractInt();

		String temp0 = Integer.toString(exception_nr);
		String temp1 = Integer.toString(exception_detail);

		switch (exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
			break;
		case odbc_SQLSvc_TerminateDialogue_SQLError_exn_:
			if (exception_detail == 25000) {
				throw SQLMXMessages.createSQLException(null, ic.getLocale(),
						"ids_25_000", null);
			}
			SQLError = new ERROR_DESC_LIST_def();
			SQLError.extractFromByteArray(buffer1, ic);
			break;
		case odbc_SQLSvc_TerminateDialogue_ParamError_exn_:
			ParamError = ic.decodeBytes(buffer1.extractString(), 1);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_program_error", ParamError, addr);
		case odbc_SQLSvc_TerminateDialogue_InvalidConnection_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_08_s01", null);
		default:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_unknown_reply_error", temp0, temp1);
		}
	}
}
