package com.schedule.initialization.models;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
Lịch thi theo ngày
 */
public class DateSchedule implements Comparable<DateSchedule>,Cloneable, Serializable {
    private String date;//ngày
    List<SubjectSchedule> subjectSchedules;//danh sách các môn thi được sắp xếp trong ngày

    public String getDate() {
        return date;
    }


    List<String[]> usedList;//danh sách phòng và ca thi đã được sử dụng trong ngày. Mảng gồm 2 phần tử :shift,id classroomLT
    List<String[]> usedListTH;
    List<String[]> usedListLT;

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    List<Subject> subjectList;//danh sách môn thi chưa được sắp xếp
    double fitness;
    Map<Subject, Set<String>> subjectMap;

    public List<SubjectSchedule> getSubjectSchedules() {
        return subjectSchedules;
    }

    public void setSubjectSchedules(List<SubjectSchedule> subjectSchedules) {
        this.subjectSchedules = subjectSchedules;
    }

    List<Subject> preparedSubject;
    List<Subject> remainSubject;
    List<ClassRoom> remainClassRoomTHList;
    List<ClassRoom> remainClassRoomLTList;
    List<RegistrationClass> registrationClasses;
    Map<String, SubjectSchedule> thClassMap;//s

    Map<String, SubjectSchedule> ltClassMap;

    public DateSchedule clone() throws CloneNotSupportedException {
        DateSchedule ds=(DateSchedule) super.clone();
        ds.setDate(this.date);
        //
        List<Subject> subjectListClone=new ArrayList<>();
        for(Subject s:subjectList){
            subjectListClone.add(s.clone());
        }
        ds.setSubjectList(subjectListClone);
        //
        Map<Subject,Set<String>> subjectMapClone=new HashMap<>();
        for (Map.Entry<Subject, Set<String>> entry : subjectMap.entrySet()) {
            subjectMapClone.put(entry.getKey().clone(),new HashSet<>(entry.getValue()));
        }
        ds.setSubjectMap(subjectMapClone);
        //
        Map<String, SubjectSchedule> ltClassMapClone=new HashMap<>();

        for (Map.Entry<String, SubjectSchedule> entry : ltClassMap.entrySet()) {
            if(entry.getValue()!=null)
            ltClassMapClone.put(entry.getKey(),entry.getValue().clone());
            else ltClassMapClone.put(entry.getKey(),null);
        }
        ds.setLtClassMap(ltClassMapClone);

        //
        Map<String, SubjectSchedule> thClassMapClone=new HashMap<>();
        for (Map.Entry<String, SubjectSchedule> entry : thClassMap.entrySet()) {
            if(entry.getValue()!=null)
            thClassMapClone.put(entry.getKey(),entry.getValue().clone());
            else thClassMapClone.put(entry.getKey(),null);
        }
        ds.setThClassMap(thClassMapClone);
        //
        List<SubjectSchedule> subjectSchedulesClone=new ArrayList<>();
        for(SubjectSchedule s:subjectSchedules){
            subjectSchedulesClone.add(s.clone());
        }
        ds.setSubjectSchedules(subjectSchedulesClone);
        //
        List<Subject> preparedSubjectListClone=new ArrayList<>();
        for(Subject s:preparedSubject){
            preparedSubjectListClone.add(s.clone());
        }
        ds.setPreparedSubject(preparedSubjectListClone);
        ds.setUsedListLT(new ArrayList<>(this.usedListLT));
        ds.setUsedListTH(new ArrayList<>(this.usedListTH));
        //
        List<Subject> remainSubjectListClone=new ArrayList<>();
        for(Subject s:remainSubject){
            remainSubjectListClone.add(s.clone());
        }
        ds.setRemainSubject(remainSubjectListClone);
        //
        List<ClassRoom> remainClassRoomLTListClone=new ArrayList<>();
        for(ClassRoom cr:remainClassRoomLTList){
            remainClassRoomLTListClone.add(cr.clone());
        }
        ds.setRemainClassRoomLTList(remainClassRoomLTListClone);
        //
        List<ClassRoom> remainClassRoomTHListClone=new ArrayList<>();
        for(ClassRoom cr:remainClassRoomTHList){
            remainClassRoomTHListClone.add(cr.clone());
        }
        ds.setRemainClassRoomTHList(remainClassRoomTHListClone);
        ds.setFitness(this.fitness);
        return ds;
    }

    public DateSchedule() {

    }

