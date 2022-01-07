/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2003-2007, 2015-2016 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.tandem.t4jdbc.CachedPreparedStatement;
import com.tandem.t4jdbc.PreparedStatementManager;

public class SQLMXPreparedStatement extends SQLMXStatement implements
		java.sql.PreparedStatement {
	// java.sql.PreparedStatement interface methods
	public void addBatch() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "addBatch", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("addBatch");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);

		}

		if (ExecuteBatchDone_ == true) {
			rowsValue_.clear();
			paramRowCount_ = 0;
			ExecuteBatchDone_ = false;
		}
		
		if (inputDesc_ == null) {
			return;
		}

		// Check if all parameters are set for current set
		checkIfAllParamsSet();
		// Add to the number of Rows Count
		if (rowsValue_ == null) {
			rowsValue_ = new ArrayList();
		}
		rowsValue_.add(paramsValue_);
		paramRowCount_++;
		paramsValue_ = new Object[inputDesc_.length];
		if (isAnyLob_ && (lobObjects_ == null)) {
			lobObjects_ = new ArrayList();
			// Clear the isValueSet_ flag in inputDesc_ and add the lob objects
			// to the lobObject List
		}
		for (int i = 0; i < inputDesc_.length; i++) {
			// If isAnyLob_ is false: inputDesc_.paramValue_ for all
			// parameters should be null
			// If isAnyLob_ is true: one or more inputDesc_.parmValue will not
			// be null, based on the number of LOB columns in the query
			if (inputDesc_[i].paramValue_ != null) {
				lobObjects_.add(inputDesc_[i].paramValue_);
				inputDesc_[i].paramValue_ = null;
			}
			inputDesc_[i].isValueSet_ = false;
		}
	}

	public void clearBatch() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "clearBatch", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("clearBatch");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		if (inputDesc_ == null) {
			return;
		}
		if (rowsValue_ != null) {
			rowsValue_.clear();
		}
		if (lobObjects_ != null) {
			lobObjects_.clear();
		}
		paramRowCount_ = 0;
		// Clear the isValueSet_ flag in inputDesc_
		for (int i = 0; i < inputDesc_.length; i++) {
			inputDesc_[i].isValueSet_ = false;
			paramsValue_[i] = null;
			inputDesc_[i].paramValue_ = null;
		}
		isAnyLob_ = false;
		batchRowCount_ = new int[] {};
	}

	public void clearParameters() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "clearParameters", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("clearParameters");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// Clear the isValueSet_ flag in inputDesc_
		if (inputDesc_ == null) {
			return;
		}

		for (int i = 0; i < inputDesc_.length; i++) {
			inputDesc_[i].isValueSet_ = false;
			paramsValue_[i] = null;
			inputDesc_[i].paramValue_ = null;
		}
		isAnyLob_ = false;
	}

	// sol 10-070613-5509 method is modified as sychronized -R3.0
	synchronized public void close() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "close", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("close");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		if (isClosed_) {
			return;
		}
		try {
			if (connection_._isClosed() == false) {
				if (!connection_.isStatementCachingEnabled()) {
					super.close();
				} else {

					// start sol 10-080909-5718 R3.0
					// logicalClose();
					PreparedStatementManager psmgr = (PreparedStatementManager) this.connection_;
					Iterator iter = psmgr.prepStmtsInCache_.values().iterator();
					boolean cached = false;

					while (iter.hasNext()) {
						if (this == ((CachedPreparedStatement) iter.next())
								.getPreparedStatement()) {
							cached = true;
							break;
						}
					}
					if (cached) {
						logicalClose();
					} else {
						super.close();
					}
					// End sol 10-080909-5718 R3.0

				}
			}
		} catch (SQLException e) {
			performConnectionErrorChecks(e);
			throw e;
		} finally {
			isClosed_ = true;
			if (!connection_.isStatementCachingEnabled()) {
				connection_.removeElement(pRef_);
			}
		}

	}

	// sol 10-070613-5509 method is modified as sychronized -R3.0
	synchronized public boolean execute() throws SQLException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "execute", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("execute");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Object[] valueArray = null;
		int inDescLength = 0;

		validateExecuteInvocation();

		// *******************************************************************
		// * If LOB is involved with autocommit enabled we throw an exception
		// *******************************************************************
		if (isAnyLob_ && (connection_.getAutoCommit())) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_lob_commit_state", null);
		}
		if (inputDesc_ != null) {
			if (!usingRawRowset_)
				valueArray = getValueArray();
			inDescLength = inputDesc_.length;
		}

		if (connection_.getAutoCommit() == false) {
			connection_.isActiveTrans = true;
		}
		execute(paramRowCount_ + 1, inDescLength, valueArray, queryTimeout_,
				isAnyLob_); // LOB
		// Support
		// - SB
		// 9/28/04

		// if (resultSet_[result_set_offset] != null)
		if (resultSet_ != null && resultSet_[result_set_offset] != null) {
			return true;
		} else {
			if (isAnyLob_) {
				populateLobObjects();
			}
			return false;
		}
	}

	// sol 10-070613-5509 method is modified as sychronized -R3.0
	synchronized public int[] executeBatch() throws SQLException,
			BatchUpdateException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "executeBatch", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("executeBatch");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		try {
			clearWarnings();
			SQLMXException se;
			Object[] valueArray = null;

			if (inputDesc_ == null) {
				se = SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "batch_command_failed", null);
				throw new BatchUpdateException(se.getMessage(), se
						.getSQLState(), new int[0]);
			}
			if (sqlStmtType_ == TRANSPORT.TYPE_SELECT) {
				se = SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(),
						"select_in_batch_not_supported", null);
				throw new BatchUpdateException(se.getMessage(), se
						.getSQLState(), new int[0]);
			}
			if (connection_._isClosed()) {
				se = SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_connection", null);
				connection_.closeErroredConnection(se);
				throw new BatchUpdateException(se.getMessage(), se
						.getSQLState(), new int[0]);
			}
			if (isAnyLob_ && (connection_.getAutoCommit())) {
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_lob_commit_state",
						null);
			}

			int prc = usingRawRowset_ ? (paramRowCount_ + 1) : paramRowCount_;

			if (paramRowCount_ < 1) {
				if (!connection_.props_.getDelayedErrorMode()) {
					return (new int[] {});
				}
			}
			if (connection_.getAutoCommit() == false) {
				connection_.isActiveTrans = true;
			}

			try {
				if (!usingRawRowset_)
					valueArray = getValueArray();

				execute(prc, inputDesc_.length, valueArray, queryTimeout_,
						lobObjects_ != null);
				populateBatchLobObjects();
			} catch (SQLException e) {
				BatchUpdateException be;
				se = SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "batch_command_failed", null);
				if (batchRowCount_ == null) // we failed before execute
				{
					batchRowCount_ = new int[paramRowCount_];
					Arrays.fill(batchRowCount_, -3);
				}
				be = new BatchUpdateException(se.getMessage(),
						se.getSQLState(), batchRowCount_);
				be.setNextException(e);

				throw be;
			}
			ExecuteBatchDone_ = true;

			return batchRowCount_;
		} finally {
			clearBatch();
		}
	}

	// sol 10-070613-5509 method is modified as sychronized -R3.0
	synchronized public ResultSet executeQuery() throws SQLException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "executeQuery", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("executeQuery");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Object[] valueArray = null;
		int inDescLength = 0;

		validateExecuteInvocation();
		if (sqlStmtType_ != TRANSPORT.TYPE_SELECT) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "non_select_invalid", null);
		}

		if (inputDesc_ != null) {
			if (!usingRawRowset_)
				valueArray = getValueArray();
			inDescLength = inputDesc_.length;
		}
		if (connection_.getAutoCommit() == false) {
			connection_.isActiveTrans = true;
		}
		//Soln 10-120803-4309 Commented try-catch block
		//try {
			execute(paramRowCount_ + 1, inDescLength, valueArray,
					queryTimeout_, isAnyLob_); // LOB
		//} catch (Exception e) {
			//e.printStackTrace();
			// if (this.connection_.props_.getCloseConnectionUponQueryTimeout()
			// .equals(SQLMXSwitchConCloseOnQueryTimeout.ON)
			// && e.getCause() instanceof SocketTimeoutException) {
			// if (e.getCause().getMessage().equals("Read timed out")) {
			// StopServerReply sr = SQLMX_AssociationServer_Connect
			// .stopServer(this.connection_.props_,
			// this.connection_.ic_, 2,
			// TRANSPORT.AS_API_STOPSRVR);
			// SQLMXException ex = SQLMXMessages.createSQLException(
			// this.connection_.props_, this.connection_.ic_
			// .getLocale(), "AS_API_STOPSRVR", null);
			// this.connection_.ic_.isClosed = true;
			// ex.initCause(e.getCause());
			// throw ex;
			// } else {
			// throw e;
			// }
			// } else {
			// throw e;
			// }
		//}

		// Support
		// - SB
		// 9/28/04
		// Soln 10-171030-5324 : executeQuery() does not call populateLobObjects
        if (isAnyLob_)
          populateLobObjects();

		return resultSet_[result_set_offset];
	}

	// sol 10-070613-5509 method is modified as sychronized -R3.0
	synchronized public int executeUpdate() throws SQLException {
		long count = executeUpdate64();

		if (count > Integer.MAX_VALUE)
			this.setSQLWarning(null, "numeric_out_of_range", null);

		return (int) count;
	}

	public long executeUpdate64() throws SQLException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "executeUpdate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("executeUpdate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Object[] valueArray = null;
		int inDescLength = 0;

		validateExecuteInvocation();
		// 7708
		// if (sqlStmtType_ == TRANSPORT.TYPE_SELECT)
		if (sqlStmtType_ == TRANSPORT.TYPE_SELECT && (ist_.stmtIsLock != true)) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "select_invalid", null);
		}

		if (usingRawRowset_ == false) {
			if (inputDesc_ != null) {
				if (!usingRawRowset_)
					valueArray = getValueArray();
				inDescLength = inputDesc_.length;
			}
		} else {
			valueArray = this.paramsValue_; // send it along raw in case we need
			// it
			paramRowCount_ -= 1; // we need to make sure that paramRowCount
			// stays exactly what we set it to since we
			// add one during execute
		}

		// *******************************************************************
		// * If LOB is involved with autocommit enabled we throw an exception
		// *******************************************************************
		if (isAnyLob_ && (connection_.getAutoCommit())) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_lob_commit_state", null);
		}

		if (connection_.getAutoCommit() == false) {
			connection_.isActiveTrans = true;
		}
		// try {
		execute(paramRowCount_ + 1, inDescLength, valueArray, queryTimeout_,
				isAnyLob_); // LOB

		// } catch (Exception e) {
		// if (this.connection_.props_.getCloseConnectionUponQueryTimeout()
		// .equals(SQLMXSwitchConCloseOnQueryTimeout.ON)
		// && e.getCause() instanceof SocketTimeoutException) {
		// if (e.getCause().getMessage().equals("Read timed out")) {
		//
		// // ex.initCause(e.getCause());
		// // throw ex;
		// CancelReply cr_ = null;
		// cr_ = SQLMX_AssociationServer_Cancel.cancel(
		// this.connection_.props_, this.connection_.ic_,
		// this.connection_.ic_.getDialogueId(), 2,
		// this.connection_.ic_.ncsAddr_.m_url + "", 0);
		//
		// SQLMXException ex = SQLMXMessages.createSQLException(
		// this.connection_.props_, this.connection_.ic_
		// .getLocale(), "AS_API_STOPSRVR", null);
		// // this.connection_.ic_.isClosed = true;
		// ex.initCause(e.getCause());
		// throw ex;
		//
		// } else {
		// try {
		// throw e;
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
		// } else {
		// try {
		// throw e;
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
		// }
		// Support
		// - SB
		// 9/28/04
		if (isAnyLob_) {
			populateLobObjects();
		}
		return ist_.getRowCount();
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "getMetaData", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("getMetaData");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		if (outputDesc_ != null) {
			return new SQLMXResultSetMetaData(this, outputDesc_);
		} else {
			return null;
		}
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "getParameterMetaData", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("getParameterMetaData");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (inputDesc_ != null) {
			return new SQLMXParameterMetaData(this, inputDesc_);
		} else {
			return null;
		}
	}

	// JDK 1.2
	public void setArray(int parameterIndex, Array x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setArray", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setArray");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setArray()");
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, length,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setAsciiStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, length,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setAsciiStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long dataLocator;

		validateSetInvocation(parameterIndex);

		dataType = inputDesc_[parameterIndex - 1].dataType_;

		// LOB Support - SB 9/28/04
		switch (dataType) {
		case Types.CLOB:
			dataLocator = connection_.getDataLocator(
					connection_.clobTableName_, false);
			SQLMXClob clob = new SQLMXClob(connection_,
					inputDesc_[parameterIndex - 1].tableName_, dataLocator, x,
					length);
			inputDesc_[parameterIndex - 1].paramValue_ = clob;
			isAnyLob_ = true;
			// addParamValue(parameterIndex, new DataWrapper(dataLocator));
			addParamValue(parameterIndex, new Long(dataLocator));
			break;
		case Types.BLOB:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.BINARY: // At this time Nonstop SQL/MX Database does not have
			// this column data type
		case Types.VARBINARY: // At this time Nonstop SQL/MX Database does not
			// have this column data type
		case Types.LONGVARBINARY: // At this time Nonstop SQL/MX Database does not
			// have this column data type
			byte[] buffer = new byte[length];
			try {
				x.read(buffer);
			} catch (java.io.IOException e) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = e.getMessage();
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "io_exception",
						messageArguments);
			}

			try {
				addParamValue(parameterIndex, new String(buffer, "ASCII"));
			} catch (java.io.UnsupportedEncodingException e) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = e.getMessage();
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "unsupported_encoding",
						messageArguments);
			}
			break;
		default:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_datatype_for_column",
					null);
		}
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, parameterIndex, x, connection_);
			connection_.props_.t4Logger_.logp(Level.FINE, "SQLMXPreparedStatement", "setBigDecimal", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, parameterIndex, x, connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setBigDecimal");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_.getLocale());

		if (x != null) {
			// Soln 10-091208-6911 -- start R30
			// Soln 10-101028-4072 -- Start

			if (inputDesc_[parameterIndex - 1].dataType_ == Types.NUMERIC
					|| inputDesc_[parameterIndex - 1].dataType_ == Types.DECIMAL) {
				// Soln 10-101028-4072 -- End
				x = Utility.setScale(x, inputDesc_[parameterIndex - 1].scale_, roundingMode_);
				// Soln 10-091208-6911 -- End
			}
			// Added to fix pre-RQA defect 893
			switch (inputDesc_[parameterIndex - 1].dataType_) {

			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				break;
			default:
				Utility.checkDecimalTruncation(parameterIndex, connection_.getLocale(), x,
						inputDesc_[parameterIndex - 1].precision_, inputDesc_[parameterIndex - 1].scale_);
			}
			addParamValue(parameterIndex, x.toString());
		} else {
			addParamValue(parameterIndex, null);
		}
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, length,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setBinaryStream",
					"setBinaryStream", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, length,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setBinaryStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long dataLocator;

		validateSetInvocation(parameterIndex);

		dataType = inputDesc_[parameterIndex - 1].dataType_;

		switch (dataType) {
		case Types.CLOB:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		case Types.BLOB:
			// Added if for spj lob support - R3.0
			if (connection_.isCallProc_) {
				dataLocator = connection_.getDataLocator(
						connection_.spjBlobTableName_, true);
				SQLMXBlob blob = new SQLMXBlob(connection_,
						connection_.spjBaseTableName_, dataLocator, x, length);
				inputDesc_[parameterIndex - 1].paramValue_ = blob;
			} else {
				dataLocator = connection_.getDataLocator(
						connection_.blobTableName_, true);
				SQLMXBlob blob = new SQLMXBlob(connection_,
						inputDesc_[parameterIndex - 1].tableName_, dataLocator,
						x, length);
				inputDesc_[parameterIndex - 1].paramValue_ = blob;
			}
			isAnyLob_ = true;

			// addParamValue(parameterIndex, new DataWrapper(dataLocator));
			addParamValue(parameterIndex, new Long(dataLocator));
			break;

		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.FLOAT:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_datatype_for_column",
					null);
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.BINARY: // At this time Nonstop SQL/MX Database does not have
			// this column data type
		case Types.VARBINARY: // At this time Nonstop SQL/MX Database does not
			// have this column data type
		case Types.LONGVARBINARY: // At this time Nonstop SQL/MX Database does not
			// have this column data type
			byte[] buffer2 = new byte[length];

			try {
				int temp = x.read(buffer2);
			} catch (java.io.IOException e) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = e.getMessage();
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "io_exception",
						messageArguments);
			}
			addParamValue(parameterIndex, buffer2);
			break;
		default:
			byte[] buffer = new byte[length];

			try {
				x.read(buffer);
			} catch (java.io.IOException e) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = e.getMessage();
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "io_exception",
						messageArguments);
			}

			// addParamValue(parameterIndex, new String(buffer));
			// just pass the raw buffer.
			addParamValue(parameterIndex, buffer);
		}
	}
	
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		if (length > Integer.MAX_VALUE) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_parameter_value",
					"Blob size is too big to handle");
		}
		setBinaryStream(parameterIndex, x, (int)length);
	}
	
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		if (length > Integer.MAX_VALUE) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_parameter_value",
					"Clob size is too big to handle");
		}
		setAsciiStream(parameterIndex, x, (int)length);
	}

	/*
	 * Sets the designated parameter to the given <tt>Blob</tt> object. The
	 * driver converts this to an SQL <tt>BLOB</tt> value when it sends it to
	 * the database.
	 * 
	 * @param i the first parameter is 1, the second is 2, ... @param x a <tt>Blob</tt>
	 * object that maps an SQL <tt>BLOB</tt> value
	 * 
	 * @throws SQLException invalid data type for column
	 */
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setBlob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setBlob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long dataLocator;
		// Soln 10-091027-5709 - start R3.0
		if (x == null) {
			setNull(parameterIndex, java.sql.Types.BLOB);
			return;
		}
		// Soln 10-091027-5709 - End R3.0
		
		if (connection_.getAutoCommit()) {
			// *******************************************************************
			// * If LOB is involved with autocommit enabled we throw an exception
			// *******************************************************************
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_lob_commit_state", null);
		}
		
		byte[] b = null;
		SQLMXBlob blob = null;
		if ((x instanceof SQLMXBlob) && (((SQLMXBlob)x).isConfiguredLob())) {
			if (((SQLMXBlob)x).binaryData_ != null) {
				int length = (int)x.length();
		
				if (length > 0)
					b = x.getBytes(1, length);
			} else if (((SQLMXBlob)x).outputStream_ != null)
				b = ((SQLMXBlob)x).outputStream_.toByteArray();
		}
		
		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		switch (dataType) {
		case Types.BLOB:
			// Added if for spj lob support - R3.0
			if (connection_.isCallProc_) {
				SQLMXBlob dblob = (SQLMXBlob)x;
				dataLocator = connection_.getDataLocator(
						connection_.spjBlobTableName_, true);
				dblob.setTablename(connection_.spjBaseTableName_);
				blob = new SQLMXBlob(connection_,
						connection_.spjBaseTableName_, dataLocator, dblob);
				inputDesc_[parameterIndex - 1].paramValue_ = blob;
			} else {
				dataLocator = connection_.getDataLocator(
						connection_.blobTableName_, true);
				
				if (b == null) {
					SQLMXBlob dblob = (SQLMXBlob)x;
					dblob.setTablename(inputDesc_[parameterIndex - 1].tableName_);
					blob = new SQLMXBlob(connection_,
							inputDesc_[parameterIndex - 1].tableName_, dataLocator, dblob);
				} else {
					blob = new SQLMXBlob(connection_,
						inputDesc_[parameterIndex - 1].tableName_, dataLocator, b);
				}
				inputDesc_[parameterIndex - 1].paramValue_ = blob;
			}
			isAnyLob_ = true;
			// addParamValue(parameterIndex, new DataWrapper(dataLocator));
			addParamValue(parameterIndex, new Long(dataLocator));
			break;
		default:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_datatype_for_column",
					null);
		}
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setBoolean", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setBoolean");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		if (x) {
			addParamValue(parameterIndex, "1"); // true
		} else {
			addParamValue(parameterIndex, "0"); // false
		}
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setByte", "setByte", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setByte");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		addParamValue(parameterIndex, Byte.toString(x));
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setBytes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long dataLocator;

		// Soln 10-091027-5709 - start R3.0
		if (x == null) {
			setNull(parameterIndex, java.sql.Types.LONGVARBINARY);
			return;
		}
		// Soln 10-091027-5709 - End R3.0

		byte[] tmpArray = new byte[x.length];
		System.arraycopy(x, 0, tmpArray, 0, x.length);
		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		switch (dataType) {
		case Types.BLOB:
			// Added if for spj lob support - R3.0
			if (connection_.isCallProc_) {
				dataLocator = connection_.getDataLocator(
						connection_.spjBlobTableName_, true);
				SQLMXBlob blob = new SQLMXBlob(connection_,
						connection_.spjBaseTableName_, dataLocator, x);
				inputDesc_[parameterIndex - 1].paramValue_ = blob;
			} else {
				dataLocator = connection_.getDataLocator(
						connection_.blobTableName_, true);
				SQLMXBlob blob = new SQLMXBlob(connection_,
						inputDesc_[parameterIndex - 1].tableName_, dataLocator,
						tmpArray);
				inputDesc_[parameterIndex - 1].paramValue_ = blob;
			}
			isAnyLob_ = true;
			// addParamValue(parameterIndex, new DataWrapper(dataLocator));
			addParamValue(parameterIndex, new Long(dataLocator));
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			addParamValue(parameterIndex, tmpArray);
			break;
		default:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, reader, length,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setCharacterStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, reader, length,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setCharacterStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		char[] value;
		int dataType;
		long dataLocator;

		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		switch (dataType) {
		case Types.CLOB:
			dataLocator = connection_.getDataLocator(
					connection_.clobTableName_, false);
			SQLMXClob clob = new SQLMXClob(connection_,
					inputDesc_[parameterIndex - 1].tableName_, dataLocator,
					reader, length);
			inputDesc_[parameterIndex - 1].paramValue_ = clob;
			isAnyLob_ = true;
			// addParamValue(parameterIndex, new DataWrapper(dataLocator));
			addParamValue(parameterIndex, new Long(dataLocator));
			break;

		case Types.BLOB:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_datatype_for_column",
					null);

		default:
			value = new char[length];
			try {
				int valuePos = reader.read(value);
				if (valuePos < 1) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = "No data to read from the Reader";
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "io_exception",
							messageArguments);
				}

				while (valuePos < length) {
					char temp[] = new char[length - valuePos];
					int tempReadLen = reader.read(temp, 0, length - valuePos);
					System.arraycopy(temp, 0, value, valuePos, tempReadLen);
					valuePos += tempReadLen;
				}
			} catch (java.io.IOException e) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = e.getMessage();
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "io_exception",
						messageArguments);
			}
			addParamValue(parameterIndex, new String(value));
			break;
		}
	}
	
    public void setCharacterStream(int parameterIndex, Reader reader, long length)
            throws SQLException
    {
            if(length > Integer.MAX_VALUE)
            {
                throw SQLMXMessages.createSQLException(connection_.props_, 
                		connection_.getLocale(), "invalid_parameter_value", 
                		"Clob size is too big to handle");
            } else
            {
                setCharacterStream(parameterIndex, reader, (int)length);
                return;
            }
    }

    public void setCharacterStream(int parameterIndex, Reader reader)
            throws SQLException
    {
            int length = 0;
            try
            {
                for(int c = 0; (c = reader.read()) != -1;)
                    length += c;

                reader.reset();
            }
            catch(IOException ioex)
            {
                throw SQLMXMessages.createSQLException(connection_.props_, 
                		connection_.getLocale(), "invalid_parameter_value", 
                		"Clob reader is not readable.");
            }
            setCharacterStream(parameterIndex, reader, length);
    }

	/**
	 * Sets the designated parameter to the given <tt>Clob</tt> object. The
	 * driver converts this to an SQL <tt>CLOB</tt> value when it sends it to
	 * the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            a <tt>Clob</tt> object that maps an SQL <tt>CLOB</tt>
	 * 
	 * @throws SQLException
	 *             invalid data type for column, or restricted data type.
	 */
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setClob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setClob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long dataLocator;

		// Soln 10-091027-5709 - start R3.0

		if (x == null) {
			setNull(parameterIndex, Types.CLOB);
			return;
		}
		// Soln 10-091027-5709 - End R3.0

		if (connection_.getAutoCommit()) {
			// *******************************************************************
			// * If LOB is involved with autocommit enabled we throw an exception
			// *******************************************************************
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_lob_commit_state", null);
		}
		
		String inputStr = null;
		SQLMXClob clob = null;
		if ((x instanceof SQLMXClob) && (((SQLMXClob)x).isConfiguredLob())) {
			inputStr = x.getSubString(1, (int)x.length());
			
			if (((SQLMXClob)x).inputLobStr_ != null) {
				int length = (int)x.length();
				if (length > 0)
					inputStr = x.getSubString(1, (int)x.length());
			} else if (((SQLMXClob)x).outputStream_ != null)
				inputStr = ((SQLMXClob)x).outputStream_.toString();
		}
		
		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		switch (dataType) {
		case Types.CLOB:
			SQLMXClob dclob = (SQLMXClob)x;
			dataLocator = connection_.getDataLocator(
					connection_.clobTableName_, false);
			dclob.setTablename(inputDesc_[parameterIndex - 1].tableName_);

			if (inputStr != null) {
				clob = new SQLMXClob(connection_,
						inputDesc_[parameterIndex - 1].tableName_, dataLocator, inputStr);
			} else {
				clob = new SQLMXClob(connection_,
						inputDesc_[parameterIndex - 1].tableName_, dataLocator, dclob);
			}
			
			inputDesc_[parameterIndex - 1].paramValue_ = clob;
			isAnyLob_ = true;
			// addParamValue(parameterIndex, new DataWrapper(dataLocator)); //
			// Somehow this is a LARGINT in the C code
			addParamValue(parameterIndex, new Long(dataLocator));
			break;
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_datatype_for_column",
					null);
		default:
