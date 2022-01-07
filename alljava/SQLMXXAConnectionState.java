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
import java.util.*;
import javax.transaction.xa.*;


class SQLMXXAConnectionState
{

  //keeps a map of the XID to the state details
  private Hashtable xid_state_table;
  private Hashtable resource_state_table;

  protected static final int xa_no_state_change_ = 0;
  protected static final int xa_send_message_to_broker_ = -1;
  protected static final int xa_state_roll_back_ = 1000;
  protected static final int xa_state_commit_ = 1001;
  protected static final int xa_state_prepare_ = 1002;
  protected static final int xa_state_start_ = 1003;
  protected static final int xa_state_end_ = 1004;
  protected static final int xa_state_forget_ = 1005;
  protected static final int xa_tm_suspend = XAResource.TMSUSPEND;

  static final Integer XA_STATE_ROLL_BACK = new Integer(xa_state_roll_back_);
  static final Integer XA_STATE_COMMIT = new Integer(xa_state_commit_);
  static final Integer XA_STATE_PREPARE = new Integer(xa_state_prepare_);
  static final Integer XA_STATE_START = new Integer(xa_state_start_);
  static final Integer XA_STATE_END = new Integer(xa_state_end_);
  static final Integer XA_STATE_FORGET = new Integer(xa_state_forget_);
  static final Integer XA_NO_STATE_CHANGE = new Integer(xa_no_state_change_);
  static final Integer XA_SEND_MESSAGE_TO_BROKER = new Integer(xa_send_message_to_broker_);

  static final Integer XAER_PROTOCOL_ERROR = new Integer(XAException.XAER_PROTO);
  static final Integer XA_READ_ONLY = new Integer(XAException.XA_RDONLY);
  static final Integer XA_STATE_SUSPEND = new Integer(xa_tm_suspend);

  SQLMXXAConnectionState()
  {
    xid_state_table = new Hashtable();
    resource_state_table = new Hashtable();
  }

  private void throwResourceTableError(SQLMXXAResource p_res, XAException xaex) throws XAException
  {
    // remover the table entry
    resource_state_table.remove(getKey(p_res));
    XAException xaex1 = new XAException("State error with the resource table entry.");
    xaex1.errorCode = XAException.XAER_RMERR;
    xaex1.setStackTrace(xaex.getStackTrace());
    throw xaex1;
  }

  private void throwXidTableError(SQLMXXid p_xid, XAException xaex) throws XAException
  {
    // remover the table entry
    xid_state_table.remove(p_xid);
    XAException xaex1 = new XAException("State error with the xid table entry.");
    xaex1.errorCode = XAException.XAER_RMERR;
    xaex1.setStackTrace(xaex.getStackTrace());
    throw xaex1;
  }

  // For Start look at res table first.
  // followed by xid table.
  XAResourceState start(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState resNextState = XAResourceState.startState;
    XAResourceState xidNextState = XAResourceState.startState;
    SQLMXXAResource l_res = xa_param.getXAResource();
    XAResourceState l_currentState = null;
    SQLMXXid l_xid = xa_param.getXIDdef();
    XAResourceState l_state = XAResourceState.startState;

    try {
      if (l_res != null)
      {
        l_currentState = getResStateFromXAParam(l_res);
        if (l_currentState != null)
        {
          resNextState = l_currentState.start(null, xa_param.getXAFlags());
        }
      }
    } catch (XAException xaex) {
         throwResourceTableError(l_res, xaex);
    }

    try
    {
      if (l_xid != null)
      {
        l_currentState = getXidStateFromXAParam(l_xid);
        if (l_currentState != null)
        {
          xidNextState = l_currentState.start(l_xid, xa_param.getXAFlags());
        }
      }
    }
    catch (XAException xaex)
    {
      throwXidTableError(l_xid, xaex);
    }


    if (resNextState == null)
    {
      l_state = XAResourceState.startState;
    }
    else if ( (resNextState instanceof StateStart) &&
             (xidNextState instanceof StateJoin))
    {
      l_state = XAResourceState.joinState;
    }
    else if (resNextState instanceof StateReadOnly) // this ugly for WLS.
    {
      l_state = XAResourceState.readOnlyState;
    } else {
      if (resNextState != null)
      {
        l_state = resNextState;
      } else if (xidNextState == null) // unreachable code. TODO: please verify.
      {
        l_state = xidNextState;
      }
    }

    return l_state;
  }


  synchronized void setStart(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState l_state = start(xa_param);
    xa_param.setAction(XAResourceState.startState);
    resource_state_table.put(getKey(xa_param.getXAResource()), xa_param);
    xid_state_table.put(xa_param.getXIDdef(), xa_param);
  }


  // For end look at res table only.
  XAResourceState end(XAInputOutputStateParameter xa_param) throws XAException
  {
    SQLMXXAResource l_res = xa_param.getXAResource();
    XAResourceState resNextState = null;
    XAResourceState l_currentState = null;

    try
    {
      l_currentState = getResStateFromXAParam(l_res);
      if (l_currentState == null)
      {
        throw XAResourceState.noTxException;
      }
      resNextState = l_currentState.end(null);
    }
    catch (XAException xaex)
    {
      throwResourceTableError(l_res, xaex);
    }

    return resNextState;
  }


