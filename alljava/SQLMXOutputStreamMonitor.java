/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2018 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.sql.SQLException;

public interface SQLMXOutputStreamMonitor {
	void streamClosed(SQLMXMonitorOutputStream out, int pos) throws SQLException;

}
