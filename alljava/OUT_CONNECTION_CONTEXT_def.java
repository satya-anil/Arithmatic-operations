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

class OUT_CONNECTION_CONTEXT_def {
	static final long OUTCONTEXT_OPT1_ENFORCE_ISO88591 = 1; // (2^0)

	static final long OUTCONTEXT_OPT1_IGNORE_SQLCANCEL = 1073741824; // (2^30)

	static final long OUTCONTEXT_OPT1_EXTRA_OPTIONS = 2147483648L; // (2^31)

	VERSION_LIST_def versionList;

	short nodeId;

	int processId;

	String computerName;

	String catalog;

	String schema;

	int optionFlags1;

	int optionFlags2;

	String _roleName;

	boolean _enforceISO;

	boolean _ignoreCancel;

	void extractFromByteArray(LogicalByteArray buf, InterfaceConnection ic)
			throws SQLException, UnsupportedCharsetException,
			CharacterCodingException {
		versionList = new VERSION_LIST_def();
		versionList.extractFromByteArray(buf);

		nodeId = buf.extractShort();
		processId = buf.extractInt();
		computerName = ic.decodeBytes(buf.extractString(), 1);

		catalog = ic.decodeBytes(buf.extractString(), 1);
		schema = ic.decodeBytes(buf.extractString(), 1);

		optionFlags1 = buf.extractInt();
		optionFlags2 = buf.extractInt();

		this._enforceISO = (optionFlags1 & OUTCONTEXT_OPT1_ENFORCE_ISO88591) > 0;
		this._ignoreCancel = (optionFlags1 & OUTCONTEXT_OPT1_IGNORE_SQLCANCEL) > 0;
		if ((optionFlags1 & OUTCONTEXT_OPT1_EXTRA_OPTIONS) > 0) {
			try {
				this.decodeExtraOptions(ic.decodeBytes(buf.extractString(), ic
						.getTerminalCharset()));
			} catch (Exception e) {
				ic.t4props_.logger
						.warning("An error occured parsing OutConnectionContext: "
								+ e.getMessage());
			}
		}
	}

	public void decodeExtraOptions(String options) {
		String[] opts = options.split(";");
		String token;
		String value;
		int index;

		for (int i = 0; i < opts.length; i++) {
			index = opts[i].indexOf('=');
			token = opts[i].substring(0, index).toUpperCase();
			value = opts[i].substring(index + 1);

			if (token.equals("RN")) {
				this._roleName = value;
			}
		}
	}
}
