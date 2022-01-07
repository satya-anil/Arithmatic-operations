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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
// SSL
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;



class InputOutput {
	private Address m_addr;

	private Socket m_socket;

	private Locale m_locale;

	private int readHdrLength = Header.sizeOf();

	private int m_dialogueId;

	private int m_timeout;

	private int m_connectionIdleTimeout;

	private Header m_rheader;

	private OutputStream m_os;

	private InputStream m_is;

	private WritableByteChannel m_wbc;

	private T4Connection m_t4conn; // trace_connection - AM

	private int m_sendBufSize;
	
	//SSL
	private static SocketFactory m_factory=null;
	



	// -
	// socket
	// factory

	static {
		// SSL
		String sslEncryption_ = System.getProperty("t4sqlmx.sslEncryption");
		if(sslEncryption_== null){
			sslEncryption_=System.getProperty("sslEncryption");
		}
		if(sslEncryption_== null){
			sslEncryption_="OFF";
		}
	
		if (sslEncryption_.equalsIgnoreCase("ON")) {
			m_factory = (SSLSocketFactory) SSLSocketFactory.getDefault(); // NRV
		} else {
			m_factory = SocketFactory.getDefault(); // NRV
		}
		

		try {
			String factStr = System.getProperty("t4sqlmx.socketFactoryClass");
			if (factStr != null) {
				Class z = Class.forName(factStr);
				if (SocketFactory.class.isAssignableFrom(z)) {
					m_factory = (SocketFactory) z.newInstance();
				} else {
					m_factory = SocketFactory.getDefault();
				}
			}
		} catch (Throwable t) {
			m_factory = SocketFactory.getDefault();
		}		
		
	}

	public static SocketFactory getSocketFactory() {
		return m_factory;
	}

	public static void setSocketFactory(SocketFactory factory) {
		m_factory = factory;
	}

	// ----------------------------------------------------------
	InputOutput(Locale locale, Address addr1) {
		m_locale = locale;
		m_addr = addr1;
		m_dialogueId = 0;
		m_timeout = 0;
		m_connectionIdleTimeout = 0;

		m_rheader = new Header((short) 0, m_dialogueId, 0, 0, 'N',
				Header.COMP_0, Header.READ_RESPONSE_FIRST, Header.SIGNATURE,
				Header.VERSION, Header.PC, Header.TCPIP, Header.SWAP_NO);

	} // end InputOutput

	// trace_connection - AM
	void setT4Connection(T4Connection t4conn) {
		m_t4conn = t4conn;
	}

	void setDialogueId(int dialogueId) {
		m_dialogueId = dialogueId;
	}
	//Modification for L36 Corda :timeout value will be in milliseconds from L36
	void setTimeout(int timeout) {
		m_timeout = timeout;
	}

	void setConnectionIdleTimeout(int timeout) {
		m_connectionIdleTimeout = timeout;
	}

