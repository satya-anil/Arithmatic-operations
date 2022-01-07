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

import java.io.Serializable;
import java.sql.SQLException;

abstract class BaseRow implements Serializable, Cloneable {

	protected Object origVals[];

	BaseRow() {
	}

	protected abstract Object getColumnObject(int i) throws SQLException;

	protected Object[] getOrigRow() {
		return origVals;
	}

	protected abstract void setColumnObject(int i, Object obj)
			throws SQLException;
}
