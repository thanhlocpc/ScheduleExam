package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
Lịch thi theo ngày
 */
public class DateSchedule {
    private String date;//ngày
    List<SubjectSchedule> subjectSchedules;//danh sách các môn thi được sắp xếp trong ngày
    List<String[]> usedList;//danh sách phòng và ca thi đã được sử dụng trong ngày. Mảng gồm 2 phần tử :shift,id classroomLT
    List<Subject> subjectList;//danh sách môn thi chưa được sắp xếp

    public DateSchedule(String date, List<Subject> subjectList) {
        this.date = date;
        this.subjectList = subjectList;
        usedList = new ArrayList<>();
        subjectSchedules = new ArrayList<>();
    }

    //    public List<ExamRoom> getListExamRoom() {
//        List<ExamRoom> erList = new ArrayList<>();
//
//    }
    public List<ClassRoom> getClassRoomLTList() throws IOException {
        List<ClassRoom> classRooms = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("data/classroomLT"));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");
            classRooms.add(new ClassRoom(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4])));
            line = reader.readLine();
        }
        return classRooms;
    }

    public List<ClassRoom> getClassRoomTHList() throws IOException {
        List<ClassRoom> classRooms = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("data/classroomTH"));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");
            classRooms.add(new ClassRoom(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4])));
            line = reader.readLine();
        }
        return classRooms;
    }

    public List<RegistrationClass> getRegistrationClass() throws IOException {
        List<RegistrationClass> classRooms = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("data/RegistrationClass"));
        String line = reader.readLine();
        Subject subject = null;
        while (line != null) {
            String[] tokens = line.split(",");
            for (Subject s : subjectList) {
                if (s.getId() == tokens[0].substring(tokens[0].lastIndexOf("-") + 1)) {
                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms());
                    break;
                }
            }
            classRooms.add(new RegistrationClass(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), subject));
            line = reader.readLine();
        }
        return classRooms;
    }

    public List<RegistrationClass> getGroupClassOfSubject(List<RegistrationClass> rcs, Subject subject) {
        List<RegistrationClass> result = new ArrayList<>();
        for (RegistrationClass rc : rcs) {
            if (rc.equals(subject)) {
                result.add(rc);
            }
        }
        return result;
    }

    public List<Subject> generateSubjectSchedule() throws IOException {
        Random rd = new Random();
        System.out.println("generate schedule for date:" + date);
        List<ClassRoom> remainClassRoomTHList = this.getClassRoomTHList();
        System.out.println("Số lượng phòng thi thực hành:" + remainClassRoomTHList.size());
        List<ClassRoom> remainClassRoomLTList = this.getClassRoomLTList();
        System.out.println("Số lương phòng thi lý thuyết:" + remainClassRoomLTList.size());
        List<RegistrationClass> registrationClasses = getRegistrationClass();
        System.out.println("số lượng registrationClass:" + registrationClasses.size());
        int numSubject = subjectList.size() < 4 ? subjectList.size() : rd.nextInt((int) (subjectList.size() * 0.5)) + 1;
        List<Subject> preparedSubject = new ArrayList<>();
        List<Subject> remainSubject = new ArrayList<>(subjectList);
        System.out.println("Số lượng môn học còn lại:" + remainSubject.size());
        for (int i = 0; i < numSubject; i++) {
            int randomIndex = rd.nextInt(remainSubject.size());
            preparedSubject.add(remainSubject.get(randomIndex));
            remainSubject.remove(randomIndex);
        }
        for (Subject s : preparedSubject) {
            System.out.println("generate schedule for subject:" + s.getName());
            List<RegistrationClass> groupSubject = getGroupClassOfSubject(registrationClasses, s);
            for (RegistrationClass rs : groupSubject) {
                int numberOfStudent = rs.getEstimatedClassSizeReal();
                int examRoomIndex = 0;
                while (numberOfStudent > 0) {
                    ExamRoom ex = new ExamRoom(rs);
                    shiftLoop:
                    for (int i = 0; i < 4; i++) {
                        ClassRoom cl = null;
                        int index = -1;
                        if (s.getExamForms() == 1) {
                            index = rd.nextInt(remainClassRoomTHList.size());
                            cl = remainClassRoomTHList.get(index);
//                    remainClassRoomTHList.remove(index);
                        } else if (s.getExamForms() == 0) {
                            index = rd.nextInt(remainClassRoomLTList.size());
                            cl = remainClassRoomLTList.get(index);
//                    remainClassRoomLTList.remove(index);
                        }

                        usedListLoop:
                        for (int j = 0; j < usedList.size(); j++) {
                            if (Integer.parseInt(usedList.get(j)[0]) == i) {
                                if (usedList.get(j)[1].compareTo(cl.getId()) == 0) {
                                    break usedListLoop;
                                }


                            }
                            ex.setIndex(1);
                            ex.setRoom(cl);
                            ex.setIndex(examRoomIndex++);
                            if (numberOfStudent > cl.getCapacityExam())
                                numberOfStudent -= cl.getCapacityExam();
                            else numberOfStudent = 0;
                            subjectSchedules.add(new SubjectSchedule(s, ex, i));
                            usedList.add(new String[]{i + "", cl.getId()});

                            break shiftLoop;
                        }
                    }
                }
            }
        }
        return remainSubject;
    }

    public String toString() {
        String s = date + "\n";
        for (SubjectSchedule ss : subjectSchedules) {
            s += (ss.toString() + "\n");
        }
        return s;
    }
}