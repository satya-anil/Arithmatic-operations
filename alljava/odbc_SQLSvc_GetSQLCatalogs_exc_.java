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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;

class odbc_SQLSvc_GetSQLCatalogs_exc_ {

	// From odbc_cl.h
	//
	// struct odbc_SQLSvc_GetSQLCatalogs_exc_
	// {
	// size_int exception_nr;
	// size_int exception_detail;
	// union {
	// odbc_SQLSvc_ParamError ParamError;
	// odbc_SQLSvc_SQLError SQLError;
	// } u;
	// };

	int exception_nr;

	int exception_detail;

	//
	// The third element is special when it is stored in
	// the buffer. It may be:
	//
	// an offset to a character array (ParamError)
	// an offset to an odbc_SQLSvc_SQLError (SQLError)
	// or it may be an integer (SQLQueryCancelled).
	//

	int p3Index;

	String ParamError;

	ERROR_DESC_LIST_def SQLError;

	int SQLQueryCancelled;

	//
	// Taken from odbc_cl.h
	// Exception number constants for
	// operation 'odbc_SQLSvc_GetSQLCatalogs'
	//
	static final int odbc_SQLSvc_GetSQLCatalogs_ParamError_exn_ = 1;

	static final int odbc_SQLSvc_GetSQLCatalogs_InvalidConnection_exn_ = 2;

	static final int odbc_SQLSvc_GetSQLCatalogs_SQLError_exn_ = 3;

	static final int odbc_SQLSvc_GetSQLCatalogs_SQLInvalidHandle_exn_ = 4;

	odbc_SQLSvc_GetSQLCatalogs_exc_() {
		// Do nothing constructor
	}

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
		case odbc_SQLSvc_GetSQLCatalogs_SQLError_exn_:
			SQLError = new ERROR_DESC_LIST_def();
			SQLError.extractFromByteArray(buffer1, ic);
			break;
		case odbc_SQLSvc_GetSQLCatalogs_ParamError_exn_:
			ParamError = ic.decodeBytes(buffer1.extractString(), 1);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_program_error", ParamError, addr);
		case odbc_SQLSvc_GetSQLCatalogs_SQLInvalidHandle_exn_:
			break;
		case odbc_SQLSvc_GetSQLCatalogs_InvalidConnection_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_08_s01", null);
		default:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_unknown_reply_error", temp0, temp1);
		}
	} // end extractFromByteArray

} // end odbc_SQLSvc_GetSQLCatalogs_exc_
