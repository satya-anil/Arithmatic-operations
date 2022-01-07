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

import java.util.Locale;
import java.sql.SQLException;

/*
 * This exception handles all the following exceptions.
 * Please refer to XABROKER XAClient_common.h file
 * for the definition of these structures.
 * 1.	BrkXaStart_exc_
 * 2.	BrkXaCommit_exc_
 * 3.	BrkXaEnd_exc_
 * 4.	BrkXaPrepare_exc_
 * 5.	BrkXaRecover_exc_
 * 6.	BrkXaRollback_exc_
 * 7.	BrkXaForget_exc_
 * 8.	BrkXaGetTimeout_exc_
 * 9.	BrkXaSetTimeout_exc_
 *
 * The exception_exc union defined by MXCS is :
 * struct exception_exc_ {
 *           IDL_long exception_nr;
 *           IDL_long exception_detail;
 *           union {
 *                     struct ParamError ParamError;
 *                     struct BrokerError BrokerError;
 *                     struct BrokerWarning BrokerWarning;
 *           } u;
 * } Exception_exc_;
 *
 */

class BrkXa_exc_ {
  private int exception_nr;
  private int exception_detail;
  private int unionPtr;
  private String ParamError;
  private String BrokerError;
  private String BrokerWarning;
  private T4Properties m_t4props;
  private Locale m_locale;
  private LogicalByteArray m_buffer;
  private int m_hIndex = 0;
  private int base_index;
  private final int exception_nr_index = 0;
  private final int exception_detail_index = 4;
  private final int union_position_index = 8;

  /**
   * Success	        0  OK	                        None
   * ParamError	        1  Error in data provided	Parameter description error message
   * InvalidConnection	2  No Connection to Server	None
   * ProgramError	3  Error returned by MXCS 	Error message
   * BrokerError	4  Error returned by XA Broker	Error message
   * BrokerWarning	5  Warning returned by XA Broker Warning message
   */

  //
  // Exception number constants for operation 'BrkXa_exc_'
  //
  static final int BrkXa_ParamError_exn_ = 1;
  static final int BrkXa_InvalidConnection_exn_ = 2;
  static final int BrkXa_ProgramError_exn_ = 3;
  static final int BrkXa_BrokerError_exn_ = 4;
  static final int BrkXa_BrokerWarning_exn_ = 5;



  void setExeceptionNumber() {
    exception_nr = m_buffer.extractInt();
  }

  void setExeceptionDetail() {
    exception_detail = m_buffer.extractInt();
  }

  boolean checkSuccess() {
    return (exception_nr == TRANSPORT.CEE_SUCCESS);
  }

  void checkProgramException() throws SQLException {
    if ( (exception_nr == BrkXa_ParamError_exn_) ||
        (exception_nr == BrkXa_ProgramError_exn_)) {
    //for R3.0 
      m_buffer.setLocation(union_position_index + base_index);
      int error_index = m_buffer.extractInt();
      ParamError = new String(m_buffer.extractChars(error_index + m_hIndex,error_index));
      throw SQLMXMessages.createSQLException(m_t4props, m_locale,
                                             "ids_program_error",
                                             ParamError);
    }
  }

  void checkInvalidConnectionException() throws SQLException {
    if (exception_nr == BrkXa_InvalidConnection_exn_)
      throw SQLMXMessages.createSQLException(m_t4props, m_locale, "ids_08_s01", "");
  }

  void checkBrokerException() throws SQLException {
    if (exception_nr == BrkXa_BrokerError_exn_) {
    	m_buffer.setLocation(union_position_index + base_index);	
      int error_index = m_buffer.extractInt();   
      m_buffer.setLocation(error_index+m_hIndex);
      int len = 0;
      while (m_buffer.extractByte() != 0)
      {
        len = len + 1;  //to find the length of the error message and need to change logic for setLocation() -- senthil
      }
      m_buffer.setLocation(error_index+m_hIndex); 
    //  BrokerError = new String(m_buffer.extractChars(error_index + m_hIndex,error_index));     
      BrokerError = new String(m_buffer.extractByteArray(len));
      throw SQLMXMessages.createSQLException(m_t4props, m_locale,
                                             "ids_xabroker_error",
                                             BrokerError);
    }

  }

  void checkBrokerWarning() throws SQLException {
    if (exception_nr == BrkXa_BrokerWarning_exn_) {
    	m_buffer.setLocation(union_position_index + base_index);	
      int error_index = m_buffer.extractInt();
      BrokerWarning = new String(m_buffer.extractChars(error_index + m_hIndex,error_index));
      Object[] messageArguments = {BrokerWarning};
      throw SQLMXMessages.createSQLWarning(m_t4props, "ids_xabroker_warning",
                                           messageArguments);
    }
  }

  //-------------------------------------------------------------------
  BrkXa_exc_(LogicalByteArray buffer1,
             int index1,
             int hIndex,
             T4Properties t4props) throws SQLException {
    m_buffer = buffer1;
    m_t4props = t4props;
    m_locale = t4props.getLocale();
    base_index = index1;
    m_hIndex = hIndex;

    setExeceptionNumber();
    if (checkSuccess())
        return;
    setExeceptionDetail();
    checkProgramException();
    checkInvalidConnectionException();
    checkBrokerException();
    checkBrokerWarning();

    throw SQLMXMessages.createSQLException(t4props, m_locale,
                                           "ids_unknown_reply_error",
                                           String.valueOf(exception_nr),
                                           String.valueOf(exception_detail));
  } // end BrkXa_exc_
} // end class BrkXa_exc_