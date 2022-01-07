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
import javax.transaction.xa.XAException;
import java.util.Locale;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.util.logging.Level;

class T4XAExecute
    extends T4Connection {
  private Locale m_locale;
  private BrkXaMessage m_xaMessage;
  private final int rmid = 0;         // we are not using it at the moment
  private T4Properties m_t4props;
  private T4XALogger m_t4XALogger;
  private int m_timeout = 0;          // default - no timeout

  //since Neo has already defined the API constants with below values, 
  //Re-assinging the api values
//  static final int XA_BASE = 3027;
  static final int XA_BASE = 3032;
  static final int SRVR_API_XASTART = XA_BASE + 0;
  static final int SRVR_API_XAEND = XA_BASE + 1;
  static final int SRVR_API_XACOMMIT = XA_BASE + 2;
  static final int SRVR_API_XAFORGET = XA_BASE + 3;
  static final int SRVR_API_XAPREPARE = XA_BASE + 4;
  static final int SRVR_API_XARECOVER = XA_BASE + 5;
  static final int SRVR_API_XAROLLBACK = XA_BASE + 6;
  static final int SRVR_API_XAGET_TIMEOUT = XA_BASE + 7;
  static final int SRVR_API_XASET_TIMEOUT = XA_BASE + 8;
  static final int SRVR_API_XAGET_TRANSACTION_STATE = XA_BASE + 9;


  T4XAExecute(T4Properties t4props, SQLMXConnection p_sqlmxConnection) throws SQLException {

    super(p_sqlmxConnection.ic_);
    m_t4props = t4props;
    m_locale = t4props.getLocale();
    m_xaMessage = new BrkXaMessage(m_t4props,
                         ((SQLMXConnection)p_sqlmxConnection).getDialogueId());
    m_t4XALogger = new T4XALogger(p_sqlmxConnection);
  }

  private static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
    int l_errorCode = sqlEx.getErrorCode();
    String l_xaMessage = SQLMXXAResource.getXAStatusString(l_errorCode);
    if (l_xaMessage == null)
    {
        l_errorCode = XAException.XAER_RMERR;
    }

    XAException xAException = new XAException(l_errorCode);
    xAException.initCause(sqlEx);
    xAException.setStackTrace(sqlEx.getStackTrace());
    return xAException;
  }

  private LogicalByteArray getReadBuffer(int command, LogicalByteArray wbuffer) throws SQLException {
    return super.getReadBuffer((short)command, wbuffer);
  }

  /**
   XASTART
   Start work on behalf of a transaction branch specified in xid If TMJOIN
   is specified, the start is for joining a transaction previously seen by
   the resource manager. If TMRESUME is specified, the start is to resume
   a suspended transaction specified in the parameter xid. If neither
   TMJOIN nor TMRESUME is specified and the transaction specified by xid
   has previously been seen by the resource manager, the resource manager
   throws the XAException exception with XAER_DUPID error code.
   Parameters:
   p_xid - A global transaction identifier to be associated with the resource
   p_flags - One of none or TMJOIN, or TMRESUME
   Throws:
   XAException - An error has occurred. Possible exceptions are XA_RB*,
   XAER_RMERR, XAER_RMFAIL, XAER_DUPID, XAER_OUTSIDE, XAER_NOTA, XAER_INVAL,
   or XAER_PROTO.
   */
  BrkXaReply XASTART(SQLMXXid p_xid, int p_flags) throws XAException {

    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XASTART",
                       T4LoggingUtilities.makeParams(m_t4props, p_xid, p_flags));
    }
    try {
      BrkXaTxnRequest txnRequest = new BrkXaStartRequest(m_t4props,
          BrkXaReply.BRK_XA_START, rmid, p_xid, p_flags, m_timeout);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest,p_xid,rmid,p_flags,BrkXaReply.BRK_XA_START);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XASTART, wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if(m_t4XALogger.isLoggable(Level.SEVERE)){
        m_t4XALogger.log("Exception in XASTART = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XASTART",
                         T4LoggingUtilities.makeParams(m_t4props, p_xid, p_flags));
      }
      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  } // end XASTART

  /**
   XACOMMIT
   Commit the global transaction specified by xid.
   Parameters:
   p_xid - A global transaction identifier
   onePhase - If true, the resource manager should use a one-phase commit
   protocol to commit the work done on behalf of xid.
   Throws:
   XAException - An error has occurred. Possible XAExceptions are XA_HEURHAZ,
   XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL, XAER_NOTA,
   XAER_INVAL, or XAER_PROTO.
   If the resource manager did not commit the transaction and the paramether
   onePhase is set to true, the resource manager may throw one of the XA_RB*
   * exceptions. Upon return, the resource manager has rolled back the
   *  branch's work and has released all held resources.
   * */

  BrkXaReply XACOMMIT(SQLMXXid p_xid, boolean onePhase) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XACOMMIT",
                       T4LoggingUtilities.makeParams(m_t4props, p_xid, onePhase));
    }

    try {
      int flag = XAResource.TMNOFLAGS;

      // TODO way to handle onePhase optimization
      if (onePhase)
          flag = XAResource.TMONEPHASE;
      else
          flag = XAResource.XA_OK;

      BrkXaTxnRequest txnRequest = new BrkXaTxnRequest(m_t4props,
          BrkXaReply.BRK_XA_COMMIT, rmid, p_xid, flag);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, p_xid,rmid,flag,BrkXaReply.BRK_XA_COMMIT);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XACOMMIT,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if(m_t4XALogger.isLoggable(Level.SEVERE)){
        m_t4XALogger.log("Exception in XACOMMIT = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XACOMMIT",
                         T4LoggingUtilities.makeParams(m_t4props, p_xid, onePhase));
      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  } // end XACOMMIT

  /**
   XAEND
   Ends the work performed on behalf of a transaction branch. The resource
   manager disassociates the XA resource from the transaction branch specified
   and let the transaction be completed.
   If TMSUSPEND is specified in flags, the transaction branch is temporarily
   suspended in incomplete state. The transaction context is in suspened state
   and must be resumed via start with TMRESUME specified.
   If TMFAIL is specified, the portion of work has failed. The resource
   manager may mark the transaction as rollback-only
   If TMSUCCESS is specified, the portion of work has completed successfully.
   Parameters:
   p_xid - A global transaction identifier that is the same as what was used
   previously in the start method.
   p_flags - One of TMSUCCESS, TMFAIL, or TMSUSPEND
   Throws:
   XAException - An error has occurred. Possible XAException values are
   XAER_RMERR, XAER_RMFAILED, XAER_NOTA, XAER_INVAL, XAER_PROTO, or XA_RB*.
   */
  BrkXaReply XAEND(SQLMXXid p_xid, int p_flags) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XAEND",
                       T4LoggingUtilities.makeParams(m_t4props, p_xid, p_flags));
    }

    try {
      BrkXaTxnRequest txnRequest = new BrkXaTxnRequest(m_t4props,
          BrkXaReply.BRK_XA_END, rmid, p_xid, p_flags);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, p_xid,rmid,p_flags,BrkXaReply.BRK_XA_END);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XAEND, wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if(m_t4XALogger.isLoggable(Level.SEVERE)){
        m_t4XALogger.log("Exception in XAEND = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XAEND",
                         T4LoggingUtilities.makeParams(m_t4props, p_xid, p_flags));
      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  } // end XAEND

  /**
   XAPREPARE
   Ask the resource manager to prepare for a transaction commit of the
   transaction specified in xid.
   Parameters:
   p_xid - A global transaction identifier
   Returns:
   A value indicating the resource manager's vote on the outcome of the
   transaction. The possible values are: XA_RDONLY or XA_OK. If the resource
   manager wants to roll back the transaction, it should do so by raising an
   appropriate XAException in the prepare method.
   Throws:
   XAException - An error has occurred. Possible exception values are: XA_RB*,
   * XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  BrkXaReply XAPREPARE(SQLMXXid p_xid) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XAPREPARE",
                       T4LoggingUtilities.makeParams(m_t4props, p_xid));
    }

    try {
      BrkXaTxnRequest txnRequest = new BrkXaTxnRequest(m_t4props,
          BrkXaReply.BRK_XA_PREPARE, rmid, p_xid, XAResource.XA_OK);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, p_xid,rmid,XAResource.XA_OK,BrkXaReply.BRK_XA_PREPARE);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XAPREPARE,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if(m_t4XALogger.isLoggable(Level.SEVERE)){
        m_t4XALogger.log("Exception in XAPREPARE = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XAPREPARE",
                         T4LoggingUtilities.makeParams(m_t4props, p_xid));
      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  }

  /**
   XARECOVER
   Obtain a list of prepared transaction branches from a resource manager. The
   transaction manager calls this method during recovery to obtain the list of
   transaction branches that are currently in prepared or heuristically
   completed states.
   Parameters:
   p_flag - One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS. TMNOFLAGS must be used
   when no other flags are set in flags.
   Returns:
   The resource manager returns zero or more XIDs for the transaction branches
   that are currently in a prepared or heuristically completed state. If an
   error occurs during the operation, the resource manager should throw the
   appropriate XAException.
   Throws:
   XAException - An error has occurred. Possible values are XAER_RMERR,
   XAER_RMFAIL, XAER_INVAL, and XAER_PROTO.
   */
  BrkXaReply XARECOVER(int p_flag) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XARECOVER",
                       T4LoggingUtilities.makeParams(m_t4props, p_flag));
    }

    try {
      int count = 0; // Not used by XABroker

      BrkXaRecoverRequest txnRequest = new BrkXaRecoverRequest(m_t4props,
          count,
          rmid,
          p_flag);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, null,rmid,p_flag,BrkXaReply.BRK_XA_RECOVER);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XARECOVER,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if(m_t4XALogger.isLoggable(Level.SEVERE)){
        m_t4XALogger.log("Exception in XARECOVER = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XARECOVER",
                         T4LoggingUtilities.makeParams(m_t4props, p_flag));

      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch

  }

  /**
   XAROLLBACK
   Inform the resource manager to roll back work done on behalf of a
   transaction branch.
   Parameters:
   p_xid - A global transaction identifier
   Throws:
   XAException - An error has occurred
   */
  BrkXaReply XAROLLBACK(SQLMXXid p_xid) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XAROLLBACK",
                       T4LoggingUtilities.makeParams(m_t4props, p_xid));
    }

    try {
      BrkXaTxnRequest txnRequest = new BrkXaTxnRequest(m_t4props,
          BrkXaReply.BRK_XA_ROLLBACK, rmid, p_xid, XAResource.XA_OK);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, p_xid,rmid,XAResource.XA_OK,BrkXaReply.BRK_XA_ROLLBACK);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XAROLLBACK,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if(m_t4XALogger.isLoggable(Level.SEVERE)){
        m_t4XALogger.log("Exception in XAROLLBACK = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XAROLLBACK",
                         T4LoggingUtilities.makeParams(m_t4props, p_xid));
      }
      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  }

  /**
   XAFORGET
   Tell the resource manager to forget about a heuristically completed
   transaction branch.
   Parameters:
   p_xid - A global transaction identifier
   Throws:
   XAException - An error has occurred. Possible exception values are
   XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  BrkXaReply XAFORGET(SQLMXXid p_xid) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XAFORGET",
                       T4LoggingUtilities.makeParams(m_t4props, p_xid));
    }

    try {
      BrkXaTxnRequest txnRequest = new BrkXaTxnRequest(m_t4props,
          BrkXaReply.BRK_XA_FORGET, rmid, p_xid, XAResource.TMNOFLAGS);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, p_xid,rmid,XAResource.TMNOFLAGS,BrkXaReply.BRK_XA_FORGET);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XAFORGET,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if (m_t4XALogger.isLoggable(Level.SEVERE))
      {
        m_t4XALogger.log("Exception in XAFORGET = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XAFORGET",
                         T4LoggingUtilities.makeParams(m_t4props, p_xid));
      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  }

  /**
   XAGET_TIMEOUT
   Obtain the current transaction timeout value set for this XAResource
   instance. If XAResource.setTransactionTimeout was not use prior to invoking
   this method, the return value is the default timeout set for the resource
   manager; otherwise, the value used in the previous setTransactionTimeout
   call is returned.
   Returns:
   the transaction timeout value in seconds.
   Throws:
   XAException - An error has occurred. Possible exception values are
   XAER_RMERR, XAER_RMFAIL.
   */
  BrkXaReply XAGET_TIMEOUT() throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XAGET_TIMEOUT",
                       T4LoggingUtilities.makeParams(m_t4props));
    }

    try {
      BrkXaTimeoutRequest txnRequest = new BrkXaTimeoutRequest(m_t4props, rmid);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, null,rmid,XAResource.TMNOFLAGS,BrkXaReply.BRK_XA_GET_TIMEOUT);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XAGET_TIMEOUT,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if (m_t4XALogger.isLoggable(Level.SEVERE))
      {
        m_t4XALogger.log("Exception in XAGET_TIMEOUT = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XAGET_TIMEOUT",
                         T4LoggingUtilities.makeParams(m_t4props));

      }
      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  } // end XAGET_TIMEOUT

  /**
   XASET_TIMEOUT
   Set the current transaction timeout value for this XAResource instance. Once
   set, this timeout value is effective until setTransactionTimeout is invoked
   again with a different value. To reset the timeout value to the default
   value used by the resource manager, set the value to zero. If the timeout
   operation is performed successfully, the method returns true; otherwise false.
   Parameters:
   seconds - the transaction timeout value in seconds.
   Returns:
   true if transaction timeout value is set successfully; otherwise false.
   Throws:
   XAException - An error has occurred. Possible exception values are
   XAER_RMERR, XAER_RMFAIL, or XAER_INVAL.
   */
  boolean XASET_TIMEOUT(int seconds) throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                       Level.FINER,
                       "T4XAExecute",
                       "XASET_TIMEOUT",
                       T4LoggingUtilities.makeParams(m_t4props, this, seconds));
    }

    m_timeout = seconds;
    /*
    try {
      BrkXaTimeoutRequest txnRequest = new BrkXaTimeoutRequest(m_t4props,
          rmid, seconds);

      MessageBuffer wbuffer = m_xaMessage.marshal(txnRequest);
      MessageBuffer rbuffer = getReadBuffer(SRVR_API_XASET_TIMEOUT,
                                            wbuffer);


      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer.getBuffer(), m_t4props);
    }
    catch (SQLException sqlex) {
      if (m_t4XALogger.isLoggable(Level.SEVERE))
      {
        m_t4XALogger.log("Exception in XASET_TIMEOUT = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XASET_TIMEOUT",
                         T4LoggingUtilities.makeParams(m_t4props, XID_def.toString(p_xid), seconds));
      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
    */
    return true;
  } // end XASET_TIMEOUT

  /**
   XAGET_TRANSACTION_STATE
   This message is not required at the moment for the Type4. This method could
   be included in the future for tracing purpose.
   */
  BrkXaReply XAGET_TXN_STATE() throws XAException {
    if(m_t4XALogger.isLoggable(Level.FINER)){
      m_t4XALogger.log("",
                  Level.FINER,
                  "T4XAExecute",
                  "XAGET_TXN_STATE",
                  T4LoggingUtilities.makeParams(m_t4props));
    }

    try {
      BrkXaRequest txnRequest = new BrkXaRequest(m_t4props);

      LogicalByteArray wbuffer = m_xaMessage.marshal(txnRequest, null,0,XAResource.TMNOFLAGS,BrkXaReply.BRK_XA_GET_TRANSACTION_STATE);
      LogicalByteArray rbuffer = getReadBuffer(SRVR_API_XAGET_TRANSACTION_STATE,
                                            wbuffer);

      //
      // Process output parameters
      //
      return new BrkXaReply(rbuffer, m_t4props);
    }
    catch (SQLException sqlex) {
      if (m_t4XALogger.isLoggable(Level.SEVERE))
      {
        m_t4XALogger.log("Exception in GET_TXN_STATE = " + sqlex.getMessage(),
                         Level.SEVERE,
                         "T4XAExecute",
                         "XAGET_TXN_STATE",
                         T4LoggingUtilities.makeParams(m_t4props));
      }

      throw mapXAExceptionFromSQLException(sqlex);
    } // end catch
  } // end XAGET_TXN_STAT

}