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


public class SQLMXXAConnection
    extends SQLMXPooledConnection
    implements XAConnection
{
  private T4Properties m_t4props;
  private SQLMXTxConnection m_tx_connection;
  SQLMXConnection m_resource_connection;
  private SQLMXXAResource m_xa_res;
  private T4XALogger m_t4XALogger;
  private boolean is_xa_conn_closed = false;

  /**
   * Retrieves a Type4 XAResource implementation object that the
   * transaction manager will use to manage this SQLMXXAConnection object's
   * participation in a distributed transaction.
   * @param p_cds SQLMXConnectionPoolDataSource datasource object
   * @param t4props T4Properties object
   */
  SQLMXXAConnection(SQLMXConnectionPoolDataSource p_cds,
                    T4Properties t4props) throws SQLException
  {
    super(p_cds, t4props);
    is_xa_conn_closed = false;
    m_t4XALogger = new T4XALogger( (SQLMXConnection)super.getConnection());
    m_t4props = t4props;
    m_tx_connection = null;
    m_resource_connection = super.getSQLMXConnectionReference();
    if (m_t4XALogger.isLoggable(Level.FINE))
    {
      m_t4XALogger.logAndIgnore("Creating a XAConnection",
                                Level.FINE,
                                "SQLMXXAConnection",
                                "constructor",
                                T4LoggingUtilities.makeParams(m_t4props));
    }
  }


  /**
   * Closes the XAConnection object and the associated SQLMXConnection.
   * @throws SQLException
   */
  public synchronized void close() throws SQLException
  {
    if (m_t4XALogger.isLoggable(Level.FINE))
    {
      m_t4XALogger.logAndIgnore("Closing the SQLMXXAConnection",
                                Level.FINE,
                                "SQLMXXAConnection",
                                "close",
                                T4LoggingUtilities.makeParams(m_t4props));
    }

    // clsoe all connections
    try
    {
      if ( (m_tx_connection != null) && (m_tx_connection.isClosed() == false))
      {
        m_tx_connection.close();
      }
    }
    catch (SQLException sqlex)
    {}
    finally
    {
      is_xa_conn_closed = true;
    }

    m_tx_connection = null;
    super.close();

    if (m_t4XALogger.isLoggable(Level.FINE))
    {
      m_t4XALogger.logAndIgnore("Closed the SQLMXXAConnection",
                                Level.FINE,
                                "SQLMXXAConnection",
                                "close",
                                T4LoggingUtilities.makeParams(m_t4props));
    }
  }


  void setXA()
  {
    if ( (m_xa_res != null) && (m_tx_connection != null))
    {
      m_tx_connection.setXA();
    }
  }


  void reSetXA()
  {
    if (m_tx_connection != null)
    {
      m_tx_connection.reSetXA();
    }
  }


  /**
   *  Retrieves a Type4 txn connection implementation object that the
   *  application will use to access the SQL/MX database in a disributed txn.
   *  @throws SQLException
   */
  public synchronized Connection getConnection() throws SQLException
  {
    if (m_t4XALogger.isLoggable(Level.FINE))
    {
      m_t4XALogger.logAndIgnore("Acquiring a SQLMXTxConnection.",
                                Level.FINE,
                                "SQLMXXAConnection",
                                "createTxConnection",
                                T4LoggingUtilities.makeParams(m_t4props));
    }

    if (super.isClosed() || is_xa_conn_closed)
    {
      XAException xe = SQLMXXAMessages.createXAException(m_t4props, m_t4props.getLocale(), "invalid_connection", null);
      throw SQLMXMessages.createSQLException(m_t4props, m_t4props.getLocale(),
                                             "error_getting_xa_conn",
                                             xe.getMessage());
    }

    SQLMXConnection l_connection = (SQLMXConnection)super.getConnection();
    m_tx_connection = new SQLMXTxConnection(l_connection);

    setXA();

    if (m_t4XALogger.isLoggable(Level.FINE))
    {
      m_t4XALogger.logAndIgnore("Acquired a SQLMXTxConnection.",
                                Level.FINE,
                                "SQLMXXAConnection",
                                "createTxConnection",
                                T4LoggingUtilities.makeParams(m_t4props));
    }

    return m_tx_connection;
  }


  /**
   * Public methods mandated by XAConnection interface
   * XAResource getXAResource()
   * Retrieves an XAResource object that the transaction manager will use to manage
   * this XAConnection object's participation in a distributed transaction.
   * T4 keeps a single connection for all the XAResource and SQLMXConnection.
   * @return SQLMXXAResource object.
   * @throws SQLException if a database access error occurs.
   */
  public synchronized XAResource getXAResource() throws SQLException
  {
    m_t4XALogger.logAndIgnore("Acquiring a new SQLMXXAResource.",
                              Level.FINE,
                              "SQLMXXAConnection",
                              "getXAResource",
                              T4LoggingUtilities.makeParams(m_t4props));

    m_resource_connection = super.getSQLMXConnectionReference();

    try
    {
      m_xa_res = new SQLMXXAResource(this, m_t4props);

    }
    catch (XAException xaex)
    {
      SQLException sqlex = SQLMXMessages.createSQLException(m_t4props,
          m_t4props.getLocale(),
          "error_getting_XAResource",
          xaex);
      sqlex.setStackTrace(xaex.getStackTrace());
      throw sqlex;
    }

    m_t4XALogger.logAndIgnore("Acquired a new SQLMXXAResource.",
                              Level.FINE,
                              "SQLMXXAConnection",
                              "getXAResource",
                              T4LoggingUtilities.makeParams(m_t4props));

    return m_xa_res;
  }

}