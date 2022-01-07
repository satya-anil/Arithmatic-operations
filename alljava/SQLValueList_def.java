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

import java.sql.SQLException;

class SQLValueList_def {
	SQLValue_def[] buffer;

	// ----------------------------------------------------------
	int sizeof() {
		int size = TRANSPORT.size_int;

		if (buffer != null) {
			for (int i = 0; i < buffer.length; i++) {
				size += buffer[i].sizeof();
			}
		}
		return size;
	}

	// ----------------------------------------------------------
	void insertIntoByteArray(LogicalByteArray buf) {
		if (buffer != null) {
			buf.insertInt(buffer.length);
			for (int i = 0; i < buffer.length; i++) {
				buffer[i].insertIntoByteArray(buf);
			}
		} else {
			buf.insertInt(0);
		}
	}

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buf) throws SQLException {
		int len = buf.extractInt();

		buffer = new SQLValue_def[len];

		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = new SQLValue_def();
			buffer[i].extractFromByteArray(buf);
		}
	}
}
