// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003-2008
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

/*Change Log
 * Vproc  SPR T1249_V20(ACK)_30NOV10_HP_NONSTOP(TM)_JDBCT4_2010_11_24
 * Solution No. 10-100318-8826 
 * Methods Changed, All prepareXXX() methods,void close(boolean, boolean)  
 * Changed By: Nagaraju
 */

/*Change Log
 * Vproc  SPR T1249_V20(ACK)_30NOV10_HP_NONSTOP(TM)_JDBCT4_2010_11_24
 * Solution No. 10-100713-1762
 * Methods Changed: closeErroredConnection(SQLMXException) 
 * Changed By: Niveditha
 */
package com.tandem.t4jdbc;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.PooledConnection;

/**
 * <p>
 * HP JDBC Type 4 SQLMXConnection class.
 * </p>
 * <p>
 * Description: The <code>SQLMXConnection</code> class is an implementation of
 * the <code>java.sql.Connection</code> interface.
 * </p>
 * <p>
 * Copyright: (C) Copyright 2004-2008 Hewlett-Packard Development Company, L.P.
 * </p>
 */
public class SQLMXConnection extends PreparedStatementManager implements
		java.sql.Connection {
	private Logger log_;

	//10-090921-4730 External call stmt - R30
	private static CallableStatement createExternalCallableStatement(String className, String call) throws SQLException {
		Class classX = null;

		/*
		 * Check if the Class exists in the Class path.
		 */
		try {
			classX = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new SQLException("Specified Class not found: "
					+ e.getLocalizedMessage());
		}

		/*
		 * Check if the Class implements the CallableStatement interface.
		 */
		boolean blnIsCallableStatement = false;

		if (classX != null) {
			Class[] interfaces = classX.getInterfaces();
			for (int nfor = 0; nfor < interfaces.length; ++nfor) {
				if (interfaces[nfor].getName().equals(
						CallableStatement.class.getName())) {
					blnIsCallableStatement = true;
					break;
				}
			}
		}
		if (!blnIsCallableStatement) {
			throw new SQLException(
					"The Specified Class does not implement java.sql.CallableStatement interface.");
		}

		Constructor[] array = null;

		if (classX != null) {
			array = classX.getDeclaredConstructors();
		}

		if (array != null) {

			boolean blnPublic = false;

			for (int nfor = 0; nfor < array.length; ++nfor) {
				if (array[nfor].getModifiers() == Modifier.PUBLIC) {
					blnPublic = true;
					Class[] params = array[nfor].getParameterTypes();
					if (params != null) {
						if (params.length == 1) {
							if (params[0].getName().equals(
									String.class.getName())) {
								Object initVar[] = new Object[1];
								initVar[0] = call;
								Object o = null;
								try {
									o = array[nfor].newInstance(initVar);
								} catch (IllegalArgumentException e) {
									throw new SQLException(e.getMessage());
								} catch (InstantiationException e) {
									throw new SQLException(e.getMessage());
								} catch (IllegalAccessException e) {
									throw new SQLException(e.getMessage());
								} catch (InvocationTargetException e) {
									throw new SQLException(e.getMessage());
								}
								return (CallableStatement) o;
							}
						}
					}
				}
			}
			if (!blnPublic) {
				throw new SQLException(
						"No Public Constructors available in the Specified Class.");
			}
		} else {
			throw new SQLException(
					"No Constructors available in the Specified Class.");
		}
		throw new SQLException(
				"No Constructor available accepting ONLY java.lang.String parameter in the Specified Class.");
	}
	//10-090921-4730 External call stmt - R30
	
	/**
	 * Validates the connection by clearing warnings and verifying that the
	 * Connection is still open.
	 * 
	 * @throws SQLException
	 *             If the Connection is not valid
	 */
	private void validateConnection() throws SQLException {
		clearWarnings();

		if (this.ic_ == null || this.ic_.isClosed()) {
			throw SQLMXMessages.createSQLException(this.props_, this
					.getLocale(), "invalid_connection", null);
		}
	}

	public String getRemoteProcess() throws SQLException {
		String url = this.ic_.getUrl();

		return url.substring(url.indexOf('$'), url.indexOf('/'));
	}

	synchronized public void close() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_
					.logp(Level.FINE, "SQLMXConnection", "close", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("close");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		// only hard-close if no pooled connection exists
		close((pc_ == null), true);
	}

	public void commit() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "commit", "",
					p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("commit");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();
		
		//Added for Commitwork RFE -Start
		if(this.props_.getUseExternalTransaction().equalsIgnoreCase("YES")){
			return;
		}
		//Added for Commitwork RFE -End
		
		if (getAutoCommit() == false) {
			isActiveTrans = false;
		}
		try {
			ic_.commit();
		} catch (SQLException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	public String getApplicationName() throws SQLException {
		validateConnection();

		return this.ic_.getApplicationName();
	}

	public String getServerDataSource() throws SQLException {
		validateConnection();

		return this.ic_.getServerDataSource();
	}

	public boolean getEnforceISO() throws SQLException {
		validateConnection();

		return this.ic_.getEnforceISO();
	}

	public int getISOMapping() throws SQLException {
		validateConnection();

		return this.ic_.getISOMapping();
	}

	public String getRoleName() throws SQLException {
		validateConnection();

		return this.ic_.getRoleName();
	}

	public int getTerminalCharset() throws SQLException {
		validateConnection();

		return this.ic_.getTerminalCharset();
	}

	public T4Properties getT4Properties() throws SQLException {
		validateConnection();

		return this.ic_.t4props_;
	}

	public String getSessionName() throws SQLException {
		validateConnection();

		return this.ic_.getSessionName();
	}
//Added for RMXCI
	public String getMXCSVersion() throws SQLException {
		validateConnection();
		return this.props_.getMxcsVersion().toString();
	}
	public String getSQLMXVersion() throws SQLException {
		validateConnection();
		String mxversion =this.props_.getSqlmxMajorVersion() +"."+this.props_.getSqlmxMinorVersion(); 
		return mxversion;
	}
	public Statement createStatement() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"createStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("createStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		try {
			SQLMXStatement stmt = new SQLMXStatement(this);
			//R3.0 Added for MFC, since Statements also go through MFC path 
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			return stmt;
		} catch (SQLException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, resultSetType,
					resultSetConcurrency,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"createStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, resultSetType,
					resultSetConcurrency,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("createStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		try {
			SQLMXStatement stmt = new SQLMXStatement(this, resultSetType, resultSetConcurrency);
			//R3.0 Added for MFC, since Statements also go through MFC path
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			return stmt;			
		} catch (SQLException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, resultSetType,
					resultSetConcurrency, resultSetHoldability,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"createStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, resultSetType,
					resultSetConcurrency,resultSetHoldability,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("createStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		try {
			SQLMXStatement stmt = new SQLMXStatement(this, resultSetType,
					resultSetConcurrency, resultSetHoldability);
			//R3.0 Added for MFC, since Statements also go through MFC path
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			return stmt;			
		} catch (SQLException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	Locale getLocale() {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection", "getLocale",
					"", p);
		}
		if (ic_ != null) {
			return ic_.getLocale();
		} else {
			return null;
		}
	}

	public boolean getAutoCommit() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"getAutoCommit", "getAutoCommit", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getAutoCommit");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		return ic_.getAutoCommit();
	}
	
	//Modification for L36 Corda : start
	/*
	 * boolean isValid(int timeout) throws SQLException
                
	 * Returns true if the connection has not been closed and is still valid. The driver shall 
	 * submit a query on the connection or use some other mechanism that positively verifies 
	 * the connection is still valid when this method is called.
     * The query submitted by the driver to validate the connection shall be executed in the 
     * context of the current transaction.

	 * Parameters:
	 * timeout - - The time in seconds to wait for the database operation used to validate the 
	 * connection to complete. If the timeout period expires before the operation completes, 
	 * this method returns false. A value of 0 indicates a timeout is not applied to the database operation.
	 
	 * Returns:
	 * true if the connection is valid, false otherwise
	 
	 * Throws:
	 * SQLException - if the value supplied for timeout is less then 0
	 */
	public boolean isValid(int Timeout) throws SQLException
	{
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,Timeout,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "isValid",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,Timeout,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("isValid");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		
		boolean result= false;
		if(Timeout<0){
			throw SQLMXMessages.createSQLException(
				this.props_, this.getLocale(),
				"invalid_parameter_value",
				"Timeout value should be greater than or equal to zero"
				);
		}
		else if(Timeout == 0){
			if(!isClosed()){
      		  	
				try{
					Statement st= createStatement();
					st.close();
    		      result= true;
    		  }catch(SQLException e){
    			  result= false;
    		  }
      	  	}else{
      	  		result=false;
      	  	}
		}
		else if(Timeout>0){
			ExecutorService service =  Executors.newSingleThreadExecutor();
			Future<Boolean> future = service.submit(new Callable<Boolean>(){
	            public Boolean call() throws SQLException{
	            	if(!isClosed()){
	            		try{
	            			Statement st= createStatement();
	            			st.close();
	            			return true;
	            		}catch(SQLException e){
	            			return false;
	            		}
	            	}else{
	            		return false;
	            	}
	            }
			});
			
			try {
				result= future.get(Timeout, TimeUnit.SECONDS);
			}  catch (TimeoutException e){
				result= false;
			} catch (InterruptedException e) {
				
			} catch (ExecutionException e) {
				
			}  finally {
				future.cancel(true);
				service.shutdown();
			}
		}
		
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,Timeout,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "isValid",
					"At exit return = " + result, p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,Timeout,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("isValid");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		return result;
	}
	
	public int getNetworkTimeout() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "getNetworkTimeout",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getNetworkTimeout");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();
		
		if(!this.isClosed()){
			return ic_.getNetworkTimeout();
		}else{
			throw new SQLException("Connection does not exist");
		}
	}
	
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		//currently ignoring executor
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, milliseconds,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "setNetworkTimeout",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, milliseconds,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setNetworkTimeout");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		
		if(!this.isClosed()){
			if(milliseconds>=0){
				ic_.setNetworkTimeout(milliseconds);
			}else{
				throw new SQLException("NetworkTimeout cannot be negative");
			}
		}else{
			throw new SQLException("Connection does not exist");
		}
	}
	//End
	public String getCatalog() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "getCatalog",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getCatalog");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		return ic_.getCatalog();
	}

	public String getSchema() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "getSchema",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getSchema");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		return ic_.getSchema();
	}

	public int getHoldability() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"getHoldability", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getHoldability");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		return holdability_;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "getMetaData",
					"getMetaData", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getMetaData");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		return new SQLMXDatabaseMetaData(this);
	}

	public int getTransactionIsolation() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"getTransactionIsolation", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getTransactionIsolation");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		validateConnection();

		return ic_.getTransactionIsolation();
	}

	public Map getTypeMap() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "getTypeMap",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getTypeMap");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();

		return userMap_;
	}

	void isConnectionOpen() throws SQLException {
		validateConnection();
	}

	public boolean isClosed() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "isClosed",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("isClosed");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		boolean rv = true;

		if (ic_ == null) {
			rv = true;
			// return true;
		} else {
			clearWarnings();
			rv = ic_.getIsClosed();
		}
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "isClosed",
					"At exit return = " + rv, p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("isClosed");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		return rv;
		// return ic_.get_isClosed();
	}

	// New method that checks if the connection is closed
	// However, this is to be used only be internal classes
	// It does not clear any warnings associated with the current connection
	// Done for CASE 10_060123_4011 ; Swastik Bihani
	boolean _isClosed() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "_isClosed",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("_isClosed");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		boolean rv = true;

		if (ic_ == null) {
			rv = true;
		} else {
			rv = ic_.getIsClosed();
		}
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "_isClosed",
					"At exit return = " + rv, p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("_isClosed");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		return rv;
	}

	/**
	 * @deprecated
	 */
	public String getServiceName() throws SQLException {
		return "";
	}

	/**
	 * @deprecated
	 */
	public void setServiceName(String serviceName) throws SQLException {

	}

	public boolean isReadOnly() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "isReadOnly",
					"", p);
		}

		validateConnection();

		return ic_.isReadOnly();
	}

	public String nativeSQL(String sql) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "nativeSQL",
					"", p);
		}

		validateConnection();

		return sql;
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "prepareCall",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareCall");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		//10-090921-4730
		if(!this.props_.getExternalCallHandler().equals("NONE")) {
			String sqlX = sql;
			if(sqlX.trim().startsWith("{")) {
				sqlX = sqlX.trim().substring(1).trim();
				if(sqlX.toUpperCase().startsWith(this.props_.getExternalCallPrefix().toUpperCase())) {
					CallableStatement externalCallStmt = SQLMXConnection.createExternalCallableStatement(this.props_.getExternalCallHandler(), sql);
					return externalCallStmt;
				}
			}
		}
		//10-090921-4730

		this.setWeight(sql, holdability_);
		SQLMXCallableStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXCallableStatement) getPreparedStatement(this, sql,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);

				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXCallableStatement(this, sql);
			
			//Added for the solution 10-151124-8260			
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			
			stmt.prepareCall(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	public CallableStatement prepareCall(String sql, String stmtLabel)
			throws SQLException {
		final String QUOTE = "\"";

		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "prepareCall",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareCall");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		if (stmtLabel == null || stmtLabel.length() == 0) {
			throw SQLMXMessages.createSQLException(props_, null, "null_data",
					null);
		}

		if (stmtLabel.startsWith(QUOTE) && stmtLabel.endsWith(QUOTE)) {
			int len = stmtLabel.length();
			if (len == 2) {
				throw SQLMXMessages.createSQLException(props_, null,
						"null_data", null);
			} else {
				stmtLabel = stmtLabel.substring(1, len - 1);
			}
		} else {
			stmtLabel = stmtLabel.toUpperCase();
		}

		SQLMXCallableStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXCallableStatement) getPreparedStatement(this, sql,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);

				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXCallableStatement(this, sql, stmtLabel);
			// MFC new property enableMFC R3.0
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			stmt.prepareCall(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetConcurrency,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "prepaseCall",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetConcurrency,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepaseCall");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		this.setWeight(sql, holdability_);
		SQLMXCallableStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXCallableStatement) getPreparedStatement(this, sql,
						resultSetType, resultSetConcurrency, holdability_);
				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXCallableStatement(this, sql, resultSetType,
					resultSetConcurrency);
			// MFC new property enableMFC R3.0
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			stmt.prepareCall(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt, resultSetType,
						resultSetConcurrency, holdability_);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetConcurrency, resultSetHoldability,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "prepareCall",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetHoldability,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareCall");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		this.setWeight(sql, resultSetHoldability);
		SQLMXCallableStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXCallableStatement) getPreparedStatement(this, sql,
						resultSetType, resultSetConcurrency,
						resultSetHoldability);
				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXCallableStatement(this, sql, resultSetType,
					resultSetConcurrency, resultSetHoldability);

			// MFC new property enableMFC R3.0
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			stmt.prepareCall(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt, resultSetType,
						resultSetConcurrency, resultSetHoldability);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	// SOL-10-060911-8963
	/**
	 * Creates a <code>PreparedStatement</code> object for sending
	 * parameterized SQL statements to the database.
	 * 
	 * @param sql
	 *            SQL statement that might contain one or more '?' IN parameter
	 *            placeholders
	 * @param stmtLabel
	 *            SQL statement label that can be passed to the method instead
	 *            of generated by the database system.
	 * @returns a new default PreparedStatement object containing the
	 *          pre-compiled SQL statement
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public PreparedStatement prepareStatement(String sql, String stmtLabel)
			throws SQLException {
		final String QUOTE = "\"";

		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		if (stmtLabel == null || stmtLabel.length() == 0) {
			throw SQLMXMessages.createSQLException(props_, null, "null_data",
					null);
		}

		if (stmtLabel.startsWith(QUOTE) && stmtLabel.endsWith(QUOTE)) {
			int len = stmtLabel.length();
			if (len == 2) {
				throw SQLMXMessages.createSQLException(props_, null,
						"null_data", null);
			} else {
				stmtLabel = stmtLabel.substring(1, len - 1);
			}
		} else {
			stmtLabel = stmtLabel.toUpperCase();
		}

		SQLMXPreparedStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXPreparedStatement) getPreparedStatement(this, sql,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);
				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXPreparedStatement(this, sql, stmtLabel);
			
			//Added for the solution 10-151124-8260			
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF") || stmt.sqlStmtType_==TRANSPORT.SQL_CONTROL) {
				stmt.removeMFCFromStmtLabel();
			}	
			

			stmt.prepare(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		this.setWeight(sql, holdability_);
		SQLMXPreparedStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXPreparedStatement) getPreparedStatement(this, sql,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);
				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXPreparedStatement(this, sql);

			// MFC new property enableMFC R3.0
			//Added filter for CQD's  -- soln number 10-101118-4598
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF") || stmt.sqlStmtType_==TRANSPORT.SQL_CONTROL) {
				stmt.removeMFCFromStmtLabel();
			}			
			stmt.prepare(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
						addPreparedStatement(this, sql, stmt,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, holdability_);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		if(sql.contains("return_generated_keys 'on'"))
		{
			stmt.usingAutoGeneratedKeys_ = true; //Added for L36 corda
		}
		return stmt;
	}

	// SB 12/02/2004 - only for LOB statements - these will be not added to the
	// statement cache
	PreparedStatement prepareLobStatement(String sql) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"prepareLobStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareLobStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		SQLMXPreparedStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		try {
			stmt = new SQLMXPreparedStatement(this, sql);
			
			//Added for the solution 10-151124-8260			
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
				stmt.removeMFCFromStmtLabel();
			}
			
			
			stmt.prepare(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;

	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql, autoGeneratedKeys, this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql, autoGeneratedKeys, this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		// Modified for L36 corda release		
		if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
			
			return prepareStatement(sql);
		}
		else if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {

			int endIndex = -1;
			String tempSql=sql.trim();
			
			if(tempSql.endsWith(";")){
				tempSql=tempSql.substring(0, tempSql.length()-2);
			}
			if ((endIndex = containsIgnoreCase(tempSql, "with")) != -1) {
				if ((endIndex = containsIgnoreCase(tempSql, "using")) != -1) {
					String temp1 = tempSql.substring(0, endIndex);
					String temp2 = tempSql.substring(endIndex);
					return prepareStatement(temp1 + ", return_generated_keys 'on' " + temp2);
				} else {
					return prepareStatement(tempSql + ", return_generated_keys 'on'");

				}
			} else if ((endIndex = containsIgnoreCase(tempSql, "using")) != -1) {
				String temp1 = tempSql.substring(0, endIndex);
				String temp2 = tempSql.substring(endIndex);
				return prepareStatement(temp1 + " with control query default return_generated_keys 'on' " + temp2);

			} else
				return prepareStatement(tempSql + " with control query default return_generated_keys 'on'");
		} else {
			throw SQLMXMessages.createSQLException(props_, getLocale(),
					"Invalid constant value provided to retrieve the generated keys", null);

		}
	}

	// Added for L36 corda release
	private int containsIgnoreCase(String sql, String match) {

		int index = -1;
		String temp = sql.toLowerCase();
		if (temp.contains(match)) {

			index = temp.lastIndexOf(match);
			if (match.equals("using")) {
				temp = temp.substring(index);
				Pattern pattern = Pattern.compile("^(using(\\s)*<<\\+).*$");
				Matcher matcher = pattern.matcher(temp);
				if (matcher.matches()) {
					return index;
				} else
					return -1;
			} // using if condition end
			else if (match.equals("with")) {
				temp = temp.substring(index);
				Pattern pattern = Pattern.compile("^(with(\\s)+((control(\\s)+query(\\s)+default)|(cqd))(\\s)).*$");
				Matcher matcher = pattern.matcher(temp);
				if (matcher.matches()) {
					return index;
				} else
					return -1;

			} // with if condition end
			else
				return -1;

		} // contains if- end
		else
			return -1;
	}
	

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					columnIndexes,this);
			props_.t4Logger_.logp(Level.FINE, "SQLConnection",
					"prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					columnIndexes,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		if (columnIndexes != null && columnIndexes.length > 0) {
			throw SQLMXMessages.createSQLException(props_, getLocale(),
					"auto_generated_keys_not_supported", null);
		} else {
			return prepareStatement(sql);
		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetConcurrency,this);
			props_.t4Logger_.logp(Level.FINE, "SQLConnection",
					"prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetConcurrency,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		this.setWeight(sql, holdability_);
		SQLMXPreparedStatement stmt;

		clearWarnings();

		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXPreparedStatement) getPreparedStatement(this, sql,
						resultSetType, resultSetConcurrency, holdability_);
				if (stmt != null) {
				 return stmt;
				} 
			}

			stmt = new SQLMXPreparedStatement(this, sql, resultSetType,
					resultSetConcurrency);
			// MFC new property enableMFC R3.0
			//Added filter for CQD's  -- soln number 10-101118-4598
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF") || stmt.sqlStmtType_==TRANSPORT.SQL_CONTROL) {
				stmt.removeMFCFromStmtLabel();
			}
			stmt.prepare(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt, resultSetType,
						resultSetConcurrency, holdability_);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetConcurrency, resultSetHoldability,this);
			props_.t4Logger_.logp(Level.FINE, "SQLConnection",
					"prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, sql,
					resultSetType, resultSetHoldability,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		this.setWeight(sql, resultSetHoldability);
		SQLMXPreparedStatement stmt;

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		try {
			if (isStatementCachingEnabled()) {
				stmt = (SQLMXPreparedStatement) getPreparedStatement(this, sql,
						resultSetType, resultSetConcurrency,
						resultSetHoldability);
				if (stmt != null) {
					return stmt;
				}
			}

			stmt = new SQLMXPreparedStatement(this, sql, resultSetType,
					resultSetConcurrency, resultSetHoldability);
			// MFC new property enableMFC R3.0
			//Added filter for CQD's  -- soln number 10-101118-4598
			if (this.props_.getEnableMFC().equalsIgnoreCase("OFF") || stmt.sqlStmtType_==TRANSPORT.SQL_CONTROL) {
				stmt.removeMFCFromStmtLabel();
			}
			stmt.prepare(stmt.sql_, stmt.queryTimeout_,
					stmt.resultSetHoldability_);

			if (isStatementCachingEnabled()) {
				addPreparedStatement(this, sql, stmt, resultSetType,
						resultSetConcurrency, resultSetHoldability);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		return stmt;
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(props_, sql, columnNames,this);
			props_.t4Logger_.logp(Level.FINE, "SQLConnection",
					"prepareStatement", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(props_, sql, columnNames,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLConnection");
			lr.setSourceMethodName("prepareStatement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		if (columnNames != null && columnNames.length > 0) {
			throw SQLMXMessages.createSQLException(props_, getLocale(),
					"auto_generated_keys_not_supported", null);
		} else {
			return prepareStatement(sql);
		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, savepoint,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"releaseSavepoint", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, savepoint,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("releaseSavepoint");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		SQLMXMessages.throwUnsupportedFeatureException(props_, getLocale(),
				"releaseSavepoint()");
	}

	public void rollback() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "rollback",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("rollback");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		
		//Added for Commitwork RFE -Start
		if(this.props_.getUseExternalTransaction().equalsIgnoreCase("YES")){
			return;
		}
		//Added for Commitwork RFE -End
		
		if (getAutoCommit() == false) {
			isActiveTrans = false;
		}
		// if (ic_.getTxid() == 0) - XA
		// return;

		// commit the Transaction
		try {
			ic_.rollback();
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
		// ic_.setTxid(0); - XA
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, savepoint,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "rollback",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, savepoint,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("rollback");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		SQLMXMessages.throwUnsupportedFeatureException(props_, getLocale(),
				"rollback(Savepoint)");
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, autoCommit,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"setAutoCommit", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, autoCommit,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setAutoCommit");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}

		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
				//Added for Commitwork RFE -Start
		if(this.props_.getUseExternalTransaction().equalsIgnoreCase("YES")){
			return;
		}
		//Added for Commitwork RFE -End
		
		//to comply with standards sol - 10-110804-9007
        if(this.getAutoCommit() == autoCommit){
              return;
        }


		
		if (getAutoCommit() == false && isActiveTrans == true) {
			commit();
		}
		try {
			ic_.setAutoCommit(this, autoCommit);
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	public void setCatalog(String catalog) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, catalog,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "setCalalog",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, catalog,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setCalalog");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		if (catalog != null) {
			try {
				ic_.setCatalog(this, catalog);
			} catch (SQLMXException se) {
				performConnectionErrorChecks(se);
				throw se;
			}
		}
	}

	
	//Modification for L36 Corda : start
	public void setSchema(String schema) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, schema,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "setSchema",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, schema,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setSchema");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		if (schema != null) {
			try {
				ic_.setSchema(this, schema);
			} catch (SQLMXException se) {
				performConnectionErrorChecks(se);
				throw se;
			}
		}
	}
	//End
	
	public void setHoldability(int holdability) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, holdability,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"setHoldability", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, holdability,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setHoldability");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		if (holdability != SQLMXResultSet.CLOSE_CURSORS_AT_COMMIT)

		{
			throw SQLMXMessages.createSQLException(props_, getLocale(),
					"invalid_holdability", null);
		}
		holdability_ = holdability;
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, readOnly,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "setReadOnly",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, readOnly,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setReadOnly");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		try {
			// 10-060905-8814 - AM
			// ic_.setReadOnly(readOnly);
			ic_.setReadOnly(this, readOnly);
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	public void setConnectionAttr(short attr, int valueNum, String valueString)
			throws SQLException {
		ic_.setConnectionAttr(this, attr, valueNum, valueString);
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, name,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"setSavepoint", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, name,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setSavepoint");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		SQLMXMessages.throwUnsupportedFeatureException(props_, getLocale(),
				"setSavepoint");
		return null;
	}

	public Savepoint setSavepoint() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"setSavepoint", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setSavepoint");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		SQLMXMessages.throwUnsupportedFeatureException(props_, getLocale(),
				"setSavepoint");
		return null;
	}

	public void setTransactionIsolation(int level) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, level,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"setTransactionIsolation", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, level,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setTransactionIsolation");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		try {
			ic_.setTransactionIsolation(this, level);
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	// JDK 1.2
	public void setTypeMap(java.util.Map map) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, map,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "setTypeMap",
					"", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, map,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("setTypeMap");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		clearWarnings();
		userMap_ = map;
	}

	public void begintransaction() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"begintransaction", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("begintransaction");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		try {
			ic_.beginTransaction();

			if (ic_.beginTransaction() == 0) {
				return;
			} else {
				setAutoCommit(false);
			}
		} catch (SQLMXException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	public long getCurrentTransaction() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection", "getTxid", "",
					p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getTxid");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}
		return ic_.getTxid();
	}

	public void setTxid(long txid) throws SQLException {
		setTransactionToJoin(Bytes
				.createLongBytes(txid, this.ic_.getByteSwap()));
	}

	public void setTransactionToJoin(byte[] txid) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, txid,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection", "setTxid",
					"", p);
		}
		if (_isClosed() == true) {
			throw SQLMXMessages.createSQLException(props_, null,
					"invalid_connection", null);
		}

		transactionToJoin = txid;
	}

	void gcStmts() {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection", "gcStmt", "",
					p);
		}
		Reference pRef;
		String stmtLabel;

		while ((pRef = refStmtQ_.poll()) != null) {
			stmtLabel = (String) refToStmt_.get(pRef);
			// All PreparedStatement objects are added to Hashtable
			// Only Statement objects that produces ResultSet are added to
			// Hashtable
			// Hence stmtLabel could be null
			if (stmtLabel != null) {
				try {
					SQLMXStatement stmt = new SQLMXStatement(this, stmtLabel);
					stmt.close();
					stmt = null;
				} catch (SQLException e) {
					performConnectionErrorChecks(e);
				} finally {
					refToStmt_.remove(pRef);
				}
			}
		}
	}

	void removeElement(Reference pRef) {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, pRef,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"removeElement", "", p);
		}

		refToStmt_.remove(pRef);
		pRef.clear();
	}

	void addElement(Reference pRef, String stmtLabel) {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, stmtLabel,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection", "addElement",
					"", p);
		}
		refToStmt_.put(pRef, stmtLabel);
	}

	private void physicalCloseStatements() {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"physicalCloseStatement", "", p);
		}
		// close all the statements
		ArrayList stmts = new ArrayList(refToStmt_.values());
		int size = stmts.size();
		for (int i = 0; i < size; i++) {
			try {
				String stmtLabel = (String) stmts.get(i);
				SQLMXStatement stmt = new SQLMXStatement(this, stmtLabel);
				stmt.close();
				stmt = null;
			} catch (SQLException se) {
				// Ignore any exception and proceed to the next statement
			}
		}
		refToStmt_.clear();
		closeLobStatements();
	}

	private void rollbackAndIgnoreError() {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"rollbackAndIgnoreError", "", p);
		}
		// Rollback the Transaction when autoCommit mode is OFF
		try {
			if(this.props_.getUseExternalTransaction().equalsIgnoreCase("YES")){
				return ;
			}
			if ((getAutoCommit() == false) && (m_isXA_ == false)
					&& (isActiveTrans == true)) {
				rollback();
			}
		} catch (SQLException sqex) {
			// ignore
		}
	}

	private void closeLobStatements() {
		for (int i = 0; i < LobPreparedStatements.length; i++) {
			try {
				if (bLobStatementPrepared[i]) {
					LobPreparedStatements[i].close();
				}
			} catch (SQLException sqes) {
				// ignore
			} finally {
				bLobStatementPrepared[i] = false;
			}
		}
		if (!lobTableToDataLoc_.isEmpty()) { // Close lob statements held by
			// datalocators
			Iterator hashTableIterator = lobTableToDataLoc_.values().iterator();
			while (hashTableIterator.hasNext()) {
				SQLMXDataLocator entry = (SQLMXDataLocator) hashTableIterator
						.next();
				entry.closeLobStatements();
			}
		}
	}

	void close(boolean hardClose, boolean sendEvents) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, hardClose,
					sendEvents,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection", "close", "",
					p);
		}
		clearWarnings();
		try {
			//R321 changes for solution 10-121025-5199
			if(ic_ !=null){
				ic_.setReadOnly(this,false);
			}
			if (!hardClose) {
				//R3.1 changes -start
				if (props_.t4Logger_.isLoggable(Level.INFO) == true) {
					Object p[] = T4LoggingUtilities.makeParams(props_, hardClose,
							sendEvents,this);
					props_.t4Logger_.logp(Level.INFO, "SQLMXConnection", "logicalClose()", "",
							p);
				}
				//R3.1 changes -End
				
				if (this.ic_ != null && this.ic_.getIsClosed()) {
					return;
				}
				if (isStatementCachingEnabled()) {
					closePreparedStatementsAll();
				} else {
					physicalCloseStatements();
				}
				rollbackAndIgnoreError();

				/*
				 * //inform the NCS server to disregard the T4 ConnectionTimeout
				 * value try{ if (ic_ != null) {
				 * ic_.disregardT4ConnectionTimeout(this); } }catch(SQLException
				 * e){ //ignore - new property from old MXCS ABD version (now
				 * known as NCS) //ignored for backward compatibility }
				 */

				// Need to logicallcally close the statement
				pc_.logicalClose(sendEvents);
				if (ic_ != null) {
					//Modification for L36 Corda : start
					if (ic_.catalog_ != null) {
						try {
							if(!ic_.catalog_.equals(ic_.getCatalog()))
								ic_.setCatalog(this, ic_.catalog_);
							
						} catch (SQLMXException se) {
							performConnectionErrorChecks(se);
							throw se;
						}
					}
					
					if (ic_.schema_ != null) {
						try {
							if(!ic_.schema_.equals(ic_.getSchema()))
								ic_.setSchema(this, ic_.schema_);
						} catch (SQLMXException se) {
							performConnectionErrorChecks(se);
							throw se;
						}
					}
					//End
					ic_.setIsClosed(true);
				}
			} else {
				if (getServerHandle() == null) {
					return;
				}

				// close all the statements
				physicalCloseStatements();

				// Need to logicallcally close the statement
				// Rollback the Transaction when autoCommit mode is OFF
				rollbackAndIgnoreError();

				if (isStatementCachingEnabled()) {
					clearPreparedStatementsAll();
				}

				// Close the connection
				try {
					ic_.close();
				} finally {
					if (ic_ != null) {
						ic_.removeElement(this);
					}
					ic_ = null;
				}
			}
		} catch (SQLException e) {
			performConnectionErrorChecks(e);
			throw e;
		} finally {
			// close the timer thread
			if (ic_ != null && ic_.getT4Connection() != null) {
				ic_.getT4Connection().closeTimers();
			}
		}
	}

	protected void finalize() {
		if (ic_ != null && ic_.getT4Connection() != null) {
			ic_.getT4Connection().closeTimers();
		}
	}

	void reuse() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection", "resue", "",
					p);
		}
		ic_.reuse();
		/*
		 * try { ic_.enforceT4ConnectionTimeout(this); } catch (SQLMXException
		 * se) { //performConnectionErrorChecks(se); //throw se; //ignore - new
		 * property from old MXCS ABD version (now known as NCS) //ignored for
		 * backward compatibility }
		 */
		// Added for Solution 10-190904-0863
		if (props_.getAutoCommit() != null) {
			boolean autoCommit = props_.getAutoCommit().equalsIgnoreCase("ON") ? true : false;
			if (this.getAutoCommit() != autoCommit)
				ic_.setAutoCommit(this, autoCommit);
		}
	}

	int getChunkSize(String lobTableName, boolean isBlob) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"getDataLocator", "", p);
		}
		SQLMXDataLocator dataLoc;

		if (lobTableName == null) {
			if (isBlob) {
				throw SQLMXMessages.createSQLException(props_, getLocale(),
						"no_blobTableName", null);
			} else {
				throw SQLMXMessages.createSQLException(props_, getLocale(),
						"no_clobTableName", null);
			}
		}
		dataLoc = (SQLMXDataLocator) lobTableToDataLoc_.get(lobTableName);
		if (dataLoc == null) {
			dataLoc = new SQLMXDataLocator(this, lobTableName);
			lobTableToDataLoc_.put(lobTableName, dataLoc);
		}

		return dataLoc.chunkSize_;
	}

	long getDataLocator(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"getDataLocator", "", p);
		}
		SQLMXDataLocator dataLoc;

		if (lobTableName == null) {
			if (isBlob) {
				throw SQLMXMessages.createSQLException(props_, getLocale(),
						"no_blobTableName", null);
			} else {
				throw SQLMXMessages.createSQLException(props_, getLocale(),
						"no_clobTableName", null);
			}
		}
		dataLoc = (SQLMXDataLocator) lobTableToDataLoc_.get(lobTableName);
		if (dataLoc == null) {
			dataLoc = new SQLMXDataLocator(this, lobTableName);
			lobTableToDataLoc_.put(lobTableName, dataLoc);
		}
		return dataLoc.getDataLocator(this, isBlob);
	}

	// Extension method for WLS, this method gives the pooledConnection object
	// associated with the given connection object.
	public PooledConnection getPooledConnection() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"getPooledConnection", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getPooledConnection");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		if (pc_ != null) {
			return pc_;
		} else {
			throw SQLMXMessages.createSQLException(props_, getLocale(),
					"null_pooled_connection", null);
		}
	}

	// Constructors with access specifier as "default"
	SQLMXConnection(SQLMXDriver driver, String url, T4Properties t4props)
			throws SQLException {
		super(t4props);

		//Added for Solution 10-160301-9171
		connectionID_=Integer.toString(this.hashCode());
		t4props.setConnectionID(connectionID_);

		// Set up Tracing and Logging
		setupLogging(t4props);
		//Added for R3.1
		if(t4props.getQueryExecuteTime()>0){
		setupSlowQueryLogging(t4props);
		}
		
		driver_ = driver;

		// Obtain all the property info
		t4props.setURL(url);
		t4props.setLoginTimeout(DriverManager.getLoginTimeout());
		if (t4props.getCatalog() == null) {
			t4props.setCatalog(driver.getCatalog());
		}
		if (t4props.getSchema() == null) {
			t4props.setSchema(driver.getSchema());

		}

		// For LOB Support - SB:10/25/2004
		/* preparedClobTableName_ = */clobTableName_ = t4props
				.getClobTableName();
		/* preparedBlobTableName_ = */blobTableName_ = t4props
				.getBlobTableName();

		catalog_ = t4props.getCatalog();
		schema_ = t4props.getSchema();
		// FOR SPJBLOB -R3.0
		spjBlobTableName_ = blobTableName_;// "super.super.spj_table";
		spjClobTableName_ = clobTableName_;// "super.super.spj_table";
		spjBaseTableName_ = "spjcat.spjsch.spj_base_table";
		makeConnection(t4props);
		
		if (t4props.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, driver, url,
					t4props,this);
			t4props.t4Logger_.logp(Level.FINE, "SQLMXConnection", "<init>", "",
					p);
		}
		if (t4props.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, driver, url,
					t4props,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("<init>");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			t4props.getLogWriter().println(temp);
		}
		
		holdability_ = SQLMXResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	SQLMXConnection(SQLMXDataSource ds, T4Properties t4props)
			throws SQLException {
		super(t4props);

		//Added for Solution 10-160301-9171
		connectionID_=Integer.toString(this.hashCode());
		t4props.setConnectionID(connectionID_);
		t4props.setDialogueID(null);
		t4props.setServerID(null);

		setupLogging(t4props);
		//Added for R3.1
		if(t4props.getQueryExecuteTime()>0){
		setupSlowQueryLogging(t4props);
		}
		
		ds_ = ds;

		// For LOB Support - SB:10/25/2004
		/* preparedClobTableName_ = */clobTableName_ = t4props
				.getClobTableName();
		/* preparedBlobTableName_ = */blobTableName_ = t4props
				.getBlobTableName();

		catalog_ = t4props.getCatalog();
		schema_ = t4props.getSchema();
		// FOR SPJBLOB R3.0
		spjBlobTableName_ = blobTableName_;// "super.super.spj_table";
		spjClobTableName_ = clobTableName_;// "super.super.spj_table";
		spjBaseTableName_ = "spjcat.spjsch.spj_base_table";
		makeConnection(t4props);
		
		
		if (t4props.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, ds, t4props,this);
			t4props.t4Logger_.logp(Level.FINE, "SQLMXConnection", "<init>", "",
					p);
		}
		if (t4props.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, ds, t4props,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("<init>");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			t4props.getLogWriter().println(temp);
		}
		
		
		holdability_ = SQLMXResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	SQLMXConnection(SQLMXPooledConnection poolConn, T4Properties t4props)
			throws SQLException {
		super(t4props);

		//Added for Solution 10-160301-9171
		connectionID_=Integer.toString(this.hashCode());
		t4props.setConnectionID(connectionID_);

		setupLogging(t4props);
		//Added For R3.1
		if(t4props.getQueryExecuteTime()>0){
		setupSlowQueryLogging(t4props);
		}
		

		pc_ = poolConn;

		// For LOB Support - SB:10/25/2004
		/* preparedClobTableName_ = */clobTableName_ = t4props
				.getClobTableName();
		/* preparedBlobTableName_ = */blobTableName_ = t4props
				.getBlobTableName();

		catalog_ = t4props.getCatalog();
		schema_ = t4props.getSchema();
		// FOR SPJBLOB -R3.0
		spjBlobTableName_ = blobTableName_;// "super.super.spj_table";
		spjClobTableName_ = clobTableName_;// "super.super.spj_table";
		spjBaseTableName_ = "spjcat.spjsch.spj_base_table";
		makeConnection(t4props);
		
		if (t4props.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, poolConn,
					t4props,this);
			t4props.t4Logger_.logp(Level.FINE, "SQLMXConnection", "<init>", "",
					p);
		}
		if (t4props.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_, poolConn,
					t4props,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("<init>");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			t4props.getLogWriter().println(temp);
		}
		
		holdability_ = SQLMXResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	private void makeConnection(T4Properties t4props) throws SQLException {
		if (t4props.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, t4props,this);
			t4props.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"makeConnection", "", p);
		}
		clearWarnings();
		try {
			ic_ = new InterfaceConnection(t4props);
			if (ic_.sqlwarning_ != null) {
				setSqlWarning(ic_.sqlwarning_);
			}
			refStmtQ_ = new ReferenceQueue();
			refToStmt_ = new Hashtable();
			pRef_ = new WeakReference(this, ic_.refQ_);
			ic_.refTosrvrCtxHandle_.put(pRef_, ic_);
			props_ = t4props;

			ic_.enableNARSupport(this, props_.getBatchRecovery());

			if (props_.getSPJEnv()) {
				ic_.enableProxySyntax(this);
			}
			
			//Added for Solution 10-160301-9171
			dialogueID_=String.valueOf(ic_.getDialogueId());
			serverID_=ic_.getServerID();
			props_.setDialogueID(dialogueID_);
			props_.setServerID(serverID_);

			// SOL-10-070111-1697 this would cause a backward compatibility with
			// pre R2 server.
			// ic_.setServiceName(this, DEFAULT_SERVICENAME);

			short majorVersion = props_.getMxcsMajorVersion();
			short minorVersion = props_.getMxcsMinorVersion();
			if (t4props.t4Logger_.isLoggable(Level.FINER) == true) {
				Object p[] = T4LoggingUtilities.makeParams(props_, t4props,this);
				String temp = "ncsMajorVersion=" + majorVersion
						+ ", ncsMinorVersion=" + minorVersion;
				t4props.t4Logger_.logp(Level.FINER, "SQLMXConnection",
						"makeConnection", temp, p);
			}
		} catch (SQLException se) {
			performConnectionErrorChecks(se);
			throw se;
		}
	}

	// --------------------------------------------------------
	private void setupLogging(T4Properties t4props) {

		String ID = T4LoggingUtilities.getUniqueID();
		String name = T4LoggingUtilities.getUniqueLoggerName(ID);

		if (Level.parse(t4props.getT4LogLevel()) == Level.OFF) {
			if (dummyLogger_ == null) {
				dummyLogger_ = Logger.getLogger(name);
			}
			t4props.t4Logger_ = dummyLogger_;
		} else {
			t4props.t4Logger_ = Logger.getLogger(name);
		}

		// t4props.t4Logger_ = Logger.getLogger(name);
		t4props.t4Logger_.setUseParentHandlers(false);
		t4props.t4Logger_.setLevel(Level.parse(t4props.getT4LogLevel()));

		if (Level.parse(t4props.getT4LogLevel()) != Level.OFF) {
			FileHandler fh1 = t4props.getT4LogFileHandler();
			t4props.t4Logger_.addHandler(fh1);
		}
	} // end setupLogging
	
	//R3.1 Changes --- start
	private void setupSlowQueryLogging(T4Properties t4props) {
		try {
			if (t4props.getT4QueryExecuteLogFile() == null) {
				t4props.setT4QueryExecuteLogFile(t4props.getT4SlowQueryGlobalLogFile());
				t4props.setT4SlowQueryLogFileHandler(t4props.getT4SlowQueryGlobalLogFileHandler());
			} else {
				if (t4props.getT4SlowQueryLogFileHandler() == null) {
					String temp = t4props.getT4QueryExecuteLogFile();
					FileHandler fh1 = new FileHandler(temp);

					// KAS Degugging
					// System.out.println("In
					// SQLMXDataSource.setupLogFileHandler");
					// System.out.println(" File = " + temp);
					// System.out.println(" Handler = " + fh1.toString());

					Formatter ff1 = new T4LogFormatter();

					fh1.setFormatter(ff1);
					t4props.setT4SlowQueryLogFileHandler(fh1);
				}
				else{
					T4Properties.t4SlowQueryGlobalLogger.addHandler(t4props.getT4SlowQueryLogFileHandler());
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end setupSlowQueryLogging
	
   //R3.1 Changes --- End
	// --------------------------------------------------------

	// Interface Methods
	InterfaceConnection getServerHandle() {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"getServerHandle", "", p);
		}
		return ic_;
	}

	// Interface Methods
	public int getDialogueId() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			props_.t4Logger_.logp(Level.FINE, "SQLMXConnection",
					"getDialogueId", "", p);
		}
		if (props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXConnection");
			lr.setSourceMethodName("getDialogueId");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			props_.getLogWriter().println(temp);
		}
		return getServerHandle().getDialogueId();
	}

	/**
	 * Returns true if the data format needs to be converted. Used by the
	 * <CODE>SQLMXResultSet</CODE> class.
	 * 
	 * @return true if conversion is needed; otherwise, false.
	 */
	public boolean getDateConversion() throws SQLException {
		validateConnection();

		return ic_.getDateConversion();
	}

	int getServerMajorVersion() throws SQLException {
		validateConnection();

		return ic_.getServerMajorVersion();
	}

	int getServerMinorVersion() throws SQLException {
		validateConnection();

		return ic_.getServerMinorVersion();
	}

	void prepareGetLobLenStmt(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareGetLobLenStmt", "", p);
		}
		if (isBlob && (!bLobStatementPrepared[BLOB_GET_LOB_LEN_STMT])) {
			String lobLenSQL = "select sum(char_length(lob_data)) from "
					+ lobTableName
					+ " where table_name = ? and data_locator = ?";
			// BlobGetLobLenStmt_ = prepareLobStatement(lobLenSQL);
			LobPreparedStatements[BLOB_GET_LOB_LEN_STMT] = prepareLobStatement(lobLenSQL);
			bLobStatementPrepared[BLOB_GET_LOB_LEN_STMT] = true;
		} else if ((!isBlob) && (!bLobStatementPrepared[CLOB_GET_LOB_LEN_STMT])) {
			String lobLenSQL = "select sum(char_length(lob_data)) from "
					+ lobTableName
					+ " where table_name = ? and data_locator = ?";
			// ClobGetLobLenStmt_ = prepareLobStatement(lobLenSQL);
			LobPreparedStatements[CLOB_GET_LOB_LEN_STMT] = prepareLobStatement(lobLenSQL);
			bLobStatementPrepared[CLOB_GET_LOB_LEN_STMT] = true;
		}
	}

	void prepareDelLobDataStmt(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareDelLobDataStmt", "", p);
		}
		if (isBlob && (!bLobStatementPrepared[BLOB_DEL_LOB_DATA_STMT])) {
			String lobDelSQL = "delete from "
					+ lobTableName
					+ " where table_name = ? and data_locator = ? and chunk_no >= ? and chunk_no <= ?";
			// BlobDelLobDataStmt_ = prepareLobStatement(lobDelSQL);
			LobPreparedStatements[BLOB_DEL_LOB_DATA_STMT] = prepareLobStatement(lobDelSQL);
			bLobStatementPrepared[BLOB_DEL_LOB_DATA_STMT] = true;
		} else if ((!isBlob)
				&& (!bLobStatementPrepared[CLOB_DEL_LOB_DATA_STMT])) {
			String lobDelSQL = "delete from "
					+ lobTableName
					+ " where table_name = ? and data_locator = ? and chunk_no >= ? and chunk_no <= ?";
			// ClobDelLobDataStmt_ = prepareLobStatement(lobDelSQL);
			LobPreparedStatements[CLOB_DEL_LOB_DATA_STMT] = prepareLobStatement(lobDelSQL);
			bLobStatementPrepared[CLOB_DEL_LOB_DATA_STMT] = true;
		}
	}

	void prepareGetLobDataStmt(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareGetLobDataStmt", "", p);
		}
		if (isBlob && (!bLobStatementPrepared[BLOB_GET_LOB_DATA_STMT])) {
			String lobGetSQL = "select lob_data from "
					+ lobTableName
					+ " where table_name = ? and data_locator = ? and chunk_no >= ? and chunk_no <= ?"
					+ " order by chunk_no";
			// BlobGetLobDataStmt_ = prepareLobStatement(lobGetSQL);
			LobPreparedStatements[BLOB_GET_LOB_DATA_STMT] = prepareLobStatement(lobGetSQL);
			bLobStatementPrepared[BLOB_GET_LOB_DATA_STMT] = true;
		} else if ((!isBlob)
				&& (!bLobStatementPrepared[CLOB_GET_LOB_DATA_STMT])) {
			String lobGetSQL = "select lob_data from "
					+ lobTableName
					+ " where table_name = ? and data_locator = ? and chunk_no >= ? and chunk_no <= ?"
					+ " order by chunk_no";
			// ClobGetLobDataStmt_ = prepareLobStatement(lobGetSQL);
			LobPreparedStatements[CLOB_GET_LOB_DATA_STMT] = prepareLobStatement(lobGetSQL);
			bLobStatementPrepared[CLOB_GET_LOB_DATA_STMT] = true;
		}
	}

	void prepareUpdLobDataStmt(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareUpdLobDataStmt", "", p);
		}
		if (isBlob) {
			if (!bLobStatementPrepared[BLOB_UPD_LOB_DATA_STMT]) {
				String lobUpdSQL = "update "
						+ lobTableName
						+ " set lob_data = subString(lob_data, 1, ?) || cast (? as varchar(24000)) || substring(lob_data from ?) where table_name = ? and data_locator = ? and chunk_no = ?";
				// BlobUpdLobDataStmt_ = prepareLobStatement(lobUpdSQL);
				LobPreparedStatements[BLOB_UPD_LOB_DATA_STMT] = prepareLobStatement(lobUpdSQL);
				bLobStatementPrepared[BLOB_UPD_LOB_DATA_STMT] = true;
			} else {
				// BlobUpdLobDataStmt_.clearBatch();
				LobPreparedStatements[BLOB_UPD_LOB_DATA_STMT].clearBatch();
			}
		} else {
			if (!bLobStatementPrepared[CLOB_UPD_LOB_DATA_STMT]) {
				String lobUpdSQL = "update "
						+ lobTableName
						+ " set lob_data = subString(lob_data, 1, ?) || cast (? as varchar(24000)) || substring(lob_data from ?) where table_name = ? and data_locator = ? and chunk_no = ?";
				// ClobUpdLobDataStmt_ = prepareLobStatement(lobUpdSQL);
				LobPreparedStatements[CLOB_UPD_LOB_DATA_STMT] = prepareLobStatement(lobUpdSQL);
				bLobStatementPrepared[CLOB_UPD_LOB_DATA_STMT] = true;
			} else {
				// ClobUpdLobDataStmt_.clearBatch();
				LobPreparedStatements[CLOB_UPD_LOB_DATA_STMT].clearBatch();
			}
		}
	}

	void prepareInsLobDataStmt(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareInsLobDataStmt", "", p);
		}
		if (isBlob) {
			if (!bLobStatementPrepared[BLOB_INS_LOB_DATA_STMT]) {
				String lobInsSQL = "insert into "
						+ lobTableName
						+ " (table_name, data_locator, chunk_no, lob_data) values (?,?,?,?)";
				// BlobInsLobDataStmt_ = prepareLobStatement(lobInsSQL);
				LobPreparedStatements[BLOB_INS_LOB_DATA_STMT] = prepareLobStatement(lobInsSQL);
				bLobStatementPrepared[BLOB_INS_LOB_DATA_STMT] = true;
			} else {
				// BlobInsLobDataStmt_.clearBatch();
				LobPreparedStatements[BLOB_INS_LOB_DATA_STMT].clearBatch();
			}
		} else {
			if (!bLobStatementPrepared[CLOB_INS_LOB_DATA_STMT]) {
				String lobInsSQL = "insert into "
						+ lobTableName
						+ " (table_name, data_locator, chunk_no, lob_data) values (?,?,?,?)";
				// ClobInsLobDataStmt_ = prepareLobStatement(lobInsSQL);
				LobPreparedStatements[CLOB_INS_LOB_DATA_STMT] = prepareLobStatement(lobInsSQL);
				bLobStatementPrepared[CLOB_INS_LOB_DATA_STMT] = true;
			} else {
				// ClobInsLobDataStmt_.clearBatch();
				LobPreparedStatements[CLOB_INS_LOB_DATA_STMT].clearBatch();
			}
		}
	}

	void prepareTrunLobDataStmt(String lobTableName, boolean isBlob)
			throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareTrunLobDataStmt", "", p);
		}
		if (isBlob && (!bLobStatementPrepared[BLOB_TRUN_LOB_DATA_STMT])) {
			String lobTrunSQL = "update "
					+ lobTableName
					+ " set lob_data = substring(lob_data, 1, ?) where table_name = ? and data_locator = ? and chunk_no = ?";
			// BlobTrunLobDataStmt_ = prepareLobStatement(lobTrunSQL);
			LobPreparedStatements[BLOB_TRUN_LOB_DATA_STMT] = prepareLobStatement(lobTrunSQL);
			bLobStatementPrepared[BLOB_TRUN_LOB_DATA_STMT] = true;
		} else if ((!isBlob)
				&& (!bLobStatementPrepared[CLOB_TRUN_LOB_DATA_STMT])) {
			String lobTrunSQL = "update "
					+ lobTableName
					+ " set lob_data = substring(lob_data, 1, ?) where table_name = ? and data_locator = ? and chunk_no = ?";
			// ClobTrunLobDataStmt_ = prepareLobStatement(lobTrunSQL);
			LobPreparedStatements[CLOB_TRUN_LOB_DATA_STMT] = prepareLobStatement(lobTrunSQL);
			bLobStatementPrepared[CLOB_TRUN_LOB_DATA_STMT] = true;
		}
	}

	boolean prepareGetStrtDataLocStmt(String lobTableName, boolean isBlob,
			int charset) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_, lobTableName,
					isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareGetStrtDataLocStmt", "", p);
		}
		if (isBlob && (!bLobStatementPrepared[BLOB_GET_STRT_DATA_LOC_STMT])) {
			String lobGetStrtDataLocSQL;
			if (charset == InterfaceUtilities.SQLCHARSETCODE_UNICODE) {
				lobGetStrtDataLocSQL = "select * from (update "
						+ lobTableName
						+ " set lob_data = cast(cast(lob_data as largeint) + ? as varchar(100) character set UCS2) where table_name = 'ZZDATA_LOCATOR' and data_locator = 0 and chunk_no = 0 return cast(old.lob_data as largeint)+1) as tab1";
			} else // ISO data
			{
				lobGetStrtDataLocSQL = "select * from (update "
						+ lobTableName
						+ " set lob_data = cast(cast(lob_data as largeint) + ? as varchar(100) character set ISO88591) where table_name = 'ZZDATA_LOCATOR' and data_locator = 0 and chunk_no = 0 return cast(old.lob_data as largeint)+1) as tab1";
			}

			LobPreparedStatements[BLOB_GET_STRT_DATA_LOC_STMT] = prepareLobStatement(lobGetStrtDataLocSQL);
			bLobStatementPrepared[BLOB_GET_STRT_DATA_LOC_STMT] = true;
			return true;
		} else if ((!isBlob)
				&& (!bLobStatementPrepared[CLOB_GET_STRT_DATA_LOC_STMT])) {
			String lobGetStrtDataLocSQL;
			if (charset == InterfaceUtilities.SQLCHARSETCODE_UNICODE) {
				lobGetStrtDataLocSQL = "select * from (update "
						+ lobTableName
						+ " set lob_data = cast(cast(cast(lob_data as largeint) + ? as largeint) as varchar(100) character set UCS2) where table_name = 'ZZDATA_LOCATOR' and data_locator = 0 and chunk_no = 0 return cast(old.lob_data as largeint)+1) as tab1";
			} else {
				lobGetStrtDataLocSQL = "select * from (update "
						+ lobTableName
						+ " set lob_data = cast(cast(cast(lob_data as largeint) + ? as largeint) as varchar(100) character set ISO88591) where table_name = 'ZZDATA_LOCATOR' and data_locator = 0 and chunk_no = 0 return cast(old.lob_data as largeint)+1) as tab1";
			}

			LobPreparedStatements[CLOB_GET_STRT_DATA_LOC_STMT] = prepareLobStatement(lobGetStrtDataLocSQL);
			bLobStatementPrepared[CLOB_GET_STRT_DATA_LOC_STMT] = true;
			return true;
		}
		return false;
	}

	void prepareInsSpjLobDataStmt(boolean isBlob) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,
					spjBlobTableName_, isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareInsSpjLobDataStmt", "", p);
		}
		if (isBlob) {
			if (!spjLobStatementPrepared[SPJ_INS_BLOB_STMT]) {
				String lobInsSQL = "insert into "
						+ spjBlobTableName_
						+ " (table_name, data_locator, chunk_no, lob_data) values (?,?,?,?)";
				SpjLobPreparedStatements[SPJ_INS_BLOB_STMT] = prepareLobStatement(lobInsSQL);
				spjLobStatementPrepared[SPJ_INS_BLOB_STMT] = true;
			} else {
				SpjLobPreparedStatements[SPJ_INS_BLOB_STMT].clearBatch();
			}
		} else {
			if (!spjLobStatementPrepared[SPJ_INS_CLOB_STMT]) {
				String lobInsSQL = "insert into "
						+ spjBlobTableName_
						+ " (table_name, data_locator, chunk_no, lob_data) values (?,?,?,?)";
				SpjLobPreparedStatements[SPJ_INS_CLOB_STMT] = prepareLobStatement(lobInsSQL);
				spjLobStatementPrepared[SPJ_INS_CLOB_STMT] = true;
			} else {
				SpjLobPreparedStatements[SPJ_INS_CLOB_STMT].clearBatch();
			}
		}
	}

	void prepareInsSpjBaseDataStmt(boolean isBlob) throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,
					spjBlobTableName_, isBlob,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareInsSpjBaseDataStmt", "", p);
		}
		if (isBlob) {
			if (!spjLobStatementPrepared[SPJ_INS_BASE_STMT]) {
				String lobInsSQL = "insert into " + spjBaseTableName_
						+ "(blob_col)" + " values (?)";
				SpjLobPreparedStatements[SPJ_INS_BASE_STMT] = prepareLobStatement(lobInsSQL);
				spjLobStatementPrepared[SPJ_INS_BASE_STMT] = true;
			} else {
				SpjLobPreparedStatements[SPJ_INS_BASE_STMT].clearBatch();
			}
		}
	}

	void prepareGetSpjLobDataStmt() throws SQLException {
		if (props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,
					spjBlobTableName_,this);
			props_.t4Logger_.logp(Level.FINER, "SQLMXConnection",
					"prepareGetSpjLobDataStmt", "", p);
		}
		if (!spjLobStatementPrepared[SPJ_GET_LOB_DATA_STMT]) {
			String lobGetSQL = "select lob_data from "
					+ spjBlobTableName_
					+ " where table_name = ? and data_locator = ? and chunk_no >= ? and chunk_no <= ?";
			SpjLobPreparedStatements[SPJ_GET_LOB_DATA_STMT] = prepareLobStatement(lobGetSQL);
			spjLobStatementPrepared[SPJ_GET_LOB_DATA_STMT] = true;
		} else {
			SpjLobPreparedStatements[SPJ_GET_LOB_DATA_STMT].clearBatch();
		}
	}

	void closeErroredConnection(SQLMXException se) {
		// start sol 10-080812-5141
		//Soln.: 10-100713-1762
		if (props_ != null) {
		//End of Soln.: 10-100713-1762
		if (props_.t4Logger_.isLoggable(Level.SEVERE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(props_,this);
			String errMsg = "Connection is closed due to unrecoverable error ";
			try {
				if (!(se.getMessage() == null)) {
					errMsg = errMsg + "  " + se.getMessage();
				}
			} catch (Exception e) {
			}
			props_.t4Logger_.logp(Level.SEVERE, "SQLMXConnection",
					"closeErroredConnection", errMsg, p);
		}
		}
		// End sol 10-080812-5141
		try {
			if (!erroredConnection) { // don't issue close repeatedly
				erroredConnection = true;
				if (pc_ != null) {
					pc_.sendConnectionErrorEvent(se);
				} else {
					// hardclose
					close(true, true);
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public synchronized int incrementAndGetStmtLabelHandleCount() {
		//Modified for soln 10-111007-0024
		if(stmtLabelHandleCount == 99999999){
			stmtLabelHandleCount = 0;
		}
		return ++stmtLabelHandleCount;
	}

	public boolean containsScalarFunctions(String sqlStatement) {
		String sqlStatementInternal = sqlStatement.toUpperCase().trim();

		if (sqlStatementsThatCanBeMFCed.contains(sqlStatementInternal)) {
			return false;
		}
		if (sqlStatementsContainingScalarFunctions
				.contains(sqlStatementInternal)) {
			return true;
		}
		boolean result = false;
		if (sqlStatementInternal.startsWith("SELECT")) {
			int indexOfFrom = sqlStatementInternal.indexOf("FROM");
			String sqlStmtFragment = sqlStatementInternal.substring("SELECT"
					.length(), indexOfFrom);
			/*
			 * First check if the functions used in the Fragment, is used with
			 * spaces or tabs. For e.g: SQRT (RATE)
			 */
			String array[] = sqlStmtFragment.split("[ \t,]");
			for (int nfor = 0; nfor < array.length; ++nfor) {
				/* If the function is found then we break immediately */
				if (scalarFunctionsSet.contains(array[nfor].trim())) {
					result = true;
					break;
				}
			}
			/*
			 * If the functions are used in the Fragment but they dont have the
			 * spaces, for e.g: SQRT(RATE) then we have a longer Process. We
			 * check if each Scalar function is the starting part of the each
			 * word in the fragment and if the next character is a "(". Then we
			 * declare that this is a Statement which uses the Scalar function.
			 */
			if (!result) {
				Iterator iterOfScalarFunctions = scalarFunctionsSet.iterator();
				while (iterOfScalarFunctions.hasNext()) {
					String scalarFunction = (String) iterOfScalarFunctions
							.next();
					for (int nfor = 0; nfor < array.length; ++nfor) {
						if (array[nfor].trim().startsWith(scalarFunction)) {
							String subString = array[nfor].trim().substring(
									scalarFunction.length());
							if (subString.startsWith("(")) {
								result = true;
								break;
							}
						}
					}
					if (result) {
						break;
					}
				}
			}
		}
		// Next time this SQL Statement comes we just return true/false
		if (result) {
			sqlStatementsContainingScalarFunctions.add(sqlStatementInternal);
		} else {
			sqlStatementsThatCanBeMFCed.add(sqlStatementInternal);
		}
		return result;
	}
	
	public Blob createBlob() throws SQLException { 
		if (props_.getBlobTableName() == null) {
			SQLException se = SQLMXMessages.createSQLException(null, null,
					"no_blobTableName", null);
			throw se;
		}
		return new SQLMXBlob(this);
	}
	
	public Clob createClob() throws SQLException { 
		if (props_.getClobTableName() == null) {
			SQLException se = SQLMXMessages.createSQLException(null, null,
					"no_clobTableName", null);
			throw se;
		}
		return new SQLMXClob(this);
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
	

	// static HashMap lobTableToDataLoc_;
	static Hashtable lobTableToDataLoc_;

	boolean erroredConnection = false;

	PreparedStatement[] LobPreparedStatements = new PreparedStatement[14];

	PreparedStatement[] SpjLobPreparedStatements = new PreparedStatement[4];
	// boolean reserveEmptyDataLocator_ = false;
	// public static final int EMPTY_DATA_LOCATOR_UPDATE = 0;
	// FOR SPJBLOB -R3.0
	String spjBlobTableName_;
	String spjClobTableName_;
	String spjBaseTableName_;
	boolean isCallProc_;
	String catalog_;
	String schema_;

	String clobTableName_;

	String blobTableName_;

	// String preparedClobTableName_;
	// String preparedBlobTableName_;

	static final int CLOB_INS_LOB_DATA_STMT = 0;

	static final int CLOB_GET_LOB_DATA_STMT = 1;

	static final int CLOB_GET_LOB_LEN_STMT = 2;

	static final int CLOB_DEL_LOB_DATA_STMT = 3;

	static final int CLOB_TRUN_LOB_DATA_STMT = 4;

	static final int CLOB_UPD_LOB_DATA_STMT = 5;

	static final int CLOB_GET_STRT_DATA_LOC_STMT = 6;

	static final int BLOB_INS_LOB_DATA_STMT = 7;

	static final int BLOB_GET_LOB_DATA_STMT = 8;

	static final int BLOB_GET_LOB_LEN_STMT = 9;

	static final int BLOB_DEL_LOB_DATA_STMT = 10;

	static final int BLOB_TRUN_LOB_DATA_STMT = 11;

	static final int BLOB_UPD_LOB_DATA_STMT = 12;

	static final int BLOB_GET_STRT_DATA_LOC_STMT = 13;

	static Logger dummyLogger_ = null;

	boolean[] bLobStatementPrepared = new boolean[14]; // initialized to false,

	// one each for the
	// BLOB/CLOB statements
	// listed above

	boolean[] spjLobStatementPrepared = new boolean[4];
	static final int SPJ_INS_BLOB_STMT = 0; // For blob insertion
	static final int SPJ_INS_CLOB_STMT = 1; // For clob insertion - not
	static final int SPJ_INS_BASE_STMT = 2; // For inserting into base table
	static final int SPJ_GET_LOB_DATA_STMT = 3; // For retriving from Blob table
	// Fields
	InterfaceConnection ic_;

	// Connection
	Map userMap_;

	ReferenceQueue refStmtQ_;

	Hashtable refToStmt_;

	Hashtable refToRS_;

	int holdability_;

	SQLMXDataSource ds_;

	SQLMXPooledConnection pc_;

	SQLMXDriver driver_;

	WeakReference pRef_;

	T4Properties props_;

	boolean m_isXA_ = false;
	boolean isActiveTrans = false;

	static HashSet sqlStatementsContainingScalarFunctions = new HashSet();
	static HashSet sqlStatementsThatCanBeMFCed = new HashSet();
	/* The following is to store the List of Scalar Functions */
	static HashSet scalarFunctionsSet = new HashSet();

	private int stmtLabelHandleCount = 0;

	byte[] transactionToJoin;
	
	//Added for Solution 10-160301-9171
	String connectionID_;
	String dialogueID_;
	String serverID_;

	

	// LOB Support - SB 9/28/04
	static {
		// lobTableToDataLoc_ = new HashMap();
		lobTableToDataLoc_ = new Hashtable(2);
	}

	//JDBC 4.x stubs
	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "createNClob()");		
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "createSQLXML()");
		return null;
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		try {
			SQLMXMessages.throwUnsupportedFeatureException(this.props_,
					this.getLocale(), "setClientInfo(String name, String value)");
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
			SQLMXMessages.throwUnsupportedFeatureException(this.props_,
					this.getLocale(), "setClientInfo(Properties properties)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "getClientInfo(String name)");		
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "getClientInfo()");		
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "createArrayOf(String typeName, Object[] elements)");		
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "createStruct(String typeName, Object[] attributes)");		
		return null;
	}

	/*@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(this.props_,
				this.getLocale(), "abort(Executor executor)");
	}*/

	/*
	 * JDK 1.6 functions public Clob createClob() throws SQLException { return
	 * null; }
	 * 
	 * 
	 * public Blob createBlob() throws SQLException { return null; }
	 * 
	 * 
	 * public NClob createNClob() throws SQLException { return null; }
	 * 
	 * 
	 * public SQLXML createSQLXML() throws SQLException { return null; }
	 * 
	 * 
	 * public boolean isValid(int _int) throws SQLException { return false; }
	 * 
	 * 
	 * public void setClientInfo(String string, String string1) throws
	 * SQLClientInfoException { }
	 * 
	 * 
	 * public void setClientInfo(Properties properties) throws
	 * SQLClientInfoException { }
	 * 
	 * 
	 * public String getClientInfo(String string) throws SQLException { return
	 * ""; }
	 * 
	 * 
	 * public Properties getClientInfo() throws SQLException { return null; }
	 * 
	 * 
	 * public Array createArrayOf(String string, Object[] objectArray) throws
	 * SQLException { return null; }
	 * 
	 * 
	 * public Struct createStruct(String string, Object[] objectArray) throws
	 * SQLException { return null; }
	 * 
	 * 
	 * public Object unwrap(Class _class) throws SQLException { return null; }
	 * 
	 * 
	 * public boolean isWrapperFor(Class _class) throws SQLException { return
	 * false; }
	 */
}
