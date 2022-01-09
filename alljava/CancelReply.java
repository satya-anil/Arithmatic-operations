// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2006-2007
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

class CancelReply {
	odbcas_ASSvc_StopSrvr_exc_ m_p1_exception;

	// -------------------------------------------------------------
	CancelReply(LogicalByteArray buf, InterfaceConnection ic) throws SQLException, CharacterCodingException,
			UnsupportedCharsetException {
		buf.setLocation(Header.sizeOf());

		m_p1_exception = new odbcas_ASSvc_StopSrvr_exc_();
		m_p1_exception.extractFromByteArray(buf, ic);
	}
}