//			throw SQLMXMessages.createSQLException(connection_.props_,
//					connection_.getLocale(), "restricted_data_type", null);
		}
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		Timestamp t1;

		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.DATE
				&& dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		if (x != null) {
			if (dataType == Types.TIMESTAMP) {
				t1 = new Timestamp(x.getTime());
				addParamValue(parameterIndex, t1.toString());
			} else {
				addParamValue(parameterIndex, x.toString());
			}
		} else {
			addParamValue(parameterIndex, null);
		}
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long dateValue;
		Date adjustedDate;
		Timestamp t1;
		Date adjustDate = null;

		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.DATE
				&& dataType != Types.TIME && dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		// Ignore the cal, since SQL would expect it to store it in the local
		// time zone
		// Start sol 10-080715-4540 -R3.0
		if (x != null) {

			if (cal != null) {
				java.util.Calendar targetCalendar = java.util.Calendar
						.getInstance(cal.getTimeZone());
				targetCalendar.clear();
				targetCalendar.setTime(x);
				java.util.Calendar defaultCalendar = java.util.Calendar
						.getInstance();
				defaultCalendar.clear();
				defaultCalendar.setTime(x);
				long timeZoneOffset = targetCalendar
						.get(java.util.Calendar.ZONE_OFFSET)
						- defaultCalendar.get(java.util.Calendar.ZONE_OFFSET)
						+ targetCalendar.get(java.util.Calendar.DST_OFFSET)
						- defaultCalendar.get(java.util.Calendar.DST_OFFSET);
				adjustDate = ((timeZoneOffset == 0) || (x == null)) ? x
						: new java.sql.Date(x.getTime() + timeZoneOffset);
			}
			if (dataType == Types.TIMESTAMP) {
				t1 = new Timestamp(adjustDate.getTime());
				addParamValue(parameterIndex, t1.toString());
			} else {
				addParamValue(parameterIndex, adjustDate.toString());
			}
		} else {
			addParamValue(parameterIndex, null);

		}
		// End sol 10-080715-4540 -R3.0
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,this);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setDouble", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setDouble");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		//Soln 10-091208-6911 -- start R30
		if (inputDesc_[parameterIndex - 1].dataType_ == Types.NUMERIC
				|| inputDesc_[parameterIndex - 1].dataType_ == Types.DECIMAL) {
			x = Utility.setScale(new BigDecimal(x),
					inputDesc_[parameterIndex - 1].scale_, roundingMode_).doubleValue();
		}
		//Soln 10-091208-6911 -- End 
		addParamValue(parameterIndex, Double.toString(x));
		inputDesc_[parameterIndex - 1].isValueSet_ = true;
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setFloat", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setFloat");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		//Soln 10-091208-6911 -- start R30
		if (inputDesc_[parameterIndex - 1].dataType_ == Types.NUMERIC
				|| inputDesc_[parameterIndex - 1].dataType_ == Types.DECIMAL) {
			x = Utility.setScale(new BigDecimal(x),
					inputDesc_[parameterIndex - 1].scale_, roundingMode_).floatValue();
		}
		//		Soln 10-091208-6911 -- End
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		addParamValue(parameterIndex, Float.toString(x));
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setInt", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setInt");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		addParamValue(parameterIndex, Integer.toString(x));
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setLong", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setLong");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		Utility.checkLongBoundary(connection_.getLocale(), BigDecimal
				.valueOf(x));
		addParamValue(parameterIndex, Long.toString(x));
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setNull", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setNull");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		addParamValue(parameterIndex, null);
	}

	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					paramIndex, sqlType, typeName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setNull", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					paramIndex, sqlType, typeName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setNull");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		setNull(paramIndex, sqlType);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (x == null) {
			setNull(parameterIndex, Types.NULL);
		} else if (x instanceof BigDecimal) {
			setBigDecimal(parameterIndex, (BigDecimal) x);
		} else if (x instanceof java.sql.Date) {
			setDate(parameterIndex, (Date) x);
		} else if (x instanceof java.sql.Time) {
			setTime(parameterIndex, (Time) x);
		} else if (x instanceof java.sql.Timestamp) {
			setTimestamp(parameterIndex, (Timestamp) x);
		} else if (x instanceof Double) {
			setDouble(parameterIndex, ((Double) x).doubleValue());
		} else if (x instanceof Float) {
			setFloat(parameterIndex, ((Float) x).floatValue());
		} else if (x instanceof Long) {
			setLong(parameterIndex, ((Long) x).longValue());
		} else if (x instanceof Integer) {
			setInt(parameterIndex, ((Integer) x).intValue());
		} else if (x instanceof Short) {
			setShort(parameterIndex, ((Short) x).shortValue());
		} else if (x instanceof Byte) {
			setByte(parameterIndex, ((Byte) x).byteValue());
		} else if (x instanceof Boolean) {
			setBoolean(parameterIndex, ((Boolean) x).booleanValue());
		} else if (x instanceof String) {
			setString(parameterIndex, x.toString());
		} else if (x instanceof byte[]) {
			setBytes(parameterIndex, (byte[]) x);
		} else if (x instanceof Clob) {
			setClob(parameterIndex, (Clob) x);
		} else if (x instanceof Blob) {
			setBlob(parameterIndex, (Blob) x);
			/*
			 * else if (x instanceof DataWrapper) {
			 * validateSetInvocation(parameterIndex); setObject(parameterIndex,
			 * x, inputDesc_[parameterIndex - 1].dataType_); }
			 */
		} else if (x instanceof BigInteger) {
			setBigDecimal(parameterIndex, new BigDecimal((BigInteger) x));
		} else {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "object_type_not_supported", null);
		}
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, targetSqlType,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, targetSqlType,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		setObject(parameterIndex, x, targetSqlType, -1);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, targetSqlType, scale,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, targetSqlType, scale,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		BigDecimal tmpbd;
		int precision;
		Locale locale = connection_.getLocale();

		// For LOB Support - SB 10/8/2004
		// See if the object type can be a wrapper object
		// Object dataObj = DataWrapper.getDataWrapper(x);
		// If it cannot be a wrapper, process the original object
		// if (dataObj == null) dataObj = x;

		if (x == null) {
			setNull(parameterIndex, Types.NULL);
		} else {
			switch (targetSqlType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			    // Soln 10-171030-5321: setObject inserts the address of the buffer, 
				// rather than its contents. 
				// Below are unsupported conversions as of JDBC 4.2.
				// However, for compatibility reasons, we would not want to validate object types
				/*if ( x instanceof Clob || x instanceof Blob ||
						x instanceof byte[] || x instanceof Array ||
						x instanceof Ref  || x instanceof URL || x instanceof Class)
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "object_type_not_supported", null);
				*/				
				setObject(parameterIndex,x);				
				break;
			case Types.VARBINARY:
			case Types.BINARY:
			case Types.LONGVARBINARY:
				//converting string to byte array if x is string 
				if(x instanceof String)
					setBytes(parameterIndex, ((String) x).getBytes());
				else if(x instanceof InputStream)
					setBinaryStream(parameterIndex, (InputStream)x, scale);
				else
					setBytes(parameterIndex, (byte[]) x);
				break;
			case Types.TIMESTAMP:
				if (x instanceof Timestamp) {
					setTimestamp(parameterIndex, (Timestamp) x);
				} else if (x instanceof Date) {
					setTimestamp(parameterIndex, Timestamp.valueOf(x.toString()
							+ " 00:00:00.0"));
				} else {
					setString(parameterIndex, x.toString());
				}
				break;
			case Types.TIME:
				if (x instanceof Time) {
					setTime(parameterIndex, (Time) x);
				} else if (x instanceof Date) {
					setTime(parameterIndex, new Time(((Date) x).getTime()));
				} else if (x instanceof Timestamp) {
					setTime(parameterIndex, new Time(((Timestamp) x).getTime()));
				} else {
					setString(parameterIndex, x.toString());
				}
				break;
			case Types.DATE:
				try {
					if (x instanceof Date) {
						setDate(parameterIndex, (Date) x);
					} else if (x instanceof Time) {
						setDate(parameterIndex, new Date(((Time) x).getTime()));
					} else if (x instanceof Timestamp) {
						setDate(parameterIndex, new Date(((Timestamp) x)
								.getTime()));
					} else {
						setDate(parameterIndex, Date.valueOf(x.toString()));
					}
				} catch (IllegalArgumentException iex) {
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "invalid_parameter_value",
							x.toString());
				}
				break;
			// - R3.0
			case Types.BIT:
				if (x.toString().equalsIgnoreCase("1")
						|| x.toString().equalsIgnoreCase("true"))
					x = "TRUE";
				else
					x = "FALSE";
				setBoolean(parameterIndex, (Boolean.valueOf(x.toString()))
						.booleanValue());
				break;
			case Types.BOOLEAN:
				setBoolean(parameterIndex, (Boolean.valueOf(x.toString()))
						.booleanValue());
				break;
			case Types.SMALLINT:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				Utility.checkShortBoundary(locale, tmpbd);
				Utility.checkLongTruncation(parameterIndex, tmpbd);
				setShort(parameterIndex, tmpbd.shortValue());
				break;
			case Types.INTEGER:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				Utility.checkLongTruncation(parameterIndex, tmpbd);
				Utility.checkIntegerBoundary(locale, tmpbd);
				setInt(parameterIndex, tmpbd.intValue());
				break;
			case Types.BIGINT:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				Utility.checkLongBoundary(locale, tmpbd);
				Utility.checkLongTruncation(parameterIndex, tmpbd);
				setLong(parameterIndex, tmpbd.longValue());
				break;
			case Types.DECIMAL:
				// precision = getPrecision(parameterIndex - 1);
				tmpbd = Utility.getBigDecimalValue(locale, x);
				tmpbd = Utility.setScale(tmpbd, scale,
						BigDecimal.ROUND_HALF_EVEN);
				// Utility.checkDecimalBoundary(locale, tmpbd, precision);
				setBigDecimal(parameterIndex, tmpbd);
				break;
			case Types.NUMERIC:
				// precision = getPrecision(parameterIndex - 1);
				tmpbd = Utility.getBigDecimalValue(locale, x);
				tmpbd = Utility.setScale(tmpbd, scale,
						BigDecimal.ROUND_HALF_EVEN);
				// Utility.checkDecimalBoundary(locale, tmpbd, precision);
				setBigDecimal(parameterIndex, tmpbd);
				break;
			case Types.TINYINT:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				tmpbd = Utility.setScale(tmpbd, scale, roundingMode_);
				Utility.checkTinyintBoundary(locale, tmpbd);
				setShort(parameterIndex, tmpbd.shortValue());
				break;
			case Types.FLOAT:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				Utility.checkFloatBoundary(locale, tmpbd);
				setDouble(parameterIndex, tmpbd.doubleValue());
				break;
			case Types.DOUBLE:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				Utility.checkDoubleBoundary(locale, tmpbd);
				setDouble(parameterIndex, tmpbd.doubleValue());
				break;
			case Types.REAL:
				tmpbd = Utility.getBigDecimalValue(locale, x);
				setFloat(parameterIndex, tmpbd.floatValue());
				break;
			case Types.CLOB:
				if (x instanceof Clob) {
					setClob(parameterIndex, (Clob) x);
				}
				/*
				 * else if (dataObj instanceof DataWrapper) {
				 * addParamValue(parameterIndex, (DataWrapper) dataObj); }
				 */
				else if (x instanceof Long) {
					addParamValue(parameterIndex, (Long) x);
				} else {
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "conversion_not_allowed",
							null);
				}
				break;
			case Types.BLOB:
				if (x instanceof Blob) {
					setBlob(parameterIndex, (Blob) x);
				}
				/*
				 * else if (dataObj instanceof DataWrapper) {
				 * addParamValue(parameterIndex, (DataWrapper) dataObj); }
				 */
				else if (x instanceof Long) {
					addParamValue(parameterIndex, (Long) x);
				} else {
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "conversion_not_allowed",
							null);
				}
				break;
			case Types.OTHER:
				if (inputDesc_[parameterIndex - 1].fsDataType_ == InterfaceResultSet.SQLTYPECODE_INTERVAL) {
					if (x instanceof byte[]) {
						addParamValue(parameterIndex, x);
					} else if (x instanceof String) {
						addParamValue(parameterIndex, x);
					} else {
						throw SQLMXMessages.createSQLException(
								connection_.props_, connection_.getLocale(),
								"conversion_not_allowed", null);
					}
					// Added for R3.0 sol.
				} else if (inputDesc_[parameterIndex - 1].fsDataType_ == InterfaceResultSet.SQLTYPECODE_DATETIME ||
						inputDesc_[parameterIndex - 1].fsDataType_ == InterfaceResultSet.SQLDTCODE_DC_DATETIME) {/*DC DATE changes for SQL/MX3.5 */

					if (x instanceof byte[]) {
						addParamValue(parameterIndex, x);
					} else if (x instanceof String) {
						addParamValue(parameterIndex, x);
					} else {
						throw SQLMXMessages.createSQLException(
								connection_.props_, connection_.getLocale(),
								"conversion_not_allowed", null);
					}
				}
				break;

			case Types.ARRAY:
				// case Types.BIT: R3.0
			case Types.DATALINK:
			case Types.DISTINCT:
			case Types.JAVA_OBJECT:
			case Types.STRUCT:
			default:
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "object_type_not_supported",
						null);
			}
		}
	}

	// JDK 1.2
	public void setRef(int i, Ref x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, i, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setRef", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, i, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setRef");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(i);
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setRef()");
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setShort", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setShort");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		addParamValue(parameterIndex, Short.toString(x));
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setString", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		// For LOB Support - SB 10/8/2004
		// SB - Commented addParamValue and replaced it with the switch case
		// code
		// addParamValue(parameterIndex, x);
		int dataType = inputDesc_[parameterIndex - 1].dataType_;

		switch (dataType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.DATE:
		case Types.TIME:
		case Types.OTHER: // This type maps to the Nonstop SQL/MX Database
			// INTERVAL
			addParamValue(parameterIndex, x);
			break;
		case Types.TIMESTAMP:
			// start sol 10-080213-0504 - R3.0
			boolean chkValid = isValidDate((String) x);
			if (chkValid == false) {
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(),
						"set_time_stamp_for_prep_stmt_error", null);
			}
			addParamValue(parameterIndex, x);
			break;
		// end sol 10-080213-0504 -R3.0
		case Types.CLOB: // For some reason CLOB needs to be able to write to
			// string, but the API standard says no
			long dataLocator = connection_.getDataLocator(
					connection_.clobTableName_, false);
			SQLMXClob clob = new SQLMXClob(connection_,
					inputDesc_[parameterIndex - 1].tableName_, dataLocator, x);
			inputDesc_[parameterIndex - 1].paramValue_ = clob;
			isAnyLob_ = true;
			addParamValue(parameterIndex, new Long(dataLocator));
			break;
		case Types.ARRAY:
			// case Types.BIT: -R3.0
		case Types.DATALINK:
		case Types.JAVA_OBJECT:
		case Types.REF:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "datatype_not_supported", null);
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.DECIMAL:
		case Types.NUMERIC:
			if (x != null) {
				x = x.trim(); // SQLJ is using numeric string with
				// leading/trailing whitespace
			}
			setObject(parameterIndex, x, dataType);
			break;
		case Types.BLOB:
		case Types.BOOLEAN:
		case Types.BIT:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NULL:
		case Types.REAL:
            setObject(parameterIndex, x, dataType);
			break;
		case Types.LONGVARBINARY:
		case Types.BINARY://Pre-RQA defect:989
		case Types.VARBINARY:
			setObject(parameterIndex, x, dataType);
			break;
		default:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "fetch_output_inconsistent", null);
		}

	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		Timestamp t1;

		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.TIME
				&& dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		if (x != null) {
			if (dataType == Types.TIMESTAMP) {
				t1 = new Timestamp(x.getTime());
				addParamValue(parameterIndex, t1.toString());
			} else {
				addParamValue(parameterIndex, x.toString());
			}
		} else {
			addParamValue(parameterIndex, null);
		}
	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long timeValue;
		Time adjustedTime;
		Timestamp t1;
		Time adjustTime = null;
		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.TIME
				&& dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		// Ignore the cal, since SQL would expect it to store it in the local
		// time zone
		// Start sol 10-080715-4540 -R3.0
		if (x != null) {
			if (cal != null) {
				java.util.Calendar targetCalendar = java.util.Calendar
						.getInstance(cal.getTimeZone());
				targetCalendar.clear();
				targetCalendar.setTime(x);
				java.util.Calendar defaultCalendar = java.util.Calendar
						.getInstance();
				defaultCalendar.clear();
				defaultCalendar.setTime(x);
				long timeZoneOffset = targetCalendar
						.get(java.util.Calendar.ZONE_OFFSET)
						- defaultCalendar.get(java.util.Calendar.ZONE_OFFSET)
						+ targetCalendar.get(java.util.Calendar.DST_OFFSET)
						- defaultCalendar.get(java.util.Calendar.DST_OFFSET);
				adjustTime = ((timeZoneOffset == 0) || (x == null)) ? x
						: new java.sql.Time(x.getTime() + timeZoneOffset);
			}
			if (dataType == Types.TIMESTAMP) {
				t1 = new Timestamp(adjustTime.getTime());
				addParamValue(parameterIndex, t1.toString());
			} else {
				addParamValue(parameterIndex, adjustTime.toString());
			}
		} else {
			addParamValue(parameterIndex, null);
		}
		// End sol 10-080715-4540 -R3.0
	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		Date d1;
		Time t1;

		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.DATE
				&& dataType != Types.TIME && dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		if (x != null) {
			switch (dataType) {
			case Types.DATE:
				d1 = new Date(x.getTime());
				addParamValue(parameterIndex, d1.toString());
				break;
			case Types.TIME:
				t1 = new Time(x.getTime());
				addParamValue(parameterIndex, t1.toString());
				break;
			default:
				addParamValue(parameterIndex, x.toString());
				break;
			}
		} else {
			addParamValue(parameterIndex, null);
		}
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		long timeValue;
		Timestamp adjustedTime;
		Date d1;
		Time t1;
		Timestamp adjustedTimestamp = null;

		validateSetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.DATE
				&& dataType != Types.TIME && dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		// Ignore the cal, since SQL would expect it to store it in the local
		// time zone
		if (x != null) {
			switch (dataType) {
			case Types.DATE:
				d1 = new Date(x.getTime());
				addParamValue(parameterIndex, d1.toString());
				break;
			case Types.TIME:
				t1 = new Time(x.getTime());
				addParamValue(parameterIndex, t1.toString());
				break;
			default:
				// Start sol 10-080715-4540 -R3.0
				if (cal != null) {
					java.util.Calendar targetCalendar = java.util.Calendar
							.getInstance(cal.getTimeZone());
					targetCalendar.clear();
					targetCalendar.setTime(x);
					java.util.Calendar defaultCalendar = java.util.Calendar
							.getInstance();
					defaultCalendar.clear();
					defaultCalendar.setTime(x);
					long timeZoneOffset = targetCalendar
							.get(java.util.Calendar.ZONE_OFFSET)
							- defaultCalendar
									.get(java.util.Calendar.ZONE_OFFSET)
							+ targetCalendar.get(java.util.Calendar.DST_OFFSET)
							- defaultCalendar
									.get(java.util.Calendar.DST_OFFSET);
					adjustedTimestamp = ((timeZoneOffset == 0) || (x == null)) ? x
							: new java.sql.Timestamp(x.getTime()
									+ timeZoneOffset);
					if (x != null) {
						adjustedTimestamp.setNanos(x.getNanos());
					}
				}
				addParamValue(parameterIndex, adjustedTimestamp.toString());
				// End sol 10-080715-4540 -R3.0
				// addParamValue(parameterIndex, x.toString());
				break;
			}
		} else {
			addParamValue(parameterIndex, null);
		}
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, length,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setUnicodeStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x, length,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setUnicodeStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		byte[] buffer = new byte[length]; // length = number of bytes in
		// stream
		validateSetInvocation(parameterIndex);
		String s;

		if (x == null) {
			addParamValue(parameterIndex, null);
		} else {
			int dataType = inputDesc_[parameterIndex - 1].dataType_;
			switch (dataType) {
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.NUMERIC:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.TINYINT:
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_datatype_for_column",
						null);
			default:
				try {
					x.read(buffer, 0, length);
				} catch (java.io.IOException e) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = e.getMessage();
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "io_exception",
							messageArguments);
				}
				try {
					s = new String(buffer, "UnicodeBig");
					addParamValue(parameterIndex, s);
				} catch (java.io.UnsupportedEncodingException e) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = e.getMessage();
					throw SQLMXMessages.createSQLException(connection_.props_,
							connection_.getLocale(), "unsupported_encoding",
							messageArguments);
				}
				break;
			}
		}
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setURL", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setURL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateSetInvocation(parameterIndex);
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setURL()");
	} // end setURL

	// -------------------------------------------------------------------------------------------
	/**
	 * This method will associate user defined data with the prepared statement.
	 * The user defined data must be in SQL/MX rowwise rowset format.
	 * 
	 * @param numRows
	 *            the number of rows contained in buffer
	 * @param buffer
	 *            a buffer containing the rows
	 * 
	 * @exception A
	 *                SQLException is thrown
	 */
	public void setDataBuffer(int numRows, ByteBuffer buffer)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					numRows, buffer,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setDataBuffer", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					numRows, buffer,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setDataBuffer");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		usingRawRowset_ = true;
		paramRowCount_ = numRows;
		rowwiseRowsetBuffer_ = buffer;
	} // end setDataBufferBuffer

	// -------------------------------------------------------------------------------------------

	// Other methods
	protected void validateExecuteInvocation() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "validateExecuteInvocation", "",
					p);
		}
		clearWarnings();
		if (isClosed_) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "stmt_closed", null);
		}
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();
		// close the previous resultset, if any
		for (int i = 0; i < num_result_sets_; i++) {
			if (resultSet_[i] != null) {
				resultSet_[i].close();
			}
		}
		if (paramRowCount_ > 0 && usingRawRowset_ == false) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "function_sequence_error", null);
		}

		if (usingRawRowset_ == false)
			checkIfAllParamsSet();

	}

	private void checkIfAllParamsSet() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "checkIfAllParamsSet", "", p);
		}
		int paramNumber;

		if (inputDesc_ == null) {
			return;
		}
		for (paramNumber = 0; paramNumber < inputDesc_.length; paramNumber++) {
			if (!inputDesc_[paramNumber].isValueSet_) {
				Object[] messageArguments = new Object[2];
				messageArguments[0] = new Integer(paramNumber + 1);
				messageArguments[1] = new Integer(paramRowCount_ + 1);
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "parameter_not_set",
						messageArguments);
			}
		}
	}

	private void validateSetInvocation(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "validateSetInvocation", "", p);
		}
		if (isClosed_) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "stmt_closed", null);
		}
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();
		if (inputDesc_ == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_parameter_index", null);
		}
		if (parameterIndex < 1 || parameterIndex > inputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_parameter_index", null);
		}
		if (inputDesc_[parameterIndex - 1].paramMode_ == DatabaseMetaData.procedureColumnOut) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "is_a_output_parameter", null);
		}
	}

	void addParamValue(int parameterIndex, Object x) {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "addParamValue", "", p);
		}

		paramsValue_[parameterIndex - 1] = x;
		inputDesc_[parameterIndex - 1].isValueSet_ = true;
	}

	Object[] getValueArray() {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "getValueArray", "", p);
		}
		Object[] valueArray;
		int length;
		int i;
		int j;
		int index;
		Object[] rows;

		if (paramRowCount_ > 0) {
			valueArray = new Object[(paramRowCount_ + 1) * inputDesc_.length];
			length = rowsValue_.size();
			for (i = 0, index = 0; i < length; i++) {
				rows = (Object[]) rowsValue_.get(i);
				for (j = 0; j < rows.length; j++, index++) {
					valueArray[index] = rows[j];
				}
			}
		} else {
			valueArray = paramsValue_;
		}
		return valueArray;
	}

	void logicalClose() throws SQLException {
		//R3.1 changes -- start
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "logicalClose", "", p);
		}
		//R3.1 changes -- end
		isClosed_ = true;
		if (rowsValue_ != null) {
			rowsValue_.clear();
			// For LOB Support - SB 10/8/2004
		}
		if (lobObjects_ != null) {
			lobObjects_.clear();

		}
		paramRowCount_ = 0;
		for (int i = 0; i < num_result_sets_; i++) {
			if (resultSet_[i] != null) {
				resultSet_[i].close();
				// Clear the isValueSet_ flag in inputDesc_
			}
		}
		result_set_offset = 0;
		resultSet_[result_set_offset] = null;
		if (inputDesc_ != null) {
			for (int i = 0; i < inputDesc_.length; i++) {
				inputDesc_[i].isValueSet_ = false;
				paramsValue_[i] = null;
			}
		}
		isAnyLob_ = false;
		if (!connection_.closePreparedStatement(connection_, sql_,
				resultSetType_, resultSetConcurrency_, resultSetHoldability_)) {
			this.close(true); // if the statement is not in the cache
			// hardclose it afterall
		}

	}

	// ----------------------------------------------------------------------------------
	// Method used by JNI Layer to update the results of Prepare
	void setPrepareOutputs(SQLMXDesc[] inputDesc, SQLMXDesc[] outputDesc,
			int inputParamCount, int outputParamCount) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					inputDesc, outputDesc, inputParamCount, outputParamCount,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "setPrepareOutputs", "", p);
		}
		inputDesc_ = inputDesc;
		outputDesc_ = outputDesc;
		paramRowCount_ = 0;

		// Prepare updares inputDesc_ and outputDesc_
		if (inputDesc_ != null) {
			paramsValue_ = new Object[inputDesc_.length];
		} else {
			paramsValue_ = null;
		}
	} // end setPrepareOutputs

	// ----------------------------------------------------------------------------------
	void setPrepareOutputs2(SQLMXDesc[] inputDesc, SQLMXDesc[] outputDesc,
			int inputParamCount, int outputParamCount, int inputParamsLength,
			int outputParamsLength, int inputDescLength, int outputDescLength)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					inputDesc, outputDesc, inputParamCount, outputParamCount,
					inputParamsLength, outputParamsLength, inputDescLength,
					outputDescLength,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "setPrepareOutputs2", "", p);
		}
		inputParamCount_ = inputParamCount;
		outputParamCount_ = outputParamCount;
		inputParamsLength_ = inputParamsLength;
		outputParamsLength_ = outputParamsLength;
		inputDescLength_ = inputDescLength;
		outputDescLength_ = outputDescLength;
		setPrepareOutputs(inputDesc, outputDesc, inputParamCount,
				outputParamCount);
	} // end setPrepareOutputs2

	// ----------------------------------------------------------------------------------
	// Method used by JNI layer to update the results of Execute
	void setExecuteOutputs(int rowCount) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					rowCount,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "setExecuteOutputs", "", p);
		}
		batchRowCount_ = new int[1];
		batchRowCount_[0] = rowCount;
		num_result_sets_ = 1;
		result_set_offset = 0;
		if (outputDesc_ != null) {
			resultSet_[result_set_offset] = new SQLMXResultSet(this,
					outputDesc_);
		} else {
			resultSet_[result_set_offset] = null;
		}
	}

	/*
	 * //----------------------------------------------------------------------------------
	 * void setExecuteSingletonOutputs(SQLValue_def[] sqlValue_def_array, short
	 * rowsAffected) throws SQLException { batchRowCount_ = new int[1];
	 * batchRowCount_[0] = rowsAffected; if (outputDesc_ != null) { resultSet_ =
	 * new SQLMXResultSet(this, outputDesc_); } else { resultSet_ = null; } if
	 * (rowsAffected == 0) { resultSet_.setFetchOutputs(new Row[0], 0, true, 0); }
	 * else { resultSet_.irs_.setSingletonFetchOutputs(resultSet_, rowsAffected,
	 * true, 0, sqlValue_def_array); } }
	 */

	// ----------------------------------------------------------------------------------
	// Method used by JNI layer to update the results of Execute
	void setExecuteBatchOutputs(int[] rowCount) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					rowCount,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "setExecuteBatchOutputs", "", p);
		}
		num_result_sets_ = 1;
		result_set_offset = 0;
		if (outputDesc_ != null) {
			resultSet_[result_set_offset] = new SQLMXResultSet(this,
					outputDesc_);
		} else {
			resultSet_[result_set_offset] = null;
		}
		batchRowCount_ = rowCount;
	}

	void reuse(SQLMXConnection connection, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, resultSetType, resultSetConcurrency,
					resultSetHoldability,connection);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "reuse", "", p);
		}
		if (resultSetType != ResultSet.TYPE_FORWARD_ONLY
				&& resultSetType != ResultSet.TYPE_SCROLL_INSENSITIVE
				&& resultSetType != ResultSet.TYPE_SCROLL_SENSITIVE) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_resultset_type", null);
		}
		if (resultSetType == ResultSet.TYPE_SCROLL_SENSITIVE) {
			resultSetType_ = ResultSet.TYPE_SCROLL_INSENSITIVE;
			setSQLWarning(null, "scrollResultSetChanged", null);
		} else {
			resultSetType_ = resultSetType;
		}
		if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY
				&& resultSetConcurrency != ResultSet.CONCUR_UPDATABLE) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_resultset_concurrency",
					null);
		}
		resultSetConcurrency_ = resultSetConcurrency;
		resultSetHoldability_ = resultSetHoldability;
		queryTimeout_ = connection_.getServerHandle().getQueryTimeout();
		fetchSize_ = SQLMXResultSet.DEFAULT_FETCH_SIZE;
		maxRows_ = 0;
		fetchDirection_ = ResultSet.FETCH_FORWARD;
		isClosed_ = false;
	}

	public void close(boolean hardClose) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					hardClose,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "close", "", p);
		}

		if (connection_._isClosed()) {
			return;
		}
		try {
			if (hardClose) {
				ist_.close();
			} else {
				logicalClose();
			}
		} catch (SQLException e) {
			performConnectionErrorChecks(e);
			throw e;
		} finally {
			isClosed_ = true;
			if (hardClose) {
				connection_.removeElement(pRef_);
			}
		}

	}

	void populateLobObjects() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "populateLobObjects", "", p);
		}
		int len;
		Object lob;

		if (isAnyLob_) {
			for (int i = 0; i < inputDesc_.length; i++) {
				if (inputDesc_[i].paramValue_ != null) {
					lob = inputDesc_[i].paramValue_;
					if (lob instanceof SQLMXClob) {
						((SQLMXClob) lob).populate();
					} else {
						((SQLMXBlob) lob).populate();
					}
				}
			}
		}
	}

	void populateBatchLobObjects() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "populateBatchLobObjects", "", p);
		}
		int len;
		Object lob;

		if (lobObjects_ != null) {
			len = lobObjects_.size();
			for (int i = 0; i < len; i++) {
				lob = lobObjects_.get(i);
				if (lob instanceof SQLMXClob) {
					((SQLMXClob) lob).populate();
				} else {
					((SQLMXBlob) lob).populate();
				}
			}
		}
	}

	// SOL-10-060911-8963
	SQLMXPreparedStatement(SQLMXConnection connection, String sql,
			String stmtLabel) throws SQLException {
		this(connection, sql, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY, connection.holdability_, stmtLabel);
		// SOL-10-061025-0064
		connection.ic_.t4props_.setUseArrayBinding(false);
		connection.ic_.t4props_.setBatchRecovery(false);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "",
					"Note, this call is before previous constructor call.", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	// MFC new property enableMFC, All scalar function will go through MFC now
	// R3.0 Moved this method to SQLMXStatement.java, since statement also requires to remove MFC from stmt label
//	synchronized void removeMFCFromStmtLabel() throws SQLException {
//		if (this.connection_.props_.getEnableMFC().equalsIgnoreCase("OFF")) {
//			this.stmtLabel_ = this.stmtLabel_.replaceAll("MFC", "");
//			this.ist_.setStmtLabel_(this.stmtLabel_);
//			this.ist_.getT4statement_().setM_stmtLabel(this.stmtLabel_);
//		}
//
//	}

	// Constructors with access specifier as "default"
	SQLMXPreparedStatement(SQLMXConnection connection, String sql)
			throws SQLException {
		this(connection, sql, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY, connection.holdability_);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "",
					"Note, this call is before previous constructor call.", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	SQLMXPreparedStatement(SQLMXConnection connection, String sql,
			int resultSetType, int resultSetConcurrency) throws SQLException {
		this(connection, sql, resultSetType, resultSetConcurrency,
				connection.holdability_);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "",
					"Note, this call is before previous constructor call.", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

	}

	// SOL-10-060911-8963
	SQLMXPreparedStatement(SQLMXConnection connection, String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability, String stmtLabel) throws SQLException {
		super(connection, resultSetType, resultSetConcurrency,
				resultSetHoldability, stmtLabel);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "",
					"Note, this call is before previous constructor call.", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();
		sqlStmtType_ = ist_.getSqlStmtType(sql);
		if (sqlStmtType_ == TRANSPORT.TYPE_STATS) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "infostats_invalid_error", null);
		} else if (sqlStmtType_ == TRANSPORT.TYPE_CONFIG) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "config_cmd_invalid_error", null);
		}
		// sql_ = sql;
		sql_ = scanSqlStr(sql);
		// stmtLabel_ = generateStmtLabel();
		stmtLabel_ = stmtLabel;
		// System.out.println("SQLMXPreparedStatement stmtLabel_ " +
		// stmtLabel_);

		usingRawRowset_ = false;
		usingAutoGeneratedKeys_ = false; //Added for L36 corda
	}

	SQLMXPreparedStatement(SQLMXConnection connection, String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		super(connection, resultSetType, resultSetConcurrency,
				resultSetHoldability);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "",
					"Note, this call is before previous constructor call.", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();
		sqlStmtType_ = ist_.getSqlStmtType(sql);
		if (sqlStmtType_ == TRANSPORT.TYPE_STATS) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "infostats_invalid_error", null);
		} else if (sqlStmtType_ == TRANSPORT.TYPE_CONFIG) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "config_cmd_invalid_error", null);
		}
		// sql_ = sql;
		sql_ = scanSqlStr(sql);
		stmtLabel_ = generateStmtLabel();

		usingRawRowset_ = false;
		usingAutoGeneratedKeys_ = false; //Added for L36 corda
	}

	SQLMXPreparedStatement(SQLMXConnection connection, String moduleName,
			int moduleVersion, long moduleTimestamp, String stmtName,
			boolean isSelect, int holdability) {
		if (connection.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection.props_,
					connection, moduleName, moduleVersion, moduleTimestamp,
					stmtName, isSelect, holdability);
			connection.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "", "", p);
		}
		try {
			if (connection.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(connection.props_,
						connection, moduleName, moduleVersion, moduleTimestamp,
						stmtName, isSelect, holdability);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXPreparedStatement");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				connection.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
			// ignore
		}
		connection_ = connection;
		moduleName_ = moduleName;
		moduleVersion_ = moduleVersion;
		moduleTimestamp_ = moduleTimestamp;
		stmtLabel_ = stmtName;
		sqlStmtType_ = (isSelect) ? TRANSPORT.TYPE_SELECT
				: TRANSPORT.TYPE_UNKNOWN;
		usingRawRowset_ = false;

		// Make Sure you initialize the other fields to the right value
		fetchSize_ = SQLMXResultSet.DEFAULT_FETCH_SIZE;
		maxRows_ = 0;
		fetchDirection_ = ResultSet.FETCH_FORWARD;
		queryTimeout_ = connection_.getServerHandle().getQueryTimeout();
		resultSetType_ = ResultSet.TYPE_FORWARD_ONLY;
		resultSetHoldability_ = holdability;
		usingRawRowset_ = false;
		usingAutoGeneratedKeys_ = false; //Added for L36 corda
	}

	// Interface methods
	public void prepare(String sql, int queryTimeout, int holdability)
			throws SQLException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, sql,this.stmtLabel_,
					queryTimeout, holdability,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "prepare", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, sql,
					queryTimeout, holdability,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("prepare");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		try {
			super.ist_.prepare(sql, queryTimeout, this);
		} catch (SQLException e) {
			performConnectionErrorChecks(e);
			throw e;
		}
	};

	public void setFetchSize(int rows) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, rows,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXPreparedStatement", "setFetchSize", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities
					.makeParams(connection_.props_, rows,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXPreparedStatement");
			lr.setSourceMethodName("setFetchSize");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}

		if (rows < 0) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_fetchSize_value", null);
		}
		if (rows > 0) {
			fetchSize_ = rows;
		}
		// If the value specified is zero, then the hint is ignored.
	}

	private void execute(int paramRowCount, int paramCount,
			Object[] paramValues, int queryTimeout, boolean isAnyLob // For
	// LOB
	// Support
	// - SB
	// 10/8/2004
	) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					paramRowCount, paramCount, paramValues, queryTimeout,
					isAnyLob,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXPreparedStatement", "execute", "", p);
		}
		try {
			// Added for ExecuteBatch - R3.0
			//Added for Soluntion 10-110523-7792 
			 if ((paramRowCount > 1 || (rowsValue_ != null && rowsValue_.size() > 0) )
					&& connection_.props_.getExecuteBatchWithRowsAffected().equals(
							"ON")) {
				ist_.execute(TRANSPORT.SRVR_API_EXECUTE_ARRAY, paramRowCount,
						paramCount, paramValues, queryTimeout, null, this);
			}else {			
			ist_.execute(TRANSPORT.SRVR_API_SQLEXECUTE2, paramRowCount,
					paramCount, paramValues, queryTimeout, null, this);
			}
			// End - R3.0
		} catch (SQLException e) {
			performConnectionErrorChecks(e);
			throw e;
		}
	};

	/*
	 * protected void setSingleton(boolean value) { singleton_ = value; }
	 * protected boolean getSingleton() { return singleton_; }
	 */

	/**
	 * Use this method to retrieve the statement type that was used when
	 * creating the statement through the HP connectivity service. ie. SELECT,
	 * UPDATE, DELETE, INSERT.
	 */
	public String getStatementType() {
		String stmtType = "";

		switch (sqlStmtType_) {
		case TRANSPORT.TYPE_SELECT:
			stmtType = "SELECT";
			break;
		case TRANSPORT.TYPE_UPDATE:
			stmtType = "UPDATE";
			break;
		case TRANSPORT.TYPE_DELETE:
			stmtType = "DELETE";
			break;
		case TRANSPORT.TYPE_INSERT:
		case TRANSPORT.TYPE_INSERT_PARAM:
			stmtType = "INSERT";
			break;
		case TRANSPORT.TYPE_CREATE:
			stmtType = "CREATE";
			break;
		case TRANSPORT.TYPE_GRANT:
			stmtType = "GRANT";
			break;
		case TRANSPORT.TYPE_DROP:
			stmtType = "DROP";
			break;
		case TRANSPORT.TYPE_CALL:
			stmtType = "CALL";
			break;
		case TRANSPORT.TYPE_EXPLAIN:
			stmtType = "EXPLAIN";
			break;
		case TRANSPORT.TYPE_STATS:
			stmtType = "INFOSTATS";
			break;
		case TRANSPORT.TYPE_CONFIG:
			stmtType = "CONFIG";
			break;
		default:
			stmtType = "";
			break;
		}

		return stmtType;
	}

	// start sol 10-080213-0504 -R3.0

	// ----------------------------------------------------------------------
	/**
	 * This method take a date string and convert it to the approperite format
	 * to check given date is valid and return boolean value.
	 * 
	 * @param dateStr
	 *            The locale for this operation
	 * 
	 */

	public boolean isValidDate(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date tempDate = null;
		try {
			tempDate = (java.util.Date) dateFormat.parse(dateStr);
		} catch (ParseException e) {
			return false;
		}
		if (dateFormat.format(tempDate).equals(dateStr.substring(0, 10))) {
			return true;
		} else {
			return false;
		}
	}

	// end sol 10-080213-0504 -R3.0

	/**
	 * Use this method to retrieve the statement type that was used when
	 * creating the statement through the HP connectivity service. ie. SELECT,
	 * UPDATE, DELETE, INSERT.
	 */
	public short getStatementTypeShort() {
		return sqlStmtType_;
	}

	/**
	 * Use this method to retrieve the statement type that was used when
	 * creating the statement through the HP connectivity service. ie. SELECT,
	 * UPDATE, DELETE, INSERT.
	 */
	public int getStatementTypeInt() {
		return ist_.getSqlQueryType();
	}

	ArrayList getKeyColumns() {
		return keyColumns;
	}

	void setKeyColumns(ArrayList keyColumns) {
		this.keyColumns = keyColumns;
	}

	ArrayList keyColumns = null;

	int paramRowCount_;
	boolean ExecuteBatchDone_ = false; // Keep track if execute Batch is done.
	String moduleName_;

	int moduleVersion_;

	long moduleTimestamp_;

	boolean isAnyLob_; // LOB Support - SB 9/28/04

	ArrayList lobObjects_; // LOB Support - SB 9/28/04

	ArrayList rowsValue_;

	Object[] paramsValue_;

	//Soln 10-091208-6911 -- start
