// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003-2007,2017-2018
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
 * Filename    : SQLMXClob.java
 * Author      : Selva, Swastik Bihani
 * Desctiption : This program implements the java.sql.Clob interface
 *
 */

package com.tandem.t4jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * The <code>SQLMXClob</code> class implements the <code>Clob</code> for the
 * SQL <code>CLOB</code> type. An SQL <code>CLOB</code> is a built-in type
 * that stores a Character Large Object as a column value in a row of a database
 * table. By default drivers implement a <code>Clob</code> object using an SQL
 * <code>locator(CLOB)</code>, which means that a <code>Clob</code> object
 * contains a logical pointer to the SQL <code>CLOB</code> data rather than
 * the data itself.
 * <P>
 * The <code>Clob</code> interface provides methods for getting the length of
 * an SQL <code>CLOB</code> (Character Large Object) value, for materializing
 * a <code>CLOB</code> value on the client, and for searching for a substring
 * or <code>CLOB</code> object within a <code>CLOB</code> value. Methods in
 * the interfaces {@link SQLMXResultSet}, {@link SQLMXCallableStatement}, and
 * {@link SQLMXPreparedStatement}, such as <code>getClob</code> and
 * <code>setClob</code> allow a programmer to access an SQL <code>CLOB</code>
 * value. In addition, this interface has methods for updating a
 * <code>CLOB</code> value.
 * 
 * <p>
 * Copyright: (C) Copyright 2004-2007,2017-2018 Hewlett-Packard Development Company, L.P.
 * </p>
 */

