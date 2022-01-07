// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003, 2004, 2005
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

class SQLMXException extends SQLException {
	protected String messageId;

	public SQLMXException(String reason, String SQLState, int vendorCode,
			String msgId) {
		super(reason, SQLState, vendorCode);
		if (msgId == null) {
			messageId = "";
		} else {
			messageId = msgId;
		}
	}

} // end class SQLMXException
