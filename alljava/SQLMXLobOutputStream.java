//
// Copyright 2003-2007
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
 * Filename    : SQLMXLobOutputStream.java
 * Author      : Selva, Swastik Bihani
 * Desctiption : This program implements the OutputStream interface. This object returned to the
 *		 application when Clob.setOutputStream() method or Blob.setOutputStream is called.
 *		 The application can use this object to write the clob/blob data
 */

package com.tandem.t4jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * <p>
 * This class implements the OutputStream interface, which can be used to write
 * Blob/Clob data.
 * </p>
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 */

public class SQLMXLobOutputStream extends OutputStream {
	public void close() throws IOException {
		if (!isClosed_) {
			flush();
			isClosed_ = true;
		}
	}

	public void flush() throws IOException {

		if (isClosed_) {
			throw new IOException("Output stream is in closed state");
		}
		if (!isFlushed_) {
			writeChunkThrowIO();
		}
	}

	public void write(byte[] b) throws IOException {
		if (b == null) {
			throw new IOException("Invalid input value");
		}
		write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int copyLen;
		int srcOffset;
		int tempLen = 0;

		if (isClosed_) {
			throw new IOException("Output stream is in closed state");
		}
		if (b == null) {
			throw new IOException("Invalid input value");
		}
		if (off < 0 || len < 0 || off > b.length) {
			throw new IndexOutOfBoundsException(
					"length or offset is less than 0 or offset is greater than the length of array");
		}
		srcOffset = off;
		copyLen = len;

		boolean writesBatched = false;
		boolean isUpdate = false;
		boolean isInsert = false;
		if ((copyLen + currentByte_) < (2 * lob_.chunkSize_)) {
			// No need to batch statements
			writesBatched = false;
		} else {
			// Better to batch statements
			writesBatched = true;
			try {
				if (currentChunkNo_ > updChunkNo_) { // Insert
					// If it is an insert --> update cannot be true int the
					// following
					// while loop
					lob_.prepareInsLobDataStmt();
					isInsert = true;
				} else { // Update
					// Even though it is an update right now, it could be that
					// in the following while loop, the write will be an insert
					// rather than an update
					lob_.prepareUpdLobDataStmt();
					isUpdate = true;
				}
			} catch (SQLException e) {
				throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
			}

		}

		// Number of rows that have been batched
		int rowsBatched = 0;
		// Solution 10-180108-5958: BLOB/CLOB - Last chunk is not inserted
		Boolean lastChunk = false;
		while (true) {
			if ((copyLen + currentByte_) < lob_.chunkSize_) {
				System.arraycopy(b, srcOffset, chunk_, currentByte_, copyLen);
				currentByte_ += copyLen;
				isFlushed_ = false;
				lastChunk = true;
			} else {
				tempLen = lob_.chunkSize_ - currentByte_;
				System.arraycopy(b, srcOffset, chunk_, currentByte_, tempLen);
				currentByte_ += tempLen;
			}

			if (writesBatched) {
				if (!isInsert && (currentChunkNo_ <= updChunkNo_)) {
					try {
						// If the update has crossed the existing allocated
						// LOB chunks, the code now needs to insert
						lob_.prepareInsLobDataStmt();
						isInsert = true;
					} catch (SQLException e) {
						throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
					}
				}
				writeBatchChunkThrowIO();
				rowsBatched++;
				if (rowsBatched >= SQLMXLob.MAX_LOB_BATCHED_ROWS) {
					rowsBatched = 0;
					try {
						if (isUpdate) {
							lob_.getUpdLobDataStmt().executeBatch();
							lob_.getUpdLobDataStmt().clearBatch();
						}
						if (isInsert) {
							lob_.getInsLobDataStmt().executeBatch();
							lob_.getInsLobDataStmt().clearBatch();
						}
					} catch (SQLException e) {
						throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
					}
				}
			} else {
				writeChunkThrowIO();
			}

			currentByte_ = 0;
			if (lastChunk)
				break;
			copyLen -= tempLen;
			srcOffset += tempLen;

		}
		// Once the while loop has exited, we need to execute the batches, if
		// any
		if (writesBatched && (rowsBatched > 0)) {
			rowsBatched = 0;
			try {
				if (isUpdate) {
					lob_.getUpdLobDataStmt().executeBatch();
					lob_.getUpdLobDataStmt().clearBatch();
				}
				if (isInsert) {
					lob_.getInsLobDataStmt().executeBatch();
					lob_.getInsLobDataStmt().clearBatch();
				}
			} catch (SQLException e) {
				throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
			}
		}
	}

