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

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SQLMXCallableStatement extends SQLMXPreparedStatement implements
		java.sql.CallableStatement {
	public Array getArray(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getArray", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getArray");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		clearWarnings();
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getArray()");
		return null;
	}

	public Array getArray(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getArray", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getArray");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getArray(parameterIndex);
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		BigDecimal retValue;
		String data;

		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBigDecimal", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBigDecimal");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// String returned may not be numeric in case of SQL_CHAR, SQL_VARCHAR
		// and SQL_LONGVARCHAR
		// fields. Hoping that java might throw invalid value exception
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);
		if (data == null) {
			wasNull_ = true;
			return null;
		} else {
			wasNull_ = false;
			try {
				retValue = new BigDecimal(data);
			} catch (NumberFormatException e) {
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_cast_specification",
						null);
			}
			return retValue;
		}
	}

	public BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, scale);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBigDecimal", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, scale);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBigDecimal");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		BigDecimal retValue;

		retValue = getBigDecimal(parameterIndex);
		if (retValue != null) {
			return retValue.setScale(scale);
		} else {
			return null;
		}
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBigDecimal", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBigDecimal");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getBigDecimal(parameterIndex);
	}

	public BigDecimal getBigDecimal(String parameterName, int scale)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, scale,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBigDecimal", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, scale,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBigDecimal");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getBigDecimal(parameterIndex, scale);
	}

	public Blob getBlob(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBlob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBlob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
//Added for SPJBlob -R3.0
		int dataType;
		long data_locator;
		Blob data;
		int targetSqlType;
		int sqlCharset;
		String tableName;
		boolean isSpj = true;
		validateGetInvocation(parameterIndex);

		targetSqlType = inputDesc_[parameterIndex - 1].dataType_;
		sqlCharset = inputDesc_[parameterIndex - 1].sqlCharset_;

		//Added for SPJBLOB 
		if (targetSqlType == java.sql.Types.BLOB) {
			data_locator = getLong(parameterIndex);
			data = readSpjBlob(data_locator);
		} else {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		//SQLMXMessages.throwUnsupportedFeatureException(connection_.props_, connection_.getLocale(), "getBlob()");
		//return null;
		
		return data;

	}

	public Blob getBlob(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBlob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBlob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getBlob(parameterIndex);
	}

	public boolean getBoolean(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBoolean", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBoolean");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);

		if (data != null) {
			wasNull_ = false;
			return (!data.equals("0"));
		} else {
			wasNull_ = true;
			return false;
		}
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBoolean", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBoolean");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getBoolean(parameterIndex);
	}

	public byte getByte(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getByte", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getByte");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);

		if (data != null) {
			wasNull_ = false;
			return Byte.parseByte(data);
		} else {
			wasNull_ = true;
			return 0;
		}
	}

	public byte getByte(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getByte", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getByte");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getByte(parameterIndex);
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBytes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;

		validateGetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
	/*	if (dataType != Types.BINARY && dataType != Types.VARBINARY
				&& dataType != Types.LONGVARBINARY) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		// BINARY, VARBINARY, LONGVARBINARY not supported
		throw SQLMXMessages.createSQLException(connection_.props_, connection_
				.getLocale(), "datatype_not_supported", null);
				*/
		
		//Added for SPJBLOB R3.0
		switch (dataType) {
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);

		case Types.BLOB: // HP Extension allows BLOB;
			Blob blob = getBlob(parameterIndex);
			if (blob == null) {
				return null;
			}
			return blob.getBytes(1L, (int) blob.length());

		default:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "datatype_not_supported", null);
		}
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getBytes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getBytes(parameterIndex);
	}

	public Clob getClob(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getClob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getClob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getClob()");
		return null;
	}

	public Clob getClob(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getClob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getClob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getClob(parameterIndex);
	}

	public Date getDate(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		String dateStr;
		Date retValue;

		validateGetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.DATE
				&& dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
		// For LOB Support - SB
		// dateStr = inputDesc_[parameterIndex-1].paramValue_;
		dateStr = getString(parameterIndex);

		if (dateStr != null) {
			wasNull_ = false;
			try {
				boolean convertDate = connection_.getDateConversion();

				if (convertDate) {
					String dt = SQLMXResultSet.convertDateFormat(dateStr);
					retValue = SQLMXResultSet.valueOf(dt);
				} else {
					retValue = Date.valueOf(dateStr);
				}
			} catch (IllegalArgumentException e) {
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_cast_specification",
						null);
			}
			return retValue;
		} else {
			wasNull_ = true;
			return null;
		}
	}

	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, cal);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, cal);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Date sqlDate;
		java.util.Date d;

		sqlDate = getDate(parameterIndex);
		if (sqlDate != null) {
			if (cal != null) {
				cal.setTime(sqlDate);
				d = cal.getTime();
				sqlDate = new Date(d.getTime());
			}
			return sqlDate;
		} else {
			return (sqlDate);
		}
	}

	public Date getDate(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getDate(parameterIndex);
	}

	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getDate(parameterIndex, cal);
	}

	public double getDouble(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getDouble", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getDouble");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);
		if (data != null) {
			wasNull_ = false;
			return Double.parseDouble(data);
		} else {
			wasNull_ = true;
			return 0;
		}
	}

	public double getDouble(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getDouble", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getDouble");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getDouble(parameterIndex);
	}

	public float getFloat(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getFloat", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getFloat");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);

		if (data != null) {
			wasNull_ = false;
			return Float.parseFloat(data);
		} else {
			wasNull_ = true;
			return 0;
		}
	}

	public float getFloat(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getFloat", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getFloat");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getFloat(parameterIndex);
	}

	public int getInt(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getInt", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getInt");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);
		if (data != null) {
			wasNull_ = false;
			return Integer.parseInt(data);
		} else {
			wasNull_ = true;
			return 0;
		}
	}

	public int getInt(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getInt", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getInt");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getInt(parameterIndex);
	}

	public long getLong(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getLong", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getLong");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);
		if (data != null) {
			wasNull_ = false;
			return Long.parseLong(data);
		} else {
			wasNull_ = true;
			return 0;
		}
	}

	public long getLong(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getLong", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getLong");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getLong(parameterIndex);
	}

	public Object getObject(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		byte byteValue;
		short shortValue;
		int intValue;
		long longValue;
		float floatValue;
		double doubleValue;
		boolean booleanValue;

		validateGetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		switch (dataType) {
		case Types.TINYINT:
			byteValue = getByte(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Byte(byteValue);
			}
		case Types.SMALLINT:
			intValue = getShort(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Integer(intValue);
			}
		case Types.INTEGER:
			intValue = getInt(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Integer(intValue);
			}
		case Types.BIGINT:
			longValue = getLong(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Long(longValue);
			}
		case Types.REAL:
			floatValue = getFloat(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Float(floatValue);
			}
		case Types.FLOAT:
		case Types.DOUBLE:
			doubleValue = getDouble(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Double(doubleValue);
			}
		case Types.DECIMAL:
		case Types.NUMERIC:
			return getBigDecimal(parameterIndex);
		case Types.BIT:
			booleanValue = getBoolean(parameterIndex);
			if (wasNull_) {
				return null;
			} else {
				return new Boolean(booleanValue);
			}
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return getString(parameterIndex);
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return getBytes(parameterIndex);
		case Types.DATE:
			return getDate(parameterIndex);
		case Types.TIME:
			return getTime(parameterIndex);
		case Types.TIMESTAMP:
			return getTimestamp(parameterIndex);
//Added for SPJBLOB -R3.0
		case Types.BLOB:
			return getBlob(parameterIndex);
		default:
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}
	}

	public Object getObject(int parameterIndex, Map map) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, map);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, map);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getObject()");
		return null;
	}

	public Object getObject(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getOjbect", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getOjbect");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getObject(parameterIndex);
	}

	public Object getObject(String parameterName, Map map) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, map,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, map,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getObject(parameterIndex, map);
	}

	public Ref getRef(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getRef", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getRef");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getRef()");
		return null;
	}

	public Ref getRef(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getRef", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getRef");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getRef(parameterIndex);
	}

	public short getShort(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getShort", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getShort");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;

		validateGetInvocation(parameterIndex);
		inputDesc_[parameterIndex - 1].checkValidNumericConversion(connection_
				.getLocale());
		// For LOB Support - SB
		// data = inputDesc_[parameterIndex-1].paramValue_;
		data = getString(parameterIndex);
		if (data != null) {
			wasNull_ = false;
			return Short.parseShort(data);
		} else {
			wasNull_ = true;
			return 0;
		}
	}

	public short getShort(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getShort", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getShort");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getShort(parameterIndex);
	}

	public String getString(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getString", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String data;
		// For LOB Support - SB 10/8/2004
		Object x;
		int targetSqlType;
		int sqlCharset;

		validateGetInvocation(parameterIndex);

		targetSqlType = inputDesc_[parameterIndex - 1].dataType_;
		sqlCharset = inputDesc_[parameterIndex - 1].sqlCharset_;
		x = inputDesc_[parameterIndex - 1].paramValue_;

		if (x == null) {
			wasNull_ = true;
			data = null;
		} else {
			if (x instanceof byte[]) {
				try {
					if (this.ist_.ic_.getISOMapping() == InterfaceUtilities.SQLCHARSETCODE_ISO88591
							&& !this.ist_.ic_.getEnforceISO()
							&& sqlCharset == InterfaceUtilities.SQLCHARSETCODE_ISO88591)
						data = new String((byte[]) x, ist_.ic_.t4props_
								.getISO88591());
					else if(sqlCharset == -1 || sqlCharset == -2 || sqlCharset == 11){ //to support MP datatpes -R3.0
						String charSet =InterfaceUtilities.getEncodingTranslation(connection_,sqlCharset);
						data = new String((byte[]) x,charSet);
					}else {
						data = this.ist_.ic_.decodeBytes((byte[]) x,sqlCharset);							
					}
//					else
//						data = this.ist_.ic_
//								.decodeBytes((byte[]) x, sqlCharset);

					wasNull_ = false;
				} catch (CharacterCodingException e) {
					SQLException se = SQLMXMessages.createSQLException(
							this.connection_.ic_.t4props_, this.connection_
									.getLocale(),
							"translation_of_parameter_failed",
							"getLocalString", e.getMessage());
					se.initCause(e);
					throw se;
				} catch (UnsupportedCharsetException e) {
					SQLException se = SQLMXMessages.createSQLException(
							this.connection_.ic_.t4props_, this.connection_
									.getLocale(), "unsupported_encoding", e
									.getCharsetName());
					se.initCause(e);
					throw se;
				} catch (UnsupportedEncodingException e) {
					SQLException se = SQLMXMessages.createSQLException(
							this.connection_.ic_.t4props_, this.connection_
									.getLocale(), "unsupported_encoding", e
									.getMessage());
					se.initCause(e);
					throw se;
				}
			} else {
				data = x.toString();
				wasNull_ = false;
			}
		}
		return data;
	}

	public String getString(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getString", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getString(parameterIndex);
	}

	public Time getTime(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		String timeStr;
		Time retValue;

		validateGetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.TIME
				&& dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}

		// For LOB Support - SB 10/8/2004
		// timeStr = inputDesc_[parameterIndex-1].paramValue_;
		timeStr = getString(parameterIndex);
		if (timeStr != null) {
			try {
				wasNull_ = false;
				retValue = Time.valueOf(timeStr);
			} catch (IllegalArgumentException e) {
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_cast_specification",
						null);
			}
			return retValue;
		} else {
			wasNull_ = true;
			return null;
		}
	}

	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, cal);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, cal);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Time sqlTime;
		java.util.Date d;

		sqlTime = getTime(parameterIndex);
		if (sqlTime != null) {
			if (cal != null) {
				cal.setTime(sqlTime);
				d = cal.getTime();
				sqlTime = new Time(d.getTime());
			}
			return sqlTime;
		} else {
			return (sqlTime);
		}
	}

	public Time getTime(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getTime(parameterIndex);
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getTime(parameterIndex, cal);
	}

	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int dataType;
		String timestampStr;
		Timestamp retValue;

		validateGetInvocation(parameterIndex);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		if (dataType != Types.CHAR && dataType != Types.VARCHAR
				&& dataType != Types.LONGVARCHAR && dataType != Types.DATE
				&& dataType != Types.TIMESTAMP) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "restricted_data_type", null);
		}

		// For LOB Support - SB 10/8/2004
		// timestampStr = inputDesc_[parameterIndex - 1].paramValue_;
		timestampStr = getString(parameterIndex);
		if (timestampStr != null) {
			try {
				wasNull_ = false;
				retValue = Timestamp.valueOf(timestampStr);
			} catch (IllegalArgumentException e) {
				throw SQLMXMessages.createSQLException(connection_.props_,
						connection_.getLocale(), "invalid_cast_specification",
						null);
			}
			return retValue;
		} else {
			wasNull_ = true;
			return null;
		}
	}

	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, cal);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, cal);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Timestamp sqlTimestamp;
		java.util.Date d;
		int nanos;

		sqlTimestamp = getTimestamp(parameterIndex);
		if (sqlTimestamp != null) {
			if (cal != null) {
				nanos = sqlTimestamp.getNanos();
				cal.setTime(sqlTimestamp);
				d = cal.getTime();
				sqlTimestamp = new Timestamp(d.getTime());
				sqlTimestamp.setNanos(nanos);
			}
			return sqlTimestamp;
		} else {
			return (sqlTimestamp);
		}
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getTimestamp(parameterIndex);
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getTimestamp(parameterIndex, cal);
	}

	public URL getURL(int parameterIndex) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getURL", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getURL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		clearWarnings();
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getURL()");
		return null;
	}

	public URL getURL(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "getURL", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("getURL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		return getURL(parameterName);
	}

	public void registerOutParameter(int parameterIndex, int sqlType)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "registerOutParameter", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("registerOutParameter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// Ignoring sqlType and scale
		validateGetInvocation(parameterIndex);
		if (inputDesc_[parameterIndex - 1].paramMode_ == DatabaseMetaData.procedureColumnOut) {
			inputDesc_[parameterIndex - 1].isValueSet_ = true;
		}
	}

	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType, scale,this);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "registerOutParameter", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType, scale,this);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("registerOutParameter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// Ignoring sqlType and scale
		validateGetInvocation(parameterIndex);
		if (inputDesc_[parameterIndex - 1].paramMode_ == DatabaseMetaData.procedureColumnOut) {
			inputDesc_[parameterIndex - 1].isValueSet_ = true;
		}
	}

	public void registerOutParameter(int parameterIndex, int sqlType,
			String typeName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType, typeName);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "registerOutParameter", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex, sqlType, typeName);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("registerOutParameter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		// Ignoring sqlType and typeName
		validateGetInvocation(parameterIndex);
		if (inputDesc_[parameterIndex - 1].paramMode_ == DatabaseMetaData.procedureColumnOut) {
			inputDesc_[parameterIndex - 1].isValueSet_ = true;
		}
	}

	public void registerOutParameter(String parameterName, int sqlType)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "registerOutParameter", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("registerOutParameter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		registerOutParameter(parameterIndex, sqlType);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType, scale,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "registerOutParameter", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType, scale,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("registerOutParameter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		registerOutParameter(parameterIndex, sqlType, scale);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType, typeName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "registerOutParameter", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType, typeName,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("registerOutParameter");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateGetInvocation(parameterName);
		registerOutParameter(parameterIndex, sqlType, typeName);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, length);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setAsciiStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setAsciiStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setAsciiStream(parameterIndex, x, length);
	}

	public void setBigDecimal(String parameterName, BigDecimal x)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setBigDecimal", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setBigDecimal");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setBigDecimal(parameterIndex, x);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, length);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setBinaryStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setBinaryStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String paramName = null;
		String tempstr = " - BLOB"; // parser appends this string to paramname for SPJ BLOB
		int dataType;
		if (connection_.isCallProc_ = true) {
			if (parameterName.indexOf(tempstr) > 0) {
				paramName = parameterName;
			} else {
				paramName = parameterName.concat(tempstr);
			}
		}
		int parameterIndex = validateSetInvocation(paramName);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		setBinaryStream(parameterIndex, x, length);
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setBlob", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setBlob");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String paramName = null;
		String tempstr = " - BLOB"; // parser appends this string to paramname for SPJ BLOB
		int dataType;
		if (connection_.isCallProc_ = true) {
			if (parameterName.indexOf(tempstr) > 0) {
				paramName = parameterName;
			} else {
				paramName = parameterName.concat(tempstr);
			}
		}
		int parameterIndex = validateSetInvocation(paramName);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		setBlob(parameterIndex, x);
	}
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setBoolean", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setBoolean");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setBoolean(parameterIndex, x);
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setByte", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setByte");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setByte(parameterIndex, x);
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setBytes", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		String paramName = null;
		String tempstr = " - BLOB"; // parser appends this string to paramname for SPJ BLOB
		int dataType;
