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

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Locale;

class LogicalByteArray {
	static private java.lang.ThreadLocal threadArray = new java.lang.ThreadLocal();

	private byte[] array;

	private ByteBuffer dataBuffer;

	private byte[] trailer;

	private boolean swap; // should we swap byte order

	private int loc; // current position

	private int length; // current bytes being used

	LogicalByteArray(int size, int startingLoc, boolean byteSwap) {
		loc = startingLoc;
		length = 0;
		swap = byteSwap;

		// TRANSPORT.IO_BUFFER_LENGTH is our minimum length so it is always safe
		// to read with the buffer
		resize((size > TRANSPORT.IO_BUFFER_LENGTH) ? size
				: TRANSPORT.IO_BUFFER_LENGTH);
	}

	void resize(int size) {
		byte[] old = (byte[]) threadArray.get();

		if (old == null || old.length < size) {
			array = new byte[size];
			if (old != null)
				System.arraycopy(old, 0, array, 0, old.length);

			threadArray.set(array);
		} else {
			array = old;
		}
	}

	void reset() {
		length = 0;
		loc = 0;
	}

	byte[] getBuffer() {
		return array;
	}

	int getTotalAllocated() {
		return array.length;
	}

	int getLength() {
		if (length < loc) {
			length = loc;
		}

		return length;
	}

	int getLocation() {
		return loc;
	}

	public void setLocation(int newLoc) {
		if (newLoc > length) {
			length = newLoc;
		}

		loc = newLoc;
	}

	void insertByte(byte value) {
		array[loc++] = value;
	}

	void insertByteArray(byte[] value, int len) {
		System.arraycopy(value, 0, array, loc, len);

		loc += len;
	}
//temp method which returns loc
//	int insertByteArray(byte[] value, int len,int temp) {
//		System.arraycopy(value, 0, array, loc, len);
//
//		loc += len;
//		return loc;
//	}
	
	//end
	void insertChar(char value) {
		array[loc++] = (byte) value;
	}

	void insertShort(short value) {
		if (swap) {
			array[loc + 1] = (byte) ((value >>> 8) & 0xff);
			array[loc] = (byte) ((value) & 0xff);
		} else {
			array[loc] = (byte) ((value >>> 8) & 0xff);
			array[loc + 1] = (byte) ((value) & 0xff);
		}

		loc += 2;
	}

	void insertInt(int value) {
		if (swap) {
			array[loc + 3] = (byte) ((value >>> 24) & 0xff);
			array[loc + 2] = (byte) ((value >>> 16) & 0xff);
			array[loc + 1] = (byte) ((value >>> 8) & 0xff);
			array[loc] = (byte) ((value) & 0xff);
		} else {
			array[loc] = (byte) ((value >>> 24) & 0xff);
			array[loc + 1] = (byte) ((value >>> 16) & 0xff);
			array[loc + 2] = (byte) ((value >>> 8) & 0xff);
			array[loc + 3] = (byte) ((value) & 0xff);
		}

		loc += 4;
	}
//temprorily adding this method which will return loc
//	int insertInt(int temp,int value) {
//		if (swap) {
//			array[loc + 3] = (byte) ((value >>> 24) & 0xff);
//			array[loc + 2] = (byte) ((value >>> 16) & 0xff);
//			array[loc + 1] = (byte) ((value >>> 8) & 0xff);
//			array[loc] = (byte) ((value) & 0xff);
//		} else {
//			array[loc] = (byte) ((value >>> 24) & 0xff);
//			array[loc + 1] = (byte) ((value >>> 16) & 0xff);
//			array[loc + 2] = (byte) ((value >>> 8) & 0xff);
//			array[loc + 3] = (byte) ((value) & 0xff);
//		}
//
//		loc += 4;
//		return loc;
//	}
	void insertLong(long value) {
		if (swap) {
			array[loc + 7] = (byte) ((value >>> 56) & 0xff);
			array[loc + 6] = (byte) ((value >>> 48) & 0xff);
			array[loc + 5] = (byte) ((value >>> 40) & 0xff);
			array[loc + 4] = (byte) ((value >>> 32) & 0xff);
			array[loc + 3] = (byte) ((value >>> 24) & 0xff);
			array[loc + 2] = (byte) ((value >>> 16) & 0xff);
			array[loc + 1] = (byte) ((value >>> 8) & 0xff);
			array[loc] = (byte) ((value) & 0xff);
		} else {
			array[loc] = (byte) ((value >>> 56) & 0xff);
			array[loc + 1] = (byte) ((value >>> 48) & 0xff);
			array[loc + 2] = (byte) ((value >>> 40) & 0xff);
			array[loc + 3] = (byte) ((value >>> 32) & 0xff);
			array[loc + 4] = (byte) ((value >>> 24) & 0xff);
			array[loc + 5] = (byte) ((value >>> 16) & 0xff);
			array[loc + 6] = (byte) ((value >>> 8) & 0xff);
			array[loc + 7] = (byte) ((value) & 0xff);
		}

		loc += 8;
	}

