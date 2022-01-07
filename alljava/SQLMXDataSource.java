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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
//import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

/**
 * 
 * <p>
 * HP JDBC Type 4 Driver <code>DataSource</code> class.
 * </p>
 * <p>
 * Description: A <code>DataSource</code> object is a factory for Connection
 * objects. An object that implements the <code>DataSource</code> interface is
 * typically registered with a JNDI service provider. A JDBC driver that is
 * accessed through the <code>DataSource</code> API does not automatically
 * register itself with the <code>DriverManager</code> object.
 * </p>
 * 
 * <p>
 * The <code>SQLMXDataSource</code> class can provide connection pooling and
 * statement pooling features.
 * </p>
 * 
 * <pre>
 * &lt;b&gt;Setting properties for the SQLMXDataSource in the Type 4 driver&lt;/b&gt;
 *    SQLMXDataSource ds = new SQLMXDataSource();
 *   ds.setUrl(&quot;jdbc:t4sqlmx://&lt;MXCS host&gt;:&lt;MXCS port&gt;/:&quot;);
 *   ds.setCatalog(&quot;your catalog&quot;);
 *   ds.setSchema(&quot;your schema&quot;);
 *   ds.setUser(&quot;safeguard user name&quot;);
 *   ds.setPassword(&quot;safeguard password&quot;);
 * 
 *   // Following are optional properties
 *   ds.setConnectionTimeout(&quot;timeout in seconds&quot;);
 *   ds.setT4LogFile(&quot;your log file location&quot;);
 *   ds.setT4LogLevel(&quot;SEVERE&quot;);
 *   ds.setServerDataSource(&quot;MXCS datasource name&quot;);
 * 
 *   // Properties relevant for Type 4 connection pooling.
 *   // Set ds.setMaxPoolSize(-1) to turn OFF connection pooling
 *   ds.setMaxPoolSize(&quot;number of connections required&quot;);
 *   ds.setMinPoolSize(&quot;number of connections required&quot;);
 * 
 *   // Properties relevant for Type 4 statement pooling.
 *   // Set ds.setMaxStatement(0) to turn statement pooling OFF
 *   // Statement pooling is enabled only when connection pooling is enabled.
 *   ds.setMaxStatements(&quot;number of statements to be pooled&quot;);
 * </pre>
 * 
 * <pre>
 * &lt;b&gt;Programmatically registering SQLMXDataSource with JDNI&lt;/b&gt;
 * 	java.util.Hashtable env = new java.util.Hashtable();
 *      env.put(Context.INITIAL_CONTEXT_FACTORY, &quot;Factory class name here&quot;);
 *      javax.naming.Context ctx = new javax.naming.InitialContext(env);
 *      ctx.rebind(&quot;DataSource name here&quot;, ds);
 * </pre>
 * 
 * <pre>
 * &lt;b&gt;Application making Type4 connection using the DataSource from JDNI&lt;/b&gt;
 * 	java.util.Hashtable env = new java.util.Hashtable();
 *      env.put(Context.INITIAL_CONTEXT_FACTORY, &quot;Factory class name here&quot;);
 *      javax.naming.Context ctx = new javax.naming.InitialContext(env);
 *      DataSource ds = (DataSource)ctx.lookup(&quot;DataSource name here&quot;);
 *      java.sql.Connection con = ds.getConnection();
 * </pre>
 * 
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 * 
 * @see T4Properties
 */
