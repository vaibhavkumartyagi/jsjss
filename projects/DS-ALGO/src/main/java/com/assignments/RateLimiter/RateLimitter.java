package com.assignments.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitter {

    private int allowedToken;
    private long startTime;
    private int windowTime;
    private int allowedTokenMax =100;
    Object lock = new Object();

    Map<String,UserDetails> map = new ConcurrentHashMap<>();

    public static class UserDetails{
        private long startTime;
        private int allowedTokenMax;
        public int allowedToken;

        public UserDetails(int allowedTokenMax ){
            this.allowedTokenMax = allowedTokenMax;
            startTime = System.currentTimeMillis();
        }

    }

    public void init(){
        allowedToken = allowedTokenMax;
        windowTime = 60*1000;
        startTime = System.currentTimeMillis();
    }


    public boolean processRequest(String userId){

        if(! isAllowed(userId)){
            return false;
        }
        return true;

    }

    public boolean isAllowed(String userId){

        UserDetails userDetails = map.get(userId);

        if( userDetails == null){

            userDetails = new UserDetails(allowedTokenMax);
            map.put(userId,userDetails);
        }
        synchronized (userDetails ) {

            if (userDetails.startTime + windowTime > System.currentTimeMillis()) {
                userDetails.allowedToken = allowedTokenMax;
                userDetails.startTime = System.currentTimeMillis();
            }

            if (userDetails.allowedToken <= 0) {
                return false;
            }

            if (userDetails.startTime + windowTime < System.currentTimeMillis() && userDetails.allowedToken > 0) {
                userDetails.allowedToken--;
                return true;
            }
        }
        return false;
    }
}
