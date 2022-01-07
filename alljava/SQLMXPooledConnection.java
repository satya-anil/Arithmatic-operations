// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003, 2004, 2005
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;

public class SQLMXPooledConnection implements javax.sql.PooledConnection {

	public void addConnectionEventListener(ConnectionEventListener listener) {
		try {
			if (connection_ != null
					&& connection_.props_.t4Logger_.isLoggable(Level.INFO) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
						listener,connection_);
				connection_.props_.t4Logger_.logp(Level.INFO,
						"SQLMXPooledConnecton", "addConnectionEventListener",
						"", p);
			}
			if (connection_ != null
					&& connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.INFO, "");
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
						listener,connection_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXPooledConnection");
				lr.setSourceMethodName("addConnectionEventListener");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		if (isClosed_ || connection_ == null) {
			return;
		}
		listenerList_.add(listener);
	}

	public void close() throws SQLException {
		if (connection_ != null
				&& connection_.props_.t4Logger_.isLoggable(Level.INFO) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.INFO,
					"SQLMXPooledConnecton", "close", "", p);
		}
		if (connection_ != null && connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.INFO, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPooledConnection");
			lr.setSourceMethodName("close");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (isClosed_) {
			return;
		}
		connection_.close(true, true);
	}

	public Connection getConnection() throws SQLException {
		if (connection_ != null
				&& connection_.props_.t4Logger_.isLoggable(Level.INFO) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.INFO,
					"SQLMXPooledConnecton", "getConnection", "", p);
		}
		if (connection_ != null && connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.INFO, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPooledConnection");
			lr.setSourceMethodName("getConnection");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (isClosed_ || connection_ == null) {
			throw SQLMXMessages.createSQLException(connection_.props_, locale_,
					"invalid_connection", null);
		}
		if (LogicalConnectionInUse_) {
			connection_.close(false, false);
		}
		LogicalConnectionInUse_ = true;
		connection_.reuse();
		return connection_;
	}

	public void removeConnectionEventListener(ConnectionEventListener listener) {
		try {
			if (connection_ != null
					&& connection_.props_.t4Logger_.isLoggable(Level.INFO) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
						listener,connection_);
				connection_.props_.t4Logger_.logp(Level.INFO,
						"SQLMXPooledConnecton",
						"removeConnectionEventListener", "", p);
			}
			if (connection_ != null
					&& connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.INFO, "");
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
						listener,connection_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXPooledConnection");
				lr.setSourceMethodName("removeConnectionEventListener");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		if (isClosed_ || connection_ == null) {
			return;
		}
		listenerList_.remove(listener);
	}

	// Called by SQLMXConnection when the connection is closed by the
	// application
	void logicalClose(boolean sendEvents) {
		int i;
		int totalListener;
		ConnectionEventListener listener;

		LogicalConnectionInUse_ = false;

		if (sendEvents) {
			totalListener = listenerList_.size();
			ConnectionEvent event = new ConnectionEvent(this);
			for (i = 0; i < totalListener; i++) {
				listener = (ConnectionEventListener) listenerList_.get(i);
				listener.connectionClosed(event);
			}
		}
	}

	void sendConnectionErrorEvent(SQLException ex) throws SQLException {
		int i;
		int totalListener;
		ConnectionEventListener listener;

		LogicalConnectionInUse_ = false;
		totalListener = listenerList_.size();
		ConnectionEvent event = new ConnectionEvent(this, ex);
		for (i = 0; i < totalListener; i++) {
			listener = (ConnectionEventListener) listenerList_.get(i);
			listener.connectionErrorOccurred(event);
		}
		close();
	}

	// Constructor
	SQLMXPooledConnection(SQLMXConnectionPoolDataSource pds,
			T4Properties t4props) throws SQLException {
		super();

		T4Properties t4LocalProps;

		pds_ = pds;
		if (t4props != null) {
			t4LocalProps = t4props;
			locale_ = t4props.getLocale();
		} else {
			t4LocalProps = new T4Properties();
			locale_ = Locale.getDefault();
		}
		listenerList_ = new LinkedList();
		connection_ = new SQLMXConnection(this, t4LocalProps);
		try {
			if (connection_ != null
					&& connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
						pds, t4props,connection_);
				connection_.props_.t4Logger_.logp(Level.FINE,
						"SQLMXPooledConnecton", "", "", p);
			}
			if (connection_ != null
					&& connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
						pds, t4props,connection_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXPooledConnection");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
	}

	SQLMXConnection getSQLMXConnectionReference() {
		return connection_;
	}

	protected boolean isClosed() throws SQLException {
		return (connection_ == null || isClosed_);
	}

	private LinkedList listenerList_;

	private boolean isClosed_ = false;

	private SQLMXConnectionPoolDataSource pds_;

	private SQLMXConnection connection_;

	private Locale locale_;

	private boolean LogicalConnectionInUse_ = false;

	//JDBC 4.x stubs
	@Override
	public void addStatementEventListener(StatementEventListener listener) {
		// TODO Auto-generated method stub

		try {
			SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
					connection_.getLocale(), "addStatementEventListener(StatementEventListener listener)");
		} catch (SQLMXException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void removeStatementEventListener(StatementEventListener listener) {
		// TODO Auto-generated method stub
		try {
			SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
					connection_.getLocale(), "removeStatementEventListener(StatementEventListener listener)");
		} catch (SQLMXException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
