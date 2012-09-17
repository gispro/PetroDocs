/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.util;

import java.util.HashMap;
import org.apache.log4j.BasicConfigurator;

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
    
    public static void log(int category, String message, int SID){
        
    }
    
}
