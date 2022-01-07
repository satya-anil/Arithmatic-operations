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

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;


public abstract class PreparedStatementManager extends SQLMXHandle implements Comparator{

	boolean isStatementCachingEnabled() {
		if (maxStatements_ < 1) {
			return false;
		} else {
			return true;
		}
	}

	public int compare(Object arg0, Object arg1) {
		int result = 0;
		if (!this.mfu) {
			if (((CachedPreparedStatement) arg0).getLastUsedTime() < ((CachedPreparedStatement) arg1)
					.getLastUsedTime()) {
				result = -1;
			}
			if (((CachedPreparedStatement) arg0).getLastUsedTime() > ((CachedPreparedStatement) arg1)
					.getLastUsedTime()) {
				result = 1;
			}
			if (((CachedPreparedStatement) arg0).getLastUsedTime() == ((CachedPreparedStatement) arg1)
					.getLastUsedTime()) {
				result = 0;
			}
		} else {
			if (((CachedPreparedStatement) arg0).getNoOfTimesUsed() < ((CachedPreparedStatement) arg1)
					.getNoOfTimesUsed()) {
				result = -1;
			}
			if (((CachedPreparedStatement) arg0).getNoOfTimesUsed() > ((CachedPreparedStatement) arg1)
					.getNoOfTimesUsed()) {
				result = 1;
			}
			if (((CachedPreparedStatement) arg0).getNoOfTimesUsed() == ((CachedPreparedStatement) arg1)
					.getNoOfTimesUsed()) {
				result = 0;
			}
		}
		return result;
	}
	