	public void write(int b) throws IOException {
		if (isClosed_) {
			throw new IOException("Output stream is in closed state");
		}
		chunk_[currentByte_] = (byte) b;
		isFlushed_ = false;
		currentByte_++;
		if (currentByte_ == lob_.chunkSize_) {
			writeChunkThrowIO();
		}
	}

	void writeChunk() throws SQLException {
		byte[] tempChunk;

		if (currentChunkNo_ > updChunkNo_) {
			lob_.prepareInsLobDataStmt();
			// System.out.println("*** SQLMXLobOutputStream prepareInsLobDataStmt
			// 1");

			synchronized (lob_.getInsLobDataStmt()) {
				lob_.getInsLobDataStmt().setString(1, lob_.tableName_);
				lob_.getInsLobDataStmt().setLong(2, lob_.dataLocator_);
				lob_.getInsLobDataStmt().setInt(3, currentChunkNo_);
				if (currentByte_ != lob_.chunkSize_) {
					tempChunk = new byte[currentByte_];
					System.arraycopy(chunk_, 0, tempChunk, 0, currentByte_);
				} else {
					tempChunk = chunk_;
				}
				lob_.getInsLobDataStmt().setBytes(4, tempChunk);
				lob_.getInsLobDataStmt().executeUpdate();
				currentChunkNo_++;
				currentByte_ = 0;
			}
		} else {
			lob_.prepareUpdLobDataStmt();
			// System.out.println("*** SQLMXLobOutputStream
			// prepareUpdLobDataStmt");

			synchronized (lob_.getUpdLobDataStmt()) {
				lob_.getUpdLobDataStmt().setString(4, lob_.tableName_);
				lob_.getUpdLobDataStmt().setLong(5, lob_.dataLocator_);
				lob_.getUpdLobDataStmt().setInt(6, currentChunkNo_);
				lob_.getUpdLobDataStmt().setInt(1, updOffset_);
				if (updOffset_ != 0 || currentByte_ != lob_.chunkSize_) {
					tempChunk = new byte[currentByte_ - updOffset_];
					System.arraycopy(chunk_, updOffset_, tempChunk, 0,
							currentByte_ - updOffset_);
				} else {
					tempChunk = chunk_;
				}
				lob_.getUpdLobDataStmt().setInt(3, currentByte_ + 1);
				lob_.getUpdLobDataStmt().setBytes(2, tempChunk);
				lob_.getUpdLobDataStmt().executeUpdate();
				currentChunkNo_++;
				currentByte_ = 0;
				updOffset_ = 0;
			}
		}
		isFlushed_ = true;
	}

	void writeChunk2() throws SQLException {
		byte[] tempChunk;
		String tableName_;
		lob_.prepareInsSpjLobDataStmt();
		tableName_ = "SPJ_BASE_TABLE";
		synchronized (lob_.getSpjInsLobDataStmt()) {
			lob_.getSpjInsLobDataStmt().setString(1, tableName_);
			lob_.getSpjInsLobDataStmt().setLong(2, lob_.dataLocator_);
			lob_.getSpjInsLobDataStmt().setInt(3, currentChunkNo_);
			if (currentByte_ != lob_.chunkSize_) {
				tempChunk = new byte[currentByte_];
				System.arraycopy(chunk_, 0, tempChunk, 0, currentByte_);
			} else {
				tempChunk = chunk_;
			}
			lob_.getSpjInsLobDataStmt().setBytes(4, tempChunk);
			lob_.getSpjInsLobDataStmt().executeUpdate();
			currentChunkNo_++;
			currentByte_ = 0;
		}
		isFlushed_ = true;
	}
	void writeChunkThrowIO() throws IOException {
		try {
			writeChunk();
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}
	}

