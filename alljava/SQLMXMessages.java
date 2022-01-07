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
/*Change Log
 * Vproc  
 * Solution No. 10-100713-1762
 * Methods Changed:createSQLException(T4Properties , Locale, String, Object[]) 
 * Changed By: Niveditha
 */
package com.tandem.t4jdbc;

import java.sql.SQLWarning;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

class SQLMXMessages {
	static Logger getMessageLogger(T4Properties t4props) {
		return (t4props != null) ? t4props.t4Logger_
				: T4Properties.t4GlobalLogger;
	}

	static void setSQLWarning(T4Properties t4props, SQLMXHandle handle,
			ERROR_DESC_LIST_def sqlWarning) {
		Logger log = getMessageLogger(t4props);


		int curErrorNo;
		ERROR_DESC_def error_desc_def[];
		SQLWarning sqlWarningLeaf;

		if (sqlWarning.length == 0) {
			handle.setSqlWarning(null);
			return;
		}

		error_desc_def = sqlWarning.buffer;
		for (curErrorNo = 0; curErrorNo < sqlWarning.length; curErrorNo++) {
			if (log != null && log.isLoggable(Level.WARNING)) {
				Object p[] = new Object[] { t4props,
						"Text: " + error_desc_def[curErrorNo].errorText,
						"SQLState: " + error_desc_def[curErrorNo].sqlstate,
						"SQLCode: " + error_desc_def[curErrorNo].sqlcode };
				log.logp(Level.WARNING, "SQLMXMessages", "setSQLWarning", "", p);
			}

			sqlWarningLeaf = new SQLWarning(
					error_desc_def[curErrorNo].errorText,
					error_desc_def[curErrorNo].sqlstate,
					error_desc_def[curErrorNo].sqlcode);
			handle.setSqlWarning(sqlWarningLeaf);
		}
		return;
	} // end setSQLWarning
	
//	 ------------------------------------------------------------------------------------------------
	static void throwSQLException(T4Properties t4props,
			ERROR_DESC_LIST_def SQLError) throws SQLMXException {
		Logger log = getMessageLogger(t4props);
		Locale locale = (t4props != null) ? t4props.getLocale() : Locale
				.getDefault();

		SQLMXException sqlException = null;
		SQLMXException sqlExceptionHead = null;
		int curErrorNo;

		if (SQLError.length == 0) {
			throw createSQLException(t4props, locale,
					"No messages in the Error description", null);
		}

		for (curErrorNo = 0; curErrorNo < SQLError.length; curErrorNo++) {
			if (log != null && log.isLoggable(Level.SEVERE)) {
				Object p[] = new Object[] { t4props,
						"Text: " + SQLError.buffer[curErrorNo].errorText,
						"SQLState: " + SQLError.buffer[curErrorNo].sqlstate,
						"SQLCode: " + SQLError.buffer[curErrorNo].sqlcode };
				log.logp(Level.SEVERE, "SQLMXMessages", "throwSQLException", "",
						p);
			}

			if (SQLError.buffer[curErrorNo].errorCodeType == TRANSPORT.ESTIMATEDCOSTRGERRWARN) {
				//
				// NCS said it was an SQL error, but it really wasn't it was a
				// NCS resource governing error
				//
				sqlException = SQLMXMessages.createSQLException(t4props, locale,
						"resource_governing", null);
			} else {
				sqlException = new SQLMXException(
						SQLError.buffer[curErrorNo].errorText,
						SQLError.buffer[curErrorNo].sqlstate,
						SQLError.buffer[curErrorNo].sqlcode, null);
			}
			if (curErrorNo == 0) {
				sqlExceptionHead = sqlException;
			} else {
				sqlExceptionHead.setNextException(sqlException);
			}
		}

		throw sqlExceptionHead;
	}
	
