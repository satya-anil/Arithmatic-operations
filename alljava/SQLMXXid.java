// @ @ @ START COPYRIGHT @ @ @
//
// Copyright 2005, 2006
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

/**
 * MXCS Xid structure.
 #define XIDDATASIZE     128             // size in bytes
 #define MAXGTRIDSIZE    64              // maximum size in bytes of gtrid
 #define MAXBQUALSIZE    64              // maximum size in bytes of bqual
        struct xid_t {
                long formatID;          // format identifier
                long gtrid_length;      // value not to exceed 64
                long bqual_length;      // value not to exceed 64
                char data[XIDDATASIZE];
        };
 */
package com.tandem.t4jdbc;

import java.util.Locale;
import java.sql.SQLException;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;
import java.util.Arrays;
import java.util.logging.Level;

// public added by Ravi

class SQLMXXid implements Xid {

  static final int XIDDATASIZE = 128;
  private int formatID;
  int gtrid_length;
  int bqual_length;
  private byte[] data;
  private T4Properties m_t4props;
  private int hashCode = 0;
 
  int sizeof() {
    return 12 + XIDDATASIZE;
  }

  int dataLength() {
    return XIDDATASIZE;
  }

  private void initializeData() {
    this.setData(new byte[dataLength()]);
    Arrays.fill(getData(), (byte) 0);
  }

  /**
   * Constructor used when LogicalByteArray received from MXCS.
   * No error checking is done intentionally since we expect
   * no error here like any other data extracted from MXCS.
   */
  SQLMXXid(T4Properties t4props, LogicalByteArray buffer1, int index1) {
    m_t4props = t4props;
    buffer1.setLocation(index1);
    setFormatID(buffer1.extractInt());
    index1 += 4;
    gtrid_length = buffer1.extractInt();
    index1 += 4;
    bqual_length = buffer1.extractInt();
    index1 += 4;
    setData(buffer1.extractByteArray(dataLength()));
    index1 = index1 + dataLength();
    hashCode = toString().hashCode();
  }

