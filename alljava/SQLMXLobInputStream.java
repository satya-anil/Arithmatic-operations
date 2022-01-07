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
 * Filename	: SQLMXLobInputStream.java
 * Author	: Selva, Swastik Bihani
 * Desctiption	: This program implements the InputStream interface. This object returned to the
 *		  application when Clob.getInputStream() method or Blob.getInputStream is called.
 *		  The application can use this object to read the clob/blob data
 *
 */

package com.tandem.t4jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * This class implements the InputStream interface that can be used to read
 * Blob/Clob data.
 * </p>
 * <p>
 * Copyright: (C) Copyright 2004-2007 Hewlett-Packard Development Company, L.P.
 * </p>
 */

public class SQLMXLobInputStream extends InputStream {
	public int available() throws IOException {
		long length;
		long readLength;

		if (isClosed_) {
			throw new IOException("Input stream is in closed state");
		}
		try {
			length = lob_.length();
			if (currentChunkNo_ > 0) {
				readLength = ((currentChunkNo_ - 1) * lob_.chunkSize_)
						+ currentByte_;
			} else {
				readLength = currentByte_;
			}
			return (int) (length - readLength);
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}
	}

	public void close() throws IOException {
		isClosed_ = true;
	}

	public void mark(int readlimit) {
	}

	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		int retValue = 0;

