// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2005, 2006
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

/**
 * XABROKER structure from XAClient_common.h
 typedef struct BrkXaGetTransactionStateRequest
 {
 uint32               	size_;		// size of the request structure
 ReqReplyFlag		request_;	// BRK_GW_REQUEST
 ReqCode		reqCode_;	// BRK_XA_GET_TRANSACTION_STATE
 uint32               	filler_;	// must be 0
 } BrkXaGetTransactionStateRequest;
 */
package com.tandem.t4jdbc;

import java.util.Locale;
import java.sql.SQLException;

class BrkXaRequest {
	protected Locale m_locale;
	protected T4Properties m_t4props;
	private final int BRK_GW_REQUEST = 1;
	private final int m_size_ = 16;
	protected int m_ReqReplyFlag = BRK_GW_REQUEST;
	protected int m_ReqCode;
	protected int m_filler = 0;

	/**
	 *     uint32               	size_;		// size of the request structure
	 *     ReqReplyFlag		request_;	// BRK_GW_REQUEST
	 *     ReqCode		reqCode_;	// BRK_XA_GET_TRANSACTION_STATE
	 *     uint32               	filler_;	// must be 0
	 */
	BrkXaRequest(T4Properties t4props) throws SQLException {
		m_locale = t4props.getLocale();
		m_ReqCode = BrkXaReply.BRK_XA_GET_TRANSACTION_STATE;
	}

	/**
	 * Common elements for all the xa request messages are:
	 *     uint32               	size_;		// size of the request structure
	 *     ReqReplyFlag		request_;	// BRK_GW_REQUEST
	 *     ReqCode		reqCode_;	// BRK_XA_*
	 *     uint32               	filler_;	// must be 0
	 */
	protected BrkXaRequest(T4Properties t4props, int p_ReqCode)
			throws SQLException {
		m_t4props = t4props;
		m_locale = t4props.getLocale();
		m_ReqCode = p_ReqCode;
	}

	/**
	 * Common elements for all the xa request messages are:
	 *     uint32               	size_;		// size of the request structure
	 *     ReqReplyFlag		request_;	// BRK_GW_REQUEST
	 *     ReqCode		reqCode_;	// BRK_XA_*
	 *     uint32               	filler_;	// must be 0
	 */
	int totalSizeOf() {
		return m_size_;
	}

	//----------------------------------------------------------
	/** This method will serialize this object into a byte array.
	 *
	 * @param  p_buffer  A byte array into which this object is serialized
	 * @param  p_index   The offset into buffer1 at which to start serialization.
	 * @param  p_locale The locale information for the error messages.
	 *
	 * @retrun  An offset in buffer of the first byte following the serialized object is returned
	 *
	 * @exception  SQLException is thrown
	 */
	//commenting this method since the values are inserted in BrkXaMessage.java itself - R30
//	int insertIntoByteArray(LogicalByteArray p_buffer, int p_index,
//			Locale p_locale) throws SQLException {
//		p_index = p_buffer.insertInt(p_index, totalSizeOf());
//		p_index = p_buffer.insertInt(p_index, m_ReqReplyFlag);
//		p_index = p_buffer.insertInt(p_index, m_ReqCode);
//		p_index = p_buffer.insertInt(p_index, m_filler);
//		return p_index;
//	} // end insertIntoByteArray

	//----------------------------------------------------------
	/** This method will un-serialize this object from a byte array.
	 *
	 * @param  p_buffer  A byte array from which this object is un-serialized.
	 * @param  p_index   The offset into buffer1 at which to start un-serialization.
	 * @param  p_hIndex   The length (in bytes) of the header.
	 * @param  p_locale The locale information for the error messages.
	 * @param  p_addr    The address information for the error messages.
	 *
	 * @retrun  An offset in buffer of the first byte following the serialized object is returned
	 *
	 * @exception  SQLException is thrown
	 */
	int extractFromByteArray(LogicalByteArray p_buffer, int p_index,
			int p_hIndex, Locale p_locale, String p_addr) throws SQLException {
		int size = p_buffer.extractInt();
		p_index += 4;
		m_ReqReplyFlag = p_buffer.extractInt();
		p_index += 4;
		m_ReqCode = p_buffer.extractInt();
		p_index += 4;
		int m_filler = p_buffer.extractInt();
		p_index += 4;
		return p_index;

	}

} // End of class BrkXaRequest
