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

import java.sql.*;
import javax.transaction.xa.*;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.text.MessageFormat;
import java.util.logging.*;

class SQLMXXAMessages {

	//------------------------------------------------------------------------------------------------
	static XAException createXAException(T4Properties t4props,
			Locale msgLocale, String messageId, Object mA1, Object mA2) {

		Object[] mAs = new Object[2];

		mAs[0] = mA1;
		mAs[1] = mA2;

		return createXAException(t4props, msgLocale, messageId, mAs);

	}

	//------------------------------------------------------------------------------------------------
	static XAException createXAException(T4Properties t4props,
			Locale msgLocale, String messageId, Object messageArgument) {
		Object[] mAs = new Object[1];

		mAs[0] = messageArgument;

		return createXAException(t4props, msgLocale, messageId, mAs);

	}

	//------------------------------------------------------------------------------------------------
	static XAException createXAException(T4Properties t4props,
			Locale msgLocale, String messageId, Object[] messageArguments) {
		if (t4props != null
				&& t4props.t4Logger_.isLoggable(Level.SEVERE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(t4props, msgLocale,
					messageId, messageArguments);
			t4props.t4Logger_.logp(Level.SEVERE, "SQLMXXAMessages",
					"createXAException", "", p);
		} else if (T4Properties.t4GlobalLogger != null
				&& T4Properties.t4GlobalLogger.isLoggable(Level.SEVERE) == true) {
			Object p[] = T4LoggingUtilities.makeParams(t4props, msgLocale,
					messageId, messageArguments);
			T4Properties.t4GlobalLogger.logp(Level.SEVERE, "SQLMXXAMessages",
					"createXAException", "", p);
		}

		Locale currentLocale;
		int xacode;

		if (msgLocale == null) {
			currentLocale = Locale.getDefault();
		} else {
			currentLocale = msgLocale;
		}
		try {
			PropertyResourceBundle messageBundle = (PropertyResourceBundle) ResourceBundle
					.getBundle("SQLMXXAMessages", currentLocale);

			MessageFormat formatter = new MessageFormat("");
			formatter.setLocale(currentLocale);
			formatter.applyPattern(messageBundle.getString(messageId + "_msg"));

			String message = formatter.format(messageArguments);

			return new XAException(message);
		} catch (MissingResourceException e) {
			// If the resource bundle is not found, concatenate the messageId and the parameters
			String message;
			int i = 0;

			message = "The message id: " + messageId;
			if (messageArguments != null) {
				message = message.concat(" With parameters: ");
				while (true) {
					message = message.concat(messageArguments[i++].toString());
					if (i >= messageArguments.length) {
						break;
					} else {
						message = message.concat(",");
					}
				}
			} // end if

			return new XAException(message);
		} // end catch
	} // end

} // end class SQLMXXAMessages
