package models;

/*
Môn thi đã được sắp lịch
 */
public class SubjectSchedule {
    private Subject subject;//môn thi
    private ExamRoom room;//phòng thi
    private int shift;// ca thi 1,2,3,4

    public SubjectSchedule(Subject subject, ExamRoom room, int shift) {
        this.subject = subject;
        this.room = room;
        this.shift = shift;
    }

    @Override
    public String toString() {
        return "SubjectSchedule{" +
                "subject=" + subject +
                ", room=" + room +
                ", shift=" + shift +
                '}';
    }
}
