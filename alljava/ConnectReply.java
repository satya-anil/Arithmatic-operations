// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2003-2007
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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.util.Locale;

class ConnectReply {
	odbcas_ASSvc_GetObjRefHdl_exc_ m_p1_exception;

	String m_p2_srvrObjRef;

	int m_p3_dialogueId;

	String m_p4_dataSource;

	byte[] m_p5_userSid;

	VERSION_LIST_def m_p6_versionList;

	int isoMapping;

	boolean byteSwap;

	private MXCSAddress m_ncsAddr_;

	// -------------------------------------------------------------
	ConnectReply(LogicalByteArray buf, InterfaceConnection ic)
			throws SQLException, UnsupportedCharsetException,
			CharacterCodingException {
		buf.setLocation(Header.sizeOf());

		m_p1_exception = new odbcas_ASSvc_GetObjRefHdl_exc_();
		m_p1_exception.extractFromByteArray(buf, ic);

		this.byteSwap = buf.getByteSwap();

		if (m_p1_exception.exception_nr == TRANSPORT.CEE_SUCCESS) {
			//Added for solution 10-180130-6186
			byte[] temp = buf.extractString();
			m_p2_srvrObjRef = new String(Bytes.read_chars(temp, 0, temp.length));
			
			m_p3_dialogueId = buf.extractInt();
			m_p4_dataSource = ic.decodeBytes(buf.extractString(), 1);
			m_p5_userSid = buf.extractByteString(); // byteString -- only place
			// used -- packed length
			// does not include the null
			// term
			m_p6_versionList = new VERSION_LIST_def();

			buf.setByteSwap(false);
			m_p6_versionList.extractFromByteArray(buf);
			buf.setByteSwap(this.byteSwap);

			if ((m_p6_versionList.list[0].buildId & InterfaceConnection.CHARSET) > 0) {
				isoMapping = buf.extractInt();
			} else {
				isoMapping = 1;
			}
		}
	}

	// -------------------------------------------------------------
	void fixupSrvrObjRef(T4Properties t4props, Locale locale, String name)
			throws SQLException {
		//
		// This method will replace the domain name returned from the
		// Association server, with a new name.
		//
		m_ncsAddr_ = null;

		if (m_p2_srvrObjRef != null) {
			try {
				m_ncsAddr_ = new MXCSAddress(t4props, locale, m_p2_srvrObjRef);
			} catch (SQLException e) {
				throw e;
			}

			// use your best guess if m_machineName was not found
/*			if (m_ncsAddr_.m_machineName == null) {
				if (m_ncsAddr_.m_ipAddress == null) {
					m_ncsAddr_.m_machineName = name;
				} else {
					m_ncsAddr_.m_machineName = m_ncsAddr_.m_ipAddress;
				}
			}
*/
            //Added for Solution 10-110511-7618
			m_ncsAddr_.m_machineName = name;
			m_p2_srvrObjRef = m_ncsAddr_.recreateAddress();
			m_ncsAddr_.validateAddress();
			m_ncsAddr_.setInputOutput();

			return;
		} // end if

	} // end fixupSrvrObjRef

	MXCSAddress getMXCSAddress() {
		return m_ncsAddr_;
	}
}
