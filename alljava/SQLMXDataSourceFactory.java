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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;

public class SQLMXDataSourceFactory implements javax.naming.spi.ObjectFactory {
	public SQLMXDataSourceFactory() {
	}

	public Object getObjectInstance(Object refobj, Name name, Context nameCtx,
			Hashtable env) throws Exception {
		Reference ref = (Reference) refobj;
		SQLMXDataSource ds;
		String dataSourceName = null;

		if (ref.getClassName().equals("com.tandem.t4jdbc.SQLMXDataSource")) {
			Properties props = new Properties();
			for (Enumeration enum2 = ref.getAll(); enum2.hasMoreElements();) {
				RefAddr tRefAddr = (RefAddr) enum2.nextElement();
				String type = tRefAddr.getType();
				String content = (String) tRefAddr.getContent();
				props.setProperty(type, content);
			}

			ds = new SQLMXDataSource(props);
			dataSourceName = ds.getDataSourceName();

			if (dataSourceName != null) {
				ds.setPoolManager(nameCtx, dataSourceName);
			}
			return ds;
		} else {
			return null;
		}
	}
}
