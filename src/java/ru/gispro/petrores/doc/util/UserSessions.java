/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.util;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.Priority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
 *
 * @author abusurin
 */
public class UserSessions {
    static HashMap <String,String> sessions;
    static {
        sessions = new HashMap<String, String>();
        BasicConfigurator.configure();
    }
    public static boolean registerUserSession(String user, String sid){
        return user != null && sid != null
                ? !sid.equals(sessions.put(user, sid))
                : false;
    }
    
    public static String getFacadeCallRequestUser(){
        ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra == null ? null : sra.getRequest();
        String user  =  req == null ? null : req.getRemoteUser();
        return user;
    }
    
    public static void trace(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        trace(logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    public static void trace(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        log(Level.TRACE_INT, logerName, user, oOpCode, sOpName, docID, bOK, message, t);
    }
    
    public static void warn(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        warn(logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    public static void warn(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        log(Level.WARN_INT, logerName, user, oOpCode, sOpName, docID, bOK, message, t);
    }

    public static void info(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        info(logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    public static void info(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        log(Level.INFO_INT, logerName, user, oOpCode, sOpName, docID, bOK, message, t);
    }
    
    public static void fatal(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        info(logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    public static void fatal(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        log(Level.FATAL_INT, logerName, user, oOpCode, sOpName, docID, bOK, message, t);
    }
    
    public static void error(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        error(logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    public static void error(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        log(Level.ERROR_INT, logerName, user, oOpCode, sOpName, docID, bOK, message, t);
    }
    
    public static void debug(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        debug(logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    public static void debug(String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        log(Level.DEBUG_INT, logerName, user, oOpCode, sOpName, docID, bOK, message, t);
    }
    
    public static void log( int category, String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message){
        log(category, logerName, user, oOpCode, sOpName, docID, bOK, message, null);
    }
    /* Possible category 
    *   Level.TRACE_INT:
    *   Level.WARN_INT:
    *   Level.INFO_INT:
    *   Level.FATAL_INT:
    *   Level.ERROR_INT:
    *   Level.DEBUG_INT:
    */
    public static void log( int category, String logerName,
                            Object user, Object oOpCode, String sOpName, 
                            Object docID, boolean bOK, String message, Throwable t){
        MDC.put("user", user);
        MDC.put("OP_CODE", oOpCode);
        MDC.put("OP_NAME", sOpName);
        MDC.put("DOC_ID", docID == null ? "-" : docID);
        MDC.put("OP_STATUS", bOK ? "Success" : "Error");
        Logger lgr = Logger.getLogger(logerName);
        switch( category ){
            case Level.TRACE_INT:   lgr.trace(message, t);  break;
            case Level.WARN_INT:    lgr.warn(message, t);   break;
            case Level.INFO_INT:    lgr.info(message, t);   break;
            case Level.FATAL_INT:   lgr.fatal(message, t);  break;
            case Level.ERROR_INT:   lgr.error(message, t);  break;
            case Level.DEBUG_INT:   lgr.debug(message, t);  break;
            default:
                lgr.error(  "Log category "+category+" not supported in \""+message+"\"", 
                            new Exception("Log category "+category+" not supported"));
        }
    }
    
}
