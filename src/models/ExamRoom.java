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
}
