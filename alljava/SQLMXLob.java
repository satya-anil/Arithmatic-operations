//
// Copyright 2004-2007
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
 * Filename    : SQLMXLob.java
 * Author      : Selva, Swastik Bihani
 * Desctiption : SQLMXClob and SQLMXBlob extends this class. Some of the common methods are
 *		 implemented here
 */
package com.tandem.t4jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// import com.tandem.tmf.Current;

/**
 * HP JDBC Type 4 LOB class.
 * <P>
 * The <code>SQLMXLob</code> class is the base class providing LOB
 * functionalities common to both <code>{@link SQLMXClob}</code> and
 * <code>{@link SQLMXBlob}</code> classes.
 * <P>
 * When using the LOB support with HP JDBC Type 4 driver, make sure that the
 * properties are set as required by the <code>{@link T4Properties}</code>
 * class:
 * <ul>
 * <li> clobTableName - The LOB table where CLOB data will be stored.</li>
 * <li> blobTableName - The LOB table where BLOB data will be stored.</li>
 * </ul>
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 */
public abstract class SQLMXLob {
	// public methods
	/**
	 * Retrieves the length in bytes of the <code>SQLMXLob</code> value
	 * designated by this <code>SQLMXLob</code> object.
	 * 
	 * @return length of the <code>CLOB</code> in bytes
	 * @exception SQLException
	 *                if there is an error accessing the length of the
	 *                <code>SQLMXLob</code> value
	 */
	public long length() throws SQLException {
		long length = 0;

		checkIfCurrent();
		prepareGetLobLenStmt();
		// System.out.println("*** SQLMXLob length prepareGetLobLenStmt 1");
		// String s = "select sum(char_length(lob_data)) from " + lobTableName_
		// + " where table_name = ? and data_locator = ?";
		// conn_.GetLobLenStmt_ = conn_.prepareStatement(s);

		synchronized (getGetLobLenStmt()) {
			getGetLobLenStmt().setString(1, tableName_);
			getGetLobLenStmt().setLong(2, dataLocator_);
			// ResultSet rs = getGetLobLenStmt().executeQuery();
			ResultSet rs = ((SQLMXPreparedStatement) getGetLobLenStmt())
					.executeQuery();
			try {
				if (rs.next()) {
					length = rs.getLong(1);
				}
			} finally {
				rs.close();
			}
		}
		return length;
	}

