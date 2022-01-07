/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2018 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class SQLMXMonitorOutputStream extends ByteArrayOutputStream {

	private SQLMXOutputStreamMonitor monitor_;
	private int pos_;

	SQLMXMonitorOutputStream(int pos) {
		pos_ = pos;
	}

	public void close() throws IOException {
		super.close();

		if (monitor_ != null) {
			try {
				monitor_.streamClosed(this, pos_);
			} catch (SQLException e) {

			}
		}
	}

	public void setMonitor(SQLMXOutputStreamMonitor monitor) {
		monitor_ = monitor;
	}

}
