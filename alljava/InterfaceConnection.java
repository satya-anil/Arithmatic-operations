/**************************************************************************
// @@@ START COPYRIGHT @@@
//
//  (C) Copyright 2003, 2004, 2005, 2015-2016 Hewlett Packard Enterprise Development LP.
//
// @@@ END COPYRIGHT @@@
**************************************************************************/

package com.tandem.t4jdbc;

import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InterfaceConnection {
	private String pwd;

	private int txnIsolationLevel = Connection.TRANSACTION_READ_COMMITTED;

	private boolean autoCommit = true;

	private boolean isReadOnly = false;

	private boolean isClosed_;

	private long txid;

	private Locale locale;

	private USER_DESC_def userDesc;

	private CONNECTION_CONTEXT_def inContext;

	private OUT_CONNECTION_CONTEXT_def outContext;

	private boolean useArrayBinding_;

	private short transportBufferSize_;

	Handler t4FileHandler;

	 MXCSAddress mxcsAddr_;

	private T4Connection t4connection_;

	private String m_mxcsSrvr_ref;

	private int dialogueId_;
	
	private String connectionID_;
	
	private String serverID_;

	private String m_sessionName;

	// character set information
	private int isoMapping_ = 1;

	private int termCharset_ = 15;

	private boolean enforceISO = false;

	private boolean wmsMode_ = false;

	private boolean byteSwap = false;

	private String _serverDataSource;

	T4Properties t4props_;

	//Modification for L36 Corda : start
	private Pattern namePat_ = Pattern.compile("(\"(([^\"]|\"\")+)\")");
	private Pattern catalogPat_ =Pattern.compile("((\"([^\"]|\"\")*\")|.+)(\\.)");
	String catalog_=null;
	String schema_=null;
	int netTimeout=0;
	//End
	SQLWarning sqlwarning_;

	Hashtable encoders = new Hashtable(11);

	Hashtable decoders = new Hashtable(11);

	// static fields from odbc_common.h and sql.h
	static final int SQL_TXN_READ_UNCOMMITTED = 1;

	static final int SQL_TXN_READ_COMMITTED = 2;

	static final int SQL_TXN_REPEATABLE_READ = 4;

	static final int SQL_TXN_SERIALIZABLE = 8;

	static final short SQL_ATTR_CURRENT_CATALOG = 109;
	//Modification for L36 Corda : start
	static final short SET_SCHEMA = 1001;
	//End
	// 10-060905-8814 - AM
	static final short SQL_ATTR_ACCESS_MODE = 101;

	static final short SQL_ATTR_AUTOCOMMIT = 102;

	static final short SQL_TXN_ISOLATION = 108;

	// spj proxy syntax support
	static final short SPJ_ENABLE_PROXY = 1040;

	static final int ROWWISE_ROWSET = 134217728; // (2^27);

	static final int CHARSET = 268435456; // (2^28)

	static final int STREAMING_DELAYEDERROR_MODE = 536870912; // 2^29

	static final short SQL_COMMIT = 0;

	static final short SQL_ROLLBACK = 1;

	// Zbig added new attribute on 4/18/2005
	static final short JDBC_ATTR_CONN_IDLE_TIMEOUT = 3000;

	// for handling WeakReferences
	static ReferenceQueue refQ_ = new ReferenceQueue();

	static Hashtable refTosrvrCtxHandle_ = new Hashtable();

	private String _roleName = "";

	private boolean _ignoreCancel;

	InterfaceConnection(T4Properties t4props) throws SQLException {
		t4props_ = t4props;

		// close any weak connections that need to be closed.
		gcConnections();

		if (t4props.getSQLException() != null) {
			throw SQLMXMessages.createSQLException(t4props_,
					t4props.getLocale(), "invalid_property", t4props
							.getSQLException());
		}

		m_sessionName = t4props_.getSessionName();

		if (m_sessionName != null && m_sessionName.length() > 0) {
			if (m_sessionName.length() > 24)
				m_sessionName = m_sessionName.substring(0, 24);

			if (!m_sessionName.matches("\\w+"))
				throw new SQLException(
						"Invalid sessionName.  Session names can only contain alphnumeric characters.");
		}

		//Modification for L36 Corda : start
		this.netTimeout=t4props.getNetworkTimeout()*1000;
		//End		
		pwd = t4props.getPassword();
		locale = t4props.getLocale();
		txid = 0;
		isClosed_ = false;
		useArrayBinding_ = t4props.getUseArrayBinding();
		// transportBufferSize_ = t4props.getTransportBufferSize();
		transportBufferSize_ = 32000;

		userDesc = getUserDescription(t4props.getUser(), t4props.getPassword());

		// Connection context details
		inContext = getInContext(t4props);
		m_mxcsSrvr_ref = t4props.getUrl();
		connectionID_ = t4props.getConnectionID();
		serverID_=null;
		_ignoreCancel = false;

		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(t4props_, this);
			String temp = "url is = " + t4props.getUrl();
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection", "",
					temp, p);
			p = T4LoggingUtilities.makeParams(t4props_, this);
			temp = "user is = " + userDesc.userName;
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection", "",
					temp, p);
		}
		sqlwarning_ = null;
		connect();
	}

	public boolean isClosed() {
		return this.isClosed_;
	}

	String getRoleName() {
		return this._roleName;
	}

	CONNECTION_CONTEXT_def getInContext() {
		return inContext;
	}

	private CONNECTION_CONTEXT_def getInContext(T4Properties t4props) {
		inContext = new CONNECTION_CONTEXT_def();
		inContext.catalog = t4props.getCatalog();
		inContext.schema = t4props.getSchema();
		inContext.datasource = t4props.getServerDataSource();
		inContext.userRole = t4props.getRoleName();
		inContext.cpuToUse = t4props.getCpuToUse();
		inContext.cpuToUseEnd = -1; // for future use by DBTransporter

		inContext.accessMode = (short) (isReadOnly ? 1 : 0);
		//RFE autoCommit
		String tempAutoCommit=null;
		tempAutoCommit=t4props.getAutoCommit();
		if (tempAutoCommit != null) {
			if (tempAutoCommit.equalsIgnoreCase("ON")) {
				this.autoCommit = true;
			} else if (tempAutoCommit.equalsIgnoreCase("OFF")) {
				this.autoCommit = false;
			}
		}
		inContext.autoCommit = (short) (autoCommit ? 1 : 0);

		inContext.queryTimeoutSec = t4props.getQueryTimeout();
		inContext.idleTimeoutSec = t4props.getConnectionTimeout(); // changes for Soln: 10-160122-8724 
		inContext.loginTimeoutSec = (short) t4props.getLoginTimeout();
		inContext.txnIsolationLevel = (short) SQL_TXN_READ_COMMITTED;
		inContext.rowSetSize = t4props.getFetchBufferSize();
		inContext.diagnosticFlag = 0;
		inContext.processId = (int) System.currentTimeMillis() & 0xFFF;

		try {
			inContext.computerName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uex) {
			inContext.computerName = "Unknown Client Host";
		}
		inContext.windowText = t4props.getApplicationName();

		inContext.ctxDataLang = 15;
		inContext.ctxErrorLang = 15;

		inContext.ctxACP = 1252;
		inContext.ctxCtrlInferNXHAR = -1;
		inContext.clientVersionList.list = getVersion(inContext.processId);
		return inContext;
	}

	private VERSION_def[] getVersion(int pid) {

		int buildId = 0;

		VERSION_def version[] = new VERSION_def[2];

		// Entry [0] is the Driver Version information
		version[0] = new VERSION_def();
		version[0].componentId = 20;
		version[0].majorVersion = SQLMXDatabaseMetaData.JDBC_DRIVER_MAJOR_VERSION; //Added for SQL/MX3.5
		version[0].minorVersion = SQLMXDatabaseMetaData.JDBC_DRIVER_MINOR_VERSION; //Added for SQL/MX3.5
		version[0].buildId = buildId | ROWWISE_ROWSET | CHARSET;

		if (this.t4props_.getDelayedErrorMode()) {
			version[0].buildId |= STREAMING_DELAYEDERROR_MODE;
		}

		// Entry [1] is the Application Version information
		version[1] = new VERSION_def();
		version[1].componentId = 8;
		version[1].majorVersion = 3;
		version[1].minorVersion = 0;
		version[1].buildId = 0;

		return version;
	}

	USER_DESC_def getUserDescription() {
		return userDesc;
	}

	private void setISOMapping(int isoMapping) {
		if (InterfaceUtilities.getCharsetName(isoMapping) == InterfaceUtilities.SQLCHARSET_UNKNOWN)
			isoMapping = InterfaceUtilities.getCharsetValue("ISO8859_1");

		isoMapping_ = isoMapping;
	}

	String getServerDataSource() {
		return this._serverDataSource;
	}

	boolean getEnforceISO() {
		return enforceISO;
	}

	int getISOMapping() {
		return isoMapping_;
	}

	public String getSessionName() {
		return m_sessionName;
	}

	private void setTerminalCharset(int termCharset) {
		if (InterfaceUtilities.getCharsetName(termCharset) == InterfaceUtilities.SQLCHARSET_UNKNOWN)
			termCharset = InterfaceUtilities.getCharsetValue("ISO8859_1");

		termCharset_ = termCharset;
	}

	int getTerminalCharset() {
		return termCharset_;
	}

	void setWmsMode(boolean value) {
		wmsMode_ = value;
	}

	boolean getWmsMode() {
		return wmsMode_;
	}

	private USER_DESC_def getUserDescription(String user, String pwd)
			throws SQLException {
		userDesc = new USER_DESC_def();
		userDesc.userDescType = (this.t4props_.getSessionToken()) ? TRANSPORT.PASSWORD_ENCRYPTED_USER_TYPE
				: TRANSPORT.UNAUTHENTICATED_USER_TYPE;
		userDesc.userName = (user.length() > 128) ? user.substring(0, 128)
				: user;
		userDesc.domainName = "";

		userDesc.userSid = null;

		if (pwd.length() > 386)
			pwd = pwd.substring(0, 386);

		byte authentication[];
		try {
			authentication = pwd.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException uex) {
			throw SQLMXMessages.createSQLException(t4props_, locale, uex
					.getMessage(), null);
		}

		if (authentication.length > 0) {
			Utility.Encryption(authentication, authentication,
					authentication.length);
		}

		userDesc.password = authentication;

		return userDesc;
	}

	T4Connection getT4Connection() {
		return t4connection_;
	}

	int getDialogueId() {
		return dialogueId_;
	}
	
	
	/**
	 * @return the connectionID_
	 */
	public String getConnectionID() {
		return connectionID_;
	}

	/**
	 * @param connectionID_ the connectionID_ to set
	 */
	public void setConnectionID(String connectionID_) {
		this.connectionID_ = connectionID_;
	}

	//Added for Solution 10-160301-9171	
	String getServerID(){		
		return serverID_;
	}

	int getQueryTimeout() {
		return inContext.queryTimeoutSec;
	}

	int getLoginTimeout() {
		return inContext.loginTimeoutSec;
	}

	int getConnectionTimeout() {
		return inContext.idleTimeoutSec;
	}

	String getCatalog() {
		if (outContext != null) {
			return outContext.catalog;
		} else {
			return inContext.catalog;
		}
	}

	//Modification for L36 Corda : start
	int getNetworkTimeout(){
		return this.netTimeout;
	}
	//End
	boolean getDateConversion() {
		return ((outContext.versionList.list[0].buildId & 512) > 0);
	}

	int getServerMajorVersion() {
		return outContext.versionList.list[0].majorVersion;
	}

	int getServerMinorVersion() {
		return outContext.versionList.list[0].minorVersion;
	}

	String getUid() {
		return userDesc.userName;
	}
