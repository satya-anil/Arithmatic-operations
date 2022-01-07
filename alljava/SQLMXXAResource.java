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

import javax.sql.XAConnection;
import java.sql.Connection;
import javax.transaction.xa.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.Vector;
import java.util.Arrays;

public class SQLMXXAResource implements XAResource {
  protected T4Properties m_t4props;
  String m_rmname;
  private BrkXaReply m_bxr_ = null;
  private int m_timeout = 0;

  private SQLMXConnection m_resource_connection;
  protected SQLMXXAConnection m_xaConnection;
  protected T4XALogger m_t4XALogger;
  protected T4XAExecute m_t4XAexe;

  static SQLMXXAConnectionState g_connection_state_obj;

  /**
     Retrieves a Type4 XAResource implementation object that the
     transaction manager will use to manage this SQLMXXAResource object's
     participation in a distributed transaction.
   */
  SQLMXXAResource(SQLMXXAConnection p_xaConnection,
                    T4Properties t4props) throws XAException, SQLException {
    m_xaConnection = p_xaConnection;
    m_resource_connection = p_xaConnection.m_resource_connection;
    m_t4XALogger = new T4XALogger(m_resource_connection);
    m_t4props = t4props;

    // NOTE: TODO serverDataSourceName could be removed from the m_rmname.
    // We could return isSameRM=true for the XABROKER.
    StringBuffer tmpName = new StringBuffer(t4props.getServerDataSource());
    tmpName = tmpName.append('-');
    tmpName = tmpName.append(t4props.getUrl());
    tmpName = tmpName.append(t4props.getUser());
    m_rmname = new String(tmpName);

    // Sync on the Runtime object since it is one per JVM.
    setStateObj();

    try
    {
      if (m_resource_connection == null || m_resource_connection.isClosed())
      {
        throw SQLMXXAMessages.createXAException(m_t4props, m_t4props.getLocale(), "xa_connection_closed", null);
      }
    }
    catch (SQLException se)
    {
      XAException xaex = new XAException("Error with connection to resource manager.");
      xaex.setStackTrace(se.getStackTrace());
      throw xaex;
    }

    m_t4XAexe = new T4XAExecute(m_t4props, m_resource_connection);

    m_t4XALogger.log("Acquired an XA resource.",
                     Level.FINE,
                     "SQLMXXAResource",
                     "Constructor",
                     T4LoggingUtilities.makeParams(m_t4props));
  }


  static private synchronized void setStateObj()
  {
    if (g_connection_state_obj == null)
    {
      g_connection_state_obj = new SQLMXXAConnectionState();
    }
  }

