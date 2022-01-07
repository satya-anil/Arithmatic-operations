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
XA* messages have the following format.

 Input Parameters
 DIALOGUE_ID_def 		dialogueId
 BrkXaRequest                   *request

 Parameter Description
 dialogueId	Provided at connection time and used to make sure the message comes from the same client.
 request	Pointer to the structure which describes input parameters.
*/

package com.tandem.t4jdbc;
import java.util.Locale;
import java.sql.SQLException;

class BrkXaMessage {
  private       int          m_dialogueId;
  private       Locale       m_locale;
  private       T4Properties m_t4props;
  private final int          m_endMapList = 0;


  BrkXaMessage(T4Properties t4props, int p_dialogueId) {
       m_t4props = t4props;
       m_dialogueId = p_dialogueId;
       m_locale     = t4props.getLocale();
  }


	LogicalByteArray marshal(BrkXaRequest p_brkXaRequest, SQLMXXid xid,
			int rmid, int flag, int operation) throws SQLException {
    int           maplength;
    int           wlength;
    int           message_length;
    short         number_of_param = 2;
    LogicalByteArray wbuffer = null;

    // 2 input parameter pointers plus 'end' indicator
    maplength = (number_of_param + 1) * 4;

    wlength = 0;

    //
    // calculate length of the buffer for each parameter
    //
    //
    //  length of DIALOGUE_ID_def dialogueId (it's an int/long)
    //
    wlength = wlength + 4;

    //
    // length of BrkXa*Request
    //
    wlength   = wlength + p_brkXaRequest.totalSizeOf();

    //
    // message length
    //
    message_length = wlength + maplength;

    //
    // Get a buffer
    //
//    wbuffer = new MessageBuffer(m_locale, message_length, 0,(number_of_param * 4), maplength);
    wbuffer = new LogicalByteArray(message_length, Header.sizeOf(),false);

    //
    // copy DIALOGUE_ID_def dialogueId
    //
    wbuffer.insertInt(m_dialogueId);

		wbuffer.insertInt(p_brkXaRequest.totalSizeOf());
		wbuffer.insertInt(p_brkXaRequest.m_ReqReplyFlag);
		wbuffer.insertInt(p_brkXaRequest.m_ReqCode);
		wbuffer.insertInt(p_brkXaRequest.m_filler);
		
		switch (operation) {			
			case BrkXaReply.BRK_XA_START:
			case BrkXaReply.BRK_XA_END:
			case BrkXaReply.BRK_XA_PREPARE:
			case BrkXaReply.BRK_XA_COMMIT:
			case BrkXaReply.BRK_XA_ROLLBACK:
			case BrkXaReply.BRK_XA_FORGET:
				//xid def
				wbuffer.insertInt(xid.getFormatID());
				wbuffer.insertInt(xid.gtrid_length);
				wbuffer.insertInt(xid.bqual_length);
				wbuffer.insertByteArray(xid.getData(), xid.dataLength());
				
				wbuffer.insertInt(rmid); //rmid
				wbuffer.insertInt(flag); //flag
				if (operation == BrkXaReply.BRK_XA_START) {
					wbuffer.insertInt(0); //timeout is 0 not used by XABroker
				}
				break;
			case BrkXaReply.BRK_XA_RECOVER:				
				 wbuffer.insertInt(0);//count is 0 not used by XABroker.
				 wbuffer.insertInt(rmid);
				wbuffer.insertInt(flag);
				break;
			case BrkXaReply.BRK_XA_GET_TRANSACTION_STATE:
				break;
			case BrkXaReply.BRK_XA_GET_TIMEOUT:
				wbuffer.insertInt(rmid);
				break;
				
		}

		wbuffer.insertInt(m_endMapList);

		return wbuffer;
	}
} // End of class BrkXaMessage