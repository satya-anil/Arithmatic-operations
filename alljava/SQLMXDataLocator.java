//
// Copyright 2003, 2005
// Hewlett-Packard Development Company, L.P.
// Protected as an unpublished work.
// All rights reserved.
//
// The computer program listings, specifications and
// documentation herein are the property of Compaq Compter
// Corporation and successor entities such as Hewlett Packard
// Development Company, L.P., or a third party supplier and
// shall not be reproduced, copied, disclosed, or used in whole
// or in part for any reason without the prior express written
// permission of Hewlett Packard Developmnet Company, L.P.
//
// @ @ @ END COPYRIGHT @ @ @
/* -*-java-*-
 * Filename    : SQLMXDataLocator.java
 * Author      : Selva, Swastik Bihani
 * Desctiption : For each clobTableName or blobTableName property, an instance of this object
 *		 will be created.
 *
 */

package com.tandem.t4jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

class SQLMXDataLocator {
	synchronized long getDataLocator(SQLMXConnection conn, boolean isBlob)
			throws SQLException {
		isBlob_ = isBlob;
		// This method is synchronized to ensure that the two different threads
		// will not reserve
		// data locator,
		if (startDataLocator_ == 0
				|| ((currentDataLocator_ - startDataLocator_ + 1) == T4Properties.reserveDataLocator_)) {
			currentDataLocator_ = getStartDataLocator(conn);
		} else {
			currentDataLocator_++;
		}
		return currentDataLocator_;
	}

	long getStartDataLocator(SQLMXConnection conn) throws SQLException {
		/*
		 * SB 9/28/04 Current tx = null; ControlRef cref = null; boolean txBegin =
		 * false; int currentTxid = conn.txid_; conn.txid_ = 0;
		 */
		// *******************************************************************
		// * If LOB is involved with autocommit enabled we throw an exception
		// *******************************************************************
		if (conn.getAutoCommit()) {
			throw SQLMXMessages.createSQLException(conn.props_,
					conn.getLocale(), "invalid_lob_commit_state", null);
		}
		// try
		// {
		/*
		 * SB 9/28/04 tx = new Current(); // Suspend the existing transaction,
		 * suspend returns null if there is none cref = tx.suspend();
		 * tx.begin(); txBegin = true;
		 */
		synchronized (conn) {
			prepareGetStrtDataLocStmt(conn);
			int getStrtDataLocStmt_const = 0;
			if (isBlob_) {
				getStrtDataLocStmt_const = conn.BLOB_GET_STRT_DATA_LOC_STMT;
			} else {
				getStrtDataLocStmt_const = conn.CLOB_GET_STRT_DATA_LOC_STMT;

			}
			/*
			 * if (conn.reserveEmptyDataLocator_) {
			 * conn.LobPreparedStatements[getStrtDataLocStmt_const].setLong(1,
			 * SQLMXConnection.EMPTY_DATA_LOCATOR_UPDATE);
			 * conn.reserveEmptyDataLocator_ = false; } else {
			 */
			conn.LobPreparedStatements[getStrtDataLocStmt_const].setLong(1,
					T4Properties.reserveDataLocator_);
			// }

			// ResultSet rs = GetStrtDataLoc_.executeQuery();
			// ResultSet rs =
			// ((SQLMXPreparedStatement)GetStrtDataLoc_).executeSingleton();
			ResultSet rs = ((SQLMXPreparedStatement) conn.LobPreparedStatements[getStrtDataLocStmt_const])
					.executeQuery();
			try {
				if (rs.next()) {
					startDataLocator_ = rs.getLong(1);
				} else {
					insertDataLocatorRow(conn);
				}
			} finally {
				rs.close();
			}

			/*
			 * SB 9/28/04 tx.commit(false); txBegin = false;
			 */
		}
		/*
		 * SB 9/28/04 if (cref != null) tx.resume(cref);
		 */
		// }
		/*
		 * SB 9/28/04 catch (com.tandem.util.FSException fe1) { SQLException se1 =
		 * null; SQLException se2; try { if (txBegin) tx.rollback(); if (cref !=
		 * null) tx.resume(cref); } catch (com.tandem.util.FSException fe2) {
		 * Object[] messageArguments = new Object[2]; messageArguments[0] =
		 * Short.toString(fe2.error); messageArguments[1] = fe2.getMessage();
		 * se1 = SQLMXMessages.createSQLException(conn.props_, conn.getLocale(),
		 * "transaction_error", messageArguments); } Object[] messageArguments =
		 * new Object[2]; messageArguments[0] = Short.toString(fe1.error);
		 * messageArguments[1] = fe1.getMessage(); se2 =
		 * SQLMXMessages.createSQLException(conn.props_, conn.getLocale(),
		 * "transaction_error", messageArguments); if (se1 != null)
		 * se2.setNextException(se1); throw se2; }
		 */
		/*
		 * SB 9/28/04 catch (SQLException se) { SQLException se1 = null; try {
		 * if (txBegin) tx.rollback(); if (cref != null) tx.resume(cref); }
		 * catch (com.tandem.util.FSException fe2) { Object[] messageArguments =
		 * new Object[2]; messageArguments[0] = Short.toString(fe2.error);
		 * messageArguments[1] = fe2.getMessage(); se1 =
		 * SQLMXMessages.createSQLException(conn.props_, conn.getLocale(),
		 * "transaction_error", messageArguments); } if (se1 != null)
		 * se.setNextException(se1); throw se; }
		 */
		/*
		 * SB 9/28/04 finally { conn.txid_ = currentTxid; }
		 */
		return startDataLocator_;
	}