//Soln 10-120718-0505
	String getSchema() {
		if (outContext != null) {
			return outContext.schema;
		} else {
			return inContext.schema;
		}
	}

	//Modification for L36 Corda : start
	void setNetworkTimeout(int Networktimeout){
		
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(this.t4props_, Networktimeout,this);
			String temp = "Setting connection Networktimeout = " + Networktimeout;
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setNetworkTimeout", temp, p);
		}
		
		this.netTimeout=Networktimeout;
		
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(this.t4props_, Networktimeout,this);
			String temp = "Setting connection Networktimeout = " + Networktimeout
					+ " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setNetworkTimeout", temp, p);
		}
	}
	//End
	void setLocale(Locale locale) {
		this.locale = locale;
	}

	Locale getLocale() {
		return locale;
	}

	boolean getByteSwap() {
		return this.byteSwap;
	}

	MXCSAddress getMXCSAddress() {
		return mxcsAddr_;
	}

	void cancel() throws SQLException {
		if (!this._ignoreCancel) {
			String srvrObjRef = "" + mxcsAddr_.getPort();
			// String srvrObjRef = t4props_.getServerID();
			int srvrType = 2; // AS server
			CancelReply cr_ = null;

			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
				String temp = "cancel request received for " + srvrObjRef;
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"connect", temp, p);
			}

			//
			// Send the cancel to the ODBC association server.
			//
			String errorText = null;
			int tryNum = 0;
			String errorMsg = null;
			String errorMsg_detail = null;
			long currentTime = (new java.util.Date()).getTime();
			long endTime;

			if (inContext.loginTimeoutSec > 0) {
				endTime = currentTime + inContext.loginTimeoutSec * 1000;
			} else {

				// less than or equal to 0 implies infinit time out
				endTime = Long.MAX_VALUE;

				//
				// Keep trying to contact the Association Server until we run
				// out of
				// time, or make a connection or we exceed the retry count.
				//
			}
			//Changed for sol. 10-100818-2570 -R3.0
	//		cr_ = SQLMX_AssociationServer_Cancel.cancel(t4props_, this,
	//				dialogueId_, srvrType, srvrObjRef, 0);

			cr_ = SQLMX_AssociationServer_Cancel.cancel(t4props_, this,
					dialogueId_, srvrType, this.mxcsAddr_.m_url + "", 0);

			switch (cr_.m_p1_exception.exception_nr) {
			case TRANSPORT.CEE_SUCCESS:
				if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
					Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
					String temp = "Cancel successful";
					t4props_.t4Logger_.logp(Level.FINEST,
							"InterfaceConnection", "connect", temp, p);
				}
				break;
			default:

				//
				// Some unknown error
				//
				if (cr_.m_p1_exception.clientErrorText != null) {
					errorText = "Client Error text = "
							+ cr_.m_p1_exception.clientErrorText;
				}
				errorText = errorText + "  :Exception = "
						+ cr_.m_p1_exception.exception_nr;
				errorText = errorText + "  :" + "Exception detail = "
						+ cr_.m_p1_exception.exception_detail;
				errorText = errorText + "  :" + "Error code = "
						+ cr_.m_p1_exception.errorCode;

				if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
					Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
					String temp = errorText;
					t4props_.t4Logger_.logp(Level.FINEST,
							"InterfaceConnection", "cancel", temp, p);
				}
				throw SQLMXMessages.createSQLException(t4props_, locale,
						"as_cancel_message_error", errorText);
			} // end switch

			currentTime = (new java.util.Date()).getTime();
		}
	}

	private void connect() throws SQLException {
//		R3.1 changes, to behave like V20
//		short retryCount = 3;
		short retryCount = (short) (Short.MAX_VALUE - 1); // This is not configurable yet, so retry a lot
		int srvrType = 2; // AS server
		ConnectReply cr_ = null;
		InitializeDialogueReply idr_ = null;

		if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
			String msg = "Association Server URL: " + m_mxcsSrvr_ref;
			Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
			t4props_.t4Logger_.logp(Level.INFO, "InterfaceConnection",
					"connect", msg, p);
		}

		//
		// Connect to the association server.
		//
		String errorText = null;
		boolean done = false;
		int tryNum = 0;
		String errorMsg = null;
		String errorMsg_detail = null;
		long currentTime = System.currentTimeMillis();
		long endTime = (inContext.loginTimeoutSec > 0) ? currentTime
				+ inContext.loginTimeoutSec * 1000 : Long.MAX_VALUE;

		do {
			if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
				String temp = "Attempting getObjRef.  Try " + (tryNum + 1)
						+ " of " + retryCount;
				Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
				t4props_.t4Logger_.logp(Level.INFO, "InterfaceConnection",
						"connect", temp, p);
			}

			cr_ = SQLMX_AssociationServer_Connect.getConnection(t4props_, this,
					inContext, userDesc, srvrType, retryCount);

			switch (cr_.m_p1_exception.exception_nr) {
			case TRANSPORT.CEE_SUCCESS:
				done = true;
				if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
					String msg = "getObjRef Successful.  Server URL: "
							+ cr_.m_p2_srvrObjRef;
					Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
					t4props_.t4Logger_.logp(Level.INFO, "InterfaceConnection",
							"connect", msg, p);
				}
				if (!cr_.m_p4_dataSource.equals(t4props_.getServerDataSource())) {
					Object[] messageArguments = new Object[1];
					messageArguments[0] = cr_.m_p4_dataSource;
					sqlwarning_ = SQLMXMessages.createSQLWarning(t4props_,
							"connected_to_Default_DS", messageArguments);
				}
				break;
			case odbcas_ASSvc_GetObjRefHdl_exc_.odbcas_ASSvc_GetObjRefHdl_ASTryAgain_exn_:
				done = false;
				tryNum = tryNum + 1;
				errorMsg = "as_connect_message_error";
				errorMsg_detail = "try again request";