	void insertStringWithCharset(byte[] str, int charset) {
		if (str != null && str.length > 0) {
			this.insertString(str);
			this.insertInt(charset);
		} else {
			this.insertInt(0);
		}
	}

	void insertFixedString(byte[] buf, int len) {
		int dataLength;

		if (buf != null) {
			dataLength = (buf.length > len - 1) ? len - 1 : buf.length; // -1
																		// for
																		// the
																		// null,
																		// max
																		// dataLength
																		// is
																		// (len-1)
			this.insertByteArray(buf, dataLength);
		} else {
			dataLength = 0;
		}

		byte[] padding = new byte[len - dataLength]; // this will always be
														// at least 1 for the
														// null padding
		this.insertByteArray(padding, padding.length);
	}

	void insertString(byte[] buf) {
		if (buf != null && buf.length > 0) {
			this.insertInt(buf.length + 1);
			this.insertByteArray(buf, buf.length);
			this.insertByte((byte) 0);
		} else { // buffer is null or length 0
			this.insertInt(0);
		}
	}

	void insertString(byte[] str, boolean fixForServer) {
		if (str != null && str.length > 0) {
			this.insertInt(str.length + 1); // +1 null term
			this.insertByteArray(str, str.length);
			this.insertByte((byte) 0);
		} else {
			this.insertInt(1);
			this.insertByte((byte) 0);
		}
	}

	// /////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////

	boolean extractBoolean() {
		return (extractByte() == 0) ? false : true;
	}

	byte extractByte() {
		return array[loc++];
	}

	byte[] extractByteArray(int len) {
		byte[] a = new byte[len];

		System.arraycopy(array, loc, a, 0, len);
		loc += len;

		return a;
	}

	byte[] extractByteArray() {
		return extractByteArray(this.extractInt());
	}

	char extractChar() {
		return (char) extractByte();
	}

	short extractShort() {
		int value;

		if (swap) {
			value = ((array[loc]) & 0x00ff) | ((array[loc + 1] << 8) & 0xff00);
		} else {
			value = ((array[loc + 1]) & 0x00ff) | ((array[loc] << 8) & 0xff00);
		}

		loc += 2;

		return (short) value;
	}

	int extractInt() {
		int value;

		if (swap) {
			value = ((array[loc]) & 0x000000ff)
					| ((array[loc + 1] << 8) & 0x0000ff00)
					| ((array[loc + 2] << 16) & 0x00ff0000)
					| ((array[loc + 3] << 24) & 0xff000000);
		} else {
			value = ((array[loc + 3]) & 0x000000ff)
					| ((array[loc + 2] << 8) & 0x0000ff00)
					| ((array[loc + 1] << 16) & 0x00ff0000)
					| ((array[loc] << 24) & 0xff000000);
		}

		loc += 4;

		return value;
	}

	char[] extractChars(int loc,int index) {
		//
		// I'm assuming chars translate from 1 byte values.
		// This is probably a bad assumption, and needs to
		// be fixed.
		//		
		byte[] temp = extractByteArray(index);

		return toByte.read_chars(temp, 0, temp.length);
	} // end extractChar
	
	long extractUnsignedInt() {
		long value;

		if (swap) {
			value = ((array[loc]) & 0x000000ff)
					| ((array[loc + 1] << 8) & 0x0000ff00)
					| ((array[loc + 2] << 16) & 0x00ff0000)
					| ((array[loc + 3] << 24) & 0xff000000);
		} else {
			value = ((array[loc + 3]) & 0x000000ff)
					| ((array[loc + 2] << 8) & 0x0000ff00)
					| ((array[loc + 1] << 16) & 0x00ff0000)
					| ((array[loc] << 24) & 0xff000000);
		}

		loc += 4;

		return value & 0xffffffffL;
	}

	byte[] extractString() {
		int len = extractInt();
		byte[] str = new byte[0];

		if (len > 0) {
			str = extractByteArray(len - 1);
			extractByte(); // trailing null
		}
		return str;
	}

	byte[] extractByteString() {
		int len = extractInt();
		byte[] b = new byte[0];

		if (len > 0) {
			b = extractByteArray(len); // the packed length DOES NOT include
			// the null character
			extractByte(); // trailing null
		}

		return b;
	}

	void setDataBuffer(ByteBuffer buf) {
		this.dataBuffer = buf;
	}

	void setTrailer(byte[] buf) {
		this.trailer = buf;
	}

	ByteBuffer getDataBuffer() {
		return this.dataBuffer;
	}

	byte[] getTrailer() {
		return this.trailer;
	}

	void setByteSwap(boolean byteSwap) {
		this.swap = byteSwap;
	}

	boolean getByteSwap() {
		return this.swap;
	}

}
