// @ @ @ START COPYRIGHT @ @ @
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
 * Filename    : SQLMXClobWriter.java
 * Author      : Selva, Swastik Bihani
 * Desctiption : This program implements the Writer interface. This object returned to the
 *		 application when Clob.setAsciiStream() method is called. The application
 *		 can use this object to write the clob data
 */
package com.tandem.t4jdbc;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;

/**
 * <p>
 * This class implements the Writer interface, which can be used to write Clob
 * data.
 * </p>
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 */

public class SQLMXClobWriter extends Writer {
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
			writeChunkThrowIO(null);
		}
	}

	public void write(char[] cbuf) throws IOException {

		if (cbuf == null) {
			throw new IOException("Invalid input value");
		}
		write(cbuf, 0, cbuf.length);
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		int copyLen;
		int srcOffset;
		int tempLen;

		if (isClosed_) {
			throw new IOException("Writer is in closed state");
		}
		if (cbuf == null) {
			throw new IOException("Invalid input value");
		}
		if (off < 0 || len < 0 || off > cbuf.length) {
			throw new IndexOutOfBoundsException(
					"length or offset is less than 0 or offset is greater than the length of array");
		}
		srcOffset = off;
		copyLen = len;

		boolean writesBatched = false;
		boolean isUpdate = false;
		boolean isInsert = false;
		if ((copyLen + currentChar_) < (2 * clob_.chunkSize_)) {
			// No need to batch statements
			writesBatched = false;
		} else {
			// Better to batch statements
			writesBatched = true;
			try {
				if (currentChunkNo_ > updChunkNo_) { // Insert
					// If it is an insert --> update cannot be true in the
					// following
					// while loop
					clob_.prepareInsLobDataStmt();
					isInsert = true;
				} else { // Update
					// Even though it is an update right now, it could be that
					// in the following while loop, the write will be an insert
					// rather than an update
					clob_.prepareUpdLobDataStmt();
					isUpdate = true;
				}
			} catch (SQLException e) {
				throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
			}

		}

		// Number of rows that have been batched
		int rowsBatched = 0;
		while (true) {
			if (copyLen + currentChar_ < (clob_.chunkSize_)) {
				System
						.arraycopy(cbuf, srcOffset, chunk_, currentChar_,
								copyLen);
				currentChar_ += copyLen;
				isFlushed_ = false;
				break;
			} else {
				if (currentChar_ != 0) {
					tempLen = clob_.chunkSize_ - currentChar_;
					System.arraycopy(cbuf, srcOffset, chunk_, currentChar_,
							tempLen);
					currentChar_ += tempLen;
					writeChunkThrowIO(null);
				} else {
					tempLen = clob_.chunkSize_;
					currentChar_ += tempLen;
					if (writesBatched) {
						if (!isInsert && (currentChunkNo_ <= updChunkNo_)) {
							try {
								// If the update has crossed the existing
								// allocated
								// LOB chunks, the code now needs to insert
								clob_.prepareInsLobDataStmt();
								isInsert = true;
							} catch (SQLException e) {
								throw new IOException(SQLMXLob
										.convSQLExceptionToIO(e));
							}
						}
						writeBatchChunkThrowIO(new String(cbuf, srcOffset,
								tempLen));
						rowsBatched++;
						// After MAX_LOB_BATCHED_ROWS rows have been batched -
						// write it out
						if (rowsBatched >= SQLMXLob.MAX_LOB_BATCHED_ROWS) {
							rowsBatched = 0;
							try {
								if (isUpdate) {
									clob_.getUpdLobDataStmt().executeBatch();
									clob_.getUpdLobDataStmt().clearBatch();
								}
								if (isInsert) {
									clob_.getInsLobDataStmt().executeBatch();
									clob_.getInsLobDataStmt().clearBatch();
								}
							} catch (SQLException e) {
								throw new IOException(SQLMXLob
										.convSQLExceptionToIO(e));
							}
						}
					} else {
						writeChunkThrowIO(new String(cbuf, srcOffset, tempLen));
					}
				}
				copyLen -= tempLen;
				srcOffset += tempLen;
				currentChar_ = 0;
			}
		}
		// Once the while loop has exited, we need to execute the batches, if
		// any
		if (writesBatched && (rowsBatched > 0)) {
			rowsBatched = 0;
			try {
				if (isUpdate) {
					clob_.getUpdLobDataStmt().executeBatch();
					clob_.getUpdLobDataStmt().clearBatch();
				}
				if (isInsert) {
					clob_.getInsLobDataStmt().executeBatch();
					clob_.getInsLobDataStmt().clearBatch();
				}
			} catch (SQLException e) {
				throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
			}
		}
	}

	public void write(int c) throws IOException {
		if (isClosed_) {
			throw new IOException("Writer is in closed state");
		}
		chunk_[currentChar_] = (char) c;
		isFlushed_ = false;
		currentChar_++;
		if (currentChar_ == clob_.chunkSize_) {
			writeChunkThrowIO(null);
		}
	}

	public void write(String str) throws IOException {
		if (str == null) {
			throw new IOException("Invalid input value");
		}
		write(str, 0, str.length());
	}

	public void write(String str, int off, int len) throws IOException {

		int tempLen;
		int writeLen;
		int srcOff;

		writeLen = len;
		srcOff = off;

		if (isClosed_) {
			throw new IOException("Writer is in closed state");
		}
		if (str == null) {
			throw new IOException("Invalid input value");
		}
		char[] cbuf = new char[len];
		str.getChars(srcOff, srcOff + len, cbuf, 0);
		write(cbuf, 0, cbuf.length);
		/*
		 * if (currentChar_ != 0) { tempLen = clob_.chunkSize_ - currentChar_;
		 * if (writeLen > tempLen) { char[] cbuf = new char[tempLen];
		 * str.getChars(srcOff, srcOff + tempLen, cbuf, 0); write(cbuf, 0,
		 * cbuf.length); writeLen -= tempLen; srcOff += tempLen; } } while
		 * (writeLen > 0) { if (writeLen < clob_.chunkSize_) { break; } else {
		 * writeChunkThrowIO(str.substring(srcOff, srcOff + clob_.chunkSize_));
		 * writeLen -= clob_.chunkSize_; srcOff += clob_.chunkSize_; } } if
		 * (writeLen != 0) { char[] cbuf = new char[writeLen];
		 * str.getChars(srcOff, srcOff + writeLen, cbuf, 0); write(cbuf, 0,
		 * cbuf.length); }
		 */
	}

	void writeChunk(String str) throws SQLException {
		String tempStr;

		if (currentChunkNo_ > updChunkNo_) {
			clob_.prepareInsLobDataStmt();
			// System.out.println("*** SQLMXCLobWriter prepareInsLobDataStmt
			// 1");
			// System.out.println("*** SQLMXCLobWriter
			// conn_.ClobInsLobDataStmt_=" + conn_.ClobInsLobDataStmt_);

			synchronized (clob_.getInsLobDataStmt()) {
				clob_.getInsLobDataStmt().setString(1, clob_.tableName_);
				clob_.getInsLobDataStmt().setLong(2, clob_.dataLocator_);
				clob_.getInsLobDataStmt().setInt(3, currentChunkNo_);
				if (str == null) {
					if (currentChar_ != clob_.chunkSize_) {
						tempStr = new String(chunk_, 0, currentChar_);
					} else {
						tempStr = new String(chunk_);
					}
				} else {
					tempStr = str;
				}
				clob_.getInsLobDataStmt().setString(4, tempStr);
				clob_.getInsLobDataStmt().executeUpdate();
				currentChunkNo_++;
				currentChar_ = 0;
			}
		} else {
			clob_.prepareUpdLobDataStmt();
			// System.out.println("*** SQLMXCLobWriter prepareUpdLobDataStmt");

			synchronized (clob_.getUpdLobDataStmt()) {
				clob_.getUpdLobDataStmt().setString(4, clob_.tableName_);
				clob_.getUpdLobDataStmt().setLong(5, clob_.dataLocator_);
				clob_.getUpdLobDataStmt().setInt(6, currentChunkNo_);
				clob_.getUpdLobDataStmt().setInt(1, updOffset_);
				if (str == null) {
					if (updOffset_ != 0 || currentChar_ != clob_.chunkSize_) {
						tempStr = new String(chunk_, updOffset_, currentChar_
								- updOffset_);
					} else {
						tempStr = new String(chunk_);
					}
				} else {
					tempStr = str;
				}
				clob_.getUpdLobDataStmt().setInt(3, currentChar_ + 1);
				clob_.getUpdLobDataStmt().setString(2, tempStr);
				clob_.getUpdLobDataStmt().executeUpdate();
				currentChunkNo_++;
				currentChar_ = 0;
				updOffset_ = 0;
			}
		}
		isFlushed_ = true;
	}

	void writeChunkThrowIO(String str) throws IOException {
		try {
			writeChunk(str);
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}
	}

	void writeBatchChunkThrowIO(String str) throws IOException {
		try {
			writeBatchChunk(str);
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}

	}

	void writeBatchChunk(String str) throws SQLException {
		String tempStr;

		if (currentChunkNo_ > updChunkNo_) {
			clob_.getInsLobDataStmt().setString(1, clob_.tableName_);
			clob_.getInsLobDataStmt().setLong(2, clob_.dataLocator_);
			clob_.getInsLobDataStmt().setInt(3, currentChunkNo_);
			if (str == null) {
				if (currentChar_ != clob_.chunkSize_) {
					tempStr = new String(chunk_, 0, currentChar_);
				} else {
					tempStr = new String(chunk_);
				}
			} else {
				tempStr = str;
			}
			clob_.getInsLobDataStmt().setString(4, tempStr);
			// conn_.InsLobDataStmt_.executeUpdate();
			clob_.getInsLobDataStmt().addBatch();
			currentChunkNo_++;
			currentChar_ = 0;
		} else {
			clob_.prepareUpdLobDataStmt();

			clob_.getUpdLobDataStmt().setString(4, clob_.tableName_);
			clob_.getUpdLobDataStmt().setLong(5, clob_.dataLocator_);
			clob_.getUpdLobDataStmt().setInt(6, currentChunkNo_);
			clob_.getUpdLobDataStmt().setInt(1, updOffset_);
			if (str == null) {
				if (updOffset_ != 0 || currentChar_ != clob_.chunkSize_) {
					tempStr = new String(chunk_, updOffset_, currentChar_
							- updOffset_);
				} else {
					tempStr = new String(chunk_);
				}
			} else {
				tempStr = str;
			}
			clob_.getUpdLobDataStmt().setInt(3, currentChar_ + 1);
			clob_.getUpdLobDataStmt().setString(2, tempStr);
			// conn_.UpdLobDataStmt_.executeUpdate();
			clob_.getUpdLobDataStmt().addBatch();
			currentChunkNo_++;
			currentChar_ = 0;
			updOffset_ = 0;

		}
		isFlushed_ = true;
	}

	void populate(Reader ir, int length) throws SQLException {
		int tempLen;
		int readLen;
		int retLen;
		boolean isInsert = false; // indicates whether populate is for insert
		// of LOB
		boolean isUpdate = false; // indicates whether populate is for insert
		// of LOB

		readLen = length;
		try {

			/* Check whether insert or update */
			if (currentChunkNo_ > updChunkNo_) { // Insert
				// If it is an insert --> update cannot be true in the following
				// while loop
				clob_.prepareInsLobDataStmt();
				isInsert = true;
			} else { // Update
				// Even though it is an update right now, it could be that
				// in the following while loop, the write will be an insert
				// rather than an update
				clob_.prepareUpdLobDataStmt();
				isUpdate = true;
			}

			boolean writesBatched = false;
			if (readLen >= (2 * clob_.chunkSize_))
				writesBatched = true;

			// Number of rows that have been batched
			int rowsBatched = 0;
			while (readLen > 0) {
				if (!isInsert && (currentChunkNo_ <= updChunkNo_)) {
					// If the update has crossed the existing allocated
					// LOB chunks, the code now needs to insert
					clob_.prepareInsLobDataStmt();
					isInsert = true;
				}

				if (readLen <= clob_.chunkSize_) {
					tempLen = readLen;
				} else {
					tempLen = clob_.chunkSize_;
				}
				retLen = ir.read(chunk_, 0, tempLen);
				if (retLen == -1) {
					break;
				}
				currentChar_ = retLen;
				if (writesBatched) {
					try {
						writeBatchChunk(null);
						rowsBatched++;
						// After SQLMXLob.MAX_LOB_BATCHED_ROWS rows have been
						// batched - write it out
						if (rowsBatched >= SQLMXLob.MAX_LOB_BATCHED_ROWS) {
							rowsBatched = 0;
							if (isUpdate) {
								clob_.getUpdLobDataStmt().executeBatch();
								clob_.getUpdLobDataStmt().clearBatch();
							}
							if (isInsert) {
								clob_.getInsLobDataStmt().executeBatch();
								clob_.getInsLobDataStmt().clearBatch();
							}
						}
					} catch (SQLException se) {
						if (isInsert) {
							clob_.getInsLobDataStmt().clearBatch();
						}
						if (isUpdate) {
							clob_.getUpdLobDataStmt().clearBatch();
						}
						throw se;
					}
				} else {
					writeChunk(null);
				}
				readLen -= retLen;
			}
			// Finally, after writeBatchChunk is called, insert/update
			if (writesBatched && (rowsBatched > 0)) {
				rowsBatched = 0;
				if (isUpdate) {
					clob_.getUpdLobDataStmt().executeBatch();
					clob_.getUpdLobDataStmt().clearBatch();
				}
				if (isInsert) {
					clob_.getInsLobDataStmt().executeBatch();
					clob_.getInsLobDataStmt().clearBatch();
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
	SQLMXClobWriter(SQLMXConnection connection, SQLMXClob clob, long pos)
			throws SQLException {
		long length;

		clob_ = clob;
		length = clob_.length();
		conn_ = connection;
		if (pos < 1 || pos > length + 1) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_position_value", null);
		}
		startingPos_ = pos;
		chunk_ = new char[clob_.chunkSize_];
		isFlushed_ = false;
		if (length == 0) {
			updChunkNo_ = -1;
		} else {
			if ((length % clob_.chunkSize_) == 0) {
				updChunkNo_ = (int) (length / clob_.chunkSize_) - 1;
			} else {
				updChunkNo_ = (int) (length / clob_.chunkSize_);
			}
		}
		currentChunkNo_ = (int) ((pos - 1) / clob_.chunkSize_);
		currentChar_ = (int) ((pos - 1) % clob_.chunkSize_);
		updOffset_ = (int) ((pos - 1) % clob_.chunkSize_);
	}

	// Fields
	SQLMXClob clob_;

	long startingPos_;

	SQLMXConnection conn_;

	boolean isClosed_;

	char[] chunk_;

	int currentChar_;

	int currentChunkNo_;

	boolean isFlushed_;

	int updChunkNo_;

	int updOffset_;
}
