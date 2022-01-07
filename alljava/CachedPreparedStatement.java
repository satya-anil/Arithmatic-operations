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

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CachedPreparedStatement {

	PreparedStatement getPreparedStatement() {
		//inUse_ = true; Modified for sol. 10-090219-9374 
		return pstmt_;
	}

	void setLastUsedInfo() {
		lastUsedTime_ = System.currentTimeMillis();
		//  noOfTimesUsed_++;  Modified for sol. 10-090219-9374 
		setNoOfTimesUsed(getNoOfTimesUsed() + 1);
	}

	long getLastUsedTime() {
		return lastUsedTime_;
	}

	String getLookUpKey() {
		return key_;
	}

	void close(boolean hardClose) throws SQLException {
		inUse_ = false;
		pstmt_.close(hardClose);
	}

	CachedPreparedStatement(PreparedStatement pstmt, String key, String sql) {
		pstmt_ = (SQLMXPreparedStatement) pstmt;
		key_ = key;
		creationTime_ = System.currentTimeMillis();
		lastUsedTime_ = creationTime_;
		// noOfTimesUsed_ = 1;
		setNoOfTimesUsed(1);
		inUse_ = true;
		this.setSqlString(sql);
	}

	public void setNoOfTimesUsed(long noOfTimesUsed_) {
		this.noOfTimesUsed_ = noOfTimesUsed_;
	}
	public long getNoOfTimesUsed() {
		return noOfTimesUsed_;
	}
	public void setSqlString(String sql) {
		this.sql = sql;
	}
	public String getSqlString() {
		return sql;
	}
	public void setInUse_(boolean inUse_) {
		this.inUse_ = inUse_;
	}
	public boolean isInUse_() {
		return inUse_;
	}
	private SQLMXPreparedStatement pstmt_;

	private String key_;

	private long lastUsedTime_;

	private long creationTime_;

	private long noOfTimesUsed_;

	boolean inUse_;
	private String sql;
}
