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

package com.tandem.t4jdbc;

import java.util.logging.*;
import java.sql.SQLException;
import javax.transaction.xa.XAException;

public class T4XALogger {
  private Logger m_t4logger;
  private T4Properties m_t4props;
  private T4LogFormatter m_lf;

  T4XALogger(SQLMXConnection p_mxconn) {
    m_t4props = p_mxconn.props_;
    m_t4logger = m_t4props.getLogger();
    m_lf = new T4LogFormatter();
  }


  void logAndIgnore(String p_logContent, Level p_level, String className, String methodName, Object[] params) {
    try {
      log(p_logContent, p_level, className, methodName, params);
    } catch (XAException xaex) {
      // ignore
    }
  }


  boolean isLoggable(Level p_level){
    try{
      return ( (m_t4logger.isLoggable(p_level)) || (m_t4props.getLogWriter() != null));
    }catch(Exception e){
      return (m_t4logger.isLoggable(p_level));
    }
  }

  void log(String p_logContent,
           Level p_level,
           String className,
           String methodName,
           Object [] params) throws XAException {
    try {
      if (m_t4logger.isLoggable(p_level) == true) {
        m_t4logger.logp(Level.FINE, className, methodName, p_logContent, params);
      }

      if (m_t4props.getLogWriter() != null) {
        LogRecord lr = new LogRecord(p_level, "");
        lr.setParameters(params);
        lr.setSourceClassName(className);
        lr.setSourceMethodName(methodName);
        lr.setMessage(p_logContent);
        String temp = m_lf.format(lr);
        m_t4props.getLogWriter().println(temp);
      }
    }
    catch (Exception ex) {
      throw new XAException(ex.getMessage());
    }
  }

}