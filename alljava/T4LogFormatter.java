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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.logging.LogRecord;

public class T4LogFormatter extends java.util.logging.Formatter {

	static DecimalFormat df = new DecimalFormat(
			"########################################################00000000");

	// ----------------------------------------------------------
	public T4LogFormatter() {
	}

	// ----------------------------------------------------------
	public String format(LogRecord lr) {
		String m1;
		String separator = " ~ ";
		Object params[] = lr.getParameters();
		Object tempParam = null;

		try {
			long sequence_number = lr.getSequenceNumber();
			String time_stamp = null;
			long thread_id = lr.getThreadID();
			String connection_id = "";
			String server_id = "";
			String dialogue_id = "";
			String class_name = lr.getSourceClassName();
			String method = lr.getSourceMethodName();
			String parameters = ""; // need to fix
			String message = lr.getMessage();

			long time_mills = lr.getMillis();
			java.util.Date d1 = new java.util.Date(time_mills);
			DateFormat df1 = java.text.DateFormat.getDateTimeInstance(
					DateFormat.MEDIUM, DateFormat.FULL);

			time_stamp = df1.format(d1);

			//
			// By convension, the first parameter is a SQLMXConnection object or
			// a T4Properties object
			//
			SQLMXConnection sc = null;
			InterfaceConnection ic_=null;
			
			T4Properties tp = null;

			if (params != null && params.length > 0) {
				
				if (params[0] instanceof SQLMXConnection)
					tp = ((SQLMXConnection) params[0]).props_;
				else
					tp = (T4Properties) params[0];


				for (int nfor = 0; nfor < params.length; nfor++) {
					if (params[nfor] != null) {

						if (params[nfor] instanceof SQLMXConnection) {
							sc = ((SQLMXConnection) params[nfor]);
							break;
						} else if (params[nfor] instanceof InterfaceConnection) {
							ic_ = ((InterfaceConnection) params[nfor]);
							break;
						}

					}
						
				

				}
				
			}
			if (sc != null) {
				connection_id = sc.connectionID_;
				server_id = sc.serverID_;
				dialogue_id = sc.dialogueID_;

			}
			else if(ic_!=null){				
				connection_id = ic_.getConnectionID();
				dialogue_id = String.valueOf(ic_.getDialogueId());
				server_id = String.valueOf(ic_.getServerID());
			}
			else if (tp != null) {
				connection_id = tp.getConnectionID();
				server_id = tp.getServerID();
				dialogue_id = tp.getDialogueID();
			}

			//
			// Format for message:
			//
			// sequence-number ~ time-stamp ~ thread-id ~ [connection-id] ~
			// [server-id]
			// ~ [dialogue-id] ~ [class] ~ [method] ~ [parameters] ~ [text]
			//

			Long l1 = new Long(sequence_number);
			String t1 = df.format(l1);
			String p1 = "";

			m1 = t1 + separator + time_stamp + separator + thread_id
					+ separator + connection_id + separator + server_id
					+ separator + dialogue_id + separator + class_name + "."
					+ method + "(";

			if (params != null) {
				String paramText = null;

				//
				// Skip the first parameter, which is a SQLMXConnection, and is
				// handled above.
				//
				for (int i = 1; i < params.length; i++) {
					tempParam = params[i];
					
					if(params[i] instanceof SQLMXConnection || params[i] instanceof InterfaceConnection){
						continue;
					}
					if (tempParam != null) {
						//
						// If the parameter is an array, try to print each
						// element of the array.
						//
						tempParam = makeObjectArray(tempParam);

						if (tempParam instanceof Object[]) {
							Object[] tempOa = (Object[]) tempParam;
							String tempOas = "";
							String tempStr2 = null;

							for (int j = 0; j < tempOa.length; j++) {
								if (tempOa[j] != null) {
									tempStr2 = tempOa[j].toString();
								} else {
									tempStr2 = "null";
								}
								tempOas = tempOas + " [" + j + "]" + tempStr2;
							}
							paramText = tempOas;
						} else {
							paramText = tempParam.toString();
						}

						// Soln 10-100219-8242 -- End
					} else {
						paramText = "null";

					}
					p1 = p1 + "\"" + paramText + "\"";
					if (i + 1 < params.length) {
						p1 = p1 + ", ";
					}
				}
			}

			m1 = m1 + p1 + ")" + separator + message + "\n";

		} catch (Exception e) {
			//
			// Tracing should never cause an internal error, but if it does, we
			// do want to
			// capture it here. An internal error here has no effect on the user
			// program,
			// so we don't want to throw an exception. We'll put the error in
			// the trace log
			// instead, and instruct the user to report it to HP.
			//
			m1 = "An internal error has occurred in the tracing logic. Please report this to your HP representative. \n"
					+ "  exception = "
					+ e.toString()
					+ "\n"
					+ "  message   = "
					+ e.getMessage() + "\n" + "  Stack trace = \n";

			StackTraceElement st[] = e.getStackTrace();

			for (int i = 0; i < st.length; i++) {
				m1 = m1 + "    " + st[i].toString() + "\n";
			}
			m1 = m1 + "\n";
		} // end catch

		//
		// The params array is reused, so we must null it out before returning.
		//
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				params[i] = null;
			}
		}

		return m1;

	} // end formatMessage

	// ---------------------------------------------------------------------
	Object makeObjectArray(Object obj) {
		Object retVal = obj;
		Object[] newVal = null;
		int i;
		//Soln 10-100219-8242 -- start
		if (obj instanceof boolean[]) {
			boolean[] temp = (boolean[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof char[]) {
			char[] temp = (char[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof byte[]) {
			byte[] temp = (byte[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof short[]) {
			short[] temp = (short[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof int[]) {
			int[] temp = (int[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof long[]) {
			long[] temp = (long[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof float[]) {
			float[] temp = (float[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		} else if (obj instanceof double[]) {
			double[] temp = (double[]) obj;
			if (obj == null) {
				return "null";
			}
			int iMax = temp.length - 1;
			if (iMax == -1) {
				return "[]";
			}
			StringBuffer b = new StringBuffer();
			b.append('[');
			for (int nfor = 0;; nfor++) {
				b.append(temp[nfor]);
				if (nfor == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		}
		//Soln 10-100219-8242 -- End

		if (newVal != null)
			retVal = newVal;

		return retVal;
	} // end makeObjectArray

} // end class T4LogFormatter
