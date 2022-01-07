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

import java.lang.*;

/***************************************************************
 *  This class contains byte manipulation methods.
 *
 *  I plagiarized most of this logic from the CORBA
 *  project.
 *
 * @author Ken Sell
 * @version 1.0
 ****************************************************************/

class toByte {

	//-------------------------------------------------------------
	/** This method will place the binary representation of a short into
	 *  a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to insert the short into.
	 * @param  index   The index in the buffer to insert the short at.
	 * @param  value   The value to insert.
	 *
	 * @retrun The next index in the buffer past the insertion of the short.
	 *
	 */
	static int fromShort(byte[] buffer, int index, short value) {
		//
		// NOTE, the value is always stored in the buffer in big endian
		//
		buffer[index + 1] = (byte) ((value) & 0xff);
		buffer[index + 0] = (byte) ((value >>> 8) & 0xff);

		return (index + 2);
	} // fromShort

	//-------------------------------------------------------------
	/** This method will place the binary representation of an int into
	 *  a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to insert the int into.
	 * @param  index   The index in the buffer to insert the int at.
	 * @param  value   The value to insert.
	 *
	 * @retrun The next index in the buffer past the insertion of the int.
	 *
	 */
	static int fromInt(byte[] buffer, int index, int value) {
		//
		// NOTE, the value is always stored in the buffer in big endian
		//
		buffer[index + 3] = (byte) ((value) & 0xff);
		buffer[index + 2] = (byte) ((value >>> 8) & 0xff);
		buffer[index + 1] = (byte) ((value >>> 16) & 0xff);
		buffer[index + 0] = (byte) ((value >>> 24) & 0xff);

		return (index + 4);
	} // end fromInt

	//-------------------------------------------------------------
	/** This method will place the binary representation of a long into
	 *  a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to insert the long into.
	 * @param  index   The index in the buffer to insert the long at.
	 * @param  value   The value to insert.
	 *
	 * @retrun The next index in the buffer past the insertion of the long.
	 *
	 */
	static int fromLong(byte[] buffer, int index, long value) {
		//
		// NOTE, the value is always stored in the buffer in big endian
		//
		buffer[index + 7] = (byte) ((value) & 0xff);
		buffer[index + 6] = (byte) ((value >>> 8) & 0xff);
		buffer[index + 5] = (byte) ((value >>> 16) & 0xff);
		buffer[index + 4] = (byte) ((value >>> 24) & 0xff);
		buffer[index + 3] = (byte) ((value >>> 32) & 0xff);
		buffer[index + 2] = (byte) ((value >>> 40) & 0xff);
		buffer[index + 1] = (byte) ((value >>> 48) & 0xff);
		buffer[index + 0] = (byte) ((value >>> 56) & 0xff);

		return (index + 8);
	} // end fromLong

	//-------------------------------------------------------------
	/** This method will place the binary representation of a char into
	 *  a byte buffer.
	 *
	 * @param  buffer  The buffer to insert the char into.
	 * @param  index   The index in the buffer to insert the char at.
	 * @param  value   The value to insert.
	 *
	 * @retrun The next index in the buffer past the insertion of the char.
	 *
	 */
	static int fromChar(byte[] buffer, int index, char value) {
		//
		// KAS NOTE, we are assuming an ASCII Unicode value.
		//     This assumption is probably wrong, and needs to be
		//     fixed.
		//
		buffer[index] = (byte) ((value) & 0xff);
		return (index + 1);
	} // fromChar

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a short
	 *  from a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to extract the short from.
	 * @param  index   The index in the buffer to extract the short at.
	 *
	 * @retrun The extracted short.
	 *
	 */
	static short read_short(byte[] buffer, int index) {
		int lv_return;

		//
		// NOTE, this assumes the value is in big endian
		//
		lv_return = ((buffer[index + 1]) & 0x00ff)
				| ((buffer[index + 0] << 8) & 0xff00);

		return (short) lv_return;
	} // end read_short

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a short
	 *  from a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to extract the short from.
	 * @param  index   The index in the buffer to extract the short at.
	 *
	 * @retrun The extracted short.
	 *
	 */
	static int read_unsigned_short(byte[] buffer, int index) {
		int lv_return;

		//
		// NOTE, this assumes the value is in big endian
		//
		lv_return = ((buffer[index + 1]) & 0x00ff)
				| ((buffer[index + 0] << 8) & 0xff00);

		return lv_return;
	} // end read_short

