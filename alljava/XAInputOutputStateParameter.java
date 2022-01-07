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

class XAInputOutputStateParameter{
  private SQLMXXid xid_;
  private XAResourceState state_;
  private int xa_flags_;
  private SQLMXXAResource xares_;

  XAInputOutputStateParameter(SQLMXXid xid, SQLMXXAResource XAres_, XAResourceState action, int xa_flags)
  {
    xid_ = xid;
    xares_ = XAres_;
    state_ = action;
    xa_flags_ = xa_flags;
  }

  SQLMXXAResource getXAResource()
  {
    return xares_;
  }
  SQLMXXid getXIDdef()
  {
    return xid_;
  }
  public int hashCode()
  {
    return getXAConnHashCode();
  }
  int getXAConnHashCode()
  {
    if(xares_ == null)
      return -1;
    else
      return xares_.hashCode();
  }
  XAResourceState  getState()
  {
    return state_;
  }
  void setAction(XAResourceState p_action)
  {
    state_ = p_action;
  }
  int getXAFlags()
  {
    return xa_flags_;
  }
  void setXAFlags(int p_xa_flags)
  {
    xa_flags_ = p_xa_flags;
  }
}