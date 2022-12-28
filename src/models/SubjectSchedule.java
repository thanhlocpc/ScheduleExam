package models;

import java.util.Objects;

/*
Môn thi đã được sắp lịch
 */
public class SubjectSchedule implements Comparable<SubjectSchedule> {
    private Subject subject;//môn thi

    public ExamRoom getRoom() {
        return room;
    }

    public void setRoom(ExamRoom room) {
        this.room = room;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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
        return room +
                ", ca:" + (shift + 1)
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectSchedule that = (SubjectSchedule) o;
        return Objects.equals(subject, that.subject) && Objects.equals(room, that.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, room, shift);
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public int compareTo(SubjectSchedule o2) {
        if (this.getSubject().getId().equals(o2.getSubject().getId())) {
            return this.shift - o2.shift;
        } else {
            return String.CASE_INSENSITIVE_ORDER.compare(this.getSubject().getId(), o2.getSubject().getId());
        }
    }
}
