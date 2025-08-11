package com.micronimbus.doctorshaheb;

public class MessageDoctor {
    public static String Sent_BY_ME="me";
    public static String Sent_BY_BOT="bot";
    String message;
    String sentby;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentby() {
        return sentby;
    }

    public void setSentby(String sentby) {
        this.sentby = sentby;
    }

    public MessageDoctor(String message, String sentby) {
        this.message = message;
        this.sentby = sentby;
    }
}
