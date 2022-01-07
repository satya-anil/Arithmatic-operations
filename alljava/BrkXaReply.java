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

package com.tandem.t4jdbc;

import java.sql.SQLException;
import java.util.Locale;
import javax.transaction.xa.Xid;

/**
 *
 * General Response structure from the MXCS
 * Header                     header
 * Eception_exc_ 	      *exception
 * BrkXaRollbackReply 	      *reply
 *
 * Refer to XABROKER documentation for more details.

 typedef struct BrkXaCommitReply
 {
 uint32			size_;		// size of the reply
 ReqReplyFlag         	request_;	// BRK_GW_REPLY
 ReqCode              	reqCode_;	// BRK_XA_COMMIT
 BrkGwStatus          	gwStatus_;       // XABroker status
 int32                	retval_;
 } BrkXaCommitReply;
 typedef struct BrkXaForgetReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag        	        request_;	// BRK_GW_REPLY
 ReqCode              	reqCode_;	// BRK_XA_FORGET
 BrkGwStatus          	gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaForgetReply;
 typedef struct BrkXaPrepareReply
 {
 uint32               	size_; 		// size of the reply
 ReqReplyFlag        	        request_;	// BRK_GW_REPLY
 ReqCode              	reqCode_; 	// BRK_XA_PREPARE
 BrkGwStatus          	gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaPrepareReply;
 // last_ -- because the XABroker process ignores the count specified in
 // the request, last_ will be set to indicate there are no more XIDs to
 // return.  The client sends ONE request.  The Gateway process will return
 // one or more replies; last_ will be set in the reply which returns the last XID.
 typedef struct BrkXaRecoverReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag         	request_;	// BRK_GW_REPLY
 ReqCode              	reqCode_;	// BRK_XA_RECOVER
 BrkGwStatus          	gwStatus_;	// XABroker status
 int32                  	last_;		// no more XIDs are available
 int32                  	retval_;	// retval indicates how many XIDs
 XID                  	xid_[1];	// are in the xid array
 } BrkXaRecoverReply;
 typedef struct BrkXaRollbackReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag         	request_;	// BRK_GW_REPLY
 ReqCode              	reqCode_; 	// BRK_XA_ROLLBACK
 BrkGwStatus          	gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaRollbackReply;
 typedef struct BrkXaStartReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag         	request_;	// BRK_GW_REPLY
 ReqCode              	reqCode_;	// BRK_XA_START
 BrkGwStatus          	gwStatus_;	// XABroker status
 int32                	retval_;
 int32			filler_;
 NskTmfTid            	localNskTid_; // for use in goRemote request – not implemented
 } BrkXaStartReply;
 typedef struct BrkXaEndReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag		request_;	// BRK_GW_REPLY
 ReqCode		reqCode_;	// BRK_XA_END
 BrkGwStatus		gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaEndReply;
 typedef struct BrkXaSetTimeoutReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag		request_;	// BRK_GW_REPLY
 ReqCode		reqCode_;	// BRK_XA_END
 BrkGwStatus		gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaSetTimeoutReply;
 typedef struct BrkXaGetTimeoutReply
 {
 uint32               	size_;		// size of the reply
 ReqReplyFlag		request_;	// BRK_GW_REPLY
 ReqCode		reqCode_;	// BRK_XA_END
 BrkGwStatus		gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaGetTimeoutReply;
 typedef struct BrkXaGetTransactionStateReply
 {
 uint32               size_;		// size of the reply
 ReqReplyFlag		request_;	// BRK_GW_REPLY
 ReqCode		reqCode_;	// BRK_XA_END
 BrkGwStatus		gwStatus_;	// XABroker status
 int32                	retval_;
 } BrkXaGetTransactionStateReply;
 */

class BrkXaReply {
	private Locale m_locale;
	private LogicalByteArray m_buffer;
	private Header m_header;
	private BrkXa_exc_ m_p1_exception;
	private int m_header_size;
	private int m_reply_size;
	private int m_ReqReplyFlag;
	private int m_ReqCode;
	private int m_BrkGwStatus;
	private int m_last;
	private int m_xarm_retval;
	private SQLMXXid[] m_xids;
	private T4Properties m_t4props;
	private int m_reply_index;
	private int m_exp_index;
	private int m_exp_pointer;
	private int m_reply_pointer;
	private byte[] m_tmfId;

