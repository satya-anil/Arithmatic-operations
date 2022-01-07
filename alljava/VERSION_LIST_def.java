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

class VERSION_LIST_def {
	VERSION_def[] list;

	int sizeOf() { // since VERSION_def is a constant size we can just call the
		// sizeOf() once
		return VERSION_def.sizeOf() * list.length + TRANSPORT.size_int;
	}

	void insertIntoByteArray(LogicalByteArray buf) {
		buf.insertInt(list.length);

		for (int i = 0; i < list.length; i++) {
			list[i].insertIntoByteArray(buf);
		}
	}

	int sizeOfChar() {
		return list.length * VERSION_def.sizeOfChar() + 10;
	}

	void insertIntoByteArrayChar(LogicalByteArray buf, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		buf.insertFixedString(ic.encodeString("" + list.length, 1), 10);

		for (int i = 0; i < list.length; i++) {
			list[i].insertIntoByteArrayChar(buf, ic);
		}
	}

	void extractFromByteArray(LogicalByteArray buf) {
		int len = buf.extractInt();

		list = new VERSION_def[len];

		for (int i = 0; i < list.length; i++) {
			list[i] = new VERSION_def();
			list[i].extractFromByteArray(buf);
		}
	}
}
