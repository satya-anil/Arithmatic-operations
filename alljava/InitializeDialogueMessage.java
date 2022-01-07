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

class InitializeDialogueMessage {
	static LogicalByteArray marshal(USER_DESC_def userDesc,
			CONNECTION_CONTEXT_def inContext, int dialogueId, int optionFlags1,
			int optionFlags2, String sessionID, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		byte[] sessionBytes = ic.encodeString(sessionID, 1);

		wlength += userDesc.sizeOf(ic);
		wlength += inContext.sizeOf(ic);

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_int; // optionFlags1
		wlength += TRANSPORT.size_int; // optionFlags2

		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		userDesc.insertIntoByteArray(buf);
		inContext.insertIntoByteArray(buf);

		buf.insertInt(dialogueId);
		buf.insertInt(optionFlags1);
		buf.insertInt(optionFlags2);

		buf.insertString(sessionBytes);

		return buf;
	}
}
