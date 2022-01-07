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

import java.nio.ByteBuffer;

class SQL_DataValue_def {
	int length;

	byte[] buffer;

	ByteBuffer userBuffer;

	// ----------------------------------------------------------
	int sizeof() {
		return (buffer != null) ? TRANSPORT.size_int + buffer.length + 1
				: TRANSPORT.size_int;
	}

	// ----------------------------------------------------------
	void insertIntoByteArray(LogicalByteArray buf) {
		if (buffer != null) {
			buf.insertInt(length);
			buf.insertByteArray(buffer, length);
		} else {
			buf.insertInt(0);
		}
	}

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buf) {
		length = buf.extractInt();

		if (length > 0) {
			buffer = buf.extractByteArray(length);
		}
	}
}
