// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2004-2007,2017-18
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
 * Filename		: SQLMXBlob.java
 * Author		: Selva, Swastik Bihani
 * Desctiption	: This program implements the java.sql.Blob interface
 *
 */

package com.tandem.t4jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * The <code>SQLMXBlob</code> class implements the <code>Blob</code>
 * interface for the SQL <code>BLOB</code> type. An SQL <code>BLOB</code> is
 * a built-in type that stores a Binary Large Object as a column value in a row
 * of a database table. By default, drivers implement <code>Blob</code> using
 * an SQL <code>locator(BLOB)</code>, which means that a <code>Blob</code>
 * object contains a logical pointer to the SQL <code>BLOB</code> data rather
 * than the data itself. A <code>Blob</code> object is valid for the duration
 * of the transaction in which is was created.
 *
 * <P>
 * Methods in the interfaces {@link SQLMXResultSet},
 * {@link SQLMXCallableStatement}, and {@link SQLMXPreparedStatement}, such as
 * <code>getBlob</code> and <code>setBlob</code> allow a programmer to
 * access an SQL <code>BLOB</code> value. The <code>Blob</code> interface
 * provides methods for getting the length of an SQL <code>BLOB</code> (Binary
 * Large Object) value, for materializing a <code>BLOB</code> value on the
 * client, and for determining the position of a pattern of bytes within a
 * <code>BLOB</code> value. In addition, this interface has methods for
 * updating a <code>BLOB</code> value.
 *
 * <p>
 * Copyright: (C) Copyright 2004-2007, 2017-2018 Hewlett-Packard Development Company, L.P.
 * </p>
 */
public class SQLMXBlob extends SQLMXLob implements Blob, SQLMXOutputStreamMonitor {

	/**
	 * Retrieves the <code>BLOB</code> value designated by this
	 * <code>Blob</code> instance as a stream.
	 *
	 * @return a stream containing the <code>BLOB</code> data
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 *                value
	 * @see #setBinaryStream
	 */

	public InputStream getBinaryStream() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob",
					"getBinaryStream", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("getBinaryStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		// hack for UTT - Hibernate 5.2.* CORDA issue
		//checkIfCurrent();
		
		if (isConfiguredLob()) {
			inputStream_ = new ByteArrayInputStream(binaryData_);
			return inputStream_;
		} else {
			return getInputStream();
		}
	}

