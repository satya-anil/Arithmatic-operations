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

class CloseMessage {
	// ----------------------------------------------------------
	static LogicalByteArray marshal(int dialogueId, String stmtLabel,
			short freeResourceOpt, InterfaceConnection ic)
			throws UnsupportedCharsetException, CharacterCodingException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		byte[] stmtLabelBytes = ic.encodeString(stmtLabel, 1);

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_bytes(stmtLabelBytes);
		wlength += TRANSPORT.size_short; // freeResourceOpt

		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		buf.insertInt(dialogueId);
		buf.insertString(stmtLabelBytes);
		buf.insertShort(freeResourceOpt);

		return buf;
	}
}
