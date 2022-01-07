package com.tandem.t4jdbc;

import javax.transaction.xa.*;


/**
 * XAResourceState handles both resource state and xid state.
 * <p>Title: NonStop JDBC Type 4 Driver</p>
 * <p>Description: NonStop JDBC Type 4 Driver, Release 1.1</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Hewlett Packard</p>
 * @author not attributable
 * @version 1.1
 */

abstract class XAResourceState
{
  protected static final int xa_no_state_change_ = 0;
  protected static final int xa_send_message_to_broker_ = -1;
  protected static final int xa_state_rollback_ = 1000;
  protected static final int xa_state_commit_ = 1001;
  protected static final int xa_state_prepare_ = 1002;
  protected static final int xa_state_start_ = 1003;
  protected static final int xa_state_end_ = 1004;
  protected static final int xa_state_forget_ = 1005;
  protected static final int xa_state_already_rolledback_ = 1006;
  protected static final int xa_state_already_committed_ = 1007;
  protected static final int xa_state_readonly_ = 1008;
  protected static final int xa_state_new_ = 1009;
  protected static final int xa_state_join_ = 1010;
  protected static final int xa_state_already_prepared_ = 1001;

  static final XAResourceState startState = new StateStart();
  static final XAResourceState endState = new StateEnd();
  static final XAResourceState prepareState = new StatePrepare();
  static final XAResourceState commitState = new StateCommit();
  static final XAResourceState rollbackState = new StateRollback();
  static final XAResourceState alreadyRolledbackState = new StateAlreadyRolledback();
  static final XAResourceState alreadyCommittedState = new StateAlreadyCommitted();
  static final XAResourceState forgetState = new StateForget();
  static final XAResourceState readOnlyState = new StateReadOnly();
  static final XAResourceState joinState = new StateJoin();

  static XAException protocolException = new ProtocolException();
  static XAException noTxException = new NoTxException();

  protected int m_stateValue;

  // just to avoid direct instantiation, for future.
  protected XAResourceState(int l_stateValue)
  {
    m_stateValue = l_stateValue;
  }


  XAResourceState start(SQLMXXid p_xid, int p_startFlags) throws XAException
  {
    throw protocolException;
  }


  XAResourceState end(SQLMXXid p_xid) throws XAException
  {
    throw protocolException;
  }


  XAResourceState prepare(SQLMXXid p_xid) throws XAException
  {
    throw protocolException;
  }


  XAResourceState commit(SQLMXXid p_xid) throws XAException
  {
    throw protocolException;
  }


  XAResourceState rollback(SQLMXXid p_xid) throws XAException
  {
    throw protocolException;
  }


  XAResourceState forget(SQLMXXid p_xid) throws XAException
  {
    throw protocolException;
  }


  int getAction()
  {
    return m_stateValue;
  }
}

class ProtocolException extends XAException
{
   ProtocolException()
   {
        super("XA Protocol Error.");
        super.errorCode = XAER_PROTO;
   }
}

 class NoTxException extends XAException
 {
    NoTxException()
    {
         super("No XA Transaction Error.");
         super.errorCode = XAER_NOTA;
    }
 }

class StateStart
    extends XAResourceState
{
  StateStart()
  {
    super(xa_state_start_);
  }


  XAResourceState start(SQLMXXid p_xid, int p_startFlags) throws XAException
  {
    XAResourceState l_state = null;
    switch (p_startFlags)
    {
      case XAResource.TMNOFLAGS:
        if (p_xid != null) {
          l_state = joinState;
        } else {
          throw protocolException;
        }
        break;

      case XAResource.TMJOIN:
        if (p_xid != null) {
          l_state = joinState;
        } else {
          l_state = readOnlyState;
        }
        break;
      case XAResource.TMRESUME:
          l_state = startState;
          break;
      default:
        throw protocolException;
    }
    return l_state;
  }

  XAResourceState commit(SQLMXXid p_xid) throws XAException
  {
    return commitState;
  }

  // WLS does it. Dont like it but WLS does it.
  XAResourceState rollback(SQLMXXid p_xid) throws XAException
  {
    return endState;
  }

  XAResourceState end(SQLMXXid p_xid) throws XAException
  {
    return endState;
  }
}