public class SQLMXDataSource extends T4DSProperties implements
		javax.sql.DataSource, java.io.Serializable, Referenceable {
	/**
	 * Attempts to establish an MXCS connection.
	 * 
	 * @return a connection to the MXCS server.
	 * @throws SQLException
	 *             if a database access error or MXCS error occurs.
	 * @see #getConnection(String, String)
	 */
	synchronized public Connection getConnection() throws SQLException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SQLMXDataSource", "getConnection");
		}

		Connection conn;
		SQLMXConnection t4Conn;
		SQLMXConnectionPoolDataSource pds;

		if (getSQLException() != null) {
			throw SQLMXMessages.createSQLException(null, getLocale(),
					"invalid_property", getSQLException());
		}

		if (getMaxPoolSize() == -1) {
			t4Conn = new SQLMXConnection(this, getT4Properties());
		} else {
			if (poolManager != null) {
				t4Conn = (SQLMXConnection) poolManager.getConnection();
			} else {

				pds = new SQLMXConnectionPoolDataSource(getProperties());
				//poolManager = new SQLMXPooledConnectionManager(pds,getT4LogLevel());
//				 Ravi 01'Feb'07 to make it for JBoss Logging for XA-datasource R3.0
				poolManager = new SQLMXPooledConnectionManager(pds,Level
						.parse(getT4LogLevel()));
				t4Conn = (SQLMXConnection) poolManager.getConnection();
			}
		}