	// ----------------------------------------------------------
	synchronized void openIO() throws SQLException {
		// trace_connection - AM
		if (m_t4conn != null
				&& m_t4conn.m_ic.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(m_t4conn.m_ic.t4props_,m_t4conn.m_ic);
			String temp = "m_socket=" + m_socket;
			m_t4conn.m_ic.t4props_.t4Logger_.logp(Level.FINEST, "InputOutput",
					"openIO", temp, p);
		}
		if (m_socket == null) {
			int numTry = 0;
			boolean found = false;
			int i = 0;
			Vector eList = new Vector();

			//
			// Sometimes the server isn't ready to be contacted, so we will try
			// 3 times
			// before giving up.
			//
			//SSL
			String sslEncryption_ = System.getProperty("t4sqlmx.sslEncryption");
			if(sslEncryption_== null){
				sslEncryption_=System.getProperty("sslEncryption");
			}
			if(sslEncryption_== null){
				sslEncryption_="OFF";
			}
			
			while (found == false && numTry < 3) {
				//
				// Because each machine name can have multiple IP addresses
				// associated with it,
				// we need to go through the entire list of IP addresses until
				// we can find
				// one we can connect too, or all address fail.
				//
				i = 0;
				while (found == false && i < m_addr.m_inetAddrs.length) {
					try {
						
						
						if (sslEncryption_.equalsIgnoreCase("ON")) {
									m_socket = (SSLSocket) m_factory.createSocket(
									m_addr.m_inetAddrs[i], m_addr.m_portNumber
											.intValue());
						} else {
							m_socket = m_factory.createSocket(
									m_addr.m_inetAddrs[i], m_addr.m_portNumber
											.intValue());
						}
					


						m_socket.setSoLinger(false, 0); // Make sure the socket
						// can immediately
						// reused if connection
						// is lost.
						m_socket.setSoTimeout(0);
						//
						// Note, I have not set a timeout here for either the
						// conneciton or for
						// read operations on the socket. I need to figure out
						// what the
						// semantics should be, and add this logic.
						//
						// KAS - 5/13/2004 - Although the user can set a
						// connection timeout, we
						// do not set the timeout on the open/connect of the
						// socket. Instead
						// we use the default system TCP/IP timeout. In theory,
						// this may be
						// longer than the user login timeout. Also, we keep
						// trying to create/connect
						// the socket a minimun of 3 times. In theory, this
						// could really mess up the
						// user's use of login timeout. For example, if the user
						// login timeout is
						// small (e.g. 5 sec.), and the TCP/IP default socket
						// create/connect timeout
						// is large (e.g. 10 sec.), and the number of inetAddrs
						// is large (e.g. 5),
						// and the correct inetAddr is the last one on the list,
						// and the AS server
						// isn't ready until the last try, we could end up
						// taking way more than
						// the user specified login time to connect (3 * 10 * 5
						// = 150 seconds vs.
						// 5 sec. the user specified!).
						//
						//
						m_os = m_socket.getOutputStream();
						m_wbc = Channels.newChannel(m_os);
						m_is = m_socket.getInputStream();
						m_sendBufSize = m_socket.getSendBufferSize();
						found = true;
						// Swastik: added code to start connection idle timers
						startConnectionIdleTimeout();
						// trace_connection - AM
						if (m_t4conn != null
								&& m_t4conn.m_ic.t4props_.t4Logger_
										.isLoggable(Level.FINEST) == true) {
							Object p[] = T4LoggingUtilities
									.makeParams(m_t4conn.m_ic.t4props_,m_t4conn.m_ic);
							String temp = "found=" + found + ",numTry="
									+ numTry + ",i=" + i
									+ ",m_addr.m_inetAddrs.length="
									+ m_addr.m_inetAddrs.length;
							m_t4conn.m_ic.t4props_.t4Logger_.logp(Level.FINEST,
									"InputOutput", "openIO", temp, p);
						}
					} catch (Exception e) {
						//
						// Make note of the exception, and keep trying all the
						// possible addresses.
						//
						// If no address works, we will chain all the exceptions
						// together,
						// and let the user figure out what went wrong.
						//
						eList.addElement(e);
						found = false;
					}
					i = i + 1;
				} // end while
				if (found == false) {
					try {
						Thread.sleep(100); // wait for 0.1 second before trying
						// again
					} catch (Exception e) {
						// Do nothing.
					}
				}
				numTry = numTry + 1;
			} // end while
			if (found == false) {
				// trace_connection - AM
				if (m_t4conn != null
						&& m_t4conn.m_ic.t4props_.t4Logger_
								.isLoggable(Level.FINEST) == true) {
					Object p[] = T4LoggingUtilities
							.makeParams(m_t4conn.m_ic.t4props_,m_t4conn.m_ic);
					String temp = "found=" + found + ",numTry=" + numTry
							+ ",i=" + i + ",m_addr.m_inetAddrs.length="
							+ m_addr.m_inetAddrs.length;
					m_t4conn.m_ic.t4props_.t4Logger_.logp(Level.FINEST,
							"InputOutput", "openIO", temp, p);
				}
				//
				// Couldn't open the socket
				//
				Exception eFirst = (Exception) eList.firstElement();

				//
				// Just add the first exception for now. I'd like to add the
				// entire list, but
				// it will take some thought to figure out the best way to put
				// each exception,
				// and it's associated exception cause (i.e. chained list)
				// together.
				// If there is more than one address, then we must be dealing
				// with a machine name.
				// Hopefully, the problem with the first address is
				// representitive of the problem
				// with all addresses.
				//
				SQLException se = SQLMXMessages.createSQLException(null,
						m_locale, "socket_open_error", eFirst.getMessage());

				se.initCause(eFirst);
				throw se;
			}
		} // end if (p1.m_hSocket == null)

		//
		// If m_socket is not null, then we will assume it is already open.
		//

	} // end openIO

