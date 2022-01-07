package com.tandem.t4jdbc;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;

public class SQLWarningOrError {
	int rowId;
	int sqlCode;
	String text;
	String sqlState;

	public SQLWarningOrError(int rowId, int sqlCode, String text, String sqlState) {
		this.rowId = rowId;
		this.sqlCode = sqlCode;
		this.text = text;
		this.sqlState = sqlState;
	}

	public SQLWarningOrError(LogicalByteArray buf, InterfaceConnection ic, int charset)
			throws CharacterCodingException, UnsupportedCharsetException {
		rowId = buf.extractInt();
		sqlCode = buf.extractInt();
		text = ic.decodeBytes(buf.extractString(), charset);
		sqlState = new String(buf.extractByteArray(5));
		buf.extractByte(); // null terminator
	}
}
