// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 200-2007
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

class Descriptor2 {
	int noNullValue_;

	int nullValue_;

	int version_;

	int dataType_;

	int datetimeCode_;

	int maxLen_;

	int precision_;

	int scale_;

	int nullInfo_;

	int signed_;

	int odbcDataType_;

	int odbcPrecision_;

	int sqlCharset_;

	int odbcCharset_;

	String colHeadingNm_;

	String tableName_;

	String catalogName_;

	String schemaName_;

	String headingName_;

	int intLeadPrec_;

	int paramMode_;

	private int rowLength;

	public void setRowLength(int len) {
		rowLength = len;
	}

	public int getRowLength() {
		return rowLength;
	}

	public Descriptor2(LogicalByteArray buf, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		noNullValue_ = buf.extractInt();
		nullValue_ = buf.extractInt();
		version_ = buf.extractInt();
		dataType_ = buf.extractInt();
		datetimeCode_ = buf.extractInt();
		maxLen_ = buf.extractInt();
		precision_ = buf.extractInt();
		scale_ = buf.extractInt();
		nullInfo_ = buf.extractInt();
		signed_ = buf.extractInt();
		odbcDataType_ = buf.extractInt();
		odbcPrecision_ = buf.extractInt();
		sqlCharset_ = buf.extractInt();
		odbcCharset_ = buf.extractInt();

		colHeadingNm_ = ic.decodeBytes(buf.extractString(), ic.getISOMapping());
		tableName_ = ic.decodeBytes(buf.extractString(), ic.getISOMapping());
		catalogName_ = ic.decodeBytes(buf.extractString(), ic.getISOMapping());
		schemaName_ = ic.decodeBytes(buf.extractString(), ic.getISOMapping());
		headingName_ = ic.decodeBytes(buf.extractString(), ic.getISOMapping());
		intLeadPrec_ = buf.extractInt();
		paramMode_ = buf.extractInt();
	}
}
