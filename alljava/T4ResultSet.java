// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003, 2004, 2005
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

final class T4ResultSet extends T4Connection {
	private String m_stmtLabel;

	private String closeConnectionUponQueryTimeout = SQLMXSwitchConCloseOnQueryTimeout.DEFAULT;
	static final short SQL_CLOSE = 0;

	boolean m_processing = false;

	T4ResultSet(InterfaceResultSet ir, String closeConnectionUponQueryTimeout) throws SQLException {
		super(ir.ic_);
		m_stmtLabel = ir.stmtLabel_;
		this.closeConnectionUponQueryTimeout = closeConnectionUponQueryTimeout;
		if (m_stmtLabel == null) {
			throwInternalException();

		}
	}

	/**
	 * This method will send a fetch rowset command to the server.
	 * 
	 * @param maxRowCnt
	 *            the maximum rowset count to return
	 * @param maxRowLen
	 *            the maximum row length to return
	 * @param sqlAsyncEnable
	 *            a flag to enable/disable asynchronies execution
	 * @param queryTimeout
	 *            the number of seconds before the query times out
	 * 
	 * @retrun a FetchPerfReply class representing the reply from the ODBC
	 *         server is returned
	 * 
	 * @exception A
	 *                SQLException is thrown
	 */

	FetchReply Fetch(int sqlAsyncEnable, int queryTimeout, int stmtHandle,
			int stmtCharset, int maxRowCnt, String cursorName,
			int cursorCharset, String stmtOptions) throws SQLException {

		try {
		//	getInputOutput().setTimeout(m_ic.t4props_.getNetworkTimeout()); -R3.0
			if (this.closeConnectionUponQueryTimeout
					.equals(SQLMXSwitchConCloseOnQueryTimeout.ON)) {
				//Modification for L36 Corda : converting to milliseconds
				getInputOutput().setTimeout(queryTimeout*1000);
				//End
			} else {
				//Modification for L36 Corda : NetworkTimeout in milliseconds
				getInputOutput().setTimeout(m_ic.getNetworkTimeout());
				//End
			}

			LogicalByteArray wbuffer = FetchMessage.marshal(m_dialogueId,
					sqlAsyncEnable, queryTimeout, stmtHandle, m_stmtLabel,
					stmtCharset, maxRowCnt, 0 // infinite row size
					, cursorName, cursorCharset, stmtOptions, this.m_ic);
		//R3.0 - for setQueryTimeout Feature
			LogicalByteArray rbuffer = null;
			try {
			rbuffer = getReadBuffer(
					TRANSPORT.SRVR_API_SQLFETCH, wbuffer);
			}catch (SQLMXException e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					throw ((SocketTimeoutException) e.getCause());
				} else {
					throw e;
				}
			}
			//
			// Process output parameters
			//
			FetchReply frr = new FetchReply(rbuffer, m_ic);

			return frr;
		} // end try
		catch (SQLException se) {
			throw se;
		} catch (CharacterCodingException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "translation_of_parameter_failed",
					"FetchMessage", e.getMessage());
			se.initCause(e);
			throw se;
		} catch (UnsupportedCharsetException e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "unsupported_encoding", e.getCharsetName());
			se.initCause(e);
			throw se;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(m_ic.t4props_,
					m_locale, "fetch_perf_message_error", e.getMessage());

			se.initCause(e);
			throw se;
		} // end catch

	} // end FetchPerf

	/**
	 * This method will send an close command, which does not return any
	 * rowsets, to the ODBC server.
	 * 
	 * @retrun A CloseReply class representing the reply from the ODBC server is
	 *         returned
	 * 
	 * @exception A
	 *                SQLException is thrown
	 */

	CloseReply Close() throws SQLException {

		try {
			//Modification for L36 Corda : NetworkTimeout in milliseconds
			getInputOutput().setTimeout(m_ic.getNetworkTimeout());
			//End
			LogicalByteArray wbuffer = CloseMessage.marshal(m_dialogueId,
					m_stmtLabel, SQL_CLOSE, this.m_ic);

			LogicalByteArray rbuffer = getReadBuffer(
					TRANSPORT.SRVR_API_SQLFREESTMT, wbuffer);

			CloseReply cr = new CloseReply(rbuffer, m_ncsAddress.getIPorName(),
					m_ic);

			return cr;
		} // end try
		catch (SQLException se) {
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
		} // end catch

	} // end Close

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
}
