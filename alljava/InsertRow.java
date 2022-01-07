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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.BitSet;

class InsertRow extends BaseRow implements Serializable, Cloneable {

	private BitSet colsInserted;

	private int cols;

	InsertRow(int i) {
		origVals = new Object[i];
		colsInserted = new BitSet(i);
		cols = i;
	}

	protected Object getColumnObject(int i) throws SQLException {
		if (!colsInserted.get(i - 1)) {
			throw SQLMXMessages.createSQLException(null, null,
					"no_column_value_specified", null);
		} else {
			return origVals[i - 1];
		}
	}

	protected void initInsertRow() {
		for (int i = 0; i < cols; i++) {
			colsInserted.clear(i);

		}
	}

	/*
	 * protected boolean isCompleteRow(RowSetMetaData rowsetmetadata) throws
	 * SQLException { for(int i = 0; i < cols; i++) if(!colsInserted.get(i) &&
	 * rowsetmetadata.isNullable(i + 1) == 0) return false; return true; }
	 */

	protected void markColInserted(int i) {
		colsInserted.set(i);
	}

	protected void setColumnObject(int i, Object obj) {
		origVals[i - 1] = obj;
		markColInserted(i - 1);
	}

	protected void insertRow(PreparedStatement insertStmt, BitSet paramCols)
			throws SQLException {
		int i;
		int j;

		for (i = 0, j = 1; i < cols; i++) {
			if (paramCols.get(i)) {
				insertStmt.setObject(j++, origVals[i]);
			}
		}
		insertStmt.execute();
		initInsertRow();
	}
}
