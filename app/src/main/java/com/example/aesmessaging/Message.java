package com.example.aesmessaging;

public class Message {
    String id, number,message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {
    }

    public Message(String id, String number, String message) {
        this.id = id;
        this.number = number;
        this.message = message;
    }
}