//				R3.1 changes, to behave like V20
				/*
				if (tryNum < retryCount) {
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
				}
				*/
				break;
			case odbcas_ASSvc_GetObjRefHdl_exc_.odbcas_ASSvc_GetObjRefHdl_ASNotAvailable_exn_:
				done = false;
				tryNum = tryNum + 1;
				errorMsg = "as_connect_message_error";
				errorMsg_detail = "association server not available";
				break;
			case odbcas_ASSvc_GetObjRefHdl_exc_.odbcas_ASSvc_GetObjRefHdl_DSNotAvailable_exn_:
				done = false;
				tryNum = tryNum + 1;
				errorMsg = "as_connect_message_error";
				errorMsg_detail = "data source not available";
				break;
			case odbcas_ASSvc_GetObjRefHdl_exc_.odbcas_ASSvc_GetObjRefHdl_PortNotAvailable_exn_:
				done = false;
				tryNum = tryNum + 1;
				errorMsg = "as_connect_message_error";
				errorMsg_detail = "port not available";
				break;
			case odbcas_ASSvc_GetObjRefHdl_exc_.odbcas_ASSvc_GetObjRefHdl_ASNoSrvrHdl_exn_:
				done = false;
				tryNum = tryNum + 1;
				errorMsg = "as_connect_message_error";
				errorMsg_detail = "server handle not available";
				break;
			default:

				//
				// Some unknown error
				//
				if (cr_.m_p1_exception.clientErrorText != null) {
					errorText = "Client Error text = "
							+ cr_.m_p1_exception.clientErrorText;

				}
				errorText = errorText + "  :Exception = "
						+ cr_.m_p1_exception.exception_nr;
				errorText = errorText + "  :" + "Exception detail = "
						+ cr_.m_p1_exception.exception_detail;
				errorText = errorText + "  :" + "Error code = "
						+ cr_.m_p1_exception.errorCode;

				if (cr_.m_p1_exception.ErrorText != null) {
					errorText = errorText + "  :" + "Error text = "
							+ cr_.m_p1_exception.ErrorText;

				}
				throw SQLMXMessages.createSQLException(t4props_, locale,
						"as_connect_message_error", errorText);
			}

			if (done == false && t4props_.t4Logger_.isLoggable(Level.INFO)) {
				String msg = "getObjRef Failed. Message from Association Server: "
						+ errorMsg_detail;
				t4props_.t4Logger_.logp(Level.INFO, "InterfaceConnection",
						"connect", msg, t4props_);
			}

			currentTime = System.currentTimeMillis();
		} while (done == false && endTime > currentTime && tryNum < retryCount);

		if (done == false) {
			SQLException se1;
			SQLException se2;

			if (currentTime >= endTime) {
				se1 = SQLMXMessages.createSQLException(t4props_, locale,
						"ids_s1_t00", null);
				se2 = SQLMXMessages.createSQLException(t4props_, locale,
						errorMsg, errorMsg_detail);
				se1.setNextException(se2);
			} else {
				se1 = SQLMXMessages.createSQLException(t4props_, locale,
						errorMsg, errorMsg_detail);
			}

			throw se1;
		}

		//
		// Connect to the MXOSRVR server created by the association server.
		//
		dialogueId_ = cr_.m_p3_dialogueId;
		m_mxcsSrvr_ref = cr_.m_p2_srvrObjRef;
		serverID_=cr_.m_p2_srvrObjRef;
		mxcsAddr_ = cr_.getMXCSAddress();
		this.byteSwap = cr_.byteSwap;
		this._serverDataSource = cr_.m_p4_dataSource;

		setISOMapping(cr_.isoMapping);

		if (cr_.isoMapping == InterfaceUtilities.getCharsetValue("ISO8859_1")) {
			setTerminalCharset(InterfaceUtilities.getCharsetValue("ISO8859_1"));
			this.inContext.ctxDataLang = 0;
			this.inContext.ctxErrorLang = 0;
		} else {
			setTerminalCharset(InterfaceUtilities.getCharsetValue("UTF-8"));
		}

		endTime = (inContext.loginTimeoutSec > 0) ? currentTime
				+ inContext.loginTimeoutSec * 1000 : Long.MAX_VALUE;
		tryNum = 0;
		done = false;

		boolean socketException = false;
		SQLException seSave = null;

		do {
			if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
				String temp = "Attempting initDiag.  Try " + (tryNum + 1)
						+ " of " + retryCount;
				Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
				t4props_.t4Logger_.logp(Level.INFO, "InterfaceConnection",
						"connect", temp, p);
			}

			socketException = false;
			try {
				t4connection_ = new T4Connection(this);
				idr_ = t4connection_.InitializeDialogue();
			} catch (SQLException se) {
				//
				// We will retry socket exceptions, but will fail on all other
				// exceptions.
				//
				int sc = se.getErrorCode();
				int s1 = SQLMXMessages.createSQLException(t4props_, locale,
						"socket_open_error", null).getErrorCode();
				int s2 = SQLMXMessages.createSQLException(t4props_, locale,
						"socket_write_error", null).getErrorCode();
				int s3 = SQLMXMessages.createSQLException(t4props_, locale,
						"socket_read_error", null).getErrorCode();

				if (sc == s1 || sc == s2 || sc == s3) {
					if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
						String temp = "A socket exception occured: "
								+ se.getMessage();
						Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
						t4props_.t4Logger_.logp(Level.INFO,
								"InterfaceConnection", "connect", temp,
								p);
					}

					socketException = true;
					seSave = se;
				} else {
					if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
						String temp = "A non-socket fatal exception occured: "
								+ se.getMessage();
						Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
						t4props_.t4Logger_.logp(Level.INFO,
								"InterfaceConnection", "connect", temp,
								p);
					}

					try {
						t4connection_.getInputOutput().CloseIO(
								new LogicalByteArray(1, 0, false));
					} catch (Exception e) {
						// ignore error
					}
					throw se;
				}
			}

			if (socketException == false) {
				if (idr_.m_p1_exception.exception_nr == TRANSPORT.CEE_SUCCESS) {
					done = true;
					if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
						String temp = "initDiag Successful.";
						Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
						t4props_.t4Logger_.logp(Level.INFO,
								"InterfaceConnection", "connect", temp,
								p);
					}
				} else if (idr_.m_p1_exception.exception_nr == odbc_SQLSvc_InitializeDialogue_exc_.odbc_SQLSvc_InitializeDialogue_SQLError_exn_) {
					if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
						String temp = "A SQL Warning or Error occured during initDiag: "
								+ idr_.m_p1_exception.SQLError;
						Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
						t4props_.t4Logger_.logp(Level.INFO,
								"InterfaceConnection", "connect", temp,
								p);
					}

					int ex_nr = idr_.m_p1_exception.exception_nr;
					int ex_nr_d = idr_.m_p1_exception.exception_detail;

					if (ex_nr_d == odbc_SQLSvc_InitializeDialogue_exc_.SQL_PASSWORD_EXPIRING
							|| ex_nr_d == odbc_SQLSvc_InitializeDialogue_exc_.SQL_PASSWORD_GRACEPERIOD) {
						done = true;
					} else {
						SQLMXMessages.throwSQLException(t4props_,
								idr_.m_p1_exception.SQLError);
					}
				}
			}

			currentTime = System.currentTimeMillis();
			tryNum = tryNum + 1;
		} while (done == false && endTime > currentTime && tryNum < retryCount);

		if (done == false) {
			SQLException se1;
			SQLException se2;

			if (socketException == true) {
				throw seSave;
			}

			if (currentTime >= endTime) {
				se1 = SQLMXMessages.createSQLException(t4props_, locale,
						"ids_s1_t00", null);
			} else if (tryNum >= retryCount) {
				se1 = SQLMXMessages.createSQLException(t4props_, locale,
						"as_connect_message_error", "exceeded retry count");
			} else {
				se1 = SQLMXMessages.createSQLException(t4props_, locale,
						"as_connect_message_error", null);
			}
			throw se1;
		}

		//
		// Set the outcontext value returned by the ODBC MX server in the
		// serverContext
		//
		outContext = idr_.m_p2_outContext;
		//Modification for L36 Corda : start
		this.catalog_ = outContext.catalog;
		this.schema_ = outContext.schema;
		//End
		enforceISO = outContext._enforceISO;
		this._roleName = outContext._roleName;
		this._ignoreCancel = outContext._ignoreCancel;

		t4props_.setDialogueID(Integer.toString(dialogueId_));
		t4props_.setServerID(m_mxcsSrvr_ref);

		t4props_
				.setMxcsMajorVersion(idr_.m_p2_outContext.versionList.list[0].majorVersion);
		t4props_
				.setMxcsMinorVersion(idr_.m_p2_outContext.versionList.list[0].minorVersion);
		t4props_
				.setSqlmxMajorVersion(idr_.m_p2_outContext.versionList.list[1].majorVersion);
		t4props_
				.setSqlmxMinorVersion(idr_.m_p2_outContext.versionList.list[1].minorVersion);

		if (t4props_.t4Logger_.isLoggable(Level.INFO)) {
			String temp = "Connection process successful";
			Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
			t4props_.t4Logger_.logp(Level.INFO, "InterfaceConnection",
					"connect", temp, p);
		}
	}

	// @deprecated
	void isConnectionClosed() throws SQLException {
		if (isClosed_ == false) {
			throw SQLMXMessages.createSQLException(t4props_, locale,
					"invalid_connection", null);
		}
	}

	// @deprecated
	void isConnectionOpen() throws SQLException {
		if (isClosed_) {
			throw SQLMXMessages.createSQLException(t4props_, locale,
					"invalid_connection", null);
		}
	}

	// @deprecated
	boolean getIsClosed() {
		return isClosed_;
	}

	void setIsClosed(boolean isClosed) {
		this.isClosed_ = isClosed;
	}

	String getUrl() {
		return m_mxcsSrvr_ref;
	}

	void setCatalog(SQLMXConnection conn, String catalog) throws SQLException {
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, catalog,conn);
			String temp = "Setting connection catalog = " + catalog;
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setCatalog", temp, p);
		}
		// Default catalog SQL/MX - AM 6/28/2006
		if (catalog != null && catalog.length() == 0) {
			catalog = T4Properties.DEFAULT_CATALOG;
		}
		setConnectionAttr(conn, SQL_ATTR_CURRENT_CATALOG, 0, catalog);
		outContext.catalog = catalog;
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, catalog,conn);
			String temp = "Setting connection catalog = " + catalog
					+ " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setCatalog", temp, p);
		}
	};
	
	//Modification for L36 Corda : start
	
	private String getCatalogToken(String schemaStr)
	{
		Matcher mat=catalogPat_.matcher(schemaStr);
		if (mat.find()) {
			String temp = mat.group().substring(0, mat.group().length() - 1);
			if (temp.startsWith("\"")) {
				if (!namePat_.matcher(schemaStr).matches())
					return (temp);
				else
					return null;
			} else
				return (temp.toUpperCase());
		} else {
			return null;
		}
	   
	}
	
	void setSchema(SQLMXConnection conn, String schema) throws SQLException {
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, schema,conn);
			String temp = "Setting connection schema = " + schema;
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setSchema", temp, p);
		}
		// Default schema SQL/MX - AM 6/28/2006
		if (schema != null && schema.length() == 0) {
			schema = T4Properties.DEFAULT_SCHEMA;
		}
		else if (schema != null) {
			if (schema.contains(".")) {  // To split catalog & schema separately, though user gives as cat.sch
				String catalogName = ""; // Soln 10-120917-4742
				catalogName = getCatalogToken(schema);
				if (catalogName != null) {
					setCatalog(conn, catalogName);
					schema = schema.substring(catalogName.length() + 1);
				}
			}
			if (!schema.startsWith("\"")){
				schema = schema.toUpperCase();
			}
			
		}
		
		setConnectionAttr(conn, SET_SCHEMA, 0, schema);
		outContext.schema = schema;
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, schema,conn);
			String temp = "Setting connection schema = " + schema
					+ " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setSchema", temp, p);
		}
	};
	//End

	// enforces the connection timeout set by the user
	// to be called by the connection pooling mechanism whenever a connection is
	// given to the user from the pool
	void enforceT4ConnectionTimeout(SQLMXConnection conn) throws SQLException {
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_,
					(short) t4props_.getConnectionTimeout(),conn);
			String temp = "Enforcing connection timeout = "
					+ (short) t4props_.getConnectionTimeout();
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"enforceT4ConnectionTimeout", temp, p);
		}
		inContext.idleTimeoutSec = (short) t4props_.getConnectionTimeout();
		setConnectionAttr(conn, JDBC_ATTR_CONN_IDLE_TIMEOUT,
				inContext.idleTimeoutSec, String
						.valueOf(inContext.idleTimeoutSec));
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_,
					(short) t4props_.getConnectionTimeout(),conn);
			String temp = "Enforcing connection timeout = "
					+ (short) t4props_.getConnectionTimeout() + " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"enforceT4ConnectionTimeout", temp, p);
		}
	};

	// disregards the T4's connectionTimeout value (set during initialize
	// dialog) and
	// enforces the connection timeout set by the NCS datasource settings
	// to be called by the connection pooling mechanism whenever a connection is
	// put into the pool (after a user has called connection.close())
	void disregardT4ConnectionTimeout(SQLMXConnection conn) throws SQLException {
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, "-1",conn);
			String temp = "Setting connection timeout = -1";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"disregardT4ConnectionTimeout", temp, p);
		}
		setConnectionAttr(conn, JDBC_ATTR_CONN_IDLE_TIMEOUT, -1, "-1");
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, "-1",conn);
			String temp = "Setting connection timeout = -1 is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"disregardT4ConnectionTimeout", temp, p);
		}
	};

	void setConnectionAttr(SQLMXConnection conn, short attr, int valueNum,
			String valueString) throws SQLException {
		SetConnectionOptionReply scr_;
		isConnectionOpen();

		try {
			scr_ = t4connection_.SetConnectionOption(attr, valueNum,
					valueString);
		} catch (SQLException tex) {
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(conn.props_, attr,
						valueNum, valueString,conn);
				String temp = "MXCS or SQLException occured.";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"setConnectionAttr", temp, p);
			}
			throw tex;
		}

		switch (scr_.m_p1.exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
		case TRANSPORT.SQL_SUCCESS_WITH_INFO: //Added for solution 10-140520-1941
			// do the warning processing
			if (scr_.m_p2.length != 0) {
				SQLMXMessages.setSQLWarning(conn.props_, conn, scr_.m_p2);
			}
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(conn.props_, attr,
						valueNum, valueString,conn);
				String temp = "Setting connection attribute is done.";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"setConnectionAttr", temp, p);
			}
			break;
		case odbc_SQLSvc_SetConnectionOption_exc_.odbc_SQLSvc_SetConnectionOption_SQLError_exn_:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(conn.props_, attr,
						valueNum, valueString,conn);
				String temp = "odbc_SQLSvc_SetConnectionOption_SQLError_exn_ occured.";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"setConnectionAttr", temp, p);
			}
			SQLMXMessages.throwSQLException(t4props_, scr_.m_p1.errorList);
		default:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(conn.props_, attr,
						valueNum, valueString,conn);
				String temp = "UnknownException occured.";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"setConnectionAttr", temp, p);
			}
			throw SQLMXMessages.createSQLException(conn.props_, locale,
					"ids_unknown_reply_error", null);
		}
	};

	void setTransactionIsolation(SQLMXConnection conn, int level)
			throws SQLException {
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, level,conn);
			String temp = "Setting transaction isolation = " + level;
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setTransactionIsolation", temp, p);
		}
		isConnectionOpen();

		if (level != Connection.TRANSACTION_NONE
				&& level != Connection.TRANSACTION_READ_COMMITTED
				&& level != Connection.TRANSACTION_READ_UNCOMMITTED
				&& level != Connection.TRANSACTION_REPEATABLE_READ
				&& level != Connection.TRANSACTION_SERIALIZABLE) {
			throw SQLMXMessages.createSQLException(conn.props_, locale,
					"invalid_transaction_isolation", null);
		}

		txnIsolationLevel = level;

		switch (txnIsolationLevel) {
		case Connection.TRANSACTION_NONE:
			inContext.txnIsolationLevel = (short) SQL_TXN_READ_COMMITTED;
			break;
		case Connection.TRANSACTION_READ_COMMITTED:
			inContext.txnIsolationLevel = (short) SQL_TXN_READ_COMMITTED;
			break;
		case Connection.TRANSACTION_READ_UNCOMMITTED:
			inContext.txnIsolationLevel = (short) SQL_TXN_READ_UNCOMMITTED;
			break;
		case Connection.TRANSACTION_REPEATABLE_READ:
			inContext.txnIsolationLevel = (short) SQL_TXN_REPEATABLE_READ;
			break;
		case Connection.TRANSACTION_SERIALIZABLE:
			inContext.txnIsolationLevel = (short) SQL_TXN_SERIALIZABLE;
			break;
		default:
			inContext.txnIsolationLevel = (short) SQL_TXN_READ_COMMITTED;
			break;
		}

		setConnectionAttr(conn, SQL_TXN_ISOLATION, inContext.txnIsolationLevel,
				String.valueOf(inContext.txnIsolationLevel));
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, level,conn);
			String temp = "Setting transaction isolation = " + level
					+ " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setTransactionIsolation", temp, p);
		}
	};

	int getTransactionIsolation() throws SQLException {
		return txnIsolationLevel;
	}

	long getTxid() {
		return txid;
	}

	void setTxid(long txid) {
		this.txid = txid;
	}

	boolean getAutoCommit() {
		return autoCommit;
	}

	void setAutoCommit(SQLMXConnection conn, boolean autoCommit)
			throws SQLException {
		isConnectionOpen();

		// SOL-10-060915-9077
		boolean commit = this.autoCommit;

		this.autoCommit = autoCommit;

		if (autoCommit == false) {
			inContext.autoCommit = 0;
		} else {
			inContext.autoCommit = 1;

		}

		// SOL-10-060915-9077
		try {
			setConnectionAttr(conn, SQL_ATTR_AUTOCOMMIT, inContext.autoCommit,
					String.valueOf(inContext.autoCommit));
		} catch (SQLException sqle) {
			this.autoCommit = commit;
			throw sqle;
		}
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, autoCommit,conn);
			String temp = "Setting autoCommit = " + autoCommit + " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"setAutoCommit", temp, p);
		}
	}

	void enableNARSupport(SQLMXConnection conn, boolean NARSupport)
			throws SQLException {
		int val = NARSupport ? 1 : 0;
		setConnectionAttr(conn, TRANSPORT.SQL_ATTR_ROWSET_RECOVERY, val, String
				.valueOf(val));
	}

	void enableProxySyntax(SQLMXConnection conn) throws SQLException {
		setConnectionAttr(conn, InterfaceConnection.SPJ_ENABLE_PROXY, 1, "1");
	}

	boolean isReadOnly() {
		return isReadOnly;
	}

	void setReadOnly(boolean readOnly) throws SQLException {
		isConnectionOpen();
		this.isReadOnly = readOnly;
	}

	// 10-060905-8814 - AM
	void setReadOnly(SQLMXConnection conn, boolean readOnly) throws SQLException {
		//R321 changes for solution 10-121025-5199
		if (this.isReadOnly == readOnly) {
			return;
		}
		
		isConnectionOpen();	
		
		this.isReadOnly = readOnly;
		if (readOnly == false) {
			inContext.accessMode = 0;
		} else {
			inContext.accessMode = 1;

		}
		setConnectionAttr(conn, SQL_ATTR_ACCESS_MODE, inContext.accessMode,
				String.valueOf(inContext.accessMode));
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(conn.props_, readOnly,conn);
			String temp = "Setting readOnly = " + readOnly + " is done.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"readOnly", temp, p);
		}

	}

	void close() throws SQLException {
		TerminateDialogueReply tdr_ = null;
		if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
			String temp = "Terminate Dialogue.";
			t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
					"close", temp, p);
		}
		try {
			tdr_ = t4connection_.TerminateDialogue();
		} catch (SQLException tex) {
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
				String temp = "SQLException during TerminateDialogue.";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"close", temp, p);
			}
			throw tex;
		}

		switch (tdr_.m_p1.exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
			break;
		case odbc_SQLSvc_TerminateDialogue_exc_.odbc_SQLSvc_TerminateDialogue_SQLError_exn_:

			// rollback any transactions if SQLError occured for local txns
			// with AutoCommit off.
			try {
				if (getAutoCommit() == false) {
					rollback();
				}
			} finally {
				close();
				SQLMXMessages.throwSQLException(t4props_, tdr_.m_p1.SQLError);
			}
		}

		// needs work here. This should be proxy destroy. close the logfiles
		try {
			// KAS t4FileHandler.close();
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,this);
				String temp = "Terminate Dialogue successful.";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"close", temp, p);
			}
		} catch (SecurityException sex) {
		}
	};

	private void endTransaction(short commitOption) throws SQLException {
		EndTransactionReply etr_ = null;
		if ((autoCommit) && (t4props_.getCommitInAutoCommitOn() == false)) {
			throw SQLMXMessages.createSQLException(t4props_, locale,
					"invalid_commit_mode", null);
		}

		isConnectionOpen();
		// XA_RESUMETRANSACTION();

		try {
			etr_ = t4connection_.EndTransaction(commitOption);

		} catch (SQLException tex) {
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "SQLException during EndTransaction."
						+ tex.toString();
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			throw tex;
		}

		switch (etr_.m_p1.exception_nr) {
		case TRANSPORT.CEE_SUCCESS:
			break;
		case odbc_SQLSvc_EndTransaction_exc_.odbc_SQLSvc_EndTransaction_ParamError_exn_:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "odbc_SQLSvc_EndTransaction_ParamError_exn_ :";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			throw SQLMXMessages.createSQLException(t4props_, locale,
					"ParamError:" + etr_.m_p1.ParamError, null);
		case odbc_SQLSvc_EndTransaction_exc_.odbc_SQLSvc_EndTransaction_InvalidConnection_exn_:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "odbc_SQLSvc_EndTransaction_InvalidConnection_exn_:";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			throw new SQLException(
					"odbc_SQLSvc_EndTransaction_InvalidConnection_exn",
					"HY100002", 10001);
		case odbc_SQLSvc_EndTransaction_exc_.odbc_SQLSvc_EndTransaction_SQLError_exn_:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "odbc_SQLSvc_EndTransaction_SQLError_exn_:"
						+ etr_.m_p1.SQLError;
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			SQLMXMessages.throwSQLException(t4props_, etr_.m_p1.SQLError);
		case odbc_SQLSvc_EndTransaction_exc_.odbc_SQLSvc_EndTransaction_SQLInvalidHandle_exn_:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "odbc_SQLSvc_EndTransaction_SQLInvalidHandle_exn_:";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			throw new SQLException(
					"odbc_SQLSvc_EndTransaction_SQLInvalidHandle_exn",
					"HY100004", 10001);
		case odbc_SQLSvc_EndTransaction_exc_.odbc_SQLSvc_EndTransaction_TransactionError_exn_:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "odbc_SQLSvc_EndTransaction_TransactionError_exn_:";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			throw new SQLException(
					"odbc_SQLSvc_EndTransaction_TransactionError_exn",
					"HY100005", 10001);
		default:
			if (t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities.makeParams(t4props_,
						commitOption,this);
				String temp = "UnknownError:";
				t4props_.t4Logger_.logp(Level.FINEST, "InterfaceConnection",
						"endTransaction", temp, p);
			}
			throw new SQLException("Unknown Error during EndTransaction",
					"HY100001", 10001);
		}

	};

	void commit() throws SQLException {
		endTransaction(SQL_COMMIT);
	};

	void rollback() throws SQLException {
		endTransaction(SQL_ROLLBACK);
	};

	long beginTransaction() throws SQLException {
		isConnectionOpen();

		return txid;
	};

	void reuse() {
		txnIsolationLevel = Connection.TRANSACTION_READ_COMMITTED;
		
		// Modification for L36 Corda : start
		this.netTimeout = t4props_.getNetworkTimeout() * 1000;
		// End
		isReadOnly = false;
		isClosed_ = false;
		txid = 0;
		t4connection_.reuse();
	};

	boolean useArrayBinding() {
		return useArrayBinding_;
	}

	short getTransportBufferSize() {
		return transportBufferSize_;
	}

	// methods for handling weak connections
	void removeElement(SQLMXConnection conn) {
		refTosrvrCtxHandle_.remove(conn.pRef_);
		conn.pRef_.clear();
	}

	void gcConnections() {
		Reference pRef;
		InterfaceConnection ic;
		while ((pRef = refQ_.poll()) != null) {
			ic = (InterfaceConnection) refTosrvrCtxHandle_.get(pRef);
			// All PreparedStatement objects are added to Hashtable
			// Only Statement objects that produces ResultSet are added to
			// Hashtable
			// Hence stmtLabel could be null
			if (ic != null) {
				try {
					ic.close();
				} catch (SQLException e) {
				} finally {
					refTosrvrCtxHandle_.remove(pRef);
				}
			}
		}
	}

	public byte[] encodeString(String str, int charset)
			throws CharacterCodingException, UnsupportedCharsetException {
		Integer key = new Integer(charset);
		CharsetEncoder ce;
		byte[] ret = null;

		if (str != null) {
			if (this.isoMapping_ == InterfaceUtilities.SQLCHARSETCODE_ISO88591
					&& !this.enforceISO) {
				ret = str.getBytes();
			} else {
				if ((ce = (CharsetEncoder) encoders.get(key)) == null) {
					Charset c = Charset.forName(InterfaceUtilities
							.getCharsetName(charset));
					ce = c.newEncoder();
					ce.onUnmappableCharacter(CodingErrorAction.REPORT);
					encoders.put(key, ce);
				}

				synchronized (ce) {
					ce.reset();
					ByteBuffer buf = ce.encode(CharBuffer.wrap(str));
					ret = new byte[buf.remaining()];
					buf.get(ret, 0, ret.length);
				}
			}
		}

		return ret;
	}

	public String decodeBytes(byte[] data, int charset)
			throws CharacterCodingException, UnsupportedCharsetException {
		Integer key = new Integer(charset);
		CharsetDecoder cd;
		String str = null;

		// we use this function for USC2 columns as well and we do NOT want to
		// apply full pass-thru mode for them
		if (this.isoMapping_ == InterfaceUtilities.SQLCHARSETCODE_ISO88591
				&& !this.enforceISO
				&& charset != InterfaceUtilities.SQLCHARSETCODE_UNICODE) {
			str = new String(data);
		} else {
			// the following is a fix for JDK 1.4.2 and MS932. For some reason
			// it does not handle single byte entries properly
			boolean fix = false;
			if (charset == 10 && data.length == 1) {
				data = new byte[] { 0, data[0] };
				fix = true;
			}

			if ((cd = (CharsetDecoder) decoders.get(key)) == null) {
				Charset c = Charset.forName(InterfaceUtilities
						.getCharsetName(charset));
				cd = c.newDecoder();
				cd.replaceWith(this.t4props_.getReplacementString());
				cd.onUnmappableCharacter(CodingErrorAction.REPLACE);
				decoders.put(key, cd);
			}

			synchronized (cd) {
				cd.reset();
				str = cd.decode(ByteBuffer.wrap(data)).toString();
			}

			if (fix)
				str = str.substring(1);
		}

		return str;
	}

	public String getApplicationName() {
		return this.t4props_.getApplicationName();
	}
}
