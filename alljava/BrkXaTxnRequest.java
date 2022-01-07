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
 * SubClass for CommitRequest which extends the basic BrkXaRequest
 *  typedef struct BrkXaCommitRequest
    {
     uint32               	size_;           // size of the request structure
          ReqReplyFlag         	request_;      // BRK_GW_REQUEST
          ReqCode             	reqCode_;    // BRK_XA_COMMIT
          uint32               	filler_;         // must be 0
          XID                  	xid_;
          int32               	rmid_;
         int32                	flags_;
    } BrkXaCommitRequest;
    typedef struct BrkXaForgetRequest
    {
          uint32		size_;		// size of the request structure
          ReqReplyFlag        	request_;	// BRK_GW_REQUEST
          ReqCode              	reqCode_;	// BRK_XA_FORGET
          uint32               	filler_;	// must be 0
          XID                  	xid_;
          int32                	rmid_;
          int32                	flags_;
    } BrkXaForgetRequest;
    typedef struct BrkXaPrepareRequest
    {
          uint32               	size_;		// size of the request structure
          ReqReplyFlag         	request_;	// BRK_GW_REQUEST
          ReqCode              	reqCode_;	// BRK_XA_PREPARE
          uint32               	filler_;	// must be 0
          XID                  	xid_;
          int32                	rmid_;
          int32                	flags_;
     } BrkXaPrepareRequest;
    typedef struct BrkXaRollbackRequest
    {
          uint32               	size_;		// size of the request structure
          ReqReplyFlag         	request_; 	// BRK_GW_REQUEST
          ReqCode              	reqCode_;	// BRK_XA_ROLLBACK
          uint32               	filler_;	// must be 0
          XID                  	xid_;
          int32                	rmid_;
          int32                	flags_;
    } BrkXaRollbackRequest;

    typedef struct BrkXaEndRequest
    {
          uint32               	size_;		// size of the request structure
          ReqReplyFlag		request_;	// BRK_GW_REQUEST
          ReqCode		reqCode_;	// BRK_XA_END
          uint32               	filler_;		// must be 0
          XID                  	xid_;
          int32                	rmid_;
          int32                	flags_;
    } BrkXaEndRequest;
 */
package com.tandem.t4jdbc;

import java.util.Locale;
import java.sql.SQLException;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;


class BrkXaTxnRequest
    extends BrkXaRequest
{
  protected int m_flags;
  protected SQLMXXid m_xid;
  protected T4Properties m_t4props;
  protected Locale m_locale;
  protected int m_rmid = 0;

  BrkXaTxnRequest(T4Properties t4props,
                  int p_ReqCode, int p_rmid, Xid p_xid,
                  int p_flags) throws SQLException, XAException
  {
    super(t4props, p_ReqCode);
    m_t4props = t4props;
    m_locale = t4props.getLocale();
    checkReqCode(p_ReqCode);
    m_xid = new SQLMXXid(m_t4props, p_xid);
    setRmid(p_rmid);
    m_flags = p_flags;
  }

  private void setRmid(int p_rmid)
  {
    m_rmid = p_rmid;
  }

  /**
   * validate the incoming XA request is valid.
   * @param p_ReqCode is XA request code defined in the XABROKER.
   */
  private void checkReqCode(int p_ReqCode) throws SQLException
  {
    switch (p_ReqCode)
    {
      case BrkXaReply.BRK_XA_COMMIT:
      case BrkXaReply.BRK_XA_FORGET:
      case BrkXaReply.BRK_XA_PREPARE:
      case BrkXaReply.BRK_XA_ROLLBACK:
      case BrkXaReply.BRK_XA_START:
      case BrkXaReply.BRK_XA_END:
        break;
      default:
        SQLException sqx1 = SQLMXMessages.createSQLException(m_t4props, m_locale,
            "internal_error", String.valueOf(p_ReqCode));
        SQLException sqx2 = SQLMXMessages.createSQLException(m_t4props, m_locale,
            "contact_hp_error", String.valueOf(p_ReqCode));
        sqx1.setNextException(sqx2);
        throw sqx1;
    }

  }


  int totalSizeOf()
  {
    int size = 0;
    size = super.totalSizeOf();
    size += m_xid.sizeof();
    size += 8; // rmid_ + flags_
    return size;
  }


  //----------------------------------------------------------
  /** This method will serialize this object to a byte array.
   *
   * @param  p_buffer  A byte array from which this object is un-serialized.
   * @param  p_index   The offset into buffer1 at which to start un-serialization.
   * @param  p_locale  The locale information for printing the messages.
   *
   * @retrun  An offset in buffer of the first byte following the serialized object is returned
   *
   * @exception  SQLException are thrown
   */
//commenting this method since the values are inserted in BrkXaMessage.java itself - R30
//  int insertIntoByteArray(LogicalByteArray p_buffer
//                          , int p_index
//                          , Locale p_locale
//                          ) throws SQLException
//  {
//    p_index = p_buffer.insertInt(p_index, totalSizeOf());
//    p_index = p_buffer.insertInt(p_index, m_ReqReplyFlag);
//    p_index = p_buffer.insertInt(p_index, m_ReqCode);
//    p_index = p_buffer.insertInt(p_index, m_filler);
//    p_index = m_xid.insertIntoByteArray(p_buffer, p_index, p_locale);
//    p_index = p_buffer.insertInt(p_index, m_rmid);
//    p_index = p_buffer.insertInt(p_index, m_flags);
//    return p_index;
//  }
} // End of BrkXaTxnRequest

