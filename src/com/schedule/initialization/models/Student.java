package com.schedule.initialization.models;

import java.util.List;
import java.util.Map;
/*
class sinh viên dùng để tính độ tối ưu của lịch thi
 */
public class Student {
    private String id;//id sinh viên
    private List<SubjectSchedule> examList;//lịch thi của sinh viên
    private Map<String,RegistrationClass> timeTable;//lịch học của sinh viên Map<id Subject, RegistrationClass>
}