  /**
   prepare
   Asks the resource manager to prepare for a transaction commit  of the
   transaction specified in xid.
   Parameter: p_xid A global transaction identifier.
       Returns: A value indicating the resource manager's vote on the outcome of the
   transaction. The possible values are: XA_RDONLY or XA_OK. If the resource
   manager wants to roll back the transaction, it should do so by raising an
   appropriate XAException in the prepare method.
   Throws: XAException An error has occurred. Possible exception values
       are: XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  synchronized public int prepare(Xid p_xid) throws XAException {

    SQLMXXid l_nsk_xid = new SQLMXXid(m_t4props, p_xid);

    m_t4XALogger.log("Entering prepare",
                     Level.FINE,
                     "SQLMXXAResource",
                     "prepare",
                     T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));

    int l_xarm_retval = XAResource.XA_RDONLY;
    XAResourceState nextState = null;
    XAInputOutputStateParameter l_xaio = new XAInputOutputStateParameter(l_nsk_xid,
        this,
        XAResourceState.prepareState,
        -1);

    try {
      nextState = g_connection_state_obj.prepare(l_xaio);
    } catch (XAException xaex1) {
      m_t4XALogger.log("Cannot prepare XID = " + l_nsk_xid,
                       Level.SEVERE,
                       "SQLMXXAResource",
                       "prepare",
                       T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));

      g_connection_state_obj.removeState(l_xaio);
      XAException xaex = new XAException("Cannot prepare XID = " + l_nsk_xid);
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    l_xaio = new XAInputOutputStateParameter(l_nsk_xid,
                                       this,
                                       XAResourceState.readOnlyState,
                                       -1);

    if (nextState instanceof StateReadOnly) {
         g_connection_state_obj.setPrepare(l_xaio);
         l_xarm_retval = XAResource.XA_RDONLY;
    } else if (nextState instanceof StatePrepare) {
      m_bxr_ = m_t4XAexe.XAPREPARE(l_nsk_xid);
      l_xarm_retval = m_bxr_.getXARMretval();
      checkForXARMError(m_bxr_, l_xaio);
      g_connection_state_obj.setPrepare(l_xaio);
    }

    m_t4XALogger.log("Exiting prepare",
                     Level.FINE,
                     "SQLMXXAResource",
                     "prepare",
                     T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));

    return l_xarm_retval;
  }


  /**
   commit
   Commits the global transaction specified by xid.
   Parameter: p_xid, a global transaction identifier
   Parameter: onePhase - If true, the resource manager should use a one-phase
   commit protocol to commit the work done on behalf of xid.
   Throws: XAException An error has occurred. Possible XAExceptions are
   XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL,
   XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   If the resource manager did not commit the transaction and the parameter
   onePhase is set to true, the resource manager may throw one of the XA_RB*
   * exceptions. Upon return, the resource manager has rolled back the branch's
   * work and has released all held resources.
   */
  synchronized public void commit(Xid p_xid, boolean onePhase) throws
      XAException {

    SQLMXXid l_nsk_xid = new SQLMXXid(m_t4props, p_xid);

    m_t4XALogger.log("Entering commit",
                     Level.FINE,
                     "SQLMXXAResource",
                     "commit",
                     T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString(), onePhase));

    XAResourceState nextState = null;
    XAInputOutputStateParameter l_xaio = null;

    try {
      l_xaio = new XAInputOutputStateParameter(l_nsk_xid,
                      this, XAResourceState.commitState,-1);
      nextState = g_connection_state_obj.commit(l_xaio);
    } catch (XAException xaex1) {
      m_t4XALogger.log("Cannot commit XID = " + l_nsk_xid ,
                       Level.SEVERE,
                       "SQLMXXAResource",
                       "commit",
                       T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString(), onePhase));