//for SPJBLOB -R3.0
		if (connection_.isCallProc_ = true) {
			if (parameterName.indexOf(tempstr) > 0) {
				paramName = parameterName;
			} else {
				paramName = parameterName.concat(tempstr);
			}
		}
		int parameterIndex = validateSetInvocation(paramName);
		dataType = inputDesc_[parameterIndex - 1].dataType_;
		setBytes(parameterIndex, x);
	}

	public void setCharacterStream(String parameterName, Reader reader,
			int length) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, reader, length);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setCharacterStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, reader, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setCharacterStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setCharacterStream(parameterIndex, reader, length);
	}

	public void setDate(String parameterName, Date x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setDate(parameterIndex, x);
	}

	public void setDate(String parameterName, Date x, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setDate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setDate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setDate(parameterIndex, x, cal);
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setDouble", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setDouble");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setDouble(parameterIndex, x);
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setFloat", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setFloat");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setFloat(parameterIndex, x);
	}

	public void setInt(String parameterName, int x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setInt", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setInt");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setInt(parameterIndex, x);
	}

	public void setLong(String parameterName, long x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setLong", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setLong");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setLong(parameterIndex, x);
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setNull", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setNull");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setNull(parameterIndex, sqlType);
	}

	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType, typeName);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setNull", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, sqlType, typeName);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setNull");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setNull(parameterIndex, sqlType, typeName);
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setObject(parameterIndex, x);
	}

	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, targetSqlType);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, targetSqlType);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setObject(parameterIndex, x, targetSqlType);
	}

	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, targetSqlType, scale);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setObject", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, targetSqlType, scale);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setObject");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setObject(parameterIndex, x, targetSqlType, scale);
	}

	public void setShort(String parameterName, short x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setShort", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setShort");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setShort(parameterIndex, x);
	}

	public void setString(String parameterName, String x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setString", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setString(parameterIndex, x);
	}

	public void setTime(String parameterName, Time x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setTime(parameterIndex, x);
	}

	public void setTime(String parameterName, Time x, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setTime", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setTime");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setTime(parameterIndex, x, cal);
	}

	public void setTimestamp(String parameterName, Timestamp x)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setTimestamp(parameterIndex, x);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, cal,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setTimestamp", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, cal,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setTimestamp");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setTimestamp(parameterIndex, x, cal);
	}

	public void setUnicodeStream(String parameterName, InputStream x, int length)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, length);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setUnicodeStream", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setUnicodeStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setUnicodeStream(parameterIndex, x, length);
	}

	public void setURL(String parameterName, URL x) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "setURL", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName, x,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("setURL");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		int parameterIndex = validateSetInvocation(parameterName);
		setURL(parameterIndex, x);
	}

	public boolean wasNull() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "wasNull", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("wasNull");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		return wasNull_;
	}

	public boolean execute() throws SQLException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "execute", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("execute");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Object[] valueArray = null;
		int inDescLength = 0;
		if (inputDesc_ != null) {
			valueArray = getValueArray();
			inDescLength = inputDesc_.length;
		}

		validateExecuteInvocation();

		valueArray = getValueArray();
		if (inputDesc_ != null && connection_.isCallProc_) {
			populateSpjLob();
		}
				if (connection_.getAutoCommit() == false) {
			connection_.isActiveTrans = true;
		}
		
		ist_.execute(TRANSPORT.SRVR_API_SQLEXECUTE2, paramRowCount_,
				inDescLength, valueArray, queryTimeout_, null, this);

		// SPJ: 5-18-2007
		// if (resultSet_[result_set_offset] != null)
		if (resultSet_[result_set_offset] != null
				&& resultSet_[result_set_offset].spj_rs_) {
			return true;
		} else {
			return false;
		}
	}

	public int[] executeBatch() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "executeBatch", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("executeBatch");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
			//Modifed for solution 10-150313-5739
			clearWarnings();
			SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
					connection_.getLocale(), "executeBatch()");
		
		return null;
	}

	public ResultSet executeQuery() throws SQLException {
		//Added stmt id to log from 3.1
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "executeQuery", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("executeQuery");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Object[] valueArray = null;
		int inDescLength = 0;
		if (inputDesc_ != null) {
			valueArray = getValueArray();
			inDescLength = inputDesc_.length;
		}

		validateExecuteInvocation();

		if (connection_.getAutoCommit() == false) {
			connection_.isActiveTrans = true;
		}


		ist_.execute(TRANSPORT.SRVR_API_SQLEXECUTE2, paramRowCount_,
				inDescLength, valueArray, queryTimeout_, null, this);

		return resultSet_[result_set_offset];
	}

	public int executeUpdate() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			//Added stmt id to log from 3.1
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,this.stmtLabel_,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "executeUpdate", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("executeUpdate");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
		Object[] valueArray = null;
		int inDescLength = 0;
		if (inputDesc_ != null) {
			valueArray = getValueArray();
			inDescLength = inputDesc_.length;
		}

		validateExecuteInvocation();
		valueArray = getValueArray();
		
		if (inputDesc_ != null && connection_.isCallProc_) {
			populateSpjLob();
		}
		if (connection_.getAutoCommit() == false) {
			connection_.isActiveTrans = true;
		}
		
		ist_.execute(TRANSPORT.SRVR_API_SQLEXECUTE2, paramRowCount_,
				inDescLength, valueArray, queryTimeout_, null, this);

		return (1);
	}

	// Other methods
	protected void validateGetInvocation(int parameterIndex)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterIndex,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXCallableStatement", "validateGetInvocation", "", p);
		}
		clearWarnings();
		// connection_.getServerHandle().isConnectionOpen();
		connection_.isConnectionOpen();
		if (isClosed_) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_statement", null);
		}
		if (inputDesc_ == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "not_a_output_parameter", null);
		}
		if (parameterIndex < 1 || parameterIndex > inputDesc_.length) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_parameter_index", null);
		}
		if (inputDesc_[parameterIndex - 1].paramMode_ != DatabaseMetaData.procedureColumnInOut
				&& inputDesc_[parameterIndex - 1].paramMode_ != DatabaseMetaData.procedureColumnOut) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "not_a_output_parameter", null);
		}
	}

	protected int validateGetInvocation(String parameterName)
			throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXCallableStatement", "validateGetInvocation", "", p);
		}
		int i;

		if (isClosed_) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_statement", null);
		}
		if (inputDesc_ == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "not_a_output_parameter", null);
		}
		for (i = 0; i < inputDesc_.length; i++) {
			if (parameterName.equalsIgnoreCase(inputDesc_[i].name_)) {
				return i + 1;
			}
		}
		throw SQLMXMessages.createSQLException(connection_.props_, connection_
				.getLocale(), "invalid_parameter_name", null);
	}

	private int validateSetInvocation(String parameterName) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					parameterName,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXCallableStatement", "validateSetInvocation", "", p);
		}
		int i;

		if (isClosed_) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "stmt_closed", null);
		}
		if (inputDesc_ == null) {
			throw SQLMXMessages.createSQLException(connection_.props_,
					connection_.getLocale(), "invalid_parameter_index", null);
		}
		for (i = 0; i < inputDesc_.length; i++) {
			if (parameterName.equalsIgnoreCase(inputDesc_[i].name_)) {
				return i + 1;
			}
		}
		throw SQLMXMessages.createSQLException(connection_.props_, connection_
				.getLocale(), "invalid_parameter_name", null);
	}

	// Constructors with access specifier as "default"
	SQLMXCallableStatement(SQLMXConnection connection, String sql)
			throws SQLException {
		super(connection, sql);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, sql,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, sql,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	SQLMXCallableStatement(SQLMXConnection connection, String sql,
			String stmtLabel) throws SQLException {
		super(connection, sql, stmtLabel);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql,connection_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql,connection_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	SQLMXCallableStatement(SQLMXConnection connection, String sql,
			int resultSetType, int resultSetConcurrency) throws SQLException {
		super(connection, sql, resultSetType, resultSetConcurrency);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	SQLMXCallableStatement(SQLMXConnection connection, String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		super(connection, sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	SQLMXCallableStatement(SQLMXConnection connection, String moduleName,
			int moduleVersion, long moduleTimestamp, String stmtName)
			throws SQLException {
		super(connection, moduleName, moduleVersion, moduleTimestamp, stmtName,
				false, connection.holdability_);
		if (connection_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, moduleName, moduleVersion, moduleTimestamp,
					stmtName);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "", "", p);
		}
		if (connection_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					connection, moduleName, moduleVersion, moduleTimestamp,
					stmtName);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXCallableStatement");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			connection_.props_.getLogWriter().println(temp);
		}
	}

	void populateSpjLob() throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "populateSpjLob", "", p);
		}
		int len;
		Object lob;
		if (connection_.isCallProc_) {
			for (int i = 0; i < inputDesc_.length; i++) {
				if (inputDesc_[i].paramValue_ != null) {
					lob = inputDesc_[i].paramValue_;
					((SQLMXBlob) lob).populate();
				}
			}
		}
	}
	Blob readSpjBlob(long data_locator) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXCallableStatement", "readSpjBlob", "", p);
		}
		long dataloc;
		Blob data = null;
		for (int i = 0; i < inputDesc_.length; i++) {
			if (inputDesc_[i].paramValue_ != null) {
				dataloc = data_locator;
				data = readSpjLobData(dataloc);
			}
		}
		return data;
	}
	// Interface methods
	void prepareCall(String sql, int queryTimeout, int holdability)
			throws SQLException {
		super.ist_.prepare(sql, queryTimeout, this);
//Added for spjblob R30
        if (inputDesc_ != null) {
            if (inputDesc_[0].dataType_ == java.sql.Types.BLOB) {
                connection_.isCallProc_ = true;
                //for spjblob

                connection_.spjBlobTableName_ = "spjcat.spjsch.spj_table";
                connection_.spjClobTableName_ = "spjcat.spjsch.spj_table";
            }
        }
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_, sql,
					queryTimeout, holdability,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXCallableStatement", "prepareCall", "", p);
		}
	};

	void executeCall(int inputParamCount, Object[] inputParamValues,
			int queryTimeout) throws SQLException {
		/*
		 * super.ist_.execute( inputParamCount, inputParamValues, queryTimeout,
		 * this);
		 */
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					inputParamCount, inputParamValues, queryTimeout,connection_);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXCallableStatement", "ececuteCall", "", p);
		}
	};

	void cpqPrepareCall(String moduleName, int moduleVersion,
			long moduleTimestamp, String stmtName, int queryTimeout,
			int holdability) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_,
					moduleName, moduleVersion, moduleTimestamp, stmtName,
					queryTimeout, holdability);
			connection_.props_.t4Logger_.logp(Level.FINER,
					"SQLMXCallableStatement", "cpqPrepareCall", "", p);
		}
		throw SQLMXMessages.createSQLException(connection_.props_, connection_
				.getLocale(), "unsupported_feature",
				new Object[] { "cpqPrepareCall" });
	};

	Blob readSpjLobData(long datalocator) throws SQLException {
		if (connection_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(connection_.props_);
			connection_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"readSpjLobData", "", p);
		}
		ResultSet rs;
		Blob blobdata;
		byte[] b;
		String tableName = "SPJ_BASE_TABLE";
		connection_.prepareGetSpjLobDataStmt();
		connection_.SpjLobPreparedStatements[connection_.SPJ_GET_LOB_DATA_STMT]
				.setString(1, "SPJ_BASE_TABLE");
		connection_.SpjLobPreparedStatements[connection_.SPJ_GET_LOB_DATA_STMT]
				.setLong(2, datalocator);
		connection_.SpjLobPreparedStatements[connection_.SPJ_GET_LOB_DATA_STMT]
				.setInt(3, 0);
		connection_.SpjLobPreparedStatements[connection_.SPJ_GET_LOB_DATA_STMT]
				.setInt(4, Integer.MAX_VALUE);
		rs = connection_.SpjLobPreparedStatements[connection_.SPJ_GET_LOB_DATA_STMT]
				.executeQuery();
		rs.next();
		b = rs.getBytes(1);
		blobdata = new SQLMXBlob(connection_, tableName, datalocator, b);
		rs.close();
		return blobdata;
	}
	// fields
	boolean wasNull_;
	
	//short returnResultSet_; // Soln 10-171121-5576: Not used.
	
	// JDBC 4.x stubs 
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setRowId(int parameterIndex, RowId x)");		
		
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNString(int parameterIndex, String value)");				
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNCharacterStream(int parameterIndex, Reader value,	long length)");		
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNClob(int parameterIndex, NClob value)");				
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setClob(int parameterIndex, Reader reader, long length)");				
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBlob(int parameterIndex, InputStream inputStream, long length)");				
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNClob(int parameterIndex, Reader reader, long length)");						
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setSQLXML(int parameterIndex, SQLXML xmlObject)");						
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setAsciiStream(int parameterIndex, InputStream x)");						
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBinaryStream(int parameterIndex, InputStream x)");				
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNCharacterStream(int parameterIndex, Reader value)");						
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setClob(int parameterIndex, Reader reader)");				
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBlob(int parameterIndex, InputStream inputStream)");		
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNClob(String parameterName, Reader reader)");		
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getRowId(int parameterIndex)");		
		return null;
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getRowId(String parameterName)");		
		return null;
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setRowId(String parameterName, RowId x)");				
	}

	@Override
	public void setNString(String parameterName, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNString(String parameterName, String value)");				
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNCharacterStream(String parameterName, Reader value, long length)");						
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNClob(String parameterName, NClob value)");						
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setClob(String parameterName, Reader reader, long length)");						
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBlob(String parameterName, InputStream inputStream, long length)");				
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNClob(String parameterName, Reader reader, long length)");				
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getNClob(int parameterIndex)");		
		return null;
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getNClob(String parameterName)");		
		return null;
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setSQLXML(String parameterName, SQLXML xmlObject)");		
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getSQLXML(int parameterIndex)");		
		return null;
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getSQLXML(String parameterName)");		
		return null;
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getNString(int parameterIndex)");		
		return null;
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getNString(String parameterName)");		
		return null;
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getNCharacterStream(int parameterIndex)");				
		return null;
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getNCharacterStream(String parameterName)");				
		return null;
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getCharacterStream(int parameterIndex)");				
		return null;
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getCharacterStream(String parameterName) ");				
		return null;
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setClob(String parameterName, Clob x)");				
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setAsciiStream(String parameterName, InputStream x, long length)");						
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBinaryStream(String parameterName, InputStream x, long length)");						
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setCharacterStream(String parameterName, Reader reader, long length)");						
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setAsciiStream(String parameterName, InputStream x)");		
		
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBinaryStream(String parameterName, InputStream x)");		
		
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setCharacterStream(String parameterName, Reader reader)");		
		
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNCharacterStream(String parameterName, Reader value)");		
		
	}

	@Override
	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setClob(String parameterName, Reader reader)");		
		
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setBlob(String parameterName, InputStream inputStream)");		

		
	}

	@Override
	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "setNClob(String parameterName, Reader reader)");		
		
	}
/*
	@Override
	public <T> T getObject(int parameterIndex, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getObject(int parameterIndex, Class<T> type)");
		return null;
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(connection_.props_,
				connection_.getLocale(), "getObject(String parameterName, Class<T> type)");
		return null;
	}
*/
}