	/*
	 * makeRoom() method has changed for new stmt caching alogorithm - R3.0
	boolean makeRoom() throws SQLException {
		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "makeRoom()");
			}
		}

		Iterator i;
		CachedPreparedStatement cs;
		long oldest;
		long stmtTime;
		String key;

		i = (prepStmtsInCache_.values()).iterator();
		if (!i.hasNext()) {
			return false;
		}
		cs = (CachedPreparedStatement) i.next();
		stmtTime = cs.getLastUsedTime();
		key = cs.getLookUpKey();
		oldest = stmtTime;

		for (; i.hasNext();) {
			cs = (CachedPreparedStatement) i.next();
			stmtTime = cs.getLastUsedTime();
			if (oldest > stmtTime) {
				oldest = stmtTime;
				key = cs.getLookUpKey();
			}
		}
		cs = (CachedPreparedStatement) prepStmtsInCache_.remove(key);
		if (cs != null) {
			if (cs.inUse_ == false) // if the user has already closed the
				// statement, hard close it
				cs.close(true);

			return true;
		} else {
			return false;
		}
	}
*/
	
//	makeRoom() method has changed for new stmt caching alogorithm (MFU most frequently used)- R3.0
	boolean makeRoom(String sql, int resultSetHoldability) throws SQLException {

		String lookupKey = createKey(((SQLMXConnection) this), sql,
				resultSetHoldability);

		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "makeRoom()");
			}
		}
		CachedPreparedStatement cs = null;
		String key = null;

		if (isStatementCachingEnabled() && prepStmtsInCache_.isEmpty()) {
			return true;
		}
		if (isStatementCachingEnabled()
				&& prepStmtsInCache_.size() < maxStatements_) {
			if (!this.prepStmtsInCache_.containsKey(lookupKey)) {
				return true;
			}
		}
		ArrayList list = new ArrayList();
		if (prepStmtsInCache_.size() == maxStatements_
				&& !this.prepStmtsInCache_.containsKey(lookupKey)) {
			for (Iterator i = (prepStmtsInCache_.values()).iterator(); i
					.hasNext();) {
				cs = (CachedPreparedStatement) i.next();
				if (SQLMXConnection.sqlStatementsContainingScalarFunctions
						.contains(cs.getLookUpKey())) {
					continue;
				}
				if (!cs.isInUse_() && !this.mfu) {
					list.add(cs);
				} else if (!cs.isInUse_() && this.mfu) {
					Integer weightOfStmtNotInUse = (Integer) this.sqlStringWeight
							.get(cs.getLookUpKey());
					if (weightOfStmtNotInUse != null) {
						/*
						 * If the Weight of Stmt not in use is less than the
						 * Incoming SQL only then try to purge it Else the
						 * Statement in Cache is more used than the incoming SQL
						 * hence dont remove this cached statement.
						 */
						{
							cs
									.setNoOfTimesUsed(weightOfStmtNotInUse
											.intValue());
							list.add(cs);
						}
					}
				}
			}
		}
		cs = null;
		Collections.sort(list, this);
		if (list.size() > 0) {
			key = ((CachedPreparedStatement) list.get(0)).getLookUpKey();
		}
		if (key != null) {
			cs = (CachedPreparedStatement) prepStmtsInCache_.remove(key);
		}
		if (cs != null) {
			//R3.1 changes -- start
			if (((SQLMXConnection) this).props_.t4Logger_.isLoggable(Level.INFO) == true) {
				Object p1[] = T4LoggingUtilities.makeParams(((SQLMXConnection) this).props_,((SQLMXPreparedStatement)cs.getPreparedStatement()).stmtLabel_,((SQLMXConnection) this));
				((SQLMXConnection) this).props_.t4Logger_.logp(Level.INFO,"PreparedStatementManager", "makeRoom",
						"STATEMENT CACHING INFO:STMT REMOVED FROM CACHE", p1);
			}
			//R3.1 changes -- end
			cs.close(true);
			return true;
		} else {
			return false;
		}
	}

	
	void closePreparedStatementsAll() throws SQLException {

		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "closePreparedStatementsAll()");
			}
		}

		Object[] csArray;

		CachedPreparedStatement cs;
		int i = 0;

		csArray = (prepStmtsInCache_.values()).toArray();
		for (i = 0; i < csArray.length; i++) {
			cs = (CachedPreparedStatement) csArray[i];
			if (cs != null) {
				cs.close(false);
			}
		}
		int count = thrownPrepStmts_.size();
		for (i = 0; i < count; i++) {
			cs = (CachedPreparedStatement) thrownPrepStmts_.get(i);
			if (cs != null) {
				cs.close(true);
			}
		}
		thrownPrepStmts_.clear();
	}

	private String createKey(SQLMXConnection connect, String sql,
			int resultSetHoldability) throws SQLException {
		String lookupKey = sql + connect.getCatalog() + connect.getSchema()
				+ connect.getTransactionIsolation() + resultSetHoldability;

		return lookupKey;
	}

	boolean closePreparedStatement(SQLMXConnection connect, String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "closePreparedStatement(" + connect
						+ ",\"" + sql + "\"," + resultSetType + ","
						+ resultSetConcurrency + "," + resultSetHoldability
						+ ")");
			}
		}

		CachedPreparedStatement cs;

		String lookupKey = createKey(connect, sql, resultSetHoldability);

		cs = (CachedPreparedStatement) prepStmtsInCache_.get(lookupKey);
		if (cs != null) {
			cs.inUse_ = false;
			return true;
		}

		return false;
	}

	void clearPreparedStatementsAll() {
		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "clearPreparedStatementsAll()");
			}
		}
		if (prepStmtsInCache_ != null) {
			prepStmtsInCache_.clear();
		}
		count_ = 0;
	}

	void addPreparedStatement(SQLMXConnection connect, String sql,
			PreparedStatement pStmt, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		//R3.1 changes -- start
		if (connect.props_.t4Logger_.isLoggable(Level.INFO) == true) {
			Object p1[] = T4LoggingUtilities.makeParams(connect.props_,((SQLMXStatement)pStmt).stmtLabel_,resultSetType, resultSetConcurrency,
					resultSetHoldability,connect);
			connect.props_.t4Logger_.logp(Level.INFO,"PreparedStatementManager", "addPreparedStatement",
					"STATEMENT CACHING INFO: STMT ADDED TO CACHE", p1);
		}
		//R3.1 changes -- end
		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "addPreparedStatement(" + connect
						+ ",\"" + sql + "\"," + pStmt + "," + resultSetType
						+ "," + resultSetConcurrency + ","
						+ resultSetHoldability + ")");
			}
		}

		CachedPreparedStatement cachedStmt;

		String lookupKey = createKey(connect, sql, resultSetHoldability);

		cachedStmt = (CachedPreparedStatement) prepStmtsInCache_.get(lookupKey);
		if (cachedStmt != null) {
			// Update the last use time
			cachedStmt.setLastUsedInfo();
		} else {
			if (count_ < maxStatements_
					&& !prepStmtsInCache_.contains(lookupKey)) {
				cachedStmt = new CachedPreparedStatement(pStmt, lookupKey, sql);
				prepStmtsInCache_.put(lookupKey, cachedStmt);
				count_++;
				//R3.1 changes -- start
				if (connect.props_.t4Logger_.isLoggable(Level.INFO) == true) {
					Object p2[] = T4LoggingUtilities.makeParams(connect.props_,connect);
					connect.props_.t4Logger_.logp(Level.INFO,"PreparedStatementManager", "addPreparedStatement",
							"STATEMENT CACHING INFO: IN USE "+count_+" out of "+maxStatements_, p2);
				}
				//R3.1 changes -- end
				
			} else {
				if (makeRoom(sql, resultSetHoldability)) {
					cachedStmt = new CachedPreparedStatement(pStmt, lookupKey,
							sql);
					prepStmtsInCache_.put(lookupKey, cachedStmt);
				}
			}
		}
	}

	PreparedStatement getPreparedStatement(SQLMXConnection connect, String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if (out_ != null) {
			if (traceLevel_ != Level.OFF) {
				out_.println(traceId_ + "getPreparedStatement(" + connect
						+ ",\"" + sql + "\"," + resultSetType + ","
						+ resultSetConcurrency + "," + resultSetHoldability
						+ ")");
			}
		}

		PreparedStatement pStmt = null;
		CachedPreparedStatement cachedStmt;

		String lookupKey = createKey(connect, sql, resultSetHoldability);

		if (prepStmtsInCache_ != null) {
			cachedStmt = (CachedPreparedStatement) prepStmtsInCache_
					.get(lookupKey);
			if (cachedStmt != null) {
				if (!cachedStmt.inUse_) {
					cachedStmt.setInUse_(true);
					pStmt = cachedStmt.getPreparedStatement();
					((com.tandem.t4jdbc.SQLMXPreparedStatement) pStmt).reuse(
							connect, resultSetType, resultSetConcurrency,
							resultSetHoldability);
					//R3.1 changes -- start
					if (connect.props_.t4Logger_.isLoggable(Level.INFO) == true) {
						Object p1[] = T4LoggingUtilities.makeParams(connect.props_,((SQLMXStatement)pStmt).stmtLabel_,connect);
						connect.props_.t4Logger_.logp(Level.INFO,"PreparedStatementManager", "getPreparedStatement",
								"STATEMENT CACHING INFO: RETRIVED STATEMENT FROM CACHE", p1);
					}
					//R3.1 changes -- end
				} else {
					pStmt = null;
					//R3.1 changes -- start
					if (connect.props_.t4Logger_.isLoggable(Level.INFO) == true) {
						Object p1[] = T4LoggingUtilities.makeParams(connect.props_,connect);
						connect.props_.t4Logger_.logp(Level.INFO,"PreparedStatementManager", "getPreparedStatement",
								"STATEMENT CACHING INFO: STATEMENT IN USE", p1);
					}
					//R3.1 changes -- end
				}
			}
			else{
			//R3.1 changes -- start
			if (connect.props_.t4Logger_.isLoggable(Level.INFO) == true) {
				Object p1[] = T4LoggingUtilities.makeParams(connect.props_,connect);
				connect.props_.t4Logger_.logp(Level.INFO,"PreparedStatementManager", "getPreparedStatement",
						"STATEMENT CACHING INFO: STATEMENT NOT FOUND IN CACHE", p1);
			}
			//R3.1 changes -- end
			}
		}
		return pStmt;
	}

	void setLogInfo(Level traceLevel, PrintWriter out) {
		this.traceLevel_ = traceLevel;
		this.out_ = out;

	}

	PreparedStatementManager() {
		super();
		String className = getClass().getName();
		traceId_ = "jdbcTrace:[" + Thread.currentThread() + "]:[" + hashCode()
				+ "]:" + className + ".";
		this.sqlStringWeight = new Hashtable();
	}

	PreparedStatementManager(T4Properties t4props) {
		super();

		String className = getClass().getName();

		String tmp;

		if (t4props != null) {
			maxStatements_ = t4props.getMaxStatements();
			if (t4props.getMfuStatementCache().equals("YES")) {
				this.setMfu(true);
			} else {
				this.setMfu(false);
			}

		}
		if (maxStatements_ > 0) {
			prepStmtsInCache_ = new Hashtable();
			thrownPrepStmts_ = new ArrayList();
		}
		this.sqlStringWeight = new Hashtable();
		traceId_ = "jdbcTrace:[" + Thread.currentThread() + "]:[" + hashCode()
				+ "]:" + className + ".";
	}

	public void setMfu(boolean mfcEnabled) {
		this.mfu = mfcEnabled;
	}
	public boolean isMfu() {
		return mfu;
	}
	public void setWeight(String sqlString, int resultSetHoldability)
			throws SQLException {
		String lookupKey = this.createKey(((SQLMXConnection) this), sqlString,
				resultSetHoldability);
		int weight = 1;
		if (this.sqlStringWeight.containsKey(lookupKey)) {
			Integer currentWeight = (Integer) this.sqlStringWeight
					.get(lookupKey);
			weight = currentWeight.intValue() + 1;
			this.sqlStringWeight.remove(lookupKey);
		}
		//commented this block since its not required as scalar func. also mfc'd
//		if (SQLMXConnection.sqlStatementsContainingScalarFunctions
//				.contains(sqlString)) {
//			weight = Integer.MAX_VALUE;
//		}
		this.sqlStringWeight.put(lookupKey, new Integer(Math.abs(weight)));
	}
	// for sol 10-080909-5718 prepStmtsInCache_ has been modified to protected -
	// R3.0
	protected Hashtable prepStmtsInCache_;

	protected Hashtable sqlStringWeight;
	private ArrayList thrownPrepStmts_;
	private int maxStatements_;

	private int count_;

	private boolean mfu;
	Level traceLevel_;

	PrintWriter out_;

	String traceId_;
}
