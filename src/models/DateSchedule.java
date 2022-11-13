package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
Lịch thi theo ngày
 */
public class DateSchedule {
    private String date;//ngày

    public String getDate() {
        return date;
    }

    List<SubjectSchedule> subjectSchedules;//danh sách các môn thi được sắp xếp trong ngày
    List<String[]> usedList;//danh sách phòng và ca thi đã được sử dụng trong ngày. Mảng gồm 2 phần tử :shift,id classroomLT
    List<String[]> usedListTH;
    List<String[]> usedListLT;
    List<Subject> subjectList;//danh sách môn thi chưa được sắp xếp
    double fitness;
    Map<Subject, Set<String>> subjectMap;
    List<Subject> preparedSubject;
    List<Subject> remainSubject;
    List<ClassRoom> remainClassRoomTHList;
    List<ClassRoom> remainClassRoomLTList;
    List<RegistrationClass> registrationClasses;

    public DateSchedule(String date, List<Subject> subjectList, Map<Subject, Set<String>> subjectMap) {
        this.date = date;
        this.subjectList = subjectList;
        usedList = new ArrayList<>();
        usedListTH = new ArrayList<>();
        usedListLT = new ArrayList<>();
        subjectSchedules = new ArrayList<>();
        preparedSubject = new ArrayList<>();
        remainSubject = new ArrayList<>(this.subjectList);
        this.subjectMap = subjectMap;
    }

    //    public List<ExamRoom> getListExamRoom() {
//        List<ExamRoom> erList = new ArrayList<>();
//
//    }
    public boolean isContainSubject(Subject subject) {
        for (Map.Entry<Subject, Set<String>> entry : subjectMap.entrySet()) {
            if (subject.getId().equals(entry.getKey().getId()))
                return true;
        }
        return false;
    }

    public void deleteSubject(Subject s) {
        List<SubjectSchedule> subjectSchedulesTmp = new ArrayList<>();
        subjectSchedulesTmp.addAll(subjectSchedules);
        SubjectSchedule ss = null;
//        System.out.println("prepare List before delete:" + preparedSubject.size());
        int i = 0;
        while (i < subjectSchedulesTmp.size()) {
            SubjectSchedule subjSche = subjectSchedulesTmp.get(i);
//            System.out.println(subjSche.getSubject().getId() + "=" + s.getId());
            if (subjSche.getSubject().getId().equals(s.getId())) {
//                ss = subjSche;

                for (int j = 0; j < usedListTH.size(); j++) {
                    if (usedListTH.get(j)[1].equals(subjSche.getRoom().getRoom().getId()) && Integer.parseInt(usedListTH.get(j)[0]) == subjSche.shift) {
                        System.out.println("remove "+usedListTH.get(j)[1]+"---"+usedListTH.get(j)[0]);
                        usedListTH.remove(j);
                        break;
                    }
                }
                for (int j = 0; j < usedListLT.size(); j++) {
                    if (usedListLT.get(j)[1].equals(subjSche.getRoom().getRoom().getId()) && Integer.parseInt(usedListLT.get(j)[0]) == subjSche.shift) {
                        System.out.println("remove "+usedListLT.get(j)[1]+"---"+usedListLT.get(j)[0]);
                        usedListLT.remove(j);

                        break;
                    }
                }
                subjectSchedulesTmp.remove(i--);

//                System.out.println("remove "+subjSche.getSubject().getName()+" "+subjSche.toString());
            }
            i++;
        }
        subjectList.add(s);
        System.out.println("is contain sbject:"+subjectList.contains(s));
        subjectMap.put(s, new HashSet<>());
        subjectSchedules = new ArrayList<>(subjectSchedulesTmp);

    }

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

