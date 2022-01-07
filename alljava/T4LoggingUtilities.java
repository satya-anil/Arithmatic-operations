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

import java.util.Enumeration;
import java.util.Properties;
public class T4LoggingUtilities {

	static final Long m_syncL = new Long(1L);

	// ----------------------------------------------------------
	public T4LoggingUtilities() {
	}

	// ----------------------------------------------------------
	static String getUniqueID() {
		synchronized (m_syncL) {
			try {
				Thread.sleep(m_syncL.longValue()); // wait 1 millisecond
			} catch (Exception e) {
			}
		}

		java.util.Date d1 = new java.util.Date();
		long t1 = d1.getTime();
		String name = null;

		name = Long.toString(t1);
		return name;
	}

	// ----------------------------------------------------------
	static String getUniqueLogFileName(String uniqueID) {
		String name = null;

		name = "%h/t4sqlmx" + uniqueID + ".log";
		return name;
	}

	// ----------------------------------------------------------
	static String getUniqueLoggerName(String uniqueID) {
		String name = null;

		name = "com.tandem.t4jdbc.logger" + uniqueID;
		return name;
	}

	// ----------------------------------------------------------
	static Object[] makeParams() {
		return null;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props) {
		Object obj[] = new Object[1];

		obj[0] = t4props;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, boolean b1) {
		Object obj[] = new Object[2];

		obj[0] = t4props;
		obj[1] = Boolean.toString(b1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1) {
		Object obj[] = new Object[2];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1) {
		Object obj[] = new Object[2];

		obj[0] = t4props;
		obj[1] = obj1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, long l1) {
		Object obj[] = new Object[2];

		obj[0] = t4props;
		obj[1] = Long.toString(l1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, int i2) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, int i1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Integer.toString(i1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, Object obj1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = obj1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, boolean b1,Object o1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Boolean.toString(b1);
		obj[3] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, double d1,Object o1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Double.toString(d1);
		obj[3] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, boolean b1, boolean b2,Object o1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = Boolean.toString(b1);
		obj[2] = Boolean.toString(b2);
		obj[3] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, boolean b1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Boolean.toString(b1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, short s1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Short.toString(s1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, double d1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Double.toString(d1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, long l1, int i1,Object o1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = Long.toString(l1);
		obj[2] = Integer.toString(i1);
		obj[3] = o1;
		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, long l1, Object o1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = Long.toString(l1);
		obj[2] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, long l1) {
		Object obj[] = new Object[3];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = Long.toString(l1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, int i1, int i2) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Integer.toString(i1);
		obj[3] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, Object obj1, int i2,Object o1) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = obj1;
		obj[3] = Integer.toString(i2);
		obj[4] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, Object obj1,
			Object obj2) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = obj1;
		obj[3] = obj2;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, int i2, Object obj1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Integer.toString(i2);
		obj[3] = obj1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, int i2, int i3,Object o1) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Integer.toString(i2);
		obj[3] = Integer.toString(i3);
		obj[4] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			Object o3) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = o3;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, int i1,
			Object o2) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = Integer.toString(i1);
		obj[3] = o2;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			int i1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = Integer.toString(i1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object[] oa1, int i1,
			Object o1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = oa1;
		obj[2] = Integer.toString(i1);
		obj[3] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			long l1) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = Long.toString(l1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, long l1,
			Object o2) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = Long.toString(l1);
		obj[3] = o2;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, boolean b1,
			Object o2) {
		Object obj[] = new Object[4];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = Boolean.toString(b1);
		obj[3] = o2;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, Object obj1,
			int i2, int i3) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = obj1;
		obj[3] = Integer.toString(i2);
		obj[4] = Integer.toString(i3);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			int i1, int i2) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Integer.toString(i1);
		obj[4] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, int i1,
			int i2, int i3,Object o1) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Integer.toString(i1);
		obj[3] = Integer.toString(i2);
		obj[4] = Integer.toString(i3);
		obj[5] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, int i1,
			long l1, Object obj2) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Integer.toString(i1);
		obj[3] = Long.toString(l1);
		obj[4] = obj1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			Object obj3, int i1) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = obj3;
		obj[4] = Integer.toString(i1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, Object[] oa1,
			int i2, Object o1) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = oa1;
		obj[3] = Integer.toString(i2);
		obj[4] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			Object o3, Object o4) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = o3;
		obj[4] = o4;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, int i1,
			boolean b1, int i2) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = Integer.toString(i1);
		obj[3] = Boolean.toString(b1);
		obj[4] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			int i1, Object o3,Object o4) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = Integer.toString(i1);
		obj[4] = o3;
		obj[5] = o4;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, long l1, Object o1,
			int i1, int i2,Object o2) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = Long.toString(l1);
		obj[2] = o1;
		obj[3] = Integer.toString(i1);
		obj[4] = Integer.toString(i2);
		obj[5] = o2;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			long l1, Object o3) {
		Object obj[] = new Object[5];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = Long.toString(l1);
		obj[4] = o3;
	
		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			int i1, int i2, int i3) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Integer.toString(i1);
		obj[4] = Integer.toString(i2);
		obj[5] = Integer.toString(i3);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			Object obj3, int i1, int i2) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = obj3;
		obj[4] = Integer.toString(i1);
		obj[5] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			boolean b1, Object obj3, Object obj4) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Boolean.toString(b1);
		obj[4] = obj3;
		obj[5] = obj4;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			int i1, boolean b1, int i2) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Integer.toString(i1);
		obj[4] = Boolean.toString(b1);
		obj[5] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, int i2,
			Object[] oa1, int i3, Object o1) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Integer.toString(i2);
		obj[3] = oa1;
		obj[4] = Integer.toString(i3);
		obj[5] = o1;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			int i1, long l1, Object obj3) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Integer.toString(i1);
		obj[4] = Long.toString(l1);
		obj[5] = obj3;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			Object o3, int i1, boolean b1) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = o3;
		;
		obj[4] = Integer.toString(i1);
		obj[5] = Boolean.toString(b1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			Object o3, boolean b1, boolean b2) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = o3;
		;
		obj[4] = Boolean.toString(b1);
		obj[5] = Boolean.toString(b2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, int i1, int i2, Object o1,
			int i3, boolean b1) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = Integer.toString(i1);
		obj[2] = Integer.toString(i2);
		obj[3] = o1;
		obj[4] = Integer.toString(i3);
		obj[5] = Boolean.toString(b1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			long l1, Object o3, int i1) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = Long.toString(l1);
		obj[4] = o3;
		obj[5] = Integer.toString(i1);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, int i1,
			long l1, Object obj2, int i2, int i3) {
		Object obj[] = new Object[7];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = Integer.toString(i1);
		obj[3] = Long.toString(l1);
		obj[4] = obj2;
		obj[5] = Integer.toString(i2);
		obj[6] = Integer.toString(i3);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			Object o3, Object o4, Object o5, Object o6) {
		Object obj[] = new Object[7];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = o3;
		obj[4] = o4;
		obj[5] = o5;
		obj[6] = o6;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			int i1, long l1, Object obj3, boolean b1, int i2) {
		Object obj[] = new Object[8];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Integer.toString(i1);
		obj[4] = Long.toString(l1);
		obj[5] = obj3;
		obj[6] = Boolean.toString(b1);
		obj[7] = Integer.toString(i2);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			Object obj3, boolean b1, int i1, int i2, Object obj4, int i3) {
		Object obj[] = new Object[9];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = obj3;
		obj[4] = Boolean.toString(b1);
		obj[5] = Integer.toString(i1);
		obj[6] = Integer.toString(i2);
		obj[7] = obj4;
		obj[8] = Integer.toString(i3);

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object obj1, Object obj2,
			int i1, int i2, int i3, int i4, int i5, int i6,Object o1)

	{
		Object obj[] = new Object[10];

		obj[0] = t4props;
		obj[1] = obj1;
		obj[2] = obj2;
		obj[3] = Integer.toString(i1);
		obj[4] = Integer.toString(i2);
		obj[5] = Integer.toString(i3);
		obj[6] = Integer.toString(i4);
		obj[7] = Integer.toString(i5);
		obj[8] = Integer.toString(i6);
		obj[9] = o1;
		

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, short s1,
			Object o2, Object o3, Object o4, Object o5, Object o6, int i1,
			int i2, long l1, int i3, int i4, short s2, int i5, Object o7,
			Object o8, Object o9) {
		Object obj[] = new Object[18];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = Short.toString(s1);
		obj[3] = o2;
		obj[4] = o3;
		obj[5] = o4;
		obj[6] = o5;
		obj[7] = o6;
		obj[8] = Integer.toString(i1);
		obj[9] = Integer.toString(i2);
		obj[10] = Long.toString(l1);
		obj[11] = Integer.toString(i3);
		obj[12] = Integer.toString(i4);
		obj[13] = Short.toString(s2);
		obj[14] = Integer.toString(i5);
		obj[15] = o7;
		obj[16] = o8;
		obj[17] = o9;

		return obj;
	} // end makeParams

	// ----------------------------------------------------------
	static Object[] makeParams(T4Properties t4props, Object o1, Object o2,
			Object o3, boolean b1,Object o4) {
		Object obj[] = new Object[6];

		obj[0] = t4props;
		obj[1] = o1;
		obj[2] = o2;
		obj[3] = o3;
		obj[4] = Boolean.toString(b1);
		obj[5] = o4;

		return obj;
	} // end makeParams
	  static Properties makeProperties(Properties t4props){
		Properties prop=new Properties();
			if(t4props!=null){
			Enumeration e = t4props.propertyNames();
			String pkey=null;
			while(e!=null && e.hasMoreElements()){
				pkey=(String)e.nextElement();
				if((pkey.equalsIgnoreCase("password"))){
					prop.setProperty(pkey, "*****");
				}
				else{
				prop.setProperty(pkey, t4props.getProperty(pkey));
				}
			}	
			}
			return prop;
	  }//
} // end class T4LogFormatter
