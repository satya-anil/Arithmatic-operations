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
import java.util.logging.Level;

public class SQLMXParameterMetaData implements java.sql.ParameterMetaData {

	public String getParameterClassName(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}
		return inputDesc[param - 1].getColumnClassName();
	}

	public int getParameterCount() throws SQLException {
		return inputDesc.length;
	}

	public int getParameterMode(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}
		return inputDesc[param - 1].paramMode_;
	}

	public int getParameterType(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}
		return inputDesc[param - 1].dataType_;
	}

	public String getParameterTypeName(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].getColumnTypeName(props.getLocale());
	}

	public int getPrecision(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].precision_;
	}

	public int getScale(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].scale_;
	}

	public int isNullable(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].isNullable_;
	}

	public boolean isSigned(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}
		return inputDesc[param - 1].isSigned_;
	}

	// ////////////////////////
	// begin custom accessors//
	// ////////////////////////
	public int getRowLength() throws SQLException {
		// this is the same for all params
		// only if we have no input params will we throw an error
		if (inputDesc.length == 0) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[0].rowLength_;
	}

	public int getDisplaySize(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].displaySize_;
	}

	public int getFSDataType(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].fsDataType_;
	}

	public int getMaxLength(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].maxLen_;
	}

	public int getNoNullOffset(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].noNullValue_;
	}

	public int getNullOffset(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].nullValue_;
	}

	public int getOdbcCharset(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].odbcCharset_;
	}

	public int getSqlCharset(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].sqlCharset_;
	}

	public int getSqlDataType(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].sqlDataType_;
	}

	public int getSqlDatetimeCode(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].sqlDatetimeCode_;
	}

	public int getSqlOctetLength(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].sqlOctetLength_;
	}

	public int getSqlPrecision(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].sqlPrecision_;
	}

	// /////////////////////////////////
	// these are legacy names...do not remove these yet even though they are
	// duplicate
	// i will depricate these before 2.3 release
	// ///////////////////////////////

	/**
	 * @deprecated
	 */
	public int getSqlTypeCode(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].dataType_;
	}

	/**
	 * @deprecated
	 */
	public int getSqlLength(int param) throws SQLException {
		if (param > inputDesc.length) {
			throw SQLMXMessages.createSQLException(props, props.getLocale(),
					"invalid_desc_index", null);
		}

		return inputDesc[param - 1].maxLen_;
	}

	SQLMXParameterMetaData(SQLMXPreparedStatement stmt, SQLMXDesc[] inputDesc) {
		this.props = stmt.connection_.props_;
		this.inputDesc = inputDesc;

		if (props.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(stmt.connection_.props_,
					stmt, inputDesc);
			stmt.connection_.props_.t4Logger_.logp(Level.FINE,
					"SQLMXParameterMetaData", "", "", p);
		}
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
	T4Properties props;

	SQLMXDesc[] inputDesc;
}