	/**
	 * Retrieves all or part of the <code>BLOB</code> value that this
	 * <code>Blob</code> object represents, as an array of bytes. This
	 * <code>byte</code> array contains up to <code>length</code>
	 * consecutive bytes starting at position <code>pos</code>.
	 *
	 * @param pos
	 *            the ordinal position of the first byte in the
	 *            <code>BLOB</code> value to be extracted; the first byte is
	 *            at position 1
	 * @param length
	 *            the number of consecutive bytes to be copied
	 * @return a byte array containing up to <code>length</code> consecutive
	 *         bytes from the <code>BLOB</code> value designated by this
	 *         <code>Blob</code> object, starting with the byte at position
	 *         <code>pos</code>
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 *                value
	 * @see #setBytes
	 * @since 1.2
	 */
	public byte[] getBytes(long pos, int length) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					length,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob", "getBytes", "",
					p);
		}
		
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					length,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("getBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		int startChunkNo;
		int endChunkNo;
		int offset;
		int copyLen;
		int copyOffset;
		int dataLength;
		int readLen;
		byte[] data;
		byte[] b;
		byte[] b1;
		
		checkIfCurrent();

		if (pos <= 0 || length < 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Blob.getBytes(long, int)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		
		if (isConfiguredLob()) {		
			if (binaryData_ == null) {				
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.getBytes(long, int). Blob does not have any data";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_
						.getLocale(), "invalid_input_value", messageArguments);
				}
			else if (pos-1+length > binaryData_.length){
				throw SQLMXMessages.createSQLException(conn_.props_, conn_
						.getLocale(), "invalid_position_value", null);
			}
			else if (pos +length > Integer.MAX_VALUE){
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.getBytes(long, int)." + 
					" Lob size is greater than maximum size (" + Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}
			else
			{
				data = new byte[length];
				System.arraycopy(binaryData_, (int)pos-1, data, 0, length);
			return data;
			}		 
		}
		
		startChunkNo = (int) ((pos - 1) / chunkSize_);
		endChunkNo = (int) ((pos - 1 + length) / chunkSize_);
		copyLen = length;
		offset = (int) ((pos - 1) % chunkSize_);
		copyOffset = 0;
		readLen = 0;
		b = new byte[length];
		prepareGetLobDataStmt();
		// System.out.println("*** SQLMXBLob getBytes prepareGetLobDataStmt");

		synchronized (conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]) {
			conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
					.setString(1, tableName_);
			conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT].setLong(
					2, dataLocator_);
			conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT].setInt(3,
					startChunkNo);
			conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT].setInt(4,
					endChunkNo);
			ResultSet rs = conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
					.executeQuery();
			try {
				while (rs.next()) {
					data = rs.getBytes(1);
					dataLength = data.length - offset;
					if (dataLength >= copyLen) {
						System.arraycopy(data, offset, b, copyOffset, copyLen);
						readLen += copyLen;
						break;
					} else {
						System.arraycopy(data, offset, b, copyOffset,
								dataLength);
						copyLen -= dataLength;
						copyOffset += dataLength;
						readLen += dataLength;
					}
					offset = 0; // reset the offset
				}
			} finally {
				rs.close();
			}
		}
		if (readLen == length) {
			return b;
		} else {
			b1 = new byte[readLen];
			System.arraycopy(b, 0, b1, 0, readLen);
			return b1;
		}
	}

	/**
	 * Retrieves the byte position in the <code>BLOB</code> value designated
	 * by this <code>Blob</code> object at which <code>pattern</code>
	 * begins. The search begins at position <code>start</code>.
	 *
	 * @param pattern
	 *            the <code>Blob</code> object designating the
	 *            <code>BLOB</code> value for which to search
	 * @param start
	 *            the position in the <code>BLOB</code> value at which to
	 *            begin searching; the first position is 1
	 * @return the position at which the pattern begins, else -1
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 *                value
	 */
	public long position(Blob pattern, long start) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pattern,
					start,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob", "position", "",
					p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pattern,
					start,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("position");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		byte[] searchPattern;

		checkIfCurrent();
		
		if (start <= 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Blob.position(Blob, long)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		searchPattern = pattern.getBytes(1L, (int) pattern.length());
		return position(searchPattern, start);
	}

	/**
	 * Retrieves the byte position at which the specified byte array
	 * <code>pattern</code> begins within the <code>BLOB</code> value that
	 * this <code>Blob</code> object represents. The search for
	 * <code>pattern</code> begins at position <code>start</code>.
	 *
	 * @param pattern
	 *            the byte array for which to search
	 * @param start
	 *            the position at which to begin searching; the first position
	 *            is 1
	 * @return the position at which the pattern appears; else -1
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 */
	public long position(byte[] pattern, long start) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pattern,
					start,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob", "position", "",
					p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pattern,
					start,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("position");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		byte[] blobData;
		long retValue;

		checkIfCurrent();
		
		if (start <= 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Blob.position(byte[], long)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}

		blobData = getBytes(start, (int) (length() - start + 1));
		retValue = findBytes(blobData, 0, blobData.length, pattern);
		if (retValue != -1) {
			retValue += start;
		}
		return retValue;
	}

	/**
	 * Retrieves a stream that can be used to write to the <code>BLOB</code>
	 * value that this <code>Blob</code> object represents. The stream begins
	 * at position <code>pos</code>.
	 *
	 * @param pos
	 *            the position in the <code>BLOB</code> value at which to
	 *            start writing
	 * @return a <code>java.io.OutputStream</code> object to which data can be
	 *         written
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 *                value
	 * @see #getBinaryStream
	 */
	public OutputStream setBinaryStream(long pos) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob",
					"setBinaryStream", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("setBinaryStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		// Check if Autocommit is set, and no external transaction exists
		checkAutoCommitExtTxn();
		checkIfCurrent();
		
		if (isConfiguredLob()) {
			if (pos  > Integer.MAX_VALUE) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.setBinaryStream(long). Lob size is greater than maximum size ("
						+ Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
						messageArguments);
			}
			else if (pos <= 0) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.setBinaryStream(long).";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
					"invalid_input_value", messageArguments);
			}

			
			if ((binaryData_ == null && pos > 1) ||
			    (binaryData_ != null && pos > (binaryData_.length + 1))) {
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
				"invalid_position_value", null);
			}

			SQLMXMonitorOutputStream os = new SQLMXMonitorOutputStream((int) pos);
			os.setMonitor(this);
			return os;
		} else
			return setOutputStream(pos);
	}

	/**
	 * Writes the given array of bytes to the <code>BLOB</code> value that
	 * this <code>Blob</code> object represents, starting at position
	 * <code>pos</code>, and returns the number of bytes written.
	 *
	 * @param pos
	 *            the position in the <code>BLOB</code> object at which to
	 *            start writing
	 * @param bytes
	 *            the array of bytes to be written to the <code>BLOB</code>
	 *            value that this <code>Blob</code> object represents
	 * @return the number of bytes written
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 *                value
	 * @see #getBytes
	 */
	public int setBytes(long pos, byte[] bytes) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					bytes, conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob", "setBytes",
					"", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					bytes, conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("setBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		
		checkIfCurrent();
		
		if (bytes == null || pos <= 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Blob.setBytes(long, byte[])";
			throw SQLMXMessages.createSQLException(conn_.props_,
					conn_.getLocale(), "invalid_input_value", messageArguments);
		}
		
		if (isConfiguredLob() == true) {
			if (pos + bytes.length > Integer.MAX_VALUE) {
						Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.setBytes(long, byte[])." + 
						" Lob size is greater than maximum size (" + 
						Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}

			// copy data into start of destination (from pos 0, copy data.length
			// bytes)
			if (binaryData_ == null) {
				if (pos > 1) {
					throw SQLMXMessages.createSQLException(conn_.props_,
							conn_.getLocale(), "invalid_position_value", null);
				}                  								
				byte[] ndata = new byte[(int) bytes.length + (int) pos - 1];
				System.arraycopy(bytes, 0, ndata, (int) pos - 1, bytes.length);
				binaryData_ = ndata;
			} else {
				if (pos <= binaryData_.length + 1) {
					long copyLen;
					if (pos - 1 == binaryData_.length)
						copyLen = binaryData_.length + bytes.length;
					else if (pos - 1 + bytes.length >= binaryData_.length)
						copyLen = (pos - 1) + bytes.length;
					else
						copyLen = binaryData_.length;

					byte[] ndata = new byte[(int) copyLen];

					if ((pos - 1 >= binaryData_.length)
							|| (pos - 1 + bytes.length >= binaryData_.length)) { // append
						System.arraycopy(binaryData_, 0, ndata, 0, (int) pos - 1);
						System.arraycopy(bytes, 0, ndata, (int) pos - 1,
								bytes.length);
					} else {
						System.arraycopy(binaryData_, 0, ndata, 0, (int) pos - 1);
						System.arraycopy(bytes, 0, ndata, (int) pos - 1,
								bytes.length);
						System.arraycopy(binaryData_, (int) pos - 1 + bytes.length,
								ndata, (int) pos + bytes.length - 1, binaryData_.length
										- (int) (pos - 1) - bytes.length);
				}
					binaryData_ = ndata;
			} else {
					throw SQLMXMessages.createSQLException(conn_.props_,
							conn_.getLocale(), "invalid_position_value", null);
			}
			}
			return bytes.length;
		} else
			return setBytes(pos, bytes, 0, bytes.length);
	}

	/**
	 * Writes all or part of the given <code>byte</code> array to the
	 * <code>BLOB</code> value that this <code>Blob</code> object represents
	 * and returns the number of bytes written. Writing starts at position
	 * <code>pos</code> in the <code>BLOB</code> value; <code>len</code>
	 * bytes from the given byte array are written.
	 *
	 * @param pos
	 *            the position in the <code>BLOB</code> object at which to
	 *            start writing
	 * @param bytes
	 *            the array of bytes to be written to this <code>BLOB</code>
	 *            object
	 * @param offset
	 *            the offset into the array <code>bytes</code> at which to
	 *            start reading the bytes to be set
	 * @param len
	 *            the number of bytes to be written to the <code>BLOB</code>
	 *            value from the array of bytes <code>bytes</code>
	 * @return the number of bytes written
	 * @exception SQLException
	 *                if there is an error accessing the <code>BLOB</code>
	 *                value
	 * @see #getBytes
	 */
	public int setBytes(long pos, byte[] bytes, int offset, int len)
			throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					bytes, offset, len,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob", "setBytes", "",
					p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					bytes, offset, len,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("setBytes");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}

		int updOffset;
		int updLen;
		int chunkNo;
		long lobLenForUpd;
		int byteOffset;
		int totalRetLen;
		int copyLen;
		long remLen;
		long lobLen;

		byte[] tempChunk = null;

		checkIfCurrent();
		
		if (pos <= 0 || len < 0 || offset < 0 || bytes == null ) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Blob.setBytes(long, byte[], int, int)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}

		lobLen = length();
		if (pos > lobLen + 1) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_position_value", null);
		}
		if (isConfiguredLob()){
		
			if ( offset + len > Integer.MAX_VALUE ) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = "setBytes(long, byte[], int , int)." + 
						" Lob size is greater than maximum size (" + 
						Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}
		
			byte[] tBytes = new byte[len];
			System.arraycopy(bytes, offset, tBytes, 0, len);
			return setBytes(pos,tBytes);
		}
		copyLen = len;
		remLen = pos - 1 + len; // Length to be either updated or inserted
		byteOffset = offset;
		totalRetLen = 0;
		chunkNo = (int) ((pos - 1) / chunkSize_);
		// calculate the length that can be updated rounded to chunk size
		if ((lobLen % chunkSize_) == 0) {
			lobLenForUpd = (lobLen / chunkSize_) * chunkSize_;
		} else {
			lobLenForUpd = ((lobLen / chunkSize_) + 1) * chunkSize_;
		}
		if (remLen <= lobLenForUpd) {
			updLen = len;
		} else {
			updLen = (int) (lobLenForUpd - (pos - 1));
		}
		if (updLen > 0) {
			updOffset = (int) ((pos - 1) % chunkSize_);
			prepareUpdLobDataStmt();

			synchronized (conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]) {
				conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
						.setString(4, tableName_);
				conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
						.setLong(5, dataLocator_);

				while (true) {
					// String is 0 based while substring in SQL is 1 based,
					// hence +1
					conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
							.setInt(6, chunkNo);
					conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
							.setInt(1, updOffset);
					if ((updOffset + updLen) <= chunkSize_) {
						conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
								.setInt(3, updOffset + updLen + 1);
						if ((byteOffset == 0)
								&& (updLen - updOffset == bytes.length)) {
							conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
									.setBytes(2, bytes);
						} else {
							tempChunk = new byte[updLen];
							System.arraycopy(bytes, byteOffset, tempChunk, 0,
									updLen);
							conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
									.setBytes(2, tempChunk);
						}
						conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
								.executeUpdate();
						totalRetLen += (updLen);
						byteOffset += (updLen);
						chunkNo++;
						break;
					} else {
						// conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT].setInt(3,
						// (chunkSize_ - updOffset) + 1);
						conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
								.setInt(3, chunkSize_ + 1);
						if (tempChunk == null
								|| tempChunk.length != chunkSize_ - updOffset) {
							tempChunk = new byte[chunkSize_ - updOffset];
						}
						System.arraycopy(bytes, byteOffset, tempChunk, 0,
								chunkSize_ - updOffset);
						conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
								.setBytes(2, tempChunk);
						conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT]
								.executeUpdate();
						totalRetLen += (chunkSize_ - updOffset);
						byteOffset += (chunkSize_ - updOffset);
						updLen -= (chunkSize_ - updOffset);
						chunkNo++;
					}
					updOffset = 0;
				}
			}
			copyLen = (int) (remLen - lobLenForUpd);// changed from len to
			// remLen
		}
		tempChunk = null;
		if (remLen > lobLenForUpd) {
			while (true) {
				prepareInsLobDataStmt();
				// System.out.println("*** SQLMXBLob prepareInsLobDataStmt 1");

				synchronized (conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]) {
					conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
							.setString(1, tableName_);
					conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
							.setLong(2, dataLocator_);
					conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
							.setInt(3, chunkNo);
					if (copyLen <= chunkSize_) {
						if (byteOffset == 0 && copyLen == bytes.length) {
							conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
									.setBytes(4, bytes);
						} else {
							tempChunk = new byte[copyLen];
							System.arraycopy(bytes, byteOffset, tempChunk, 0,
									copyLen);
							conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
									.setBytes(4, tempChunk);
						}
						conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
								.executeUpdate();
						totalRetLen += copyLen;
						break;
					} else {
						if (tempChunk == null) {
							tempChunk = new byte[chunkSize_];
						}
						System.arraycopy(bytes, byteOffset, tempChunk, 0,
								chunkSize_);
						conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
								.setBytes(4, tempChunk);
						conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
								.executeUpdate();
						byteOffset += chunkSize_;
						;
						copyLen -= chunkSize_;
						totalRetLen += chunkSize_;
					}
					chunkNo++;
				}
			}
		}
		return totalRetLen;

	}

	// This function populates the Blob data from one of the following:
	// 1. InputStream set in PreparedStatement.setBinaryStream
	// 2. From another clob set in PreparedStatement.setBlob or
	// ResultSet.updateBlob
	// This function is called at the time of PreparedStatement.executeUpdate,
	// execute and
	// executeBatch

	void populate() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob", "populate",
					"", p);
		}
		SQLMXLobOutputStream os;

		if (is_ != null) {
			os = (SQLMXLobOutputStream) setOutputStream(1);
			if (conn_.isCallProc_) {
				os.populate2(is_, isLength_);
			} else
			os.populate(is_, isLength_);
			is_ = null;
		} else if (inputLob_ != null) {
			populateFromBlob();
			inputLob_ = null;
		} else if (binaryData_ != null) {
			setBytes(1, binaryData_);
			binaryData_ = null;
		}
	}

	void populateFromBlob() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXBlob",
					"populateFromBlob", "", p);
		}
		long pos;
		byte[] b;
		int ret;
		ResultSet rs;
		SQLMXBlob inputBlob;
		int chunkNo = 0;

		pos = 1;
		if ((inputLob_ instanceof SQLMXBlob) && (isConfiguredLob() == false)) {
			// When Nonstop SQL/MX Database supports insert into a table by
			// selecting some other rows in
			// the same table, we should change the code to do so
			// Until then, we read a row and write to the same table with
			// different
			// data locator till all the rows are read
			inputBlob = (SQLMXBlob) inputLob_;
			prepareGetLobDataStmt();
			// System.out.println("*** SQLMXBLob populateFromBlob
			// prepareGetLobDataStmt");

			prepareInsLobDataStmt();
			// System.out.println("*** SQLMXBLob InsLobDataStmt 2");

			synchronized (conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]) {
				conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
						.setString(1, inputBlob.tableName_);
				conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
						.setLong(2, inputBlob.dataLocator_);
				conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
						.setInt(3, 0); // start
				// ChunkNo
				conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
						.setInt(4, Integer.MAX_VALUE);
				rs = conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT]
						.executeQuery();
				synchronized (conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]) {
					try {
						conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
								.setString(1, tableName_);
						conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
								.setLong(2, dataLocator_);
						while (rs.next()) {
							b = rs.getBytes(1);
							conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
									.setInt(3, chunkNo);
							conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
									.setBytes(4, b);
							conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT]
									.executeUpdate();
							chunkNo++;
						}
					} finally {
						rs.close();
					}
				}
			}
		} else {
			while (true) {
				b = inputLob_.getBytes(pos, chunkSize_);
				if (b.length == 0) {
					break;
				}
				ret = setBytes(pos, b);
                // Soln 10-171030-5322 : JDBC T4: SerialException returned while 
				// validating position for BLOB columns. 
                // Specifying a starting position that is greater than the available 
				// length is an error.
                if (b.length < chunkSize_)
                  break;

				pos += b.length;
			}
		}
	}

	// This code is taken from keryxsoft.hpl.hp.com/keryx-1.0a/
	// code/hplb/misc/ByteArray.java
	static final int findBytes(byte buf[], int off, int len, byte ptrn[]) {
		int buf_len = off + len;
		int ptrn_len = ptrn.length;
		int i; // index into buf
		int j; // index into ptrn;
		byte b = ptrn[0]; // next byte of interest

		for (i = off; i < buf_len;) {
			j = 0;
			while (i < buf_len && j < ptrn_len && buf[i] == ptrn[j]) {
				i++;
				j++;
			}
			if (i == buf_len || j == ptrn_len) {
				return i - j;
			} else {
				// We have to go back a bit as there may be an overlapping
				// match starting a bit later in buf...
				i = i - j + 1;
			}
		}
		return -1;
	}

	void prepareGetLobLenStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareGetLobLenStmt", "", p);
		}
		conn_.prepareGetLobLenStmt(lobTableName_, true);
	}

	void prepareDelLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareDelLobDataStmt", "", p);
		}
		conn_.prepareDelLobDataStmt(lobTableName_, true);
	}

	void prepareGetLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareGetLobDataStmt", "", p);
		}
		conn_.prepareGetLobDataStmt(lobTableName_, true);
	}

	void prepareUpdLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareUpdLobDataStmt", "", p);
		}

		conn_.prepareUpdLobDataStmt(lobTableName_, true);
	}

	void prepareInsLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareInsLobDatStmt", "", p);
		}
		conn_.prepareInsLobDataStmt(lobTableName_, true);
	}

	void prepareTrunLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareTrunLobDataStmt", "", p);
		}

		conn_.prepareTrunLobDataStmt(lobTableName_, true);
	}