	//-------------------------------------------------------------
	/** This method will extract the binary representation of an int
	 *  from a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to extract the int from.
	 * @param  index   The index in the buffer to extract the int at.
	 *
	 * @retrun The extracted short.
	 *
	 */
	static int read_int(byte[] buffer, int index) {
		int lv_return;

		//
		// NOTE, this assumes the value is in big endian
		//
		lv_return = ((buffer[index + 3]) & 0x000000ff)
				| ((buffer[index + 2] << 8) & 0x0000ff00)
				| ((buffer[index + 1] << 16) & 0x00ff0000)
				| ((buffer[index + 0] << 24) & 0xff000000);

		return (int) lv_return;
	} // end read_int

	//-------------------------------------------------------------
	/** This method will extract the binary representation of an int
	 *  from a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to extract the int from.
	 * @param  index   The index in the buffer to extract the int at.
	 *
	 * @retrun The extracted short.
	 *
	 */
	static long read_unsigned_int(byte[] buffer, int index) {
		long lv_return;
		long lv_l1 = 0;
		long lv_l2;

		//
		// NOTE, this assumes the value is in big endian
		//
		lv_l1 = 0;
		lv_l2 = ((buffer[index + 0] << 24) & 0xff000000)
				| ((buffer[index + 1] << 16) & 0x00ff0000)
				| ((buffer[index + 2] << 8) & 0x0000ff00)
				| ((buffer[index + 3]) & 0x000000ff);
		lv_return = (lv_l1 << 32) | (lv_l2 & 0xffffffffl);

		return lv_return;
	} // end read_unsigned_int

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a long
	 *  from a byte buffer using big endian byte format.
	 *
	 * @param  buffer  The buffer to extract the long from.
	 * @param  index   The index in the buffer to extract the long at.
	 *
	 * @retrun The extracted long.
	 *
	 */
	static long read_long(byte[] buffer, int index) {
		long lv_return;
		long lv_l1;
		long lv_l2;

		//
		// NOTE, this assumes the value is in big endian
		//
		lv_l1 = ((buffer[index + 0] << 24) & 0xff000000)
				| ((buffer[index + 1] << 16) & 0x00ff0000)
				| ((buffer[index + 2] << 8) & 0x0000ff00)
				| ((buffer[index + 3]) & 0x000000ff);
		lv_l2 = ((buffer[index + 4] << 24) & 0xff000000)
				| ((buffer[index + 5] << 16) & 0x00ff0000)
				| ((buffer[index + 6] << 8) & 0x0000ff00)
				| ((buffer[index + 7]) & 0x000000ff);
		lv_return = (lv_l1 << 32) | (lv_l2 & 0xffffffffl);

		return lv_return;
	} // end read_long

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a char
	 *  from a byte.
	 *
	 * @param  b1      The byte to extract the char from.
	 *
	 * @retrun The extracted char.
	 *
	 */
	static char read_char(byte b1) {
		char la_char;

		la_char = (char) (b1 & 0xff);

		return la_char;
	} // end read_char

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a char
	 *  from a byte buffer.
	 *
	 * @param  buffer  The buffer to extract the char from.
	 * @param  index   The index in the buffer to extract the char at.
	 *
	 * @retrun The extracted char.
	 *
	 */
	static char read_char(byte[] buffer, int index) {
		return read_char(buffer[index]);
	} // end read_char

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a char
	 *  array from a byte buffer. The array is assumed to be terminated
	 *  with a null (i.e. 0) byte.
	 *
	 * @param  buffer  The buffer to extract the char array from.
	 * @param  index   The index in the buffer to extract the char array at.
	 *
	 * @retrun The extracted char array.
	 *
	 */
	static char[] read_chars(byte[] buffer, int index) {
		char[] la_chars;
		int len = 0;

		//
		// KAS Note, a very slow method. This need improvement.
		//

		// find the null terminator
		while (buffer[index + len] != 0) {
			len = len + 1;
		}

		char[] temp1 = read_chars(buffer, index, len);

		return temp1;
	} // end read_chars

