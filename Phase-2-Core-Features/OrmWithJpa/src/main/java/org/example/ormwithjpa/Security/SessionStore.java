package org.example.ormwithjpa.Security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStore {
    private static final Map<String,Long> sessions = new ConcurrentHashMap<>();

    public static void save(String session,Long userId){
        sessions.put(session,userId);
    }

    public static Long getUserId(String sessionId){
        return sessions.get(sessionId);
    }

    public static boolean isValid(String sessionId){
        return sessions.containsKey(sessionId);
    }

    public static String generateSession(){
        return UUID.randomUUID().toString();
    }

}
