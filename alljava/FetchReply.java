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

class FetchReply {
	int returnCode;
	int totalErrorLength;
	SQLWarningOrError[] errorList;
	int rowsAffected;
	int outValuesFormat;
	byte[] outValues;

	// -------------------------------------------------------------
	public FetchReply(LogicalByteArray buf, InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException {
		buf.setLocation(Header.sizeOf());

		returnCode = buf.extractInt();

		if (returnCode != TRANSPORT.SQL_SUCCESS) {
			totalErrorLength = buf.extractInt();
			if (totalErrorLength > 0) {
				errorList = new SQLWarningOrError[buf.extractInt()];
				for (int i = 0; i < errorList.length; i++) {
					errorList[i] = new SQLWarningOrError(buf, ic, ic.getISOMapping());
				}
			}
		}

		if (errorList == null) {
			errorList = new SQLWarningOrError[0];
		}

		rowsAffected = buf.extractInt();
		outValuesFormat = buf.extractInt();

		if (returnCode == TRANSPORT.SQL_SUCCESS || returnCode == TRANSPORT.SQL_SUCCESS_WITH_INFO) {
			outValues = buf.extractByteArray();
		}
	}
}
