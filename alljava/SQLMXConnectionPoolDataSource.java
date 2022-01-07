// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003-2007
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
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.PooledConnection;

/**
 * 
 * <p>
 * HP JDBC Type 4 Driver <code>ConnectionPoolDataSource</code> class.
 * </p>
 * <p>
 * Description: A <code>ConnectionPoolDataSource</code> object is a factory
 * for <code>PooledConnection</code> objects. As the name indicates, this
 * object provides a <code>PooledConnection</code> for data sources to be used
 * by the application servers.
 * </p>
 * 
 * <p>
 * The <code>SQLMXConnectionPoolDataSource</code> class should be used to
 * provide JDBC3.0 connection pooling features. The
 * <code>SQLMXConnectionPoolDataSource</code> is used by the application
 * servers like WSAS to provide connection pooling features to the J2EE
 * applications. <code>SQLMXConnectionPoolDataSource.getPooledConnection()</code>
 * returns the <code>javax.sql.PooledConnection object</code>.
 * </p>
 * 
 * 
 * Setting connection properties such as catalog, schema, timeouts, and so on
 * are done at the higher level objects such as DataSource or DriverManager.
 * 
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 * 
 * @see T4Properties
 * @see SQLMXDataSource
 */

public class SQLMXConnectionPoolDataSource extends T4DSProperties implements
		javax.sql.ConnectionPoolDataSource, java.io.Serializable, Referenceable

