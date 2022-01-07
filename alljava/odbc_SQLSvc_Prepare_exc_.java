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

class odbc_SQLSvc_Prepare_exc_ {
	int returnCode;

	String sqlError;

	int rowsAffected;

	int outValuesFormat;

	byte[] outValues;

	static final int odbc_SQLSvc_Prepare_ParamError_exn_ = 1;

	static final int odbc_SQLSvc_Prepare_InvalidConnection_exn_ = 2;

	static final int odbc_SQLSvc_Prepare_SQLError_exn_ = 3;

	static final int odbc_SQLSvc_Prepare_SQLStillExecuting_exn_ = 4;

	static final int odbc_SQLSvc_Prepare_SQLQueryCancelled_exn_ = 5;

	static final int odbc_SQLSvc_Prepare_TransactionError_exn_ = 6;

	void extractFromByteArray(LogicalByteArray buf, String addr,
			InterfaceConnection ic) throws SQLException,
			UnsupportedCharsetException, CharacterCodingException {
		returnCode = buf.extractInt();

		String temp0 = Integer.toString(returnCode);

		switch (returnCode) {
		case TRANSPORT.CEE_SUCCESS:
			break;
		case odbc_SQLSvc_Prepare_SQLStillExecuting_exn_:
			break;
		case odbc_SQLSvc_Prepare_SQLQueryCancelled_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_s1_008", null);
		case odbc_SQLSvc_Prepare_SQLError_exn_:
			break;
		case odbc_SQLSvc_Prepare_ParamError_exn_:
			sqlError = ic.decodeBytes(buf.extractString(), 1);
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_program_error", sqlError, addr);
		case odbc_SQLSvc_Prepare_InvalidConnection_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_08_s01", null);
		case odbc_SQLSvc_Prepare_TransactionError_exn_:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_transaction_error", temp0, addr);
		default:
			throw SQLMXMessages.createSQLException(null, ic.getLocale(),
					"ids_unknown_reply_error", temp0);
		}
	}
}
