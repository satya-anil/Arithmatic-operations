/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2003-2007, 2015-2016 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.math.BigDecimal;
import java.sql.DataTruncation;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

class InterfaceStatement {
	InterfaceConnection ic_;

	private long rowCount_;

	static final String CFG_CMD_TAG = "cfgcmd:"; // cfgcmd:<cmd>

	static final String SERVICEQS_CMD_TAG = "service:WMS:"; // service:WMS:<cmd>

	static final short SQL_DROP = 1;

	static short EXTERNAL_STMT = 0;

	int sqlStmtType_ = TRANSPORT.TYPE_UNKNOWN;

	int stmtType_ = 0;

	T4Statement t4statement_;

	int queryTimeout_;

	private String stmtLabel_;

	String cursorName_;

	SQLMXStatement stmt_;

	int sqlQueryType_;

	int stmtHandle_;

	int estimatedCost_;

	boolean prepare2 = false;

	boolean stmtIsLock = false; // 7708
	
	static boolean rowWiseData = false;//for executeArray R3.0

	// used for SPJ transaction
	static Class LmUtility_class_ = null;
	//Modified for soln 10-140409-1405	
	int outValuesFormat = 0;

	static java.lang.reflect.Method LmUtility_getTransactionId_ = null;
	PrepareReply pr_;

	// ----------------------------------------------------------------------
	InterfaceStatement(SQLMXStatement stmt) throws SQLException {
		this.ic_ = ((SQLMXConnection) stmt.getConnection()).getServerHandle();
		queryTimeout_ = stmt.queryTimeout_;
		stmtLabel_ = stmt.stmtLabel_;
		cursorName_ = stmt.cursorName_;
		t4statement_ = new T4Statement(this, stmt.connection_.props_
				.getCloseConnectionUponQueryTimeout());
		stmt_ = stmt;
	};

	public int getSqlQueryType() {
		return sqlQueryType_;
	}

	// wm_merge - AM
	private String convertDateFormat(String dt) {
		String tokens[] = dt.split("[/]", 3);

		if (tokens.length != 3) {
			return dt;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(tokens[0]).append("-").append(tokens[1]).append("-").append(
				tokens[2]);
		return sb.toString();
	}

	// ----------------------------------------------------------------------
	/**
	 * This method will take an object and convert it to the approperite format
	 * for sending to SQLMX.
	 * 
	 * @param locale
	 *            The locale for this operation
	 * @param pstmt
	 *            The prepared statement associated with the object
	 * @param paramValue
	 *            The object to convert
	 * @param paramNumber
	 *            The parameter number associated with this object
	 * @param values
	 *            The array to place the converted object into
	 */
	void convertObjectToSQL2(Locale locale, SQLMXStatement pstmt,
			Object paramValue, int paramRowCount, int paramNumber,
			byte[] values, int rowNumber) throws SQLException {
		byte[] tmpBarray = null;
		int i;
		BigDecimal tmpbd;

		int precision = pstmt.inputDesc_[paramNumber].precision_;
		int scale = pstmt.inputDesc_[paramNumber].scale_;
		int sqlDatetimeCode = pstmt.inputDesc_[paramNumber].sqlDatetimeCode_;
		int FSDataType = pstmt.inputDesc_[paramNumber].fsDataType_;
		int OdbcDataType = pstmt.inputDesc_[paramNumber].dataType_;
		int maxLength = pstmt.inputDesc_[paramNumber].sqlOctetLength_;
		int dataType = pstmt.inputDesc_[paramNumber].sqlDataType_;
		int dataCharSet = pstmt.inputDesc_[paramNumber].sqlCharset_;
		int dataLen;

		// setup the offsets
		int noNullValue = pstmt.inputDesc_[paramNumber].noNullValue_;
		int nullValue = pstmt.inputDesc_[paramNumber].nullValue_;
		int dataLength = pstmt.inputDesc_[paramNumber].maxLen_;

		if (dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR_WITH_LENGTH 
				|| dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH) { /* VARCHAR2 changes for SQL/MX3.5 */
			dataLength += 2;

			if (dataLength % 2 != 0)
				dataLength++;
		}
		
		if (dataType == InterfaceResultSet.SQLTYPECODE_VARBINARY_WITH_LENGTH) { /* VARCHAR2 changes for SQL/MX3.5 */
			dataLength += 2;

			if (dataLength % 2 != 0)
				dataLength++;
		}
		noNullValue = (noNullValue * paramRowCount) + (rowNumber * dataLength);

		if (nullValue != -1)
			nullValue = (nullValue * paramRowCount) + (rowNumber * 2);

		if (paramValue == null) {
			if (nullValue == -1) {
			//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"null_parameter_for_not_null_column", new Integer(
								paramNumber + 1));
			//end
			}

			// values[nullValue] = -1;
			Bytes.insertShort(values, nullValue, (short) -1, this.ic_
					.getByteSwap());
			return;
		}

