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

class CancelMessage {
	static LogicalByteArray marshal(int dialogueId, int srvrType, String srvrObjRef, int stopType,
			InterfaceConnection ic) throws UnsupportedCharsetException, CharacterCodingException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf = null;

		byte[] srvrObjRefBytes = ic.encodeString(srvrObjRef, 1);

		wlength += TRANSPORT.size_int; // dialogueId
		wlength += TRANSPORT.size_int; // srvrType
		wlength += TRANSPORT.size_bytes(srvrObjRefBytes); // srvrObjReference
		wlength += TRANSPORT.size_int; // stopType

		buf = new LogicalByteArray(wlength, Header.sizeOf(), ic.getByteSwap());

		buf.insertInt(dialogueId);
		buf.insertInt(srvrType);
		buf.insertString(srvrObjRefBytes);
		buf.insertInt(stopType);

		return buf;
	}
}