public class SQLMXClob extends SQLMXLob implements Clob, SQLMXWriterMonitor, 
	SQLMXOutputStreamMonitor {
	/**
	 * Retrieves the <code>CLOB</code> value designated by this
	 * <code>Clob</code> object as an ASCII stream.
	 * 
	 * @return a <code>java.io.InputStream</code> object containing the
	 *         <code>CLOB</code> data
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 * @see #setAsciiStream
	 */
	public InputStream getAsciiStream() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob",
					"getAsciiStream", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("getAsciiStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		checkIfCurrent();
		// Close the reader and inputStream hander over earlier
		if (reader_ != null) {
			try {
				reader_.close();
			} catch (IOException e) {
			} finally {
				reader_ = null;
			}
		}
		
		if (isConfiguredLob()) {
			inputStream_ = new ByteArrayInputStream(inputLobStr_.getBytes());
			return inputStream_;
		} else 
			return getInputStream();
	}

	/**
	 * Retrieves the <code>CLOB</code> value designated by this
	 * <code>Clob</code> object as a <code>java.io.Reader</code> object (or
	 * as a stream of characters).
	 * 
	 * @return a <code>java.io.Reader</code> object containing the
	 *         <code>CLOB</code> data
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 * @see #setCharacterStream
	 */
	public Reader getCharacterStream() throws SQLException

	{
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob",
					"getCharacterStream", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("getCharacterStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		checkIfCurrent();
		// Close the reader and inputStream hander over earlier
		if (reader_ != null) {
			try {
				reader_.close();
			} catch (IOException e) {
			} finally {
				reader_ = null;
			}
		}
		if (inputStream_ != null) {
			try {
				inputStream_.close();
			} catch (IOException e) {
			} finally {
				inputStream_ = null;
			}
		}
		
		if (isConfiguredLob()) {
			creader_ = new CharArrayReader(inputLobStr_.toCharArray());
			return creader_;
		} else {
			reader_ = new SQLMXClobReader(conn_, this);
			return reader_;
		}
	}

	/**
	 * Retrieves a copy of the specified substring in the <code>CLOB</code>
	 * value designated by this <code>Clob</code> object. The substring begins
	 * at position <code>pos</code> and has up to <code>length</code>
	 * consecutive characters.
	 * 
	 * @param pos
	 *            the first character of the substring to be extracted. The
	 *            first character is at position 1.
	 * @param length
	 *            the number of consecutive characters to be copied
	 * @return a <code>String</code> that is the specified substring in the
	 *         <code>CLOB</code> value designated by this <code>Clob</code>
	 *         object
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 */
	public String getSubString(long pos, int length) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					length,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob",
					"getSubString", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos,
					length,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("getSubString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (pos <= 0 || length < 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Clob.getSubString(long, int)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		
		checkIfCurrent();
		
		if (isConfiguredLob()) {
		
			if( length + pos > Integer.MAX_VALUE ){
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.getSubString(long, int)." + 
					" Lob size is greater than maximum size (" + 
						Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}
		
			if ((inputLobStr_ == null) && (outputStream_ == null))
				return null;
				
			if ((inputLobStr_ == null) && (outputStream_ != null)) {
				inputLobStr_ = outputStream_.toString();
			} 
			
			if (inputLobStr_ != null) {
				
				if (pos + length > length())
					return inputLobStr_.substring((int)(pos-1));
				else 
					return inputLobStr_.substring((int)(pos-1), (int)(length+pos-1)); 
					
			} else
				return null;
		}
		
		int startChunkNo;
		int endChunkNo;
		int offset;
		int copyLen;
		int dataLength;
		String data;
		StringBuffer retString;

		startChunkNo = (int) ((pos - 1) / chunkSize_);
		endChunkNo = (int) ((pos - 1 + length) / chunkSize_);
		copyLen = length;
		offset = (int) ((pos - 1) % chunkSize_);
		retString = new StringBuffer(length);

		prepareGetLobDataStmt();
		// System.out.println("*** SQLMXCLob getSubString
		// prepareGetLobDataStmt");

		synchronized (conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]) {
			conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
					.setString(1, tableName_);
			conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT].setLong(
					2, dataLocator_);
			conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT].setInt(3,
					startChunkNo);
			conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT].setInt(4,
					endChunkNo);
			ResultSet rs = conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
					.executeQuery();
			try {
				while (rs.next()) {
					data = rs.getString(1);
					dataLength = data.length() - offset;
					if (dataLength >= copyLen) {
						retString.append(data.substring(offset, offset
								+ copyLen));
						break;
					} else {
						if (offset == 0) {
							retString.append(data);
						} else {
							retString.append(data.substring(offset));
						}
						copyLen -= dataLength;
					}
					offset = 0; // reset the offset
				}
			} finally {
				rs.close();
			}
		}
		return retString.toString();
	}

	/**
	 * Retrieves the character position at which the specified <code>Clob</code>
	 * object <code>searchstr</code> appears in this <code>Clob</code>
	 * object. The search begins at position <code>start</code>.
	 * 
	 * @param searchstr
	 *            the <code>Clob</code> object for which to search
	 * @param start
	 *            the position at which to begin searching; the first position
	 *            is 1
	 * @return the position at which the <code>Clob</code> object appears or
	 *         -1 if it is not present; the first position is 1
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 */
	public long position(Clob searchstr, long start) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, searchstr,
					start);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob", "position",
					"", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, searchstr,
					start);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("position");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		String searchString;

		if (start <= 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Clob.position(Clob, long)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		checkIfCurrent();
		searchString = searchstr.getSubString(1L, (int) searchstr.length());
		return position(searchString, start);
	}

	/**
	 * Retrieves the character position at which the specified substring
	 * <code>searchstr</code> appears in the SQL <code>CLOB</code> value
	 * represented by this <code>Clob</code> object. The search begins at
	 * position <code>start</code>.
	 * 
	 * @param searchstr
	 *            the substring for which to search
	 * @param start
	 *            the position at which to begin searching; the first position
	 *            is 1
	 * @return the position at which the substring appears or -1 if it is not
	 *         present; the first position is 1
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 */
	public long position(String searchstr, long start) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, searchstr,
					start);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob", "position",
					"", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, searchstr,
					start);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("position");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		String clobData;
		long retValue;

		if (start <= 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Clob.position(String, long)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		checkIfCurrent();
		clobData = getSubString(start, (int) length());
		retValue = clobData.indexOf(searchstr);
		if (retValue != -1) {
			retValue += start;
		}
		return retValue;
	}

	/**
	 * Retrieves a stream to be used to write ASCII characters to the
	 * <code>CLOB</code> value that this <code>Clob</code> object
	 * represents, starting at position <code>pos</code>.
	 * 
	 * @param pos
	 *            the position at which to start writing to this
	 *            <code>CLOB</code> object
	 * @return the stream to which ASCII encoded characters can be written
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 * @see #getAsciiStream
	 * 
	 */
	public OutputStream setAsciiStream(long pos) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob",
					"setAsciiStream", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("setAsciiStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		// Check if Autocommit is set, and no external transaction exists
		checkAutoCommitExtTxn();
		checkIfCurrent();
		// Close the writer and OutputStream hander over earlier
		if (writer_ != null) {
			try {
				writer_.close();
			} catch (IOException e) {
			} finally {
				writer_ = null;
			}
		}
		if (isConfiguredLob()) {
			if (pos  > Integer.MAX_VALUE) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.setAsciiStream(long). Lob size is greater than maximum size ("
						+ Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
						messageArguments);
			} else if ( pos <= 0) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.setAsciiStream(long).";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
					"invalid_input_value", messageArguments);
			}
			
			if ((inputLobStr_ == null && pos > 1) ||
			    (inputLobStr_ != null && pos > (inputLobStr_.length() + 1))) {
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
				"invalid_position_value", null);

			}
			
			SQLMXMonitorOutputStream os = new SQLMXMonitorOutputStream((int)pos);
			os.setMonitor(this);
			return os;
		} else 
			return setOutputStream(pos);
	}

	/**
	 * Retrieves a stream to be used to write a stream of Unicode characters to
	 * the <code>CLOB</code> value that this <code>Clob</code> object
	 * represents, at position <code>pos</code>.
	 * 
	 * @param pos
	 *            the position at which to start writing to the
	 *            <code>CLOB</code> value
	 * 
	 * @return a stream to which Unicode encoded characters can be written
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 * @see #getCharacterStream
	 */
	public Writer setCharacterStream(long pos) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob",
					"setCharacterStream", "", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("setCharacterStream");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		// Check if Autocommit is set, and no external transaction exists
		checkAutoCommitExtTxn();
		checkIfCurrent();
		// Close the writer and OutputStream hander over earlier
		if (writer_ != null) {
			try {
				writer_.close();
			} catch (IOException e) {
			} finally {
				writer_ = null;
			}
		}
		if (outputStream_ != null) {
			try {
				outputStream_.close();
			} catch (IOException e) {
			} finally {
				outputStream_ = null;
			}
		}
		
		if (isConfiguredLob()) {
			
			if (pos  > Integer.MAX_VALUE) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.setCharacterStream(long). Lob size is greater than maximum size ("
						+ Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
						messageArguments);
			} else if ( pos <= 0) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.setCharacterStream(long).";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
					"invalid_input_value", messageArguments);
			}
			
			if ((inputLobStr_ == null && pos > 1) ||
			    (inputLobStr_ != null && pos > (inputLobStr_.length() + 1))) {
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
				"invalid_position_value", null);

			}

			SQLMXMonitorWriter writer = new SQLMXMonitorWriter((int) pos);
			writer.setMonitor(this);
			return writer;

		} else {
			writer_ = new SQLMXClobWriter(conn_, this, pos);
			return writer_;
		}
	}

	/**
	 * Writes the given Java <code>String</code> to the <code>CLOB</code>
	 * value that this <code>Clob</code> object designates at the position
	 * <code>pos</code>.
	 * 
	 * @param pos
	 *            the position at which to start writing to the
	 *            <code>CLOB</code> value that this <code>Clob</code> object
	 *            represents
	 * @param str
	 *            the string to be written to the <code>CLOB</code> value that
	 *            this <code>Clob</code> designates
	 * @return the number of characters written
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 */
	public int setString(long pos, String str) throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos, str,
					conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob", "setString",
					"", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos, str,
					conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("setString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		
		checkIfCurrent();		
		if (str == null || pos <= 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Clob.setString(long, String).";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
					messageArguments);
		}

		if (isConfiguredLob() == true) {
			if (pos + str.length() > Integer.MAX_VALUE) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.setString(long, String). " + 
						" Lob size is greater than maximum size (" + 
						Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}
			if (inputLobStr_ == null) {
				if (pos > 1) {
					throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
					"invalid_position_value", null);				
				}
				inputLobStr_ = str;
			} else {
				String data;
				
				if (pos > inputLobStr_.length() + 1) {
					throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), 
					"invalid_position_value", null);				
				}
				else if ((pos - 1) >= inputLobStr_.length()) { // append
					data = inputLobStr_ + str;
				} else if (((pos - 1) + str.length()) >= inputLobStr_.length()) {
					data = inputLobStr_.substring(0, (int) pos - 1) + str;
				} else {
					data = inputLobStr_.substring(0, (int) pos - 1) + str;
					data += inputLobStr_.substring((int) (pos - 1 + str.length()));
				}
				inputLobStr_ = data;
			}
			return inputLobStr_.length();
		} else
			return setString(pos, str, 0, str.length());

	}

	/**
	 * Writes <code>len</code> characters of <code>str</code>, starting at
	 * character <code>offset</code>, to the <code>CLOB</code> value that
	 * this <code>Clob</code> represents.
	 * 
	 * @param pos
	 *            the position at which to start writing to this
	 *            <code>CLOB</code> object
	 * @param str
	 *            the string to be written to the <code>CLOB</code> value that
	 *            this <code>Clob</code> object represents
	 * @param offset
	 *            the offset into <code>str</code> to start reading the
	 *            characters to be written
	 * @param len
	 *            the number of characters to be written
	 * @return the number of characters written
	 * @exception SQLException
	 *                if there is an error accessing the <code>CLOB</code>
	 *                value
	 */
	public int setString(long pos, String str, int offset, int len)
			throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos, str,
					offset, len,conn_);
			conn_.props_.t4Logger_.logp(Level.FINE, "SQLMXClob", "setString",
					"", p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_, pos, str,
					offset, len,conn_);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("setString");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		int endChunkNo;
		int updOffset;
		int updLen;
		int chunkNo;
		long lobLenForUpd;
		int strOffset;
		int retLen;
		int totalRetLen;
		int copyLen;
		long remLen;
		long lobLen;
		

	

		if (str == null || pos <= 0 || len < 0 || offset < 0) {
			Object[] messageArguments = new Object[1];
			messageArguments[0] = "Clob.setString(long, String, int, int)";
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_input_value", messageArguments);
		}
		checkIfCurrent();
		lobLen = length();
		if (pos > lobLen + 1) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "invalid_position_value", null);
		}
		
		if (isConfiguredLob() == true) {
			if (offset + len  > Integer.MAX_VALUE) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = " Clob.setString(long, String , int , int)." + 
					" Lob size is greater than maximum size (" + 
					Integer.MAX_VALUE + " bytes)";
				throw SQLMXMessages.createSQLException(conn_.props_,
						conn_.getLocale(), "invalid_input_value", messageArguments);
			}
			String data = str.substring(offset, offset+len);
			return setString(pos,data);
		} 
		
		copyLen = len;
		remLen = pos - 1 + len; // Length that need to be either updated or
		// inserted
		strOffset = offset;
		totalRetLen = 0;
		chunkNo = (int) ((pos - 1) / chunkSize_); // Starting chunkNo
		// Calculate the length that can be updated rounded to chunk size
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
			// System.out.println("*** SQLMXCLob setString
			// prepareUpdLobDataStmt");

			synchronized (conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]) {
				conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
						.setString(4, tableName_);
				conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
						.setLong(5, dataLocator_);

				while (true) {
					conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
							.setInt(6, chunkNo);
					conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
							.setInt(1, updOffset);
					if ((updOffset + updLen) <= chunkSize_) {
						conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
								.setInt(3, updOffset + updLen + 1);
						conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
								.setString(2, str.substring(strOffset,
										strOffset + updLen));
						conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
								.executeUpdate();
						totalRetLen += (updLen);
						// strOffset += (updLen + updOffset);
						strOffset += (updLen);
						chunkNo++;
						break;
					} else {
						// conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT].setInt(3,
						// (chunkSize_ - updOffset) + 1);
						conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
								.setInt(3, chunkSize_ + 1);
						conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
								.setString(2, str.substring(strOffset,
										strOffset + chunkSize_ - updOffset));
						conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT]
								.executeUpdate();
						totalRetLen += (chunkSize_ - updOffset);
						strOffset += (chunkSize_ - updOffset);
						updLen -= (chunkSize_ - updOffset);
						chunkNo++;
					}
					updOffset = 0;
				}
			}
			copyLen = (int) (remLen - lobLenForUpd);// changed len to remLen
		}
		if (remLen > lobLenForUpd) {
			while (true) {
				prepareInsLobDataStmt();
				// System.out.println("*** SQLMXCLob prepareInsLobDataStmt 1");

				synchronized (conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]) {
					conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
							.setString(1, tableName_);
					conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
							.setLong(2, dataLocator_);
					conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
							.setInt(3, chunkNo);
					if (copyLen <= chunkSize_) {
						conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
								.setString(4, str.substring(strOffset,
										strOffset + copyLen));
						conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
								.executeUpdate();
						strOffset += copyLen;
						totalRetLen += copyLen;
						break;
					} else {
						conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
								.setString(4, str.substring(strOffset,
										strOffset + chunkSize_));
						conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
								.executeUpdate();
						strOffset += chunkSize_;
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

	void close() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_
					.logp(Level.FINE, "SQLMXClob", "close", "", p);
		}

		try {
			if (reader_ != null) {
				reader_.close();
			}
			if (writer_ != null) {
				writer_.close();
			}
		} catch (IOException e) {
		} finally {
			reader_ = null;
			writer_ = null;
		}
		super.close();
	}

	// This function populates the Clob data from one of the following:
	// 1. InputStream set in PreparedStatement.setAsciiStream
	// 2. Reader set in PreparedStatement.setCharacterStream
	// 3. From another clob set in PreparedStatement.setClob or
	// ResultSet.updateClob
	// This function is called at the time of PreparedStatement.executeUpdate,
	// execute and
	// executeBatch

	void populate() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINER) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob", "populate",
					"", p);
		}

		SQLMXLobOutputStream os;
		SQLMXClobWriter cw;

		if (is_ != null) {
			os = (SQLMXLobOutputStream) setOutputStream(1);
			os.populate(is_, isLength_);
			is_ = null;
		} else if (ir_ != null) {
			cw = (SQLMXClobWriter) setCharacterStream(1);
			cw.populate(ir_, irLength_);
			ir_ = null;
		} else if (inputLob_ != null) {
			populateFromClob();
			inputLob_ = null;
		} else if (inputLobStr_ != null) {
			setString(1, inputLobStr_);
			inputLobStr_ = null;
		}
	}

	void populateFromClob() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"populateFromClob", "", p);
		}

		long pos;
		String s;
		int ret;
		ResultSet rs;
		SQLMXClob inputClob;
		int chunkNo = 0;

		pos = 1;
		if ((inputLob_ instanceof SQLMXClob) && (isConfiguredLob() == false)) {
			// When SQL supports insert into a table by selecting some other
			// rows in
			// the same table, we should change the code to do so
			// Until then, we read a row and write to the same table with
			// different
			// data locator till all the rows are read
			inputClob = (SQLMXClob) inputLob_;
			prepareGetLobDataStmt();
			// System.out.println("*** SQLMXCLob populateFromClob
			// prepareGetLobDataStmt");

			prepareInsLobDataStmt();
			// System.out.println("*** SQLMXBLob prepareInsLobDataStmt 2");

			synchronized (conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]) {
				conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
						.setString(1, inputClob.tableName_);
				conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
						.setLong(2, inputClob.dataLocator_);
				conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
						.setInt(3, 0); // start
				// ChunkNo
				conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
						.setInt(4, Integer.MAX_VALUE);
				rs = conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT]
						.executeQuery();
				synchronized (conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]) {
					try {
						conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
								.setString(1, tableName_);
						conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
								.setLong(2, dataLocator_);

						while (rs.next()) {
							s = rs.getString(1);
							conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
									.setInt(3, chunkNo);
							conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
									.setString(4, s);
							conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT]
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
				s = inputLob_.getSubString(pos, chunkSize_);
				if (s.length() == 0) {
					break;
				}
				ret = setString(pos, s);
                // Soln 10-171030-5322 : JDBC T4: SerialException returned while 
				// validating position for CLOB columns. 
                // Specifying a starting position that is greater than the available 
				// length is an error.
                if (s.length() < chunkSize_)
                  break;
                
				pos += s.length();
			}
		}
	}

	void prepareGetLobLenStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"prepareGetLobLenStmt", "", p);
		}

		conn_.prepareGetLobLenStmt(lobTableName_, false);
	}

	void prepareDelLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"prepareDelLobDataStmt", "", p);
		}
		conn_.prepareDelLobDataStmt(lobTableName_, false);
	}

	void prepareGetLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"prepareGetLobDataStmt", "", p);
		}
		conn_.prepareGetLobDataStmt(lobTableName_, false);
	}

	void prepareUpdLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"prepareUpdLobDataStmt", "", p);
		}
		conn_.prepareUpdLobDataStmt(lobTableName_, false);
	}

	void prepareInsLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"prepareInsLobDataStmt", "", p);
		}
		conn_.prepareInsLobDataStmt(lobTableName_, false);
	}

	void prepareTrunLobDataStmt() throws SQLException {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"prepareTrunLobDataStmt", "", p);
		}
		conn_.prepareTrunLobDataStmt(lobTableName_, false);
	}

	void prepareInsSpjLobDataStmt() throws SQLException {
		/* for future use */
	}

	void prepareInsSpjBaseDataStmt() throws SQLException {
		/* for future use */
	}

	PreparedStatement getSpjInsLobDataStmt() {
		/* for future use */
		return null;
	}

	PreparedStatement getSpjInsBaseDataStmt() {
		/* for future use */
		return null;
	}

	PreparedStatement getGetLobLenStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"getGetLobLenStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_LEN_STMT];
	}

	PreparedStatement getDelLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"getDelLobDataStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.CLOB_DEL_LOB_DATA_STMT];
	}

	PreparedStatement getTrunLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"getTrunLobDataStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.CLOB_TRUN_LOB_DATA_STMT];
	}

	PreparedStatement getInsLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"getInsLobDataStmt", "", p);
		}
		return conn_.LobPreparedStatements[conn_.CLOB_INS_LOB_DATA_STMT];
	}

	PreparedStatement getUpdLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"getUpdLobDataStmt", "", p);
		}

		return conn_.LobPreparedStatements[conn_.CLOB_UPD_LOB_DATA_STMT];
	}

	PreparedStatement getGetLobDataStmt() {
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_);
			conn_.props_.t4Logger_.logp(Level.FINER, "SQLMXClob",
					"getGetLobDataStmt", "", p);
		}

		return conn_.LobPreparedStatements[conn_.CLOB_GET_LOB_DATA_STMT];
	}

	// Constructors
	SQLMXClob(SQLMXConnection connection, String tableName, long dataLocator)
			throws SQLException {
		super(connection, tableName, dataLocator, connection.clobTableName_,
				false);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXClob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.clobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_clobTableName", null);
		}
	}

	SQLMXClob(SQLMXConnection connection, String tableName, long dataLocator,
			InputStream x, int length) throws SQLException {
		super(connection, tableName, dataLocator, x, length,
				connection.clobTableName_, false);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, x, length);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXClob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, x, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.clobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_clobTableName", null);
		}
	}

	SQLMXClob(SQLMXConnection connection, String tableName, long dataLocator,
			Reader x, int length) throws SQLException {
		super(connection, tableName, dataLocator, connection.clobTableName_,
				false);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, x, length);
			conn_.props_.t4Logger_
					.logp(
							Level.FINER,
							"SQLMXClob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, x, length);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.clobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_clobTableName", null);
		}
		ir_ = x;
		irLength_ = length;
	}

	SQLMXClob(SQLMXConnection connection, String tableName, long dataLocator,
			Clob inputLob) throws SQLException {
		super(connection, tableName, dataLocator, connection.clobTableName_,
				false);
		if (conn_.props_.t4Logger_.isLoggable(Level.FINE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, inputLob);
			conn_.props_.t4Logger_
					.logp(
							Level.FINE,
							"SQLMXClob",
							"",
							"Note, this constructor was called before the previous constructor",
							p);
		}
		if (conn_.props_.getLogWriter() != null) {
			LogRecord lr = new LogRecord(Level.FINE, "");
			Object p[] = T4LoggingUtilities.makeParams(conn_.props_,
					connection, tableName, dataLocator, inputLob);
			lr.setParameters(p);
			lr.setSourceClassName("SQLMXClob");
			lr.setSourceMethodName("");
			T4LogFormatter lf = new T4LogFormatter();
			String temp = lf.format(lr);
			conn_.props_.getLogWriter().println(temp);
		}
		if (connection.clobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_clobTableName", null);
		}
		inputLob_ = inputLob;
	}

	SQLMXClob(SQLMXConnection connection, String tableName, long dataLocator,
			String lobStr) throws SQLException {
		super(connection, tableName, dataLocator, connection.clobTableName_,
				false);
		if (connection.clobTableName_ == null) {
			throw SQLMXMessages.createSQLException(conn_.props_, conn_
					.getLocale(), "no_clobTableName", null);
		}
		inputLobStr_ = lobStr;
	}
	
	SQLMXClob(SQLMXConnection connection) throws SQLException {
	    super(connection, connection.clobTableName_, false);
	}


	public long length() throws SQLException {
		checkIfCurrent();
		if (isConfiguredLob()) {
			if (inputLobStr_ != null) {
				return inputLobStr_.length();
			} else if (outputStream_ != null) {
				inputLobStr_ = outputStream_.toString();
				return inputLobStr_.length();
			} else if (cwriter_ != null) {
				inputLobStr_ = cwriter_.toString();
				return inputLobStr_.length();
			} else
				return 0;
		} else
			return super.length();
	}


	public void free() throws SQLException {
		inputLob_ = null;
		inputLobStr_ = null;
	    super.free();
	}

	public void truncate(long len) throws SQLException {
		checkIfCurrent();
		if (isConfiguredLob()) {
			if (len < 0) {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.truncate(long)";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
						messageArguments);
			}
			if (inputLobStr_ != null) {
				if (inputLobStr_.length() >= len)
					inputLobStr_ = inputLobStr_.substring(0, (int) len);
				else {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = "Clob.truncate(long). Length should not exceed Clob size";
					throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
							messageArguments);
					}

			} else {
				Object[] messageArguments = new Object[1];
				messageArguments[0] = "Clob.truncate(long)";
				throw SQLMXMessages.createSQLException(conn_.props_, conn_.getLocale(), "invalid_input_value",
						messageArguments);
				}
		} else
			super.truncate(len);
	}

	// fields
	SQLMXClobReader reader_;

	SQLMXClobWriter writer_;
	
	CharArrayReader creader_;
	
	CharArrayWriter cwriter_;

	Reader ir_;

	int irLength_;

	Clob inputLob_;

	String inputLobStr_;
	
	ByteArrayOutputStream outputStream_;
	
	ByteArrayInputStream inputStream_;
	
	// JDBC 4.x stubs
	@Override
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		// TODO Auto-generated method stub
		SQLMXMessages.throwUnsupportedFeatureException(conn_.props_,
				conn_.getLocale(), "getCharacterStream(long pos, long length)");		
		
		return null;
	}
	
	@Override
	public synchronized void writerClosed(SQLMXMonitorWriter out, int pos) throws SQLException {
		if (out.size() != 0)
			setString(pos, out.toString());
	}

	@Override
	public synchronized void streamClosed(SQLMXMonitorOutputStream out, int pos) throws SQLException {
		if (out.size() != 0)
			setString(pos, out.toString());
	}

}
