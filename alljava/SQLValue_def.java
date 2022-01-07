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

class SQLValue_def {
	int dataType;

	short dataInd;

	SQL_DataValue_def dataValue;

	int dataCharSet;

	// ----------------------------------------------------------
	int sizeof() {
		return TRANSPORT.size_int * 2 + TRANSPORT.size_short
				+ dataValue.sizeof();
	}

	// ----------------------------------------------------------
	void insertIntoByteArray(LogicalByteArray buf) {
		buf.insertInt(dataType);
		buf.insertShort(dataInd);
		dataValue.insertIntoByteArray(buf);
		buf.insertInt(dataCharSet);
	}

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buf) throws SQLException {
		dataType = buf.extractInt();
		dataInd = buf.extractShort();

		dataValue = new SQL_DataValue_def();
		dataValue.extractFromByteArray(buf);

		dataCharSet = buf.extractInt();
	}
}