      XAException xaex = new XAException("Cannot commit XID = " + l_nsk_xid.toString());
      xaex1.setStackTrace(xaex.getStackTrace());
      throw xaex1;
    }

    if ( nextState instanceof StateAlreadyCommitted ) {
          // do nothing. need to clean the global table here.
    } else if (nextState instanceof StateCommit) {
      m_bxr_ = m_t4XAexe.XACOMMIT(l_nsk_xid, onePhase);
      checkForXARMError(m_bxr_, l_xaio);
      g_connection_state_obj.setCommit(l_xaio);
    }

    m_t4XALogger.log("Commited XID = " + l_nsk_xid,
                     Level.FINE,
                     "SQLMXXAResource",
                     "commit",
                     T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString(), onePhase));
  }


  /**
   rollback
   Informs the resource manager XARM to roll back work done on behalf of a
   transaction branch.
   @parameter p_xid A global transaction identifier.
   @throws XAException An error has occurred. Possible XAExceptions are
   XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL,
   XAER_NOTA, XAER_INVAL, or XAER_PROTO.
       If the transaction branch is already marked rollback-only the resource manager
   may throw one of the XA_RB* exceptions.
       Upon return, the resource manager XARM has rolled back the branch's work and
   has released all held resources.
   */
  synchronized public void rollback(Xid p_xid) throws XAException {

    SQLMXXid l_nsk_xid = new SQLMXXid(m_t4props, p_xid);

    m_t4XALogger.log("Entering rollback",
                     Level.FINE,
                     "SQLMXXAResource",
                     "rollback",
                     T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));

    XAResourceState nextState;
    XAInputOutputStateParameter l_xaio = new XAInputOutputStateParameter(l_nsk_xid, this
          , XAResourceState.rollbackState, -1);

    try {
      nextState = g_connection_state_obj.rollback(l_xaio);
    } catch (XAException xaex1) {
      m_t4XALogger.log("Cannot rollback XID = " + l_nsk_xid.toString(),
                       Level.SEVERE,
                       "SQLMXXAResource",
                       "rollback",
                       T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));
      XAException xaex = new XAException("Cannot rollback XID = " + p_xid);
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    if (nextState instanceof StateAlreadyRolledback) {
      // do nothing. need to clean the global table here.
    } else if (nextState instanceof StateRollback) {
      m_bxr_ = m_t4XAexe.XAROLLBACK(l_nsk_xid);
      checkForXARMError(m_bxr_, l_xaio);
      g_connection_state_obj.setRollback(l_xaio);
    } else if (nextState instanceof StateEnd) {
      // NOTE: In case of unrecoverable SQL errors, MXCS should mark the
      // transaction setRollbackOnly() so that T4 can accept
      // the rollback from the TM directly with out expecting
      // xaend() call.
      // WLS does it. Not nice. Not cool as per XA spec.
      try {
        end(p_xid, XAResource.TMFAIL);
      } catch (XAException xaex) {
        // Ignore since this is a sledge hammer rollback.
        // TODO: Just ignore only XARM_NOTA assuming BROKER rolledback.
        m_t4XALogger.log("End for Rollback failed.",
                         Level.SEVERE,
                         "SQLMXXAResource",
                         "rollback",
                         T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));
      }

      m_bxr_ = m_t4XAexe.XAROLLBACK(l_nsk_xid);
      try {
        checkForXARMError(m_bxr_, l_xaio);
      } catch (XAException xaex) {
        // ignore since this is a sledge hammer rollback.
        if (xaex.errorCode != XAException.XAER_NOTA)
        {
          m_t4XALogger.log("Rollback failed.",
                           Level.SEVERE,
                           "SQLMXXAResource",
                           "rollback",
                           T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));
        }
        else
        {
            m_t4XALogger.log("Transaction already rolledback.",
                             Level.WARNING,
                             "SQLMXXAResource",
                             "rollback",
                             T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));

        }
      }
      g_connection_state_obj.setRollback(l_xaio);
    }

    m_t4XALogger.log("Exiting rollback.",
                     Level.FINE,
                     "SQLMXXAResource",
                     "rollback",
                     T4LoggingUtilities.makeParams(m_t4props, l_nsk_xid.toString()));

  }




  /**
   end
       Ends the work performed on behalf of a transaction branch. The resource manager
       XARM disassociates the XA resource from the transaction branch specified and
   lets the transaction complete.
       If TMSUSPEND is specified in the flags, the transaction branch is temporarily
       suspended in an incomplete state. The transaction context is in a suspended
   state and must be resumed via the start method with TMRESUME specified.
       If TMFAIL is specified, the portion of work has failed. The resource manager
   XARM may mark the transaction as rollback-only
   If TMSUCCESS is specified, the portion of work has completed successfully.
   Parameter: p_xid, A global transaction identifier that is the same as the
   identifier used previously in the start method.
   Parameter: flag, One of TMSUCCESS, TMFAIL, or TMSUSPEND.
   Throws: XAException - An error has occurred. Possible XAException values a
   re XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, XAER_PROTO, or XA_RB*
   */
  synchronized public void end(Xid p_xid, int flag) throws XAException {

    String s_xid = SQLMXXid.toString(p_xid);

    m_t4XALogger.log("Entering end",
                     Level.FINE,
                     "SQLMXXAResource",
                     "end",
                     T4LoggingUtilities.makeParams(m_t4props, s_xid, flag));

    SQLMXXid l_xid = new SQLMXXid(m_t4props, p_xid);
    m_bxr_ = null;
    XAResourceState nextState = null;
    XAInputOutputStateParameter l_xaio = new XAInputOutputStateParameter(l_xid, this
              , XAResourceState.endState, flag);

    switch (flag) {
      case TMSUSPEND: // do suspend need special care?
      case TMFAIL:
      case TMSUCCESS:
        try {
          nextState = g_connection_state_obj.end(l_xaio);
        } catch (XAException xaex1) {
          m_t4XALogger.log("Cannot end XID = " + l_xid.toString(),
                           Level.SEVERE,
                           "SQLMXXAResource",
                           "end",
                           T4LoggingUtilities.makeParams(m_t4props, l_xid.toString(), flag));

          XAException xaex = new XAException("Cannot end XID = " + l_xid.toString());
          xaex.setStackTrace(xaex1.getStackTrace());
          throw xaex;
        }

        if (nextState instanceof StateEnd)
        {
          m_bxr_ = m_t4XAexe.XAEND(l_xid, flag);
          g_connection_state_obj.setEnd(l_xaio);
          checkForXARMError(m_bxr_, l_xaio);
          m_xaConnection.reSetXA();

        }
        break;
      default:
        m_t4XALogger.log("XAException.XAER_INVAL error detected for XID = " + l_xid.toString(),
                         Level.FINE,
                         "SQLMXXAResource",
                         "end",
                         T4LoggingUtilities.makeParams(m_t4props, l_xid.toString(), flag));
        throw new XAException(XAException.XAER_INVAL);
    }

    m_t4XALogger.log("Exiting end",
                 Level.FINE,
                 "SQLMXXAResource",
                 "end",
                 T4LoggingUtilities.makeParams(m_t4props, s_xid, flag));

  }



  /**
   forget
   Tells the resource manager XARM to forget about a heuristically completed
   transaction branch.
   Parameter: p_xid A global transaction identifier.
   throws: XAException An error has occurred. Possible exception values are
   XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  public void forget(Xid p_xid) throws XAException {
    SQLMXXid l_xid = new SQLMXXid(m_t4props, p_xid);

    m_t4XALogger.log("Entering forget",
                     Level.FINE,
                     "SQLMXXAResource",
                     "forget",
                     T4LoggingUtilities.makeParams(m_t4props, l_xid.toString()));

    XAInputOutputStateParameter l_xaio = new XAInputOutputStateParameter(l_xid, this, XAResourceState.forgetState, -1);
    m_bxr_ = m_t4XAexe.XAFORGET(l_xid);
    checkForXARMError(m_bxr_, l_xaio);

    m_t4XALogger.log("Exiting forget",
                 Level.FINE,
                 "SQLMXXAResource",
                 "forget",
                 T4LoggingUtilities.makeParams(m_t4props, l_xid.toString()));

  }

  /**
   getTransactionTimeout
   Obtains the current transaction timeout value set for this
   XAResource instance. If XAResource.setTransactionTimeout
   was not used prior to invoking this method, the return value
   is the default timeout set for the resource manager; otherwise,
   the value used in the previous setTransactionTimeout  call is returned.
   return the transaction timeout value in seconds.
   throws XAException An error has occurred. Possible exception values are
   XAER_RMERR and XAER_RMFAIL.
   */
  public int getTransactionTimeout() throws XAException {

    m_t4XALogger.log("Entering getTransactionTimeout",
                     Level.FINE,
                     "SQLMXXAResource",
                     "getTransactionTimeout",
                     T4LoggingUtilities.makeParams(m_t4props));

    if (m_timeout == 0) {
      // return the timeout if it is locally available.
      m_bxr_ = m_t4XAexe.XAGET_TIMEOUT();
      m_timeout = m_bxr_.getXARMretval();
    }

    m_t4XALogger.log("Timeout is = " + m_timeout,
                       Level.FINE,
                       "SQLMXXAResource",
                       "getTransactionTimeout",
                       T4LoggingUtilities.makeParams(m_t4props));

    return m_timeout;
  }


  /**
   isSameRM
       This method is called to determine if the resource manager instance represented
       by the target object is the same as the resource manager instance represented
   by the parameter xares.
       Parameter: ‘xares’ An XAResource object whose resource manager instance is to be
   compared with the resource manager instance of the target object.
   Returns: true if it's the same RM instance; otherwise false.
   Throws: XAException An error has occurred. Possible exception values are
   XAER_RMERR and XAER_RMFAIL.
   */
  public boolean isSameRM(XAResource xares) throws XAException {
    m_t4XALogger.log("Entering isSameRM",
                     Level.FINE,
                     "SQLMXXAResource",
                     "isSameRM",
                     T4LoggingUtilities.makeParams(m_t4props, "this.rmname = " + m_rmname));

    boolean isSameRM = false;
    if ( (xares != null) && (xares instanceof SQLMXXAResource))
    {
      SQLMXXAResource sqlmxXAres = (SQLMXXAResource) xares;
      isSameRM = sqlmxXAres.m_rmname.equals(this.m_rmname);
    }

    m_t4XALogger.log("Exiting isSameRM",
                 Level.FINE,
                 "SQLMXXAResource",
                 "isSameRM",
                 T4LoggingUtilities.makeParams(m_t4props, isSameRM));

    return isSameRM;
  }


  public String toString() {
    return "Thread-hashcode = " + Thread.currentThread().getName() + "-" + hashCode();
  }


  /**
   recover
   Obtains a list of prepared transaction branches from a resource manager.
   The transaction manager calls this method during recovery to obtain the
       list of transaction branches that are currently in prepared or heuristically
   completed states.
   Param: flag One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS, TMNOFLAGS must be
   used when no other flags are set in the parameter.
   Returns: The resource manager returns zero or more XIDs of the transaction
       branches that are currently in a prepared or heuristically completed state.
   If an error occurs during the operation, the resource manager should throw
   the appropriate XAException.
   Throws: XAException An error has occurred. Possible values are XAER_RMERR,
   XAER_RMFAIL, XAER_INVAL, and XAER_PROTO.
   */
  public Xid[] recover(int flag) throws XAException {

    m_t4XALogger.log("Entering recover",
                     Level.FINE,
                     "SQLMXXAResource",
                     "recover",
                     T4LoggingUtilities.makeParams(m_t4props, flag));

     Xid[] l_xids = null;

    // TODO suport start scan and end scan. Currently end scan returns null list.
    switch (flag) {
      case XAResource.TMSTARTRSCAN:
      case XAResource.TMNOFLAGS:
      case (XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN):
        m_bxr_ = m_t4XAexe.XARECOVER(XAResource.TMSTARTRSCAN |
                                     XAResource.TMENDRSCAN);
        l_xids = m_bxr_.getXids();
        break;
      case XAResource.TMENDRSCAN:
        break;
      default:
        throw new XAException(XAException.XAER_INVAL);
    }

    m_t4XALogger.log("Exiting recover",
                 Level.FINE,
                 "SQLMXXAResource",
                 "recover",
                 T4LoggingUtilities.makeParams(m_t4props, flag));

     return l_xids;

  }



  /**
   setTransactionTimeout
       Sets the current transaction timeout value for this T4SQLMXXAResource instance.
   Once set, this timeout value is effective until setTransactionTimeout is
   invoked again with a different value.
       To reset the timeout value to the default value used by the resource manager,
   set the value to zero. If the timeout operation is performed successfully,
   the method returns true; otherwise false.
   If a resource manager does not support explicitly setting the transaction
   timeout value, this method returns false.
   Parameter: p_timeout, The transaction timeout value in seconds.
       Returns: true if the transaction timeout value is set successfully; otherwise
   false.
   Throws: XAException An error has occurred. Possible exception values
   are XAER_RMERR, XAER_RMFAIL, or XAER_INVAL.
   */
  public boolean setTransactionTimeout(int p_timeout) throws
      XAException {

    m_timeout = p_timeout;
    m_t4XALogger.log("Setting timeout",
                     Level.FINE,
                     "SQLMXXAResource",
                     "setTransactionTimeout",
                     T4LoggingUtilities.makeParams(m_t4props, p_timeout));

    boolean l_xarm_retval = m_t4XAexe.XASET_TIMEOUT(p_timeout);

    m_t4XALogger.log("Set timeout.",
                 Level.FINE,
                 "SQLMXXAResource",
                 "setTransactionTimeout",
                 T4LoggingUtilities.makeParams(m_t4props, p_timeout));

    return (l_xarm_retval);
  }


  /**
   start
   Starts work on behalf of a transaction branch specified in xid. If TMJOIN
       is specified, the start applies to joining a transaction previously seen by
   the resource manager. If TMRESUME is specified, the start applies to
   resuming a suspended transaction specified in the parameter xid.
   If neither TMJOIN nor TMRESUME is specified and the transaction specified
   by xid has previously been seen by the resource manager, the resource
   manager throws the XAException exception with XAER_DUPID error code.
   Parameter: p_xid, a global transaction identifier to be associated with the
   resource.
   Parameter: p_flags, One of TMNOFLAGS, TMJOIN, or TMRESUME.
       Throws: XAException An error has occurred. Possible exceptions are *XA_RB*,
   * XAER_RMERR, XAER_RMFAIL, XAER_DUPID, XAER_OUTSIDE, XAER_NOTA, XAER_INVAL,
   * or XAER_PROTO.
   * */
  synchronized public void start(Xid p_xid, int p_flags) throws XAException {

    m_t4XALogger.log("Starting " + toString(),
                     Level.FINE,
                     "SQLMXXAResource",
                     "start",
                     T4LoggingUtilities.makeParams(m_t4props, SQLMXXid.toString(p_xid), p_flags));


    SQLMXXid l_xid = new SQLMXXid(m_t4props, p_xid);
    XAResourceState nextState = null;

    switch (p_flags) {
      case TMRESUME:
      case TMNOFLAGS:
      case TMJOIN:
        try {
          nextState = g_connection_state_obj.start(new XAInputOutputStateParameter(l_xid, this, XAResourceState.startState, p_flags));
        } catch (XAException xaex) {
          m_t4XALogger.log("Cannot start XID = " + l_xid.toString(),
                           Level.SEVERE,
                           "SQLMXXAResource",
                           "start",
                           T4LoggingUtilities.makeParams(m_t4props, l_xid.toString(), p_flags));

          XAException xaex1 = new XAException("Cannot start XID = " + l_xid.toString());
          xaex1.setStackTrace(xaex.getStackTrace());
          throw xaex1;
        }

        if (nextState instanceof StateReadOnly) {
                 g_connection_state_obj.setStart(new XAInputOutputStateParameter(l_xid, this
                     , XAResourceState.startState, p_flags));
       } else if (nextState instanceof StateStart) {
          startTransaction(p_xid, p_flags);
          g_connection_state_obj.setStart(new XAInputOutputStateParameter(l_xid, this
              , XAResourceState.startState, p_flags));
        } else if (nextState instanceof StateJoin) {
          startTransaction(p_xid, XAResource.TMJOIN);
          g_connection_state_obj.setStart(new XAInputOutputStateParameter(l_xid, this
              , XAResourceState.startState, XAResource.TMJOIN));
        }
        break;
      default:
        throw new XAException(XAException.XAER_INVAL);
    }

    m_t4XALogger.log("Started " + toString(),
                       Level.FINE,
                       "SQLMXXAResource",
                       "start",
                       T4LoggingUtilities.makeParams(m_t4props, l_xid.toString(), p_flags));
  }



  // just code saver.
  protected void startTransaction(Xid p_xid, int p_flags) throws XAException {
    SQLMXXid l_xid = new SQLMXXid(m_t4props, p_xid);
    XAInputOutputStateParameter l_xaio = new XAInputOutputStateParameter(l_xid,
                  this, XAResourceState.startState, p_flags);
    m_bxr_ = m_t4XAexe.XASTART(l_xid, p_flags);
    checkForXARMError(m_bxr_, l_xaio);
  }

  //package only methods for testing.
  int getTxnState() throws XAException {
    m_bxr_ = m_t4XAexe.XAGET_TXN_STATE();
    int l_gw_status = m_bxr_.getBrkStatus();
    return l_gw_status;
  }

  // private methods
  static String getXAStatusString(int p_xarm_retval) {
    String xa_exp_string = null;

    switch (p_xarm_retval) {
      case XAResource.XA_OK:
      case XAException.XA_RDONLY: // The transaction branch was read-only and has been committed.
        break;
      case XAException.XA_HEURCOM: // The transaction branch has been heuristically committed.
        xa_exp_string =
            "XA_HEURCOM: The transaction branch has been heuristically committed.";
        break;
      case XAException.XA_HEURHAZ: // The transaction branch may have been heuristically completed.
        xa_exp_string =
            "XA_HEURHAZ: The transaction branch may have been heuristically completed.";
        break;
      case XAException.XA_HEURMIX: // The transaction branch has been heuristically committed and rolled back.
        xa_exp_string = "XA_HEURMIX: The transaction branch has been heuristically committed and rolled back.";
        break;
      case XAException.XA_HEURRB: // The transaction branch has been heuristically rolled back.
        xa_exp_string =
            "XA_HEURRB: The transaction branch has been heuristically rolled back.";
        break;
      case XAException.XA_NOMIGRATE: // Resumption must occur where the suspension occurred.
        xa_exp_string =
            "XA_NOMIGRATE: Resumption must occur where the suspension occurred.";
        break;
      case XAException.XA_RBBASE: // The inclusive lower bound of the rollback codes.
        xa_exp_string =
            "XA_RBBASE: The inclusive lower bound of the rollback codes.";
        break;
      case XAException.XA_RBCOMMFAIL: // Indicates that the rollback was caused by a communication failure.
        xa_exp_string = "XA_RBCOMMFAIL: Indicates that the rollback was caused by a communication failure.";
        break;
      case XAException.XA_RBDEADLOCK: //  A deadlock was detected.
        xa_exp_string = "XA_RBDEADLOCK: A deadlock was detected.";
        break;
      case XAException.XA_RBEND: // The inclusive upper bound of the rollback error code.
        xa_exp_string =
            "XA_RBEND: The inclusive upper bound of the rollback error code.";
        break;
      case XAException.XA_RBINTEGRITY: // A condition that violates the integrity of the resource was detected.
        xa_exp_string = "XA_RBINTEGRITY: A condition that violates the integrity of the resource was detected.";
        break;
      case XAException.XA_RBOTHER: // The resource manager rolled back the transaction branch for a reason not on this list.
        xa_exp_string = "XA_RBOTHER: The resource manager rolled back the transaction branch for a reason not on this list.";
        break;
      case XAException.XA_RBPROTO: // A protocol error occurred in the resource manager.
        xa_exp_string =
            "XA_RBPROTO: A protocol error occurred in the resource manager.";
        break;
        //case XAException.XA_RBROLLBACK: // Indicates that the rollback was caused by an unspecified reason.
      case XAException.XA_RBTIMEOUT: // A transaction branch took too long.
        xa_exp_string = "XA_RBTIMEOUT: A transaction branch took too long.";
        break;
        // case XAException.XA_RBTRANSIENT:    // May retry the transaction branch.
      case XAException.XA_RETRY: // Routine returned with no effect and may be reissued.
        xa_exp_string =
            "XA_RETRY: Routine returned with no effect and may be reissued.";
        break;
      case XAException.XAER_ASYNC: // There is an asynchronous operation already outstanding.
        xa_exp_string =
            "XAER_ASYNC: There is an asynchronous operation already outstanding.";
        break;
      case XAException.XAER_DUPID: // The XID already exists.
        xa_exp_string = "XAER_DUPID: The XID already exists.";
        break;
      case XAException.XAER_INVAL: // Invalid arguments were given.
        xa_exp_string = "XAER_INVAL: Invalid arguments were given.";
        break;
      case XAException.XAER_NOTA: // The XID is not valid.
        xa_exp_string = "XAER_NOTA: The XID is not valid";
        break;
      case XAException.XAER_OUTSIDE: // The resource manager is doing work outside a global transaction.
        xa_exp_string =
            "XAER_OUTSIDE: The resource manager is doing work outside a global transaction.";
        break;
      case XAException.XAER_PROTO: // Routine was invoked in an inproper context.
        xa_exp_string =
            "XAER_PROTO: Routine was invoked in an inproper context.";
        break;
      case XAException.XAER_RMERR: // A resource manager error has occurred in the transaction branch.
        xa_exp_string =
            "XAER_RMERR: NSK resource manager error has occurred in the transaction branch.";
        break;
      case XAException.XAER_RMFAIL: // Resource manager is unavailable.
        xa_exp_string = "XAER_RMFAIL: Resource manager is unavailable.";
        break;
      default:
        xa_exp_string = "Unknown value returned by the XARM";
        break;
    }

    return xa_exp_string;
  }

  protected void checkForXARMError(BrkXaReply p_bxr,
                                   XAInputOutputStateParameter p_xaio)
                                   throws XAException {
    String xa_exp_string = null;
    int l_xarm_retval = p_bxr.getXARMretval();

    xa_exp_string = getXAStatusString(l_xarm_retval);

    if (xa_exp_string != null) {
      xa_exp_string = xa_exp_string + ". rmval = " + l_xarm_retval;

      m_t4XALogger.log("XAException occured = " + xa_exp_string,
                       Level.SEVERE,
                       "SQLMXXAResource",
                       "checkForXARMError",
                       T4LoggingUtilities.makeParams(m_t4props, p_bxr));


      switch (l_xarm_retval) {
        case XAException.XA_HEURCOM: // The transaction branch has been heuristically committed.
        case XAException.XA_HEURHAZ: // The transaction branch may have been heuristically completed.
        case XAException.XA_HEURMIX: // The transaction branch has been heuristically committed and rolled back.
        case XAException.XA_HEURRB: // The transaction branch has been heuristically rolled back.
        case XAException.XA_RBBASE: // The inclusive lower bound of the rollback codes.
        case XAException.XA_RBCOMMFAIL: // Indicates that the rollback was caused by a communication failure.
        case XAException.XA_RBDEADLOCK: //  A deadlock was detected.
        case XAException.XA_RBEND: // The inclusive upper bound of the rollback error code.
        case XAException.XA_RBINTEGRITY: // A condition that violates the integrity of the resource was detected.
        case XAException.XA_RBOTHER: // The resource manager rolled back the transaction branch for a reason not on this list.
        case XAException.XA_RBTIMEOUT: // A transaction branch took too long.
        case XAException.XAER_NOTA: // The XID is not valid.
        case XAException.XAER_OUTSIDE: // The resource manager is doing work outside a global transaction.
     // case XAException.XAER_PROTO: // Routine was invoked in an inproper context.
        case XAException.XAER_RMERR: // A resource manager error has occurred in the transaction branch.
        case XAException.XAER_RMFAIL: // Resource manager is unavailable.
          g_connection_state_obj.removeState(p_xaio);
          break;
        default:
          // Error does not deserve state table clearing. Example: XAER_RETRY
          break;
      }

      XAException xaexp1 = new XAException(xa_exp_string);
      xaexp1.errorCode = l_xarm_retval;
      throw xaexp1;
    }
  }

}