    public DateSchedule(String date, List<Subject> subjectList, Map<Subject, Set<String>> subjectMap) throws IOException {
        this.date = date;
        this.subjectList = subjectList;
        usedList = new ArrayList<>();
        usedListTH = new ArrayList<>();
        usedListLT = new ArrayList<>();
        subjectSchedules = new ArrayList<>();
        preparedSubject = new ArrayList<>();
        remainSubject = new ArrayList<>(this.subjectList);
        this.subjectMap = subjectMap;
        thClassMap = new HashMap<>();
        ltClassMap = new HashMap<>();
        initiateClassMap();
    }
    public boolean isContainSubject(Subject subject) {
        for (Map.Entry<Subject, Set<String>> entry : subjectMap.entrySet()) {
            if (subject.getId().equals(entry.getKey().getId()))
                return true;
        }
        return false;
    }

    public DateSchedule swap(int[] swapIndex) throws IOException {
        List<Map.Entry<String, SubjectSchedule>> list = new ArrayList<>(ltClassMap.entrySet());
        SubjectSchedule ss1 = list.get(swapIndex[0]).getValue();
        SubjectSchedule ss2 = list.get(swapIndex[1]).getValue();
//        System.out.println("ss1 is null:" + (ss1 == null) + " ss2 is null:" + (ss2 == null));
        if (ss1 != null && ss2 != null) {
//            System.out.println("not null both");
            ExamRoom er1 = ss1.getRoom();
            int shift1 = ss1.shift;
            Subject subject1 = ss1.getSubject();
            ss1.setRoom(ss2.getRoom());
            ss1.setShift(ss2.getShift());
            ss1.setSubject(ss2.getSubject());
            ss2.setRoom(er1);
            ss2.setShift(shift1);
            ss2.setSubject(subject1);
            for (int i = 0; i < usedListLT.size(); i++) {
                if (usedListLT.get(i)[1].equals(list.get(swapIndex[0]).getValue().getRoom().getRoom().getId()) && Integer.parseInt(usedListLT.get(i)[0]) == list.get(swapIndex[0]).getValue().shift) {
                    usedListLT.set(i, new String[]{ss1.shift + "", ss1.getRoom().getRoom().getId()});
                    break;
                }
                if (usedListLT.get(i)[1].equals(list.get(swapIndex[1]).getValue().getRoom().getRoom().getId()) && Integer.parseInt(usedListLT.get(i)[0]) == list.get(swapIndex[1]).getValue().shift) {
                    usedListLT.set(i, new String[]{ss2.shift + "", ss2.getRoom().getRoom().getId()});
                    break;
                }
            }
            for (int i = 0; i < usedListTH.size(); i++) {
                if (usedListTH.get(i)[1].equals(list.get(swapIndex[0]).getValue().getRoom().getRoom().getId()) && Integer.parseInt(usedListTH.get(i)[0]) == list.get(swapIndex[0]).getValue().shift) {
                    usedListTH.set(i, new String[]{ss1.shift + "", ss1.getRoom().getRoom().getId()});
                    break;
                }
                if (usedListTH.get(i)[1].equals(list.get(swapIndex[1]).getValue().getRoom().getRoom().getId()) && Integer.parseInt(usedListTH.get(i)[0]) == list.get(swapIndex[1]).getValue().shift) {
                    usedListTH.set(i, new String[]{ss2.shift + "", ss2.getRoom().getRoom().getId()});
                    break;
                }
            }
            for (int i = 0; i > subjectSchedules.size(); i++) {
                SubjectSchedule ss = subjectSchedules.get(i);
                if (ss.equals(list.get(swapIndex[0]).getValue()) && ss.shift == list.get(swapIndex[0]).getValue().shift) {
                    subjectSchedules.set(i, ss1);
                }
                if (ss.equals(list.get(swapIndex[1]).getValue()) && ss.shift == list.get(swapIndex[1]).getValue().shift) {
                    subjectSchedules.set(i, ss2);
                }
            }
            if (ss2.getSubject().getExamForms() == 0)
                ltClassMap.put(list.get(swapIndex[0]).getKey(), ss2);
            else
                thClassMap.put(list.get(swapIndex[0]).getKey(), ss2);

            if (ss2.getSubject().getExamForms() == 1)
                ltClassMap.put(list.get(swapIndex[1]).getKey(), ss1);
            else
                thClassMap.put(list.get(swapIndex[1]).getKey(), ss1);
        } else if (ss1 == null && ss2 != null) {
            String s = list.get(swapIndex[0]).getKey();
            int shift = Integer.parseInt(s.substring(s.lastIndexOf("-") + 1));
            ExamRoom er = ss2.getRoom();
            er.setRoom(getClassRoomById(s.substring(0, s.lastIndexOf("-"))));
            ss1 = new SubjectSchedule(ss2.getSubject(), er, shift);
            for (int i = 0; i > subjectSchedules.size(); i++) {
                SubjectSchedule ss = subjectSchedules.get(i);
                if (ss.equals(ss2) && ss.shift == ss2.shift) {
                    subjectSchedules.set(i, ss1);
                }

            }
            for (int i = 0; i < usedListLT.size(); i++) {
                if (usedListLT.get(i)[1].equals(ss2.getRoom().getRoom().getId()) && Integer.parseInt(usedListLT.get(i)[0]) == ss2.shift) {
                    usedListLT.set(i, new String[]{ss1.shift + "", ss1.getRoom().getRoom().getId()});
                    break;
                }
            }

            for (int i = 0; i < usedListTH.size(); i++) {
                if (usedListTH.get(i)[1].equals(ss2.getRoom().getRoom().getId()) && Integer.parseInt(usedListTH.get(i)[0]) == ss2.shift) {
                    usedListTH.set(i, new String[]{ss1.shift + "", ss1.getRoom().getRoom().getId()});
                    break;
                }
            }
            if (ss2.getSubject().getExamForms() == 0)
                ltClassMap.put(list.get(swapIndex[0]).getKey(), ss2);
            else
                thClassMap.put(list.get(swapIndex[0]).getKey(), ss2);

            if (ss2.getSubject().getExamForms() == 0)
                ltClassMap.put(list.get(swapIndex[1]).getKey(), null);
            else
                thClassMap.put(list.get(swapIndex[1]).getKey(), null);

        }
//        System.out.println(subjectSchedules);
        fitness();
        return this;
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
//                        System.out.println("remove " + usedListTH.get(j)[1] + "---" + usedListTH.get(j)[0]);
                        thClassMap.put(usedListTH.get(j)[1] + "-" + usedListTH.get(j)[0], null);
                        usedListTH.remove(j);
                        break;
                    }
                }
                for (int j = 0; j < usedListLT.size(); j++) {
                    if (usedListLT.get(j)[1].equals(subjSche.getRoom().getRoom().getId()) && Integer.parseInt(usedListLT.get(j)[0]) == subjSche.shift) {
//                        System.out.println("remove " + usedListLT.get(j)[1] + "---" + usedListLT.get(j)[0]);
                        ltClassMap.put(usedListLT.get(j)[1] + "-" + usedListLT.get(j)[0], null);
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
//        System.out.println("is contain sbject:" + subjectList.contains(s));
        subjectMap.put(s, new HashSet<>());
        subjectSchedules = new ArrayList<>(subjectSchedulesTmp);

    }

    public void countNumberOfNun() {
        int count = 0;
        List<Map.Entry<String, SubjectSchedule>> list = new ArrayList<>(ltClassMap.entrySet());
        for (Map.Entry<String, SubjectSchedule> s : list) {
            if (s.getValue() == null) {
                count++;
            }
        }
//        System.out.println("subjectSchedules list:" + subjectSchedules.size());
//        System.out.println("number of num:" + count + "/" + list.size());
    }

    public void initiateClassMap() throws IOException {
        List<ClassRoom> totalClassRoomTHList = this.getClassRoomTHList();
        List<ClassRoom> totalClassRoomLTList = this.getClassRoomLTList();
        for (int i = 0; i < 4; i++) {
            for (ClassRoom c : totalClassRoomLTList) {
                this.ltClassMap.put(c.getName() + "-" + i, null);
            }
            for (ClassRoom c : totalClassRoomTHList) {
                this.thClassMap.put(c.getName() + "-" + i, null);
            }
        }
    }

    public ClassRoom getClassRoomById(String id) throws IOException {
        ClassRoom classRoom = null;
        BufferedReader reader = new BufferedReader(new FileReader("data/classroomLT"));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");
            if (tokens[0].equals(id)) {
                classRoom = new ClassRoom(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));//add builing id
                break;
            }
            line = reader.readLine();
        }
        return classRoom;
    }

    public List<ClassRoom> getClassRoomLTList() throws IOException {
        List<ClassRoom> classRooms = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("data/classroomLT"));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");
            classRooms.add(new ClassRoom(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5])));//add builing id
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
            classRooms.add(new ClassRoom(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5])));//add builing id
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
                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms(), s.getExamTime(), s.getLessonTime());
                    break;
                }
            }
            if (subject != null)
                classRooms.add(new RegistrationClass(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), subject, new Grade(tokens[4], tokens[6]),Integer.parseInt(tokens[5])));
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
        int[] shifts = {0, 1, 2, 3};
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
        remainSubject = generateSchedule();
    }


    public Map<String, SubjectSchedule> getThClassMap() {
        return thClassMap;
    }

    public void setThClassMap(Map<String, SubjectSchedule> thClassMap) {
        this.thClassMap = thClassMap;
    }

    public Map<String, SubjectSchedule> getLtClassMap() {
        return ltClassMap;
    }

    public void setLtClassMap(Map<String, SubjectSchedule> ltClassMap) {
        this.ltClassMap = ltClassMap;
    }

    public List<Subject> generateSchedule() throws IOException {
        remainClassRoomTHList = this.getClassRoomTHList();
//        System.out.println("Số lượng phòng thi thực hành:" + remainClassRoomTHList.size());
        remainClassRoomLTList = this.getClassRoomLTList();
//        System.out.println("Số lương phòng thi lý thuyết:" + remainClassRoomLTList.size());
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
                        // nếu ca trước xếp gần hết phòng thì chuyển sang ca sau thi
                        final int shift = i;
                        int countRoomThUsedOfShift = usedListTH.stream().filter(e -> e[0].equals(shift + "")).collect(Collectors.toList()).size();
                        int countRoomLtUsedOfShift = usedListLT.stream().filter(e -> e[0].equals(shift + "")).collect(Collectors.toList()).size();
                        if (s.getExamForms() == 1 || s.getExamForms() == 2) { //TH

                            if (totalClassRoomTHList.size() - countRoomThUsedOfShift < 2) {
                                if (i == 3) {
                                    remainSubject.add(preparedSubject.get(si));
                                    break subjectLoop;
                                } else {
                                    continue shiftLoop;
                                }
                            }
                        } else {
                            if (totalClassRoomLTList.size() - countRoomLtUsedOfShift < 2) {
                                if (i == 3) {
                                    remainSubject.add(preparedSubject.get(si));
                                    break subjectLoop;
                                } else {
                                    continue shiftLoop;
                                }
                            }
                        }

                        ClassRoom cl = null;
                        int index = -1;
                        // một môn thi, ưu tiên thi trong 1 ngày,
                        // môn thi lt 1 ca thi tối đa 6 phòng, môn thi thực hành 1 ca 4 phòng
                        int totalClassRoomUsedForSubjectShift = 0;
                        if (s.getExamForms() == 1) {
//                            if (usedList.size() > totalClassRoomTHList.size() * 4 - 1) {
                            if (usedListTH.size() > totalClassRoomTHList.size() * 4 - 1) {
//                                remainSubject.addAll(preparedSubject.subList(si, preparedSubject.size()));
                                remainSubject.add(preparedSubject.get(si));
                                continue subjectLoop;
                            } else {
//                            System.out.println("        là phòng thực hành:");
                                loopFindClass:
                                while (true) {
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
                                        try {
                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
                                            subjectSchedules.add(ss);
                                            usedListTH.add(new String[]{i + "", cl.getId()});
                                            Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                            set.add(this.date);
                                            subjectMap.put(s, set);
                                            thClassMap.put(cl.getName() + "-" + i, ss);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        totalClassRoomUsedForSubjectShift++;
                                        if (numberOfStudent == 0) {
                                            break shiftLoop;
                                        }
                                        if (totalClassRoomUsedForSubjectShift == 4) {
                                            break loopFindClass;
                                        }
                                    } else {
                                        usedListLoop:
                                        for (int j = 0; j < usedListTH.size(); j++) {
//                                System.out.println("            usedList " + j + " :" + usedList.get(j)[0] + "-" + usedList.get(j)[1]);
                                            if (Integer.parseInt(usedListTH.get(j)[0]) == i) {
                                                if (usedListTH.size() > totalClassRoomTHList.size() * 4 - 1) {
                                                    remainSubject.add(preparedSubject.get(si));
                                                    continue subjectLoop;
                                                }
                                                if (usedListTH.get(j)[1].compareTo(cl.getId()) == 0) {
//                                        System.out.println("            ca thi " + i + " và lớp " + cl.getId() + " đã dùng");
                                                    countRoomThUsedOfShift = usedListTH.stream().filter(e -> e[0].equals(shift + "")).collect(Collectors.toList()).size();
                                                    if (countRoomThUsedOfShift == totalClassRoomTHList.size()) {
                                                        continue shiftLoop;
                                                    } else {
                                                        continue loopFindClass;
                                                    }
                                                }
                                            }

                                        }
                                        try {
                                            ex.setRoom(cl);
                                            ex.setIndex(examRoomIndex++);
                                            if (numberOfStudent > cl.getCapacityExam()) {
                                                ex.setCapacity(cl.getCapacityExam());
                                                numberOfStudent -= cl.getCapacityExam();
                                            } else {
                                                ex.setCapacity(numberOfStudent);
                                                numberOfStudent = 0;
                                            }
                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
                                            subjectSchedules.add(ss);
                                            usedListTH.add(new String[]{i + "", cl.getId()});
                                            Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                            set.add(this.date);
                                            subjectMap.put(s, set);
                                            thClassMap.put(cl.getName() + "-" + i, ss);

                                            totalClassRoomUsedForSubjectShift++;
                                            if (numberOfStudent == 0) {
                                                break shiftLoop;
                                            }
                                            if (totalClassRoomUsedForSubjectShift == 4) {
                                                break loopFindClass;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

//                                System.out.println("fd");
                            }
                        } else if (s.getExamForms() == 0) {
//                            if (usedList.size() > totalClassRoomLTList.size() * 4 - 1) {
                            if (usedListLT.size() > totalClassRoomLTList.size() * 4 - 1) {
//                                remainSubject.addAll(preparedSubject.subList(si, preparedSubject.size()));
                                remainSubject.add(preparedSubject.get(si));
                                continue subjectLoop;
                            } else {
//                            System.out.println("        là phòng lý thuyết:");
                                loopFindClass:
                                while (true) {
                                    index = rd.nextInt(remainClassRoomLTList.size());
                                    cl = remainClassRoomLTList.get(index);
                                    if (usedListLT.size() == 0) {
                                        try {
                                            ex.setRoom(cl);
                                            ex.setIndex(examRoomIndex++);
                                            if (numberOfStudent > cl.getCapacityExam()) {
                                                ex.setCapacity(cl.getCapacityExam());
                                                numberOfStudent -= cl.getCapacityExam();
                                            } else {
                                                ex.setCapacity(numberOfStudent);
                                                numberOfStudent = 0;
                                            }
                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
                                            subjectSchedules.add(ss);
                                            usedListLT.add(new String[]{i + "", cl.getId()});
                                            Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                            set.add(this.date);
                                            subjectMap.put(s, set);
                                            ltClassMap.put(cl.getName() + "-" + i, ss);
                                            totalClassRoomUsedForSubjectShift++;
                                            if (numberOfStudent == 0) {
                                                break shiftLoop;
                                            }
                                            if (totalClassRoomUsedForSubjectShift == 6) {
                                                break loopFindClass;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        usedListLoop:
                                        for (int j = 0; j < usedListLT.size(); j++) {
//                                System.out.println("            usedList " + j + " :" + usedList.get(j)[0] + "-" + usedList.get(j)[1]);
                                            if (Integer.parseInt(usedListLT.get(j)[0]) == i) {
                                                if (usedListLT.get(j)[1].compareTo(cl.getId()) == 0) {
//                                        System.out.println("            ca thi " + i + " và lớp " + cl.getId() + " đã dùng");
                                                    if (usedListLT.size() > totalClassRoomLTList.size() * 4 - 1) {
                                                        remainSubject.add(preparedSubject.get(si));
                                                        continue subjectLoop;
                                                    }
                                                    countRoomLtUsedOfShift = usedListLT.stream().filter(e -> e[0].equals(shift + "")).collect(Collectors.toList()).size();
                                                    if (countRoomLtUsedOfShift == totalClassRoomLTList.size()) {
                                                        continue shiftLoop;
                                                    } else {
                                                        continue loopFindClass;
                                                    }
                                                }
                                            }
                                        }
                                        try {
                                            ex.setRoom(cl);
                                            ex.setIndex(examRoomIndex++);
                                            if (numberOfStudent > cl.getCapacityExam()) {
                                                ex.setCapacity(cl.getCapacityExam());
                                                numberOfStudent -= cl.getCapacityExam();
                                            } else {
                                                ex.setCapacity(numberOfStudent);
                                                numberOfStudent = 0;
                                            }
                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
                                            subjectSchedules.add(ss);
                                            usedListLT.add(new String[]{i + "", cl.getId()});
                                            Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                            set.add(this.date);
                                            subjectMap.put(s, set);
                                            ltClassMap.put(cl.getName() + "-" + i, ss);
                                            totalClassRoomUsedForSubjectShift++;
                                            if (numberOfStudent == 0) {
                                                break shiftLoop;
                                            }
                                            if (totalClassRoomUsedForSubjectShift == 6) {
                                                break loopFindClass;
                                            }
                                        } catch (Exception e) {

                                        }
                                    }
                                }
                            }
                        }
                        else if (s.getExamForms() == 2) {
//                            if (usedList.size() > totalClassRoomTHList.size() * 4 - 1) {
                            if (usedListTH.size() > totalClassRoomTHList.size() * 4 - 1 || i != 0) {
//                                remainSubject.addAll(preparedSubject.subList(si, preparedSubject.size()));
                                remainSubject.add(preparedSubject.get(si));
                                continue subjectLoop;
                            } else {
//                            System.out.println("        là phòng vấn đáp:");
                                List<ClassRoom> remainClassRoomTHListClone = new ArrayList<>(remainClassRoomTHList);
                                index = rd.nextInt(remainClassRoomTHListClone.size());
                                cl = remainClassRoomTHListClone.get(index);
//                                remainClassRoomTHListClone.remove(index);
                                if (usedListTH.size() == 0) {
                                    ex.setRoom(cl);
                                    ex.setIndex(examRoomIndex++);

                                    ex.setCapacity(numberOfStudent);
                                    numberOfStudent = 0;
                                    try {
                                        SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
                                        subjectSchedules.add(ss);

                                        Set<String> set = subjectMap.get(s);
//                                    System.out.println("subject map size:"+subjectMap.size());
//                                    System.out.println(s + " " + (set == null));
                                        set.add(this.date);
                                        subjectMap.put(s, set);
                                        for (int l = 0; l < 4; l++) {
                                            final int finalL = l;
                                            final ClassRoom finalCl = cl;
                                            if (usedListTH.stream().filter(item -> (Integer.parseInt(item[0]) == finalL && item[1] == finalCl.getId())).count() > 0) {
                                                remainSubject.add(preparedSubject.get(si));
                                                continue subjectLoop;
                                            }
                                        }
                                        usedListTH.add(new String[]{0 + "", cl.getId()});
                                        thClassMap.put(cl.getName() + "-" + 0, ss);
                                        for (int k = 1; k < 4; k++) {
                                            usedListTH.add(new String[]{k + "", cl.getId()});
                                            ex.setCapacity(numberOfStudent);
                                            ss = new SubjectSchedule(s, ex.clone(), k);
                                            subjectSchedules.add(ss);
                                            thClassMap.put(cl.getName() + "-" + k, ss);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    totalClassRoomUsedForSubjectShift++;
                                    if (numberOfStudent == 0) {
                                        break shiftLoop;
                                    }
//                                        if (totalClassRoomUsedForSubjectShift == 4) {
//                                            break loopFindClass;
//                                        }
                                }else {
                                    int remainClassIndex=0;
                                    boolean canAdd=true;
                                    remainThClassRoomForOral:
                                    for (remainClassIndex=0;remainClassIndex<remainClassRoomTHList.size();remainClassIndex++){
                                        cl = remainClassRoomTHListClone.get(remainClassIndex);
                                        int[] shifts = {1, 2, 3, 0};
//                                        innerShiftLoop:
                                        for (int j = 0; j < usedListTH.size(); j++) {
                                        for (int shiftL:shifts){

                                                if(Integer.parseInt(usedListTH.get(j)[0]) == shiftL&&usedListTH.get(j)[1].compareTo(cl.getId()) == 0){
                                                    if(remainClassIndex==remainClassRoomTHList.size()-1){
                                                        canAdd=false;
                                                    }
                                                    continue remainThClassRoomForOral;
                                                }
                                            }
                                        }
                                        if(!canAdd){
                                            remainSubject.add(preparedSubject.get(si));
                                            continue subjectLoop;
                                        }
                                        try {

                                            ex.setRoom(cl);
                                            ex.setIndex(examRoomIndex++);

                                            ex.setCapacity(numberOfStudent);
                                            numberOfStudent = 0;

                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), 0);
                                            subjectSchedules.add(ss);

                                            Set<String> set = subjectMap.get(s);

                                            set.add(this.date);
                                            subjectMap.put(s, set);
                                            usedListTH.add(new String[]{0 + "", cl.getId()});
                                            thClassMap.put(cl.getName() + "-" + 0, ss);
                                            for (int k = 1; k < 4; k++) {
                                                usedListTH.add(new String[]{k + "", cl.getId()});
                                                ex.setCapacity(numberOfStudent);
                                                ss = new SubjectSchedule(s, ex.clone(), k);
                                                subjectSchedules.add(ss);
                                                thClassMap.put(cl.getName() + "-" + k, ss);
                                            }

                                            totalClassRoomUsedForSubjectShift++;
                                            if (numberOfStudent == 0) {
                                                break shiftLoop;
                                            }
//                                            if (totalClassRoomUsedForSubjectShift == 4) {
//                                                break loopFindClass;
//                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
//                                loopFindClass:
//                                while (true) {
//                                    index = rd.nextInt(remainClassRoomTHListClone.size());
//                                    cl = remainClassRoomTHListClone.get(index);
//                                    remainClassRoomTHListClone.remove(index);
//                                    if (usedListTH.size() == 0) {
//                                        ex.setRoom(cl);
//                                        ex.setIndex(examRoomIndex++);
//
//                                        ex.setCapacity(numberOfStudent);
//                                        numberOfStudent = 0;
//                                        try {
//                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
//                                            subjectSchedules.add(ss);
//
//                                            Set<String> set = subjectMap.get(s);
////                                    System.out.println("subject map size:"+subjectMap.size());
////                                    System.out.println(s + " " + (set == null));
//                                            set.add(this.date);
//                                            subjectMap.put(s, set);
//                                            for (int l = 0; l < 4; l++) {
//                                                final int finalL = l;
//                                                final ClassRoom finalCl = cl;
//                                                if (usedListTH.stream().filter(item -> (Integer.parseInt(item[0]) == finalL && item[1] == finalCl.getId())).count() > 0) {
//                                                    remainSubject.add(preparedSubject.get(si));
//                                                    if (remainClassRoomTHListClone.size() > 0)
//                                                        continue loopFindClass;
//                                                    else
//                                                        break loopFindClass;
//                                                }
//                                            }
//                                            for (int k = 1; k < 4; k++) {
//                                                usedListTH.add(new String[]{k + "", cl.getId()});
//                                                ss.setShift(k);
//                                                ss.getRoom().setCapacity(0);
//                                                subjectSchedules.add(ss);
//                                                thClassMap.put(cl.getName() + "-" + k, ss);
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        totalClassRoomUsedForSubjectShift++;
//                                        if (numberOfStudent == 0) {
//                                            break shiftLoop;
//                                        }
//                                        if (totalClassRoomUsedForSubjectShift == 4) {
//                                            break loopFindClass;
//                                        }
//                                    } else {
//                                        usedListLoop:
//                                        for (int j = 0; j < usedListTH.size(); j++) {
//                                            int[] shifts = {1, 2, 3, 0};
////                                System.out.println("            usedList " + j + " :" + usedList.get(j)[0] + "-" + usedList.get(j)[1]);
//                                            if (Arrays.asList(shifts).contains(Integer.parseInt(usedListTH.get(j)[0]))) {
//                                                if (usedListTH.size() > totalClassRoomTHList.size() * 4 - 1) {
//                                                    remainSubject.add(preparedSubject.get(si));
//                                                    continue subjectLoop;
//                                                }
//                                                if (usedListTH.get(j)[1].compareTo(cl.getId()) == 0) {
////                                        System.out.println("            ca thi " + i + " và lớp " + cl.getId() + " đã dùng");
//                                                    countRoomThUsedOfShift = usedListTH.stream().filter(e -> e[0].equals(shift + "")).collect(Collectors.toList()).size();
//                                                    if (countRoomThUsedOfShift == totalClassRoomTHList.size()) {
//                                                        continue shiftLoop;
//                                                    } else {
//                                                        continue loopFindClass;
//                                                    }
//                                                }
//                                            }
//
//                                        }
//
//                                        try {
//                                            ex.setRoom(cl);
//                                            ex.setIndex(examRoomIndex++);
//
//                                            ex.setCapacity(numberOfStudent);
//                                            numberOfStudent = 0;
//
//                                            SubjectSchedule ss = new SubjectSchedule(s, ex.clone(), i);
//                                            subjectSchedules.add(ss);
//
//                                            Set<String> set = subjectMap.get(s);
////                                    System.out.println("subject map size:"+subjectMap.size());
////                                    System.out.println(s + " " + (set == null));
//                                            set.add(this.date);
//                                            subjectMap.put(s, set);
//                                            for (int l = 0; l < 4; l++) {
//                                                final int finalL = l;
//                                                final ClassRoom finalCl = cl;
//                                                if (usedListTH.stream().filter(item -> (Integer.parseInt(item[0]) == finalL && item[1] == finalCl.getId())).count() > 0) {
//                                                    remainSubject.add(preparedSubject.get(si));
//                                                    if (remainClassRoomTHListClone.size() > 0)
//                                                        continue loopFindClass;
//                                                    else
//                                                        break loopFindClass;
//                                                }
//                                            }
//                                            for (int k = 1; k < 4; k++) {
//                                                usedListTH.add(new String[]{k + "", cl.getId()});
//                                                ss.setShift(k);
//                                                ss.getRoom().setCapacity(0);
//                                                subjectSchedules.add(ss);
//                                                thClassMap.put(cl.getName() + "-" + k, ss);
//                                            }
//
//                                            totalClassRoomUsedForSubjectShift++;
//                                            if (numberOfStudent == 0) {
//                                                break shiftLoop;
//                                            }
//                                            if (totalClassRoomUsedForSubjectShift == 4) {
//                                                break loopFindClass;
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }

//                                System.out.println("fd");
                            }
                        }
                    }
                }
            }
//            System.out.println("remain subjects:"+remainSubject.size());
        }
        preparedSubject.clear();
        return remainSubject;
    }

    // tính tổng số sv của 1 môn
    public int totalStudentOfRegistrationClassList(List<RegistrationClass> registrationClasses) {
        if (registrationClasses == null || registrationClasses.size() == 0) return 0;
        return registrationClasses.stream().reduce(0, (prev, e) -> prev + e.getEstimatedClassSizeReal(), Integer::sum);
    }

    public void setPreparedSubject(List<Subject> preparedSubject) {
        this.preparedSubject = preparedSubject;
    }

    public List<Subject> generateInitialSubjectSchedule() throws IOException {
        Random rd = new Random();
        int numSubject = subjectList.size() < 4 ? subjectList.size() : rd.nextInt((int) (subjectList.size() * 0.4)) + 2;

        for (int i = 0; i < numSubject; i++) {
            int randomIndex = rd.nextInt(remainSubject.size());
            preparedSubject.add(remainSubject.get(randomIndex));
            remainSubject.remove(randomIndex);
        }
        generateSchedule();
        return remainSubject;
    }

    public String toString() {
//        String s = "-"+date + "\n";
        String s = date + "\n";
        Collections.sort(subjectSchedules);
        for (SubjectSchedule ss : subjectSchedules) {
            s += (ss.toString() + "\n");
        }
        return s;
    }

    @Override
    public int compareTo(DateSchedule o) {
        return Double.compare(this.fitness, o.fitness);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String[]> getUsedList() {
        return usedList;
    }

    public void setUsedList(List<String[]> usedList) {
        this.usedList = usedList;
    }

    public List<String[]> getUsedListTH() {
        return usedListTH;
    }

    public void setUsedListTH(List<String[]> usedListTH) {
        this.usedListTH = usedListTH;
    }

    public List<String[]> getUsedListLT() {
        return usedListLT;
    }

    public void setUsedListLT(List<String[]> usedListLT) {
        this.usedListLT = usedListLT;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Map<Subject, Set<String>> getSubjectMap() {
        return subjectMap;
    }

    public void setSubjectMap(Map<Subject, Set<String>> subjectMap) {
        this.subjectMap = subjectMap;
    }

    public List<Subject> getPreparedSubject() {
        return preparedSubject;
    }

    public List<Subject> getRemainSubject() {
        return remainSubject;
    }

    public void setRemainSubject(List<Subject> remainSubject) {
        this.remainSubject = remainSubject;
    }

    public List<ClassRoom> getRemainClassRoomTHList() {
        return remainClassRoomTHList;
    }

    public void setRemainClassRoomTHList(List<ClassRoom> remainClassRoomTHList) {
        this.remainClassRoomTHList = remainClassRoomTHList;
    }

    public List<ClassRoom> getRemainClassRoomLTList() {
        return remainClassRoomLTList;
    }

    public void setRemainClassRoomLTList(List<ClassRoom> remainClassRoomLTList) {
        this.remainClassRoomLTList = remainClassRoomLTList;
    }

    public List<RegistrationClass> getRegistrationClasses() {
        return registrationClasses;
    }

    public void setRegistrationClasses(List<RegistrationClass> registrationClasses) {
        this.registrationClasses = registrationClasses;
    }
}
