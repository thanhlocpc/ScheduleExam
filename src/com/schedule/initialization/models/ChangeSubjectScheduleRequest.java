package com.schedule.initialization.models;

public class ChangeSubjectScheduleRequest {
    private String courseName;
    private String oldDate;


    private String date;
    private int shift;
    private int subjectScheduleIndex;

    public ChangeSubjectScheduleRequest(String courseName, String oldDate, String date, int shift, int subjectScheduleIndex) {
        this.courseName = courseName;
        this.oldDate = oldDate;
        this.date = date;
        this.shift = shift;
        this.subjectScheduleIndex = subjectScheduleIndex;
    }
    public ChangeSubjectScheduleRequest(){

    }
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getSubjectScheduleIndex() {
        return subjectScheduleIndex;
    }

    public void setSubjectScheduleIndex(int subjectScheduleIndex) {
        this.subjectScheduleIndex = subjectScheduleIndex;
    }

    public String getOldDate() {
        return oldDate;
    }

    public void setOldDate(String oldDate) {
        this.oldDate = oldDate;
    }
}
