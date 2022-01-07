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

class odbc_SQLSvc_Execute_exc_ {
	int exception_nr;

	int exception_detail;

	String ParamError;

	ERROR_DESC_LIST_def SQLError;

	ERROR_DESC_LIST_def SQLRetryCompile;

	int SQLQueryCancelled;

	//
	// Taken from odbc_cl.h
	// Exception number constants for
	// operation 'odbc_SQLSvc_ExecuteRowset'
	//
	static final int odbc_SQLSvc_Execute_ParamError_exn_ = 1;

	static final int odbc_SQLSvc_Execute_InvalidConnection_exn_ = 2;

	static final int odbc_SQLSvc_Execute_SQLError_exn_ = 3;

	static final int odbc_SQLSvc_Execute_SQLInvalidHandle_exn_ = 4;

	static final int odbc_SQLSvc_ExecuteSQLNeedData_exn_ = 5;

	static final int odbc_SQLSvc_Execute_SQLRetryCompile_exn_ = 6;

	static final int odbc_SQLSvc_Execute_SQLStillExecuting_exn_ = 7;

	static final int odbc_SQLSvc_Execute_SQLQueryCancelled_exn_ = 8;

	static final int odbc_SQLSvc_Execute_TransactionError_exn_ = 9;

	odbc_SQLSvc_Execute_exc_() {
	}

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
		case odbc_SQLSvc_Execute_SQLStillExecuting_exn_:
			break;
		case odbc_SQLSvc_Execute_SQLQueryCancelled_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_s1_008", null);
		case odbc_SQLSvc_Execute_SQLError_exn_:
			SQLError = new ERROR_DESC_LIST_def();
			SQLError.extractFromByteArray(buf, ic);
			break;
		case odbc_SQLSvc_Execute_SQLRetryCompile_exn_:
			SQLRetryCompile = new ERROR_DESC_LIST_def();
			SQLRetryCompile.extractFromByteArray(buf, ic);
			break;
		case odbc_SQLSvc_Execute_ParamError_exn_:
			ParamError = ic.decodeBytes(buf.extractString(), 1);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_program_error", ParamError, addr);
		case odbc_SQLSvc_Execute_InvalidConnection_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_08_s01", null);
		case odbc_SQLSvc_Execute_TransactionError_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_transaction_error", temp1, addr);
		default:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_unknown_reply_error", temp0, temp1);
		}
	}
}
