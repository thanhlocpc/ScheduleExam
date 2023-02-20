package models;

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
    private Grade grade;
    private int estimatedClassSize; // sĩ số lớp dự kiến
    private int estimatedClassSizeReal; // sĩ số lớp thực tế
    private int dbId;
    private List<Student> listStudent;
    public RegistrationClass clone() throws CloneNotSupportedException {
        RegistrationClass rc=(RegistrationClass) super.clone();
        rc.setName(this.name);
        rc.setId(this.id);
        rc.setGrade(this.grade.clone());
        rc.setEstimatedClassSize(this.estimatedClassSize);
        rc.setEstimatedClassSizeReal(this.estimatedClassSizeReal);
        rc.setDbId(this.dbId);
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
        return Objects.hash(id, name, estimatedClassSize, estimatedClassSizeReal, listStudent, date, beginLearning, amountLearning, subject, classRoom);
    }

    @Override
    public String toString() {
        return "RegistrationClass{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", estimatedClassSize=" + estimatedClassSize +
                ", estimatedClassSizeReal=" + estimatedClassSizeReal +
                '}';
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
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

    public int getEstimatedClassSize() {
        return estimatedClassSize;
    }

    public void setEstimatedClassSize(int estimatedClassSize) {
        this.estimatedClassSize = estimatedClassSize;
    }

    public int getEstimatedClassSizeReal() {
        return estimatedClassSizeReal;
    }

    public void setEstimatedClassSizeReal(int estimatedClassSizeReal) {
        this.estimatedClassSizeReal = estimatedClassSizeReal;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getBeginLearning() {
        return beginLearning;
    }

    public void setBeginLearning(int beginLearning) {
        this.beginLearning = beginLearning;
    }

    public int getAmountLearning() {
        return amountLearning;
    }

    public void setAmountLearning(int amountLearning) {
        this.amountLearning = amountLearning;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }

    private int date; // ngày ... trong tuần
    private int beginLearning; // tiết bắt đầu 1 2 3...
    private int amountLearning; // số tiết học
    private Subject subject;//môn học của học phần này
    private ClassRoom classRoom; // lớp này học phòng này

    public RegistrationClass(String id, String name, int estimatedClassSize, int estimatedClassSizeReal, Subject subject, Grade grade,int dbId) {
        this.id = id;
        this.name = name;
        this.estimatedClassSize = estimatedClassSize;
        this.estimatedClassSizeReal = estimatedClassSizeReal;
        this.subject = subject;
        this.grade = grade;
        this.dbId=dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