	//-------------------------------------------------------------
	/** This method will extract the binary representation of a char
	 *  array from a byte buffer for the specified length.
	 *
	 * @param  buffer  The buffer to extract the char array from.
	 * @param  index   The index in the buffer to extract the char array at.
	 * @param  tLen    The length of the char array (-1 implies null terminated).
	 *
	 * @retrun The extracted char array.
	 *
	 */
	static char[] read_chars(byte[] buffer, int index, int tLen) {
		char[] la_chars;
		int len = tLen;

		if (len == -1) // must find null to get length
		{
			int ii = index;
			while (buffer[ii] != (byte) 0) {
				ii = ii + 1;
			}
			len = ii - index;
		}

		la_chars = new char[len];

		int i = 0;
		while (i < len) {
			la_chars[i] = (char) (buffer[index] & 0xff);
			i = i + 1;
			index = index + 1;
		}

		return la_chars;
	} // end read_chars

	//-------------------------------------------------------------
	/** This method will place the binary representation of a short into
	 *  a byte buffer using big endian format.
	 *
	 * @param  value   The value to insert.
	 *
	 * @retrun A byte array containing the short.
	 *
	 */
	static byte[] write_short(int pv_short) {
		byte[] ibuffer = new byte[2];

		ibuffer[0] = (byte) ((pv_short >>> 8) & 0xff);
		ibuffer[1] = (byte) ((pv_short) & 0xff);

		return ibuffer;
	} // end write_short

	//-------------------------------------------------------------
	/** This method will place the binary representation of an int into
	 *  a byte buffer using big endian byte format.
	 *
	 * @param  value   The value to insert.
	 *
	 * @retrun A byte array containing the int.
	 *
	 */
	static byte[] write_int(int pv_int) {
		byte[] ibuffer = new byte[4];

		ibuffer[0] = (byte) ((pv_int >>> 24) & 0xff);
		ibuffer[1] = (byte) ((pv_int >>> 16) & 0xff);
		ibuffer[2] = (byte) ((pv_int >>> 8) & 0xff);
		ibuffer[3] = (byte) ((pv_int) & 0xff);

		return ibuffer;
	} // end write_int

	//-------------------------------------------------------------
	/** This method will place the binary representation of a long into
	 *  a byte buffer using big endian byte fromat.
	 *
	 * @param  value   The value to insert.
	 *
	 * @retrun A byte array containing the long.
	 *
	 */
	static byte[] write_long(long pv_long) {
		byte[] ibuffer = new byte[8];

		ibuffer[0] = (byte) ((pv_long >>> 56) & 0xff);
		ibuffer[1] = (byte) ((pv_long >>> 48) & 0xff);
		ibuffer[2] = (byte) ((pv_long >>> 40) & 0xff);
		ibuffer[3] = (byte) ((pv_long >>> 32) & 0xff);
		ibuffer[4] = (byte) ((pv_long >>> 24) & 0xff);
		ibuffer[5] = (byte) ((pv_long >>> 16) & 0xff);
		ibuffer[6] = (byte) ((pv_long >>> 8) & 0xff);
		ibuffer[7] = (byte) ((pv_long) & 0xff);

		return ibuffer;
	} // end write_long

} // end class toByte
