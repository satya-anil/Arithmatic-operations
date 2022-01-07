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

import java.sql.SQLException;
import java.util.Locale;

final class MXCSAddress extends Address {

	private static final String ODBCServerPrefix = "TCP:";

	private static final String ODBCServerSuffix = ":NonStopODBC";

	private static final int minODBCServerAddrLen = ODBCServerPrefix.length()
			+ 7 + ODBCServerSuffix.length();

	static final int OS_type = 2; // TCP:\<Machine Name>.<Process

	// Name>/<port>:NonStopODBC

	/**
	 * The constructor.
	 * 
	 * @param addr
	 *            The addr has the format:
	 * 
	 * ODBC server connect format returned by the ODBC Association Server.
	 * 
	 * TCP:\<{IP Address|Machine Name}>.<Process Name>/<port>:NonStopODBC
	 * 
	 * Example: TCP:\CANCUN.$Z0065/61752:NonStopODBC
	 * 
	 */
	MXCSAddress(T4Properties t4props, Locale locale, String addr)
			throws SQLException {
		super(t4props, locale, addr);
		int index0;
		int index1;
		int index2;
		int index3;

		m_locale = locale;

		if (addr == null) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_null_error", null);
			throw se;
		}

		if (acceptsURL(addr) == true) {
			//
			// We are dealing with an address of the form:
			//
			// TCP:\<{IP Address|Machine Name}>.<Process
			// Name>/<port>:NonStopODBC
			//
			m_type = OS_type;
			if (addr.endsWith(ODBCServerSuffix) == false) {
				SQLException se = SQLMXMessages.createSQLException(m_t4props,
						m_locale, "address_parsing_error", addr);
				SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
						m_locale, "odbc_server_suffix_error", ODBCServerSuffix);

				se.setNextException(se2);
				throw se;
			}
			if (addr.length() < minODBCServerAddrLen) {
				SQLException se = SQLMXMessages.createSQLException(m_t4props,
						m_locale, "address_parsing_error", addr);
				SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
						m_locale, "min_address_length_error", null);

				se.setNextException(se2);
				throw se;
			}

			addr = addr.substring(ODBCServerPrefix.length());
			addr = addr.substring(0, addr.length() - ODBCServerSuffix.length());

			if (addr.indexOf(",") > 0)
				interpretMXCSAddress(t4props, locale, addr);
			else
				interpretAddress(t4props, locale, addr);

			if ((m_machineName == null && m_ipAddress == null)
					|| m_processName == null || m_portNumber == null) {
				SQLException se = SQLMXMessages.createSQLException(m_t4props,
						m_locale, "address_parsing_error", addr);
				SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
						m_locale, "address_format_1_error", null);

				se.setNextException(se2);
				throw se;
			}
		}
	} // end Address

	// ----------------------------------------------------------
	void interpretAddress(T4Properties t4props, Locale locale, String addr)
			throws SQLException {
		//
		// We are now expecting addr = "\<machine name>.<process name>/<port
		// number>"
		//

		int index1 = addr.indexOf("\\");
		int index3 = addr.indexOf("/");

		//
		// Find <{IP Address|Machine Name}>
		//
		int index2 = addr.lastIndexOf(".", index3);

		if ((-1 < index1 && index1 < index2 && index2 < index3 && index3 < addr
				.length()) == false) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", addr);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_format_1_error", null);

			se.setNextException(se2);
			throw se;
		}

		String temp4 = addr.substring((index1 + 1), index2);

		if (Character.isDigit(temp4.charAt(0))
				|| temp4.substring(0, 1).equals("[")) {
			//
			// If first letter is a digit or "[" (i.e. IPv6), I'll assume it is
			// an IP address
			//
			m_ipAddress = temp4;
		} else {
			m_machineName = temp4;
		}

		m_processName = addr.substring((index2 + 1), index3);
		m_portNumber = new Integer(addr.substring((index3 + 1), addr.length()));
	}

	void interpretMXCSAddress(T4Properties t4props, Locale locale, String addr)
			throws SQLException {
		//
		// We are now expecting addr = "\<machine name>.<process name>,<{IP
		// Address|Machine Name}>/<port number>"
		//
		int index1 = addr.indexOf("\\");
		int index3 = addr.indexOf("/");
		int index4 = addr.indexOf(",");
		//
		// Find <{IP Address|Machine Name}>
		//
		int index2 = addr.indexOf(".", 0);

		if ((-1 < index1 && index1 < index2 && index2 < index4
				&& index4 < index3 && index3 < addr.length()) == false) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", addr);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_format_1_error", null);

			se.setNextException(se2);
			throw se;
		}

		String temp4 = addr.substring((index4 + 1), index3);

		if (Character.isDigit(temp4.charAt(0))
				|| temp4.substring(0, 1).equals("[")) {
			//
			// If first letter is a digit or "[" (i.e. IPv6), I'll assume it is
			// an IP address
			//
			m_ipAddress = temp4;
		} else {
			m_machineName = temp4;
		}

		m_processName = addr.substring((index2 + 1), index4);
		m_portNumber = new Integer(addr.substring((index3 + 1), addr.length()));
	}

	// ----------------------------------------------------------
	String recreateAddress() {
		String addr = ODBCServerPrefix + "\\";

		if (m_machineName != null) {
			addr = addr + m_machineName;
		}
		addr = addr + ".";

		if (m_processName != null) {
			addr = addr + m_processName;

		}
		addr = addr + "/";

		if (m_portNumber != null) {
			addr = addr + m_portNumber;

		}
		addr = addr + ODBCServerSuffix;

		return addr;
	} // end recreateAddress

	static boolean acceptsURL(String url) throws SQLException {
		try {
			return (url.toUpperCase().startsWith(ODBCServerPrefix));
		} catch (Exception ex) {
			throw new SQLException(ex.toString());
		}
	}

} // end class Address
