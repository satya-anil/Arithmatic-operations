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
 Implements the public interface XADataSource. A factory for SQLMXXAConnection
 objects that is used internally. SQLMXXADataSource is typically registered
 with a naming service that uses the Java Naming and Directory InterfaceTM (JNDI).
 */
package com.tandem.t4jdbc;

import java.sql.Connection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SQLMXXADataSource
    extends SQLMXConnectionPoolDataSource
    implements XADataSource {

  public SQLMXXADataSource() {
    super();
    try {
      if (t4Logger_.isLoggable(Level.FINE) == true)
      {
        Object p[] = T4LoggingUtilities.makeParams(null);
        t4Logger_.logp(Level.FINE, "SQLMXXADataSource", "Constructor", "", p);
      }
      if (getLogWriter() != null)
      {
        LogRecord lr = new LogRecord(Level.FINE, "");
        Object p[] = T4LoggingUtilities.makeParams(null);
        lr.setParameters(p);
        lr.setSourceClassName("SQLMXXADataSource");
        lr.setSourceMethodName("Constructor");
        T4LogFormatter lf = new T4LogFormatter();
        String temp = lf.format(lr);
        getLogWriter().println(temp);
      }
    } catch (SQLException sqlex) {
      // ignore the logging exception as done in the super.
    }

  }

  /**
   * getXAConnection with no parameters
   * Creates the XAConnection for this datasource.
   * Returns: SQLMXXAConnection object.
   * Parameters: none
   */
  public synchronized XAConnection getXAConnection() throws SQLException {
    if (t4Logger_.isLoggable(Level.FINE) == true)
    {
      Object p[] = T4LoggingUtilities.makeParams(null);
      t4Logger_.logp(Level.FINE, "SQLMXXADataSource", "getXAConnection", "", p);
    }
    if (getLogWriter() != null)
    {
      LogRecord lr = new LogRecord(Level.FINE, "");
      Object p[] = T4LoggingUtilities.makeParams(null);
      lr.setParameters(p);
      lr.setSourceClassName("SQLMXXADataSource");
      lr.setSourceMethodName("getXAConnection");
      T4LogFormatter lf = new T4LogFormatter();
      String temp = lf.format(lr);
      getLogWriter().println(temp);
    }

    /* In most connection
     * pool environments it is possible to get the same
     * properties object for different connection. So make
     * a copy to isolate cleanly.
     */
    Properties l_props = super.getProperties();
    T4Properties l_t4props = new T4Properties(l_props);

      // Disable T4 connection pooling on XADataSource
      if (l_t4props.getMaxPoolSize() > -1) {
          throw SQLMXMessages.createSQLException(l_t4props,
           l_t4props.getLocale(),
           "xa_t4_connection_pool_not_supported",
           "T4 connection pooling is supported for XA connections. Set maxPoolSize to -1.");
      }

      return new SQLMXXAConnection(this, l_t4props);
  }

  /**
   * getXAConnection with user and password credentials
   * Creates the XAConnection for this datasource.
   * Returns: SQLMXXAConnection object.
   * @param: user name
   * @param: user password
   */
  public XAConnection getXAConnection(String user, String password) throws
      SQLException {
    if (t4Logger_.isLoggable(Level.FINE) == true)
    {
      Object p[] = T4LoggingUtilities.makeParams(null, user, "password=****");
      t4Logger_.logp(Level.FINE, "SQLMXXADataSource", "getXAConnection", "", p);
    }
    if (getLogWriter() != null)
    {
      LogRecord lr = new LogRecord(Level.FINE, "");
      Object p[] = T4LoggingUtilities.makeParams(null);
      lr.setParameters(p);
      lr.setSourceClassName("SQLMXXADataSource");
      lr.setSourceMethodName("getXAConnection");
      T4LogFormatter lf = new T4LogFormatter();
      String temp = lf.format(lr);
      getLogWriter().println(temp);
    }

    setUser(user);
    setPassword(password);
    return getXAConnection();
  }

}