{

	/**
	 * Attempts to establish a physical database connection that can be used as
	 * a pooled connection.
	 * 
	 * @return A <code>PooledConnection</code> object that is a physical
	 *         connection to the MXCS server that this
	 *         <code>SQLMXConnectionPoolDataSource</code> object represents.
	 * @throws SQLException
	 *             If any MXCS error occurs.
	 */
	public PooledConnection getPooledConnection() throws SQLException {
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"getPooledConnection", "", p);
		}
		if (getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(null);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnectionPoolDataSource");
			lr.setSourceMethodName("getPooledConnection");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			getLogWriter().println(temp);
		}
		SQLMXPooledConnection connect;

		Properties l_props = super.getProperties();
		T4Properties l_t4props = new T4Properties(l_props);
		connect = new SQLMXPooledConnection(this, l_t4props);

		return connect;
	}

	/**
	 * Attempts to establish a physical database connection that can be used as
	 * a pooled connection.
	 * 
	 * @param username
	 *            Safeguard user name.
	 * @param password
	 *            Safeguard user password.
	 * @return A <code>PooledConnection</code> object that is a physical
	 *         connection to the MXCS server that this
	 *         <code>SQLMXConnectionPoolDataSource</code> object represents.
	 * @throws SQLException
	 *             If any MXCS error occurs.
	 */
	public PooledConnection getPooledConnection(String username, String password)
			throws SQLException {
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			//Soln 10-100419-9578 -start
			Object p[] = T4LoggingUtilities.makeParams(null, username, "*****");
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"getPooledConnection", "", p);
		}
		if (getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			//Soln 10-100419-9578 -start
			Object p[] = T4LoggingUtilities.makeParams(null, username, "*****");
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnectionPoolDataSource");
			lr.setSourceMethodName("getPooledConnection");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			getLogWriter().println(temp);
		}
		SQLMXPooledConnection connect;

		setUser(username);
		setPassword(password);
		return getPooledConnection();

	}

	/**
	 * Returns all the properties associated with this
	 * <code>ConnectionPoolDataSource</code>.
	 * 
	 * @return Reference Object containing all the Type 4 property references.
	 * @throws NamingException
	 */
	public Reference getReference() throws NamingException {
		if (t4Logger_ != null && t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"getReference", "", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXConnectionPoolDataSource");
				lr.setSourceMethodName("getReference");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}

		Reference ref = new Reference(this.getClass().getName(),
				"com.tandem.t4jdbc.SQLMXConnectionPoolDataSourceFactory", null);
		ref = addReferences(ref);
		ref.add(new StringRefAddr("propertyCycle", Integer
				.toString(propertyCycle_)));
		return ref;

	}

	/**
	 * Sets the Property cycle property. This property is not supprted by the
	 * Type 4 driver. This property is ignored by the Type 4 driver.
	 * 
	 * @param propertyCycle
	 */
	public void setPropertyCycle(int propertyCycle) {
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"setPropertyCycle", "", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null, propertyCycle);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXConnectionPoolDataSource");
				lr.setSourceMethodName("setPropertyCycle");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		propertyCycle_ = propertyCycle;
	}

	/**
	 * Returns the Property cycle property. This property is not supprted by the
	 * Type 4 driver. This property is ignored by the Type 4 driver.
	 * 
	 * @return propertyCycle
	 */
	public int getPropertyCycle() {
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"getPropertyCycle", "", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXConnectionPoolDataSource");
				lr.setSourceMethodName("getPropertyCycle");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		return propertyCycle_;
	}

	// --------------------------------------------------------
	void setupLogFileHandler() {
		try {
			if (getT4LogFile() == null) {
				setT4LogFile(getT4GlobalLogFile());
				setT4LogFileHandler(getT4GlobalLogFileHandler());
			} else {
				if (getT4LogFileHandler() == null) {
					String temp = getT4LogFile();
					FileHandler fh1 = new FileHandler(temp);
					Formatter ff1 = new T4LogFormatter();

					fh1.setFormatter(ff1);
					setT4LogFileHandler(fh1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end setupLogFileHandler

	// --------------------------------------------------------
	//R3.1 changes - start
	// --------------------------------------------------------
	void setupSlowQueryLogFileHandler() {
		try {
			if (getT4QueryExecuteLogFile() == null) {
				setT4QueryExecuteLogFile(getT4SlowQueryGlobalLogFile());
				setT4SlowQueryLogFileHandler(getT4SlowQueryGlobalLogFileHandler());
			} else {
				if (getT4SlowQueryLogFileHandler() == null) {
					String temp = getT4QueryExecuteLogFile();
					FileHandler fh1 = new FileHandler(temp);
					Formatter ff1 = new T4LogFormatter();

					fh1.setFormatter(ff1);
					setT4SlowQueryLogFileHandler(fh1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end setupSlowQueryLogFileHandler
	//R3.1 changes -- end
	// --------------------------------------------------------


	/**
	 * Creates a pooled connection object.
	 * 
	 * @see #SQLMXConnectionPoolDataSource(Properties)
	 * @see T4Properties
	 */
	public SQLMXConnectionPoolDataSource() {
		super();
		//Added for R3.1
		if(this.getQueryExecuteTime()>0){
		 setupSlowQueryLogFileHandler();
		}
//		 Ravi 01'Feb'07 to make it for JBoss Logging for XA-datasource -R3.0
		if (Level.parse(getT4LogLevel()) != Level.OFF)
			setupLogFileHandler();
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"SQLMXConnectionPoolDataSource",
					"Note, super called before this.", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXConnectionPoolDataSource");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
	}

	/**
	 * Creates a pooled connection object with the properties specified.
	 * 
	 * @param props
	 *            properties for the Type 4 connection
	 * @see #SQLMXConnectionPoolDataSource()
	 * @link T4Properties
	 */
	public SQLMXConnectionPoolDataSource(Properties props) {
		super(props);
		//Added for R3.1
		if(this.getQueryExecuteTime()>0){
		setupSlowQueryLogFileHandler();
		}
		//// Ravi 01'Feb'07 to make it for JBoss Logging for XA-datasource R3.0
		if (Level.parse(getT4LogLevel()) != Level.OFF)
			setupLogFileHandler();
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null, props);
			t4Logger_.logp(Level.FINE, "SQLMXConnectionPoolDataSource",
					"SQLMXConnectionPoolDataSource",
					"Note, super called before this.", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null, props);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXConnectionPoolDataSource");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
	}

	/**
	 * @deprecated
	 */
	public void setNameType(String nameType) {
	}

	/**
	 * @deprecated
	 */
	public String getNameType() {
		return null;
	}

	// Standard ConnectionPoolDataSource Properties
	int propertyCycle_;
}
