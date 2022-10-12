package models;

import java.util.List;

/*
Lịch thi theo ngày
 */
public class DateSchedule {
    private String date;//ngày
    List<SubjectSchedule> subjectSchedules;//danh sách các môn thi được sắp xếp trong ngày
    List<String[]> usedList;//danh sách phòng và ca thi đã được sử dụng trong ngày. Mảng gồm 2 phần tử :id classroom,shift
}