//	int roundingMode_ = BigDecimal.ROUND_HALF_UP;
	//Soln 10-091208-6911 -- End
	// boolean singleton_ = false;

	// ================ SQL Statement type ====================
	public static final short TYPE_UNKNOWN = 0;

	public static final short TYPE_SELECT = 0x0001;

	public static final short TYPE_UPDATE = 0x0002;

	public static final short TYPE_DELETE = 0x0004;

	public static final short TYPE_INSERT = 0x0008;

	public static final short TYPE_EXPLAIN = 0x0010;

	public static final short TYPE_CREATE = 0x0020;

	public static final short TYPE_GRANT = 0x0040;

	public static final short TYPE_DROP = 0x0080;

	public static final short TYPE_INSERT_PARAM = 0x0100;

	public static final short TYPE_SELECT_CATALOG = 0x0200;

	public static final short TYPE_SMD = 0x0400;

	public static final short TYPE_CALL = 0x0800;

	public static final short TYPE_STATS = 0x1000;

	public static final short TYPE_CONFIG = 0x2000;

	// =================== SQL Query ===================
	public static final int SQL_OTHER = -1;

	public static final int SQL_UNKNOWN = 0;

	public static final int SQL_SELECT_UNIQUE = 1;

	public static final int SQL_SELECT_NON_UNIQUE = 2;

	public static final int SQL_INSERT_UNIQUE = 3;

	public static final int SQL_INSERT_NON_UNIQUE = 4;

	public static final int SQL_UPDATE_UNIQUE = 5;

	public static final int SQL_UPDATE_NON_UNIQUE = 6;

	public static final int SQL_DELETE_UNIQUE = 7;

	public static final int SQL_DELETE_NON_UNIQUE = 8;

	public static final int SQL_CONTROL = 9;

	public static final int SQL_SET_TRANSACTION = 10;

	public static final int SQL_SET_CATALOG = 11;

	public static final int SQL_SET_SCHEMA = 12;

	// =================== new identifiers ===================
	public static final int SQL_CREATE_TABLE = SQL_SET_SCHEMA + 1;

	public static final int SQL_CREATE_VIEW = SQL_CREATE_TABLE + 1;

	public static final int SQL_CREATE_INDEX = SQL_CREATE_VIEW + 1;

	public static final int SQL_CREATE_UNIQUE_INDEX = SQL_CREATE_INDEX + 1;

	public static final int SQL_CREATE_SYNONYM = SQL_CREATE_UNIQUE_INDEX + 1;

	public static final int SQL_CREATE_VOLATILE_TABLE = SQL_CREATE_SYNONYM + 1;;

	public static final int SQL_CREATE_MV = SQL_CREATE_VOLATILE_TABLE + 1;

	public static final int SQL_CREATE_MVG = SQL_CREATE_MV + 1;

	public static final int SQL_CREATE_MP_ALIAS = SQL_CREATE_MVG + 1;

	public static final int SQL_CREATE_PROCEDURE = SQL_CREATE_MP_ALIAS + 1;

	public static final int SQL_CREATE_TRIGGER = SQL_CREATE_PROCEDURE + 1;

	public static final int SQL_CREATE_SET_TABLE = SQL_CREATE_TRIGGER + 1;

	public static final int SQL_CREATE_MULTISET_TABLE = SQL_CREATE_SET_TABLE + 1;

	public static final int SQL_DROP_TABLE = SQL_CREATE_MULTISET_TABLE + 1;

	public static final int SQL_DROP_VIEW = SQL_DROP_TABLE + 1;

	public static final int SQL_DROP_INDEX = SQL_DROP_VIEW + 1;

	public static final int SQL_DROP_SYNONYM = SQL_DROP_INDEX + 1;

	public static final int SQL_DROP_VOLATILE_TABLE = SQL_DROP_SYNONYM + 1;;

	public static final int SQL_DROP_MV = SQL_DROP_VOLATILE_TABLE + 1;

	public static final int SQL_DROP_MVG = SQL_DROP_MV + 1;

	public static final int SQL_DROP_MP_ALIAS = SQL_DROP_MVG + 1;

	public static final int SQL_DROP_PROCEDURE = SQL_DROP_MP_ALIAS + 1;

	public static final int SQL_DROP_TRIGGER = SQL_DROP_PROCEDURE + 1;

	public static final int SQL_DROP_SET_TABLE = SQL_DROP_TRIGGER + 1;

	public static final int SQL_DROP_MULTISET_TABLE = SQL_DROP_SET_TABLE + 1;

	public static final int SQL_ALTER_TABLE = SQL_DROP_MULTISET_TABLE + 1;

	public static final int SQL_ALTER_INDEX = SQL_ALTER_TABLE + 1;

	public static final int SQL_ALTER_TRIGGER = SQL_ALTER_INDEX + 1;

	public static final int SQL_ALTER_MP_ALIAS = SQL_ALTER_TRIGGER + 1;

	// JDBC 4.x stubs
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setRowId(int parameterIndex, RowId x)");
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setNString(int parameterIndex, String value)");
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setNCharacterStream(int parameterIndex, Reader value,	long length)");
		
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setNClob(int parameterIndex, NClob value)");
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setClob(int parameterIndex, Reader reader, long length)");
		
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setBlob(int parameterIndex, InputStream inputStream, long length)");
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setNClob(int parameterIndex, Reader reader, long length)");
		
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setSQLXML(int parameterIndex, SQLXML xmlObject)");
		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setAsciiStream(int parameterIndex, InputStream x)");
		
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setBinaryStream(int parameterIndex, InputStream x)");
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setNCharacterStream(int parameterIndex, Reader value)");
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setClob(int parameterIndex, Reader reader)");
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setBlob(int parameterIndex, InputStream inputStream)");
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.getT4Properties(),
				connection_.getLocale(), "setNClob(int parameterIndex, Reader reader)");
	}

}
