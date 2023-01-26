package models;

import java.util.Objects;

/*
lớp thi được chia nhỏ từ lớp gốc
VD: LTM DH20DTA  có 103 sinh viên được chia thành những lớp thi có sức chứa 30 sinh viên. Chia thành 4 lớp thi gồm
30/30/30/23
 */
public class ExamRoom implements Cloneable{
    private RegistrationClass registrationClass;//môn học
    private ClassRoom room;//phòng học để tổ chức thi
    private int index;//thứ tự phòng được chia từ tổng sỉ số của lớp đó
    private int capacity;
    public ExamRoom clone() throws CloneNotSupportedException {
        ExamRoom er=(ExamRoom) super.clone();
        er.setRoom(room.clone());
        er.setRegistrationClass(registrationClass.clone());
        er.setIndex(this.index);
        er.setCapacity(this.capacity);
        return er;
    }
    public RegistrationClass getRegistrationClass() {
        return registrationClass;
    }

    public void setRegistrationClass(RegistrationClass registrationClass) {
        this.registrationClass = registrationClass;
    }

    public ClassRoom getRoom() {
        return room;
    }

    public void setRoom(ClassRoom room) {
        this.room = room;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ExamRoom(RegistrationClass registrationClass) {
        this.registrationClass = registrationClass;
    }

    @Override
    public String toString() {
        return
//                registrationClass.getName() + "-" + registrationClass.getId() +
//                        ", " + registrationClass.getGrade().getName() +
//                        ", " + room.getName() +
//                        ",index:" + index +
//                        ", sl:" + capacity
                registrationClass.getName() + "," + registrationClass.getId() +
                        "," + registrationClass.getGrade().getName() +
                        "," + room.getName() +
                        "," + index +
                        "," + capacity
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamRoom examRoom = (ExamRoom) o;
        return index == examRoom.index && capacity == examRoom.capacity && Objects.equals(registrationClass, examRoom.registrationClass) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationClass, room, index, capacity);
    }

    //tính chỗ ngồi còn dư
    public int remainSlot() {
        return room.getCapacityExam() - capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }


}
