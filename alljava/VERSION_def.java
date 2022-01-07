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

class VERSION_def {
	short componentId;

	short majorVersion;

	short minorVersion;

	int buildId;

	static int sizeOf() {
		return TRANSPORT.size_int + TRANSPORT.size_short * 3;
	}

	void insertIntoByteArray(LogicalByteArray buf) {
		buf.insertShort(componentId);
		buf.insertShort(majorVersion);
		buf.insertShort(minorVersion);
		buf.insertInt(buildId);
	}

	static int sizeOfChar() {
		return 50;
	}

	void insertIntoByteArrayChar(LogicalByteArray buf, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		buf.insertFixedString(ic.encodeString("" + componentId, 1), 10);
		buf.insertFixedString(ic.encodeString("" + majorVersion, 1), 10);
		buf.insertFixedString(ic.encodeString("" + minorVersion, 1), 10);
		buf.insertFixedString(ic.encodeString("" + buildId, 1), 20);
	}

	void extractFromByteArray(LogicalByteArray buf) {
		componentId = buf.extractShort();
		majorVersion = buf.extractShort();
		minorVersion = buf.extractShort();
		buildId = buf.extractInt();
	}
}