	void insertDataLocatorRow(SQLMXConnection conn) throws SQLException {

		if (prepareInsert_) {
			String lobGetMaxDataLocSQL = "select max(data_locator) from "
					+ lobTableName_;
			GetMaxDataLoc_ = conn.prepareLobStatement(lobGetMaxDataLocSQL);
		}

		// ResultSet rs = GetMaxDataLoc_.executeQuery();
		ResultSet rs = ((SQLMXPreparedStatement) GetMaxDataLoc_).executeQuery();
		try {
			if (rs.next()) {
				startDataLocator_ = rs.getLong(1) + 1;
			}
		} finally {
			rs.close();
		}

		long dataLocVal = startDataLocator_ + T4Properties.reserveDataLocator_
				- 1;

		// Re-prepare special LOB table row if lobTableName_ has changed
		// (prepareInsert_ flag)
		// or if the data locator value has changed from previous statement
		// prepare.
		if (prepareInsert_ || (dataLocVal != prepDataLocVal_)) {
			String lobInsDataLocRowSQL;
			if (lobCharSet_ == InterfaceUtilities.SQLCHARSETCODE_UNICODE) // Unicode
			// data
			{
				lobInsDataLocRowSQL = "insert into "
						+ lobTableName_
						+ " (table_name, data_locator, chunk_no, lob_data) values ('ZZDATA_LOCATOR', 0, 0, cast("
						+ dataLocVal + " as VARCHAR(100) character set UCS2))";
			} else // ISO data
			{
				lobInsDataLocRowSQL = "insert into "
						+ lobTableName_
						+ " (table_name, data_locator, chunk_no, lob_data) values ('ZZDATA_LOCATOR', 0, 0, cast("
						+ dataLocVal
						+ " as VARCHAR(100) character set ISO88591))";
			}
			InsDataLocRow_ = conn.prepareLobStatement(lobInsDataLocRowSQL);
			prepDataLocVal_ = dataLocVal;
			prepareInsert_ = false;
		}

		InsDataLocRow_.executeUpdate();
	}

	void prepareGetStrtDataLocStmt(SQLMXConnection conn) throws SQLException {
		if (conn.prepareGetStrtDataLocStmt(lobTableName_, isBlob_, lobCharSet_)) {
			/*
			 * if (isBlob_) { GetStrtDataLoc_ =
			 * conn.LobPreparedStatements[conn.BLOB_GET_STRT_DATA_LOC_STMT]; }
			 * else { GetStrtDataLoc_ =
			 * conn.LobPreparedStatements[conn.CLOB_GET_STRT_DATA_LOC_STMT]; }
			 */
			// Set flag to re-prepare next two LOB statements in the path
			// (GetMaxDataLoc_ and InsDataLocRow_)
			prepareInsert_ = true;
		}
	}

	void closeLobStatements() {
		// This will be called by the SQLMXConnection object that holds
		// references to the DataLocator object(s)
		prepareInsert_ = true;
		try {
			if (GetMaxDataLoc_ != null) {
				GetMaxDataLoc_.close();
			}
		} catch (SQLException se) {
			// no-op
		}
		try {
			if (InsDataLocRow_ != null) {
				InsDataLocRow_.close();
			}
		} catch (SQLException se) {
			// no-op
		}
	}

	SQLMXDataLocator(SQLMXConnection conn, String lobTableName)
			throws SQLException {
		lobTableName_ = lobTableName;
		// Obtain the precision of the lob_data column to set the appropriate
		// chunk size for LOB data operations. Using the precision of the
		// lob_data
		// column in the ZZDATA_LOCATOR special row.
		String s = "select lob_data from " + lobTableName
				+ " where table_name = 'ZZDATA_LOCATOR' ";
		PreparedStatement ps = conn.prepareLobStatement(s);
		ResultSetMetaData rsmd = ps.getMetaData();
		lobCharSet_ = ((SQLMXResultSetMetaData) rsmd).getSqlCharset(1);
		// Set appropriate Unicode chunkSize_ based on character set type
		if (lobCharSet_ == InterfaceUtilities.SQLCHARSETCODE_UNICODE) {
			chunkSize_ = (rsmd.getPrecision(1)) / 2;
		} else {
			chunkSize_ = rsmd.getPrecision(1);
		}
		ps.close();
	}

	// Fields
	String lobTableName_;

	int lobCharSet_;

	long startDataLocator_;

	long currentDataLocator_;

	int chunkSize_;

	long prepDataLocVal_;

	boolean isBlob_;

	boolean prepareInsert_ = false;

	// PreparedStatement GetStrtDataLoc_;
	PreparedStatement GetMaxDataLoc_;

	PreparedStatement InsDataLocRow_;
}