class StateJoin
    extends XAResourceState
{
  StateJoin()
  {
    super(xa_state_join_);
  }


  XAResourceState start(SQLMXXid p_xid, int p_startFlags) throws XAException
  {
    switch (p_startFlags)
    {
      case XAResource.TMJOIN:
        break;
      default:
        throw protocolException;
    }
    return joinState;
  }


  XAResourceState end(SQLMXXid p_xid) throws XAException
  {
    return endState;
  }
}

class StateEnd
    extends XAResourceState
{
  StateEnd()
  {
    super(xa_state_end_);
  }


  XAResourceState start(SQLMXXid p_xid, int p_startFlags) throws XAException
  {
    XAResourceState l_state = startState;
    switch (p_startFlags)
    {
      case XAResource.TMNOFLAGS:
      case XAResource.TMJOIN:
        if (p_xid != null)
        {
          l_state = joinState;
        } else {
          l_state = startState;
        }
        break;
      case XAResource.TMRESUME:
        l_state = startState;
        break;
      default:
        throw protocolException;
    }
    return l_state;
  }


  XAResourceState end(SQLMXXid p_xid) throws XAException
  {
    return endState;
  }


  XAResourceState prepare(SQLMXXid p_xid) throws XAException
  {
    return prepareState;
  }


  XAResourceState commit(SQLMXXid p_xid) throws XAException
  {
    return commitState;
  }


  XAResourceState rollback(SQLMXXid p_xid) throws XAException
  {
    return rollbackState;
  }
}

class StatePrepare
    extends XAResourceState
{
  StatePrepare()
  {
    super(xa_state_prepare_);
  }

  XAResourceState start(SQLMXXid p_xid, int p_startFlags) throws XAException
  {
    switch (p_startFlags)
    {
      case XAResource.TMNOFLAGS:
      case XAResource.TMRESUME:
      case XAResource.TMJOIN:
        break;
      default:
        throw protocolException;
    }
    return startState;
  }

  XAResourceState prepare(SQLMXXid p_xid) throws XAException
  {
    return readOnlyState;
  }


  XAResourceState commit(SQLMXXid p_xid) throws XAException
  {
    return commitState;
  }


  XAResourceState rollback(SQLMXXid p_xid) throws XAException
  {
    return rollbackState;
  }
}

class StateReadOnly
    extends StatePrepare
{
  final int m_stateValue = xa_state_readonly_;

  XAResourceState prepare(SQLMXXid p_xid) throws XAException
  {
    return readOnlyState;
  }
}

class StateCommit
    extends XAResourceState
{
  StateCommit()
  {
    super(xa_state_commit_);
  }


  XAResourceState commit(SQLMXXid p_xid) throws XAException
  {
    return alreadyCommittedState;
  }


  XAResourceState forget(SQLMXXid p_xid) throws XAException
  {
    throw noTxException;
  }
}

class StateAlreadyCommitted
    extends StateCommit
{
  final int m_stateValue = xa_state_already_committed_;
}

class StateRollback
    extends XAResourceState
{
  StateRollback()
  {
    super(xa_state_rollback_);
  }


  XAResourceState rollback(SQLMXXid p_xid) throws XAException
  {
    // already rolledback
    return alreadyRolledbackState;
  }


  XAResourceState forget(SQLMXXid p_xid) throws XAException
  {
    throw noTxException;
  }
}

class StateAlreadyRolledback
    extends StateRollback
{
  final int m_stateValue = xa_state_already_rolledback_;
}

class StateForget
    extends XAResourceState
{
  StateForget()
  {
    super(xa_state_forget_);
  }


  XAResourceState forget(SQLMXXid p_xid) throws XAException
  {
    throw noTxException;
  }
}

