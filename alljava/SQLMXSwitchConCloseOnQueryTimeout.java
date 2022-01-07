/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tandem.t4jdbc;

/**
 *
 * @author gargesh
 */
interface SQLMXSwitchConCloseOnQueryTimeout {
    
    // Raises an exception upon setting of Query Timeout
    static String DEFAULT = "DEFAULT";
    
    // Does nothing upon Query exceeding value for Query Time-out
    static String IGNORE = "IGNORE";  
    
    // Closes connection if query exceeds Query Time-out value
    static String ON = "ON"; 
    
}