	/**
	 * Please refer to XABROKER XAClient_Common.h file these defined values.
	 */
	static final int BRK_XA_COMMIT = 2;
	static final int BRK_XA_FORGET = 3;
	static final int BRK_XA_PREPARE = 5;
	static final int BRK_XA_RECOVER = 6;
	static final int BRK_XA_ROLLBACK = 7;
	static final int BRK_XA_START = 8;
	static final int BRK_XA_END = 10;
	static final int BRK_XA_RECOVER_NEXT = 11;
	static final int BRK_XA_SET_TIMEOUT = 12;
	static final int BRK_XA_GET_TIMEOUT = 13;
	static final int BRK_XA_GET_TRANSACTION_STATE = 14;

	static final int BRK_GW_REPLY = 0;

	final int BRK_GW_OK = 0; // 0
	final int BRK_GW_UNKNOWNREQ = 1; // 1,	unknown request code
	final int BRK_GW_MAXCONNECTIONS = 2; // 2,	connection will be closed due
	//	to maxConnections limit
	final int BRK_GW_INCORRECTSIZE = 3; // 3,	request has incorrect size
	final int BRK_GW_NOTOPENED = 4; // 4,	need to do xa_open first
	final int BRK_GW_NULLRMNAME = 5; // 5,	RM name is zero length
	final int BRK_GW_NOTSAMERMNAME = 6; // 6,	rm Name in xa_open does not
	//	match that in previous open
	final int BRK_GW_BADFLAGS = 7; // 7,	flags parameter is not valid
	final int BRK_GW_INVALIDSESSION = 8; // 8,
	final int BRK_GW_NOMEMORY = 9; // 9,
	final int BRK_GW_INCORRECTVERSION = 10; // 10

	int getBrkStatus() {
		return m_BrkGwStatus;
	}

	int getXARMretval() {
		return m_xarm_retval;
	}

	Xid[] getXids() {
		return m_xids;
	}

