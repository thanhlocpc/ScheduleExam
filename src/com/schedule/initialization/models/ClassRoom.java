package com.schedule.initialization.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/

// PHÒNG HỌC(VẬT LÍ)
public class ClassRoom implements Cloneable, Serializable {
    private String id;
    private String name;
    private int capacityBase;
    private int capacityExam;
    private int type;//0:LT,1:TH

    private int buildingId;

    public ClassRoom(String id, String name, int capacityBase, int capacityExam, int type, int buildingId) {
        this.id = id;
        this.name = name;
        this.capacityBase = capacityBase;
        this.capacityExam = capacityExam;
        this.type = type;
        this.buildingId = buildingId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ClassRoom clone(){
        ClassRoom cl=new ClassRoom();
        cl.setId(this.id);
        cl.setName(this.name);
        cl.setCapacityBase(this.capacityBase);
        cl.setCapacityExam(this.capacityExam);
        cl.setType(this.type);
        cl.setBuildingId(this.buildingId);
        return cl;
    }
    public ClassRoom() {
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

    public int getCapacityBase() {
        return capacityBase;
    }

    public void setCapacityBase(int capacityBase) {
        this.capacityBase = capacityBase;
    }

    public int getType() {
        return type;
    }

    public int getCapacityExam() {
        return capacityExam;
    }

    public void setCapacityExam(int capacityExam) {
        this.capacityExam = capacityExam;
    }

    @Override
    public String toString() {
        return "ClassRoom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacityBase=" + capacityBase +
                ", capacityExam=" + capacityExam +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassRoom classRoom = (ClassRoom) o;
        return capacityBase == classRoom.capacityBase && capacityExam == classRoom.capacityExam && type == classRoom.type && Objects.equals(id, classRoom.id) && Objects.equals(name, classRoom.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacityBase, capacityExam, type);
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }
}