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

class PrepareReply {
	int returnCode;

	int totalErrorLength;

	SQLWarningOrError[] errorList;

	int sqlQueryType;

	int stmtHandle;

	int estimatedCost;

	int inputDescLength;

	int inputParamLength;

	int inputNumberParams;

	Descriptor2[] inputDesc;

	int outputDescLength;

	int outputParamLength;

	int outputNumberParams;

	Descriptor2[] outputDesc;

	// -------------------------------------------------------------
	PrepareReply(LogicalByteArray buf, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		buf.setLocation(Header.sizeOf());

		returnCode = buf.extractInt();

		// should check SQL_SUCCESS or SQL_SUCCESS_WITH_INFO
		// if(returnCode == TRANSPORT.SQL_SUCCESS)
		if (returnCode == TRANSPORT.SQL_SUCCESS
				|| returnCode == TRANSPORT.SQL_SUCCESS_WITH_INFO) {
			if (returnCode == TRANSPORT.SQL_SUCCESS_WITH_INFO) {
				totalErrorLength = buf.extractInt();

				if (totalErrorLength > 0) {
					errorList = new SQLWarningOrError[buf.extractInt()];
					for (int i = 0; i < errorList.length; i++) {
						errorList[i] = new SQLWarningOrError(buf, ic, ic
								.getISOMapping());
					}
				}
			}
			sqlQueryType = buf.extractInt();
			stmtHandle = buf.extractInt();
			estimatedCost = buf.extractInt();

			inputDescLength = buf.extractInt();
			if (inputDescLength > 0) {
				inputParamLength = buf.extractInt();
				inputNumberParams = buf.extractInt();

				inputDesc = new Descriptor2[inputNumberParams];
				for (int i = 0; i < inputNumberParams; i++) {
					inputDesc[i] = new Descriptor2(buf, ic);
					inputDesc[i].setRowLength(inputParamLength);
				}
			}

			outputDescLength = buf.extractInt();
			if (outputDescLength > 0) {
				outputParamLength = buf.extractInt();
				outputNumberParams = buf.extractInt();

				outputDesc = new Descriptor2[outputNumberParams];
				for (int i = 0; i < outputNumberParams; i++) {
					outputDesc[i] = new Descriptor2(buf, ic);
					outputDesc[i].setRowLength(outputParamLength);
				}
			}
		} else {
			totalErrorLength = buf.extractInt();

			if (totalErrorLength > 0) {
				errorList = new SQLWarningOrError[buf.extractInt()];
				for (int i = 0; i < errorList.length; i++) {
					errorList[i] = new SQLWarningOrError(buf, ic, ic
							.getISOMapping());
				}
			}
		}
	}
}
