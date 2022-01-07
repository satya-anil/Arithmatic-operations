// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003, 2004, 2005, 2006
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.Locale;

// Referenced classes of package sun.jdbc.rowset:
//            BaseRow

class Row extends BaseRow implements Serializable, Cloneable {

	private Object currentVals[];

	private BitSet colsChanged;

	private boolean deleted;

	private boolean updated;

	private boolean inserted;

	private int numCols;

	Row(int i) {
		origVals = new Object[i];
		currentVals = new Object[i];
		colsChanged = new BitSet(i);
		numCols = i;
	}

	Row(int i, Object aobj[]) {
		origVals = new Object[i];
		for (int j = 0; j < i; j++) {
			origVals[j] = aobj[j];

		}
		currentVals = new Object[i];
		colsChanged = new BitSet(i);
		numCols = i;
	}

	protected void clearDeleted() {
		deleted = false;
	}

	protected void clearInserted() {
		inserted = false;
	}

	protected void clearUpdated() {
		updated = false;
		for (int i = 0; i < numCols; i++) {
			currentVals[i] = null;
			colsChanged.clear(i);
		}

	}

	protected boolean getColUpdated(int i) {
		return colsChanged.get(i);
	}

	protected Object getColumnObject(int i) throws SQLException {
		if (getColUpdated(i - 1)) {
			return currentVals[i - 1];
		} else {
			return origVals[i - 1];
		}
	}

	protected boolean getDeleted() {
		return deleted;
	}

	protected boolean getInserted() {
		return inserted;
	}

	protected boolean getUpdated() {
		return updated;
	}

	protected void initColumnObject(int i, Object obj) {
		origVals[i - 1] = obj;
	}

	protected void moveCurrentToOrig() {
		for (int i = 0; i < numCols; i++) {
			if (getColUpdated(i)) {
				origVals[i] = currentVals[i];
				currentVals[i] = null;
				colsChanged.clear(i);
			}
		}
	}

	private void setColUpdated(int i) {
		colsChanged.set(i);
	}

	protected void setColumnObject(int i, Object obj) {
		currentVals[i - 1] = obj;
		setColUpdated(i - 1);
	}

	protected void setLobObject(int i, Object obj) {
		currentVals[i - 1] = obj;
		origVals[i - 1] = obj;
	}

	protected void setDeleted() {
		deleted = true;
	}

	protected void setInserted() {
		inserted = true;
	}

	protected void setUpdated() {
		updated = true;
	}

	protected void deleteRow(Locale locale, PreparedStatement deleteStmt,
			BitSet paramCols) throws SQLException {
		int i;
		int j;
		int count;

		for (i = 0, j = 1; i < numCols; i++) {
			if (paramCols.get(i)) {
				deleteStmt.setObject(j++, origVals[i]);
			}
		}
		count = deleteStmt.executeUpdate();
		if (count == 0) {
			throw SQLMXMessages.createSQLException(null, locale, "row_modified",
					null);
		}
	}

	protected void updateRow(Locale locale, PreparedStatement updateStmt,
			BitSet paramCols, BitSet keyCols) throws SQLException {
		int i;
		int j;
		int count;

		for (i = 0, j = 1; i < numCols; i++) {
			if (keyCols.get(i)) {
				if (getColUpdated(i)) {
					throw SQLMXMessages.createSQLException(null, locale,
							"primary_key_not_updateable", null);
				}
			} else {
				if (paramCols.get(i)) { // LOB Support SB 10/8/2004
					Object obj = getColumnObject((i + 1));
					if (obj instanceof SQLMXLob) {
						if (obj == origVals[i]) { // New and old Lob objects
							// are same
							updateStmt.setObject(j++, new Long(
									((SQLMXLob) obj).dataLocator_));
							continue;
						}
					}
					updateStmt.setObject(j++, getColumnObject(i + 1));
				}
			}
		}

		for (i = 0; i < numCols; i++) {
			// if (paramCols.get(i))
			if (keyCols.get(i)) {
				Object obj = origVals[i];
				if (obj instanceof SQLMXLob) {
					updateStmt.setObject(j++, new Long(
							((SQLMXLob) obj).dataLocator_));
					continue;
				}
				updateStmt.setObject(j++, origVals[i]);
			}
		}

		count = updateStmt.executeUpdate();
		if (count == 0) {
			throw SQLMXMessages.createSQLException(null, locale, "row_modified",
					null);
		}
		moveCurrentToOrig();
		setUpdated();
	}

	protected void refreshRow(Locale locale, PreparedStatement selectStmt,
			BitSet selectCols, BitSet keyCols) throws SQLException {
		int i;
		int j;
		ResultSet rs;
		ResultSetMetaData rsmd;
		int columnCount;

		clearUpdated();

		for (i = 0, j = 1; i < numCols; i++) {
			if (keyCols.get(i)) {
				selectStmt.setObject(j++, origVals[i]);
			}
		}
		rs = selectStmt.executeQuery();
		if (rs != null) {
			try {
				rsmd = rs.getMetaData();
				columnCount = rsmd.getColumnCount();
				rs.next();
				for (i = 0, j = 1; i < numCols; i++) {
					if (selectCols.get(i)) {
						origVals[i] = rs.getObject(j++);
					}
				}
			} catch (SQLException ex) {
				throw ex;
			} finally {
				rs.close();
			}
		}
	}

	protected void closeLobObjects() {
		int i;
		SQLMXLob lob;

		for (i = 0; i < numCols; i++) {
			if (currentVals[i] instanceof SQLMXLob) {
				lob = (SQLMXLob) currentVals[i];
				lob.close();
			}

		}
	}

}
