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

/**********************************************************
 * This class represents an address reference.
 *
 *
 * @author Ken Sell
 * @version 1.0
 **********************************************************/

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

abstract class Address {
	protected Locale m_locale;

	protected T4Properties m_t4props;

	protected String m_ipAddress;

	protected String m_machineName;

	protected String m_processName;

	protected Integer m_portNumber;

	protected Properties m_properties;

	InetAddress[] m_inetAddrs;

	protected int m_type;

	protected String m_url;

	protected InputOutput m_io;

	/**
	 * The constructor.
	 * 
	 * @param addr
	 *            The addr has two forms:
	 * 
	 * DriverManager getConnection addr parameter format for connecting via the
	 * Fast JDBC Type 3 driver.
	 * 
	 * jdbc:subprotocol:subname
	 * 
	 * Where:
	 * 
	 * subprotocol = t4sqlmx
	 * 
	 * subname = //<{IP Address|Machine Name}[:port]>/<database name>
	 * 
	 * Example: jdbc:t4sqlmx://130.168.200.30:1433/database1
	 * 
	 * 
	 * ODBC server connect format returned by the ODBC Association Server.
	 * 
	 * TCP:\<{IP Address|Machine Name}>.<Process Name>/<port>:NonStopODBC
	 * 
	 * Example: TCP:\CANCUN.$Z0065/61752:NonStopODBC
	 * 
	 */

	// ----------------------------------------------------------
	Address(T4Properties t4props, Locale locale, String addr)
			throws SQLException {
		m_t4props = t4props;
		m_locale = locale;
		m_url = addr;
	}

	abstract String recreateAddress();

	// ----------------------------------------------------------
	String getIPorName() {
		if (m_machineName != null) {
			return m_machineName;
		} else {
			return m_ipAddress;
		}
	} // end getIPorName

	protected boolean validateAddress() throws SQLException {
		String IPorName = getIPorName();
		try {
			m_inetAddrs = InetAddress.getAllByName(IPorName);
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_lookup_error", m_url, e.getMessage());
			se.initCause(e);
			throw se;
		}
		return true;
	}

	// ----------------------------------------------------------
	Integer getPort() {
		return m_portNumber;
	} // end getIPorName

	void setInputOutput() {
		m_io = new InputOutput(m_locale, this);
	}

	InputOutput getInputOutput() {
		return m_io;
	}
} // end class Address
