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
 * Connection wrapper around the SQLMXConnection object.
 * It is possible to add this function inside the SQLMXConnection itself.
 * But a wrapper gives a clean seperation of XA classes from non-XA if a
 * packaging need arises.
 */
package com.tandem.t4jdbc;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
//import java.util.concurrent.Executor;
import java.util.logging.Level;
import javax.transaction.xa.*;

public class SQLMXTxConnection
    implements Connection {
  private SQLMXConnection m_sqlmxConnection;
  private boolean m_xa_txnActive = false;
  private T4Properties m_t4props;
  private T4XALogger m_t4XALogger;
  private boolean m_isClosed;

  /**
   * Constructor for the SQLMXTxConnection class. Creates a wrapper around
   * the SQLMXConnection class.
   * @param p_sqlmxConnection
   * @throws SQLException
   */
  SQLMXTxConnection(SQLMXConnection p_sqlmxConnection) throws SQLException {
    m_isClosed = false;
    m_sqlmxConnection = p_sqlmxConnection;
    m_t4props = m_sqlmxConnection.props_;
    m_t4XALogger = new T4XALogger(m_sqlmxConnection);

    m_t4XALogger.logAndIgnore("SQLMXTxConnection acquired.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "Constructor",
                              T4LoggingUtilities.makeParams(m_t4props, p_sqlmxConnection));

  }

  void setXA()  {
    m_xa_txnActive = true;
    m_sqlmxConnection.m_isXA_ = true;
    m_sqlmxConnection.props_.setUseExternalTransaction("NO"); //Commitwork RFE
  }

  void reSetXA()  {
    m_xa_txnActive = false;
    m_sqlmxConnection.m_isXA_ = false;
  }

  SQLMXConnection getSQLMXConnection() {
    return m_sqlmxConnection;
  }

  // Public Connection Methods
  public Statement createStatement() throws SQLException {

    m_t4XALogger.logAndIgnore("Creating Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "createStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    Statement stmt = m_sqlmxConnection.createStatement();

    m_t4XALogger.logAndIgnore("Creating Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "createStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return stmt;
  }

  public PreparedStatement prepareStatement(String p_string) throws
      SQLException {

    m_t4XALogger.logAndIgnore("Preparing Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    PreparedStatement pstmt = m_sqlmxConnection.prepareStatement(p_string);

    m_t4XALogger.logAndIgnore("Prepared Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    return pstmt;
  }

  public CallableStatement prepareCall(String p_string) throws SQLException {
    m_t4XALogger.logAndIgnore("Preparing a Callable Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareCall",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    CallableStatement cstmt = m_sqlmxConnection.prepareCall(p_string);

    m_t4XALogger.logAndIgnore("Preparing a Callable Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareCall",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    return cstmt;
  }

  public String nativeSQL(String p_string) throws SQLException {
    m_t4XALogger.logAndIgnore("Getting nativeSQL statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "nativeSQL",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    String nsql = m_sqlmxConnection.nativeSQL(p_string);

    m_t4XALogger.logAndIgnore("Got nativeSQL statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "nativeSQL",
                              T4LoggingUtilities.makeParams(m_t4props, nsql));

    return nsql;
  }

  public void setAutoCommit(boolean p_autoCommitMode) throws SQLException {

    m_t4XALogger.logAndIgnore("Setting the autoCommit mode",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setAutoCommit",
                              T4LoggingUtilities.makeParams(m_t4props, p_autoCommitMode));

    throwWhenConnectionClosed();
    if (p_autoCommitMode == true) {
      checkXaAndThrowSqlException();
    }
    m_sqlmxConnection.setAutoCommit(p_autoCommitMode);

    m_t4XALogger.logAndIgnore("Set the autoCommit mode",
                          Level.FINE,
                          "SQLMXTxConnection",
                          "setAutoCommit",
                          T4LoggingUtilities.makeParams(m_t4props, p_autoCommitMode));

  }

  public boolean getAutoCommit() throws SQLException {
    m_t4XALogger.logAndIgnore("Getting the autoCommit mode",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getAutoCommit",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    boolean autocommit = m_sqlmxConnection.getAutoCommit();

    m_t4XALogger.logAndIgnore("Got the autoCommit mode",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getAutoCommit",
                              T4LoggingUtilities.makeParams(m_t4props, autocommit));

    return autocommit;
  }

  public void commit() throws SQLException {
    m_t4XALogger.logAndIgnore("Committing the txn",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "commit",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    checkXaAndThrowSqlException();
    m_sqlmxConnection.commit();

    m_t4XALogger.logAndIgnore("Committed the txn",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "commit",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

  }

  public void rollback() throws SQLException {
    m_t4XALogger.logAndIgnore("Rolling back txn.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "rollback",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    checkXaAndThrowSqlException();
    m_sqlmxConnection.rollback();

    m_t4XALogger.logAndIgnore("Rolled back the txn.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "rollback",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));
  }


  public void close() throws SQLException {
    m_t4XALogger.logAndIgnore("Closing TX connection",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "close",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    m_isClosed = true;
    reSetXA();

    m_t4XALogger.logAndIgnore("Closed TX connection",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "close",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

  }

  public synchronized boolean isClosed() throws SQLException {
    m_t4XALogger.logAndIgnore("",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "isClosed",
                              T4LoggingUtilities.makeParams(m_t4props, m_isClosed));

    return m_isClosed;
  }

  public DatabaseMetaData getMetaData() throws SQLException {
    m_t4XALogger.logAndIgnore("Get the database meta data.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getMetaData",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    DatabaseMetaData dbm = m_sqlmxConnection.getMetaData();

    m_t4XALogger.logAndIgnore("Got the database meta data.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getMetaData",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return dbm;
  }

  public void setReadOnly(boolean p_readOnly) throws SQLException {
    m_t4XALogger.logAndIgnore("Setting the connection read only.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setReadOnly",
                              T4LoggingUtilities.makeParams(m_t4props, p_readOnly));

    throwWhenConnectionClosed();
    m_sqlmxConnection.setReadOnly(p_readOnly);

    m_t4XALogger.logAndIgnore("Set the connection read only.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setReadOnly",
                              T4LoggingUtilities.makeParams(m_t4props, p_readOnly));
  }

  public boolean isReadOnly() throws SQLException {
    m_t4XALogger.logAndIgnore("Check the connection is read only.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getReadOnly",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    boolean readOnly = m_sqlmxConnection.isReadOnly();

    m_t4XALogger.logAndIgnore("Checked the connection is read only setting.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getReadOnly",
                              T4LoggingUtilities.makeParams(m_t4props, readOnly));

    return readOnly;
  }

  public void setCatalog(String p_catalog) throws SQLException {
    m_t4XALogger.logAndIgnore("Setting the catalog for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setCatalog",
                              T4LoggingUtilities.makeParams(m_t4props, p_catalog));

    throwWhenConnectionClosed();
    m_sqlmxConnection.setCatalog(p_catalog);

    m_t4XALogger.logAndIgnore("Set the catalog for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setCatalog",
                              T4LoggingUtilities.makeParams(m_t4props, p_catalog));

  }

  //Modification for L36 Corda : start
  public void setSchema(String p_schema) throws SQLException {
	    m_t4XALogger.logAndIgnore("Setting the schema for the connection.",
	                              Level.FINE,
	                              "SQLMXTxConnection",
	                              "setSchema",
	                              T4LoggingUtilities.makeParams(m_t4props, p_schema));

	    throwWhenConnectionClosed();
	    m_sqlmxConnection.setSchema(p_schema);

	    m_t4XALogger.logAndIgnore("Set the schema for the connection.",
	                              Level.FINE,
	                              "SQLMXTxConnection",
	                              "setSchema",
	                              T4LoggingUtilities.makeParams(m_t4props, p_schema));

	  }
  //End
  
  public String getCatalog() throws SQLException {
    m_t4XALogger.logAndIgnore("Get the catalog for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getCatalog",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    String catalog = m_sqlmxConnection.getCatalog();

    m_t4XALogger.logAndIgnore("Got the catalog for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getCatalog",
                              T4LoggingUtilities.makeParams(m_t4props, catalog));

    return catalog;
  }

  //Modification for L36 Corda : start
  public String getSchema() throws SQLException {
	    m_t4XALogger.logAndIgnore("Get the schema for the connection.",
	                              Level.FINE,
	                              "SQLMXTxConnection",
	                              "getSchema",
	                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

	    throwWhenConnectionClosed();
	    String schema = m_sqlmxConnection.getSchema();

	    m_t4XALogger.logAndIgnore("Got the schema for the connection.",
	                              Level.FINE,
	                              "SQLMXTxConnection",
	                              "getSchema",
	                              T4LoggingUtilities.makeParams(m_t4props, schema));

	    return schema;
  }
  //End
  
  public void setTransactionIsolation(int p_txn_isolation) throws SQLException {

    m_t4XALogger.logAndIgnore("Setting the transaction isolation for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setTransactionIsolation",
                              T4LoggingUtilities.makeParams(m_t4props, p_txn_isolation));

    throwWhenConnectionClosed();
    checkXaAndThrowSqlException();
    m_sqlmxConnection.setTransactionIsolation(p_txn_isolation);

    m_t4XALogger.logAndIgnore("Set the transaction isolation for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setTransactionIsolation",
                              T4LoggingUtilities.makeParams(m_t4props, p_txn_isolation));

  }

  public int getTransactionIsolation() throws SQLException {
    m_t4XALogger.logAndIgnore("Get the transaction isolation for the connection.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getTransactionIsolation",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    int isol = m_sqlmxConnection.getTransactionIsolation();

    m_t4XALogger.logAndIgnore("Got the transaction isolation for the connection.",
                          Level.FINE,
                          "SQLMXTxConnection",
                          "getTransactionIsolation",
                          T4LoggingUtilities.makeParams(m_t4props, isol));

    return isol;
  }

  public SQLWarning getWarnings() throws SQLException {
    m_t4XALogger.logAndIgnore("Get the SQLWarnings.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getWarnings",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    SQLWarning warn = m_sqlmxConnection.getWarnings();

    m_t4XALogger.logAndIgnore("Got the SQLWarnings.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getWarnings",
                              T4LoggingUtilities.makeParams(m_t4props, warn));

    return warn;
  }

  public void clearWarnings() throws SQLException {
    m_t4XALogger.logAndIgnore("Clear the SQLWarnings.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "clearWarnings",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    m_sqlmxConnection.clearWarnings();

    m_t4XALogger.logAndIgnore("Cleared the SQLWarnings.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "clearWarnings",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));
  }

  public Statement createStatement(int p_int0, int p_int1) throws SQLException {
    m_t4XALogger.logAndIgnore("Create a new SQLStatement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "createStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    Statement stmt = m_sqlmxConnection.createStatement(p_int0, p_int1);

    m_t4XALogger.logAndIgnore("Created a new SQLStatement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "createStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return stmt;
  }

  public PreparedStatement prepareStatement(String p_string, int p_int1,
                                            int p_int2) throws SQLException {

    m_t4XALogger.logAndIgnore("Preparing SQL Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    PreparedStatement pstmt = m_sqlmxConnection.prepareStatement(p_string, p_int1, p_int2);

    m_t4XALogger.logAndIgnore("Prepared SQL Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));
    return pstmt;
  }

  public CallableStatement prepareCall(String p_string, int p_int1, int p_int2) throws
      SQLException {

    m_t4XALogger.logAndIgnore("Preparing callable SQL Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareCall",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    CallableStatement cstmt = m_sqlmxConnection.prepareCall(p_string, p_int1, p_int2);

    m_t4XALogger.logAndIgnore("Prepared callable SQL Statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareCall",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return cstmt;
  }

  public Map getTypeMap() throws SQLException {

    m_t4XALogger.logAndIgnore("Get the Type Map.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getTypeMap",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    Map map = m_sqlmxConnection.getTypeMap();

    m_t4XALogger.logAndIgnore("Got the Type Map.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getTypeMap",
                              T4LoggingUtilities.makeParams(m_t4props, map));

    return map;
  }

  public void setTypeMap(Map p_map) throws SQLException {
    m_t4XALogger.logAndIgnore("Setting the Type Map.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setTypeMap",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    m_sqlmxConnection.setTypeMap(p_map);

    m_t4XALogger.logAndIgnore("Set the Type Map.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setTypeMap",
                              T4LoggingUtilities.makeParams(m_t4props, p_map));

  }

  public void setHoldability(int p_int0) throws SQLException {
    m_t4XALogger.logAndIgnore("Setting the Holdability.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setHoldability",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    m_sqlmxConnection.setHoldability(p_int0);

    m_t4XALogger.logAndIgnore("Set the Holdability.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setHoldability",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));
  }

  public int getHoldability() throws SQLException {
    m_t4XALogger.logAndIgnore("Get the Holdability.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getHoldability",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    int hold = m_sqlmxConnection.getHoldability();

    m_t4XALogger.logAndIgnore("Got the Holdability.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "getHoldability",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return hold;
  }

  public Savepoint setSavepoint() throws SQLException {
    m_t4XALogger.logAndIgnore("Setting the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setSavepoint",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    Savepoint save = m_sqlmxConnection.setSavepoint();

    m_t4XALogger.logAndIgnore("Set the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setSavepoint",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return save;
  }

  public Savepoint setSavepoint(String p_string) throws SQLException {

    m_t4XALogger.logAndIgnore("Setting the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setSavepoint",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    Savepoint save = m_sqlmxConnection.setSavepoint(p_string);

    m_t4XALogger.logAndIgnore("Set the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "setSavepoint",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));
    return save;
  }

  public void rollback(Savepoint p_savepoint) throws SQLException {

    m_t4XALogger.logAndIgnore("Rolling back to the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "rollback",
                              T4LoggingUtilities.makeParams(m_t4props, p_savepoint));

    throwWhenConnectionClosed();
    m_sqlmxConnection.rollback(p_savepoint);

    m_t4XALogger.logAndIgnore("Rolled lback to the Savepoint.",
                          Level.FINE,
                          "SQLMXTxConnection",
                          "rollback",
                          T4LoggingUtilities.makeParams(m_t4props, p_savepoint));

  }

  public void releaseSavepoint(Savepoint p_savepoint) throws SQLException {

    m_t4XALogger.logAndIgnore("Releasing the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "releaseSavepoint",
                              T4LoggingUtilities.makeParams(m_t4props, p_savepoint));

    throwWhenConnectionClosed();
    m_sqlmxConnection.releaseSavepoint(p_savepoint);

    m_t4XALogger.logAndIgnore("Released the Savepoint.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "releaseSavepoint",
                              T4LoggingUtilities.makeParams(m_t4props, p_savepoint));

  }

  public Statement createStatement(int p_int0, int p_int1, int p_int2) throws
      SQLException {
    m_t4XALogger.logAndIgnore("Creating the SQL statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "createStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    Statement stmt = m_sqlmxConnection.createStatement(p_int0, p_int1, p_int2);

    m_t4XALogger.logAndIgnore("Created the SQL statement.",
                          Level.FINE,
                          "SQLMXTxConnection",
                          "createStatement",
                          T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return stmt;
  }

  public PreparedStatement prepareStatement(String p_string, int p_int1,
                                            int p_int2, int p_int3) throws
      SQLException {

    m_t4XALogger.logAndIgnore("Creating the PreparedStatement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    PreparedStatement pstmt = m_sqlmxConnection.prepareStatement(p_string, p_int1, p_int2, p_int3);

    m_t4XALogger.logAndIgnore("Created the PreparedStatement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return pstmt;
  }

  public CallableStatement prepareCall(String p_string, int p_int1, int p_int2,
                                       int p_int3) throws SQLException {

      m_t4XALogger.logAndIgnore("Creating the callable statement.",
                                Level.FINE,
                                "SQLMXTxConnection",
                                "prepareCall",
                                T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    throwWhenConnectionClosed();
    CallableStatement cstmt = m_sqlmxConnection.prepareCall(p_string, p_int1, p_int2, p_int3);

    m_t4XALogger.logAndIgnore("Created the callable statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareCall",
                              T4LoggingUtilities.makeParams(m_t4props, m_sqlmxConnection));

    return cstmt;
  }

  public PreparedStatement prepareStatement(String p_string, int p_int1) throws
      SQLException {
    m_t4XALogger.logAndIgnore("Creating the prepared statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    PreparedStatement pstmt = m_sqlmxConnection.prepareStatement(p_string, p_int1);

    m_t4XALogger.logAndIgnore("Created the prepared statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    return pstmt;
  }

  public PreparedStatement prepareStatement(String p_string, int[] p_intArray) throws
      SQLException {
    m_t4XALogger.logAndIgnore("Creating the prepared statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    PreparedStatement pstmt = m_sqlmxConnection.prepareStatement(p_string, p_intArray);

    m_t4XALogger.logAndIgnore("Created the prepared statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    return pstmt;
  }

  public PreparedStatement prepareStatement(String p_string,
                                            String[] p_stringArray) throws
      SQLException {
    m_t4XALogger.logAndIgnore("Create the prepared statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));

    throwWhenConnectionClosed();
    PreparedStatement pstmt = m_sqlmxConnection.prepareStatement(p_string, p_stringArray);

    m_t4XALogger.logAndIgnore("Created the prepared statement.",
                              Level.FINE,
                              "SQLMXTxConnection",
                              "prepareStatement",
                              T4LoggingUtilities.makeParams(m_t4props, p_string));
    return pstmt;
  }


  // throws SQLException if XA transaction is active
  private void checkXaAndThrowSqlException() throws SQLException {
    if (m_xa_txnActive == true) {
       throw SQLMXMessages.createSQLException(m_t4props, null,
                                           "invalid_tx_operation", null);
    }
  }

  // throws SQLException if the connection id closed.
  private void throwWhenConnectionClosed() throws SQLException {
    if (m_isClosed == true) {
       throw SQLMXMessages.createSQLException(m_t4props, null,
                                           "invalid_tx_connection", null);
    }
  }

	// JDBC 4.x Wrapper interface implementation
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		if (iface.isAssignableFrom(getClass())) {
			return iface.cast(this);
		}
		throw new SQLException("Cannot unwrap to requested type [" + iface.getName() + "]");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return iface.isAssignableFrom(getClass());
	}

	// JDBC 4.x stubs
	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"createClob()");
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"createBlob()");
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"createNClob()");
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"createSQLXML()");
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"isValid(int timeout)");
		return false;
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		try {
			SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
					"setClientInfo(String name, String value)");
		} catch (SQLMXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		try {
			SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
					"setClientInfo(Properties properties)");
		} catch (SQLMXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"getClientInfo(String name)");

		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"getClientInfo()");

		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"createArrayOf(String typeName, Object[] elements)");

		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"createStruct(String typeName, Object[] attributes)");

		return null;
	}

	/*
	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"abort(Executor executor)");

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"setNetworkTimeout(Executor executor, int milliseconds)");

	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(m_t4props, null,
				"getNetworkTimeout()");

		return 0;
	}
	*/

}