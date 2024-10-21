package com.schedule.initialization.models;

import java.io.Serializable;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/
public class Subject implements  Serializable {
    private String id;
    private String name;
    private int examForms; // hình thức thi: 0 thi lt, 1 thi thực hành,2 :van dap
    private int examTime;

    public int getExamTime() {
        return examTime;
    }

    public void setExamTime(int examTime) {
        this.examTime = examTime;
    }

    public Subject(String id, String name, int examForms, int examTime) {
        this.id = id;
        this.name = name;
        this.examForms = examForms;
        this.examTime = examTime;

    }
    public Subject clone(){
        Subject s=new Subject();
        s.setId(this.getId());
        s.setName(this.getName());
        s.setExamForms(this.getExamForms());
        s.setExamTime(this.getExamTime());
        return s;
    }
    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", examForms=" + examForms +
                '}';
    }

    public Subject() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Subject other = (Subject) obj;
        return this.id.equals(other.getId());

    }
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExamForms() {
        return examForms;
    }

    public void setExamForms(int examForms) {
        this.examForms = examForms;
    }
}
