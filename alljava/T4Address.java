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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

final class T4Address extends Address {

	private static final String t4ConnectionPrefix = "jdbc:t4sqlmx:";

	private static final String urlPrefix = t4ConnectionPrefix + "//";

	private static final int minT4ConnectionAddrLen = t4ConnectionPrefix
			.length() + 4;

	private static final int AS_type = 1; // jdbc:subprotocol:subname
	//IPV6 CHANGES
	boolean isIPV6Flag=false;

	/**
	 * The constructor.
	 * 
	 * @param addr
	 *            The addr has two forms:
	 * 
	 * DriverManager getConnection addr parameter format for connecting via the
	 * Fast JDBC Type 4 driver.
	 * 
	 * jdbc:subprotocol:subname
	 * 
	 * Where:
	 * 
	 * subprotocol = t4sqlmx
	 * 
	 * subname = //<{IP Address|Machine Name}[:port]>/<properties>
	 * 
	 * Example: jdbc:t4sqlmx://130.168.200.30:1433/database1
	 * 
	 */

	// ----------------------------------------------------------
	T4Address(T4Properties t4props, Locale locale, String addr)
			throws SQLException {
		super(t4props, locale, addr);

		if (addr == null) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_null_error", null);
			throw se;
		}

		//
		// We are now expecting addr = "//<{IP Address|Machine
		// Name}[:port]>/<properties>"
		//
		m_type = AS_type;

		//
		// We don't recognize this address syntax
		//
		if (acceptsURL(addr) == false) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", addr);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "unknown_prefix_error", null);