	void writeBatchChunkThrowIO() throws IOException {
		try {
			writeBatchChunk();
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}

	}

	void writeBatchChunk() throws SQLException {
		byte[] tempChunk;

		if (currentChunkNo_ > updChunkNo_) {
			// lob_.prepareInsLobDataStmt(conn_, lob_.lobTableName_);
			lob_.getInsLobDataStmt().setString(1, lob_.tableName_);
			lob_.getInsLobDataStmt().setLong(2, lob_.dataLocator_);
			lob_.getInsLobDataStmt().setInt(3, currentChunkNo_);
			if (currentByte_ != lob_.chunkSize_) {
				tempChunk = new byte[currentByte_];
				System.arraycopy(chunk_, 0, tempChunk, 0, currentByte_);
			} else {
				tempChunk = chunk_;
			}
			lob_.getInsLobDataStmt().setBytes(4, tempChunk);
			lob_.getInsLobDataStmt().addBatch();
			currentChunkNo_++;
			currentByte_ = 0;
		} else {
			// lob_.prepareUpdLobDataStmt(conn_, lob_.lobTableName_);
			lob_.getUpdLobDataStmt().setString(4, lob_.tableName_);
			lob_.getUpdLobDataStmt().setLong(5, lob_.dataLocator_);
			lob_.getUpdLobDataStmt().setInt(6, currentChunkNo_);
			lob_.getUpdLobDataStmt().setInt(1, updOffset_);
			if (updOffset_ != 0 || currentByte_ != lob_.chunkSize_) {
				tempChunk = new byte[currentByte_ - updOffset_];
				System.arraycopy(chunk_, updOffset_, tempChunk, 0, currentByte_
						- updOffset_);
			} else {
				tempChunk = chunk_;
			}
			lob_.getUpdLobDataStmt().setInt(3, currentByte_ + 1);
			lob_.getUpdLobDataStmt().setBytes(2, tempChunk);
			lob_.getUpdLobDataStmt().addBatch();
			currentChunkNo_++;
			currentByte_ = 0;
			updOffset_ = 0;

		}
		isFlushed_ = true;
	}

