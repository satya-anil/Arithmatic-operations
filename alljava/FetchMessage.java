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

class FetchMessage {
	// ----------------------------------------------------------
	static LogicalByteArray marshal(int dialogueId, int sqlAsyncEnable, int queryTimeout, int stmtHandle,
			String stmtLabel, int stmtCharset, int maxRowCnt, int maxRowLen, String cursorName, int cursorCharset,
			String stmtOptions, InterfaceConnection ic) throws CharacterCodingException, UnsupportedCharsetException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		byte[] stmtLabelBytes = ic.encodeString(stmtLabel, 1);
		byte[] cursorNameBytes = ic.encodeString(cursorName, 1);
		byte[] stmtOptionsBytes = ic.encodeString(stmtOptions, 1);

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_int; // sqlAsyncEnable
		wlength += TRANSPORT.size_int; // queryTimeout
		wlength += TRANSPORT.size_int; // stmtHandle
		wlength += TRANSPORT.size_bytesWithCharset(stmtLabelBytes);
		wlength += TRANSPORT.size_long; // maxRowCnt
		wlength += TRANSPORT.size_long; // maxRowLen
		wlength += TRANSPORT.size_bytesWithCharset(cursorNameBytes);
		wlength += TRANSPORT.size_bytes(stmtOptionsBytes);

		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		buf.insertInt(dialogueId);
		buf.insertInt(sqlAsyncEnable);
		buf.insertInt(queryTimeout);
		buf.insertInt(stmtHandle);
		buf.insertStringWithCharset(stmtLabelBytes, stmtCharset);
		buf.insertLong(maxRowCnt);
		buf.insertLong(maxRowLen);
		buf.insertStringWithCharset(cursorNameBytes, cursorCharset);
		buf.insertString(stmtOptionsBytes);

		return buf;
	}
}
