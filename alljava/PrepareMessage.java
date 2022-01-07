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

class PrepareMessage {
	// ----------------------------------------------------------
	static LogicalByteArray marshal(int dialogueId, int sqlAsyncEnable,
			int queryTimeout, short stmtType, int sqlStmtType,
			String stmtLabel, int stmtLabelCharset, String cursorName,
			int cursorNameCharset, String moduleName, int moduleNameCharset,
			long moduleTimestamp, String sqlString, int sqlStringCharset,
			String stmtOptions, String stmtExplainLabel, int maxRowsetSize,
			byte[] txId, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		byte[] stmtLabelBytes = ic.encodeString(stmtLabel, 1);
		byte[] cursorNameBytes = ic.encodeString(cursorName, 1);
		byte[] moduleNameBytes = ic.encodeString(moduleName, 1);
		byte[] sqlStringBytes = ic.encodeString(sqlString, ic
				.getTerminalCharset());
		byte[] stmtOptionsBytes = ic.encodeString(stmtOptions, 1);
		byte[] stmtExplainLabelBytes = ic.encodeString(stmtExplainLabel, 1);

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_int; // sqlAsyncEnable
		wlength += TRANSPORT.size_int; // queryTimeout
		wlength += TRANSPORT.size_short; // stmtType
		wlength += TRANSPORT.size_int; // sqlStmtType
		wlength += TRANSPORT.size_bytesWithCharset(stmtLabelBytes); // +stmtCharset
		wlength += TRANSPORT.size_bytesWithCharset(cursorNameBytes); // +cursorCharset
		wlength += TRANSPORT.size_bytesWithCharset(moduleNameBytes);
		if (moduleName != null && moduleName.length() > 0) {
			wlength += TRANSPORT.size_long; // moduleTimestamp
		}
		wlength += TRANSPORT.size_bytesWithCharset(sqlStringBytes); // +sqlStringCharset
		wlength += TRANSPORT.size_bytes(stmtOptionsBytes);
		wlength += TRANSPORT.size_bytes(stmtExplainLabelBytes);
		wlength += TRANSPORT.size_int; // maxRowsetSize
		wlength += TRANSPORT.size_bytes(txId); // transId

		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		buf.insertInt(dialogueId);
		buf.insertInt(sqlAsyncEnable);
		buf.insertInt(queryTimeout);
		buf.insertShort(stmtType);
		buf.insertInt(sqlStmtType);
		buf.insertStringWithCharset(stmtLabelBytes, stmtLabelCharset);
		buf.insertStringWithCharset(cursorNameBytes, cursorNameCharset);
		buf.insertStringWithCharset(moduleNameBytes, moduleNameCharset);
		if (moduleName != null && moduleName.length() > 0) {
			buf.insertLong(moduleTimestamp);
		}
		buf.insertStringWithCharset(sqlStringBytes, sqlStringCharset);
		buf.insertString(stmtOptionsBytes);
		buf.insertString(stmtExplainLabelBytes);
		buf.insertInt(maxRowsetSize);
		buf.insertString(txId);

		return buf;
	}
}