	// ----------------------------------------------------------
	synchronized LogicalByteArray doIO(short odbcAPI, LogicalByteArray buffer)
			throws SQLException {
		int wCount = buffer.getLength();
		int rCount;
		int tcount;
		ByteBuffer dataBuffer = buffer.getDataBuffer();
		byte[] trailer = buffer.getTrailer();

		if (dataBuffer != null && trailer != null) {
			wCount += dataBuffer.limit();
			wCount += trailer.length;
		}

		// trace_connection - AM
		if (m_t4conn != null
				&& m_t4conn.m_ic.t4props_.t4Logger_.isLoggable(Level.FINEST) == true) {
			Object p[] = T4LoggingUtilities.makeParams(m_t4conn.m_ic.t4props_,m_t4conn.m_ic);
			String temp = "MessageBuffer";
			m_t4conn.m_ic.t4props_.t4Logger_.logp(Level.FINEST, "InputOutput",
					"doIO", temp, p);
		}
		Header wheader = new Header(
				odbcAPI,
				m_dialogueId,
				wCount - readHdrLength // minus
				// the
				// size
				// of
				// the
				// Header
				, 0, 'N', Header.COMP_0, Header.WRITE_REQUEST_FIRST,
				Header.SIGNATURE, Header.VERSION, Header.PC, Header.TCPIP,
				Header.SWAP_NO);

		m_rheader.reuseHeader(odbcAPI, m_dialogueId);

		// Send to the server
		int buffer_index = 0;

		// reset header length if user provided dataBuffer exists.
		if (dataBuffer != null) {
			wCount = buffer.getLength();
		}

		while (wCount > 0) {
			if (wCount > m_sendBufSize) {
				tcount = m_sendBufSize;
			} else {
				tcount = wCount;
			}
			TCPIPDoWrite(wheader, buffer, buffer_index, tcount);

			wheader.hdr_type_ = Header.WRITE_REQUEST_NEXT;
			wCount = wCount - tcount;
			buffer_index = buffer_index + tcount;
		}

		if (dataBuffer != null && trailer != null) {
			TCPIPWriteByteBuffer(dataBuffer);
			TCPIPWriteByteBuffer(ByteBuffer.wrap(trailer));
		}

		// Receive from the server
		buffer.reset();
		// buffer.insertByteArray(new byte[200], 200); TODO:
		// buffer.reset();

		// Read for READ_RESPONSE_FIRST
		int numRead = 0;
		int totalNumRead = 0;
		int whileCount1 = 0;

		// Read the first part
		m_rheader.hdr_type_ = Header.READ_RESPONSE_FIRST;
		m_rheader.total_length_ = readHdrLength;

		// Keep reading until we have a header, but give up after 3 attempts.
		while (totalNumRead < readHdrLength && whileCount1 < 3) {
			numRead = TCPIPDoRead(m_rheader, buffer, totalNumRead);
			totalNumRead = totalNumRead + numRead;
			whileCount1 = whileCount1 + 1;
			// trace_connection - AM
			if (m_t4conn != null
					&& m_t4conn.m_ic.t4props_.t4Logger_
							.isLoggable(Level.FINEST) == true) {
				Object p[] = T4LoggingUtilities
						.makeParams(m_t4conn.m_ic.t4props_,m_t4conn.m_ic);
				String temp = "MessageBuffer whileCount1=" + whileCount1
						+ ",numRead=" + numRead + ",totalNumRead="
						+ totalNumRead;
				m_t4conn.m_ic.t4props_.t4Logger_.logp(Level.FINEST,
						"InputOutput", "doIO", temp, p);
			}
		} // end while

		// trace_connection - AM
		// if (totalNumRead < readHdrLength)
		if (numRead < readHdrLength) {
			//
			// We didn't even get the header back, so something is seriously
			// wrong.
			//
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"problem_with_server_read", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"header_not_long_enough", null);

			se.setNextException(se2);
			throw se;
		}

		buffer.setLocation(0);
		m_rheader.extractFromByteArray(buffer);
		if (m_rheader.swap_ == 'Y')
			buffer.setByteSwap(true);