	// ------------------------------------------------------------------------------------------------
	static void throwSQLException(T4Properties t4props, SQLWarningOrError[] we1)
			throws SQLMXException {
		Logger log = getMessageLogger(t4props);
		Locale locale = (t4props != null) ? t4props.getLocale() : Locale
				.getDefault();

		SQLMXException sqlException = null;
		SQLMXException sqlExceptionHead = null;
		int curErrorNo;

		if (we1.length == 0) {
			throw createSQLException(t4props, locale,
					"No messages in the Error description", null);
		}

		for (curErrorNo = 0; curErrorNo < we1.length; curErrorNo++) {
			if (log != null && log.isLoggable(Level.SEVERE)) {
				Object p[] = new Object[] { t4props,
						"Text: " + we1[curErrorNo].text,
						"SQLState: " + we1[curErrorNo].sqlState,
						"SQLCode: " + we1[curErrorNo].sqlCode };
				log.logp(Level.SEVERE, "SQLMXMessages", "throwSQLException", "",
						p);
			}

			sqlException = new SQLMXException(we1[curErrorNo].text,
					we1[curErrorNo].sqlState, we1[curErrorNo].sqlCode, null);
			if (curErrorNo == 0) {
				sqlExceptionHead = sqlException;
			} else {
				sqlExceptionHead.setNextException(sqlException);
			}
		} // end for

		throw sqlExceptionHead;
	} // end throwSQLException

	static void setSQLWarning(T4Properties t4props, SQLMXHandle handle,
			SQLWarningOrError[] we1) {
		Logger log = getMessageLogger(t4props);

		int curErrorNo;
		SQLWarning sqlWarningLeaf;

		if (we1.length == 0) {
			handle.setSqlWarning(null);
			return;
		}

		for (curErrorNo = 0; curErrorNo < we1.length; curErrorNo++) {
			if (log != null && log.isLoggable(Level.WARNING)) {
				Object p[] = new Object[] { t4props,
						"Text: " + we1[curErrorNo].text,
						"SQLState: " + we1[curErrorNo].sqlState,
						"SQLCode: " + we1[curErrorNo].sqlCode };
				log.logp(Level.WARNING, "SQLMXMessages", "setSQLWarning", "", p);
			}

			sqlWarningLeaf = new SQLWarning(we1[curErrorNo].text,
					we1[curErrorNo].sqlState, we1[curErrorNo].sqlCode);
			handle.setSqlWarning(sqlWarningLeaf);
		} // end for
		return;
	}

	
	static SQLWarning createSQLWarning(T4Properties t4props, String messageId,
			Object[] messageArguments) {
		Logger log = getMessageLogger(t4props);

		if (log != null && log.isLoggable(Level.WARNING)) {
			Object p[] = T4LoggingUtilities.makeParams(t4props, messageId,
					messageArguments);
			log.logp(Level.WARNING, "SQLMXMessages", "createSQLWarning", "", p);
		}

		Locale currentLocale = (t4props != null) ? t4props.getLocale() : Locale
				.getDefault();
		int sqlcode = 1;
		SQLWarning ret = null;

		try {
			PropertyResourceBundle messageBundle = (PropertyResourceBundle) ResourceBundle
					.getBundle("SQLMXMessages", currentLocale);

			MessageFormat formatter = new MessageFormat("");
			formatter.setLocale(currentLocale);
			formatter.applyPattern(messageBundle.getString(messageId + "_msg"));

			String message = formatter.format(messageArguments);
			String sqlState = messageBundle.getString(messageId + "_sqlstate");
			String sqlcodeStr = messageBundle.getString(messageId + "_sqlcode");

			if (sqlcodeStr != null) {
				try {
					sqlcode = Integer.parseInt(sqlcodeStr);
				} catch (NumberFormatException e1) {
					// use 1 as default
				}
			}

			ret = new SQLWarning(message, sqlState, sqlcode);
		} catch (MissingResourceException e) {
			// If the resource bundle is not found, concatenate the messageId
			// and the parameters
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

			ret = new SQLWarning(message, "01000", 1);
		}

		return ret;
	}

	
	// ------------------------------------------------------------------------------------------------
	static SQLMXException createSQLException(T4Properties t4props,
			Locale msgLocale, String messageId, Object mA1, Object mA2) {

		Object[] mAs = new Object[2];

		mAs[0] = mA1;
		mAs[1] = mA2;

		return createSQLException(t4props, msgLocale, messageId, mAs);

	} // end createSQLException

