package com.example.zavrsniprojektvojvodic.domain;

import java.util.*;
public class UserSession {

        private static UserSession instance;

        private String userFirstName;
        private String userLastName;
        private Set<String> privileges;

        private String OIB;

        private UserSession(String userName,String userLastname, Set<String> privileges,String OIB) {
            this.userFirstName = userName;
            this.userLastName=userLastname;
            this.privileges = privileges;
            this.OIB=OIB;
        }

        public static UserSession getInstace(String userName,String userLastName, Set<String> privileges,String OIB) {
            if(instance == null || instance.OIB==null) {
                instance = new UserSession(userName, userLastName,privileges,OIB);
            }
            return instance;
        }

        public String getUserFirstName() {
            return userFirstName;
        }

        public Set<String> getPrivileges() {
            return privileges;
        }

    public String getUserLastName() {
        return userLastName;
    }

    public String getOIB() {
        return OIB;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }



    public void cleanUserSession() {
            userFirstName = null;// or null
            userLastName=null;
            privileges = null;// or null
            OIB=null;
        }

        @Override
        public String toString() {
            return "UserSession{" +
                    "userName='" + userFirstName + '\'' +
                    ", privileges=" + privileges +
                    '}';
        }

}
