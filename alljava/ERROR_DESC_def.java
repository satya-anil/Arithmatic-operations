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

class ERROR_DESC_def {
	int rowId;

	int errorDiagnosticId;

	int sqlcode;

	String sqlstate;

	String errorText;

	int operationAbortId;

	int errorCodeType;

	String Param1;

	String Param2;

	String Param3;

	String Param4;

	String Param5;

	String Param6;

	String Param7;

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buffer1, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		rowId = buffer1.extractInt();
		errorDiagnosticId = buffer1.extractInt();
		sqlcode = buffer1.extractInt();

		// Note, SQLSTATE is logically 5 bytes, but ODBC uses 6 bytes for some
		// reason.
		sqlstate = ic.decodeBytes(buffer1.extractByteArray(6), 1);
		errorText = ic.decodeBytes(buffer1.extractString(), ic
				.getTerminalCharset());

		operationAbortId = buffer1.extractInt();
		errorCodeType = buffer1.extractInt();
		Param1 = ic.decodeBytes(buffer1.extractString(), 1);
		Param2 = ic.decodeBytes(buffer1.extractString(), 1);
		Param3 = ic.decodeBytes(buffer1.extractString(), 1);
		Param4 = ic.decodeBytes(buffer1.extractString(), 1);
		Param5 = ic.decodeBytes(buffer1.extractString(), 1);
		Param6 = ic.decodeBytes(buffer1.extractString(), 1);
		Param7 = ic.decodeBytes(buffer1.extractString(), 1);
	}
	
}
