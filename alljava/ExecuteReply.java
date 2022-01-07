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

class ExecuteReply {
	int returnCode;
	int totalErrorLength;
	SQLWarningOrError[] errorList;
	long rowsAffected;
	int queryType;
	int estimatedCost;
	byte[] outValues;

	int numResultSets;
	Descriptor2[][] outputDesc;
	String stmtLabels[];

	int outputParamLength;
	int outputNumberParams;

	String[] proxySyntax;
	
	//Added for ExecuteBatch R3.0
	int rowsAffectedArrayLength;
	int []rowsAffectedArray;
	//End R3.0
	//R321
	int outValuesFormat_;

	// ----------------------------------------------------------
	ExecuteReply(LogicalByteArray buf, InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException {
		int errorCharset = (ic.getWmsMode()) ? InterfaceUtilities.SQLCHARSETCODE_UTF8 : ic.getISOMapping();

		buf.setLocation(Header.sizeOf());

		returnCode = buf.extractInt();

		totalErrorLength = buf.extractInt();

		if (totalErrorLength > 0) {
			errorList = new SQLWarningOrError[buf.extractInt()];
			for (int i = 0; i < errorList.length; i++) {
				errorList[i] = new SQLWarningOrError(buf, ic, errorCharset);
			}
		}

		int outputDescLength = buf.extractInt();
		if (outputDescLength > 0) {
			outputDesc = new Descriptor2[1][];

			outputParamLength = buf.extractInt();
			outputNumberParams = buf.extractInt();

			outputDesc[0] = new Descriptor2[outputNumberParams];
			for (int i = 0; i < outputNumberParams; i++) {
				outputDesc[0][i] = new Descriptor2(buf, ic);
				outputDesc[0][i].setRowLength(outputParamLength);
			}
		}
		rowsAffected = buf.extractUnsignedInt();
		queryType = buf.extractInt();
		estimatedCost = buf.extractInt();

		// 64 bit rowsAffected
		// this is a horrible hack because we cannot change the protocol yet
		// rowsAffected should be made a regular 64 bit value when possible
		rowsAffected |= ((long) estimatedCost) << 32;

		outValues = buf.extractByteArray();

		numResultSets = buf.extractInt();

		if (numResultSets > 0) {
			outputDesc = new Descriptor2[numResultSets][];
			stmtLabels = new String[numResultSets];
			proxySyntax = new String[numResultSets];

			for (int i = 0; i < numResultSets; i++) {
				buf.extractInt(); // int stmt_handle

				stmtLabels[i] = ic.decodeBytes(buf.extractString(), 1);

				buf.extractInt(); // long stmt_label_charset
				outputDescLength = buf.extractInt();

				int outputParamsLength = 0;
				int outputNumberParams = 0;
				Descriptor2[] outputParams = null;

				if (outputDescLength > 0) {
					outputParamsLength = buf.extractInt();
					outputNumberParams = buf.extractInt();

					outputParams = new Descriptor2[outputNumberParams];
					for (int j = 0; j < outputNumberParams; j++) {
						outputParams[j] = new Descriptor2(buf, ic);
						outputParams[j].setRowLength(outputParamsLength);
					}
				}
				outputDesc[i] = outputParams;
				proxySyntax[i] = ic.decodeBytes(buf.extractString(), InterfaceUtilities.SQLCHARSETCODE_UTF8);
			}
		}

		String singleSyntax = ic.decodeBytes(buf.extractString(), InterfaceUtilities.SQLCHARSETCODE_UTF8);
		
		//extract rowsAffectedArray only for execute_array API.
		//R321
		if (ic.t4props_.getExecuteBatchWithRowsAffected()
				.equalsIgnoreCase("ON") && InterfaceStatement.rowWiseData == true) {
			//Added for ExecuteBatch R3.0
			rowsAffectedArrayLength = buf.extractInt();
			if (rowsAffectedArrayLength > 0) {
				rowsAffectedArray = new int[rowsAffectedArrayLength];
				for (int i = 0; i < rowsAffectedArrayLength; i++) {
					rowsAffectedArray[i] = buf.extractInt();
				}
			}
		} else {
			outValuesFormat_=buf.extractInt();
		}
		//End R3.0
		if (proxySyntax == null) {
			proxySyntax = new String[1];
			proxySyntax[0] = singleSyntax;
		}
	}
}
