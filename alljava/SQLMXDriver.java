/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2003-2007, 2015-2016 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>
 * HP JDBC Type 4 Driver implementation.
 * </p>
 * <p>
 * Description: <code>SQLMXDriver</code> is an implementation of the
 * <code>java.sql.Driver</code> interface. The Java SQL framework allows for
 * multiple database drivers to be loaded in single java program. The
 * <code>SQLMXDriver</code> can be loaded programatically by
 * <code>Class.forName("com.tandem.t4jdbc.SQLMXDriver") </code> or by passing
 * <code>-Djdbc.drivers=com.tandem.t4jdbc.SQLMXDriver</code> in the command line of
 * the Java program.
 * </p>
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 * 
 * @see java.sql.DriverManager
 * @see java.sql.Connection
 */
public class SQLMXDriver extends T4Properties implements java.sql.Driver {
	/**
	 * Retrieves whether the Type 4 driver determined that it can open a
	 * connection to the given URL. Typically drivers return true if they
	 * recognize the subprotocol specified in the URL and false if they do not.
	 * For Type 4 driver to recognize the protocol, the URL must start with
	 * <code>jdbc:t4sqlmx</code>.
	 * 
	 * @param url
	 *            The URL of the database.
	 * @return true if the Type 4 driver recognizes the given URL; otherwise,
	 *         false.
	 */
	public boolean acceptsURL(String url) throws SQLException {
		if (t4GlobalLogger.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null, url);
			t4GlobalLogger.logp(Level.FINE, "SQLMXDriver", "acceptsURL", "", p);
		}
		if (getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(null, url);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDriver");
			lr.setSourceMethodName("acceptsURL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			getLogWriter().println(temp);
		}

		return T4Address.acceptsURL(url);
	}