	/**
	 * Truncates the <code>SQLMXLob</code> value that this <code>SQLMXLob</code>
	 * designates to have a length of <code>len</code> bytes.
	 * 
	 * @param len
	 *            the length, in bytes, to which the <code>SQLMXLob</code>
	 *            value should be truncated
	 * @exception SQLException
	 *                if there is an error accessing the <code>SQLMXLob</code>
	 *                value
	 */
	public void truncate(long len) throws SQLException {
		int chunkNo;
		int offset;
		byte[] chunk;

		if (len < 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "SQLMXLob.truncate(long)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		checkIfCurrent();
		chunkNo = (int) (len / chunkSize_);
		offset = (int) (len % chunkSize_);
		prepareDelLobDataStmt();
		// System.out.println("*** SQLMXLob truncate prepareDelLobDataStmt");
		// String s = "delete from " + lobTableName_ + " where table_name = ?
		// and data_locator = ? and chunk_no >= ? and chunk_no <= ?";
		// conn_.DelLobDataStmt_ = conn_.prepareStatement(s);

		synchronized (getDelLobDataStmt()) {
			getDelLobDataStmt().setString(1, tableName_);
			getDelLobDataStmt().setLong(2, dataLocator_);
			getDelLobDataStmt().setInt(3, chunkNo + 1);
			getDelLobDataStmt().setInt(4, Integer.MAX_VALUE);
			getDelLobDataStmt().executeUpdate();
		}
		if (offset != 0) {
			prepareTrunLobDataStmt();
			// System.out.println("*** SQLMXLob truncate
			// prepareTrunLobDataStmt");
			// String p = "update " + lobTableName_ + " set lob_data =
			// substring(lob_data, 1, ?) where table_name = ? and data_locator =
			// ? and chunk_no = ?";
			// conn_.TrunLobDataStmt_ = conn_.prepareStatement(p);

			synchronized (getTrunLobDataStmt()) {
				getTrunLobDataStmt().setInt(1, offset);
				getTrunLobDataStmt().setString(2, tableName_);
				getTrunLobDataStmt().setLong(3, dataLocator_);
				getTrunLobDataStmt().setInt(4, chunkNo);
				getTrunLobDataStmt().executeUpdate();
			}
		}

	}

	InputStream getInputStream() throws SQLException {
		if (inputStream_ != null) {
			try {
				inputStream_.close();
			} catch (IOException e) {
			} finally {
				inputStream_ = null;
			}
		}
		inputStream_ = new SQLMXLobInputStream(conn_, this);
		return inputStream_;

	}

	OutputStream setOutputStream(long pos) throws SQLException {
		if (outputStream_ != null) {
			try {
				outputStream_.close();
			} catch (IOException e) {
			} finally {
				outputStream_ = null;
			}
		}
		outputStream_ = new SQLMXLobOutputStream(conn_, this, pos);
		return outputStream_;
	}

	void close() {
		isCurrent_ = false;
		try {
			if (inputStream_ != null) {
				inputStream_.close();
			}
			if (outputStream_ != null) {
				outputStream_.close();
			}
		} catch (IOException e) {
		} finally {
			inputStream_ = null;
			outputStream_ = null;
		}
	}

	static String convSQLExceptionToIO(SQLException e) {
		SQLException e1;
		e1 = e;
		StringBuffer s = new StringBuffer(1000);
		do {
			s.append("SQLState :");
			s.append(e1.getSQLState());
			s.append(" ErrorCode :");
			s.append(e1.getErrorCode());
			s.append(" Message:");
			s.append(e1.getMessage());
		} while ((e1 = e1.getNextException()) != null);
		return s.toString();
	}

	void checkIfCurrent() throws SQLException {
		if (!isCurrent_) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = this;
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "lob_not_current", messageArguments);
		}
	}

	// *******************************************************************
	// * If Autocommit is enabled, and no external transaction exists, an
	// * exception will be thrown. In this case, JDBC cannot play the role of
	// * Autocommit (updating the base and lob tables in a single unit of work)
	// * because we return an OutputStream or Writer object to the application,
	// * who could hold it indefinitely. This is the case for
	// * Clob.setAsciiStream, Clob.setCharacterStream, and Blob.setBinaryStream.
	// *******************************************************************
	void checkAutoCommitExtTxn() throws SQLException {
		/*
		 * SB 9/28/04 Current tx = null; int txnState = -1; try { tx = new
		 * Current(); txnState = tx.get_status(); if (conn_.getAutoCommit() &&
		 * (txnState == tx.StatusNoTransaction)) { throw
		 * SQLMXMessages.createSQLException(conn_.props_,
		 * conn_.getLocale(),"invalid_lob_commit_state", null); } } catch
		 * (com.tandem.util.FSException fe1) { Object[] messageArguments = new
		 * Object[2]; messageArguments[0] = Short.toString(fe1.error);
		 * messageArguments[1] = fe1.getMessage(); throw
		 * SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(),
		 * "transaction_error_update", messageArguments); }
		 */
		if (conn_.getAutoCommit()) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_lob_commit_state", null);
		}
	}

	abstract void prepareGetLobLenStmt() throws SQLException;

	abstract void prepareDelLobDataStmt() throws SQLException;

	abstract void prepareGetLobDataStmt() throws SQLException;

	abstract void prepareUpdLobDataStmt() throws SQLException;

	abstract void prepareInsLobDataStmt() throws SQLException;

	abstract void prepareTrunLobDataStmt() throws SQLException;
