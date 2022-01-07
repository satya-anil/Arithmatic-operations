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

class ExecuteMessage {
	// ----------------------------------------------------------
	static LogicalByteArray marshal(int dialogueId, int sqlAsyncEnable, int queryTimeout, int inputRowCnt,
			int maxRowsetSize, int sqlStmtType, int stmtHandle, int stmtType, String sqlString, int sqlStringCharset,
			String cursorName, int cursorNameCharset, String stmtLabel, int stmtLabelCharset, String stmtExplainLabel,
			SQL_DataValue_def inputDataValue, SQLValueList_def inputValueList, byte[] txId, boolean isUserBuffer,
			InterfaceConnection ic,byte []extTxID

	) throws CharacterCodingException, UnsupportedCharsetException

	{
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		byte[] sqlStringBytes = ic.encodeString(sqlString, ic.getTerminalCharset());
		byte[] cursorNameBytes = ic.encodeString(cursorName, 1);
		byte[] stmtLabelBytes = ic.encodeString(stmtLabel, 1);
		byte[] stmtExplainLabelBytes = ic.encodeString(stmtExplainLabel, 1);

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_int; // sqlAsyncEnable
		wlength += TRANSPORT.size_int; // queryTimeout
		wlength += TRANSPORT.size_int; // inputRowCnt
		wlength += TRANSPORT.size_int; // maxRowsetSize
		wlength += TRANSPORT.size_int; // sqlStmtType
		wlength += TRANSPORT.size_int; // stmtHandle
		wlength += TRANSPORT.size_int; // stmtType
		wlength += TRANSPORT.size_bytesWithCharset(sqlStringBytes); // +sqlStringCharset
		wlength += TRANSPORT.size_bytesWithCharset(cursorNameBytes); // +cursorNameCharset
		wlength += TRANSPORT.size_bytesWithCharset(stmtLabelBytes); // +stmtLabelCharset
		wlength += TRANSPORT.size_bytes(stmtExplainLabelBytes);

		if (!isUserBuffer) {
			wlength += inputDataValue.sizeof();
			wlength += TRANSPORT.size_bytes(txId); // transId
		}
		//Commitwork RFE changes
		if(extTxID != null){
			wlength += TRANSPORT.size_bytes(extTxID);	// extTxID
		}else {
			wlength += TRANSPORT.size_int; // extTxID
		}
		
		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		buf.insertInt(dialogueId);
		buf.insertInt(sqlAsyncEnable);
		buf.insertInt(queryTimeout);
		buf.insertInt(inputRowCnt);
		buf.insertInt(maxRowsetSize);
		buf.insertInt(sqlStmtType);
		buf.insertInt(stmtHandle);
		buf.insertInt(stmtType);
		buf.insertStringWithCharset(sqlStringBytes, sqlStringCharset);
		buf.insertStringWithCharset(cursorNameBytes, cursorNameCharset);
		buf.insertStringWithCharset(stmtLabelBytes, stmtLabelCharset);
		buf.insertString(stmtExplainLabelBytes);

		if (isUserBuffer) {
			buf.setDataBuffer(inputDataValue.userBuffer);

			byte[] trailer = null;
			if (txId == null || txId.length == 0) {
				trailer = new byte[4];
				for (int i = 0; i < 4; ++i) {
					trailer[i] = (byte) 0;
				}
			} else {
				int len = txId.length + 1;
				trailer = new byte[4 + txId.length + 1];

				trailer[0] = (byte) ((len >>> 24) & 0xff);
				trailer[1] = (byte) ((len >>> 16) & 0xff);
				trailer[2] = (byte) ((len >>> 8) & 0xff);
				trailer[3] = (byte) ((len) & 0xff);
				System.arraycopy(txId, 0, trailer, 4, txId.length);
				trailer[len + 4 - 1] = '\0';
			}

			buf.setTrailer(trailer);
		} else {
			inputDataValue.insertIntoByteArray(buf);
			buf.insertString(txId);
		}
		//Added for Commitwork RFE -Start
	    if(extTxID != null && extTxID.length != 0){        
	    	  buf.insertInt(extTxID.length);	
	          buf.insertByteArray(extTxID, extTxID.length);
	          buf.insertByte((byte) 0);
		}else {
	    	  buf.insertInt(0);
		}
	  //Added for Commitwork RFE -End
		return buf;
	}
}