  /**
   * Construct xabroker xid from xid passed by the external TM.
   * @param t4props - T4Properties object containing connection information.
   * @param xid - xid from the external TM.
   */
 SQLMXXid(T4Properties t4props, Xid xid) throws XAException {
    m_t4props = t4props;

    if (xid == null)
    {
      XAException xaex = new XAException(XAException.XAER_INVAL);
      XAException xaex1 = new XAException("Invalid Xid. Xid is null");
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    this.setFormatID(xid.getFormatId());
    if (this.getFormatID() < 0)
    {
      XAException xaex = new XAException(XAException.XAER_INVAL);
      XAException xaex1 = new XAException("Invalid Xid. FormatID is null");
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    byte[] gtrid = xid.getGlobalTransactionId();
    if (gtrid == null)
    {
      XAException xaex = new XAException(XAException.XAER_INVAL);
      XAException xaex1 = new XAException("Invalid Xid. Global Transaction ID is null");
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    this.gtrid_length = gtrid.length;
    if ((gtrid_length < 1) || (gtrid_length > 64))
    {
      XAException xaex = new XAException(XAException.XAER_INVAL);
      XAException xaex1 = new XAException("Invalid Xid. Incorrect Global Transaction Id length.");
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    byte[] bqual = xid.getBranchQualifier();
    if (bqual == null)
    {
      XAException xaex = new XAException(XAException.XAER_INVAL);
      XAException xaex1 = new XAException("Invalid Xid. Branch Qualifier is null");
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    this.bqual_length = bqual.length;
    if ((bqual_length < 1) || (bqual_length > 64))
    {
      XAException xaex = new XAException(XAException.XAER_INVAL);
      XAException xaex1 = new XAException("Invalid Xid. Incorrect Branch Qualifier length.");
      xaex.setStackTrace(xaex1.getStackTrace());
      throw xaex;
    }

    initializeData();
    System.arraycopy(gtrid, 0, getData(), 0, gtrid_length);

    System.arraycopy(bqual, 0, getData(), gtrid_length, bqual_length);

    hashCode = toString().hashCode();
  }

  /**
   * Construct xabroker xid from parameters passed for tests.
   * This constructor is used only by the internal system tests.
   * @param formatId
   * @param gtrid
   * @param bqual
   */
  SQLMXXid(T4Properties t4props, int p_formatId, String p_gtrid, String p_bqual) {
    m_t4props = t4props;
    this.setFormatID(p_formatId);
    byte[] gtrid = p_gtrid.getBytes();
    this.gtrid_length = gtrid.length;
    byte[] bqual = p_bqual.getBytes();
    this.bqual_length = bqual.length;
    initializeData();
    System.arraycopy(gtrid, 0, getData(), 0, gtrid_length);
    System.arraycopy(bqual, 0, getData(), gtrid_length, bqual_length);
    hashCode = toString().hashCode();
  }

//commenting this method since the values are inserted in BrkXaMessage.java itself - R30
//  //----------------------------------------------------------
//  int insertIntoByteArray(LogicalByteArray buffer1, int index1, Locale locale) throws
//      SQLException {
//    index1 = buffer1.insertInt(index1, getFormatID());
//    index1 = buffer1.insertInt(index1, gtrid_length);
//    index1 = buffer1.insertInt(index1, bqual_length);
//    index1 = buffer1.insertByteArray(getData(), dataLength(),0);
//
//    return index1;
//  }

  //----------------------------------------------------------
  int extractFromByteArray(LogicalByteArray buffer1, int index1, int hIndex,
                           Locale locale, String addr) throws SQLException {
    setFormatID(buffer1.extractInt());
    index1 += 4;
    gtrid_length = buffer1.extractInt();
    index1 += 4;
    bqual_length = buffer1.extractInt();
    index1 += 4;
    setData(buffer1.extractByteArray( dataLength()));
    index1 = index1 + dataLength();

    return index1;
  }

  //----------------------------------------------------------
  void printObject(String indent) {
    String newIndent = indent + "  ";

    System.out.println(indent + "XID_def");

    System.out.println(newIndent + "formatID = " + getFormatID());
    System.out.println(newIndent + "gtrid_length = " + gtrid_length);
    System.out.println(newIndent + "bqual_length = " + bqual_length);
    System.out.println(newIndent + "data = " + getData());
  }

  //----------------------------------------------------------
  String toString(String indent) {
    String temp1 = "";
    String newIndent = indent + "  ";

    temp1 = temp1 + indent + "XID_def" + "\n";

    temp1 = temp1 + newIndent + "formatID = " + getFormatID() + "\n";
    temp1 = temp1 + newIndent + "gtrid_length = " + gtrid_length + "\n";
    temp1 = temp1 + newIndent + "bqual_length = " + bqual_length + "\n";
    temp1 = temp1 + newIndent + "data = " + getData() + "\n";

    return temp1;
  } // end toString

  /**
       getFormatId
       public int getFormatId()Obtain the format identifier part of the XID.
       Returns:
       Format identifier. O means the OSI CCR format.
   */
  public int getFormatId() {
    return getFormatID();
  }

  /**
       getGlobalTransactionId
       public byte[] getGlobalTransactionId()
       Obtain the global transaction identifier part of XID as an array of bytes.
       Returns:
       Global transaction identifier.
   */
  public byte[] getGlobalTransactionId() {
    byte[] gtrid = new byte[gtrid_length];
    System.arraycopy(getData(), 0, gtrid, 0, gtrid_length);
    return gtrid;
  }

  /**
       getBranchQualifier
       public byte[] getBranchQualifier()
       Obtain the transaction branch identifier part of XID as an array of bytes.
       Returns:
       Global transaction identifier.
   */
  public byte[] getBranchQualifier() {
    byte[] bqual = new byte[bqual_length];
    System.arraycopy(getData(), gtrid_length, bqual, 0, bqual_length);
    return bqual;
  }

  public void checkSize() throws SQLException {
    if ( (gtrid_length > 64) || (bqual_length > 64) ||
        (getData().length > XIDDATASIZE)) {
      SQLMXMessages.createSQLException(m_t4props,
                                       m_t4props == null ? null : m_t4props.getLocale(),
                                       "xid_size_incorrect",
                                       "XID size is incorrect.");
    }
  }

  static String getGlobalIdAsString(Xid p_xid)
  {
    String l_xid = null;
    try {
      byte[] gtrid = p_xid.getGlobalTransactionId();

      int lengthAsString = 0;

      if (gtrid != null) {
        lengthAsString += (2 * gtrid.length);
      }

      StringBuffer asString = new StringBuffer(lengthAsString);

      if (gtrid != null) {
        for (int i = 0; i < gtrid.length; i++) {
          String asHex = Integer.toHexString(gtrid[i] & 0xff);

          if (asHex.length() == 1) {
            asString.append("0");
          }

          asString.append(asHex);
        }
      }
     l_xid = asString.toString();
    }
    catch (Exception ex) {
      // ignore
    }
    return l_xid;
  }

  static String toString(Xid p_xid) {
    String l_xid = null;
    try {
      byte[] gtrid = p_xid.getGlobalTransactionId();

      byte[] btrid = p_xid.getBranchQualifier();

      int lengthAsString = 1; // for '.'

      if (gtrid != null) {
        lengthAsString += (2 * gtrid.length);
      }

      if (btrid != null) {
        lengthAsString += (2 * btrid.length);
      }

      StringBuffer asString = new StringBuffer(lengthAsString);

      if (gtrid != null) {
        for (int i = 0; i < gtrid.length; i++) {
          String asHex = Integer.toHexString(gtrid[i] & 0xff);

          if (asHex.length() == 1) {
            asString.append("0");
          }

          asString.append(asHex);
        }
      }

      if (btrid != null) {
        asString.append(".");

        for (int i = 0; i < btrid.length; i++) {
          String asHex = Integer.toHexString(btrid[i] & 0xff);

          if (asHex.length() == 1) {
            asString.append("0");
          }

          asString.append(asHex);
        }
      }
      l_xid = asString.toString();
    }
    catch (Exception ex) {
      // ignore
    }
    return l_xid;

  }

  /**
    package access method toString()
    Handy method for status printing, logging and passing around.
   */
  public String toString() {
    return toString(this);
  }

  public boolean equals(Object p_x1) {
    boolean l_isEqual = false;

    if (p_x1 instanceof SQLMXXid) {
      l_isEqual = equals((Xid)p_x1);
    } else if (p_x1 instanceof Xid) {
      l_isEqual = this.equals((Xid)p_x1);
    }

    return l_isEqual;
  }

  private boolean equals(Xid x1) {
    if (x1 == null)
      return false;
    if (x1.getFormatId() != this.getFormatId())
      return false;
    if (Arrays.equals(x1.getGlobalTransactionId(),
                       this.getGlobalTransactionId()) == false)
      return false;
    if (Arrays.equals(x1.getBranchQualifier(),
                   this.getBranchQualifier()) == false)
      return false;

    return true;
  }

  public int hashCode()
  {
    return hashCode;
  }

/**
 * @param formatID the formatID to set
 */
void setFormatID(int formatID) {
	this.formatID = formatID;
}

/**
 * @return the formatID
 */
int getFormatID() {
	return formatID;
}

/**
 * @param data the data to set
 */
public void setData(byte[] data) {
	this.data = data;
}

/**
 * @return the data
 */
public byte[] getData() {
	return data;
}

//@Override
//int insertIntoByteArray(LogicalByteArray buffer1, int index1, Locale locale)
//		throws SQLException {
//	// TODO Auto-generated method stub
//	return 0;
//}
}
