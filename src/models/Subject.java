package models;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/
public class Subject {
    private String id;
    private String name;
    private int credit;
    private int examForms; // hình thức thi: 0 thi lt, 1 thi thực hành

    public Subject(String id, String name, int credit, int examForms) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.examForms = examForms;
    }

    public Subject() {
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

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getExamForms() {
        return examForms;
    }

    public void setExamForms(int examForms) {
        this.examForms = examForms;
    }
}
