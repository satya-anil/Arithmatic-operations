/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2018 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.io.CharArrayWriter;
import java.sql.SQLException;

public class SQLMXMonitorWriter extends CharArrayWriter {

	private SQLMXWriterMonitor monitor_;
	private int pos_;

	SQLMXMonitorWriter(int pos) {
		pos_ = pos;
	}

	public void close() {
		super.close();
		if (monitor_ != null) {
			try {
				monitor_.writerClosed(this, pos_);
			} catch (SQLException e) {

			}
		}
	}

	public void setMonitor(SQLMXWriterMonitor monitor) {
		this.monitor_ = monitor;
	}

}
