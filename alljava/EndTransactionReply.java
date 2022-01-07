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

class EndTransactionReply {
	odbc_SQLSvc_EndTransaction_exc_ m_p1;

	ERROR_DESC_LIST_def m_p2;

	// -------------------------------------------------------------
	EndTransactionReply(LogicalByteArray buf, String addr,
			InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException, SQLException {
		buf.setLocation(Header.sizeOf());

		m_p1 = new odbc_SQLSvc_EndTransaction_exc_();
		m_p1.extractFromByteArray(buf, addr, ic);

		if (m_p1.exception_nr == TRANSPORT.CEE_SUCCESS) {
			m_p2 = new ERROR_DESC_LIST_def();
			m_p2.extractFromByteArray(buf, ic);
		}
	}
}
