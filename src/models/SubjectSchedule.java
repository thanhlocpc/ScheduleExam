package models;

/*
Môn thi đã được sắp lịch
 */
public class SubjectSchedule {
    private Subject subject;//môn thi

    public ExamRoom getRoom() {
        return room;
    }

    public void setRoom(ExamRoom room) {
        this.room = room;
    }

    private ExamRoom room;//phòng thi
     int shift;// ca thi 1,2,3,4

    public SubjectSchedule(Subject subject, ExamRoom room, int shift) {
        this.subject = subject;
        this.room = room;
        this.shift = shift;
    }

    @Override
    public String toString() {
        return   room +
                ", " + (shift+1)
                ;
    }

    public Subject getSubject() {
        return subject;
    }
}
