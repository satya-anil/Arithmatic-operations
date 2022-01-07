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

class InitializeDialogueReply {
	Header m_hdr;

	odbc_SQLSvc_InitializeDialogue_exc_ m_p1_exception;

	OUT_CONNECTION_CONTEXT_def m_p2_outContext;

	// -------------------------------------------------------------
	InitializeDialogueReply(LogicalByteArray buf, String addr,
			InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException, SQLException {
		buf.setLocation(Header.sizeOf());

		m_p1_exception = new odbc_SQLSvc_InitializeDialogue_exc_();
		m_p1_exception.extractFromByteArray(buf, addr, ic);

		int ex_nr = m_p1_exception.exception_nr;
		int ex_nr_d = m_p1_exception.exception_detail;

		// If we got an error, but we are within the password expiring or
		// password grace period, then
		// the connection is ok.
		if (ex_nr == TRANSPORT.CEE_SUCCESS
				|| (ex_nr == odbc_SQLSvc_InitializeDialogue_exc_.odbc_SQLSvc_InitializeDialogue_SQLError_exn_ && (ex_nr_d == odbc_SQLSvc_InitializeDialogue_exc_.SQL_PASSWORD_EXPIRING || ex_nr_d == odbc_SQLSvc_InitializeDialogue_exc_.SQL_PASSWORD_GRACEPERIOD))) {
			m_p2_outContext = new OUT_CONNECTION_CONTEXT_def();
			m_p2_outContext.extractFromByteArray(buf, ic);
		}
	}
}