  synchronized void setEnd(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState l_state = end(xa_param);
    resource_state_table.remove(getKey(xa_param.getXAResource()));
    xa_param.setAction(l_state);
    xid_state_table.put(xa_param.getXIDdef(), xa_param);
  }

  // prepare looks at only the xid table.
  // we also check if res is in good state as well.
  XAResourceState prepare(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState resNextState = XAResourceState.prepareState;
    XAResourceState xidNextState = XAResourceState.prepareState;
    SQLMXXAResource l_res = xa_param.getXAResource();
    SQLMXXid l_xid = xa_param.getXIDdef();

    try
    {
      XAResourceState l_currentState = getXidStateFromXAParam(l_xid);
      if (l_currentState != null)
      {
        xidNextState = l_currentState.prepare(l_xid);
      }

      l_currentState = getResStateFromXAParam(l_res);
      if (l_currentState != null)
      {
        resNextState = l_currentState.prepare(l_xid);
      }

    }
    catch (XAException xaex)
    {
      throwXidTableError(l_xid, xaex);
    }

    return xidNextState;
  }


  synchronized void setPrepare(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState l_state = prepare(xa_param);
    xa_param.setAction(l_state);
    resource_state_table.remove(getKey(xa_param.getXAResource()));
    xid_state_table.put(xa_param.getXIDdef(), xa_param);
  }


  // check resource and xid are in a state to commit
  XAResourceState commit(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState resNextState = XAResourceState.commitState;
    XAResourceState xidNextState = XAResourceState.commitState;
    SQLMXXAResource l_res = xa_param.getXAResource();
    SQLMXXid l_xid = xa_param.getXIDdef();

    try
    {
      XAResourceState l_currentState = getXidStateFromXAParam(l_xid);
      if (l_currentState != null)
      {
        xidNextState = l_currentState.commit(l_xid);
      }

      l_currentState = getResStateFromXAParam(l_res);
      if (l_currentState != null)
      {
        resNextState = l_currentState.commit(l_xid);
      }
    }
    catch (XAException xaex)
    {
      throwXidTableError(l_xid, xaex);
    }

    return xidNextState;
  }


  // Add a comment that xid_table is not cleaned up. TODO: Find a way to create
  // the hashtable with a max-size and reuses the space to avoid a growing xid_table.
  synchronized void setCommit(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState l_state = commit(xa_param);
    resource_state_table.remove(getKey(xa_param.getXAResource()));
    /*Solution  10-150602-6624, clearing the XID table to fix the memory leak issue. */
    xid_state_table.remove(xa_param.getXIDdef()); 
  }

  // check resource and xid are in a state to rollback
  XAResourceState rollback(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState resNextState = XAResourceState.rollbackState;
    SQLMXXid l_xid = xa_param.getXIDdef();
    SQLMXXAResource l_res = xa_param.getXAResource();
    XAResourceState l_currentState = null;

    try
    {
      l_currentState = getXidStateFromXAParam(l_xid);
      if (l_currentState != null)
      {
        resNextState = l_currentState.rollback(l_xid);
      }
    }
    catch (XAException xaex)
    {
      throwXidTableError(l_xid, xaex);
    }

    try
    {
      if (resNextState instanceof StateRollback)
      {
        // check if resource is ok to rollback.
        l_currentState = getResStateFromXAParam(l_res);
        if (l_currentState != null)
        {
          resNextState = l_currentState.rollback(null);
        }
      }
    } catch (XAException xaex) {
       throwResourceTableError(l_res, xaex);
    }

    return resNextState;
  }


  synchronized void setRollback(XAInputOutputStateParameter xa_param) throws XAException
  {
    XAResourceState l_state = rollback(xa_param);
    resource_state_table.remove(getKey(xa_param.getXAResource()));
    xa_param.setAction(l_state);
    /*Solution  10-150602-6624, clearing the XID table to fix the memory leak issue. */
    xid_state_table.remove(xa_param.getXIDdef());
  }



  synchronized void removeState(XAInputOutputStateParameter xa_param) throws XAException
  {
    resource_state_table.remove(getKey(xa_param.getXAResource()));
    xid_state_table.remove(xa_param.getXIDdef());
  }

  /**
   * Return the XAResourceState from the XAInputOutputStateParamete
   * @param SQLMXXAResource
   * @return XAResourceState
   */
  private XAResourceState getResStateFromXAParam(SQLMXXAResource p_res)
  {
    XAResourceState l_currentState = null;
    XAInputOutputStateParameter l_xa_param = (XAInputOutputStateParameter) resource_state_table.get(getKey(p_res));
    if (l_xa_param != null)
    {
      l_currentState = l_xa_param.getState();
    }
    return l_currentState;
  }


  /**
   * Return the XAResourceState from the XAInputOutputStateParamete
   * @param SQLMXXid
   * @return XAResourceState
   */
  private XAResourceState getXidStateFromXAParam(SQLMXXid p_xid)
  {
    XAResourceState l_currentState = null;
    XAInputOutputStateParameter l_xa_param = (XAInputOutputStateParameter) xid_state_table.get(p_xid);
    if (l_xa_param != null)
    {
      l_currentState = l_xa_param.getState();
    }
    return l_currentState;
  }

  /**
   * Method for creating the key. Making it a method will help the changes
   * in the key simple and in one place.
   * @param p_res
   * @return Object value of the key.
   */
  private Object getKey(SQLMXXAResource p_res)
  {
    return p_res.m_xaConnection;
  }
}