		switch (dataType) {
		case InterfaceResultSet.SQLTYPECODE_CHAR:
			if (paramValue == null) {
				// Note for future optimization. We can probably remove the next
				// line,
				// because the array is already initialized to 0.
				Bytes.insertShort(values, noNullValue, (short) 0, this.ic_
						.getByteSwap());
			} else if (paramValue instanceof byte[]) {
				tmpBarray = (byte[]) paramValue;
			} else if (paramValue instanceof String) {
				String charSet = "";

				try {
					if (this.ic_.getISOMapping() == InterfaceUtilities.SQLCHARSETCODE_ISO88591
							&& !this.ic_.getEnforceISO()
							&& dataCharSet == InterfaceUtilities.SQLCHARSETCODE_ISO88591)
						charSet = ic_.t4props_.getISO88591();
					else
//						charSet = InterfaceUtilities
//								.getCharsetName(dataCharSet); //modified for R3.0						
						charSet =InterfaceUtilities.getEncodingTranslation(pstmt.connection_,dataCharSet);
		
					tmpBarray = ((String) paramValue).getBytes(charSet);
				} catch (Exception e) {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", charSet);
				}
			} // end if (paramValue instanceof String)
			else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_parameter_value",
						"CHAR data should be either bytes or String for column: "
								+ (paramNumber + 1));
			}

			//
			// We now have a byte array containing the parameter
			//

			dataLen = tmpBarray.length;
			if (maxLength >= dataLen) {
				System.arraycopy(tmpBarray, 0, values, noNullValue, dataLen);
				// Blank pad for rest of the buffer
				if (maxLength > dataLen) {
					if (dataCharSet == InterfaceUtilities.SQLCHARSETCODE_UNICODE) {
						// pad with Unicode spaces (0x00 0x32)
						int i2 = dataLen;
						while (i2 < maxLength) {
							values[noNullValue + i2] = (byte) 0;
							values[noNullValue + (i2 + 1)] = (byte) ' ';
							i2 = i2 + 2;
						}
					} else {
						Arrays.fill(values, (noNullValue + dataLen),
								(noNullValue + maxLength), (byte) ' ');
					}
				}
			} else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_string_parameter",
						"CHAR input data is longer than the length for column: "
								+ (paramNumber + 1));
			}

			break;
		case InterfaceResultSet.SQLTYPECODE_VARCHAR:
			if (paramValue instanceof byte[]) {
				tmpBarray = (byte[]) paramValue;
			} else if (paramValue instanceof String) {
				String charSet = "";

				try {
					if (this.ic_.getISOMapping() == InterfaceUtilities.SQLCHARSETCODE_ISO88591
							&& !this.ic_.getEnforceISO()
							&& dataCharSet == InterfaceUtilities.SQLCHARSETCODE_ISO88591)
						charSet = ic_.t4props_.getISO88591();
					else
//						charSet = InterfaceUtilities
//						.getCharsetName(dataCharSet); //modified for R3.0						
				charSet =InterfaceUtilities.getEncodingTranslation(pstmt.connection_,dataCharSet);

					tmpBarray = ((String) paramValue).getBytes(charSet);
				} catch (Exception e) {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", charSet);
				}

			} // end if (paramValue instanceof String)
			else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_parameter_value",
						"VARCHAR data should be either bytes or String for column: "
								+ (paramNumber + 1));
			}

			dataLen = tmpBarray.length;
			if (maxLength > dataLen) {
				Bytes.insertShort(values, noNullValue, (short) dataLen,
						this.ic_.getByteSwap());
				System
						.arraycopy(tmpBarray, 0, values, noNullValue + 2,
								dataLen);
			} else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_parameter_value",
						"VARCHAR input data is longer than the length for column: "
								+ (paramNumber + 1));
			}
			break;
		case InterfaceResultSet.SQLTYPECODE_DATETIME:
			Date tmpdate;
			switch (sqlDatetimeCode) {
			case InterfaceResultSet.SQLDTCODE_DATE:
				try {
					tmpdate = Date.valueOf((String) paramValue);
				} catch (IllegalArgumentException iex) {
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_parameter_value",
							"Date data format is incorrect for column: "
									+ (paramNumber + 1) + " = " + paramValue);
				}
				try {
					byte[] temp1 = tmpdate.toString().getBytes("ASCII");
					System.arraycopy(temp1, 0, values, noNullValue,
							temp1.length);
				} catch (java.io.UnsupportedEncodingException e) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = e.getMessage();
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", messageArguments);
				}
				break;
			case InterfaceResultSet.SQLDTCODE_TIMESTAMP:
			case InterfaceResultSet.SQLDTCODE_DC_DATETIME: /*DC DATE changes for SQL/MX3.5 */
				Timestamp tmpts;
				try {
					tmpts = Timestamp.valueOf((String) paramValue);
				} catch (IllegalArgumentException iex) {
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_parameter_value",
							"Timestamp data format is incorrect for column: "
									+ (paramNumber + 1) + " = " + paramValue);
				}

				// ODBC precision is nano secs. JDBC precision is micro secs
				// so substract 3 from ODBC precision.
				maxLength = maxLength - 3;
				try {
					tmpBarray = tmpts.toString().getBytes("ASCII");
				} catch (java.io.UnsupportedEncodingException e) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = e.getMessage();
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", messageArguments);
				}
				dataLen = tmpBarray.length;

				if (maxLength > dataLen) {
					System
							.arraycopy(tmpBarray, 0, values, noNullValue,
									dataLen);

					// Don't know when we need this. padding blanks. Legacy??
					Arrays.fill(values, (noNullValue + dataLen),
							(noNullValue + maxLength), (byte) ' ');
				} else {
					System.arraycopy(tmpBarray, 0, values, noNullValue,
							maxLength);
				}
				break;
			case InterfaceResultSet.SQLDTCODE_TIME:

				// case SQLMXDesc.SQLDTCODE_HOUR_TO_FRACTION: // --> Nonstop SQL/MX
				// Database maps it to TIME(6)
				// --> NCS maps it to TIMESTAMP(6)
				// If the OdbcDataType is equal to Types.Other, that means
				// that this is HOUR_TO_FRACTION and should be treated
				// as a Type.Other --> see in SQLDesc.java
				if (OdbcDataType != java.sql.Types.OTHER) // do the processing
				// for TIME
				{
					Time tmptime;
					try {
						if (paramValue instanceof byte[]) {
							tmptime = Time.valueOf(new String(
									(byte[]) paramValue, "ASCII"));
						} else {
							tmptime = Time.valueOf(paramValue.toString());
						}
						byte[] tempb1 = tmptime.toString().getBytes("ASCII");
						System.arraycopy(tempb1, 0, values, noNullValue,
								tempb1.length);
					} catch (IllegalArgumentException iex) {
						//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"invalid_parameter_value",
								"Time data format is incorrect for column: "
										+ (paramNumber + 1) + " = " + paramValue);
					} catch (java.io.UnsupportedEncodingException e) {
						Object[] messageArguments = new Object[1];
						messageArguments[0] = e.getMessage();
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"unsupported_encoding", messageArguments);
					}
					break; // KAS - This is a very confussing use of CASE. This
					// should be re-written.
				} else {
					// SQLMXDesc.SQLDTCODE_HOUR_TO_FRACTION data type!!!
					// let the next case structure handle it
				}
			case SQLMXDesc.SQLDTCODE_YEAR:
			case SQLMXDesc.SQLDTCODE_YEAR_TO_MONTH:
			case SQLMXDesc.SQLDTCODE_MONTH:
			case SQLMXDesc.SQLDTCODE_MONTH_TO_DAY:
			case SQLMXDesc.SQLDTCODE_DAY:
			case SQLMXDesc.SQLDTCODE_HOUR:
			case SQLMXDesc.SQLDTCODE_HOUR_TO_MINUTE:
			case SQLMXDesc.SQLDTCODE_MINUTE:
			case SQLMXDesc.SQLDTCODE_MINUTE_TO_SECOND:
				// case SQLMXDesc.SQLDTCODE_MINUTE_TO_FRACTION:
			case SQLMXDesc.SQLDTCODE_SECOND:
				// case SQLMXDesc.SQLDTCODE_SECOND_TO_FRACTION:
			case SQLMXDesc.SQLDTCODE_YEAR_TO_HOUR:
			case SQLMXDesc.SQLDTCODE_YEAR_TO_MINUTE:
			case SQLMXDesc.SQLDTCODE_MONTH_TO_HOUR:
			case SQLMXDesc.SQLDTCODE_MONTH_TO_MINUTE:
			case SQLMXDesc.SQLDTCODE_MONTH_TO_SECOND:
				// case SQLMXDesc.SQLDTCODE_MONTH_TO_FRACTION:
			case SQLMXDesc.SQLDTCODE_DAY_TO_HOUR:
			case SQLMXDesc.SQLDTCODE_DAY_TO_MINUTE:
			case SQLMXDesc.SQLDTCODE_DAY_TO_SECOND:
				// case SQLMXDesc.SQLDTCODE_DAY_TO_FRACTION:
			default:
				if (paramValue instanceof String) {
					try {
						tmpBarray = ((String) paramValue).getBytes("ASCII");
					} catch (Exception e) {
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"unsupported_encoding", "ASCII");
					}
				} else if (paramValue instanceof byte[]) {
					tmpBarray = (byte[]) paramValue;
				} else {
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_cast_specification",
							"DATETIME data should be either bytes or String for column: "
									+ (paramNumber + 1));
				}
				dataLen = tmpBarray.length;
				if (maxLength == dataLen) {
					System.arraycopy(tmpBarray, 0, values, noNullValue,
							maxLength);
				} else if (maxLength > dataLen) {
					System
							.arraycopy(tmpBarray, 0, values, noNullValue,
									dataLen);

					// Don't know when we need this. padding blanks. Legacy??
					Arrays.fill(values, (noNullValue + dataLen),
							(noNullValue + maxLength), (byte) ' ');
				} else {
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_parameter_value",
							"DATETIME data longer than column length: "
									+ (paramNumber + 1));
				}
				break;
			}
			break;
		case InterfaceResultSet.SQLTYPECODE_INTERVAL:
			if (paramValue instanceof byte[]) {
				tmpBarray = (byte[]) paramValue;
			} else if (paramValue instanceof String) {
				try {
					tmpBarray = ((String) paramValue).getBytes("ASCII");
				} catch (Exception e) {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", "ASCII");
				}
			} else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_cast_specification",
						"INTERVAL data should be either bytes or String for column: "
								+ (paramNumber + 1));
			}

			dataLen = tmpBarray.length;
			if (maxLength >= dataLen) {
				dataLen = tmpBarray.length;
				if (maxLength == dataLen) {
					System.arraycopy(tmpBarray, 0, values, noNullValue,
							maxLength);
				} else if (maxLength > dataLen) {
					System
							.arraycopy(tmpBarray, 0, values, noNullValue,
									dataLen);

					// Don't know when we need this. padding blanks. Legacy??
					Arrays.fill(values, (noNullValue + dataLen),
							(noNullValue + maxLength), (byte) ' ');
				}
			} else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_parameter_value",
						"INTERVAL data longer than column length: "
								+ (paramNumber + 1));
			}

			break;
		case InterfaceResultSet.SQLTYPECODE_VARCHAR_WITH_LENGTH:
		case InterfaceResultSet.SQLTYPECODE_VARCHAR_LONG:
		case InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH: /* VARCHAR2 changes for SQL/MX3.5 */
			if (paramValue instanceof byte[]) {
				tmpBarray = (byte[]) paramValue;
			} else if (paramValue instanceof String) {
				String charSet = "";

				try {
					if (this.ic_.getISOMapping() == InterfaceUtilities.SQLCHARSETCODE_ISO88591
							&& !this.ic_.getEnforceISO()
							&& dataCharSet == InterfaceUtilities.SQLCHARSETCODE_ISO88591)
						charSet = ic_.t4props_.getISO88591();
					else
//						charSet = InterfaceUtilities
//						.getCharsetName(dataCharSet); //modified for R3.0						
				charSet =InterfaceUtilities.getEncodingTranslation(pstmt.connection_,dataCharSet);

					tmpBarray = ((String) paramValue).getBytes(charSet);
				} catch (Exception e) {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", charSet);
				}
			} // end if (paramValue instanceof String)
			else {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				if (dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH) { /* VARCHAR2 changes for SQL/MX3.5 */
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_cast_specification",
							"VARCHAR2 data should be either bytes or String for column: "
									+ (paramNumber + 1));
				} else {

					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_cast_specification",
							"VARCHAR data should be either bytes or String for column: "
									+ (paramNumber + 1));

				}
			}

			dataLen = tmpBarray.length;
			if (maxLength > (dataLen + 2)) { // size of short is 2
				maxLength = dataLen + 2;

				// TODO: should this be swapped?!
				System.arraycopy(Bytes.createShortBytes((short) dataLen,
						this.ic_.getByteSwap()), 0, values, noNullValue, 2);
				System.arraycopy(tmpBarray, 0, values, (noNullValue + 2),
						dataLen);
			} else {
				// start -- solution 10-130315-6605 Behavior of
				// executeBatchWithRowsAffected property in the T4 driver failed
				/*
				 * VARCHAR2 changes for SQL / MX3 .5
				 */
				if (dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH) {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_string_parameter",
							"VARCHAR2 data longer than column length: "
									+ (paramNumber + 1));
				} else {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_string_parameter",
							"VARCHAR data longer than column length: "
									+ (paramNumber + 1));
				}
					
				
			}
			break;
		case InterfaceResultSet.SQLTYPECODE_BINARY:	/*BINARY data type for SQL/MX 3.6*/
			{
				if (paramValue == null) {
					// Note for future optimization. We can probably remove 
					// the next line,because the array is already initialized to 0.
					Bytes.insertShort(values, noNullValue, (short) 0, this.ic_
							.getByteSwap());
				} else if (paramValue instanceof byte[]) {
					tmpBarray = (byte[]) paramValue;
				} else if (paramValue instanceof String) {
					String charSet = "";

					try {
						if (this.ic_.getISOMapping() == InterfaceUtilities.SQLCHARSETCODE_ISO88591
								&& !this.ic_.getEnforceISO()
								&& dataCharSet == InterfaceUtilities.SQLCHARSETCODE_ISO88591)
							charSet = ic_.t4props_.getISO88591();
						else
							charSet =InterfaceUtilities.getEncodingTranslation(pstmt.connection_,dataCharSet);
		
						tmpBarray = ((String) paramValue).getBytes(charSet);
					} catch (Exception e) {
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"unsupported_encoding", charSet);
					}
				} // end if (paramValue instanceof String)
				else {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_parameter_value",
							"BINARY data should be either bytes or String for column: "
									+ (paramNumber + 1));
				}

				//
				// We now have a byte array containing the parameter
				//
				dataLen = tmpBarray.length;
				if (maxLength >= dataLen) {
					System.arraycopy(tmpBarray, 0, values, noNullValue, dataLen);
				} else {
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"invalid_string_parameter",
							"BINARY input data is longer than the length for column: "
									+ (paramNumber + 1));
				}						
			}
			break;/*End of case SQLTYPECODE_BINARY*/
		case InterfaceResultSet.SQLTYPECODE_VARBINARY_WITH_LENGTH: /*VARBINARY data type for SQL/MX 3.6*/
			{
				if (paramValue instanceof byte[]) {
					tmpBarray = (byte[]) paramValue;
				} else if (paramValue instanceof String) {
					String charSet = "";

					try {
						if (this.ic_.getISOMapping() == InterfaceUtilities.SQLCHARSETCODE_ISO88591
								&& !this.ic_.getEnforceISO()
								&& dataCharSet == InterfaceUtilities.SQLCHARSETCODE_ISO88591)
							charSet = ic_.t4props_.getISO88591();
						else
//							charSet = InterfaceUtilities
//							.getCharsetName(dataCharSet); //modified for R3.0						
					charSet =InterfaceUtilities.getEncodingTranslation(pstmt.connection_,dataCharSet);

						tmpBarray = ((String) paramValue).getBytes(charSet);
					} catch (Exception e) {
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"unsupported_encoding", charSet);
					}
				} // end if (paramValue instanceof String)
				else {
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					if (dataType == InterfaceResultSet.SQLTYPECODE_BINARY) { /* VARCHAR2 changes for SQL/MX3.5 */
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"invalid_cast_specification",
								"BINARY data should be either bytes or String for column: "
										+ (paramNumber + 1));
					} else if (dataType == InterfaceResultSet.SQLTYPECODE_VARBINARY_WITH_LENGTH) {

						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"invalid_cast_specification",
								"VARBINARY data should be either bytes or String for column: "
										+ (paramNumber + 1));

					}
				}

				dataLen = tmpBarray.length;
				if (maxLength > (dataLen + 2)) { // size of short is 2
					maxLength = dataLen + 2;

					// TODO: should this be swapped?!
					System.arraycopy(Bytes.createShortBytes((short) dataLen,
							this.ic_.getByteSwap()), 0, values, noNullValue, 2);
					System.arraycopy(tmpBarray, 0, values, (noNullValue + 2),
							dataLen);
				} else {
					// start -- solution 10-130315-6605 Behavior of
					// executeBatchWithRowsAffected property in the T4 driver failed
					/*
					 * VARCHAR2 changes for SQL / MX3 .5
					 */
					if (dataType == InterfaceResultSet.SQLTYPECODE_BINARY) {
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"invalid_string_parameter",
								"BINARY data longer than column length: "
										+ (paramNumber + 1));
					} else if (dataType == InterfaceResultSet.SQLTYPECODE_VARBINARY_WITH_LENGTH) {
						throw SQLMXMessages.createSQLException(
								pstmt.connection_.props_, locale,
								"invalid_string_parameter",
								"VARBINARY data longer than column length: "
										+ (paramNumber + 1));
					}	
				}
			}
			break;/*End of case SQLTYPECODE_VARBINARY*/
		case InterfaceResultSet.SQLTYPECODE_INTEGER:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			if (scale > 0) {
				tmpbd = tmpbd.movePointRight(scale);
			}

			// data truncation check
			if (pstmt.roundingMode_ == BigDecimal.ROUND_UNNECESSARY) {
				Utility.checkLongTruncation(paramNumber, tmpbd);

			}
			Utility.checkIntegerBoundary(locale, tmpbd);

			// check boundary condition for Numeric.
			Utility.checkDecimalBoundary(locale, tmpbd, precision);
			Bytes.insertInt(values, noNullValue, tmpbd.intValue(), this.ic_
					.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_INTEGER_UNSIGNED:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			if (scale > 0) {
				tmpbd = tmpbd.movePointRight(scale);
			}

			// data truncation check
			if (pstmt.roundingMode_ == BigDecimal.ROUND_UNNECESSARY) {
				Utility.checkLongTruncation(paramNumber, tmpbd);

				// range checking
			}
			Utility.checkUnsignedIntegerBoundary(locale, tmpbd);

			// check boundary condition for Numeric.
			Utility.checkDecimalBoundary(locale, tmpbd, precision);
			Bytes.insertInt(values, noNullValue, tmpbd.intValue(), this.ic_
					.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_SMALLINT:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			if (scale > 0) {
				tmpbd = tmpbd.movePointRight(scale);
			}

			// data truncation check
			if (pstmt.roundingMode_ == BigDecimal.ROUND_UNNECESSARY) {
				Utility.checkLongTruncation(paramNumber, tmpbd);

				// range checking
			}
			Utility.checkShortBoundary(locale, tmpbd);

			// check boundary condition for Numeric.
			Utility.checkDecimalBoundary(locale, tmpbd, precision);
			Bytes.insertShort(values, noNullValue, tmpbd.shortValue(), this.ic_
					.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_SMALLINT_UNSIGNED:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			if (scale > 0) {
				tmpbd = tmpbd.movePointRight(scale);
			}

			// data truncation check
			if (pstmt.roundingMode_ == BigDecimal.ROUND_UNNECESSARY) {
				Utility.checkLongTruncation(paramNumber, tmpbd);

				// range checking
			}
			Utility.checkSignedShortBoundary(locale, tmpbd);

			// check boundary condition for Numeric.
			Utility.checkDecimalBoundary(locale, tmpbd, precision);
			Bytes.insertShort(values, noNullValue, tmpbd.shortValue(), this.ic_
					.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_LARGEINT:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			if (scale > 0) {
				tmpbd = tmpbd.movePointRight(scale);

				// check boundary condition for Numeric.
			}
			Utility.checkDecimalBoundary(locale, tmpbd, precision);
			Bytes.insertLong(values, noNullValue, tmpbd.longValue(), this.ic_
					.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_DECIMAL:
		case InterfaceResultSet.SQLTYPECODE_DECIMAL_UNSIGNED:

			// create an parameter with out "."
			try {
				tmpbd = Utility.getBigDecimalValue(locale, paramValue);
				if (scale > 0) {
					tmpbd = tmpbd.movePointRight(scale);

				}
				tmpbd = Utility.setScale(tmpbd, scale, pstmt.roundingMode_);

				// data truncation check.
				if (pstmt.roundingMode_ == BigDecimal.ROUND_UNNECESSARY) {
					Utility.checkLongTruncation(paramNumber, tmpbd);

					// get only the mantissa part
				}
				try {
					tmpBarray = String.valueOf(tmpbd.longValue()).getBytes(
							"ASCII");
				} catch (java.io.UnsupportedEncodingException e) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = e.getMessage();
					throw SQLMXMessages.createSQLException(
							pstmt.connection_.props_, locale,
							"unsupported_encoding", messageArguments);
				}
			} catch (NumberFormatException nex) {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw SQLMXMessages.createSQLException(
						pstmt.connection_.props_, locale,
						"invalid_parameter_value",
						"DECIMAL data format incorrect for column: "
								+ (paramNumber + 1) + ". Error is: "
								+ nex.getMessage());
			}

			dataLen = tmpBarray.length;

			// pad leading zero's if datalen < maxLength
			int desPos = 0;
			int srcPos = 0;
			boolean minus = false;

			// check if data is negative.
			if (tmpbd.signum() == -1) {
				minus = true;
				srcPos++;
				dataLen--;
			}

			// pad beginning 0 for empty space.
			int numOfZeros = maxLength - dataLen;

			// DataTruncation is happening.
			if (numOfZeros < 0) {
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				throw new DataTruncation((paramNumber + 1), true, false, maxLength,
						dataLen);
			}

			for (i = 0; i < numOfZeros; i++) {
				values[noNullValue + desPos] = (byte) '0';
				desPos = desPos + 1;
			}
			System.arraycopy(tmpBarray, srcPos, values, noNullValue + desPos,
					dataLen);

			// handling minus sign in decimal. OR -80 with the first byte for
			// minus
			if (minus) {
				values[noNullValue] = (byte) ((byte) (-80) | values[noNullValue]);
			}
			break;
		case InterfaceResultSet.SQLTYPECODE_REAL:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			Utility.checkFloatBoundary(locale, tmpbd);
			float fvalue = tmpbd.floatValue();
			int bits = Float.floatToIntBits(fvalue);

			Bytes.insertInt(values, noNullValue, bits, this.ic_.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_FLOAT:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			Utility.checkFloatBoundary(locale, tmpbd);
			Bytes.insertLong(values, noNullValue, Double.doubleToLongBits(tmpbd
					.doubleValue()), this.ic_.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_DOUBLE:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			Utility.checkDoubleBoundary(locale, tmpbd);
			Bytes.insertLong(values, noNullValue, Double.doubleToLongBits(tmpbd
					.doubleValue()), this.ic_.getByteSwap());
			break;
		case InterfaceResultSet.SQLTYPECODE_NUMERIC:
		case InterfaceResultSet.SQLTYPECODE_NUMERIC_UNSIGNED:
			tmpbd = Utility.getBigDecimalValue(locale, paramValue);
			byte[] b = InterfaceUtilities.convertBigDecimalToSQLBigNum(tmpbd,
					maxLength, scale);
			System.arraycopy(b, 0, values, noNullValue, maxLength);
			break;
		// You will not get this type, since server internally converts it
		// SMALLINT, INTERGER or LARGEINT
		case InterfaceResultSet.SQLTYPECODE_DECIMAL_LARGE:
		case InterfaceResultSet.SQLTYPECODE_DECIMAL_LARGE_UNSIGNED:
		case InterfaceResultSet.SQLTYPECODE_BIT:
		case InterfaceResultSet.SQLTYPECODE_BITVAR:
		case InterfaceResultSet.SQLTYPECODE_BPINT_UNSIGNED:
		default:
			if (ic_.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(
						stmt_.connection_.props_, locale, pstmt, paramValue,
						paramNumber);
				String temp = "Restricted_Datatype_Error";
				ic_.t4props_.t4Logger_.logp(Level.FINEST, "InterfaceStatement",
						"convertObjectToSQL2", temp, p);
			}

			throw SQLMXMessages.createSQLException(pstmt.connection_.props_,
					locale, "restricted_data_type", null);
		}
		if (ic_.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(
					stmt_.connection_.props_, locale, pstmt, paramValue,
					paramNumber);
			String temp = "datatype = " + dataType;
			ic_.t4props_.t4Logger_.logp(Level.FINEST, "InterfaceStatement",
					"convertObjectToSQL2", temp, p);
		}

	} // end convertObjectToSQL2

	private SQLWarningOrError[] mergeErrors(SQLWarningOrError[] client,
			SQLWarningOrError[] server) {
		SQLWarningOrError[] target = new SQLWarningOrError[client.length
				+ server.length];

		int si = 0; // server index
		int ci = 0; // client index
		int ti = 0; // target index

		int sr; // server rowId
		int cr; // client rowId

		int so = 0; // server offset

		while (ci < client.length && si < server.length) {
			cr = client[ci].rowId;
			sr = server[si].rowId + so;

			if (cr <= sr || server[si].rowId == 0) {
				so++;
				target[ti++] = client[ci++];
			} else {
				server[si].rowId += so;
				target[ti++] = server[si++];
			}
		}

		// we only have one array left
		while (ci < client.length) {
			target[ti++] = client[ci++];
		}

		while (si < server.length) {
			if (server[si].rowId != 0)
				server[si].rowId += so;
			target[ti++] = server[si++];
		}

		return target;
	}

	SQL_DataValue_def fillInSQLValues2(Locale locale, SQLMXStatement stmt,
			int paramRowCount, int paramCount, Object[] paramValues,
			ArrayList clientErrors) throws SQLException

	{
		SQL_DataValue_def dataValue = new SQL_DataValue_def();

		if (paramRowCount == 0 && paramValues != null && paramValues.length > 0)
			paramRowCount = 1; // fake a single row if we are doing inputParams
		// for an SPJ

		// TODO: we should really figure out WHY this could happen
		if (stmt.inputParamsLength_ < 0) {
			dataValue.buffer = new byte[0];
			dataValue.length = 0;
		} else {
			int bufLen = stmt.inputParamsLength_ * paramRowCount;

			dataValue.buffer = new byte[bufLen];
			//start -- soln 10-130419-7074 R3.2.1:GWP:Execute Batch inserts wrong data incase of any SQL fails
			byte[] tempBuffer = new byte[bufLen];
			//end 
			for (int row = 0; row < paramRowCount; row++) {
				//start -- soln 10-130419-7074 R3.2.1:GWP:Execute Batch inserts wrong data incase of any SQL fails
				System.arraycopy(dataValue.buffer, 0, tempBuffer, 0, bufLen);
				//end
				for (int col = 0; col < paramCount; col++) {
					try {
						convertObjectToSQL2(locale, stmt, paramValues[row
								* paramCount + col], paramRowCount, col,
								dataValue.buffer, row - clientErrors.size());
					} catch (SQLMXException e) {
						//start -- soln 10-130419-7074 R3.2.1:GWP:Execute Batch inserts wrong data incase of any SQL fails
						if (paramRowCount == 1 || rowWiseData == true) // for single rows we need to
						//end
							// throw immediately
							throw e;
						//start -- soln 10-130419-7074 R3.2.1:GWP:Execute Batch inserts wrong data incase of any SQL fails
						System.arraycopy(tempBuffer, 0, dataValue.buffer, 0, bufLen);
						//end
						clientErrors.add(new SQLWarningOrError(row + 1, e
								.getErrorCode(), e.getMessage(), e
								.getSQLState()));
						break; // skip the rest of the row
					}
				}
			}
			if(rowWiseData == true){
			//Seema-start-- added this to get the pointers for the parameters and then to create a new byte array with row-wise data
			int[] paramPoint=new int[paramCount * paramRowCount];
			int count=0;
			int noNullValue_;
			int nullValue_;
			int dataLength_;
			int dataType_;
			for(int i=0;i<paramCount;i++){
				for(int j=0;j<paramRowCount;j++){
					 noNullValue_ = stmt.inputDesc_[i].noNullValue_;
					 nullValue_ = stmt.inputDesc_[i].nullValue_;
					 dataLength_ = stmt.inputDesc_[i].maxLen_;
					 dataType_ = stmt.inputDesc_[i].sqlDataType_;

					if (dataType_ == InterfaceResultSet.SQLTYPECODE_VARCHAR_WITH_LENGTH
							|| dataType_ == InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH ) { /* VARCHAR2 changes for SQL/MX3.5 */
						dataLength_ += 2;

						if (dataLength_ % 2 != 0)
							dataLength_++;
					}

					noNullValue_ = (noNullValue_ * paramRowCount) + (j * dataLength_);
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					if (nullValue_ != -1)
						nullValue_ = (nullValue_ * paramRowCount) + (j * 2);
					if(paramValues[j * paramCount + i] == null)
					{
						paramPoint[count++]=nullValue_;
					}
					else						
						paramPoint[count++]=noNullValue_;
					//end
				}
			}
			byte[] newByteArray = new byte[bufLen] ;
			int destPos=0;
			int noOfBytesToCopy = 0;
			int ptr= 0;
			int noOfBytesToZero=0;
			
//			for (int j=0; j< paramRowCount; j++){
//				ptr= j;
//			for(int i=0; i<paramCount; i++) {
//				if (j==(paramRowCount-1)&& i==(paramCount-1))
//				{
//					noOfBytesToCopy = bufLen - paramPoint[ptr];
//					System.arraycopy(dataValue.buffer, paramPoint[ptr], newByteArray, destPos, noOfBytesToCopy);
//				}
//				else {
//				noOfBytesToCopy =paramPoint[ptr+1]-paramPoint[ptr];
//				System.arraycopy(dataValue.buffer, paramPoint[ptr], newByteArray, destPos, noOfBytesToCopy);
//				ptr = ptr+paramRowCount ;
//				destPos = destPos+noOfBytesToCopy;
//				}
//			}
//			
//			
//			}    
			
			for (int j=0; j< paramRowCount; j++){
				ptr= j;
			for(int i=0; i<paramCount; i++) {
				
				//calcualate the actual size of this column
				noOfBytesToCopy =stmt.inputDesc_[i].maxLen_;
				if(stmt.inputDesc_[i].sqlDataType_== InterfaceResultSet.SQLTYPECODE_VARCHAR_WITH_LENGTH
						    || stmt.inputDesc_[i].sqlDataType_ == InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH /* VARCHAR2 changes for SQL/MX3.5 */
							|| stmt.inputDesc_[i].sqlDataType_ == InterfaceResultSet.SQLTYPECODE_VARCHAR_LONG
							|| stmt.inputDesc_[i].sqlDataType_ == InterfaceResultSet.SQLTYPECODE_VARCHAR){
					noOfBytesToCopy += 2;
				if (noOfBytesToCopy % 2 !=0)
					noOfBytesToCopy++; }
				//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
				if(paramValues[j * paramCount + i] == null)
				{
					noOfBytesToCopy = 2;
				}
				//end -- solution 10-130315-6605 
				//Now calculate the no of bytes that should be zeroed. 
				if(i == (paramCount-1)){
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					if(paramValues[j * paramCount + i] == null)
					{
						
						noOfBytesToZero = (stmt.inputDesc_[i].nullValue_ - stmt.inputDesc_[i].noNullValue_);
						if(noOfBytesToZero != 0){
							Arrays.fill(newByteArray, destPos, destPos+noOfBytesToZero, (byte) '\0');
							destPos = destPos + noOfBytesToZero;
						}
						noOfBytesToZero = (stmt.inputParamsLength_- stmt.inputDesc_[i].nullValue_) - noOfBytesToCopy;
					}
					else
					{
					//end -- solution 10-130315-6605 
						noOfBytesToZero = (stmt.inputParamsLength_- stmt.inputDesc_[i].noNullValue_) - noOfBytesToCopy;
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					}
					//end -- solution 10-130315-6605 
				}
				else {
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					if(paramValues[j * paramCount + i] == null)
					{
						noOfBytesToZero = (stmt.inputDesc_[i].nullValue_ - stmt.inputDesc_[i].noNullValue_);
						if(noOfBytesToZero != 0){
							Arrays.fill(newByteArray, destPos, destPos+noOfBytesToZero, (byte) '\0');
							destPos = destPos + noOfBytesToZero;
						}
						noOfBytesToZero = (stmt.inputDesc_[i+1].noNullValue_ - stmt.inputDesc_[i].nullValue_)- noOfBytesToCopy;
					}
					else
					{
					//end -- solution 10-130315-6605 
						noOfBytesToZero = (stmt.inputDesc_[i+1].noNullValue_ - stmt.inputDesc_[i].noNullValue_)- noOfBytesToCopy;
					//start -- solution 10-130315-6605 Behavior of executeBatchWithRowsAffected property in the T4 driver failed
					}
					//end -- solution 10-130315-6605 
				}
				
				//Copy the data first
				System.arraycopy(dataValue.buffer, paramPoint[ptr], newByteArray, destPos, noOfBytesToCopy);
				destPos = destPos+noOfBytesToCopy;
				
				//Now pad zeroes only if required
				if(noOfBytesToZero != 0){
				Arrays.fill(newByteArray, destPos, destPos+noOfBytesToZero, (byte) '\0');
				destPos = destPos + noOfBytesToZero;
				}
				
				ptr = ptr+paramRowCount ;
					
			}
			
			}		   
			
			System.arraycopy(newByteArray, 0, dataValue.buffer, 0, bufLen);
			// Seema-changes end---
			}
			
			 
			// fix the column offsets if we had errors
			if (clientErrors.size() > 0) {
				int oldOffset;
				int newOffset;
				int noNullValue;
				int nullValue;
				int colLength;
				int dataType;

				//start -- soln 10-130419-7074 R3.2.1:GWP:Execute Batch inserts wrong data incase of any SQL fails
				for (int i = 0; i < paramCount; i++) // skip the first col
				//end
				{
					noNullValue = stmt.inputDesc_[i].noNullValue_;
					nullValue = stmt.inputDesc_[i].nullValue_;
					colLength = stmt.inputDesc_[i].maxLen_;
					dataType = stmt.inputDesc_[i].dataType_;
					if (dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR_WITH_LENGTH
							|| dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR2_WITH_LENGTH /* VARCHAR2 changes for SQL/MX3.5 */
							|| dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR_LONG
							|| dataType == InterfaceResultSet.SQLTYPECODE_VARCHAR) {
						colLength += 2;

						if (colLength % 2 != 0)
							colLength++;
					}
					//start -- soln 10-130419-7074 R3.2.1:GWP:Execute Batch inserts wrong data incase of any SQL fails
					oldOffset = noNullValue * paramRowCount;
					newOffset = oldOffset - (noNullValue * clientErrors.size());
					System.arraycopy(dataValue.buffer, oldOffset,
							dataValue.buffer, newOffset, colLength
									* (paramRowCount - clientErrors.size()));

					if (nullValue != -1) {
						oldOffset = nullValue * paramRowCount;
						newOffset = oldOffset
								- (nullValue * clientErrors.size());
						System.arraycopy(dataValue.buffer, oldOffset,
								dataValue.buffer, newOffset,
								2 * (paramRowCount - clientErrors.size()));
					}
					//end
				}
			}

			dataValue.length = stmt.inputParamsLength_
					* (paramRowCount - clientErrors.size());
		}
		return dataValue;
	}

	boolean hasParameters(String sql) {
		boolean foundParam = false;

		String[] s = sql.split("\"[^\"]*\"|'[^']*'");
		for (int i = 0; i < s.length; i++) {
			if (s[i].indexOf('?') != -1) {
				foundParam = true;
				break;
			}
		}

		return foundParam;
	}

	// -------------------------------------------------------------
	short getSqlStmtType(String str) {
		
		stmtIsLock = false;
		//Solution 10-141031-4299 -- start
		
		str=str.trim();
		
		while (str.startsWith("/*")) {
			int endIndex = 0;
			endIndex = str.indexOf("*/");
			if(endIndex > 0){
			str = str.substring(endIndex + 2);
			str=str.trim();
			}
			else
				break;			

		}
		
		
		//Added for solution 10-170620-4198 
		int startIndex = 0;
		int stopIndex = 0;
		int len = str.length();
		
		for (int i = 0; i < len; i++) {
			char find = str.charAt(i);
			if (Character.isLetter(find) == true) {
				startIndex = i;
				break;
			}
		}
		
		for (int i = startIndex + 1; i < len; i++) {
			char find = str.charAt(i);
			if (Character.isLetter(find) == false) {
				stopIndex = i;
				break;
			}
			else if((i + 1) == len) {//Added for solution 10-190301-9539 
				stopIndex = len;
				break;
			}
		}		

		
		short rt1 = TRANSPORT.TYPE_UNKNOWN;
		String str3 = "";
		str3 = str.substring(startIndex, stopIndex).toUpperCase();

		if ((str3.equals("SELECT")) || (str3.equals("SHOWSHAPE"))
				|| (str3.equals("INVOKE")) || (str3.equals("SHOWCONTROL"))
				|| (str3.equals("SHOWDDL")) || (str3.equals("EXPLAIN"))
				|| (str3.equals("SHOWPLAN")) || (str3.equals("REORGANIZE"))
				|| (str3.equals("MAINTAIN"))
				|| (str3.equals("SHOWLABEL"))
				|| (str3.equals("VALUES"))
				||
				// (str3.equals("PURGEDATA")) ||
				(str3.equals("REORG")) || (str3.equals("SEL"))
				|| (str3.equals("GET")) || (str3.equals("SHOWSTATS"))
				|| (str3.equals("GIVE"))) {
			rt1 = TRANSPORT.TYPE_SELECT;
		}
		// 7708
		/*
		 * else if ( (str3.equals("LOCK")) || (str3.equals("LOCKING")) ) { rt1 =
		 * TRANSPORT.TYPE_SELECT; stmtIsLock = true; }
		 */
		else if (str3.equals("UPDATE") || str3.equals("MERGE")) {
			rt1 = TRANSPORT.TYPE_UPDATE;
		} else if (str3.equals("DELETE")) {
			rt1 = TRANSPORT.TYPE_DELETE;
		} else if (str3.equals("INSERT") || str3.equals("INS")) {
			if (hasParameters(str)) {
				rt1 = TRANSPORT.TYPE_INSERT_PARAM;
			} else {
				rt1 = TRANSPORT.TYPE_INSERT;
			}
		}
		/*
		 * else if (str3.equals("EXPLAIN")) { rt1 = TRANSPORT.TYPE_EXPLAIN; }
		 */
		else if (str3.equals("CREATE")) {
			rt1 = TRANSPORT.TYPE_CREATE;
		} else if (str3.equals("GRANT")) {
			rt1 = TRANSPORT.TYPE_GRANT;
		} else if (str3.equals("DROP")) {
			rt1 = TRANSPORT.TYPE_DROP;
		} else if (str3.equals("CALL")) {
			rt1 = TRANSPORT.TYPE_CALL;
		} else if (str3.equals("EXPLAIN")) {
			rt1 = TRANSPORT.TYPE_EXPLAIN;
		} else if (str3.equals("INFOSTATS")) {
			rt1 = TRANSPORT.TYPE_STATS;
		}
		// else if (str3.equals("ADD") || str3.equals("ALTER") ||
		// str3.equals("INFO") || str3.equals("START") || str3.equals("STOP"))
		else if (str.startsWith(CFG_CMD_TAG)) {
			rt1 = TRANSPORT.TYPE_CONFIG;
		} else if (str.startsWith(SERVICEQS_CMD_TAG)) {
			rt1 = TRANSPORT.TYPE_QS;
		}
		// qs_interface support and 10-061219-1278
		else if (str3.equals("WMSOPEN"))
			rt1 = TRANSPORT.TYPE_QS_OPEN;
		else if (str3.equals("STATUS") || str3.equals("INFO")) {
			rt1 = TRANSPORT.TYPE_QS;
		} else if (str3.equals("WMSCLOSE"))
			rt1 = TRANSPORT.TYPE_QS_CLOSE;
		else if (str3.equals("CONTROL"))
			rt1 = TRANSPORT.SQL_CONTROL;
		else {
			rt1 = TRANSPORT.TYPE_UNKNOWN;
		}
		return rt1;

	} // end getSqlStmtType

	// -------------------------------------------------------------
	long getRowCount() {
		return rowCount_;
	}

	// -------------------------------------------------------------
	void setRowCount(long rowCount) {
		if (rowCount < 0) {
			rowCount_ = -1;
		} else {
			rowCount_ = rowCount;
		}
	}

	// -------------------------------------------------------------
	static SQLMXDesc[] NewDescArray(SQLItemDescList_def desc) {
		int index;
		SQLMXDesc[] SQLMXDescArray;
		SQLItemDesc_def SQLDesc;

		if (desc.list == null || desc.list.length == 0) {
			return null;
		}

		SQLMXDescArray = new SQLMXDesc[desc.list.length];

		for (index = 0; index < desc.list.length; index++) {
			SQLDesc = desc.list[index];
			boolean nullInfo = (((new Byte(SQLDesc.nullInfo)).shortValue()) == 1) ? true
					: false;
			boolean signType = (((new Byte(SQLDesc.signType)).shortValue()) == 1) ? true
					: false;
			SQLMXDescArray[index] = new SQLMXDesc(SQLDesc.dataType,
					(short) SQLDesc.datetimeCode, SQLDesc.maxLen,
					SQLDesc.precision, SQLDesc.scale, nullInfo,
					SQLDesc.colHeadingNm, signType, SQLDesc.ODBCDataType,
					SQLDesc.ODBCPrecision, SQLDesc.SQLCharset,
					SQLDesc.ODBCCharset, SQLDesc.CatalogName,
					SQLDesc.SchemaName, SQLDesc.TableName, SQLDesc.dataType,
					SQLDesc.intLeadPrec, SQLDesc.paramMode);
			//Soln 10-100528-0676 start
			if (desc.list[index].Heading != null) {
				SQLMXDescArray[index].columnLabel_ = desc.list[index].Heading;
			}
			//Soln 10-100528-0676 End
		}
		return SQLMXDescArray;
	}

	// -------------------------------------------------------------
	static SQLMXDesc[] NewDescArray(Descriptor2[] descArray) {
		int index;
		SQLMXDesc[] SQLMXDescArray;
		Descriptor2 desc;

		if (descArray == null || descArray.length == 0) {
			return null;
		}

		SQLMXDescArray = new SQLMXDesc[descArray.length];

		for (index = 0; index < descArray.length; index++) {
			desc = descArray[index];
			boolean nullInfo = false;
			boolean signType = false;

			if (desc.nullInfo_ != 0) {
				nullInfo = true;
			}
			if (desc.signed_ != 0) {
				signType = true;

			}
			SQLMXDescArray[index] = new SQLMXDesc(desc.noNullValue_,
					desc.nullValue_, desc.version_, desc.dataType_,
					(short) desc.datetimeCode_, desc.maxLen_,
					(short) desc.precision_, (short) desc.scale_, nullInfo,
					signType, desc.odbcDataType_, desc.odbcPrecision_,
					desc.sqlCharset_, desc.odbcCharset_, desc.colHeadingNm_,
					desc.tableName_, desc.catalogName_, desc.schemaName_,
					desc.headingName_, desc.intLeadPrec_, desc.paramMode_,
					desc.dataType_, desc.getRowLength());
		}
		return SQLMXDescArray;
	}

	// -------------------------------------------------------------
	// Interface methods
	void executeDirect(int queryTimeout, SQLMXStatement stmt)
			throws SQLException {
		short executeAPI = stmt.getOperationID();
		byte[] messageBuffer = stmt.getOperationBuffer();
		GenericReply gr = null;

		gr = t4statement_.ExecuteGeneric(executeAPI, messageBuffer);
		stmt.operationReply_ = gr.replyBuffer;

		if (ic_.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(
					stmt_.connection_.props_, queryTimeout, stmt);
			String temp = "Exiting ExecDirect.";
			ic_.t4props_.t4Logger_.logp(Level.FINEST, "InterfaceStatement",
					"executeDirect", temp, p);
		}
	} // end executeDirect

	// --------------------------------------------------------------------------
	int close() throws SQLException {
		int rval = 0;
		CloseReply cry_ = null;
		ic_.isConnectionOpen();
		if (ic_.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities
					.makeParams(stmt_.connection_.props_,stmt_.connection_);
			String temp = "Closing = " + stmtLabel_;
			ic_.t4props_.t4Logger_.logp(Level.FINEST, "InterfaceStatement",
					"close", temp, p);
		}

		cry_ = t4statement_.Close();
		switch (cry_.m_p1.exception_nr) {
		case TRANSPORT.CEE_SUCCESS:

			// ignore the SQLWarning for the static close
			break;
		case odbc_SQLSvc_Close_exc_.odbc_SQLSvc_Close_SQLError_exn_:
			SQLMXMessages.throwSQLException(stmt_.connection_.props_,
					cry_.m_p1.SQLError);
		default:
			throw SQLMXMessages.createSQLException(stmt_.connection_.props_,
					ic_.getLocale(), "ids_unknown_reply_error", null);
		} // end switch

		return cry_.m_p2; // rowsAffected
	} // end close

	// --------------------------------------------------------------------------
	void cancel() throws SQLException {
		ic_.cancel();
	}

	// --------------------------------------------------------------------------
	// Interface methods for prepared statement
	void prepare(String sql, int queryTimeout, SQLMXPreparedStatement pstmt)
			throws SQLException {
		int sqlAsyncEnable = 0;
		this.stmtType_ = this.EXTERNAL_STMT;
		this.sqlStmtType_ = getSqlStmtType(sql);
		int stmtLabelCharset = 1;
		String cursorName = pstmt.cursorName_;
		int cursorNameCharset = 1;
		String moduleName = pstmt.moduleName_;
		int moduleNameCharset = 1;
		long moduleTimestamp = pstmt.moduleTimestamp_;
		String sqlString = sql;
		int sqlStringCharset = 1;
		String stmtOptions = "";
		int maxRowsetSize = pstmt.getMaxRows();

		byte[] txId;

		if (ic_.t4props_.getSPJEnv())
			txId = getUDRTransaction(this.ic_.getByteSwap());
		else
			txId = Bytes.createIntBytes(0, false);

		if (sqlStmtType_ == TRANSPORT.TYPE_STATS) {
			throw SQLMXMessages.createSQLException(pstmt.connection_.props_,
					ic_.getLocale(), "infostats_invalid_error", null);
		} else if (sqlStmtType_ == TRANSPORT.TYPE_CONFIG) {
			throw SQLMXMessages.createSQLException(pstmt.connection_.props_,
					ic_.getLocale(), "config_cmd_invalid_error", null);
		}

		PrepareReply pr = t4statement_.Prepare(sqlAsyncEnable,
				(short) this.stmtType_, this.sqlStmtType_, pstmt.stmtLabel_,
				stmtLabelCharset, cursorName, cursorNameCharset, moduleName,
				moduleNameCharset, moduleTimestamp, sqlString,
				sqlStringCharset, stmtOptions, maxRowsetSize, txId);

		pr_ = pr;
		this.sqlQueryType_ = pr.sqlQueryType;

		switch (pr.returnCode) {
		case TRANSPORT.SQL_SUCCESS:
		case TRANSPORT.SQL_SUCCESS_WITH_INFO:
			SQLMXDesc[] OutputDesc = InterfaceStatement
					.NewDescArray(pr.outputDesc);
			SQLMXDesc[] InputDesc = InterfaceStatement
					.NewDescArray(pr.inputDesc);
			pstmt.setPrepareOutputs2(InputDesc, OutputDesc,
					pr.inputNumberParams, pr.outputNumberParams,
					pr.inputParamLength, pr.outputParamLength,
					pr.inputDescLength, pr.outputDescLength);

			if (pr.errorList != null && pr.errorList.length > 0) {
				SQLMXMessages.setSQLWarning(stmt_.connection_.props_, pstmt,
						pr.errorList);
			}

			this.stmtHandle_ = pr.stmtHandle;

			break;

		case odbc_SQLSvc_Prepare_exc_.odbc_SQLSvc_Prepare_SQLError_exn_:

		default:
			SQLMXMessages.throwSQLException(stmt_.connection_.props_,
					pr.errorList);
		}

		if (ic_.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(
					stmt_.connection_.props_, sql, queryTimeout, pstmt);
			String temp = "Exiting prepare...";
			ic_.t4props_.t4Logger_.logp(Level.FINEST, "InterfaceStatement",
					"prepare", temp, p);
		}
	};

	// used to keep the same transaction inside an SPJ. we call out to the UDR
	// server and use their transaction for all executes.
	byte[] getUDRTransaction(boolean swapBytes) throws SQLException {
		byte[] ret = null;

		try {
			// To get references to method
			InterfaceStatement.LmUtility_class_ = Class
					.forName("com.tandem.sqlmx.LmUtility");
			InterfaceStatement.LmUtility_getTransactionId_ = InterfaceStatement.LmUtility_class_
					.getMethod("getTransactionId", (java.lang.Class[])null);

			// To invoke the method
			short[] tId = (short[]) InterfaceStatement.LmUtility_getTransactionId_
					.invoke(null, (java.lang.Object[])null);

			ret = new byte[tId.length * 2];

			for (int i = 0; i < tId.length; i++) {
				Bytes.insertShort(ret, i * 2, tId[i], swapBytes);
			}
		} catch (Exception e) {
			ic_.t4props_.t4Logger_
					.logp(Level.FINEST, "InterfaceStatement",
							"getUDRTransaction",
							"Error calling UDR for transaction id");

			String s = e.toString() + "\r\n";
			StackTraceElement[] st = e.getStackTrace();

			for (int i = 0; i < st.length; i++) {
				s += st[i].toString() + "\r\n";
			}

			throw new SQLException(s);
		}

		return ret;
	}

	// -------------------------------------------------------------------
	void execute(short executeAPI, int paramRowCount, int paramCount,
			Object[] paramValues, int queryTimeout
			// executeDirect
			, String sql, SQLMXStatement stmt

	) throws SQLException {
		cursorName_ = stmt.cursorName_;
		rowCount_ = 0;

		int sqlAsyncEnable = 0;
		int inputRowCnt = paramRowCount;
		int maxRowsetSize = stmt.getMaxRows();
		String sqlString = (sql == null) ? stmt.getSQL() : sql;
		int sqlStringCharset = 1;
		int cursorNameCharset = 1;
		int stmtLabelCharset = 1;
		byte[] txId;
		ArrayList clientErrors = new ArrayList();

		// Added for SetQuerytimeout R3.0
		t4statement_.m_queryTimeout = queryTimeout;
		//Added for ExecuteArray R3.0, if API is execute_array, 
		//server required marshalled data in rowwise
		if(executeAPI == TRANSPORT.SRVR_API_EXECUTE_ARRAY){
			rowWiseData = true;
		}else {
			rowWiseData = false;
		}
		if (ic_.t4props_.getSPJEnv())
			txId = getUDRTransaction(this.ic_.getByteSwap());
		else if (stmt.transactionToJoin != null)
			txId = stmt.transactionToJoin;
		else if (stmt.connection_.transactionToJoin != null)
			txId = stmt.connection_.transactionToJoin;
		else
			txId = Bytes.createIntBytes(0, false); // 0 length, no data

		SQL_DataValue_def inputDataValue;
		SQLValueList_def inputValueList = new SQLValueList_def();
		byte[] inputParams = null;

		if (executeAPI == TRANSPORT.SRVR_API_SQLEXECDIRECT) {
			sqlStmtType_ = getSqlStmtType(sql);
			//Added for CQDs filter soln 10-101118-4598
			if(sqlStmtType_==TRANSPORT.SQL_CONTROL){
			stmt.removeMFCFromStmtLabel();
			}
			stmt.outputDesc_ = null; // clear the output descriptors

			if (sqlStmtType_ == TRANSPORT.TYPE_CONFIG) {
				// begins with cfg_cmd:
				if (sqlString.startsWith(CFG_CMD_TAG))
					sqlString = sqlString
							.substring(InterfaceStatement.CFG_CMD_TAG.length());
			} else if (sqlStmtType_ == TRANSPORT.TYPE_QS) {
				if (sqlString.startsWith(SERVICEQS_CMD_TAG)) {
					sqlString = sqlString
							.substring(InterfaceStatement.SERVICEQS_CMD_TAG
									.length());
					// toUpper ruins some Query IDs -- dont do it!
					// sqlString = sqlString.toUpperCase();

					if (sqlString.startsWith("WMSOPEN")) {
						sqlStmtType_ = TRANSPORT.TYPE_QS_OPEN;
					} else if (sqlString.startsWith("WMSCLOSE")) {
						sqlStmtType_ = TRANSPORT.TYPE_QS_CLOSE;
					}
				}
			}

			if (sqlStmtType_ == TRANSPORT.TYPE_QS_OPEN) {
				this.ic_.setWmsMode(true);
			} else if (sqlStmtType_ == TRANSPORT.TYPE_QS_CLOSE) {
				this.ic_.setWmsMode(false);
			}

			// qs_interface support and 10-061219-1278
			if (sqlStmtType_ == TRANSPORT.TYPE_QS) {
				if (sqlString.toUpperCase().startsWith("STATUS")
						|| sqlString.toUpperCase().startsWith("INFO")) {
					sqlStmtType_ = TRANSPORT.TYPE_SELECT;
				} else {
					sqlStmtType_ = TRANSPORT.TYPE_UNKNOWN;
				}
			}
		}

		if (stmt.usingRawRowset_ == true) {
			executeAPI = TRANSPORT.SRVR_API_SQLEXECUTE2;
			inputDataValue = new SQL_DataValue_def();
			inputDataValue.userBuffer = stmt.rowwiseRowsetBuffer_;
			inputDataValue.length = stmt.rowwiseRowsetBuffer_.limit() - 4;

			if (this.sqlQueryType_ == 16) // use the param values
			{
				try {
					inputRowCnt = Integer.parseInt(paramValues[0].toString());
					maxRowsetSize = Integer.parseInt(paramValues[1].toString());
				} catch (Exception e) {
					throw new SQLException(
							"Error setting inputRowCnt and maxRowsetSize.  Parameters not set or invalid.");
				}
			} else {
				inputRowCnt = paramRowCount - 1;
			}
		} else {
			inputDataValue = fillInSQLValues2(ic_.getLocale(), stmt,
					inputRowCnt, paramCount, paramValues, clientErrors);

			if (ic_.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(
						stmt_.connection_.props_, paramRowCount, paramCount,
						paramValues, queryTimeout, stmt);
				String temp = "invoke ==> Execute2";
				ic_.t4props_.t4Logger_.logp(Level.FINEST, "InterfaceStatement",
						"execute", temp, p);
			}
		}
		//R3.1 changes -- start
		long beginTime=0,endTime,timeTaken;
		if (stmt_.connection_.props_.getQueryExecuteTime() > 0) {
		beginTime=System.currentTimeMillis();
		}
		//R3.1 changes -- End
		
		
	        	ExecuteReply er = t4statement_.Execute(executeAPI, sqlAsyncEnable,
				inputRowCnt - clientErrors.size(), maxRowsetSize,
				this.sqlStmtType_, this.stmtHandle_, sqlString,
				sqlStringCharset, this.cursorName_, cursorNameCharset,
				stmt.stmtLabel_, stmtLabelCharset, inputDataValue,
				inputValueList, txId, stmt.usingRawRowset_);
	        	
	        	//R3.1 changes -- start
				if (stmt_.connection_.props_.getQueryExecuteTime() > 0) {
				endTime=System.currentTimeMillis();
				timeTaken=endTime - beginTime;
				
					if (timeTaken > stmt_.connection_.props_.getQueryExecuteTime()) {

						if (T4Properties.t4SlowQueryGlobalLogger.isLoggable(Level.INFO) == true) {
							Object p1[] = T4LoggingUtilities
									.makeParams();
							T4Properties.t4SlowQueryGlobalLogger.logp(Level.INFO,stmt_.stmtLabel_,stmt_.sql_,"TIME TAKEN "+timeTaken+" ms");
							
						}
					}
				}
				//R3.1 changes -- end

		if (executeAPI == TRANSPORT.SRVR_API_SQLEXECDIRECT) {
			this.sqlQueryType_ = er.queryType;
		}

		if (clientErrors.size() > 0) {
			if (er.errorList == null)
				er.errorList = (SQLWarningOrError[]) clientErrors
						.toArray(new SQLWarningOrError[clientErrors.size()]);
			else
				er.errorList = mergeErrors((SQLWarningOrError[]) clientErrors
						.toArray(new SQLWarningOrError[clientErrors.size()]),
						er.errorList);
		}

		stmt_.result_set_offset = 0;
		rowCount_ = er.rowsAffected;
	//Modified for soln 10-140409-1405
		outValuesFormat=er.outValuesFormat_;
		int numStatus;

		if (stmt_.connection_.props_.getDelayedErrorMode()) {
			if (stmt_._lastCount > 0) {
				numStatus = stmt_._lastCount;
			} else {
				numStatus = inputRowCnt;
			}
		} else {
			numStatus = inputRowCnt;
		}

		if (numStatus < 1) {
			numStatus = 1;
		}

		stmt.batchRowCount_ = new int[numStatus];

		// Modified for ExecuteBatch R3.0
		// start
		if (executeAPI == TRANSPORT.SRVR_API_EXECUTE_ARRAY) {
			//Modified for sol. 10-110916-9704 
			if(er.returnCode == TRANSPORT.SQL_SUCCESS
					|| er.returnCode == TRANSPORT.SQL_SUCCESS_WITH_INFO) {
				
				stmt.batchRowCount_ = er.rowsAffectedArray;
				if(er.errorList !=null && er.errorList.length > 0){
					SQLMXMessages.setSQLWarning(stmt_.connection_.props_, stmt, er.errorList);
				}				
				return;
			}else {
				if(er.errorList !=null){
					SQLMXMessages.throwSQLException(stmt_.connection_.props_,
							er.errorList);
					return;
				}
			}		
			
		}
		// End
		
		if (stmt_.connection_.props_.getDelayedErrorMode()
				&& stmt_._lastCount < 1) {
			Arrays.fill(stmt.batchRowCount_, -2); // fill with success
		} else if (er.returnCode == TRANSPORT.SQL_SUCCESS
				|| er.returnCode == TRANSPORT.SQL_SUCCESS_WITH_INFO
				|| er.returnCode == TRANSPORT.NO_DATA_FOUND) {
			Arrays.fill(stmt.batchRowCount_, -2); // fill with success

			if (er.errorList != null) // if we had errors with valid rowIds,
			// update the array
			{
				for (int i = 0; i < er.errorList.length; i++) {
					int row = er.errorList[i].rowId - 1;
					if (row >= 0 && row < stmt.batchRowCount_.length) {
						stmt.batchRowCount_[row] = -3;
					}
				}
			}

			// set the statement label if we didnt get one back.
			if (er.stmtLabels == null || er.stmtLabels.length == 0) {
				er.stmtLabels = new String[1];
				er.stmtLabels[0] = stmt.stmtLabel_;
			}

			// get the descriptors from the proper location
			SQLMXDesc[][] desc = null;

			// try from execute data first
			if (er.outputDesc != null && er.outputDesc.length > 0) {
				desc = new SQLMXDesc[er.outputDesc.length][];

				for (int i = 0; i < er.outputDesc.length; i++) {
					desc[i] = InterfaceStatement.NewDescArray(er.outputDesc[i]);
				}

				// Added for solution 10-150824-7462

				if (this.sqlStmtType_ != TRANSPORT.TYPE_CALL) {
					stmt.outputDesc_ = desc[0];
				}

			}
			// try from the prepare data
			else if (stmt.outputDesc_ != null && stmt.outputDesc_.length > 0) {
				desc = new SQLMXDesc[1][];
				desc[0] = stmt.outputDesc_;
			}

			if (this.sqlStmtType_ == TRANSPORT.TYPE_CALL) {
				// Soln 10-171121-5576: Cannot execute stored procedures using 
				// PreparedStatement or Statement Object due to below cast
				//SQLMXCallableStatement cstmt = (SQLMXCallableStatement) stmt;
				Object[] outputValueArray;
				if (er.returnCode == TRANSPORT.NO_DATA_FOUND) { // this should
					// really only
					// happen with
					// LAST0
					// specified
					outputValueArray = new Object[stmt.outputDesc_.length];
				} else {
					//Modified for soln 10-140409-1405
					outputValueArray = InterfaceResultSet.getExecute2Outputs(
							stmt.connection_, stmt.outputDesc_, er.outValues,
							this.ic_.getByteSwap(),outValuesFormat);
				}

				stmt.setExecuteCallOutputs(outputValueArray,
						(short) er.rowsAffected);
				stmt.setMultipleResultSets(er.numResultSets, desc,
						er.stmtLabels, er.proxySyntax);
			} else {
				// fix until we start returning numResultsets for more than just
				// SPJs
				if (desc != null && desc.length > 0 && er.numResultSets == 0) {
					er.numResultSets = 1;
				}

				if (er.outValues != null && er.outValues.length > 0) {
					stmt.setExecute2Outputs(er.outValues,
							(short) er.rowsAffected, false, er.proxySyntax,
							desc[0]);
				} else {
					stmt.setMultipleResultSets(er.numResultSets, desc,
							er.stmtLabels, er.proxySyntax);
				}
			}
			if (er.errorList != null) {
				SQLMXMessages.setSQLWarning(stmt_.connection_.props_, stmt,
						er.errorList);
			}
		} else {
			Arrays.fill(stmt.batchRowCount_, -3); // fill with failed
			SQLMXMessages.throwSQLException(stmt_.connection_.props_,
					er.errorList);
		}
	}

	// For MFC R3.0
	public synchronized void setStmtLabel_(String stmtLabel_) {
		this.stmtLabel_ = stmtLabel_;
	}

	public String getStmtLabel_() {
		return stmtLabel_;
	}

	public void setT4statement_(T4Statement t4statement_) {
		this.t4statement_ = t4statement_;
	}

	public T4Statement getT4statement_() {
		return t4statement_;
	}
} // end class InterfaceStatement