//Added for setLogWriter()
	    if (getLogWriter() == null)
	    {
	      setLogWriter(DriverManager.getLogWriter());

	    }
		t4Conn.setLogInfo(Level.parse(getT4LogLevel()), getLogWriter());
		conn = t4Conn;

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SQLMXDataSource", "getConnection", conn);
		}

		return conn;
	}

	/**
	 * Attempts to establish an MXCS connection.
	 * 
	 * @return a connection to the MXCS server.
	 * @param username
	 *            Safeguard user name
	 * @param password
	 *            Safeguard user password
	 * @throws SQLException
	 *             if a database access error or MXCS error occurs.
	 * @see #getConnection()
	 */
	synchronized public Connection getConnection(String username,
			String password) throws SQLException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering("SQLMXDataSource", "getConnection", new Object[] {
					this, username, password });
		}

		Connection conn;

		setUser(username);
		setPassword(password);

		conn = getConnection();

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("SQLMXDataSource", "getConnection", conn);
		}

		return conn;
	}

	/**
	 * @return Reference Object containing all the Type 4 property references.
	 * @throws NamingException
	 */
	public Reference getReference() throws NamingException {
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_.logp(Level.FINE, "SQLMXDataSource", "Reference", "", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDataSource");
				lr.setSourceMethodName("Reference");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException e) {
	// ignore
		}
		Reference ref = new Reference(this.getClass().getName(),
				"com.tandem.t4jdbc.SQLMXDataSourceFactory", null);
		return addReferences(ref);
	}

	/**
	 * Sets the print writer for the current Type 4 data source.
	 * 
	 * @param out
	 *            java.io.PrintWriter for the current T4 connection.
	 * @throws SQLException
	 *             when error occurs.
	 * @see #getLogWriter()
	 * @see javax.sql.ConnectionPoolDataSource
	 */
	public void setLogWriter(PrintWriter out) throws SQLException {
		super.setLogWriter(out);
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null, out);
			t4Logger_
					.logp(
							Level.FINE,
							"SQLMXDataSource",
							"setLogWriter",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(null, out);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDataSource");
			lr.setSourceMethodName("setLogWriter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			getLogWriter().println(temp);
		}
		if (poolManager != null) {
			poolManager.setLogWriter(getLogWriter());
		}
	}

	// Local methods
	void setPoolManager(Context nameCtx, String dataSourceName)
			throws Exception {
		if (t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null, nameCtx.toString(),
					dataSourceName);
			t4Logger_.logp(Level.FINER, "SQLMXDataSource", "setPoolManager", "",
					p);
		}
		Object pds;

		try {
			pds = nameCtx.lookup(dataSourceName);
			if (pds instanceof SQLMXConnectionPoolDataSource) {
//				poolManager = new SQLMXPooledConnectionManager(
//						(SQLMXConnectionPoolDataSource) pds, getT4LogLevel());				
//				 Ravi 01'Feb'07 to make it for JBoss Logging for XA-datasource R3.0
				poolManager = new SQLMXPooledConnectionManager((SQLMXConnectionPoolDataSource)pds,Level.parse(getT4LogLevel()));
			}
		} catch (javax.naming.NameNotFoundException nnfe) {
		}
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
					//R3.1 changes for Soln 10-110701-8409  -- start
					if (temp.contains("%u")) {
					temp = temp.replace("%u", T4LoggingUtilities.getUniqueID());
					} 
					//R3.1 changes for Soln 10-110701-8409  -- end
					FileHandler fh1 = new FileHandler(temp);

					// KAS Degugging
					// System.out.println("In
					// SQLMXDataSource.setupLogFileHandler");
					// System.out.println(" File = " + temp);
					// System.out.println(" Handler = " + fh1.toString());

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
	//R3.1 Changes -- start
	void setupSlowQueryLogFileHandler() {
		try {
			if (getT4QueryExecuteLogFile() == null) {
				setT4QueryExecuteLogFile(getT4SlowQueryGlobalLogFile());
				setT4SlowQueryLogFileHandler(getT4SlowQueryGlobalLogFileHandler());
			} else {
				if (getT4SlowQueryLogFileHandler() == null) {
					String temp = getT4QueryExecuteLogFile();
					FileHandler fh1 = new FileHandler(temp);

					// KAS Degugging
					// System.out.println("In
					// SQLMXDataSource.setupLogFileHandler");
					// System.out.println(" File = " + temp);
					// System.out.println(" Handler = " + fh1.toString());

					Formatter ff1 = new T4LogFormatter();

					fh1.setFormatter(ff1);
					setT4SlowQueryLogFileHandler(fh1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end setupSlowQueryLogFileHandler
		//R3.1 Changes -- End

	// --------------------------------------------------------

	/**
	 * Contructor for the <code>SQLMXDataSource</code> object.
	 * 
	 * @see #SQLMXDataSource(java.util.Properties)
	 */
	public SQLMXDataSource() {
		super();
		//R3.1 changes -- start
		if(this.getQueryExecuteTime()>0){
		setupSlowQueryLogFileHandler();
		}
		//R3.1 changes -- end
		//if (getT4LogLevel() != Level.OFF) {- R3.0
		// Ravi 01'Feb'07 to make it for JBoss Logging for XA-datasource
		if (Level.parse(getT4LogLevel()) != Level.OFF) {
			setupLogFileHandler();
		}
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_
					.logp(
							Level.FINE,
							"SQLMXDataSource",
							"<init>",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDataSource");
				lr.setSourceMethodName("<init>");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}

	}

	/**
	 * Contructor for the <code>SQLMXDataSource</code> object.
	 * 
	 * @param info
	 *            Contains all the Type 4 properties in a <code>name,
	 * value</code>
	 *            pair.
	 * @see #SQLMXDataSource()
	 * @see java.util.Properties
	 */
	public SQLMXDataSource(Properties info) {
		super(info);
		//R3.1 changes -- start
		if(this.getQueryExecuteTime()>0){
		setupSlowQueryLogFileHandler();
		}
		//R3.1 changes -- end
	//	if (getT4LogLevel() != Level.OFF) { - R3.0
		// Ravi 01'Feb'07 to make it for JBoss Logging for XA-datasource
		
		if (Level.parse(getT4LogLevel()) != Level.OFF) {
			setupLogFileHandler();
		}
		if (t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4Logger_
					.logp(
							Level.FINE,
							"SQLMXDataSource",
							"<init>",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDataSource");
				lr.setSourceMethodName("<init>");
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

	// Fields
	SQLMXPooledConnectionManager poolManager;


	// JDBC 4.x stubs
	/*@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		try {
			SQLMXMessages.throwUnsupportedFeatureException(this.getT4Properties(),
					this.getLocale(), "getParentLogger()");
		} catch (SQLMXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/

}