        while (line != null) {
            String[] tokens = line.split(",");
            Subject subject = null;
            for (Subject s : subjectList) {
                if (s.getId().equals(tokens[0].substring(0, tokens[0].lastIndexOf("-")))) {
                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms());
                    break;
                }
            }
            if (subject != null)
                classRooms.add(new RegistrationClass(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), subject));
            line = reader.readLine();
        }
        return classRooms;
    }

    public List<RegistrationClass> getGroupClassOfSubject(List<RegistrationClass> rcs, Subject subject) {
        List<RegistrationClass> result = new ArrayList<>();
//        System.out.println("regist class size :"+rcs.size());
        for (RegistrationClass rc : rcs) {
//            System.out.print(rc.toString()+"--");
            if (rc.getSubject().getId().equals(subject.getId())) {
                result.add(rc);
            }
        }
//        System.out.println();
        return result;
    }

    public void fitness() {
        double result = 0;
        //tính lỗi với chỗ dư của examclass đã được sắp xếp
        for (int i = 0; i < subjectSchedules.size(); i++) {
            result += subjectSchedules.get(i).getRoom().remainSlot();
        }


        //tính số lượng ca thi của các lớp thi của 1 môn
        Set<String> subjectListInDate = new HashSet<>();
        for (int i = 0; i < subjectSchedules.size(); i++) {
            subjectListInDate.add(subjectSchedules.get(i).getSubject().getId());
        }
        for (String s : subjectListInDate) {
            Map<Integer, Integer> examClassInShifts = new HashMap<>();
            examClassInShifts.put(0, 0);
            examClassInShifts.put(1, 0);
            examClassInShifts.put(2, 0);
            examClassInShifts.put(3, 0);
            for (int i = 0; i < subjectSchedules.size(); i++) {
                SubjectSchedule ss = subjectSchedules.get(i);
                if (ss.getSubject().getId().equals(s))
                    examClassInShifts.put(ss.shift, examClassInShifts.get(ss.shift) + 1);
            }
            int numShift = 0;
            for (Map.Entry<Integer, Integer> entry : examClassInShifts.entrySet()) {
                if (entry.getValue() > 0)
                    numShift++;
            }
            result += (numShift - 1) * 5;
        }
        //tính số tiết để trống
        int[] shifts = {0, 0, 0, 0};
        for (int i = 0; i < subjectSchedules.size(); i++) {
            int shift = subjectSchedules.get(i).shift;
            switch (shift) {
                case 0:
                    shifts[0] = shifts[0] + 1;
                    break;
                case 1:
                    shifts[1] = shifts[1] + 1;
                    break;
                case 2:
                    shifts[2] = shifts[2] + 1;
                    break;
                case 3:
                    shifts[3] = shifts[3] + 1;
                    break;
            }
        }
        for (int s : shifts) {
            if (s == 0) result += 30;
        }
        this.fitness = result;
    }

    public void addNewSubject(Subject s) throws IOException {
        preparedSubject.add(s);
        //System.out.println(preparedSubject.toString());
        for(String[] sss:usedListTH){
            //System.out.print(sss[0]+"-"+sss[1]+",");
        }
        //System.out.println();
        for(String[] sss:usedListLT){
            //System.out.print(sss[0]+"-"+sss[1]+",");
        }
        //System.out.println();

            remainSubject=generateSchedule();
    }


    public List<Subject> generateSchedule() throws IOException {
        Random rd = new Random();
        List<ClassRoom> totalClassRoomTHList = this.getClassRoomTHList();
        List<ClassRoom> totalClassRoomLTList = this.getClassRoomLTList();
        registrationClasses = getRegistrationClass();
        subjectLoop:
        for (int si = 0; si < preparedSubject.size(); si++) {
            Subject s = preparedSubject.get(si);
//            System.out.println("generate schedule for subject:" + s.toString());
            List<RegistrationClass> groupSubject = getGroupClassOfSubject(registrationClasses, s);
//            System.out.println("    number of group subject for " + s.getName() + " :" + groupSubject.size());
            for (RegistrationClass rs : groupSubject) {
//                System.out.println("    group subject:" + rs.toString());
                int numberOfStudent = rs.getEstimatedClassSizeReal();
                int examRoomIndex = 0;
                while (numberOfStudent > 0) {
//                    System.out.println("        numberOfStudent:" + numberOfStudent);
                    ExamRoom ex = new ExamRoom(rs);
                    shiftLoop:
                    for (int i = 0; i < 4; i++) {
                        ClassRoom cl = null;
                        int index = -1;
                        if (s.getExamForms() == 1) {
//                            if (usedList.size() > totalClassRoomTHList.size() * 4 - 1) {
                            if (usedListTH.size() > totalClassRoomTHList.size() * 4 - 1) {
//                                remainSubject.addAll(preparedSubject.subList(si, preparedSubject.size()));
                                remainSubject.add(preparedSubject.get(si));
                                continue subjectLoop;
                            } else {
//                            System.out.println("        là phòng thực hành:");
                                index = rd.nextInt(remainClassRoomTHList.size());
                                cl = remainClassRoomTHList.get(index);
                                if (usedListTH.size() == 0) {
                                    ex.setRoom(cl);
                                    ex.setIndex(examRoomIndex++);
                                    if (numberOfStudent > cl.getCapacityExam()) {
                                        ex.setCapacity(cl.getCapacityExam());
                                        numberOfStudent -= cl.getCapacityExam();
                                    } else {
                                        ex.setCapacity(numberOfStudent);
                                        numberOfStudent = 0;
                                    }
                                    subjectSchedules.add(new SubjectSchedule(s, ex, i));
                                    usedListTH.add(new String[]{i + "", cl.getId()});
                                    Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                    set.add(this.date);
                                    subjectMap.put(s, set);
                                    break shiftLoop;
                                } else {
                                    usedListLoop:
                                    for (int j = 0; j < usedListTH.size(); j++) {
//                                System.out.println("            usedList " + j + " :" + usedList.get(j)[0] + "-" + usedList.get(j)[1]);
                                        if (Integer.parseInt(usedListTH.get(j)[0]) == i) {
                                            if (usedListTH.get(j)[1].compareTo(cl.getId()) == 0) {
//                                        System.out.println("            ca thi " + i + " và lớp " + cl.getId() + " đã dùng");
                                                continue shiftLoop;
                                            }
                                        }

                                    }
                                    ex.setRoom(cl);
                                    ex.setIndex(examRoomIndex++);
                                    if (numberOfStudent > cl.getCapacityExam()) {
                                        ex.setCapacity(cl.getCapacityExam());
                                        numberOfStudent -= cl.getCapacityExam();
                                    } else {
                                        ex.setCapacity(numberOfStudent);
                                        numberOfStudent = 0;
                                    }
                                    subjectSchedules.add(new SubjectSchedule(s, ex, i));
                                    usedListTH.add(new String[]{i + "", cl.getId()});
                                    Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                    set.add(this.date);
                                    subjectMap.put(s, set);
                                    break shiftLoop;
                                }
                            }
                        }
                        else if (s.getExamForms() == 0 || s.getExamForms() == 2) {
//                            if (usedList.size() > totalClassRoomLTList.size() * 4 - 1) {
                            if (usedListLT.size() > totalClassRoomLTList.size() * 4 - 1) {
//                                remainSubject.addAll(preparedSubject.subList(si, preparedSubject.size()));
                                remainSubject.add(preparedSubject.get(si));
                                continue subjectLoop;
                            } else {
//                            System.out.println("        là phòng lý thuyết:");
                                index = rd.nextInt(remainClassRoomLTList.size());
                                cl = remainClassRoomLTList.get(index);
                                if (usedListLT.size() == 0) {
                                    ex.setRoom(cl);
                                    ex.setIndex(examRoomIndex++);
                                    if (numberOfStudent > cl.getCapacityExam()) {
                                        ex.setCapacity(cl.getCapacityExam());
                                        numberOfStudent -= cl.getCapacityExam();
                                    } else {
                                        ex.setCapacity(numberOfStudent);
                                        numberOfStudent = 0;
                                    }
                                    subjectSchedules.add(new SubjectSchedule(s, ex, i));
                                    usedListLT.add(new String[]{i + "", cl.getId()});
                                    Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                    set.add(this.date);
                                    subjectMap.put(s, set);
                                    break shiftLoop;
                                } else {
                                    usedListLoop:
                                    for (int j = 0; j < usedListLT.size(); j++) {
//                                System.out.println("            usedList " + j + " :" + usedList.get(j)[0] + "-" + usedList.get(j)[1]);
                                        if (Integer.parseInt(usedListLT.get(j)[0]) == i) {
                                            if (usedListLT.get(j)[1].compareTo(cl.getId()) == 0) {
//                                        System.out.println("            ca thi " + i + " và lớp " + cl.getId() + " đã dùng");
                                                continue shiftLoop;
                                            }
                                        }

                                    }
                                    ex.setRoom(cl);
                                    ex.setIndex(examRoomIndex++);
                                    if (numberOfStudent > cl.getCapacityExam()) {
                                        ex.setCapacity(cl.getCapacityExam());
                                        numberOfStudent -= cl.getCapacityExam();
                                    } else {
                                        ex.setCapacity(numberOfStudent);
                                        numberOfStudent = 0;
                                    }
                                    subjectSchedules.add(new SubjectSchedule(s, ex, i));
                                    usedListLT.add(new String[]{i + "", cl.getId()});
                                    Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                    set.add(this.date);
                                    subjectMap.put(s, set);
                                    break shiftLoop;
                                }
                            }
                        }
                    }
                }
            }
        }
        preparedSubject.clear();
        return remainSubject;
    }

    public List<Subject> generateInitialSubjectSchedule() throws IOException {
        Random rd = new Random();

//        System.out.println("generate schedule for date:" + date);
        remainClassRoomTHList = this.getClassRoomTHList();
//        System.out.println("Số lượng phòng thi thực hành:" + remainClassRoomTHList.size());
        remainClassRoomLTList = this.getClassRoomLTList();
//        System.out.println("Số lương phòng thi lý thuyết:" + remainClassRoomLTList.size());

//        System.out.println("số lượng registrationClass:" + registrationClasses.size());
        int numSubject = subjectList.size() < 4 ? subjectList.size() : rd.nextInt((int) (subjectList.size() * 0.4)) + 2;

//        System.out.println("Số lượng môn học còn lại:" + remainSubject.size());
        for (int i = 0; i < numSubject; i++) {
            int randomIndex = rd.nextInt(remainSubject.size());
            preparedSubject.add(remainSubject.get(randomIndex));
            remainSubject.remove(randomIndex);
        }
//        System.out.println("Số lượng môn học chuẩn bị sắp xếp:" + preparedSubject.size());
//        System.out.println("Remain subject 1:" + remainSubject.size());
        generateSchedule();
//        System.out.println("Remain subject 2:" + remainSubject.size());
        return remainSubject;

    }

    public String toString() {
        String s = date + "\n";
        Collections.sort(subjectSchedules, new Comparator<SubjectSchedule>() {
            @Override
            public int compare(SubjectSchedule o1, SubjectSchedule o2) {
                return o1.shift - o2.shift;
            }
        });
        for (SubjectSchedule ss : subjectSchedules) {
            s += (ss.toString() + "\n");
        }
        return s;
    }
}
