package com.plainid.assignment.dao;

public class BattleResult {
    String status;
    String message;

    public BattleResult(String status, String message) {
        setStatus(status);
        setMessage(message);
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

}
