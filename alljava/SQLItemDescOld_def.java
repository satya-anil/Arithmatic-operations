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

class SQLItemDescOld_def extends SQLItemDesc_def {
	void extractFromByteArray(LogicalByteArray buf, InterfaceConnection ic) throws UnsupportedCharsetException,
			CharacterCodingException {
		version = buf.extractInt();
		dataType = buf.extractInt();
		datetimeCode = buf.extractInt();
		maxLen = buf.extractInt();
		precision = buf.extractShort();
		scale = buf.extractShort();
		nullInfo = buf.extractByte();

		colHeadingNm = ic.decodeBytes(buf.extractString(), 1);

		signType = buf.extractByte();
		ODBCDataType = buf.extractInt();
		ODBCPrecision = buf.extractShort();
		SQLCharset = buf.extractInt();
		ODBCCharset = buf.extractInt();

		TableName = ic.decodeBytes(buf.extractString(), 1);
		CatalogName = ic.decodeBytes(buf.extractString(), 1);
		SchemaName = ic.decodeBytes(buf.extractString(), 1);
		Heading = ic.decodeBytes(buf.extractString(), 1);

		intLeadPrec = buf.extractInt();
		paramMode = buf.extractInt();
	}
}
