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
    typedef struct BrkXaRecoverRequest
    {
          uint32               	size_;		//size of the request structure
          ReqReplyFlag         	request_;	// BRK_GW_REQUEST
          ReqCode              	reqCode_;	// BRK_XA_RECOVER
          uint32               	filler_;	// must be 0
          int32                	count_;		//NOTE: ignored by Gateway process
          int32                	rmid_;
         int32                	flags_;
    } BrkXaRecoverRequest;
 */
package com.tandem.t4jdbc;

import java.util.Locale;
import java.sql.SQLException;

final class BrkXaRecoverRequest
    extends BrkXaRequest {
  private int m_flags;
  private int m_count = 1;   // currently not being used.
  private int m_rmid = 0;    // currently not used.


  BrkXaRecoverRequest(T4Properties t4props, int p_count, int p_rmid,
                      int p_flags) throws SQLException {
    super(t4props, BrkXaReply.BRK_XA_RECOVER);
    setRmid(p_rmid);
    m_flags = p_flags;
    m_count = p_count;
  }

  private void setRmid(int p_rmid)
  {
    m_rmid = p_rmid;
  }

  final int totalSizeOf() {
    int size = super.totalSizeOf();
    size += 12; // count_ + rmid_ + flags_
    return size;
  }

  //----------------------------------------------------------
  /** This method will un-serialize this object from a byte array.
   *
   * @param  p_buffer  A byte array from which this object is un-serialized.
   * @param  p_index   The offset into buffer1 at which to start un-serialization.
   * @param  p_locale  The locale information for the error messages.
   *
   * @retrun  An offset in buffer of the first byte following the serialized object is returned
   *
   * @exception  SQLException are thrown
   */
  //commenting this method since the values are inserted in BrkXaMessage.java itself - R30
//  int insertIntoByteArray1(LogicalByteArray p_buffer
//                                 , int p_index
//                                 , Locale p_locale
//                                 ) throws SQLException {
//    p_index = p_buffer.insertInt(p_index, totalSizeOf());
//    p_index = p_buffer.insertInt(p_index, m_ReqReplyFlag);
//    p_index = p_buffer.insertInt(p_index, m_ReqCode);
//    p_index = p_buffer.insertInt(p_index, m_filler);
//    p_index = p_buffer.insertInt(p_index, m_count);
//    p_index = p_buffer.insertInt(p_index, m_rmid);
//    p_index = p_buffer.insertInt(p_index, m_flags);
//    return p_index;
//  }
} // End of BrkXaRecoverRequest