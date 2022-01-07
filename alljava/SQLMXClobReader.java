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
 * Filename    : SQLMXClobReader.java
 * Author      : Selva, Swastik Bihani
 * Desctiption : This program implements the Reader interface. This object returned to the
 *		 application when Clob.getAsciiStream() method is called. The application
 *		 can use this object to read the clob data
 */

package com.tandem.t4jdbc;

import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * This class implements the Reader interface, which can be used to read Clob
 * data.
 * </p>
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 */

public class SQLMXClobReader extends Reader {

	public void close() throws IOException {
		isClosed_ = true;
	}

	public void mark(int readAheadLimit) throws IOException {
	}

	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		int retValue = 0;

		if (isClosed_) {
			throw new IOException("Reader is in closed state");
		}
		if (currentChar_ == charsRead_) {
			retValue = readChunkThrowIO(null, 0, clob_.chunkSize_);
		}
		if (retValue != -1) {
			retValue = chunk_[currentChar_];
			if (currentChar_ != charsRead_) {
				currentChar_++;
			}
		}
		return retValue;
	}

	public int read(char[] cbuf) throws IOException {
		if (cbuf == null) {
			throw new IOException("Invalid input value");
		}
		return read(cbuf, 0, cbuf.length);
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		int readLen;
		int copyLen;
		int copyOffset;
		int tempLen = 0;
		int rowsToRead;
		int retLen;

		if (isClosed_) {
			throw new IOException("Reader is in closed state");
		}
		if (cbuf == null) {
			throw new IOException("Invalid input value");
		}
		copyLen = len;
		copyOffset = off;
		readLen = 0;
		if (currentChar_ < charsRead_) {
			if (copyLen + currentChar_ <= charsRead_) {
				System.arraycopy(chunk_, currentChar_, cbuf, copyOffset,
						copyLen);
				currentChar_ += copyLen;
				readLen = copyLen;
				return readLen;
			} else {
				tempLen = charsRead_ - currentChar_;
				System.arraycopy(chunk_, currentChar_, cbuf, copyOffset,
						tempLen);
				copyOffset += tempLen;
				copyLen -= tempLen;
				currentChar_ += tempLen;
			}
		}
		readLen = readChunkThrowIO(cbuf, copyOffset, copyLen);
		if (readLen != -1) {
			retLen = readLen + tempLen;
		} else {
			retLen = tempLen;
		}
		if (retLen == 0) {
			return -1;
		} else {
			return retLen;
		}
	}

	public void reset() throws IOException {
		if (isClosed_) {
			throw new IOException("Reader is in closed state");
		}
		currentChar_ = 0;
		currentChunkNo_ = 0;
		charsRead_ = 0;
		return;
	}

	public long skip(long n) throws IOException {
		long charsToSkip;
		int noOfChunks = 0;
		int remChars;
		long retLen = 0;
		long charsSkipped = 0;
		int oldChunkNo;
		int readLen;

		if (isClosed_) {
			throw new IOException("Reader is in closed state");
		}
		if (n <= 0) {
			return 0;
		}
		if (currentChar_ + n > charsRead_) {
			charsSkipped = charsRead_ - currentChar_;
			charsToSkip = n - charsSkipped;
			currentChar_ += charsSkipped;
		} else {
			currentChar_ += n;
			return n;
		}
		noOfChunks += (int) ((charsToSkip - 1) / clob_.chunkSize_);
		if ((charsToSkip % clob_.chunkSize_) == 0) {
			remChars = clob_.chunkSize_;
		} else {
			remChars = (int) (charsToSkip % clob_.chunkSize_);
		}
		oldChunkNo = currentChunkNo_; // Which is already 1 more
		currentChunkNo_ = currentChunkNo_ + noOfChunks;
		retLen = readChunkThrowIO(null, 0, clob_.chunkSize_);
		if (retLen != -1) {
			charsSkipped += (currentChunkNo_ - oldChunkNo - 1)
					* clob_.chunkSize_;
			if (retLen < remChars) {
				remChars = (int) retLen;
			}
			currentChar_ = remChars;
			charsSkipped += remChars;
		} else {
			if (currentChunkNo_ > 0) {
				readLen = ((currentChunkNo_ - 1) * clob_.chunkSize_)
						+ currentChar_;
			} else {
				readLen = currentChar_;
			}
			try {
				charsSkipped = charsSkipped + clob_.length() - readLen;
			} catch (SQLException e) {
				throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
			}
			// Exclude the bytes that are in chunk already
			remChars = (int) (charsSkipped - (charsRead_ - currentChar_));
			noOfChunks += (int) ((remChars - 1) / clob_.chunkSize_);
			currentChunkNo_ = oldChunkNo + noOfChunks;
			// calculate the bytes in the chunk and set currentChar and
			// charsRead
			// to reach EOD
			if (remChars == 0) {
				currentChar_ = 0;
				charsRead_ = 0;
			} else {
				if ((remChars % clob_.chunkSize_) == 0) {
					currentChar_ = clob_.chunkSize_;
				} else {
					currentChar_ = (int) (remChars % clob_.chunkSize_);
				}
				charsRead_ = currentChar_;
			}
		}
		return charsSkipped;
	}

	int readChunkThrowIO(char[] c, int off, int len) throws IOException {
		int readLen;

		try {
			readLen = readChunk(c, off, len);
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}
		return readLen;

	}

	int readChunk(char[] c, int off, int len) throws SQLException {
		int rowsToRead;
		String data;
		int copyLen;
		int copyOffset;
		int readLen = 0;
		int dataLen;

		rowsToRead = (len - 1) / clob_.chunkSize_;
		clob_.prepareGetLobDataStmt();
		// System.out.println("*** SQLMXCLobReader readChunk
		// prepareGetLobDataStmt");

		synchronized (clob_.getGetLobDataStmt()) {
			clob_.getGetLobDataStmt().setString(1, clob_.tableName_);
			clob_.getGetLobDataStmt().setLong(2, clob_.dataLocator_);
			clob_.getGetLobDataStmt().setInt(3, currentChunkNo_);
			clob_.getGetLobDataStmt().setInt(4, currentChunkNo_ + rowsToRead);
			ResultSet rs = clob_.getGetLobDataStmt().executeQuery();
			if (rowsToRead > 1) {
				rs.setFetchSize(rowsToRead >= 8 ? 8 : rowsToRead);
			}
			copyLen = len;
			copyOffset = off;
			try {
				while (rs.next()) {
					data = rs.getString(1);
					currentChunkNo_++;
					charsRead_ = data.length();
					dataLen = charsRead_;
					if (c == null) {
						data.getChars(0, dataLen, chunk_, 0);
						readLen += dataLen;
						currentChar_ = 0;
						break;
					} else {
						if (copyLen >= dataLen) {
							data.getChars(0, dataLen, c, copyOffset);
							copyLen -= dataLen;
							readLen += dataLen;
							copyOffset += dataLen;
							currentChar_ = dataLen;
						} else {
							data.getChars(0, copyLen, c, copyOffset);
							// copy the rest of data to chunk
							data.getChars(copyLen, dataLen, chunk_, copyLen);
							readLen += copyLen;
							currentChar_ = copyLen;
							break;
						}
					}
				}
			} finally {
				rs.close();
			}
		}
		if (readLen == 0) {
			return -1;
		} else {
			return readLen;
		}
	}

	// constructors
	SQLMXClobReader(SQLMXConnection connection, SQLMXClob clob) {
		clob_ = clob;
		conn_ = connection;
		chunk_ = new char[clob_.chunkSize_];
	}

	// Fields
	SQLMXClob clob_;

	SQLMXConnection conn_;

	boolean isClosed_;

	char[] chunk_;

	int currentChar_;

	int currentChunkNo_;

	int charsRead_;
}