	// ------------------------------------------------------------------------------------------------
	static SQLMXException createSQLException(T4Properties t4props,
			Locale msgLocale, String messageId, Object messageArgument) {
		Object[] mAs = new Object[1];

		mAs[0] = messageArgument;

		return createSQLException(t4props, msgLocale, messageId, mAs);

	} // end createSQLException

	// ------------------------------------------------------------------------------------------------
	static SQLMXException createSQLException(T4Properties t4props,
			Locale msgLocale, String messageId, Object[] messageArguments) {
		Logger log = getMessageLogger(t4props);

		if (log != null && log.isLoggable(Level.SEVERE)) {
			Object p[] = T4LoggingUtilities.makeParams(t4props, messageId,
					messageArguments);
			log.logp(Level.SEVERE, "SQLMXMessages", "createSQLException", "", p);
		}

		Locale currentLocale;
		int sqlcode;

		if (msgLocale == null) {
			currentLocale = Locale.getDefault();
		} else {
			currentLocale = msgLocale;

		}
		try {
			//Soln.: 10-100713-1762
			PropertyResourceBundle messageBundle = null;
			try {
				messageBundle = (PropertyResourceBundle) ResourceBundle
						.getBundle("SQLMXMessages", currentLocale);
			} catch (MissingResourceException me) {
				// If the resource bundle is not found, set the Locale to English and load its properties file
				if ((currentLocale.getLanguage()) != "en") {
					Locale tmpLocale = new Locale("en", "US");
					currentLocale = tmpLocale;
				}
				messageBundle = (PropertyResourceBundle) ResourceBundle
						.getBundle("SQLMXMessages", currentLocale);
			}
			//End of Soln.: 10-100713-1762
			MessageFormat formatter = new MessageFormat("");
			formatter.setLocale(currentLocale);
			formatter.applyPattern(messageBundle.getString(messageId + "_msg"));

			String message = formatter.format(messageArguments);
			String sqlState = messageBundle.getString(messageId + "_sqlstate");
			String sqlcodeStr = messageBundle.getString(messageId + "_sqlcode");

			if (sqlcodeStr != null) {
				try {
					sqlcode = Integer.parseInt(sqlcodeStr);
					sqlcode = -sqlcode;
				} catch (NumberFormatException e1) {
					sqlcode = -1;
				}
			} else {
				sqlcode = -1;

			}
			return new SQLMXException(message, sqlState, sqlcode, messageId);
		}
		//    catch (MissingResourceException e)
		catch (Exception e) {
			// If the resource bundle is not found, concatenate the messageId
			// and the parameters
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

			return new SQLMXException(message, "HY000", -1, messageId);
		} // end catch
	} // end createSQLException

	// ------------------------------------------------------------------------------------------------
	static void throwUnsupportedFeatureException(T4Properties t4props,
			Locale locale, String s) throws SQLMXException {
		Object[] messageArguments = new Object[1];

		messageArguments[0] = s;
		throw SQLMXMessages.createSQLException(t4props, locale,
				"unsupported_feature", messageArguments);
	} // end throwUnsupportedFeatureException

	// ------------------------------------------------------------------------------------------------
	static void throwDeprecatedMethodException(T4Properties t4props,
			Locale locale, String s) throws SQLMXException {
		Object[] messageArguments = new Object[1];

		messageArguments[0] = s;
		throw SQLMXMessages.createSQLException(t4props, locale,
				"deprecated_method", messageArguments);
	} // end throwDeprecatedMethodException

} // end class SQLMXMessages
