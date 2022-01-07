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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.util.Locale;

class SQLMX_AssociationServer_Connect {

	/**
	 * This method will establish an initial connection to the ODBC association
	 * server.
	 * 
	 * @param locale
	 *            The locale associated with this operation
	 * @param inContext
	 *            A CONNETION_CONTEXT_def object containing connection
	 *            information
	 * @param userDesc
	 *            A USER_DESC_def object containing user information
	 * @param srvrType
	 *            A server type
	 * @param retryCount
	 *            The number of times to retry the connection
	 * 
	 * @retrun A ConnectReply class representing the reply from the association
	 *         server is returned
	 * 
	 * @exception A
	 *                SQLException is thrown
	 */
	static ConnectReply getConnection(T4Properties t4props,
			InterfaceConnection ic_, CONNECTION_CONTEXT_def inContext,
			USER_DESC_def userDesc, int srvrType, short retryCount)
			throws SQLException {
		Locale locale = ic_.getLocale();

		if (inContext == null || userDesc == null) {
			SQLException se = SQLMXMessages.createSQLException(t4props, locale,
					"internal_error", null);
			SQLException se2 = SQLMXMessages.createSQLException(t4props, locale,
					"contact_hp_error", null);

			se.setNextException(se2);
			throw se;
		}
		try {
			LogicalByteArray rbuffer;
			LogicalByteArray wbuffer;

			// Do marshaling of input parameters.
			wbuffer = ConnectMessage.marshal(inContext, userDesc, srvrType,
					retryCount, 0, 0, Vproc.getVproc(), ic_);

			// Get the address of the ODBC Association server.
			T4Address address1 = new T4Address(t4props, locale, ic_.getUrl());

			// Open the connection
			InputOutput io1 = address1.getInputOutput();

			io1.openIO();
			//Modification for L36 Corda : converting to milliseconds
			io1.setTimeout(ic_.getLoginTimeout()*1000);
			//End
			io1.setConnectionIdleTimeout(ic_.getConnectionTimeout());

			// Send message to the ODBC Association server.
			rbuffer = io1.doIO(TRANSPORT.AS_API_GETOBJREF, wbuffer);

			// Process output parameters
			ConnectReply cr1 = new ConnectReply(rbuffer, ic_);

			// Close IO
			//Modification for L36 Corda : converting to milliseconds
			io1.setTimeout(ic_.t4props_.getLoginTimeout()*1000);
			//End
			io1.CloseIO(wbuffer); // Note, we are re-using the wbuffer

			String name1 = null;
			if (address1.m_ipAddress != null) {
				name1 = address1.m_ipAddress;
			} else if (address1.m_machineName != null) {
				name1 = address1.m_machineName;

			}
			cr1.fixupSrvrObjRef(t4props, locale, name1);

			return cr1;
		} catch (SQLException se) {
			throw se;
		} catch (CharacterCodingException e) {
			SQLException se = SQLMXMessages.createSQLException(ic_.t4props_,
					locale, "translation_of_parameter_failed",
					"ConnectMessage", e.getMessage());
			se.initCause(e);
			throw se;
		} catch (UnsupportedCharsetException e) {
			SQLException se = SQLMXMessages.createSQLException(ic_.t4props_,
					locale, "unsupported_encoding", e.getCharsetName());
			se.initCause(e);
			throw se;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(t4props, locale,
					"as_connect_message_error", e.getMessage());

			se.initCause(e);
			throw se;
		}
	}
}
