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

class USER_DESC_def {
	int userDescType;

	byte[] userSid;

	String domainName;

	String userName;

	byte[] password;

	byte[] domainNameBytes;

	byte[] userNameBytes;

	int sizeOf(InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException {
		int size = 0;

		domainNameBytes = ic.encodeString(domainName, 1);
		userNameBytes = ic.encodeString(userName, 1);

		size += TRANSPORT.size_int; // descType

		size += TRANSPORT.size_bytes(userSid);
		size += TRANSPORT.size_bytes(domainNameBytes);
		size += TRANSPORT.size_bytes(userNameBytes);
		size += TRANSPORT.size_bytes(password);

		return size;
	}

	void insertIntoByteArray(LogicalByteArray buf) {
		buf.insertInt(userDescType);

		buf.insertString(userSid);
		buf.insertString(domainNameBytes);
		buf.insertString(userNameBytes);
		buf.insertString(password);
	}

	int sizeOfChar() {
		return 122;
	}

	void insertIntoByteArrayChar(LogicalByteArray buf, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		buf.insertFixedString(ic.encodeString("" + userDescType, 1), 10);

		buf.insertFixedString(userSid, 12);
		buf.insertFixedString(ic.encodeString(domainName, 1), 25);
		buf.insertFixedString(ic.encodeString(userName, 1), 50);
		buf.insertFixedString(password, 25);
	}
}
