package models;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/

// PHÒNG HỌC(VẬT LÍ)
public class ClassRoom {
    private String id;
    private String name;
    private int capacityBase;
    private int capacityExam;

    public ClassRoom(String id, String name, int capacityBase, int capacityExam) {
        this.id = id;
        this.name = name;
        this.capacityBase = capacityBase;
        this.capacityExam = capacityExam;
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
}
