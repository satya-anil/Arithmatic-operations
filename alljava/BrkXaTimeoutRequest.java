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
  typedef struct BrkXaGetTimeoutRequest
  {
           uint32               	size_;		// size of the request structure
           ReqReplyFlag		        request_;	// BRK_GW_REQUEST
           ReqCode		        reqCode_;	// BRK_XA_GET_TIMEOUT
           uint32               	filler_;	// must be 0
           int32                	rmid_;
  } BrkXaGetTimeoutRequest;

 typedef struct BrkXaSetTimeoutRequest
 {
          uint32               	size_;		// size of the request structure
          ReqReplyFlag		request_;	// BRK_GW_REQUEST
          ReqCode		reqCode_;	// BRK_XA_SET_TIMEOUT
          uint32               	filler_;	// must be 0
          int32                	rmid_;
          int32                	timeout_;
 } BrkXaSetTimeoutRequest;
*/
package com.tandem.t4jdbc;

import java.sql.SQLException;
import java.util.Locale;

final class BrkXaTimeoutRequest
    extends BrkXaRequest {

  private int m_timeout;
  private int m_rmid = 0;

  private void setRmid(int p_rmid)
  {
    m_rmid = p_rmid;
  }

  /**
   * This constructor is for GET_TIMEOUT request.
   * @param p_t4props is a T4Properties object
   * @param p_rmid is the RMID value
   * @throws SQLException
   */
  BrkXaTimeoutRequest(T4Properties p_t4props, int p_rmid) throws SQLException {
    super(p_t4props, BrkXaReply.BRK_XA_GET_TIMEOUT);
    setRmid(p_rmid);
  }

  /**
   * This contructor is for SET_TIMEOUT request.
   * @param p_t4props is a T4Properties object
   * @param p_rmid is the RMID value
   * @param p_timeout is the the timeout value in seconds.
   * @throws SQLException
   */
  BrkXaTimeoutRequest(T4Properties p_t4props, int p_rmid, int p_timeout
                     ) throws SQLException {
    super(p_t4props, BrkXaReply.BRK_XA_SET_TIMEOUT);
    setRmid(p_rmid);
    m_timeout = p_timeout;
  }

  final int totalSizeOf() {
    int size = 0;
    size = super.totalSizeOf();
    size += 4; // rmid_

    /*
     * SetTimeout has 4 bytes more for timeout parameter
     */
    if (m_ReqCode == BrkXaReply.BRK_XA_SET_TIMEOUT)
        size += 4; // timeout

    return size;
  }

  //----------------------------------------------------------
  /** This method will serialize this object to a byte array.
   *
   * @param  p_buffer  A byte array from which this object is un-serialized.
   * @param  p_index   The offset into buffer1 at which to start un-serialization.
   * @param  p_locale  The locale information for the error messages.
   *
   * @retrun  An offset in buffer of the first byte following the serialized
   * object is returned
   *
   * @exception  SQLExceptions  are thrown
   */
	//commenting this method since the values are inserted in BrkXaMessage.java itself - R30
//  int insertIntoByteArray(LogicalByteArray p_buffer
//                          , int p_index
//                          , Locale p_locale
//                          ) throws SQLException {
//    p_index = p_buffer.insertInt(p_index, totalSizeOf());
//    p_index = p_buffer.insertInt(p_index, m_ReqReplyFlag);
//    p_index = p_buffer.insertInt(p_index, m_ReqCode);
//    p_index = p_buffer.insertInt(p_index, m_filler);
//    p_index = p_buffer.insertInt(p_index, m_rmid);
//
//    if (m_ReqCode == BrkXaReply.BRK_XA_SET_TIMEOUT)
//        p_index = p_buffer.insertInt(p_index, m_timeout);
//    return p_index;
//  }
} // End of BrkXaTimeoutRequest