/**
 * XABROKER Structure for BrkXaStartRequest
typedef struct BrkXaStartRequest
{
      uint32               	size_;		// size of the request structure
      ReqReplyFlag         	request_;	// BRK_GW_REQUEST
      ReqCode              	reqCode_;	// BRK_XA_START
      uint32               	filler_;	// must be 0
      XID                  	xid_;
      int32                	rmid_;
      int32                	flags_;
      int32                     timeout_;
} BrkXaStartRequest;
*/
final class BrkXaStartRequest extends BrkXaTxnRequest
{
  // no timeout is default
  int m_timeout = 0;

  BrkXaStartRequest(T4Properties t4props,
                    int p_ReqCode, int p_rmid, Xid p_xid,
                    int p_flags, int p_timeout) throws SQLException, XAException
  {
    super(t4props, p_ReqCode, p_rmid, p_xid, p_flags);
    checkReqCode(p_ReqCode);
    m_timeout = p_timeout;
  }

  int totalSizeOf()
  {
    int size = 0;
    size = super.totalSizeOf();
    size += 4; // timeout
    return size;
  }

  /**
   * validate the incoming XA request is valid.
   * @param p_ReqCode
   */
  private void checkReqCode(int p_ReqCode) throws SQLException
  {
    switch (p_ReqCode)
    {
      case BrkXaReply.BRK_XA_START:
        break;
      default:
        SQLException sqx1 = SQLMXMessages.createSQLException(m_t4props, m_locale,
            "internal_error", String.valueOf(p_ReqCode));
        SQLException sqx2 = SQLMXMessages.createSQLException(m_t4props, m_locale,
            "contact_hp_error", String.valueOf(p_ReqCode));
        sqx1.setNextException(sqx2);
        throw sqx1;
    }

  }


//commenting this method since the values are inserted in BrkXaMessage.java itself - R30
//  int insertIntoByteArray(LogicalByteArray p_buffer
//                          , int p_index
//                          , Locale p_locale
//                          ) throws SQLException
//  {
//
//    p_index = p_buffer.insertInt(p_index, totalSizeOf());
//    p_index = p_buffer.insertInt(p_index, m_ReqReplyFlag);
//    p_index = p_buffer.insertInt(p_index, m_ReqCode);
//    p_index = p_buffer.insertInt(p_index, m_filler);
//    p_index = m_xid.insertIntoByteArray(p_buffer, p_index, p_locale);
//    p_index = p_buffer.insertInt(p_index, m_rmid);
//    p_index = p_buffer.insertInt(p_index, m_flags);
//    p_index = p_buffer.insertInt(p_index, m_timeout);
//    return p_index;
//  }

}