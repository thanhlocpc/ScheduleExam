package com.schedule.initialization.models;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/

// HỌC KÌ
public class Semester {
    private String id;
    private String name;

    public Semester(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Semester() {
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

    @Override
    public String toString() {
        return "Semester{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