		if (isClosed_) {
			throw new IOException("Input stream is in closed state");
		}
		if (currentByte_ == bytesRead_) {
			retValue = readChunkThrowIO(null, 0, lob_.chunkSize_);
		}
		if (retValue != -1) {
			retValue = chunk_[currentByte_];
			// Should be a value between 0 and 255
			// -1 is mapped to 255, -2 is 254 etc
			if (retValue < 0) {
				retValue = 256 + retValue;
			}
			if (currentByte_ != bytesRead_) {
				currentByte_++;
			}
		}
		return retValue;
	}

	public int read(byte[] b) throws IOException {
		if (b == null) {
			throw new IOException("Invalid input value");
		}
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int readLen;
		int copyLen;
		int copyOffset;
		int tempLen = 0;
		int rowsToRead;
		int retLen;

		if (isClosed_) {
			throw new IOException("Input stream is in closed state");
		}
		if (b == null) {
			throw new IOException("Invalid input value");
		}
		copyLen = len;
		copyOffset = off;
		readLen = 0;
		if (currentByte_ < bytesRead_) {
			if (copyLen + currentByte_ <= bytesRead_) {
				System.arraycopy(chunk_, currentByte_, b, copyOffset, copyLen);
				currentByte_ += copyLen;
				readLen = copyLen;
				return readLen;
			} else {
				tempLen = bytesRead_ - currentByte_;
				System.arraycopy(chunk_, currentByte_, b, copyOffset, tempLen);
				copyOffset += tempLen;
				copyLen -= tempLen;
				currentByte_ += tempLen;
			}
		}
		readLen = readChunkThrowIO(b, copyOffset, copyLen);
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
			throw new IOException("Input stream is in closed state");
		}
		currentByte_ = 0;
		currentChunkNo_ = 0;
		bytesRead_ = 0;
		return;
	}

	public long skip(long n) throws IOException {
		long bytesToSkip;
		int noOfChunks = 0;
		int remBytes;
		long retLen = -1;
		long bytesSkipped = 0;
		int oldChunkNo;

		if (isClosed_) {
			throw new IOException("Input stream is in closed state");
		}
		if (n <= 0) {
			throw new IOException("Invalid input Value");
		}
		if (currentByte_ + n > bytesRead_) {
			bytesSkipped = bytesRead_ - currentByte_;
			bytesToSkip = n - bytesSkipped;
			currentByte_ += bytesSkipped;
		} else {
			currentByte_ += n;
			return n;
		}
		noOfChunks += (int) ((bytesToSkip - 1) / lob_.chunkSize_);
		if ((bytesToSkip % lob_.chunkSize_) == 0) {
			remBytes = lob_.chunkSize_;
		} else {
			remBytes = (int) (bytesToSkip % lob_.chunkSize_);
		}
		oldChunkNo = currentChunkNo_; // Which is already 1 more
		currentChunkNo_ = currentChunkNo_ + noOfChunks;
		retLen = readChunkThrowIO(null, 0, lob_.chunkSize_);
		if (retLen != -1) {
			bytesSkipped += (currentChunkNo_ - oldChunkNo - 1)
					* lob_.chunkSize_;
			if (retLen < remBytes) {
				remBytes = (int) retLen;
			}
			currentByte_ = remBytes;
			bytesSkipped += remBytes;
		} else {
			bytesSkipped += available();
			// Exclude the bytes that are in chunk already
			remBytes = (int) (bytesSkipped - (bytesRead_ - currentByte_));
			noOfChunks += (int) ((remBytes - 1) / lob_.chunkSize_);
			currentChunkNo_ = oldChunkNo + noOfChunks;
			// calculate the bytes in the chunk and set currentByte and
			// bytesRead
			// to reach EOD
			if (remBytes == 0) {
				currentByte_ = 0;
				bytesRead_ = 0;
			} else {
				if ((remBytes % lob_.chunkSize_) == 0) {
					currentByte_ = lob_.chunkSize_;
				} else {
					currentByte_ = (int) (remBytes % lob_.chunkSize_);
				}
				bytesRead_ = currentByte_;
			}
		}
		return bytesSkipped;
	}

	int readChunkThrowIO(byte[] b, int off, int len) throws IOException {
		int readLen;
		try {
			readLen = readChunk(b, off, len);
		} catch (SQLException e) {
			throw new IOException(SQLMXLob.convSQLExceptionToIO(e));
		}
		return readLen;
	}

	int readChunk(byte[] b, int off, int len) throws SQLException {
		int rowsToRead;
		byte[] data;
		int copyLen;
		int copyOffset;
		int readLen = 0;

		rowsToRead = (len - 1) / lob_.chunkSize_;
		lob_.prepareGetLobDataStmt();
		// System.out.println("*** SQLMXLobInputStream readChunk
		// prepareGetLobDataStmt");

		synchronized (lob_.getGetLobDataStmt()) {
			lob_.getGetLobDataStmt().setString(1, lob_.tableName_);
			lob_.getGetLobDataStmt().setLong(2, lob_.dataLocator_);
			lob_.getGetLobDataStmt().setInt(3, currentChunkNo_);
			lob_.getGetLobDataStmt().setInt(4, currentChunkNo_ + rowsToRead);
			copyLen = len;
			copyOffset = off;

			ResultSet rs = lob_.getGetLobDataStmt().executeQuery();
			try {
				if (rowsToRead > 1) {
					rs.setFetchSize(rowsToRead >= 8 ? 8 : rowsToRead);
				}
				while (rs.next()) {
					data = rs.getBytes(1);
					currentChunkNo_++;
					bytesRead_ = data.length;
					if (b == null) {
						System.arraycopy(data, 0, chunk_, 0, data.length);
						readLen += data.length;
						currentByte_ = 0;
						break;
					} else {
						if (copyLen >= data.length) {
							System.arraycopy(data, 0, b, copyOffset,
									data.length);
							copyLen -= data.length;
							readLen += data.length;
							copyOffset += data.length;
							currentByte_ = data.length;
						} else {
							System.arraycopy(data, 0, b, copyOffset, copyLen);
							// copy the rest of data to chunk
							System.arraycopy(data, copyLen, chunk_, copyLen,
									data.length - copyLen);
							readLen += copyLen;
							currentByte_ = copyLen;
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

	// Constructor

	SQLMXLobInputStream(SQLMXConnection connection, SQLMXLob lob) {
		lob_ = lob;
		conn_ = connection;
		chunk_ = new byte[lob_.chunkSize_];
	}

	// Fields
	SQLMXLob lob_;

	SQLMXConnection conn_;

	boolean isClosed_;

	byte[] chunk_;

	int currentByte_;

	int currentChunkNo_;

	int bytesRead_;
}
