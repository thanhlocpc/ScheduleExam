package models;

/*
lớp thi được chia nhỏ từ lớp gốc
VD: LTM DH20DTA  có 103 sinh viên được chia thành những lớp thi có sức chứa 30 sinh viên. Chia thành 4 lớp thi gồm
30/30/30/23
 */
public class ExamRoom {
    private RegistrationClass registrationClass;//môn học
    private ClassRoom room;//phòng học để tổ chức thi
    private int index;//thứ tự phòng được chia từ tổng sỉ số của lớp đó
    private int capacity;
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
                 registrationClass.getName() +"-"+registrationClass.getId()+
                ", " + room.getName() +
                ", " + index +
                ", " + capacity
                ;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