		if (m_rheader.signature_ != Header.SIGNATURE) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"problem_with_server_read", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"wrong_header_signature", String.valueOf(Header.SIGNATURE),
					String.valueOf(m_rheader.signature_));

			se.setNextException(se2);
			throw se;
		}
		if (m_rheader.version_ != Header.VERSION) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"problem_with_server_read", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"wrong_header_version", String.valueOf(Header.VERSION),
					String.valueOf(m_rheader.version_));

			se.setNextException(se2);
			throw se;
		}

		if (m_rheader.error_ != 0) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"driver_err_error_from_server", String
							.valueOf(m_rheader.error_), String
							.valueOf(m_rheader.error_detail_));

			throw se;
		}

		numRead = 0;
		buffer.resize(m_rheader.total_length_ + readHdrLength); // make sure the
		// buffer is big
		// enough

		while (totalNumRead < (m_rheader.total_length_ + readHdrLength)) {
			m_rheader.hdr_type_ = Header.READ_RESPONSE_NEXT;

			numRead = TCPIPDoRead(m_rheader, buffer, totalNumRead);
			totalNumRead = totalNumRead + numRead;
		}

		return buffer;
	} // end doIO

	// ----------------------------------------------------------
	synchronized void CloseIO(LogicalByteArray buffer) throws SQLException {
		Header hdr = new Header(Header.CLOSE_TCPIP_SESSION, m_dialogueId, 0, 0,
				'N', Header.COMP_0, Header.CLOSE_TCPIP_SESSION,
				Header.SIGNATURE, Header.VERSION, Header.PC, Header.TCPIP,
				Header.SWAP_NO);

		TCPIPDoWrite(hdr, buffer, 0, hdr.sizeOf());
		try {
			m_socket.close();
			m_socket = null;
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"session_close_error", e.getMessage());
			se.initCause(e);
			throw se;
		} finally {
			closeTimers();
		}
	} // end CloseIO

	void TCPIPWriteByteBuffer(ByteBuffer buffer) throws SQLException {

		if (m_socket == null) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"socket_write_error", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"socket_is_closed_error", null);

			se.setNextException(se2);
			throw se;
		}

		try {
			m_wbc.write(buffer);
			m_os.flush();
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"socket_write_error", e.getMessage());

			se.initCause(e);
			throw se;
		} finally {
			resetTimedOutConnection();
		}

	} // end TCPIPWriteByteBuffer

	// ----------------------------------------------------------
	void TCPIPDoWrite(Header hdr, LogicalByteArray buffer, int buffer_index,
			int bufcount) throws SQLException {
		int error = 0;
		short error_detail = 0;
		int wcount = 0;
		int data_length = 0;

		if (m_socket == null) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"socket_write_error", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"socket_is_closed_error", null);

			se.setNextException(se2);
			throw se;
		}

		switch (hdr.hdr_type_) {
		case Header.WRITE_REQUEST_FIRST:
		case Header.CLOSE_TCPIP_SESSION:
			buffer.setLocation(0);
			hdr.insertIntoByteArray(buffer, m_locale);
		case Header.WRITE_REQUEST_NEXT:
			data_length = data_length + bufcount;

			send_nblk(buffer.getBuffer(), buffer_index, data_length);
			break;
		default:
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"unknown_message_type_error", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"internal_error", null);
			SQLException se3 = SQLMXMessages.createSQLException(null, m_locale,
					"cntact_hp_error", null);

			se.setNextException(se2);
			se2.setNextException(se3);
			throw se;
			// break;
		} // end switch (hdr.hdr_type)

	} // end TCPIPDoWrite

	// ----------------------------------------------------------
	int TCPIPDoRead(Header hdr, LogicalByteArray buffer, int buffer_index)
			throws SQLException {
		int numRead = 0;

		if (m_socket == null) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"socket_read_error", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"socket_is_closed_error", null);

			se.setNextException(se2);
			throw se;
		}

		switch (hdr.hdr_type_) {
		case Header.READ_RESPONSE_FIRST:
		case Header.READ_RESPONSE_NEXT:
			numRead = recv_nblk(buffer.getBuffer(), buffer_index);

			break;
		default:
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"unknown_message_type_error", null);
			SQLException se2 = SQLMXMessages.createSQLException(null, m_locale,
					"internal_error", null);
			SQLException se3 = SQLMXMessages.createSQLException(null, m_locale,
					"cntact_hp_error", null);

			se.setNextException(se2);
			se2.setNextException(se3);
			throw se;
		} // end switch (hdr.hdr_type)

		return numRead;

	} // end TCPIPDoRead

	// ----------------------------------------------------------
	void send_nblk(byte[] buf, int offset, int len) throws SQLException {
		try {
			m_os.write(buf, offset, len);
			m_os.flush();
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"socket_write_error", e.getMessage());

			se.initCause(e);
			throw se;
		} finally {
			resetTimedOutConnection();
		}
	} // end send_nblk

	// ----------------------------------------------------------
	int recv_nblk(byte[] buf, int offset) throws SQLException {
		int num_read = 0;

		try {
			
			//Modification for L36 Corda : start
			m_socket.setSoTimeout(m_timeout);
			//End
			//R3.2 changes -- port from neo -- start
			if (m_t4conn!=null && m_t4conn.m_ic.t4props_.getKeepAlive() != null
					&& !(m_t4conn.m_ic.t4props_
							.getCloseConnectionUponQueryTimeout()
							.equalsIgnoreCase("ON"))) {
				if (m_t4conn.m_ic.t4props_.getKeepAlive()
						.equalsIgnoreCase("ON")) {
					m_socket.setKeepAlive(true);
				}
			}
			//R3.2 changes -- port from neo -- end
			num_read = m_is.read(buf, offset, buf.length - offset);

			// if the socket.read returns -1 then return 0 instead of -1
			if (num_read < 0) {
				num_read = 0;

			}
			m_socket.setSoTimeout(0); // set timeout back to infinite
		} catch (SocketTimeoutException ste) {			
			// If the socket timed out, communication with this server is messed
			// up.
			// Close the socket.
			try {
				m_socket.close();
//				this.m_t4conn.m_ic.setIsClosed(true);
//				this.m_t4conn.m_ic.cancel();

				throw ste;
			} catch (Exception e) {
				SQLException se = SQLMXMessages.createSQLException(null,
						m_locale, "session_close_error", e.getMessage());
				se.initCause(e);
				throw se;
			}
		} catch (Exception e) {
			SQLException se = SQLMXMessages.createSQLException(null, m_locale,
					"socket_read_error", e.getMessage());

			se.initCause(e);
			throw se;
		} finally {
			resetTimedOutConnection();
		}

		return num_read;
	} // recv_nblk

	/** ***************************************** */
	// Start of connectino timeout related code //
	/** ***************************************** */
	void closeTimers() {
		if (m_connectionIdleTimeout > 0) {
			T4TimerThread t = T4TimerThread.getThread(m_dialogueId);
			if (t != null) {
				// t.interrupt(); //SB:2/24/05
				T4TimerThread.removeThread(this.m_dialogueId);
			}
		}
	}

	void resetConnectionIdleTimeout() {
		if (m_connectionIdleTimeout > 0) {
			T4TimerThread t = T4TimerThread.getThread(m_dialogueId);
			if (t != null) {
				t.reset(m_connectionIdleTimeout * 1000);
			} else { // first time
				startConnectionIdleTimeout();
			}
		}
	}

	private void resetTimedOutConnection() {
		if (m_connectionIdleTimeout > 0) {
			// check connection idle timeout
			boolean timedOut = checkConnectionIdleTimeout();
			if (timedOut) {
				startConnectionIdleTimeout(); // this closes existing timers
				// and starts a new one
				// required for long runnign queries
			} else {
				resetConnectionIdleTimeout();
			}
		}
	}

	boolean checkConnectionIdleTimeout() {
		if (m_connectionIdleTimeout > 0) {
			T4TimerThread t = T4TimerThread.getThread(m_dialogueId);
			if (t != null && t.getTimedOut()) {
				return true;
			}
		}
		return false;
	}

	void startConnectionIdleTimeout() {
		if (m_connectionIdleTimeout > 0 && m_dialogueId > 0) {
			closeTimers();
			T4TimerThread m_t4TimerThread = new T4TimerThread(m_dialogueId,
					m_connectionIdleTimeout * 1000);
			// m_t4TimerThread.start(); ==> // SB:2/24/05 this class is no
			// longer
			// inheriting the thread package
			// However it can be modified if
			// need be - see class comments.
		}
	}
	
} // end class InputOutput
