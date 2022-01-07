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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SQLMXResultSetMetaData implements java.sql.ResultSetMetaData {

	// begin required methods
	public String getCatalogName(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getCatalogName", "", p);
		}
		
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getCatalogName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].catalogName_;
	}

	public String getColumnClassName(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnClassName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnClassName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].getColumnClassName();
	}

	public int getColumnCount() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnCount", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnCount");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return outputDesc_.length;
	}

	public int getColumnDisplaySize(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnDisplaySize", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnDisplaySize");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].displaySize_;
	}

	public String getColumnLabel(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnLabel", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnLabel");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
        // soln no. 10-130215-6284 
		return (outputDesc_[column - 1].columnLabel_.equals("")) ? outputDesc_[column - 1].name_
				: outputDesc_[column - 1].columnLabel_;
	}

	public String getColumnName(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].name_;
	}

	public int getColumnType(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnType", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnType");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].dataType_;
	}

	public String getColumnTypeName(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getColumnTypeName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getColumnTypeName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].getColumnTypeName(connection_
				.getLocale());
	}

	public int getPrecision(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getPrecision", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getPrecision");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].precision_;
	}

	public int getScale(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getScale", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getScale");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].scale_;
	}

	public String getSchemaName(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getSchemaName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getSchemaName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].schemaName_;
	}

	public String getTableName(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "getTableName", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("getTableName");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].tableName_;
	}

	public boolean isAutoIncrement(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isAutoIncrement", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isAutoIncrement");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].isAutoIncrement_;
	}

	public boolean isCaseSensitive(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isCaseSensitive", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isCaseSensitive");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].isCaseSensitive_;
	}

	public boolean isCurrency(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isCurrency", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isCurrency");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].isCurrency_;
	}

	public boolean isDefinitelyWritable(int column) throws SQLException {
				if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isDefinitelyWritable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isDefinitelyWritable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return true;
	}

	public int isNullable(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isNullable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isNullable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].isNullable_;
	}

	public boolean isReadOnly(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isReadOnly", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isReadOnly");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return false;
	}

	public boolean isSearchable(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isSearchable", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isSearchable");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].isSearchable_;
	}

	public boolean isSigned(int column) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "isSigned", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					column,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXResultSetMetaData");
			lr.setSourceMethodName("isSigned");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].isSigned_;
	}

	public boolean isWritable(int column) throws SQLException {
		return true;
	}

	// ////////////////////////
	// begin custom accessors//
	// ////////////////////////

	public int getFSDataType(int column) throws SQLException {
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].fsDataType_;
	}

	public int getMaxLength(int column) throws SQLException {
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].maxLen_;
	}

	public int getOdbcCharset(int column) throws SQLException {
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].odbcCharset_;
	}

	public int getRowLength() throws SQLException {
		// this is the same for all params
		// only if we have no input params will we throw an error
		if (outputDesc_.length == 0) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.props_.getLocale(), "invalid_desc_index", null);
		}

		return outputDesc_[0].rowLength_;
	}

	public int getSqlCharset(int column) throws SQLException {
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].sqlCharset_;
	}

	public int getSqlPrecision(int column) throws SQLException {
		if (column > outputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].sqlPrecision_;
	}

	public int getSqlDatetimeCode(int param) throws SQLException {
		return stmt_.ist_.pr_.outputDesc[param - 1].datetimeCode_;
	}

	// /////////////////////////////////
	// these are legacy names...do not remove these yet even though they are
	// duplicate
	// ///////////////////////////////

	/**
	 * @deprecated
	 */
	public String cpqGetCharacterSet(int column) throws SQLException {
		if ((column > outputDesc_.length) || (column <= 0)) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_desc_index", null);
		}
		return outputDesc_[column - 1].getCharacterSetName();
	}

	/**
	 * @deprecated
	 */
	public int getSqlTypeCode(int param) throws SQLException {
		return stmt_.ist_.pr_.outputDesc[param - 1].dataType_;
	} // end getSqlTypeCode

	/**
	 * @deprecated
	 */
	public int getSqlLength(int param) throws SQLException {
		return stmt_.ist_.pr_.outputDesc[param - 1].maxLen_;
	} // end getSqlTypeCode

	SQLMXResultSetMetaData(SQLMXStatement stmt, SQLMXDesc[] outputDesc) {
		if (stmt.connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(stmt.connection_.props_,
					stmt, outputDesc,stmt.connection_);
			stmt.connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "", "", p);
		}
		try {
			if (stmt.connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(
						stmt.connection_.props_, stmt, outputDesc,stmt.connection_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXResultSetMetaData");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				stmt.connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
		}
		connection_ = stmt.connection_;
		outputDesc_ = outputDesc;
		stmt_ = stmt;
	}

	SQLMXResultSetMetaData(SQLMXResultSet resultSet, SQLMXDesc[] outputDesc) {
		if (resultSet.connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(
					resultSet.connection_.props_, resultSet, outputDesc,resultSet.connection_);
			resultSet.connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXResultSetMetaData", "", "", p);
		}
		try {
			if (resultSet.connection_.props_.getLogWriter() != null) {
				LogRecord lr = new LogRecord(Level.FINE, "");
				Object p[] = T4LoggingUtilities.makeParams(
						resultSet.connection_.props_, resultSet, outputDesc,resultSet.connection_);
				lr.setParameters(p);
				lr.setSourceClassName("SQLMXResultSetMetaData");
				lr.setSourceMethodName("");
				T4LogFormatter lf = new T4LogFormatter();
				String temp = lf.format(lr);
				resultSet.connection_.props_.getLogWriter().println(temp);
			}
		} catch (SQLException se) {
		}

		resultSet_ = resultSet;
		connection_ = resultSet_.connection_;
		outputDesc_ = outputDesc;
		stmt_ = resultSet.stmt_;
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
	SQLMXResultSet resultSet_;

	SQLMXConnection connection_;

	SQLMXDesc[] outputDesc_;

	SQLMXStatement stmt_;
}