	/**
	 * Attempts to make an MXCS connection to the given URL. The Type 4 driver
	 * returns "null" when it determines that the driver is the wrong kind of
	 * driver to connect to the given URL. This occurence is common; for
	 * example, when the JDBC driver manager is requested to connect to a given
	 * URL, the driver manager it passes the URL to each loaded driver in turn.
	 * The Type 4 driver throws an SQLException when it has trouble connecting
	 * to MXCS. You can use the <code>java.util.Properties</code> argument to
	 * pass arbitrary string name-value pairs as connection arguments.
	 * Typically, you should include "user" and "password" properties in the
	 * Properties object.
	 * 
	 * @param url
	 *            The URL string of format
	 *            <code>jdbc:t4sqlmx://host:port/:</code>
	 * @param info
	 *            <code>java.util.Properties</code> object containing
	 *            name-value pair.
	 * @return The <code>SQLMXConnection</code> object.
	 * @throws SQLException
	 *             When an error occurs connecting to MXCS.
	 * @see T4Properties
	 */
  synchronized public java.sql.Connection connect(String url, Properties info) throws SQLException
  {
		if (t4GlobalLogger.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null, url,
					T4LoggingUtilities.makeProperties(info));
			t4GlobalLogger.logp(Level.CONFIG, "SQLMXDriver", "connect", "", p);
		}
		if (getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.CONFIG, "");
			Object p[] = T4LoggingUtilities.makeParams(null, url,
					T4LoggingUtilities.makeProperties(info));
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDriver");
			lr.setSourceMethodName("connect");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			getLogWriter().println(temp);
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.entering("SQLMXDataSource", "getConnection");
		}

		String key = null;
		SQLMXDataSource ds = null;

		if (acceptsURL(url)) {
			synchronized (this) {
				// properties in the info take precedence.
				// Note, setURL also assigns the T4Properties that are on the
				// URL
				Properties urlProps = setURL(url);

				//
				// Merge any property that is in the url but not in info.
				//
				if (urlProps != null && info != null) {
					Enumeration en1 = urlProps.propertyNames();
					String key1 = null;

					while (en1 != null && en1.hasMoreElements() == true) {
						key1 = (String) en1.nextElement();
						if (info.getProperty(key1) == null) {
							info.setProperty(key1, urlProps.getProperty(key1));
						}
					}
				}

				// If info is false, but there were properties on the URL,
				// the URL properties were already set when we called setURL.
				if (info != null) {
					initialize(info);
					if (getSQLException() != null) {
						throw SQLMXMessages.createSQLException(null,
								getLocale(), "invalid_property",
								getSQLException());
					}
				}
				if (getMaxPoolSize() != -1) {
					key = getUrl() + getCatalog() + getSchema() + getUser()
							+ getPassword() + getServerDataSource()
							+ getBlobTableName() + getClobTableName();

					ds = (SQLMXDataSource) dsCache_.get(key);

					if (ds == null) {
						ds = new SQLMXDataSource(getProperties());
						dsCache_.put(key, ds);
					}
				} else {
					ds = new SQLMXDataSource(getProperties());
				}
			}

			return ds.getConnection(ds.getUser(), ds.getPassword());
		} else {
			return null;
		}
	}

	/**
	 * Retrieves the driver's major JDBC version number. For the Type 4 Driver,
	 * the number should be 3.
	 * 
	 * @return 3.
	 */
	public int getMajorVersion() {
		if (t4GlobalLogger.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4GlobalLogger.logp(Level.FINE, "SQLMXDriver", "getMajorVersion",
					"", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDriver");
				lr.setSourceMethodName("getMajorVersion");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}

		 return SQLMXDatabaseMetaData.JDBC_DRIVER_MAJOR_VERSION; //Added for SQL/MX3.5 release
	}

	/**
	 * Gets the Type 4 driver's minor version number. For the Type 4 driver, the
	 * number should be 11.
	 * 
	 * @return 11
	 */
	public int getMinorVersion() {
		if (t4GlobalLogger.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4GlobalLogger.logp(Level.FINE, "SQLMXDriver", "getMinorVersion",
					"", p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDriver");
				lr.setSourceMethodName("getMinorVersion");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}

		 return SQLMXDatabaseMetaData.JDBC_DRIVER_MINOR_VERSION; //Added for SQL/MX3.5 release
	}

	/**
	 * Gets information about the possible properties for the Type 4 driver. The
	 * <code>getPropertyInfo</code> method is intended to allow a generic GUI
	 * tool to determine the properties that the tool should prompt from a human
	 * to get enough information to connect to MXCS. Depending on the values the
	 * human has supplied so far, additional values might be necessary, so you
	 * might need to iterate though several calls to the
	 * <code>getPropertyInfo</code> method.
	 * 
	 * @param url
	 *            The URL of the database to which to connect
	 * @param info
	 *            <code>java.util.Properties</code> object containing
	 *            name-value pairs. The Type 4 driver ignores the Properties
	 *            parameter passed to the driver.
	 * @return array of <code>DriverPropertyInfo</code> containing property
	 *         details.
	 */

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		Properties prop = new Properties();
		if (info != null) {
			Enumeration e = info.propertyNames();
			String pkey = null;
			while (e != null && e.hasMoreElements()) {
				pkey = (String) e.nextElement();
				if ((pkey.equalsIgnoreCase("password"))) {
					prop.setProperty(pkey, "*****");
				} else {
					prop.setProperty(pkey, info.getProperty(pkey));
				}
			}
		}
		if (t4GlobalLogger.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null, url, info);
			t4GlobalLogger.logp(Level.FINE, "SQLMXDriver", "getPropertyInfo",
					"", p);
		}
		if (getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(null, url, info);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDriver");
			lr.setSourceMethodName("getPropertyInfo");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			getLogWriter().println(temp);
		}
		if (acceptsURL(url)) {
			return super.getPropertyInfo(url, info);
		} else {
			return null;
		}
	}

	/**
	 * Returns whether the Type 4 driver is JDBC compliant.
	 * 
	 * @return true
	 */
	public boolean jdbcCompliant() {
		if (t4GlobalLogger.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(null);
			t4GlobalLogger.logp(Level.FINE, "SQLMXDriver", "jdbcCompliant", "",
					p);
		}
		try {
			if (getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(null);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDriver");
				lr.setSourceMethodName("jdbcCompliant");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}

		return true;
	};

	// Fields
	private static SQLMXDriver singleton_;

	static Hashtable dsCache_;

	/**
	 * Instantiated by either <code>
	 * Class.forName("com.tandem.t4jdbc.SQLMXDriver")</code>
	 * or by passing <code>-Djdbc.drivers=com.tandem.t4jdbc.SQLMXDriver</code>
	 * property in the command line of the JDBC program.
	 */
	public SQLMXDriver() {
		super();
		if (logger.isLoggable(Level.INFO)) {
			logger.entering("SQLMXDataSource", "getConnection");
		}
	}

	// initializer to register the Driver with the Driver manager
	static {
		// Register the Driver with the Driver Manager
		try {
			singleton_ = new SQLMXDriver();
			DriverManager.registerDriver(singleton_);
		} catch (SQLException e) {
			singleton_ = null;
			e.printStackTrace();
		}

		dsCache_ = new Hashtable();
	}
}
