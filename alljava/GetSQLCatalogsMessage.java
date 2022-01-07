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

class GetSQLCatalogsMessage {
	static LogicalByteArray marshal(int dialogueId, String stmtLabel,
			short APIType, String catalogNm, String schemaNm, String tableNm,
			String tableTypeList, String columnNm, int columnType,
			int rowIdScope, int nullable, int uniqueness, int accuracy,
			short sqlType, int metadataId, String fkCatalogNm,
			String fkSchemaNm, String fkTableNm, InterfaceConnection ic)
			throws CharacterCodingException, UnsupportedCharsetException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		byte[] stmtLabelBytes = ic.encodeString(stmtLabel, 1);
		byte[] catalogNmBytes = ic.encodeString(catalogNm, ic.getISOMapping());
		byte[] schemaNmBytes = ic.encodeString(schemaNm, ic.getISOMapping());
		byte[] tableNmBytes = ic.encodeString(tableNm, ic.getISOMapping());
		byte[] tableTypeListBytes = ic.encodeString(tableTypeList, ic
				.getISOMapping());
		byte[] columnNmBytes = ic.encodeString(columnNm, ic.getISOMapping());

		byte[] fkCatalogNmBytes = ic.encodeString(fkCatalogNm, ic
				.getISOMapping());
		byte[] fkSchemaNmBytes = ic
				.encodeString(fkSchemaNm, ic.getISOMapping());
		byte[] fkTableNmBytes = ic.encodeString(fkTableNm, ic.getISOMapping());

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_bytes(stmtLabelBytes);
		wlength += TRANSPORT.size_short; // APIType
		wlength += TRANSPORT.size_bytes(catalogNmBytes, true);
		wlength += TRANSPORT.size_bytes(schemaNmBytes, true);
		wlength += TRANSPORT.size_bytes(tableNmBytes, true);
		wlength += TRANSPORT.size_bytes(tableTypeListBytes, true);
		wlength += TRANSPORT.size_bytes(columnNmBytes, true);
		wlength += TRANSPORT.size_int; // columnType
		wlength += TRANSPORT.size_int; // rowIdScope
		wlength += TRANSPORT.size_int; // nullable
		wlength += TRANSPORT.size_int; // uniqueness
		wlength += TRANSPORT.size_int; // accuracy
		wlength += TRANSPORT.size_short; // sqlType
		wlength += TRANSPORT.size_int; // metadataId
		wlength += TRANSPORT.size_bytes(fkCatalogNmBytes, true);
		wlength += TRANSPORT.size_bytes(fkSchemaNmBytes, true);
		wlength += TRANSPORT.size_bytes(fkTableNmBytes, true);

		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		buf.insertInt(dialogueId);
		buf.insertString(stmtLabelBytes);
		buf.insertShort(APIType);
		buf.insertString(catalogNmBytes, true);
		buf.insertString(schemaNmBytes, true);
		buf.insertString(tableNmBytes, true);
		buf.insertString(tableTypeListBytes, true);
		buf.insertString(columnNmBytes, true);
		buf.insertInt(columnType);
		buf.insertInt(rowIdScope);
		buf.insertInt(nullable);
		buf.insertInt(uniqueness);
		buf.insertInt(accuracy);
		buf.insertShort(sqlType);
		buf.insertInt(metadataId);
		buf.insertString(fkCatalogNmBytes, true);
		buf.insertString(fkSchemaNmBytes, true);
		buf.insertString(fkTableNmBytes, true);

		return buf;
	}
}
