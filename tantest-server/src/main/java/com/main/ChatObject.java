package com.main;

public class ChatObject {

    private String email;
    private String message;

    public ChatObject() {
    }

    public ChatObject(String userName, String message) {
        super();
        this.email = userName;
        this.message = message;
    }

    public String getUserName() {
        return email;
    }
    public void setUserName(String userName) {
        this.email = userName;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