//For SPJ BLOB - implement the same method for CLOB too -R3.0
	void prepareInsSpjLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareInsSpjLobDatStmt", "", p);
		}
		conn_.prepareInsSpjLobDataStmt(true);
	}
	PreparedStatement getSpjInsLobDataStmt() {
		return conn_.SpjLobPreparedStatements[conn_.SPJ_INS_BLOB_STMT];
	}
	void prepareInsSpjBaseDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"prepareInsSpjBaseDataStmt", "", p);
		}
		conn_.prepareInsSpjBaseDataStmt(true);
	}
	PreparedStatement getSpjInsBaseDataStmt() {
		return conn_.SpjLobPreparedStatements[conn_.SPJ_INS_BASE_STMT];
	}
	PreparedStatement getGetLobLenStmt() {
		return conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_LEN_STMT];
	}

	PreparedStatement getDelLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"getDelLobDataStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.BLOB_DEL_LOB_DATA_STMT];
	}

	PreparedStatement getTrunLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"getTrunLobDataStmt", "", p);
		}

		return conn_.LobPreparedStatements[conn_.BLOB_TRUN_LOB_DATA_STMT];
	}

	PreparedStatement getInsLobDataStmt() {
		return conn_.LobPreparedStatements[conn_.BLOB_INS_LOB_DATA_STMT];
	}

	PreparedStatement getUpdLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"getUpdLobDataStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.BLOB_UPD_LOB_DATA_STMT];
	}

	PreparedStatement getGetLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,conn_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXBlob",
					"getGetLobDataStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.BLOB_GET_LOB_DATA_STMT];
	}

	// Constructors
	SQLMXBlob(SQLMXConnection connection, String tableName, long dataLocator)
			throws SQLException {
		super(connection, tableName, dataLocator, connection.blobTableName_,
				true);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXBlob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.blobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_blobTableName", null);
		}
	}

	SQLMXBlob(SQLMXConnection connection, String tableName, long dataLocator,
			InputStream x, int length) throws SQLException {
		super(connection, tableName, dataLocator, x, length,
				connection.blobTableName_, true);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, x, length);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXBlob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, x, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.blobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_blobTableName", null);
		}
	}

	SQLMXBlob(SQLMXConnection connection, String tableName, long dataLocator,
			Blob inputLob) throws SQLException {
		super(connection, tableName, dataLocator, connection.blobTableName_,
				true);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, inputLob);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXBlob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, inputLob);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.blobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_blobTableName", null);
		}
		inputLob_ = inputLob;
	}

	SQLMXBlob(SQLMXConnection connection, String tableName, long dataLocator,
			byte[] b) throws SQLException {
		super(connection, tableName, dataLocator, connection.blobTableName_,
				true);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, b);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXBlob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, b);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXBlob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.blobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_blobTableName", null);
		}
		binaryData_ = b;
	}
	
	SQLMXBlob(SQLMXConnection connection) throws SQLException {
	    super(connection, connection.blobTableName_, true);
	}


	void setBlobData (Blob blob) {
        this.inputLob_ = blob;
	}

	public long length() throws SQLException {
		
		checkIfCurrent();		
		if (isConfiguredLob()) {
			if (binaryData_ != null)
				return binaryData_.length;
			else if (outputStream_ != null)
				return outputStream_.size();
			else if (inputLob_ != null)
				return inputLob_.length();
			else
				return 0;
		} else
			return super.length();
	}
	
	public void truncate(long length) throws SQLException {
		
		checkIfCurrent();

		if (isConfiguredLob()) {
			if (length < 0) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.truncate(long)";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
						messageArguments);
			}
			if( length > Integer.MAX_VALUE){
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Blob.truncate(long)." + 
					" Lob size is greater than maximum size (" + 
						Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}
			if( length > binaryData_.length ){
			Object[] messageArguments = new Object[1];
			messageArguments[0] = " Blob.truncate(long). Length should not exceed Blob size";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
					messageArguments);
			}
			
			byte[] data = null;
			int dataLength = 0;
			int copyLen = 0;
			data = new byte[(int)length];
			dataLength = binaryData_.length;
			copyLen = (int)length;
			if (dataLength >= copyLen) {
				System.arraycopy(binaryData_, 0, data, 0, copyLen);
				binaryData_ = data;
			} 
		} else {
			super.truncate(length);
		}
	}

	public void free() throws SQLException {
	        binaryData_ = null;
	        super.free();
	}	
	
	// fields
	Blob inputLob_;

	byte[] binaryData_;
		
	ByteArrayOutputStream outputStream_;
	
	ByteArrayInputStream inputStream_;

	// JDBC 4.x stubs 
	public InputStream getBinaryStream(long pos, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(conn_.props_,
				conn_.getLocale(), "getBinaryStream(long pos, long length)");
		
		return null;
	}

	@Override
	public synchronized void streamClosed(SQLMXMonitorOutputStream out, int pos) throws SQLException {
		if (out.size() != 0)
			setBytes(pos, out.toByteArray());
	}
}
