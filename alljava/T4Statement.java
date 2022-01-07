// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003-2007
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

import java.net.SocketTimeoutException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.util.logging.Level;

final class T4Statement extends T4Connection {
	//changed private to public for setQuerytimeout R3.0
	public int m_queryTimeout;

	private String m_stmtLabel;

	private String m_stmtExplainLabel;

	private static short EXTERNAL_STMT = 0;

	boolean m_processing = false;
	
	private String closeConnectionUponQueryTimeout = SQLMXSwitchConCloseOnQueryTimeout.DEFAULT;

	// -----------------------------------------------------------------------------------
	T4Statement(InterfaceStatement is, String closeConnectionUponQueryTimeout) throws SQLException {
		super(is.ic_);
		m_queryTimeout = is.queryTimeout_;
		//R3.0
		 setM_stmtLabel(is.getStmtLabel_());
	//	m_stmtLabel = is.stmtLabel_;
		m_stmtExplainLabel = "";
		this.closeConnectionUponQueryTimeout = closeConnectionUponQueryTimeout;

	    if (getM_stmtLabel() == null)
	    {
	      throwInternalException();

	    }
	}// end T4Statement

	// -----------------------------------------------------------------------------------

	ExecuteReply Execute(short executeAPI, int sqlAsyncEnable, int inputRowCnt,
			int maxRowsetSize, int sqlStmtType, int stmtHandle,
			String sqlString, int sqlStringCharset, String cursorName,
			int cursorNameCharset, String stmtLabel, int stmtLabelCharset,
			SQL_DataValue_def inputDataValue, SQLValueList_def inputValueList,
			byte[] txId, boolean userBuffer) throws SQLException {
		try {
	//		getInputOutput().setTimeout(m_ic.t4props_.getNetworkTimeout());
			//R3.0 - for SetQueryTimeOut() feature
			if (this.closeConnectionUponQueryTimeout
					.equals(SQLMXSwitchConCloseOnQueryTimeout.ON)) {
				//Modification for L36 Corda : converting to milliseconds
				getInputOutput().setTimeout(m_queryTimeout*1000);
				//End
			} else {
				//Modification for L36 Corda : NetworkTimeout in milliseconds
				getInputOutput().setTimeout(m_ic.getNetworkTimeout());
				//End
			}

			//Added for Commitwork RFE -Start
			short[] tmfHandle = null;
			byte[] ret = null;
			String os = System.getProperty("os.name");
			if (os.equalsIgnoreCase("NONSTOP_KERNEL")
					&& m_ic.t4props_.getUseExternalTransaction()
							.equalsIgnoreCase("YES")) {
				try {
					com.tandem.tmf.Current txn = new com.tandem.tmf.Current();
					com.tandem.tmf.ControlRef cref = txn.get_control();
					if (cref != null) {					
						tmfHandle = cref.getTransactionHandle();
					
						ret = new byte[tmfHandle.length * 2];
						for (int i = 0; i < tmfHandle.length; i++) {
							Bytes.insertShort(ret, i * 2, tmfHandle[i], false);
						}
					if (m_ic.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
							Object p[] = T4LoggingUtilities
									.makeParams(m_ic.t4props_,m_ic);
							String temp = "Transaction handle obtained";
							m_ic.t4props_.t4Logger_.logp(Level.FINEST,
									"T4Statement", "Execute", temp, p);
						}
					}else {
						throw SQLMXMessages.createSQLException(m_ic.t4props_,
								m_locale, "txn_error", null);
					}

				}
				catch (Exception e) {
					if (e instanceof com.tandem.util.FSException) {
						// Need to decide the behaviour
						SQLException se = SQLMXMessages.createSQLException(
								m_ic.t4props_, m_locale, "nsj_error",
								"Execute", e.getMessage());
						se.initCause(e);
						throw se;
					}
					if (e instanceof SQLException) {
						throw e;
					}
					else {
						SQLException se = SQLMXMessages.createSQLException(
								m_ic.t4props_, m_locale,
								"nsj_internal_error", "Execute", e
										.getMessage());
						throw se;
					}
				}
			}
			 ////Added for Commitwork RFE - End 
			LogicalByteArray wbuffer = ExecuteMessage.marshal(
					this.m_dialogueId, sqlAsyncEnable, this.m_queryTimeout,
					inputRowCnt, maxRowsetSize, sqlStmtType, stmtHandle,
					this.EXTERNAL_STMT, sqlString, sqlStringCharset,
					cursorName, cursorNameCharset, stmtLabel, stmtLabelCharset,
					this.m_stmtExplainLabel, inputDataValue, inputValueList,
					txId, userBuffer, this.m_ic,ret);

			LogicalByteArray rbuffer = getReadBuffer(executeAPI, wbuffer);

			ExecuteReply er = new ExecuteReply(rbuffer, m_ic);

			return er;
		} catch (SQLException e) {
//			throw e;
			//Added for SetQueryTimeout impl. -R3.0
			
			synchronized (this) {

				if (m_ic.t4props_.getCloseConnectionUponQueryTimeout().equals(
						SQLMXSwitchConCloseOnQueryTimeout.ON)
						&& e.getCause() instanceof SocketTimeoutException) {
					if (e.getCause().getMessage().equals("Read timed out")) {

						
						//Modified for solution 10-140418-1543
						SQLMXException ex = null;
						CancelReply cr_ = null;
						if (m_ic != null) {
							ex = SQLMXMessages.createSQLException(
									m_ic.t4props_, m_ic.getLocale(),
									"AS_API_STOPSRVR", null);
							this.m_ic.setIsClosed(true);

							cr_ = SQLMX_AssociationServer_Cancel.cancel(
									m_ic.t4props_, m_ic, m_ic.getDialogueId(),
									2, m_ic.mxcsAddr_.m_url + "", 0);
						} else {

							ex = SQLMXMessages.createSQLException(null,
									m_locale, "Connection Already closed.",
									null);

						}

						ex.initCause(e.getCause());
						throw ex;

					} else {
						throw e;
					}
				} else {
					throw e;
				}

			}// synchronized end.
					
		} catch (CharacterCodingException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "translation_of_parameter_failed",
					"ExecuteMessage", e.getMessage());
			se.initCause(e);
			throw se;
		} catch (UnsupportedCharsetException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "unsupported_encoding", e.getCharsetName());
			se.initCause(e);
			throw se;
		} catch (Exception e) {	
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "execute_message_error", e.getMessage());
			se.initCause(e);
			throw se;
		}
	} // end Execute

	// -----------------------------------------------------------------------------------
	GenericReply ExecuteGeneric(short executeAPI, byte[] messageBuffer)
			throws SQLException {
		LogicalByteArray wbuffer = null;
		LogicalByteArray rbuffer = null;
		GenericReply gr = null;

		try {
		//getInputOutput().setTimeout(m_ic.t4props_.getNetworkTimeout());
			//R3.0 - for SetQueryTimeOut() feature
			if (this.closeConnectionUponQueryTimeout
					.equals(SQLMXSwitchConCloseOnQueryTimeout.ON)) {
				//Modification for L36 Corda : converting to milliseconds
				getInputOutput().setTimeout(m_queryTimeout*1000);
				//End
			} else {
				//Modification for L36 Corda : NetworkTimeout in milliseconds
				getInputOutput().setTimeout(m_ic.getNetworkTimeout());
				//End
			}
			
			wbuffer = GenericMessage
					.marshal(m_locale, messageBuffer, this.m_ic);
			rbuffer = getReadBuffer(executeAPI, wbuffer);
			gr = new GenericReply(m_locale, rbuffer);

			return gr;
		} catch (SQLException se) {
			throw se;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "execute_message_error", e.getMessage());

			se.initCause(e);
			throw se;
		}
	} // end ExecuteGeneric

	// -----------------------------------------------------------------------------------
	PrepareReply Prepare(int sqlAsyncEnable, short stmtType, int sqlStmtType,
			String stmtLabel, int stmtLabelCharset, String cursorName,
			int cursorNameCharset, String moduleName, int moduleNameCharset,
			long moduleTimestamp, String sqlString, int sqlStringCharset,
			String stmtOptions, int maxRowsetSize, byte[] txId

	) throws SQLException {

		if (sqlString == null) {
			throwInternalException();
		}
		try {
			//Modification for L36 Corda : NetworkTimeout in milliseconds
			getInputOutput().setTimeout(m_ic.getNetworkTimeout());
			//End

			LogicalByteArray wbuffer = PrepareMessage.marshal(
					this.m_dialogueId, sqlAsyncEnable, this.m_queryTimeout,
					stmtType, sqlStmtType, stmtLabel, stmtLabelCharset,
					cursorName, cursorNameCharset, moduleName,
					moduleNameCharset, moduleTimestamp, sqlString,
					sqlStringCharset, stmtOptions, this.m_stmtExplainLabel,
					maxRowsetSize, txId, this.m_ic);

			LogicalByteArray rbuffer = getReadBuffer(
					TRANSPORT.SRVR_API_SQLPREPARE, wbuffer);

			PrepareReply pr = new PrepareReply(rbuffer, m_ic);

			return pr;
		} catch (SQLException se) {
			throw se;
		} catch (CharacterCodingException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "translation_of_parameter_failed",
					"PrepareMessage", e.getMessage());
			se.initCause(e);
			throw se;
		} catch (UnsupportedCharsetException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "unsupported_encoding", e.getCharsetName());
			se.initCause(e);
			throw se;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "prepare_message_error", e.getMessage());

			se.initCause(e);
			throw se;
		}
	} // end Prepare

	// -----------------------------------------------------------------------------------

	CloseReply Close() throws SQLException {
		try {
			LogicalByteArray wbuffer = CloseMessage.marshal(m_dialogueId,
					m_stmtLabel, InterfaceStatement.SQL_DROP, this.m_ic);
			//Modification for L36 Corda : NetworkTimeout in milliseconds
			getInputOutput().setTimeout(m_ic.getNetworkTimeout());
			//End
			LogicalByteArray rbuffer = getReadBuffer(
					TRANSPORT.SRVR_API_SQLFREESTMT, wbuffer);

			CloseReply cr = new CloseReply(rbuffer, m_ncsAddress.getIPorName(),
					m_ic);

			return cr;
		} catch (SQLException se) {
			throw se;
		} catch (CharacterCodingException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "translation_of_parameter_failed",
					"CloseMessage", e.getMessage());
			se.initCause(e);
			throw se;
		} catch (UnsupportedCharsetException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "unsupported_encoding", e.getCharsetName());
			se.initCause(e);
			throw se;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "close_message_error", e.getMessage());

			se.initCause(e);
			throw se;
		}
	}

	// --------------------------------------------------------------------------------
	protected LogicalByteArray getReadBuffer(short odbcAPI,
			LogicalByteArray wbuffer) throws SQLException {
		LogicalByteArray buf = null;

		try {
			m_processing = true;
			buf = super.getReadBuffer(odbcAPI, wbuffer);
			m_processing = false;
		} catch (SQLException se) {
			m_processing = false;
			throw se;
		}
		return buf;
	}
	
	//For MFC R3.0
	public synchronized void setM_stmtLabel(String m_stmtLabel) {
		this.m_stmtLabel = m_stmtLabel;
	}



	public String getM_stmtLabel() {
		return m_stmtLabel;
	}
}
