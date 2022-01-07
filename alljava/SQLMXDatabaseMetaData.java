/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2003-2007, 2015-2016 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>
 * NonStop JDBC Type 4 driver DataBaseMetaData class.
 * </p>
 * Comprehensive information about the SQL/MX3.0 and Type 4 driver as
 * a whole. This class lets applications users know the capabilities of Nonstop
 * SQL/MX3.0 Database in combination with the Type 4 driver. Information returned
 * by methods in this class applies to the capabilities of a Type 4 driver and
 * Nonstop SQL/MX3.0 Database working together. A user for this class is commonly a
 * tool that needs to discover how to deal with the underlying Nonstop SQL/MX3.0
 * Database. This is especially true for applications that are intended to be
 * used with more than one DBMS. For example, a tool might use the method
 * <code>getTypeInfo</code> to find out what data types can be used in a
 * CREATE TABLE statement. Or a user might call the method
 * <code>supportsCorrelatedSubqueries</code> to see if it is possible to use a
 * correlated subquery or <code>supportsBatchUpdates</code> to see if it is
 * possible to use batch updates. Some DatabaseMetaData methods return lists of
 * information in the form of ResultSet objects. Regular ResultSet methods, such
 * as getString and getInt, can be used to retrieve the data from these
 * ResultSet objects. If a given form of metadata is not available, the
 * ResultSet getter methods throw an SQLException. Some DatabaseMetaData methods
 * take arguments that are String patterns. These arguments all have names such
 * as fooPattern. Within a pattern String, "%" means match any substring of 0 or
 * more characters, and "_" means match any one character. Only metadata entries
 * matching the search pattern are returned. If a search pattern argument is set
 * to null, that argument's criterion will be dropped from the search. A method
 * that gets information about a feature that the Type 4 driver does not support
 * will throw an SQLException. In the case of methods that return a ResultSet
 * object, either a ResultSet object (which might be empty) is returned, or an
 * SQLException is thrown.
 *
 * <p>
 * Description: NonStop JDBC Type 4 driver
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004-2007
 * </p>
 * <p>
 * Company: Hewlett Packard
 * </p>
 */
