
package com.schedule.initialization.models;

public class ChangeScheduleRequest {
    private String subject;
    private String date;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public ChangeScheduleRequest(){

    }
    public ChangeScheduleRequest(String subject, String date) {
        this.subject = subject;
        this.date = date;
    }
}