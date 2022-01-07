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

class CONNECTION_CONTEXT_def {
	String datasource = "";

	String catalog = "";

	String schema = "";

	String location = "";

	String userRole = "";

	short accessMode;

	short autoCommit;

	short queryTimeoutSec;

	// This value is called "connectionTimeout" property of the driver.
	int idleTimeoutSec;   // changed short to int for Soln: 10-160122-8724

	short loginTimeoutSec;

	short txnIsolationLevel;

	short rowSetSize;

	int diagnosticFlag;

	int processId;

	String computerName = "";

	String windowText = "";

	int ctxACP;

	int ctxDataLang;

	int ctxErrorLang;

	short ctxCtrlInferNXHAR;

	short cpuToUse = -1;

	short cpuToUseEnd = -1; // for future use by DBTransporter

	String connectOptions = "";

	VERSION_LIST_def clientVersionList = new VERSION_LIST_def();

	byte[] datasourceBytes;

	byte[] catalogBytes;

	byte[] schemaBytes;

	byte[] locationBytes;

	byte[] userRoleBytes;

	byte[] computerNameBytes;

	byte[] windowTextBytes;

	byte[] connectOptionsBytes;

	// ----------------------------------------------------------
	int sizeOf(InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException {
		int size = 0;

		datasourceBytes = ic.encodeString(datasource, ic.getTerminalCharset());
		catalogBytes = ic.encodeString(catalog, ic.getTerminalCharset());
		schemaBytes = ic.encodeString(schema, ic.getTerminalCharset());
		locationBytes = ic.encodeString(location, 1);
		userRoleBytes = ic.encodeString(userRole, 1);
		computerNameBytes = ic.encodeString(computerName, ic
				.getTerminalCharset());
		windowTextBytes = ic.encodeString(windowText, ic.getTerminalCharset());
		connectOptionsBytes = ic.encodeString(connectOptions, 1);

		size = TRANSPORT.size_bytes(datasourceBytes);
		size += TRANSPORT.size_bytes(catalogBytes);
		size += TRANSPORT.size_bytes(schemaBytes);
		size += TRANSPORT.size_bytes(locationBytes);
		size += TRANSPORT.size_bytes(userRoleBytes);

		size += TRANSPORT.size_short; // accessMode
		size += TRANSPORT.size_short; // autoCommit
		size += TRANSPORT.size_int; // queryTimeoutSec
		size += TRANSPORT.size_int; // idleTimeoutSec
		size += TRANSPORT.size_int; // loginTimeoutSec
		size += TRANSPORT.size_short; // txnIsolationLevel
		size += TRANSPORT.size_short; // rowSetSize

		size += TRANSPORT.size_short; // diagnosticFlag
		size += TRANSPORT.size_int; // processId

		size += TRANSPORT.size_bytes(computerNameBytes);
		size += TRANSPORT.size_bytes(windowTextBytes);

		size += TRANSPORT.size_int; // ctxACP
		size += TRANSPORT.size_int; // ctxDataLang
		size += TRANSPORT.size_int; // ctxErrorLang
		size += TRANSPORT.size_short; // ctxCtrlInferNCHAR

		size += TRANSPORT.size_short; // cpuToUse
		size += TRANSPORT.size_short; // cpuToUseEnd
		size += TRANSPORT.size_bytes(connectOptionsBytes);

		size += clientVersionList.sizeOf();

		return size;
	}

	// ----------------------------------------------------------
	void insertIntoByteArray(LogicalByteArray buf) {
		buf.insertString(datasourceBytes);
		buf.insertString(catalogBytes);
		buf.insertString(schemaBytes);
		buf.insertString(locationBytes);
		buf.insertString(userRoleBytes);

		buf.insertShort(accessMode);
		buf.insertShort(autoCommit);
		buf.insertInt(queryTimeoutSec);
		buf.insertInt(idleTimeoutSec);
		buf.insertInt(loginTimeoutSec);
		buf.insertShort(txnIsolationLevel);
		buf.insertShort(rowSetSize);

		buf.insertInt(diagnosticFlag);
		buf.insertInt(processId);

		buf.insertString(computerNameBytes);
		buf.insertString(windowTextBytes);

		buf.insertInt(ctxACP);
		buf.insertInt(ctxDataLang);
		buf.insertInt(ctxErrorLang);
		buf.insertShort(ctxCtrlInferNXHAR);

		buf.insertShort(cpuToUse);
		buf.insertShort(cpuToUseEnd);
		buf.insertString(connectOptionsBytes);

		clientVersionList.insertIntoByteArray(buf);
	}

	int sizeOfChar() {
		return clientVersionList.sizeOfChar() + 552;
	}

	void insertIntoByteArrayChar(LogicalByteArray buf, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		buf.insertFixedString(ic.encodeString(datasource, ic
				.getTerminalCharset()), TRANSPORT.sql_identifier);
		buf.insertFixedString(
				ic.encodeString(catalog, ic.getTerminalCharset()),
				TRANSPORT.sql_identifier);
		buf.insertFixedString(ic.encodeString(schema, ic.getTerminalCharset()),
				TRANSPORT.sql_identifier);
		buf.insertFixedString(ic.encodeString(location, 1),
				TRANSPORT.sql_identifier);
		buf.insertFixedString(ic.encodeString(userRole, 1),
				TRANSPORT.sql_identifier);

		buf.insertFixedString(ic.encodeString("" + accessMode, 1), 10);
		buf.insertFixedString(ic.encodeString("" + autoCommit, 1), 10);
		buf.insertFixedString(ic.encodeString("" + queryTimeoutSec, 1), 12);
		buf.insertFixedString(ic.encodeString("" + idleTimeoutSec, 1), 12);
		buf.insertFixedString(ic.encodeString("" + loginTimeoutSec, 1), 12);
		buf.insertFixedString(ic.encodeString("" + txnIsolationLevel, 1), 10);
		buf.insertFixedString(ic.encodeString("" + rowSetSize, 1), 10);

		buf.insertFixedString(ic.encodeString("" + diagnosticFlag, 1), 12);
		buf.insertFixedString(ic.encodeString("" + processId, 1), 12);

		buf.insertFixedString(ic.encodeString("" + computerName, 1), 16);
		buf.insertFixedString(ic.encodeString("" + windowText, 1), 50);

		buf.insertFixedString(ic.encodeString("" + ctxACP, 1), 12);
		buf.insertFixedString(ic.encodeString("" + ctxDataLang, 1), 12);
		buf.insertFixedString(ic.encodeString("" + ctxErrorLang, 1), 12);
		buf.insertFixedString(ic.encodeString("" + ctxCtrlInferNXHAR, 1), 10);

		buf.insertFixedString(ic.encodeString("" + cpuToUse, 1), 10);
		buf.insertFixedString(ic.encodeString("" + cpuToUseEnd, 1), 10);

		buf.insertFixedString(ic.encodeString("" + connectOptions, 1), 20);

		clientVersionList.insertIntoByteArrayChar(buf, ic);
	}
}
