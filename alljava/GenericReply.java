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

import java.sql.SQLException;
import java.util.Locale;

class GenericReply {
	byte[] replyBuffer;

	// ----------------------------------------------------------
	GenericReply(Locale locale, LogicalByteArray buf) throws SQLException {
		Header header = new Header();
		int bufferLength = 0;

		buf.setLocation(0);
		header.extractFromByteArray(buf);
		buf.setLocation(Header.sizeOf());
		bufferLength = header.total_length_;
		replyBuffer = buf.extractByteArray(bufferLength);

	} // end marshal

} // end class GenericReply
