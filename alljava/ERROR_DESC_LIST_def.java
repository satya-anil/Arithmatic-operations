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


class ERROR_DESC_LIST_def {
	int length;
	ERROR_DESC_def[] buffer;

	// ----------------------------------------------------------
	void extractFromByteArray(LogicalByteArray buf, InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException {
		length = buf.extractInt();
		buffer = new ERROR_DESC_def[length];

		for (int i = 0; i < length; i++) {
			buffer[i] = new ERROR_DESC_def();
			buffer[i].extractFromByteArray(buf, ic);
		}
	}

}
