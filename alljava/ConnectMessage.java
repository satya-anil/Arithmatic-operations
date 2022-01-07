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

class ConnectMessage {
	static LogicalByteArray marshal(CONNECTION_CONTEXT_def inContext,
			USER_DESC_def userDesc, int srvrType, short retryCount,
			int optionFlags1, int optionFlags2, String vproc,
			InterfaceConnection ic) throws CharacterCodingException,
			UnsupportedCharsetException {
		int wlength = Header.sizeOf();
		LogicalByteArray buf = null;

		byte[] vprocBytes = ic.encodeString(vproc, 1);

		if (ic.t4props_.getSeaquest()) {
			wlength += inContext.sizeOfChar();
			wlength += userDesc.sizeOfChar();

			// FIXME:
			wlength += 46 + 51;
			// wlength += TRANSPORT.size_bytes(vprocBytes);

			buf = new LogicalByteArray(wlength, Header.sizeOf(), ic
					.getByteSwap());

			inContext.insertIntoByteArrayChar(buf, ic);
			userDesc.insertIntoByteArrayChar(buf, ic);

			buf.insertFixedString(ic.encodeString("" + srvrType, 1), 10);
			buf.insertFixedString(ic.encodeString("" + retryCount, 1), 12);

			buf.insertFixedString(ic.encodeString("" + optionFlags1, 1), 12);
			buf.insertFixedString(ic.encodeString("" + optionFlags2, 1), 12);

			buf.insertFixedString(ic.encodeString(vproc, 1), 51);
		} else {
			wlength += inContext.sizeOf(ic);
			wlength += userDesc.sizeOf(ic);

			wlength += TRANSPORT.size_int; // srvrType
			wlength += TRANSPORT.size_short; // retryCount
			wlength += TRANSPORT.size_int; // optionFlags1
			wlength += TRANSPORT.size_int; // optionFlags2
			wlength += TRANSPORT.size_bytes(vprocBytes);

			buf = new LogicalByteArray(wlength, Header.sizeOf(), ic
					.getByteSwap());

			inContext.insertIntoByteArray(buf);
			userDesc.insertIntoByteArray(buf);

			buf.insertInt(srvrType);
			buf.insertShort(retryCount);
			buf.insertInt(optionFlags1);
			buf.insertInt(optionFlags2);
			buf.insertString(vprocBytes);
		}

		return buf;
	}
}
