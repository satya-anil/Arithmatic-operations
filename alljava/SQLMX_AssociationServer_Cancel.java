// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2006
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

import java.sql.SQLException;
import java.util.Locale;

/*******************************************************************************
 * 
 * This class corresponds to the ODBC client driver function
 * odbcas_ASSvc_StopSrvr_pst_ as taken from odbcas_drvr.cpp.
 * 
 * @author Swastik Bihani
 * @version 1.0
 ******************************************************************************/

class SQLMX_AssociationServer_Cancel {

	/**
	 * This method will establish an initial connection to the ODBC association
	 * server.
	 * 
	 * @param locale
	 *            The locale associated with this operation
	 * @param dialogueId
	 *            A dialogue ID
	 * @param srvrType
	 *            A server type
	 * @param srvrObjRef
	 *            A server object reference
	 * @param stopType
	 *            The stop type
	 * 
	 * @retrun A CancelReply class representing the reply from the association
	 *         server is returned
	 * 
	 * @exception A
	 *                SQLException is thrown
	 */

	static CancelReply cancel(T4Properties t4props, InterfaceConnection ic_, int dialogueId, int srvrType,
			String srvrObjRef, int stopType) throws SQLException {
		Locale locale = ic_.getLocale();

		try {
			LogicalByteArray rbuffer;
			LogicalByteArray wbuffer;
			//
			// Do marshaling of input parameters.
			//
			wbuffer = CancelMessage.marshal(dialogueId, srvrType, srvrObjRef, stopType, ic_);

			//
			// Get the address of the ODBC Association server.
			//
			// T4Address address1 = new T4Address(t4props, locale,
			// ic_.getUrl());
			T4Address address1 = new T4Address(t4props, locale, t4props.getUrl());

			//
			// Send message to the ODBC Association server.
			//
			InputOutput io1 = address1.getInputOutput();

			io1.openIO();
			//Modification for L36 Corda : NetworkTimeout in milliseconds
			io1.setTimeout(ic_.getNetworkTimeout());
			//End
			io1.setConnectionIdleTimeout(ic_.getConnectionTimeout());

			rbuffer = io1.doIO(TRANSPORT.AS_API_STOPSRVR, wbuffer);

			//
			// Process output parameters
			//

			CancelReply cr1 = new CancelReply(rbuffer, ic_);

			//
			// Close IO
			//
			// io1.setTimeout(ic_.t4props_.getCloseConnectionTimeout());
			//Modification for L36 Corda : NetworkTimeout in milliseconds
			io1.setTimeout(ic_.getNetworkTimeout());
			//End
			io1.CloseIO(wbuffer); // Note, we are re-using the wbuffer

			return cr1;
		} // end try
		catch (SQLException se) {
			throw se;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(t4props, locale, "as_cancel_message_error", e
					.getMessage());

			se.initCause(e);
			throw se;
		} // end catch

	} // end getConnection

} // SQLMX_AssociationServer_Connect