	private void checkXABrokerStatus() throws SQLException {
		switch (m_BrkGwStatus) {
		case BRK_GW_OK:
			break;
		case BRK_GW_MAXCONNECTIONS: // 2,	connection will be closed due to maxConnections limit
			throw SQLMXMessages.createSQLException(m_t4props, m_locale,
					"ids_max_brk_conn_reached", String.valueOf(m_BrkGwStatus));
		case BRK_GW_BADFLAGS: // 7,	flags parameter is not valid
			throw SQLMXMessages.createSQLException(m_t4props, m_locale,
					"ids_invalid_xa_flags", String.valueOf(m_BrkGwStatus));
		case BRK_GW_INCORRECTVERSION: // 10
			throw SQLMXMessages.createSQLException(m_t4props, m_locale,
					"ids_invalid_xa_version", String.valueOf(m_BrkGwStatus));
		case BRK_GW_UNKNOWNREQ: // 1,	unknown request code
		case BRK_GW_INCORRECTSIZE: // 3,	request has incorrect size
		case BRK_GW_NOTOPENED: // 4,	need to do xa_open first
		case BRK_GW_NULLRMNAME: // 5,	RM name is zero length
		case BRK_GW_NOTSAMERMNAME: // 6,	rm Name in xa_open does not match that in previous open
		case BRK_GW_INVALIDSESSION: // 8,
		case BRK_GW_NOMEMORY: // 9,
		default:
			SQLException sqx1 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "internal_error", String.valueOf(m_BrkGwStatus));
			SQLException sqx2 = SQLMXMessages
					.createSQLException(m_t4props, m_locale,
							"contact_hp_error", String.valueOf(m_BrkGwStatus));
			sqx1.setNextException(sqx2);
			throw sqx1;
		}
	}

	/**
	 * validate the incoming XA request is valid.
	 * @param m_locale
	 * @param m_addr
	 * @param p_ReqCode
	 */
	static void checkReqCode(T4Properties t4props, int p_ReqCode)
			throws SQLException {
		switch (p_ReqCode) {
		case BRK_XA_COMMIT:
		case BRK_XA_FORGET:
		case BRK_XA_PREPARE:
		case BRK_XA_RECOVER:
		case BRK_XA_ROLLBACK:
		case BRK_XA_START:
		case BRK_XA_END:
		case BRK_XA_RECOVER_NEXT:
		case BRK_XA_SET_TIMEOUT:
		case BRK_XA_GET_TIMEOUT:
		case BRK_XA_GET_TRANSACTION_STATE:
			break;
		default:
			SQLException sqx1 = SQLMXMessages.createSQLException(t4props, null,
					"internal_error", String.valueOf(p_ReqCode));
			SQLException sqx2 = SQLMXMessages.createSQLException(t4props, null,
					"contact_hp_error", String.valueOf(p_ReqCode));
			sqx1.setNextException(sqx2);
			throw sqx1;

		}

	}

	private void checkReqReplyFlag() throws SQLException {
		if (m_ReqReplyFlag != BRK_GW_REPLY) {
			SQLException sqx1 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "internal_error", String.valueOf(m_ReqReplyFlag));
			SQLException sqx2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "contact_hp_error", String
							.valueOf(m_ReqReplyFlag));
			sqx1.setNextException(sqx2);
			throw sqx1;
		}
	}

	// Header starts at 0
	private void extractHeader() {
		m_header.extractFromByteArray(m_buffer);

		// increment past the header
		m_header_size = m_header.sizeOf();
		m_exp_pointer = m_header_size;
		m_reply_pointer = m_header_size + 4;
	}

	private void extractException() throws SQLException {
		// Extract the exception
		m_exp_index = m_buffer.extractInt() + m_header_size;
		m_buffer.setLocation(m_exp_index);
		m_p1_exception = new BrkXa_exc_(m_buffer, m_exp_index, m_header_size,
				m_t4props);
	}

	private void extractReplySize() {
		m_buffer.setLocation(m_reply_pointer);
		m_reply_index = m_buffer.extractInt() + m_header_size;
		m_buffer.setLocation(m_reply_index);
		m_reply_size = m_buffer.extractInt();
		m_reply_index += 4;
	}

	private void extractReplyFlag() throws SQLException {
		m_ReqReplyFlag = m_buffer.extractInt();
		checkReqReplyFlag();
		m_reply_index += 4;
	}

	private void extractReqCode() throws SQLException {
		m_ReqCode = m_buffer.extractInt();
		checkReqCode(m_t4props, m_ReqCode);
		m_reply_index += 4;
	}

	private void extractGwStatus() throws SQLException {
		m_BrkGwStatus = m_buffer.extractInt();
		checkXABrokerStatus();
		m_reply_index += 4;
	}

	private void extractLast() {
		// Read m_last only for BRK_XA_RECOVER. m_last is not used.
		if (m_ReqCode == BRK_XA_RECOVER) {
			// Read the m_last. m_last is not used.
			m_last = m_buffer.extractInt();
			m_reply_index += 4;

		}
	}

	private void extractRmRetVal() {
		m_xarm_retval = m_buffer.extractInt();
		m_reply_index += 4;
	}

	// refer to XAClient_common.h for structure details.
	private void extractXids() {
		// Read Xid[] only for BRK_XA_RECOVER
		if (m_ReqCode == BRK_XA_RECOVER) {
			m_xids = new SQLMXXid[m_xarm_retval];
			//get IDL_unsigned_long _length. IDL_unsigned_long_length is not used.
			int IDL_unsigned_long_length = m_buffer.extractInt();
			m_reply_index += 4;
			//next, is a 4 byte padding
			m_reply_index += 4;
			//followed by the offset location (from start) where the XIDs lie
			m_buffer.setLocation(m_reply_index);
			int offset_from_start = m_buffer.extractInt();
			m_reply_index = m_header_size + offset_from_start;
			for (int i = 0; i < m_xarm_retval; i++) {
				m_xids[i] = new SQLMXXid(m_t4props, m_buffer, m_reply_index);
				m_reply_index += m_xids[i].sizeof();
			}
		}
	}

	private void extractTmfId() {
		if (m_ReqCode == BRK_XA_START) {
			m_tmfId = m_buffer.extractByteArray(8);
			m_reply_index += 8;
		}
	}

	byte[] getTmfId() {
		return m_tmfId;
	}

	//-------------------------------------------------------------
	// Maintain the extracting methods sequence since the data
	// format is fixed. Refer to the XABROKER documentation for the
	// message data format.
	BrkXaReply(LogicalByteArray buffer1, T4Properties t4props)
			throws SQLException {

		m_buffer = buffer1;
		m_t4props = t4props;
		m_locale = t4props.getLocale();
		m_header = new Header();
		//added this for R30
		buffer1.setLocation(0);
		// Extract the message header
		extractHeader();

		// Extract the exception
		extractException();

		//Read the Reply Size
		extractReplySize();
		// TODO: Validate the size returned.

		// Read the ReqReplyFlag
		extractReplyFlag();

		// Read the ReqCode
		extractReqCode();

		// Read the BrkGwStatus
		extractGwStatus();

		// Read m_last only for BRK_XA_RECOVER
		extractLast();

		// Read the xarm_retval
		extractRmRetVal();

		// Read Xid[] only for BRK_XA_RECOVER
		extractXids();

		// Read tmfId only for BRK_XA_START
		extractTmfId();
	} // end BrkXaReply

} // BrkXaReply
