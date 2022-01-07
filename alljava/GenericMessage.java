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

class GenericMessage {
	// ----------------------------------------------------------
	static LogicalByteArray marshal(Locale locale, byte[] messageBuffer, InterfaceConnection ic) throws SQLException

	{
		int wlength = Header.sizeOf();
		LogicalByteArray buf;

		if (messageBuffer != null)
			wlength += messageBuffer.length;
		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());
		if (messageBuffer != null)
			buf.insertByteArray(messageBuffer, messageBuffer.length);

		return buf;
	} // end marshal

} // end class GenericMessage