public class SQLMXDatabaseMetaData extends SQLMXHandle implements
		java.sql.DatabaseMetaData {

	// ----------------------------------------------------------------------
	// First, a variety of minor information about the target database.

	/*
	 * Can all the procedures returned by getProcedures be called by the current
	 * user?
	 *
	 * @return true if so
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "allProceduresAreCallable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("allProceduresAreCallable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Can all the tables returned by getTable be SELECTed by the current user?
	 *
	 * @return true if so
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "allTablesAreSelectable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("allTablesAreSelectable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * What's the url for this database?
	 *
	 * @return the url or null if it can't be generated * "TCP:<hostname>:<portnumber>/NonStopJDBC"
	 * is good enough.
	 */
	public String getURL() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getURL", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getURL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// Solution 10-171030-5323: getURL does not return url in right format    
		String urlFormat = System.getProperty("sqlmxURLFormat");
		if ((urlFormat != null) && (urlFormat.compareToIgnoreCase("old")==0))
		   return connection_.getServerHandle().getUrl();
		else
		   return connection_.getT4Properties().getUrl();
		
	}

	/*
	 * What's our user name as known to the database?
	 *
	 * @return our database user name *
	 *
	 */
	public String getUserName() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getUserName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getUserName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return connection_.getServerHandle().getUid();
	}

	/*
	 * Is the database in read-only mode?
	 *
	 * @return true if so *
	 *
	 */
	public boolean isReadOnly() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "isReadOnly", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("isReadOnly");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Are NULL values sorted high?
	 *
	 * @return true if so
	 *
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "nullsAreSortedHigh", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("nullsAreSortedHigh");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Are NULL values sorted low?
	 *
	 * @return true if so
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "nullsAreSortedLow", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("nullsAreSortedLow");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Are NULL values sorted at the start regardless of sort order?
	 *
	 * @return true if so
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "nullsAreSortedAtStart", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("nullsAreSortedAtStart");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Are NULL values sorted at the end regardless of sort order?
	 *
	 * @return true if so
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "nullsAreSortedAtEnd", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("nullsAreSortedAtEnd");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * What's the name of this database product?
	 *
	 * @return database product name
	 */
	public String getDatabaseProductName() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDatabaseProductName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDatabaseProductName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("NonStop SQL/MX");
	}

	/*
	 * What's the version of this database product?
	 *
	 * @return database version *
	 *
	 */
	public String getDatabaseProductVersion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDatabaseProductVersion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDatabaseProductVersion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// return new String("D43");
		return new String("" + getDatabaseMajorVersion() + "."
				+ getDatabaseMinorVersion());
	}

	/*
	 * What's the name of this JDBC driver?
	 *
	 * @return JDBC driver name
	 */
	public String getDriverName() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDriverName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDriverName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("com.tandem.t4jdbc.SQLMXDriver");
	}

	/*
	 * What's the version of this JDBC driver?
	 *
	 * @return JDBC driver version
	 */
	public String getDriverVersion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDriverVersion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDriverVersion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// return new String("T1249V11");
		return Vproc.getVproc();
	}

	/*
	 * What's this JDBC driver's major version number?
	 *
	 * @return JDBC driver major version
	 */
	public int getDriverMajorVersion() {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDriverMajorVersion", "", p);
		}
		try {
			if (connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDatabaseMetaData");
				lr.setSourceMethodName("getDriverMajorVersion");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}

		 return JDBC_DRIVER_MAJOR_VERSION; //Added for SQL/MX3.5 release
	}

	/*
	 * What's this JDBC driver's minor version number?
	 *
	 * @return JDBC driver minor version number
	 */
	public int getDriverMinorVersion() {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDriverMinorVersion", "", p);
		}
		try {
			if (connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDatabaseMetaData");
				lr.setSourceMethodName("getDriverMinorVersion");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		return JDBC_DRIVER_MINOR_VERSION;//Added for SQL/MX3.5 release
	}

	/*
	 * Does the database store tables in a local file?
	 *
	 * @return true if so
	 */
	public boolean usesLocalFiles() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "usesLocalFiles", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("usesLocalFiles");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Does the database use a file for each table?
	 *
	 * @return true if the database uses a local file for each table
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "usesLocalFilePerTable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("usesLocalFilePerTable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database does not treat the mixed case unquoted SQL
	 * identifiers as case sensitive and does not store the result in mixed
	 * case.
	 *
	 * @return false always for the Type 4 driver.
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsMixedCaseIdentifiers", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsMixedCaseIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Nonstop SQL/MX Database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in upper case.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"storesUpperCaseIdentifiers", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("storesUpperCaseIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in lower case?
	 *
	 * @return true if so
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"storesLowerCaseIdentifiers", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("storesLowerCaseIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Does the database treat mixed case unquoted SQL identifiers as case
	 * insensitive and store them in mixed case?
	 *
	 * @return true if so
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"storesMixedCaseIdentifiers", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("storesMixedCaseIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Nonstop SQL/MX Database treat mixed case quoted SQL identifiers as case
	 * sensitive and as a result store them in mixed case.
	 *
	 * A JDBC-Compliant driver will always return false.
	 *
	 * @return true always true for the Type 4 drive.
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsMixedCaseQuotedIdentifiers", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsMixedCaseQuotedIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in upper case?
	 *
	 * @return true if so
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "storesUpperCaseQuotedIdentifiers",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("storesUpperCaseQuotedIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in lower case?
	 *
	 * @return true if so
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "storesLowerCaseQuotedIdentifiers",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("storesLowerCaseQuotedIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Does the database treat mixed case quoted SQL identifiers as case
	 * insensitive and store them in mixed case?
	 *
	 * @return true if so
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					" storesMixedCaseQuotedIdentifiers", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName(" storesMixedCaseQuotedIdentifiers");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Nonstop SQL/MX Database uses the " string to quote SQL identifiers. This call
	 * returns a space " " if identifier quoting isn't supported.
	 *
	 * A JDBC-Compliant driver always uses a double quote character.
	 *
	 * @return the quoting string ".
	 */
	public String getIdentifierQuoteString() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getIdentifierQuoteString", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getIdentifierQuoteString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("\"");
	}

	/*
	 * Get a comma separated list of all a database's SQL keywords that are NOT
	 * also SQL92 keywords.
	 *
	 * @return the list *
	 *
	 */
	public String getSQLKeywords() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSQLKeywords", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSQLKeywords");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return "DATETIME,FRACTION,PROTOTYPE,SQL_CHAR,SQL_DATE,SQL_DECIMAL,SQL_DOUBLE,SQL_FLOAT"
				+ ",SQL_INT,SQL_INTEGER,SQL_REAL,SQL_SMALLINT,SQL_TIME,SQL_TIMESTAMP,SQL_VARCHAR"
				+ ",TRANSPOSE,UPSHIFT";
	}

	/*
	 * Get a comma separated list of math functions.
	 *
	 * @return the list *
	 *
	 */
	public String getNumericFunctions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getNumericFunctions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getNumericFunctions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String(
				"ABS,ACOS,ASIN,ATAN,ATAN2,CEILING,COS,COSH,DEGREES,EXP,FLOOR,LOG,LOG10,MOD,PI,POWER,RADIANS,RAND"
						+ "SIGN,SIN,SINH,SORT,TAN,TANH");
	}

	/*
	 * Get a comma separated list of string functions.
	 *
	 * @return the list *
	 *
	 */
	public String getStringFunctions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getStringFunctions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getStringFunctions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String(
				"ASCII,CHAR,CHAR_LENGTH,CONCAT,INSERT,LCASE,LEFT,LOCATE,LOWER,LPAD,LTRIM,OCTET_LENGTH"
						+ "POSITION,REPEAT,REPLACE,RIGHT,RPAD,RTRIM,SPACE,SUBSTRING,TRIM,UCASE,UPPER,UPSHIFT");
	}

	/*
	 * Get a comma separated list of system functions.
	 *
	 * @return the list *
	 *
	 */
	public String getSystemFunctions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSystemFunctions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSystemFunctions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("CURRENT_USER,USER");
	}

	/*
	 * Get a comma separated list of time and date functions.
	 *
	 * @return the list *
	 *
	 */
	public String getTimeDateFunctions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getTimeDateFunctions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getTimeDateFunctions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		return new String(
				"CONVERTTIMESTAMP,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP"
						+ "DATEFORMAT,DAY,DAYNAME,DAYOFMONTH,DAYOFWEEK,DAYOFYEAR,EXTRACT,HOUR,JULIANTIMESTAMP,MINUTE"
						+ "MONTH,MONTHNAME,QUARTER,SECOND,WEEK,YEAR");
	}

	/*
	 * This is the string that can be used to escape '_' or '%' in the string
	 * pattern style catalog search parameters.
	 *
	 * <P>The '_' character represents any single character. <P>The '%'
	 * character represents any sequence of zero or more characters. @return the
	 * string used to escape wildcard characters *
	 *
	 */
	public String getSearchStringEscape() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSearchStringEscape", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSearchStringEscape");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("\\");
	}

	/*
	 * Get all the "extra" characters that can be used in unquoted identifier
	 * names (those beyond a-z, A-Z, 0-9 and _).
	 *
	 * @return the string containing the extra characters
	 */
	public String getExtraNameCharacters() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getExtraNameCharacters", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getExtraNameCharacters");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return null;
	}

	// --------------------------------------------------------------------
	// Functions describing which features are supported.

	/*
	 * Is "ALTER TABLE" with add column supported?
	 *
	 * @return true if so
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsAlterTableWithAddColumn",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsAlterTableWithAddColumn");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Is "ALTER TABLE" with drop column supported?
	 *
	 * @return true if so
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsAlterTableWithDropColumn",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsAlterTableWithDropColumn");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Is column aliasing supported?
	 *
	 * <P>If so, the SQL AS clause can be used to provide names for computed
	 * columns or to provide alias names for columns as required.
	 *
	 * A JDBC-Compliant driver always returns true.
	 *
	 * @return true if so
	 */
	public boolean supportsColumnAliasing() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsColumnAliasing", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsColumnAliasing");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database concatenations between NULL and non-NULL values is
	 * NULL.
	 *
	 * A JDBC-Compliant driver always returns true.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "nullPlusNonNullIsNull", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("nullPlusNonNullIsNull");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is the CONVERT function between SQL types supported?
	 *
	 * @return true if so
	 */
	public boolean supportsConvert() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsConvert", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsConvert");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is CONVERT between the given SQL types supported?
	 *
	 * @param fromType the type to convert from @param toType the type to
	 * convert to @return true if so
	 *
	 * @see Types
	 */
	public boolean supportsConvert(int fromType, int toType)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					fromType, toType);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsConvert", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					fromType, toType);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsConvert");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		switch (fromType) {
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.REAL:
		case Types.SMALLINT:
			switch (toType) {
			case Types.CHAR:
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.BIGINT:
				return true;
			default:
				return false;
			}
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			switch (toType) {
			case Types.CHAR:
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.BIGINT:
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return true;
			default:
				return false;
			}
		case Types.DATE:
			switch (toType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.DATE:
			case Types.TIMESTAMP:
				return true;
			default:
				return false;
			}
		case Types.TIME:
			switch (toType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.TIME:
			case Types.TIMESTAMP:
				return true;
			default:
				return false;
			}
		case Types.TIMESTAMP:
			switch (toType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return true;
			default:
				return false;
			}
		case Types.BIT:
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.TINYINT:
			return false;
		default:
			return false;
		}
	}

	/*
	 * Type 4 driver supports table correlation names. Type 4 driver always
	 * returns true.
	 *
	 * @return true for Type 4 driver
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsTableCorrelationNames",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsTableCorrelationNames");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database table correlation names are supported, and they are
	 * not restricted to be different from the names of the Nonstop SQL/MX Database
	 * tables.
	 *
	 * @return false always for Type 4 driver.
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsDifferentTableCorrelationNames", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsDifferentTableCorrelationNames");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * SQL expressions in "ORDER BY" lists are not supported by the Nonstop SQL/MX
	 * Database and Type 4 driver.
	 *
	 * @return false always for Type 4 driver.
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsExpressionsInOrderBy", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsExpressionsInOrderBy");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Can an "ORDER BY" clause use columns not in the SELECT?
	 *
	 * @return true if so
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsOrderByUnrelated", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsOrderByUnrelated");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is some form of "GROUP BY" clause supported?
	 *
	 * @return true if so
	 */
	public boolean supportsGroupBy() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsGroupBy", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsGroupBy");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a "GROUP BY" clause use columns not in the SELECT?
	 *
	 * @return true if so
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsGroupByUnrelated", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsGroupByUnrelated");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Can a "GROUP BY" clause add columns not in the SELECT provided it
	 * specifies all the columns in the SELECT?
	 *
	 * @return true if so
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsGroupByBeyondSelect", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsGroupByBeyondSelect");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Nonstop SQL/MX Database supports escape character in "LIKE" clauses.
	 *
	 * A JDBC-Compliant driver always returns true.
	 *
	 * @return true always true for the Type 4 driver.
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsLikeEscapeClause", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsLikeEscapeClause");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Are multiple ResultSets from a single execute supported?
	 *
	 * @return true if so
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"supportsMultipleResultSets", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsMultipleResultSets");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can we have multiple transactions open at once (on different
	 * connections)?
	 *
	 * @return true if so
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsMultipleTransactions", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsMultipleTransactions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// Need to change it 'true' once Nonstop SQL/MX Database fixes this problem
		return false;
	}

	/*
	 * Nonstop SQL/MX Database table columns can be defined as non-nullable.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"supportsNonNullableColumns", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsNonNullableColumns");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Type 4 driver supports the ODBC Minimum SQL grammar.
	 *
	 * All JDBC-Compliant drivers must return true.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsMinimumSQLGrammar", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsMinimumSQLGrammar");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is the ODBC Core SQL grammar supported?
	 *
	 * @return true if so
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsCoreSQLGrammar", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCoreSQLGrammar");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is the ODBC Extended SQL grammar supported?
	 *
	 * @return true if so
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"supportsExtendedSQLGrammar", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsExtendedSQLGrammar");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Nonstop SQL/MX Database supports the ANSI92 entry level SQL grammar.
	 *
	 * All JDBC-Compliant drivers must return true.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsANSI92EntryLevelSQL", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsANSI92EntryLevelSQL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is the ANSI92 intermediate SQL grammar supported?
	 *
	 * @return true if so
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsANSI92IntermediateSQL",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsANSI92IntermediateSQL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Is the ANSI92 full SQL grammar supported?
	 *
	 * @return true if so
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsANSI92FullSQL", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsANSI92FullSQL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Is the SQL Integrity Enhancement Facility supported?
	 *
	 * @return true if so
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsIntegrityEnhancementFacility", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsIntegrityEnhancementFacility");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Is some form of outer join supported?
	 *
	 * @return true if so
	 */
	public boolean supportsOuterJoins() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsOuterJoins", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsOuterJoins");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Are full nested outer joins supported?
	 *
	 * @return true if so
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsFullOuterJoins", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsFullOuterJoins");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Nonstop SQL/MX Database provides the limited support for outer joins. This
	 * will be true if supportFullOuterJoins is true.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsLimitedOuterJoins", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsLimitedOuterJoins");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * What's the database vendor's preferred term for "schema"?
	 *
	 * @return the vendor term
	 */
	public String getSchemaTerm() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSchemaTerm", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSchemaTerm");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("SCHEMA");
	}

	/*
	 * What's the database vendor's preferred term for "procedure"?
	 *
	 * @return the vendor term
	 */
	public String getProcedureTerm() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getProcedureTerm", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getProcedureTerm");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("PROCEDURE");
	}

	/*
	 * What's the database vendor's preferred term for "catalog"?
	 *
	 * @return the vendor term
	 */
	public String getCatalogTerm() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getCatalogTerm", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getCatalogTerm");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String("CATALOG");
	}

	/*
	 * Nonstop SQL/MX Database catalog name appear at the start of a qualified table
	 * name. (Otherwise it appears at the end)
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean isCatalogAtStart() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "isCatalogAtStart", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("isCatalogAtStart");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * What's the separator between catalog and table name?
	 *
	 * @return the separator string
	 */
	public String getCatalogSeparator() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getCatalogSeparator", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getCatalogSeparator");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return new String(".");
	}

	/*
	 * Can a schema name be used in a data manipulation statement?
	 *
	 * @return true if so
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsSchemasInDataManipulation", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSchemasInDataManipulation");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a schema name be used in a procedure call statement?
	 *
	 * @return true if so
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsSchemasInProcedureCalls",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSchemasInProcedureCalls");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a schema name be used in a table definition statement?
	 *
	 * @return true if so
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsSchemasInTableDefinitions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSchemasInTableDefinitions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a schema name be used in an index definition statement?
	 *
	 * @return true if so
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsSchemasInIndexDefinitions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSchemasInIndexDefinitions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a schema name be used in a privilege definition statement?
	 *
	 * @return true if so
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsSchemasInPrivilegeDefinitions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSchemasInPrivilegeDefinitions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a catalog name be used in a data manipulation statement?
	 *
	 * @return true if so
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsCatalogsInDataManipulation", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCatalogsInDataManipulation");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a catalog name be used in a procedure call statement?
	 *
	 * @return true if so
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsCatalogsInProcedureCalls",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCatalogsInProcedureCalls");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a catalog name be used in a table definition statement?
	 *
	 * @return true if so
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsCatalogsInTableDefinitions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCatalogsInTableDefinitions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a catalog name be used in an index definition statement?
	 *
	 * @return true if so
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsCatalogsInIndexDefinitions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCatalogsInIndexDefinitions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can a catalog name be used in a privilege definition statement?
	 *
	 * @return true if so
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsCatalogsInPrivilegeDefinitions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCatalogsInPrivilegeDefinitions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is positioned DELETE supported?
	 *
	 * @return true if so
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsPositionedDelete", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsPositionedDelete");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database supports positioned UPDATE supported through the Type
	 * 4 driver.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsPositionedUpdate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsPositionedUpdate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database supports SELECT for UPDATE through the Type 4 driver.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsSelectForUpdate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSelectForUpdate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * NonStop stored procedure calls using the stored procedure escape syntax
	 * are supported.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsStoredProcedures", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsStoredProcedures");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database subqueries in comparison expressions are supported
	 * through the Type 4 driver.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsSubqueriesInComparisons",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSubqueriesInComparisons");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database supports subqueries in 'exists' expressions through
	 * Type 4 driver.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"supportsSubqueriesInExists", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSubqueriesInExists");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database supports subqueries in 'in' statements through the
	 * Type 4 driver.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsSubqueriesInIns", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSubqueriesInIns");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * The subqueries in quantified expressions supported in Nonstop SQL/MX Database
	 * and Type 4 driver.
	 *
	 * @return true always for Type 4 driver.
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsSubqueriesInQuantifieds",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsSubqueriesInQuantifieds");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Nonstop SQL/MX Database supports correlated subqueries.
	 *
	 * A JDBC-Compliant driver always returns true.
	 *
	 * @return true always for the Type 4 driver.
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsCorrelatedSubqueries", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsCorrelatedSubqueries");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is SQL UNION supported?
	 *
	 * @return true if so
	 */
	public boolean supportsUnion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsUnion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsUnion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Is SQL UNION ALL supported?
	 *
	 * @return true if so
	 */
	public boolean supportsUnionAll() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsUnionAll", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsUnionAll");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can cursors remain open across commits?
	 *
	 * @return true if cursors always remain open; false if they might not
	 * remain open
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsOpenCursorsAcrossCommit",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsOpenCursorsAcrossCommit");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Can cursors remain open across rollbacks?
	 *
	 * @return true if cursors always remain open; false if they might not
	 * remain open
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsOpenCursorsAcrossRollback", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsOpenCursorsAcrossRollback");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Can statements remain open across commits?
	 *
	 * @return true if statements always remain open; false if they might not
	 * remain open
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsOpenStatementsAcrossCommit", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsOpenStatementsAcrossCommit");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Can statements remain open across rollbacks?
	 *
	 * @return true if statements always remain open; false if they might not
	 * remain open
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsOpenStatementsAcrossRollback", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsOpenStatementsAcrossRollback");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	// ----------------------------------------------------------------------
	// The following group of methods exposes various limitations
	// based on the target database with the current driver.
	// Unless otherwise specified, a result of zero means there is no
	// limit, or the limit is not known.

	/*
	 * How many hex characters can you have in an inline binary literal?
	 *
	 * @return max literal length
	 */
	public int getMaxBinaryLiteralLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxBinaryLiteralLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxBinaryLiteralLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 4050;
	}

	/*
	 * What's the max length for a character literal?
	 *
	 * @return max literal length
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxCharLiteralLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxCharLiteralLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 4050;
	}

	/*
	 * What's the limit on column name length?
	 *
	 * @return max literal length
	 */
	public int getMaxColumnNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxColumnNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxColumnNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 30;
	}

	/*
	 * What's the maximum number of columns in a "GROUP BY" clause?
	 *
	 * @return max number of columns
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxColumnsInGroupBy", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxColumnsInGroupBy");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum number of columns allowed in an index?
	 *
	 * @return max columns
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxColumnsInIndex", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxColumnsInIndex");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum number of columns in an "ORDER BY" clause?
	 *
	 * @return max columns
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxColumnsInOrderBy", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxColumnsInOrderBy");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum number of columns in a "SELECT" list?
	 *
	 * @return max columns
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxColumnsInSelect", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxColumnsInSelect");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum number of columns in a table?
	 *
	 * @return max columns
	 */
	public int getMaxColumnsInTable() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxColumnsInTable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxColumnsInTable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * How many active connections can we have at a time to this database?
	 *
	 * @return max connections
	 */
	public int getMaxConnections() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxConnections", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxConnections");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum cursor name length?
	 *
	 * @return max cursor name length in bytes * This is defined in jdbc_sqlmp.h
	 * (ID_MAX)
	 */
	public int getMaxCursorNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxCursorNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxCursorNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 30;
	}

	/*
	 * What's the maximum length of an index (in bytes)?
	 *
	 * @return max index length in bytes
	 */
	public int getMaxIndexLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxIndexLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxIndexLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 4050;
	}

	/*
	 * What's the maximum length allowed for a schema name?
	 *
	 * @return max name length in bytes
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxSchemaNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxSchemaNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 30;
	}

	/*
	 * What's the maximum length of a procedure name?
	 *
	 * @return max name length in bytes
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxProcedureNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxProcedureNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 30;
	}

	/*
	 * What's the maximum length of a catalog name?
	 *
	 * @return max name length in bytes
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxCatalogNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxCatalogNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 30;
	}

	/*
	 * What's the maximum length of a single row?
	 *
	 * @return max row size in bytes
	 */
	public int getMaxRowSize() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxRowSize", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxRowSize");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 4050;
	}

	/*
	 * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY blobs?
	 *
	 * @return true if so
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXDatabaseMetaData",
							"doesMaxRowSizeIncludeBlobs", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("doesMaxRowSizeIncludeBlobs");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * What's the maximum length of a SQL statement?
	 *
	 * @return max length in bytes
	 */
	public int getMaxStatementLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxStatementLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxStatementLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * How many active statements can we have open at one time to this database?
	 *
	 * @return the maximum
	 */
	public int getMaxStatements() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxStatements", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxStatements");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum length of a table name?
	 *
	 * @return max name length in bytes
	 */
	public int getMaxTableNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxTableNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxTableNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 30;
	}

	/*
	 * What's the maximum number of tables in a SELECT?
	 *
	 * @return the maximum
	 */
	public int getMaxTablesInSelect() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxTablesInSelect", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxTablesInSelect");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 0;
	}

	/*
	 * What's the maximum length of a user name?
	 *
	 * @return max name length in bytes
	 */
	public int getMaxUserNameLength() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getMaxUserNameLength", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getMaxUserNameLength");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return 32;
	}

	// ----------------------------------------------------------------------

	/*
	 * TRANSACTION_READ_COMMITTED is the default transaction isolation level for
	 * the Type 4 driver. The values are defined in java.sql.Connection.
	 *
	 * @return the default isolation level TRANSACTION_READ_COMMITTED.
	 *
	 * @see Connection
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDefaultTransactionIsolation",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDefaultTransactionIsolation");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return Connection.TRANSACTION_READ_COMMITTED;
	}

	/*
	 * Returns whether transactions are supported. It is true for Type 4 driver.
	 * @return true always since Type supports transactions.
	 */
	public boolean supportsTransactions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsTransactions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsTransactions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Does the database support the given transaction isolation level?
	 *
	 * @param level the values are defined in java.sql.Connection @return true
	 * if so
	 *
	 * @see Connection
	 */
	public boolean supportsTransactionIsolationLevel(int level)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					"supportsTransactionIsolationLevel",connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsTransactionIsolationLevel", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					"supportsTransactionIsolationLevel",connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsTransactionIsolationLevel");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Are both data definition and data manipulation statements within a
	 * transaction supported?
	 *
	 * @return true if so
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsDataDefinitionAndDataManipulationTransactions",
					"", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr
					.setSourceMethodName("supportsDataDefinitionAndDataManipulationTransactions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*
	 * Are only data manipulation statements within a transaction supported?
	 *
	 * @return true if so
	 */
	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"supportsDataManipulationTransactionsOnly", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsDataManipulationTransactionsOnly");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Does a data definition statement within a transaction force the
	 * transaction to commit?
	 *
	 * @return true if so
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"dataDefinitionCausesTransactionCommit", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("dataDefinitionCausesTransactionCommit");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Is a data definition statement within a transaction ignored?
	 *
	 * @return true if so
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData",
					"dataDefinitionIgnoredInTransactions", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("dataDefinitionIgnoredInTransactions");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*
	 * Get a description of stored procedures available in a catalog.
	 *
	 * <P>Only procedure descriptions matching the schema and procedure name
	 * criteria are returned. They are ordered by PROCEDURE_SCHEM, and
	 * PROCEDURE_NAME.
	 *
	 * <P>Each procedure description has the the following columns: <OL> <LI><B>PROCEDURE_CAT</B>
	 * String => procedure catalog (may be null) <LI><B>PROCEDURE_SCHEM</B>
	 * String => procedure schema (may be null) <LI><B>PROCEDURE_NAME</B>
	 * String => procedure name <LI> reserved for future use <LI> reserved for
	 * future use <LI> reserved for future use <LI><B>REMARKS</B> String =>
	 * explanatory comment on the procedure <LI><B>PROCEDURE_TYPE</B> short =>
	 * kind of procedure: <UL> <LI> procedureResultUnknown - May return a result
	 * <LI> procedureNoResult - Does not return a result <LI>
	 * procedureReturnsResult - Returns a result </UL> </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schemaPattern
	 * a schema name pattern; "" retrieves those without a schema @param
	 * procedureNamePattern a procedure name pattern @return ResultSet - each
	 * row is a procedure description
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getProcedures(String catalog,
			String schemaPattern, String procedureNamePattern)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, procedureNamePattern,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getProcedures", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, procedureNamePattern,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getProcedures");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		int i;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;
		}

		// KAS - 2/7/2005 - I'm adding this code per the 1.4.2 specification
		// that says,
		// "catalog - a catalog name; must match the catalog name as it is
		// stored in the
		// database; "" retrieves those without a catalog; null means that the
		// catalog
		// name should not be used to narrow the search
		// schemaPattern - a schema name pattern; must match the schema name as
		// it is
		// stored in the database; "" retrieves those without a schema; null
		// means that
		// the schema name should not be used to narrow the search
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName cannot contain a string search pattern.", so if the
		// catalog name
		// is null, well just pass null to MXCS. MXCS will use the default
		// catalog in that case.
		//
		
		//Soln 10-131202-9921
		String connectionSchema = null;
		connectionSchema = connection_.getSchema();
		if (schemaPattern != null) {
			schemaNm = schemaPattern;
		} else if (connectionSchema == null	|| (connectionSchema != null && connectionSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = "%";
		} else {
			schemaNm = connectionSchema;
		}		
				
		if (procedureNamePattern == null) {

			// KAS - 2/8/2005 - The 1.4.2 specification says, "If a search
			// pattern
			// argument is set to null, that argument's criterion will be
			// dropped from the search."
			procedureNamePattern = "%";

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLPROCEDURES, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				procedureNamePattern, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of a catalog's stored procedure parameters and result
	 * columns.
	 *
	 * <P>Only descriptions matching the schema, procedure and parameter name
	 * criteria are returned. They are ordered by PROCEDURE_SCHEM and
	 * PROCEDURE_NAME. Within this, the return value, if any, is first. Next are
	 * the parameter descriptions in call order. The column descriptions follow
	 * in column number order.
	 *
	 * <P>Each row in the ResultSet is a parameter description or column
	 * description with the following fields: <OL> <LI><B>PROCEDURE_CAT</B>
	 * String => procedure catalog (may be null) <LI><B>PROCEDURE_SCHEM</B>
	 * String => procedure schema (may be null) <LI><B>PROCEDURE_NAME</B>
	 * String => procedure name <LI><B>COLUMN_NAME</B> String =>
	 * column/parameter name <LI><B>COLUMN_TYPE</B> Short => kind of
	 * column/parameter: <UL> <LI> procedureColumnUnknown - nobody knows <LI>
	 * procedureColumnIn - IN parameter <LI> procedureColumnInOut - INOUT
	 * parameter <LI> procedureColumnOut - OUT parameter <LI>
	 * procedureColumnReturn - procedure return value <LI> procedureColumnResult -
	 * result column in ResultSet </UL> <LI><B>DATA_TYPE</B> short => SQL type
	 * from java.sql.Types <LI><B>TYPE_NAME</B> String => SQL type name <LI><B>PRECISION</B>
	 * int => precision <LI><B>LENGTH</B> int => length in bytes of data <LI><B>SCALE</B>
	 * short => scale <LI><B>RADIX</B> short => radix <LI><B>NULLABLE</B>
	 * short => can it contain NULL? <UL> <LI> procedureNoNulls - does not allow
	 * NULL values <LI> procedureNullable - allows NULL values <LI>
	 * procedureNullableUnknown - nullability unknown </UL> <LI><B>REMARKS</B>
	 * String => comment describing parameter/column </OL>
	 *
	 * <P><B>Note:</B> Some databases may not return the column descriptions
	 * for a procedure. Additional columns beyond REMARKS can be defined by the
	 * database.
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schemaPattern
	 * a schema name pattern; "" retrieves those without a schema @param
	 * procedureNamePattern a procedure name pattern @param columnNamePattern a
	 * column name pattern @return ResultSet - each row is a stored procedure
	 * parameter or column description
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getProcedureColumns(String catalog,
			String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, procedureNamePattern,
					columnNamePattern);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getProcedureColumns", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, procedureNamePattern,
					columnNamePattern);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getProcedureColumns");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;
		SQLMXResultSet resultSet;

		int i;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}

		// KAS - 2/7/2005 - I'm adding this code per the 1.4.2 specification
		// that says,
		// "catalog - a catalog name; must match the catalog name as it is
		// stored in the
		// database; "" retrieves those without a catalog; null means that the
		// catalog
		// name should not be used to narrow the search
		// schemaPattern - a schema name pattern; must match the schema name as
		// it is
		// stored in the database; "" retrieves those without a schema; null
		// means that
		// the schema name should not be used to narrow the search
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName cannot contain a string search pattern.", so if the
		// catalog name
		// is null, well just pass null to MXCS. MXCS will use the default
		// catalog in that case.
		//
		
		// Soln 10-131202-9921
		String connectionSchema = null;
		connectionSchema = connection_.getSchema();
		if (schemaPattern != null) {
			schemaNm = schemaPattern;
		} else if (connectionSchema == null	|| (connectionSchema != null && connectionSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = "%";
		} else {
			schemaNm = connectionSchema;
		}
		if (procedureNamePattern == null) {

			// KAS - 2/8/2005 - The 1.4.2 specification says, "If a search
			// pattern argument
			// is set to null, that argument's criterion will be dropped from
			// the search."
			procedureNamePattern = "%";

		}
		if (columnNamePattern == null) {

			// KAS - 2/8/2005 - The 1.4.2 specification says, "If a search
			// pattern argument
			// is set to null, that argument's criterion will be dropped from
			// the search."
			columnNamePattern = "%";

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLPROCEDURECOLUMNS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				procedureNamePattern, // table name
				"", // tableTypeList
				columnNamePattern, // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		resultSet = getResultSet();
		// path column Names as per JDBC specification
		resultSet.setColumnName(8, "PRECISION");
		resultSet.setColumnName(9, "LENGTH");
		resultSet.setColumnName(10, "SCALE");
		resultSet.setColumnName(11, "RADIX");
		return resultSet;
	}

	/*
	 * Get a description of tables available in a catalog.
	 *
	 * <P>Only table descriptions matching the catalog, schema, table name and
	 * type criteria are returned. They are ordered by TABLE_TYPE, TABLE_SCHEM
	 * and TABLE_NAME.
	 *
	 * <P>Each table description has the following columns: <OL> <LI><B>TABLE_CAT</B>
	 * String => table catalog (may be null) <LI><B>TABLE_SCHEM</B> String =>
	 * table schema (may be null) <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>TABLE_TYPE</B> String => table type. Typical types are "TABLE",
	 * "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
	 * "SYNONYM". <LI><B>REMARKS</B> String => explanatory comment on the
	 * table </OL>
	 *
	 * <P><B>Note:</B> Some databases may not return information for all
	 * tables.
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schemaPattern
	 * a schema name pattern; "" retrieves those without a schema @param
	 * tableNamePattern a table name pattern @param types a list of table types
	 * to include; null returns all types @return ResultSet - each row is a
	 * table description
	 *
	 * @see #getSearchStringEscape
	 */

	public java.sql.ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String types[]) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, tableNamePattern, types);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getTables", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, tableNamePattern, types);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getTables");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		StringBuffer tableType;
		String tableTypeList;
		String catalogNm;
		String schemaNm;
		String tableNm;

		int i;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName cannot contain a string search pattern.", so if the
		// catalog name
		// is null, well just pass null to MXCS. MXCS will use the default
		// catalog in that case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}

		// KAS - 2/7/2005 - I'm adding this code per the 1.4.2 specification
		// that says,
		// "catalog - a catalog name; must match the catalog name as it is
		// stored in the
		// database; "" retrieves those without a catalog; null means that the
		// catalog
		// name should not be used to narrow the search
		// schemaPattern - a schema name pattern; must match the schema name as
		// it is
		// stored in the database; "" retrieves those without a schema; null
		// means that
		// the schema name should not be used to narrow the search
		// tableNamePattern - a table name pattern; must match the table name as
		// it is
		// stored in the database
		// types - a list of table types to include; null returns all types

		// Soln 10-131202-9921
		String connectionSchema = null;
		connectionSchema = connection_.getSchema();
		if (schemaPattern != null) {
			schemaNm = schemaPattern;
		} else if (connectionSchema == null || (connectionSchema != null && connectionSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = "%";
		} else {
			schemaNm = connectionSchema;
		}		
		
		if (tableNamePattern == null) {

			// KAS - 2/8/2005 - The 1.4.2 specification says, "If a search
			// pattern argument
			// is set to null, that argument's criterion will be dropped from
			// the search."
			tableNm = "%";
		} else {
			tableNm = tableNamePattern;

		}
		if (types != null) {
			if (types.length != 0) {
				tableType = new StringBuffer(types.length * 10);
				for (i = 0; i < types.length; i++) {
					tableType.append(types[i]);
					tableType.append(',');
				}
				tableTypeList = tableType.toString();
			} else {
				tableTypeList = null;
			}
		} else {
			tableTypeList = null;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLTABLES_JDBC, // catalogAPI - should add SQL_API_JDBC
				catalogNm, // catalog
				schemaNm, // schema
				tableNm, // table name
				tableTypeList, // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get the schema names available in this database. The results are ordered
	 * by schema name.
	 *
	 * <P>The schema column is: <OL> <LI><B>TABLE_SCHEM</B> String => schema
	 * name <LI><B>TABLE_CATALOG String => catalog name (may be null) </OL>
	 *
	 * @return ResultSet - each row has a single String column that is a schema
	 * name
	 */
	public java.sql.ResultSet getSchemas2() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSchemas", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSchemas");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		// 2/18/2005 - KAS
		// Although the JDBC 3.0 spec implies that we return all schemas
		// in this database, the ODBC server can not handle wild cards
		// (i.e. catalog name = "%". So, we will only return the schemas
		// associated with this connection's catalog. If the connection's
		// catalog is null, then we will use the empty string, which
		// will return no schemas.
		//
		// 2/22/2005 - KAS
		// New plan. ODBC will fix the wild card problem, and we will
		// pass "%" to get all schemas.
		//

		String catalog = connection_.getCatalog();
		if (catalog == null)
			catalog = "";

		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				(short) (SQL_API_SQLTABLES_JDBC), // catalogAPI
				"", // catalog
				"%", // schema
				"", // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}
	
	 public java.sql.ResultSet getSchemas() throws SQLException
	  {
	    if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true)
	    {
	      Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
	      connection_.props_.t4Logger_.logp(Level.FINE, "SQLMXDatabaseMetaData", "getSchemas",
	                                        "", p);
	    }
	    if (connection_.props_.getLogWriter() != null)
	    {
	      LogRecord lr = new LogRecord(Level.FINE, "");
	      Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
	      lr.setParameters(p);
	      lr.setSourceClassName("SQLMXDatabaseMetaData");
	      lr.setSourceMethodName("getSchemas");
	      T4LogFormatter lf = new T4LogFormatter();
	      String temp = lf.format(lr);
	      connection_.props_.getLogWriter().println(temp);
	    }

	    clearWarnings();
	    //Begin Soln 10-050413-6643
	    //connection_.getServerHandle().isConnectionOpen();
	    connection_.isConnectionOpen();
	   //End Soln 10-050413-6643


	    // 2/18/2005 - KAS
	    // Although the JDBC 3.0 spec implies that we return all schemas
	    // in this database, the ODBC server can not handle wild cards
	    // (i.e. catalog name = "%". So, we will only return the schemas
	    // associated with this connection's catalog. If the connection's
	    // catalog is null, then we will use the empty string, which
	    // will return no schemas.
	    //
	    // 2/22/2005 - KAS
	    // New plan. ODBC will fix the wild card problem, and we will
	    // pass "%" to get all schemas.
	    //

	    String catalog = connection_.getCatalog();
	    if (catalog == null){
	      catalog = "";
	    }

	    if(connection_.props_.getDbMetaDataforDBTools().equalsIgnoreCase("OFF")){
	    	return getSchemas2();
	    }

	    if (catalog.startsWith("NONSTOP_SYSTEM") ){
	    	ResultSet rs = getCatalogs();
	    	rs.next();
	    	  catalog = rs.getString(1);
	      }

	    SQLMXDesc[] outputDesc = new SQLMXDesc[2];
	    outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0, 128, (short) 0, (short) 0, false, "TABLE_SCHEM",
	            false, Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100, 0, 0);

	    outputDesc[1] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0, 128, (short) 0, (short) 0, false, "TABLE_CATALOG",
	            false, Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100, 0, 0);


	    SQLMXResultSet rs = new SQLMXResultSet(this, outputDesc, "", true);


	    ResultSet rss = getSchemas2();
	    List list = new ArrayList();
	    while(rss.next()){
	    	  	if(rss.getString("TABLE_CATALOG").equalsIgnoreCase(catalog)){
	        	 		 list.add(rss.getString("TABLE_SCHEM"));
	    	  	}
	    }

	     Row[] rows = new Row[list.size()];
	     String[] rowValue = new String[2];

	    	for(int i=0;i<list.size();i++){
	    		rowValue[0]=(String) list.get(i);
	    		rowValue[1]=catalog.toUpperCase();
	    		rows[i] = new Row(2,rowValue);

	       	}

	    rs.setFetchOutputs(rows, rows.length, true);
	    return rs;


	  }


	/*
	 * Get the catalog names available in this database. The results are ordered
	 * by catalog name.
	 *
	 * <P>The catalog column is: <OL> <LI><B>TABLE_CAT</B> String => catalog
	 * name </OL>
	 *
	 * @return ResultSet - each row has a single String column that is a catalog
	 * name
	 */
	public java.sql.ResultSet getCatalogs2() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getCatalogs", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getCatalogs");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				(short) (SQL_API_SQLTABLES_JDBC), // catalogAPI
				"%", // catalog
				"", // schema
				"", // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}
	
	 public java.sql.ResultSet getCatalogs() throws SQLException
	  {
	      if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true)
	      {
	          Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
	          connection_.props_.t4Logger_.logp(Level.FINE, "SQLMXDatabaseMetaData", "getCatalogs", "", p);
	      }
	      if (connection_.props_.getLogWriter() != null)
	      {
	          LogRecord lr = new LogRecord(Level.FINE, "");
	          Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
	          lr.setParameters(p);
	          lr.setSourceClassName("SQLMXDatabaseMetaData");
	          lr.setSourceMethodName("getCatalogs");
	          T4LogFormatter lf = new T4LogFormatter();
	          String temp = lf.format(lr);
	          connection_.props_.getLogWriter().println(temp);
	      }
	      clearWarnings();
	      // Begin Soln 10-050413-6643
	      // connection_.getServerHandle().isConnectionOpen();
	      connection_.isConnectionOpen();
	      // End Soln 10-050413-6643

	      String catalog = connection_.getCatalog().trim();

	      if(connection_.props_.getDbMetaDataforDBTools().equals("OFF")){
	    	  return getCatalogs2();
	      }

	       ResultSet rss = getCatalogs2();
	       String tempCat = null;
	       String defaultCat = "NONSTOP_SQLMX";
	      while(rss.next()){

	  	  	if(rss.getString("TABLE_CAT").equalsIgnoreCase(catalog)){
	  	  		tempCat =rss.getString("TABLE_CAT");
	  	  	}
	  	  	if(rss.getString("TABLE_CAT").startsWith(defaultCat)){
	  	  		defaultCat = rss.getString("TABLE_CAT");

	  	  	}
	      }

	    	  if(tempCat == null){
	    	      	  catalog = defaultCat;
	    	  }else {
	    		  catalog = tempCat;
	    	  }



	      SQLMXDesc[] outputDesc = new SQLMXDesc[1];
	      outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0, 128, (short) 0, (short) 0, false, "TABLE_CAT",
	              false, Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100, 0, 0);

	      SQLMXResultSet rs = new SQLMXResultSet(this, outputDesc, "", true);
	      Row[] rows = new Row[1];
	      String[] rowValue = new String[1];
	      rowValue[0] = catalog;
	      rows[0] = new Row(1, rowValue);

	      rs.setFetchOutputs(rows, rows.length, true);

	      return rs;
	  }


	/*
	 * Get the table types available in this database. The results are ordered
	 * by table type.
	 *
	 * <P>The table type is: <OL> <LI><B>TABLE_TYPE</B> String => table type.
	 * Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
	 * "LOCAL TEMPORARY", "ALIAS", "SYNONYM". </OL>
	 *
	 * @return ResultSet - each row has a single String column that is a table
	 * type
	 */
	public java.sql.ResultSet getTableTypes() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getTableTypes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getTableTypes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXResultSet resultSet;
		SQLMXDesc[] outputDesc;
		Row[] rows;
		String[] rowValue;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		outputDesc = new SQLMXDesc[1];
		outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TABLE_TYPE", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);

		resultSet = new SQLMXResultSet(this, outputDesc, "", true);
		rows = new Row[5];

		// Populate the rows
		rowValue = new String[1];

		rowValue[0] = new String("SYSTEM TABLE");
		rows[0] = new Row(1, rowValue);

		rowValue[0] = new String("TABLE");
		rows[1] = new Row(1, rowValue);

		rowValue[0] = new String("VIEW");
		rows[2] = new Row(1, rowValue);

		//Modified for SQL/MX 3.5 Materialized View Feature
		rowValue[0] = new String("MATERIALIZED VIEW"); 
		rows[3] = new Row(1, rowValue);
		
		//Modified for SQL/MX 3.6 Synonym Feature
		rowValue[0] = new String("SYNONYM"); 
		rows[4] = new Row(1, rowValue);

		if (((getDatabaseMajorVersion() == 3) && (getDatabaseMinorVersion() >= 6)) || (getDatabaseMajorVersion() > 3)) {
			resultSet.setFetchOutputs(rows, 5, true);
		}
		else if (((getDatabaseMajorVersion() == 3) && (getDatabaseMinorVersion() >= 5)) || (getDatabaseMajorVersion() > 3)) {
			resultSet.setFetchOutputs(rows, 4, true);
		}
		else{
			resultSet.setFetchOutputs(rows, 3, true);
		}

		return resultSet;
	}

	/*
	 * Get a description of table columns available in a catalog.
	 *
	 * <P>Only column descriptions matching the catalog, schema, table and
	 * column name criteria are returned. They are ordered by TABLE_SCHEM,
	 * TABLE_NAME and ORDINAL_POSITION.
	 *
	 * <P>Each column description has the following columns: <OL> <LI><B>TABLE_CAT</B>
	 * String => table catalog (may be null) <LI><B>TABLE_SCHEM</B> String =>
	 * table schema (may be null) <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>COLUMN_NAME</B> String => column name <LI><B>DATA_TYPE</B>
	 * short => SQL type from java.sql.Types <LI><B>TYPE_NAME</B> String =>
	 * Data source dependent type name <LI><B>COLUMN_SIZE</B> int => column
	 * size. For char or date types this is the maximum number of characters,
	 * for numeric or decimal types this is precision. <LI><B>BUFFER_LENGTH</B>
	 * is not used. <LI><B>DECIMAL_DIGITS</B> int => the number of fractional
	 * digits <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or
	 * 2) <LI><B>NULLABLE</B> int => is NULL allowed? <UL> <LI> columnNoNulls -
	 * might not allow NULL values <LI> columnNullable - definitely allows NULL
	 * values <LI> columnNullableUnknown - nullability unknown </UL> <LI><B>REMARKS</B>
	 * String => comment describing column (may be null) <LI><B>COLUMN_DEF</B>
	 * String => default value (may be null) <LI><B>SQL_DATA_TYPE</B> int =>
	 * unused <LI><B>SQL_DATETIME_SUB</B> int => unused <LI><B>CHAR_OCTET_LENGTH</B>
	 * int => for char types the maximum number of bytes in the column <LI><B>ORDINAL_POSITION</B>
	 * int => index of column in table (starting at 1) <LI><B>IS_NULLABLE</B>
	 * String => "NO" means column definitely does not allow NULL values; "YES"
	 * means the column might allow NULL values. An empty string means nobody
	 * knows. </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schemaPattern
	 * a schema name pattern; "" retrieves those without a schema @param
	 * tableNamePattern a table name pattern @param columnNamePattern a column
	 * name pattern @return ResultSet - each row is a column description
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, catalog, schemaPattern,
							tableNamePattern, columnNamePattern);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getColumns", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, catalog, schemaPattern,
							tableNamePattern, columnNamePattern);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getColumns");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;
		String tableNm;
		String columnNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		// Soln 10-131202-9921
		String connectionSchema = null;
		connectionSchema = connection_.getSchema();
		if (schemaPattern != null) {
			schemaNm = schemaPattern;
		} else if (connectionSchema == null
				|| (connectionSchema != null && connectionSchema
						.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = "%";
		} else {
			schemaNm = connectionSchema;
		}
				
		// KAS - 2/7/2005 - I'm adding this code per the 1.4.2 specification
		// that says,
		// catalog - a catalog name; must match the catalog name as it is stored
		// in
		// the database; "" retrieves those without a catalog; null means that
		// the
		// catalog name should not be used to narrow the search
		// schemaPattern - a schema name pattern; must match the schema name as
		// it
		// is stored in the database; "" retrieves those without a schema; null
		// means
		// that the schema name should not be used to narrow the search
		// tableNamePattern - a table name pattern; must match the table name as
		// it is stored in the database
		// columnNamePattern - a column name pattern; must match the column name
		// as
		// it is stored in the database
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName cannot contain a string search pattern.", so if the
		// catalog name
		// is null, well just pass null to MXCS. MXCS will use the default
		// catalog in that case.
		//
		

		if (tableNamePattern == null) {
			// KAS - 2/8/2005 - The 1.4.2 specification says, "If a search
			// pattern argument
			// is set to null, that argument's criterion will be dropped from
			// the search."
			tableNm = "%";
		} else {
			tableNm = tableNamePattern;
		}
		if (columnNamePattern == null) {
			// KAS - 2/8/2005 - The 1.4.2 specification says, "If a search
			// pattern argument
			// is set to null, that argument's criterion will be dropped from
			// the search."
			columnNm = "%";
		} else {
			columnNm = columnNamePattern;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLCOLUMNS_JDBC, // catalogAPI- should be _JDBC
				catalogNm, // catalog
				schemaNm, // schema
				tableNm, // table name
				"", // tableTypeList
				columnNm, // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of the access rights for a table's columns.
	 *
	 * <P>Only privileges matching the column name criteria are returned. They
	 * are ordered by COLUMN_NAME and PRIVILEGE.
	 *
	 * <P>Each privilige description has the following columns: <OL> <LI><B>TABLE_CAT</B>
	 * String => table catalog (may be null) <LI><B>TABLE_SCHEM</B> String =>
	 * table schema (may be null) <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>COLUMN_NAME</B> String => column name <LI><B>GRANTOR</B> =>
	 * grantor of access (may be null) <LI><B>GRANTEE</B> String => grantee of
	 * access <LI><B>PRIVILEGE</B> String => name of access (SELECT, INSERT,
	 * UPDATE, REFRENCES, ...) <LI><B>IS_GRANTABLE</B> String => "YES" if
	 * grantee is permitted to grant to others; "NO" if not; null if unknown
	 * </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schema a
	 * schema name; "" retrieves those without a schema @param table a table
	 * name @param columnNamePattern a column name pattern @return ResultSet -
	 * each row is a column privilege description
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getColumnPrivileges(String catalog,
			String schema, String table, String columnNamePattern)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table, columnNamePattern);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getColumnPrivileges", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table, columnNamePattern);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getColumnPrivileges");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;
		String columnNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_tableName", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;
		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}
		if (columnNamePattern == null) {
			columnNm = "%";
		} else {
			columnNm = columnNamePattern;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLCOLUMNPRIVILEGES, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				columnNm, // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of the access rights for each table available in a
	 * catalog. Note that a table privilege applies to one or more columns in
	 * the table. It would be wrong to assume that this priviledge applies to
	 * all columns (this may be true for some systems but is not true for all.)
	 *
	 * <P>Only privileges matching the schema and table name criteria are
	 * returned. They are ordered by TABLE_SCHEM, TABLE_NAME, and PRIVILEGE.
	 *
	 * <P>Each privilige description has the following columns: <OL> <LI><B>TABLE_CAT</B>
	 * String => table catalog (may be null) <LI><B>TABLE_SCHEM</B> String =>
	 * table schema (may be null) <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>GRANTOR</B> => grantor of access (may be null) <LI><B>GRANTEE</B>
	 * String => grantee of access <LI><B>PRIVILEGE</B> String => name of
	 * access (SELECT, INSERT, UPDATE, REFRENCES, ...) <LI><B>IS_GRANTABLE</B>
	 * String => "YES" if grantee is permitted to grant to others; "NO" if not;
	 * null if unknown </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schemaPattern
	 * a schema name pattern; "" retrieves those without a schema @param
	 * tableNamePattern a table name pattern @return ResultSet - each row is a
	 * table privilege description
	 *
	 * @see #getSearchStringEscape
	 */
	public java.sql.ResultSet getTablePrivileges(String catalog,
			String schemaPattern, String tableNamePattern) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, tableNamePattern,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getTablePrivileges", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, tableNamePattern,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getTablePrivileges");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;
		String tableNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName cannot contain a string search
		// pattern.", so if the catalog name is null, we'll
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schemaPattern == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schemaPattern;

		}
		if (tableNamePattern == null) {
			tableNm = "%";
		} else {
			tableNm = tableNamePattern;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLTABLEPRIVILEGES, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				tableNm, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of a table's optimal set of columns that uniquely
	 * identifies a row. They are ordered by SCOPE.
	 *
	 * <P>Each column description has the following columns: <OL> <LI><B>SCOPE</B>
	 * short => actual scope of result <UL> <LI> bestRowTemporary - very
	 * temporary, while using row <LI> bestRowTransaction - valid for remainder
	 * of current transaction <LI> bestRowSession - valid for remainder of
	 * current session </UL> <LI><B>COLUMN_NAME</B> String => column name <LI><B>DATA_TYPE</B>
	 * short => SQL data type from java.sql.Types <LI><B>TYPE_NAME</B> String =>
	 * Data source dependent type name <LI><B>COLUMN_SIZE</B> int => precision
	 * <LI><B>BUFFER_LENGTH</B> int => not used <LI><B>DECIMAL_DIGITS</B>
	 * short => scale <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo
	 * column like an Oracle ROWID <UL> <LI> bestRowUnknown - may or may not be
	 * pseudo column <LI> bestRowNotPseudo - is NOT a pseudo column <LI>
	 * bestRowPseudo - is a pseudo column </UL> </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schema a
	 * schema name; "" retrieves those without a schema @param table a table
	 * name @param scope the scope of interest; use same values as SCOPE @param
	 * nullable include columns that are nullable? @return ResultSet - each row
	 * is a column description
	 */
	public java.sql.ResultSet getBestRowIdentifier(String catalog,
			String schema, String table, int scope, boolean nullable)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table, scope, nullable);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getBestRowIdentifier", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table, scope, nullable);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getBestRowIdentifier");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_use_of_null", null);
		}

		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}
		long nullableODBC;
		if (nullable) {
			nullableODBC = SQL_NULLABLE;
		} else {
			nullableODBC = SQL_NO_NULLS;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLSPECIALCOLUMNS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				"", // cloumn name
				SQL_BEST_ROWID, // cloumnType
				scope, // rowIdScope
				nullableODBC, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of a table's columns that are automatically updated
	 * when any value in a row is updated. They are unordered.
	 *
	 * <P>Each column description has the following columns: <OL> <LI><B>SCOPE</B>
	 * short => is not used <LI><B>COLUMN_NAME</B> String => column name <LI><B>DATA_TYPE</B>
	 * short => SQL data type from java.sql.Types <LI><B>TYPE_NAME</B> String =>
	 * Data source dependent type name <LI><B>COLUMN_SIZE</B> int => precision
	 * <LI><B>BUFFER_LENGTH</B> int => length of column value in bytes <LI><B>DECIMAL_DIGITS</B>
	 * short => scale <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo
	 * column like an Oracle ROWID <UL> <LI> versionColumnUnknown - may or may
	 * not be pseudo column <LI> versionColumnNotPseudo - is NOT a pseudo column
	 * <LI> versionColumnPseudo - is a pseudo column </UL> </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schema a
	 * schema name; "" retrieves those without a schema @param table a table
	 * name @return ResultSet - each row is a column description
	 */
	public java.sql.ResultSet getVersionColumns(String catalog, String schema,
			String table) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getVersionColumns", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getVersionColumns");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_use_of_null", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLSPECIALCOLUMNS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				"", // cloumn name
				SQL_ROWVER, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of a table's primary key columns. They are ordered by
	 * COLUMN_NAME.
	 *
	 * <P>Each primary key column description has the following columns: <OL>
	 * <LI><B>TABLE_CAT</B> String => table catalog (may be null) <LI><B>TABLE_SCHEM</B>
	 * String => table schema (may be null) <LI><B>TABLE_NAME</B> String =>
	 * table name <LI><B>COLUMN_NAME</B> String => column name <LI><B>KEY_SEQ</B>
	 * short => sequence number within primary key <LI><B>PK_NAME</B> String =>
	 * primary key name (may be null) </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catjava -Dt4sqlmx.connectionTimout=0 test.junk5 cancun
	 * 38000alog name from the selection criteria @param schema a schema name
	 * pattern; "" retrieves those without a schema @param table a table name
	 * @return ResultSet - each row is a primary key column description
	 */
	public java.sql.ResultSet getPrimaryKeys(String catalog, String schema,
			String table) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getPrimaryKeys", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getPrimaryKeys");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_use_of_null", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}

		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLPRIMARYKEYS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of the primary key columns that are referenced by a
	 * table's foreign key columns (the primary keys imported by a table). They
	 * are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>Each primary key column description has the following columns: <OL>
	 * <LI><B>PKTABLE_CAT</B> String => primary key table catalog being
	 * imported (may be null) <LI><B>PKTABLE_SCHEM</B> String => primary key
	 * table schema being imported (may be null) <LI><B>PKTABLE_NAME</B>
	 * String => primary key table name being imported <LI><B>PKCOLUMN_NAME</B>
	 * String => primary key column name being imported <LI><B>FKTABLE_CAT</B>
	 * String => foreign key table catalog (may be null) <LI><B>FKTABLE_SCHEM</B>
	 * String => foreign key table schema (may be null) <LI><B>FKTABLE_NAME</B>
	 * String => foreign key table name <LI><B>FKCOLUMN_NAME</B> String =>
	 * foreign key column name <LI><B>KEY_SEQ</B> short => sequence number
	 * within foreign key <LI><B>UPDATE_RULE</B> short => What happens to
	 * foreign key when primary is updated: <UL> <LI> importedNoAction - do not
	 * allow update of primary key if it has been imported <LI>
	 * importedKeyCascade - change imported key to agree with primary key update
	 * <LI> importedKeySetNull - change imported key to NULL if its primary key
	 * has been updated <LI> importedKeySetDefault - change imported key to
	 * default values if its primary key has been updated <LI>
	 * importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x
	 * compatibility) </UL> <LI><B>DELETE_RULE</B> short => What happens to
	 * the foreign key when primary is deleted. <UL> <LI> importedKeyNoAction -
	 * do not allow delete of primary key if it has been imported <LI>
	 * importedKeyCascade - delete rows that import a deleted key <LI>
	 * importedKeySetNull - change imported key to NULL if its primary key has
	 * been deleted <LI> importedKeyRestrict - same as importedKeyNoAction (for
	 * ODBC 2.x compatibility) <LI> importedKeySetDefault - change imported key
	 * to default if its primary key has been deleted </UL> <LI><B>FK_NAME</B>
	 * String => foreign key name (may be null) <LI><B>PK_NAME</B> String =>
	 * primary key name (may be null) <LI><B>DEFERRABILITY</B> short => can
	 * the evaluation of foreign key constraints be deferred until commit <UL>
	 * <LI> importedKeyInitiallyDeferred - see SQL92 for definition <LI>
	 * importedKeyInitiallyImmediate - see SQL92 for definition <LI>
	 * importedKeyNotDeferrable - see SQL92 for definition </UL> </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schema a
	 * schema name pattern; "" retrieves those without a schema @param table a
	 * table name @return ResultSet - each row is a primary key column
	 * description
	 *
	 * @see #getExportedKeys
	 */
	public java.sql.ResultSet getImportedKeys(String catalog, String schema,
			String table) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getImportedKeys", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getImportedKeys");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "table_cannot_be_null", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}

		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLFOREIGNKEYS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				"", // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				catalogNm, // fcatalog
				schemaNm, // fschema
				table // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of the foreign key columns that reference a table's
	 * primary key columns (the foreign keys exported by a table). They are
	 * ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>Each foreign key column description has the following columns: <OL>
	 * <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be
	 * null) <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may
	 * be null) <LI><B>PKTABLE_NAME</B> String => primary key table name <LI><B>PKCOLUMN_NAME</B>
	 * String => primary key column name <LI><B>FKTABLE_CAT</B> String =>
	 * foreign key table catalog (may be null) being exported (may be null) <LI><B>FKTABLE_SCHEM</B>
	 * String => foreign key table schema (may be null) being exported (may be
	 * null) <LI><B>FKTABLE_NAME</B> String => foreign key table name being
	 * exported <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
	 * being exported <LI><B>KEY_SEQ</B> short => sequence number within
	 * foreign key <LI><B>UPDATE_RULE</B> short => What happens to foreign key
	 * when primary is updated: <UL> <LI> importedNoAction - do not allow update
	 * of primary key if it has been imported <LI> importedKeyCascade - change
	 * imported key to agree with primary key update <LI> importedKeySetNull -
	 * change imported key to NULL if its primary key has been updated <LI>
	 * importedKeySetDefault - change imported key to default values if its
	 * primary key has been updated <LI> importedKeyRestrict - same as
	 * importedKeyNoAction (for ODBC 2.x compatibility) </UL> <LI><B>DELETE_RULE</B>
	 * short => What happens to the foreign key when primary is deleted. <UL>
	 * <LI> importedKeyNoAction - do not allow delete of primary key if it has
	 * been imported <LI> importedKeyCascade - delete rows that import a deleted
	 * key <LI> importedKeySetNull - change imported key to NULL if its primary
	 * key has been deleted <LI> importedKeyRestrict - same as
	 * importedKeyNoAction (for ODBC 2.x compatibility) <LI>
	 * importedKeySetDefault - change imported key to default if its primary key
	 * has been deleted </UL> <LI><B>FK_NAME</B> String => foreign key name
	 * (may be null) <LI><B>PK_NAME</B> String => primary key name (may be
	 * null) <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign
	 * key constraints be deferred until commit <UL> <LI>
	 * importedKeyInitiallyDeferred - see SQL92 for definition <LI>
	 * importedKeyInitiallyImmediate - see SQL92 for definition <LI>
	 * importedKeyNotDeferrable - see SQL92 for definition </UL> </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schema a
	 * schema name pattern; "" retrieves those without a schema @param table a
	 * table name @return ResultSet - each row is a foreign key column
	 * description
	 *
	 * @see #getImportedKeys
	 */
	public java.sql.ResultSet getExportedKeys(String catalog, String schema,
			String table) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getExportedKeys", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getExportedKeys");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "table_cannot_be_null", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;

		}
		
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}

		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLFOREIGNKEYS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of the foreign key columns in the foreign key table
	 * that reference the primary key columns of the primary key table (describe
	 * how one table imports another's key). This should normally return a
	 * single foreign key/primary key pair (most tables only import a foreign
	 * key from a table once.) They are ordered by FKTABLE_CAT, FKTABLE_SCHEM,
	 * FKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>Each foreign key column description has the following columns: <OL>
	 * <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be
	 * null) <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may
	 * be null) <LI><B>PKTABLE_NAME</B> String => primary key table name <LI><B>PKCOLUMN_NAME</B>
	 * String => primary key column name <LI><B>FKTABLE_CAT</B> String =>
	 * foreign key table catalog (may be null) being exported (may be null) <LI><B>FKTABLE_SCHEM</B>
	 * String => foreign key table schema (may be null) being exported (may be
	 * null) <LI><B>FKTABLE_NAME</B> String => foreign key table name being
	 * exported <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
	 * being exported <LI><B>KEY_SEQ</B> short => sequence number within
	 * foreign key <LI><B>UPDATE_RULE</B> short => What happens to foreign key
	 * when primary is updated: <UL> <LI> importedNoAction - do not allow update
	 * of primary key if it has been imported <LI> importedKeyCascade - change
	 * imported key to agree with primary key update <LI> importedKeySetNull -
	 * change imported key to NULL if its primary key has been updated <LI>
	 * importedKeySetDefault - change imported key to default values if its
	 * primary key has been updated <LI> importedKeyRestrict - same as
	 * importedKeyNoAction (for ODBC 2.x compatibility) </UL> <LI><B>DELETE_RULE</B>
	 * short => What happens to the foreign key when primary is deleted. <UL>
	 * <LI> importedKeyNoAction - do not allow delete of primary key if it has
	 * been imported <LI> importedKeyCascade - delete rows that import a deleted
	 * key <LI> importedKeySetNull - change imported key to NULL if its primary
	 * key has been deleted <LI> importedKeyRestrict - same as
	 * importedKeyNoAction (for ODBC 2.x compatibility) <LI>
	 * importedKeySetDefault - change imported key to default if its primary key
	 * has been deleted </UL> <LI><B>FK_NAME</B> String => foreign key name
	 * (may be null) <LI><B>PK_NAME</B> String => primary key name (may be
	 * null) <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign
	 * key constraints be deferred until commit <UL> <LI>
	 * importedKeyInitiallyDeferred - see SQL92 for definition <LI>
	 * importedKeyInitiallyImmediate - see SQL92 for definition <LI>
	 * importedKeyNotDeferrable - see SQL92 for definition </UL> </OL>
	 *
	 * @param primaryCatalog a catalog name; "" retrieves those without a
	 * catalog; null means drop catalog name from the selection criteria @param
	 * primarySchema a schema name pattern; "" retrieves those without a schema
	 * @param primaryTable the table name that exports the key @param
	 * foreignCatalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param foreignSchema
	 * a schema name pattern; "" retrieves those without a schema @param
	 * foreignTable the table name that imports the key @return ResultSet - each
	 * row is a foreign key column description
	 *
	 * @see #getImportedKeys
	 */
	public java.sql.ResultSet getCrossReference(String primaryCatalog,
			String primarySchema, String primaryTable, String foreignCatalog,
			String foreignSchema, String foreignTable) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					primaryCatalog, primarySchema, primaryTable,
					foreignCatalog, foreignSchema, foreignTable);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getCrossReference", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					primaryCatalog, primarySchema, primaryTable,
					foreignCatalog, foreignSchema, foreignTable);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getCrossReference");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm, fcatalogNm;
		String schemaNm, fschemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (primaryTable == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "primaryTable_cannot_be_null",
					null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		// The same thing applies to the foreign* parameters.
		if (foreignTable == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "foreigntable_cannot_be_null",
					null);
		}

		if (primaryCatalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = primaryCatalog;

		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (primarySchema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = primarySchema;

		}
		if (foreignCatalog == null) {
			fcatalogNm = connection_.getCatalog();
		} else {
			fcatalogNm = foreignCatalog;

		}
		//Soln 10-131202-9921
		String tmpSchema1=null;
		tmpSchema1=connection_.getSchema();
		if (foreignSchema == null && tmpSchema1!=null && !(tmpSchema1.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			fschemaNm = tmpSchema1;
		} else {
			fschemaNm = foreignSchema;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLFOREIGNKEYS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				primaryTable, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				fcatalogNm, // fcatalog
				fschemaNm, // fschema
				foreignTable // ftable
		);

		return getResultSet();
	}

	/*
	 * Get a description of all the standard SQL types supported by this
	 * database. They are ordered by DATA_TYPE and then by how closely the data
	 * type maps to the corresponding JDBC SQL type.
	 *
	 * <P>Each type description has the following columns: <OL> <LI><B>TYPE_NAME</B>
	 * String => Type name <LI><B>DATA_TYPE</B> short => SQL data type from
	 * java.sql.Types <LI><B>PRECISION</B> int => maximum precision <LI><B>LITERAL_PREFIX</B>
	 * String => prefix used to quote a literal (may be null) <LI><B>LITERAL_SUFFIX</B>
	 * String => suffix used to quote a literal (may be null) <LI><B>CREATE_PARAMS</B>
	 * String => parameters used in creating the type (may be null) <LI><B>NULLABLE</B>
	 * short => can you use NULL for this type? <UL> <LI> typeNoNulls - does not
	 * allow NULL values <LI> typeNullable - allows NULL values <LI>
	 * typeNullableUnknown - nullability unknown </UL> <LI><B>CASE_SENSITIVE</B>
	 * boolean=> is it case sensitive? <LI><B>SEARCHABLE</B> short => can you
	 * use "WHERE" based on this type: <UL> <LI> typePredNone - No support <LI>
	 * typePredChar - Only supported with WHERE .. LIKE <LI> typePredBasic -
	 * Supported except for WHERE .. LIKE <LI> typeSearchable - Supported for
	 * all WHERE .. </UL> <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it
	 * unsigned? <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money
	 * value? <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an
	 * auto-increment value? <LI><B>LOCAL_TYPE_NAME</B> String => localized
	 * version of type name (may be null) <LI><B>MINIMUM_SCALE</B> short =>
	 * minimum scale supported <LI><B>MAXIMUM_SCALE</B> short => maximum scale
	 * supported <LI><B>SQL_DATA_TYPE</B> int => unused <LI><B>SQL_DATETIME_SUB</B>
	 * int => unused <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10 </OL>
	 *
	 * @return ResultSet - each row is a SQL type description
	 */
	public java.sql.ResultSet getTypeInfo() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getTypeInfo", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getTypeInfo");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLGETTYPEINFO, // catalogAPI
				"", // catalog
				"", // schema
				"", // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		// Patch the column names as per JDBC specification
		resultSet_.setColumnName(3, "PRECISION");
		resultSet_.setColumnName(12, "AUTO_INCREMENT");
		return getResultSet();
	}

	/*
	 * Get a description of a table's indices and statistics. They are ordered
	 * by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
	 *
	 * <P>Each index column description has the following columns: <OL> <LI><B>TABLE_CAT</B>
	 * String => table catalog (may be null) <LI><B>TABLE_SCHEM</B> String =>
	 * table schema (may be null) <LI><B>TABLE_NAME</B> String => table name
	 * <LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique? false
	 * when TYPE is tableIndexStatistic <LI><B>INDEX_QUALIFIER</B> String =>
	 * index catalog (may be null); null when TYPE is tableIndexStatistic <LI><B>INDEX_NAME</B>
	 * String => index name; null when TYPE is tableIndexStatistic <LI><B>TYPE</B>
	 * short => index type: <UL> <LI> tableIndexStatistic - this identifies
	 * table statistics that are returned in conjuction with a table's index
	 * descriptions <LI> tableIndexClustered - this is a clustered index <LI>
	 * tableIndexHashed - this is a hashed index <LI> tableIndexOther - this is
	 * some other style of index </UL> <LI><B>ORDINAL_POSITION</B> short =>
	 * column sequence number within index; zero when TYPE is
	 * tableIndexStatistic <LI><B>COLUMN_NAME</B> String => column name; null
	 * when TYPE is tableIndexStatistic <LI><B>ASC_OR_DESC</B> String =>
	 * column sort sequence, "A" => ascending, "D" => descending, may be null if
	 * sort sequence is not supported; null when TYPE is tableIndexStatistic
	 * <LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then
	 * this is the number of rows in the table; otherwise, it is the number of
	 * unique values in the index. <LI><B>PAGES</B> int => When TYPE is
	 * tableIndexStatisic then this is the number of pages used for the table,
	 * otherwise it is the number of pages used for the current index. <LI><B>FILTER_CONDITION</B>
	 * String => Filter condition, if any. (may be null) </OL>
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog; null
	 * means drop catalog name from the selection criteria @param schema a
	 * schema name pattern; "" retrieves those without a schema @param table a
	 * table name @param unique when true, return only indices for unique
	 * values; when false, return indices regardless of whether unique or not
	 * @param approximate when true, result is allowed to reflect approximate or
	 * out of data values; when false, results are requested to be accurate
	 * @return ResultSet - each row is an index column description
	 */
	public java.sql.ResultSet getIndexInfo(String catalog, String schema,
			String table, boolean unique, boolean approximate)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table, unique, approximate);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getIndexInfo", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table, unique, approximate);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getIndexInfo");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_use_of_null", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;
		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}
		int uniqueness;
		if (unique) {
			uniqueness = SQL_INDEX_UNIQUE;
		} else {
			uniqueness = SQL_INDEX_ALL;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				SQL_API_SQLSTATISTICS, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				uniqueness, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	// dbscripts_mv_synonym
	/**
	 * Get a description of a table's synonyms.
	 *
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @return ResultSet - each row is synonym information
	 */
	public java.sql.ResultSet getSynonymInfo(String catalog, String schema,
			String table) throws SQLException {
		return getGenericCatInfo(catalog, schema, table, "getSynonymInfo",
				SQL_API_TBLSYNONYM);
	}

	/**
	 * Get a description of a table's materialized views.
	 *
	 * @param catalog
	 *            a catalog name; "" retrieves those without a catalog; null
	 *            means drop catalog name from the selection criteria
	 * @param schema
	 *            a schema name pattern; "" retrieves those without a schema
	 * @param table
	 *            a table name
	 * @return ResultSet - each row is materialized view information
	 */
	public java.sql.ResultSet getMaterializedViewInfo(String catalog,
			String schema, String table) throws SQLException {
		return getGenericCatInfo(catalog, schema, table,
				"getMaterializedViewInfo", SQL_API_TBLMVS);
	}

	private java.sql.ResultSet getGenericCatInfo(String catalog, String schema,
			String table, String method, short apiType) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", method, "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schema, table,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName(method);
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String catalogNm;
		String schemaNm;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		if (table == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_use_of_null", null);
		}
		//
		// Also note that MXCS does not support "%". The ODBC standard says,
		// "CatalogName, SchemaName, and TableName cannot contain a string
		// search
		// pattern.", so if the catalog, schema or table name is null, well
		// just pass null to MXCS. MXCS will use the default catalog in that
		// case.
		//
		if (catalog == null) {
			catalogNm = connection_.getCatalog();
		} else {
			catalogNm = catalog;
		}
		//Soln 10-131202-9921
		String tmpSchema=null;
		tmpSchema=connection_.getSchema();
		if (schema == null && tmpSchema!=null && !(tmpSchema.equalsIgnoreCase("PUBLIC_ACCESS_SCHEMA"))) {
			schemaNm = tmpSchema;
		} else {
			schemaNm = schema;

		}
		getSQLCatalogsInfo(connection_.getServerHandle(), // Server Handle
				apiType, // catalogAPI
				catalogNm, // catalog
				schemaNm, // schema
				table, // table name
				"", // tableTypeList
				"", // cloumn name
				(int) 0, // cloumnType
				(int) 0, // rowIdScope
				(long) 0, // nullable
				(int) 0, // uniqueness
				(int) 0, // accuracy
				(short) 0, // sqlType
				(int) 0, // metadataId
				"", // fcatalog
				"", // fschema
				"" // ftable
		);

		return getResultSet();
	}

	/*------------------------------------------------------------
	 * JDBC 2.0
	 *-----------------------------------------------------------*/
	/*------------------------------------------------------------
	 * boolean deletesAreDetected(int type)
	 *------------------------------------------------------------*/
	public boolean deletesAreDetected(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "deleteAreDetected", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("deleteAreDetected");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		//10-200206-1737 For TYPE_FORWARD_ONLY cursor the method will return True
		//as the cursor will leave a hole(invalid row) after deleteRow is called 
		//rowDeleted detects that hole and returns True.
		if(type==ResultSet.TYPE_FORWARD_ONLY) return true;
		return false;
	}

	/*----------------------------------------------------------------
	 * Connection getConnection()
	 *----------------------------------------------------------------*/
	public java.sql.Connection getConnection() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getConnection", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getConnection");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return (connection_);
	}

	/*--------------------------------------------------------------
	 * ResultSet getUDTs(String catalog, String schemaPattern,
	 *				String typeNamePattern, int[] types)
	 *--------------------------------------------------------------*/
	public java.sql.ResultSet getUDTs(String catalog, String schemaPattern,
			String typeNamePattern, int[] types) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, typeNamePattern, types);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getUDTs", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, typeNamePattern, types);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getUDTs");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXResultSet resultSet;
		SQLMXDesc[] outputDesc;
		Row[] rows;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		outputDesc = new SQLMXDesc[7];
		outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_CAT", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[1] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_SCHEM", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[2] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[3] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "CLASS_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[4] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "DATA_TYPE", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[5] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "REMARKS", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[6] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_SMALLINT, (short) 0,
				2, (short) 0, (short) 0, false, "BASE_TYPE", false,
				Types.SMALLINT, (short) 0, (short) 0, 0, null, null, null, 130,
				0, 0);

		resultSet = new SQLMXResultSet(this, outputDesc, "", true);
		rows = new Row[0];

		// Populate the rows
		resultSet.setFetchOutputs(rows, 0, true);
		return resultSet;
	}

	/*------------------------------------------------------------
	 * boolean insertsAreDetected(int type)
	 *------------------------------------------------------------*/
	public boolean insertsAreDetected(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "insertsAreDetected", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("insertsAreDetected");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*------------------------------------------------------------
	 * boolean updatesAreDetected(int type)
	 *------------------------------------------------------------*/
	public boolean updatesAreDetected(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "updatesAreDetected", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("updatesAreDetected");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*------------------------------------------------------------
	 * boolean othersDeletesAreVisible(int type)
	 *------------------------------------------------------------*/
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "type", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("type");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*------------------------------------------------------------
	 * boolean othersInsertsAreVisible(int type)
	 *------------------------------------------------------------*/
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "othersInsertsAreVisible", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("othersInsertsAreVisible");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*------------------------------------------------------------
	 * boolean othersUpdatesAreVisible(int type)
	 *------------------------------------------------------------*/
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "othersUpdatesAreVisible", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("othersUpdatesAreVisible");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	/*------------------------------------------------------------
	 * boolean ownDeletesAreVisible(int type)
	 *------------------------------------------------------------*/
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "ownDeletesAreVisible", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("ownDeletesAreVisible");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*------------------------------------------------------------
	 * boolean ownInsertsAreVisible(int type)
	 *------------------------------------------------------------*/
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "ownInsertsAreVisible", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("ownInsertsAreVisible");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*------------------------------------------------------------
	 * boolean ownUpdatesAreVisible(int type)
	 *------------------------------------------------------------*/
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "ownUpdatesAreVisible", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("ownUpdatesAreVisible");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*-----------------------------------------------------------
	 * boolean supportsBatchUpdates()
	 *-----------------------------------------------------------*/
	public boolean supportsBatchUpdates() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsBatchUpdates", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsBatchUpdates");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	/*------------------------------------------------------------
	 * boolean supportsResultSetType(int type)
	 *------------------------------------------------------------*/
	public boolean supportsResultSetType(int type) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsResultSetType", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, type,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsResultSetType");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		switch (type) {
		case ResultSet.TYPE_FORWARD_ONLY:
		case ResultSet.TYPE_SCROLL_INSENSITIVE:
			return true;
		default:
			return false;
		}
	}

	/*------------------------------------------------------------
	 * boolean supportsResultSetConcurrency(int type, int concurrency)
	 *------------------------------------------------------------*/
	public boolean supportsResultSetConcurrency(int type, int concurrency)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					type, concurrency);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsResultSetConcurrency", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					type, concurrency);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsResultSetConcurrency");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		switch (type) {
		case ResultSet.TYPE_FORWARD_ONLY:
		case ResultSet.TYPE_SCROLL_INSENSITIVE:
			return true; // We support both ResultSet.CONCUR_READ_ONLY and
			// ResultSet.CONCUR_UPDATABLE
		case ResultSet.TYPE_SCROLL_SENSITIVE:
			return false;
		default:
			return false;
		}
	}

	// jdk 1.4

	public boolean supportsSavepoints() throws SQLException {
		return false;
	}

	public boolean supportsNamedParameters() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsNamedParameters", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsNamedParameters");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	public boolean supportsMultipleOpenResults() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsMultipleOpenResults", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsMultipleOpenResults");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	public boolean supportsGetGeneratedKeys() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE, "SQLMXDatabaseMetaData", "supportsGetGeneratedKeys", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsGetGeneratedKeys");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		// Modified for L36 corda release
		if ((getDatabaseMajorVersion() == 3) && (getDatabaseMinorVersion() >= 6) || getDatabaseMajorVersion() > 3) {
			return true;
		} else {
			return false;
		}

	}

	public ResultSet getSuperTypes(String catalog, String schemaPattern,
			String typeNamePattern) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, typeNamePattern,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSuperTypes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, typeNamePattern,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSuperTypes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXResultSet resultSet;
		SQLMXDesc[] outputDesc;
		Row[] rows;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		outputDesc = new SQLMXDesc[6];
		outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_CAT", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[1] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_SCHEM", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[2] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[3] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SUPERTYPE_CAT", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[4] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SUPERTYPE_SCHEM", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[5] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SUPERTYPE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);

		resultSet = new SQLMXResultSet(this, outputDesc, "", true);
		rows = new Row[0];

		// Populate the rows
		resultSet.setFetchOutputs(rows, 0, true);
		return resultSet;
	}

	public ResultSet getSuperTables(String catalog, String schemaPattern,
			String tableNamePattern) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, tableNamePattern,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSuperTables", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, tableNamePattern,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSuperTables");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXResultSet resultSet;
		SQLMXDesc[] outputDesc;
		Row[] rows;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		outputDesc = new SQLMXDesc[4];
		outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_CAT", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[1] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_SCHEM", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[2] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[3] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SUPERTABLE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);

		resultSet = new SQLMXResultSet(this, outputDesc, "", true);
		rows = new Row[0];

		// Populate the rows
		resultSet.setFetchOutputs(rows, 0, true);
		return resultSet;

	}

	public ResultSet getAttributes(String catalog, String schemaPattern,
			String typeNamePattern, String attributeNamePattern)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, typeNamePattern,
					attributeNamePattern);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getAttributes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					catalog, schemaPattern, typeNamePattern,
					attributeNamePattern);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getAttributes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXResultSet resultSet;
		SQLMXDesc[] outputDesc;
		Row[] rows;

		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();

		outputDesc = new SQLMXDesc[21];
		outputDesc[0] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_CAT", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[1] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_SCHEM", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[2] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "TYPE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[3] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "ATTR_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[4] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_SMALLINT, (short) 0,
				2, (short) 0, (short) 0, false, "DATA_TYPE", false,
				Types.SMALLINT, (short) 0, (short) 0, 0, null, null, null, 130,
				0, 0);
		outputDesc[5] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "ATTR_TYPE_NAME", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[6] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "ATTR_SIZE", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[7] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "DECIMAL_DIGITS ", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[8] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "NUM_PREC_RADIX", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[9] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "NULLABLE ", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[10] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "REMARKS", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[11] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "ATTR_DEF", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[12] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "SQL_DATA_TYPE", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[13] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "SQL_DATETIME_SUB", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[14] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "CHAR_OCTET_LENGTH", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[15] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_INTEGER, (short) 0,
				4, (short) 0, (short) 0, false, "ORDINAL_POSITION", false,
				Types.INTEGER, (short) 0, (short) 0, 0, null, null, null, 132,
				0, 0);
		outputDesc[16] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "IS_NULLABLE", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[17] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SCOPE_CATALOG", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[18] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SCOPE_SCHEMA", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[19] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_VARCHAR, (short) 0,
				128, (short) 0, (short) 0, false, "SCOPE_TABLE", false,
				Types.VARCHAR, (short) 0, (short) 0, 0, null, null, null, 100,
				0, 0);
		outputDesc[20] = new SQLMXDesc(SQLMXDesc.SQLTYPECODE_SMALLINT, (short) 0,
				2, (short) 0, (short) 0, false, "SOURCE_DATA_TYPE", false,
				Types.SMALLINT, (short) 0, (short) 0, 0, null, null, null, 130,
				0, 0);

		resultSet = new SQLMXResultSet(this, outputDesc, "", true);
		rows = new Row[0];

		// Populate the rows
		resultSet.setFetchOutputs(rows, 0, true);
		return resultSet;
	}

	public int getJDBCMajorVersion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getJDBCMajorVersion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getJDBCMajorVersion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		 return JDBC_DRIVER_MAJOR_VERSION; //Added for SQL/MX3.5 release
	}

	public int getJDBCMinorVersion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getJDBCMinorVersion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getJDBCMinorVersion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		 return JDBC_DRIVER_MINOR_VERSION; //Added for SQL/MX3.5 release
	}

	public int getSQLStateType() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getSQLStateType", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getSQLStateType");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return DatabaseMetaData.sqlStateXOpen;
	}

	public boolean locatorsUpdateCopy() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "locatorsUpdateCopy", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("locatorsUpdateCopy");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	public boolean supportsStatementPooling() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsStatementPooling", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsStatementPooling");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	public boolean supportsResultSetHoldability(int holdability)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					holdability,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "supportsResultSetHoldability", "",
					p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					holdability,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("supportsResultSetHoldability");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	public int getResultSetHoldability() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getResultSetHoldability", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getResultSetHoldability");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return SQLMXResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	public int getDatabaseMajorVersion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDatabaseMajorVersion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDatabaseMajorVersion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// return 1;
		//		return connection_.getServerMajorVersion();
				return connection_.props_.getSqlmxMajorVersion();

	}

	public int getDatabaseMinorVersion() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "getDatabaseMinorVersion", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXDatabaseMetaData");
			lr.setSourceMethodName("getDatabaseMinorVersion");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// return 8;
		//		return connection_.getServerMinorVersion();
		return connection_.props_.getSqlmxMinorVersion();
	}

	// Method used by JNI Layer to update the results of GetCatalogs
	private SQLMXResultSet getResultSet() {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXDatabaseMetaData", "getResultSet", "", p);
		}
		return resultSet_;
	}

	// Constructors
	SQLMXDatabaseMetaData(SQLMXConnection connection) {
		if (connection.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection.props_,
					connection);
			connection.props_.t4Logger_.logp(Level.FINE,
					"SQLMXDatabaseMetaData", "", "", p);
		}
		try {
			if (connection.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(connection.props_,
						connection);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXDatabaseMetaData");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		connection_ = connection;
	}

	// Interface methods
	private void getSQLCatalogsInfo(InterfaceConnection ic, short catalogAPI,
			String catalogNm, String schemaNm, String tableNm,
			String tableTypeList, String columnNm, int columnType,
			int rowIdScope, long nullable, int uniqueness, int accuracy,
			short sqlType, int metadataId, String fkcatalogNm,
			String fkschemaNm, String fktableNm) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, ic,
					catalogAPI, catalogNm, schemaNm, tableNm, tableTypeList,
					columnNm, columnType, rowIdScope, nullable, uniqueness,
					accuracy, sqlType, metadataId, fkcatalogNm, fkschemaNm,
					fktableNm);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXDatabaseMetaData", "getSQLCatalogsInfo", "", p);
		}
		GetSQLCatalogsReply gcr_ = null;
		int hashcode = hashCode();

		// Nonstop SQL/MX Database doesn't like '-' character in CursorName
		if (hashcode < 0) {
			hashcode = -hashcode;

		}
		String stmtLabel = "STMT_CATALOG_" + hashcode + 1000;
		if (stmtLabel.length() > 17) {
			stmtLabel = stmtLabel.substring(0, 17);

			// SQLMX stores them all in upper case
		}

		// we should not be messing with case at all here
		// removing quotes is also not needed, the user should provide exactly
		// what they want
		// major change of functionality going into R2.4
		//R3.0 changes -- removing the comment block sol. 10-100916-3120
		
		//Code Change is added for solution 10-180817-8152.Previously driver 
		//was removing the "" double quotes passed as catalog name which cause
		//issue in server to fetch the metadtata for them.So we have removed that
		//portion of code to pass the catalog name as it as passed by client.
		
		  if (catalogNm != null) {
			if (catalogNm.startsWith("\"") && catalogNm.endsWith("\""))
					 {
						if (catalogNm.length() == 2)
							catalogNm = "%";
				} else {
				catalogNm = catalogNm.toUpperCase();
			}
		}
		if (schemaNm != null) {
			if (schemaNm.startsWith("\"") && schemaNm.endsWith("\"")
					&& schemaNm.length() > 1) {
				schemaNm = schemaNm.substring(1, schemaNm.length() - 1);
				if (schemaNm.length() == 0) {
					schemaNm = "%";
				}
			} else {
				schemaNm = schemaNm.toUpperCase();
			}
		}
		if (tableNm != null) {
			if (tableNm.startsWith("\"") && tableNm.endsWith("\"")
					&& tableNm.length() > 1) {
				tableNm = tableNm.substring(1, tableNm.length() - 1);
				if (tableNm.length() == 0) {
					tableNm = "%";
				}
			} else {
				tableNm = tableNm.toUpperCase();
			}
		}
		if (columnNm != null) {
			if (columnNm.startsWith("\"") && columnNm.endsWith("\"")
					&& columnNm.length() > 1) {
				columnNm = columnNm.substring(1, columnNm.length() - 1);
				if (columnNm.length() == 0) {
					columnNm = "%";
				}
			} else {
				columnNm = columnNm.toUpperCase();
			}
		}
		
		//Code Change is added for solution 10-180817-8152.Previously driver 
		//was removing the "" double quotes passed as catalog name which cause
		//issue in server to fetch the metadtata for them.So we have removed that
		//portion of code to pass the catalog name as it as passed by client.
		
		if (fkcatalogNm != null) {
			if (fkcatalogNm.startsWith("\"") && fkcatalogNm.endsWith("\"")){
				if(fkcatalogNm.length() == 2)
					fkcatalogNm = "%";
			} else {
				fkcatalogNm = fkcatalogNm.toUpperCase();
			}
		}
		if (fkschemaNm != null) {
			if (fkschemaNm.startsWith("\"") && fkschemaNm.endsWith("\"")
					&& fkschemaNm.length() > 1) {
				fkschemaNm = fkschemaNm.substring(1, fkschemaNm.length() - 1);
				if (fkschemaNm.length() == 0) {
					fkschemaNm = "%";
				}
			} else {
				fkschemaNm = fkschemaNm.toUpperCase();
			}
		}
		if (fktableNm != null) {
			if (fktableNm.startsWith("\"") && fktableNm.endsWith("\"")
					&& fktableNm.length() > 1) {
				fktableNm = fktableNm.substring(1, fktableNm.length() - 1);
				if (fktableNm.length() == 0) {
					fktableNm = "%";
				}
			} else {
				fktableNm = fktableNm.toUpperCase();
			}
		}


		try {
			gcr_ = ic.getT4Connection().GetSQLCatalogs(stmtLabel,
					(short) (catalogAPI), catalogNm, schemaNm, tableNm,
					tableTypeList, columnNm, columnType, rowIdScope,
					(int) nullable, uniqueness, accuracy, sqlType, metadataId,
					fkcatalogNm, fkschemaNm, fktableNm);
		} catch (SQLException tex) {
			if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
				connection_.props_.t4Logger_.logp(Level.FINER,
						"SQLMXDatabaseMetaData", "getSQLCatalogsInfo",
						"Got an exception after ic.getT4Connection().GetSQLCatalogs. Exception text: "
								+ tex.getMessage(), p);
			}
			performConnectionErrorChecks(tex);
			throw tex;
		}

		switch (gcr_.m_p1.exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
		case TRANSPORT.SQL_SUCCESS_WITH_INFO: //Added for solution 10-140520-1941
			if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
				connection_.props_.t4Logger_.logp(Level.FINER,
						"SQLMXDatabaseMetaData", "getSQLCatalogsInfo",
						"gcr_.m_p1.exception_nr = TRANSPORT.CEE_SUCCESS and gcr_.m_p4._length = "
								+ gcr_.m_p4.length, p);
			}

			// do the warning processing
			if (gcr_.m_p4.length != 0) {
				SQLMXMessages.setSQLWarning(connection_.props_, this, gcr_.m_p4);

				// If there is no description definitions data throw fits. MXCS
				// bug.
				// Mr.Rao fixing it.
			}
			if (gcr_.m_p3 == null || gcr_.m_p3.list == null
					|| gcr_.m_p3.list.length == 0) {
				if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
					Object p[] = T4LoggingUtilities
							.makeParams(connection_.props_,connection_);
					connection_.props_.t4Logger_.logp(Level.FINER,
							"SQLMXDatabaseMetaData", "getSQLCatalogsInfo",
							"SQLDescList null or 0 length", p);
				}
				throw SQLMXMessages.createSQLException(connection_.props_, ic
						.getLocale(), "ids_unknown_reply_error",
						"Empty SQL Desc List returned by MXCS");
			}

			SQLMXDesc[] outputDesc = InterfaceStatement.NewDescArray(gcr_.m_p3);

			resultSet_ = new SQLMXResultSet(this, outputDesc, gcr_.m_p2, true);
			resultSet_.proxySyntax_ = gcr_.proxySyntax;
			break;
		case odbc_SQLSvc_GetSQLCatalogs_exc_.odbc_SQLSvc_GetSQLCatalogs_SQLError_exn_:
			if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
				connection_.props_.t4Logger_
						.logp(
								Level.FINER,
								"SQLMXDatabaseMetaData",
								"getSQLCatalogsInfo",
								"gcr_.m_p1.exception_nr = odbc_SQLSvc_GetSQLCatalogs_exc_.odbc_SQLSvc_GetSQLCatalogs_SQLError_exn_",
								p);
			}
			SQLMXMessages.throwSQLException(connection_.props_,
					gcr_.m_p1.SQLError);
		default:
			if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
				connection_.props_.t4Logger_.logp(Level.FINER,
						"SQLMXDatabaseMetaData", "getSQLCatalogsInfo",
						"case gcr_.m_p1.exception_nr default", p);
			}
			throw SQLMXMessages.createSQLException(connection_.props_, ic
					.getLocale(), "ids_unknown_reply_error", null);

		}
	};

	void closeErroredConnection(SQLMXException sme) {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, sme,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXDatabaseMetaData", "closeErroredConneciton", "", p);
		}
		connection_.closeErroredConnection(sme);
	}
	
	// JDBC 4.x Wrapper Interface implementation
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

	//DC Date changes for SQL/MX3.5 release.	
	public static final int JDBC_DRIVER_MAJOR_VERSION = 3;
	public static final int JDBC_DRIVER_MINOR_VERSION = 6;

	// fields
	SQLMXConnection connection_;

	SQLMXResultSet resultSet_;

	// declarations from sql.h
	private static final short SQL_API_SQLGETTYPEINFO = 47;

	private static final short SQL_API_SQLCOLUMNS = 40;

	private static final short SQL_API_SQLSPECIALCOLUMNS = 52;

	private static final short SQL_API_SQLSTATISTICS = 53;

	private static final short SQL_API_SQLTABLES = 54;

	private static final short SQL_API_SQLCOLUMNPRIVILEGES = 56;

	private static final short SQL_API_SQLFOREIGNKEYS = 60;

	// private static final short SQL_API_TBLSYNONYM = 63;
	// //dbscripts_mv_synonym
	// private static final short SQL_API_TBLMVS = 64; //dbscripts_mv_synonym
	private static final short SQL_API_SQLPRIMARYKEYS = 65;

	private static final short SQL_API_SQLPROCEDURECOLUMNS = 66;

	private static final short SQL_API_SQLPROCEDURES = 67;

	private static final short SQL_API_SQLTABLEPRIVILEGES = 70;

	private static final short SQL_API_TBLSYNONYM = 1917; // dbscripts_mv_synonym

	private static final short SQL_API_TBLMVS = 1918; // dbscripts_mv_synonym

	private static final short SQL_API_JDBC = 9999;

	private static final short SQL_API_SQLTABLES_JDBC = (short) (SQL_API_SQLTABLES + SQL_API_JDBC);

	private static final short SQL_API_SQLCOLUMNS_JDBC = (short) (SQL_API_SQLCOLUMNS + SQL_API_JDBC);

	private static final short SQL_API_SQLSPECIALCOLUMNS_JDBC = (short) (SQL_API_SQLSPECIALCOLUMNS + SQL_API_JDBC);

	private static final short SQL_API_SQLGETTYPEINFO_JDBC = (short) (SQL_API_SQLGETTYPEINFO + SQL_API_JDBC);

	// values of NULLABLE field in descriptor
	private static final long SQL_NO_NULLS = 0;

	private static final long SQL_NULLABLE = 1;

	// Reserved values for UNIQUE argument of SQLStatistics()
	private static final int SQL_INDEX_UNIQUE = 0;

	private static final int SQL_INDEX_ALL = 1;

	// Column types and scopes in SQLSpecialColumns.
	private static final int SQL_BEST_ROWID = 1;

	private static final int SQL_ROWVER = 2;

	// JDBC 4.x stubs
	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "getRowIdLifetime()");		
		return null;
	}

	@Override
	public ResultSet getSchemas(String catalog, String schemaPattern)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "getSchemas(String catalog, String schemaPattern)");		
		return null;
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "supportsStoredFunctionsUsingCallSyntax()");		
		return false;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "autoCommitFailureClosesAllResultSets()");		
		return false;
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "getClientInfoProperties()");		
		return null;
	}

	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern,
			String functionNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "getFunctionColumns(String catalog, String schemaPattern,String functionNamePattern)");		
		return null;
	}

	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern,
			String functionNamePattern, String columnNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "getFunctionColumns(String catalog, String schemaPattern,String functionNamePattern, String columnNamePattern)");
		return null;
	}
	/*
	@Override
	public ResultSet getPseudoColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "getPseudoColumns(String catalog, String schemaPattern,String tableNamePattern, String columnNamePattern)");
		return null;
	}

	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "generatedKeyAlwaysReturned()");
		
		return false;
	}
	*/
}