//Added for SPJBlob -R3.0
	abstract void prepareInsSpjLobDataStmt() throws SQLException;
	abstract void prepareInsSpjBaseDataStmt() throws SQLException;
	abstract PreparedStatement getSpjInsLobDataStmt();
	abstract PreparedStatement getSpjInsBaseDataStmt();
	abstract PreparedStatement getGetLobLenStmt();

	abstract PreparedStatement getDelLobDataStmt();

	abstract PreparedStatement getTrunLobDataStmt();

	abstract PreparedStatement getInsLobDataStmt();

	abstract PreparedStatement getUpdLobDataStmt();

	abstract PreparedStatement getGetLobDataStmt();

	// Constructors
	SQLMXLob(SQLMXConnection connection, String tableName, long dataLocator,
			String lobTableName, boolean isBlob) throws SQLException {
		conn_ = connection;
		tableName_ = tableName;
		isCurrent_ = true;
		dataLocator_ = dataLocator;
		if (lobTableName != null) {
			lobTableName_ = lobTableName;

			// TODO: this is a quick fix for SELECT statements dealing with LOBs
			// causing table locks
			// it is silly for a SELECT to actually request and update the start
			// data locator stored in the table
			// a new function getChunkSize has been added to facilitate
			// initializing a DataLocator object without getting a start locator
			// all the LOB code needs to be rewritten IMO -- MD

			/*
			 * SQLMXDataLocator tempLoc = (SQLMXDataLocator)
			 * SQLMXConnection.lobTableToDataLoc_.get(lobTableName); if (tempLoc ==
			 * null) { //connection.reserveEmptyDataLocator_ = false;
			 * dataLocator = conn_.getDataLocator(lobTableName_, isBlob); }
			 * SQLMXDataLocator dataLoc = (SQLMXDataLocator)
			 * SQLMXConnection.lobTableToDataLoc_.get(lobTableName);
			 */
			chunkSize_ = conn_.getChunkSize(lobTableName_, isBlob);
		}
	}

	SQLMXLob(SQLMXConnection connection, String tableName, long dataLocator,
			InputStream x, int length, String lobTableName, boolean isBlob)
			throws SQLException {
		conn_ = connection;
		tableName_ = tableName;
		is_ = x;
		isLength_ = length;
		isCurrent_ = true;
		dataLocator_ = dataLocator;
		if (lobTableName != null) {
		
			if (connection.isCallProc_)
					
				lobTableName = connection.spjBlobTableName_;
				
			// System.out.println("*** lobTableName_=" + lobTableName_ + "
			// lobTableName=" + lobTableName);
			// System.out.println("*** connection.clobTableName_=" +
			// connection.clobTableName_ + " connection.blobTableName_=" +
			// connection.blobTableName_);

			lobTableName_ = lobTableName;
			SQLMXDataLocator tempLoc = (SQLMXDataLocator) SQLMXConnection.lobTableToDataLoc_
					.get(lobTableName);
			if (tempLoc == null) {
				// connection.reserveEmptyDataLocator_ = false;
				dataLocator = conn_.getDataLocator(lobTableName_, isBlob);
			}
			SQLMXDataLocator dataLoc = (SQLMXDataLocator) SQLMXConnection.lobTableToDataLoc_
					.get(lobTableName);
			chunkSize_ = dataLoc.chunkSize_;
		}
		// System.out.println("*** SQLMXLob connection.ClobInsLobDataStmt_=" +
		// connection.ClobInsLobDataStmt_);

	}

	
	SQLMXLob(SQLMXConnection connection, String lobTableName, boolean isBlob) throws SQLException {
		conn_ = connection;
		dataLocator_ = 0;
		tableName_ = null;
		isCurrent_ = true;

        lobTableName_ = lobTableName;
		chunkSize_ = conn_.getChunkSize(lobTableName_, isBlob);
		isConfiguredLob_ = true;
	}
	
	void setTablename(String tablename) {
        this.tableName_ = tablename;
	}


	protected boolean isLobDataLocatorSet() {
		if (this.dataLocator_ == 0) 
			return false;
		else
			return true;
	}
	
	
	protected boolean isConfiguredLob() throws SQLException {
		if (lobTableName_ == null) {
			SQLException se = SQLMXMessages.createSQLException(null, null,
					"no_lobTableName", null);
			throw se;
		}
		return isConfiguredLob_;
	}
	
	protected void free() throws SQLException {
		dataLocator_ = 0;
		tableName_ = null;
		isCurrent_ = false;
	}
	
	// fields
	SQLMXConnection conn_;

	String tableName_;

	long dataLocator_;

	SQLMXLobInputStream inputStream_;

	SQLMXLobOutputStream outputStream_;

	boolean isCurrent_;

	InputStream is_;

	int isLength_;

	String lobTableName_;

	int chunkSize_;

	public static final int MAX_LOB_BATCHED_ROWS = 100;
	
	private boolean isConfiguredLob_ = false;

}
