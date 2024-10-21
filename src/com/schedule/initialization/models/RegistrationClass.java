package com.schedule.initialization.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/

// LỚP ĐĂNG KÍ HỌC PHẦN
public class RegistrationClass implements Cloneable, Serializable {
    private String id;
    private String name;
    private String grade;
    private int estimatedClassSizeReal; // sĩ số lớp thực tế
    private int dbId;
    public RegistrationClass clone() throws CloneNotSupportedException {
        RegistrationClass rc=(RegistrationClass) super.clone();
        rc.setName(this.name);
        rc.setId(this.id);
        rc.setGrade(this.grade);
        rc.setEstimatedClassSizeReal(this.estimatedClassSizeReal);
        return rc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationClass that = (RegistrationClass) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, estimatedClassSizeReal, subject);
    }

    @Override
    public String toString() {
        return "RegistrationClass{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", estimatedClassSizeReal=" + estimatedClassSizeReal +
                ", subjectId=" + subject.getId() +
                '}';
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
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


    public int getEstimatedClassSizeReal() {
        return estimatedClassSizeReal;
    }

    public void setEstimatedClassSizeReal(int estimatedClassSizeReal) {
        this.estimatedClassSizeReal = estimatedClassSizeReal;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }


    private Subject subject;//môn học của học phần này

    public RegistrationClass(String id, String name, int estimatedClassSizeReal, Subject subject, String grade) {
        this.id = id;
        this.name = name;
        this.estimatedClassSizeReal = estimatedClassSizeReal;
        this.subject = subject;
        this.grade = grade;
    }

}