			se.setNextException(se2);
			throw se;
		}

		//
		// We are now expecting addr = "<{IP Address|Machine Name}[:port]>"
		// Get the IP or Name
		//
		String IPorName = extractHostFromUrl(addr);
		if (isIPAddress(IPorName)) {
			m_ipAddress = IPorName;
		} else {
			m_machineName = IPorName;

			//
			// Get the port number if there is one.
			//
		}
		m_portNumber = new Integer(extractPortFromUrl(addr));
		m_properties = extractPropertiesFromString(addr);

		m_url = recreateAddress();

		validateAddress();
		setInputOutput();
	}

	String recreateAddress() {
		String addr = null;

		addr = t4ConnectionPrefix + "//";

		if (m_machineName != null) {
			addr = addr + m_machineName;
		} else if (m_ipAddress != null) {
			addr = addr + m_ipAddress;

		}
		if (m_portNumber != null) {
			addr = addr + ":" + m_portNumber;

		}
		addr = addr + "/";

		return addr;
	} // end recreateAddress

	static boolean acceptsURL(String url) throws SQLException {
		try {
			return url.toLowerCase().startsWith(t4ConnectionPrefix);
		} catch (Exception ex) {
			throw new SQLException(ex.toString());
		}
	}

	// ----------------------------------------------------------
	String getUrl() {
		//IPV6 CHANGES
		if(isIPV6Flag){
		return urlPrefix + '[' + getIPorName() + ']' + ':' + getPort().toString() + "/:";
		}
		else{
			return urlPrefix + getIPorName() + ':' + getPort().toString() + "/:";	
		}
	} // end getProps()

	// ----------------------------------------------------------
	Properties getProps() {
		return m_properties;
	} // end getProps()

	/**
	 * Return the host value
	 * 
	 * @param url
	 *            of format jdbc:t4sqlmx://host:port/:[prop-name=prop-value]..
	 * @return host string
	 */
	private String extractHostFromUrl(String url) throws SQLException {
		if (url.length() < minT4ConnectionAddrLen) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", url);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "min_address_length_error", null);

			se.setNextException(se2);
			throw se;
		}

		int hostStartIndex = urlPrefix.length();
		int hostEndIndex = -1;
		int tmphostEndIndex = -1;
		if (isIPV6(url)) {
			hostStartIndex = hostStartIndex +1;  //IPV6 CHANGES
			tmphostEndIndex = url.lastIndexOf(']'); // IP6
			hostEndIndex = url.indexOf(':', tmphostEndIndex);			
			
		} else {
			hostEndIndex = url.indexOf(':', hostStartIndex); // IP4

		}
		if (hostEndIndex < 0) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", url);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_format_error", url);

			se.setNextException(se2);
			throw se;
		}
		if (isIPV6(url)) {
			hostEndIndex = hostEndIndex - 1;
			
		}
		
		String host = url.substring(hostStartIndex, hostEndIndex);
		if ((host == null) || (host.length() == 0)) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", url);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_format_error", null);
			SQLException se3 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "missing_ip_or_name_error", null);
			se.setNextException(se2);
			se2.setNextException(se3);
			throw se;
		}

		return host;
	}

	/**
	 * Return the port value
	 * 
	 * @param url
	 *            of format jdbc:t4sqlmx://host:port/:[prop-name=prop-value]..
	 * @return port string
	 */
	private String extractPortFromUrl(String url) throws SQLException {
		//IPV6 CHANGES
		int portStartIndex;
		if (isIPV6(url)) {
			portStartIndex = url.indexOf(']') + 2; //IPV6
		} else {
			portStartIndex = url.indexOf(':', urlPrefix.length()) + 1;
		}
		int portEndIndex = url.indexOf('/', portStartIndex);
		if (portEndIndex < 0) {
			portEndIndex = url.length();

		}
		String port = url.substring(portStartIndex, portEndIndex);
		if (port.length() < 1) {
			throw new SQLException("Incorrect port value in the URL.");
		}
		int asPort;
		try {
			asPort = Integer.parseInt(port);
		} catch (Exception e) {
			throw new SQLException("Incorrect port value in the URL.");
		}

		if ((asPort < 0) || (asPort > 65535)) {
			throw new SQLException("Port value out of range in the URL.");
		}

		return port;
	}

	/**
	 * Checks if the url is of IP6 protocol
	 */
	private boolean isIPV6(String url) throws SQLException {
		if (url == null) {
			SQLException se = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_parsing_error", url);
			SQLException se2 = SQLMXMessages.createSQLException(m_t4props,
					m_locale, "address_format_2_error", null);
			se.setNextException(se2);
			throw se;

		}
		int hostStartIndex = urlPrefix.length();
		//IPV6 CHANGES
		if(url.charAt(hostStartIndex) == '['){
			isIPV6Flag=true;
		}
		return (isIPV6Flag);
	}

	/**
	 * Extracts the property name, value pair from a url String, seperated by ;
	 * 
	 * @param url
	 *            of format jdbc:t4sqlmx://host:port/:[prop-name=prop-value]..
	 * @return Propeties object
	 * @throws IOException
	 */
	private Properties extractPropertiesFromString(String url)
			throws SQLException {
		int urLength = url.length();
		int hostStartIndex = urlPrefix.length();
		int propStartIndex = url.indexOf('/', hostStartIndex);
		if (propStartIndex < 0) {
			return null;
		}

		if (propStartIndex == urLength) {
			return null;
		}

		if (url.charAt(propStartIndex) == '/') {
			propStartIndex++;

		}
		if (propStartIndex == urLength) {
			return null;
		}

		if (url.charAt(propStartIndex) == ':') {
			propStartIndex++;

		}
		if (propStartIndex == urLength) {
			return null;
		}

		String propStr = url.substring(propStartIndex);
		if ((propStr == null) || (propStr.length() == 0)) {
			return null;
		}

		Properties props = new Properties();
		propStr = propStr.replace(';', '\n');
		ByteArrayInputStream byteArrIPStream = new ByteArrayInputStream(propStr
				.getBytes());

		try {
			props.load(byteArrIPStream);
		} catch (IOException ioex) {
			throw new SQLException(ioex.getMessage());
		}

		return props;
	}

	/**
	 * Checks the string is host or port.
	 * 
	 * @param IPorName
	 * @return true if the address is a IP address
	 */
	private boolean isIPAddress(String IPorName) {
		// Minimum length = 7; 1.1.1.1
		if (IPorName.length() < 7)
			return false;
		//
		// If first letter is a digit or ":" (i.e. IPv6), I'll assume it is an
		// IP address
		//
		return (Character.isDigit(IPorName.charAt(0)) || (IPorName.charAt(1) == ':'));
	}
} // end class Address