	void populate(InputStream is, int length) throws SQLException {
		int tempLen;
		int readLen;
		int retLen;
		boolean isInsert = false; // indicates whether populate is for insert
		// of LOB
		boolean isUpdate = false; // indicates whether populate is for insert
		// of LOB

		readLen = length;
		try {
			if (readLen > 0) {

				/* Check whether insert or update */
				if (currentChunkNo_ > updChunkNo_) { // Insert
					// If it is an insert --> update cannot be true int the
					// following
					// while loop
					lob_.prepareInsLobDataStmt();
					isInsert = true;
				} else { // Update
					// Even though it is an update right now, it could be that
					// in the following while loop, the write will be an insert
					// rather than an update
					lob_.prepareUpdLobDataStmt();
					isUpdate = true;
				}

				// Number of rows that have been batched
				int rowsBatched = 0;
				// While there is more data in the stream, call writeChunkBatch
				while (readLen > 0) {
					if (!isInsert && (currentChunkNo_ <= updChunkNo_)) {
						// If the update has crossed the existing allocated
						// LOB chunks, the code now needs to insert
						lob_.prepareInsLobDataStmt();
						isInsert = true;
					}

					if (readLen <= lob_.chunkSize_) {
						tempLen = readLen;
					} else {
						tempLen = lob_.chunkSize_;
					}
					retLen = is.read(chunk_, 0, tempLen);
					if (retLen == -1) {
						break;
					}
					currentByte_ = retLen;
					// writeChunk(); //Commented for performance during inserts
					// SB: 11/18/2004 - for LOB Support
					// Improving performance by batching write chunk statements

					try {
						writeBatchChunk();
						rowsBatched++;
						if (rowsBatched >= SQLMXLob.MAX_LOB_BATCHED_ROWS) {
							rowsBatched = 0;
							if (isUpdate) {
								lob_.getUpdLobDataStmt().executeBatch();
								lob_.getUpdLobDataStmt().clearBatch();
							}
							if (isInsert) {
								lob_.getInsLobDataStmt().executeBatch();
								lob_.getInsLobDataStmt().clearBatch();
							}
						}
					} catch (SQLException se) {
						if (isInsert) {
							lob_.getInsLobDataStmt().clearBatch();
						}
						if (isUpdate) {
							lob_.getUpdLobDataStmt().clearBatch();
						}
						throw se;
					}
					readLen -= retLen;
				}

				// Finally, after writeBatchChunk is called, insert/update
				if (rowsBatched > 0) {
					rowsBatched = 0;
					if (isUpdate) {
						lob_.getUpdLobDataStmt().executeBatch();
						lob_.getUpdLobDataStmt().clearBatch();
					}
					if (isInsert) {
						lob_.getInsLobDataStmt().executeBatch();
						lob_.getInsLobDataStmt().clearBatch();
					}
				}
			}
		} catch (IOException e) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = e.getMessage();
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "io_exception", messageArguments);
		} finally {
			if (isInsert) {
				lob_.getInsLobDataStmt().clearBatch();
			}
			if (isUpdate) {
				lob_.getUpdLobDataStmt().clearBatch();
			}
		}
	}

	void populate2(InputStream is, int length) throws SQLException {
		int tempLen;
		int readLen;
		int retLen;
		boolean isInsert = false; //indicates whether populate is for insert of LOB
		boolean isUpdate = false; //indicates whether populate is for insert of LOB
		readLen = length;
		try {
			if (readLen > 0) {
				lob_.prepareInsSpjBaseDataStmt();
				synchronized (lob_.getSpjInsBaseDataStmt()) {
					lob_.getSpjInsBaseDataStmt().setLong(1, lob_.dataLocator_);
					lob_.getSpjInsBaseDataStmt().executeUpdate();
				}
				while (readLen > 0) {
					if (readLen <= lob_.chunkSize_) {
						tempLen = readLen;
					} else {
						tempLen = lob_.chunkSize_;
					}
					retLen = is.read(chunk_, 0, tempLen);
					if (retLen == -1) {
						break;
					}
					currentByte_ = retLen;
					writeChunk2();
					readLen -= retLen;
				}
			}
		} catch (IOException e) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = e.getMessage();
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "io_exception", messageArguments);
		}
	}
	// constructors
	SQLMXLobOutputStream(SQLMXConnection connection, SQLMXLob lob, long pos)
			throws SQLException {
		long length;

		lob_ = lob;
		length = lob_.length();
		conn_ = connection;
		if (pos < 1 || pos > length + 1) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_position_value", null);
		}
		startingPos_ = pos;
		chunk_ = new byte[lob_.chunkSize_];
		isFlushed_ = false;
		if (length == 0) {
			updChunkNo_ = -1;
		} else {
			if ((length % lob_.chunkSize_) == 0) {
				updChunkNo_ = (int) (length / lob_.chunkSize_) - 1;
			} else {
				updChunkNo_ = (int) (length / lob_.chunkSize_);
			}
		}
		currentChunkNo_ = (int) ((pos - 1) / lob_.chunkSize_);
		currentByte_ = (int) ((pos - 1) % lob_.chunkSize_);
		updOffset_ = (int) ((pos - 1) % lob_.chunkSize_);
	}

	// Fields
	SQLMXLob lob_;

	long startingPos_;

	SQLMXConnection conn_;

	boolean isClosed_;

	byte[] chunk_;

	int currentByte_;

	int currentChunkNo_;

	boolean isFlushed_;

	int updChunkNo_;

	int updOffset_;
}
