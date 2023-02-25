package com.schedule.initialization.models;

import com.schedule.initialization.utils.ExcelFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/*
 * lịch thi gồm:
 * - danh sách lích thi theo ngày
 * - fitness
 *
 */
public class Schedule implements Comparable<Schedule> ,Cloneable, Serializable {
    private List<DateSchedule> dateScheduleList;
    public double fitness;
    int remainSubject;
    List<Subject> subjectList;
    Map<Subject, Set<String>> subjectMap;
    List<SubjectSchedule> totalSubjectSchedule;

    public Schedule(List<String> dates) throws IOException {

        this.subjectList = ExcelFile.getSubjects();
        subjectMap = new HashMap<>();
        for (Subject s : subjectList) {
            subjectMap.put(s, new HashSet<>());
        }
        generateSchedule(dates);
        fitness();
    }

    public boolean isFinish() throws IOException {
        totalSubjectSchedule = new ArrayList<>();

        Map<String, Integer> check = new HashMap<>();
        for (Subject s : subjectList) {
            check.put(s.getId(), 0);
        }
        //list registration class
        List<RegistrationClass> classRooms = new ArrayList<>();
//        BufferedReader reader = new BufferedReader(new FileReader("data/RegistrationClass"));
//        String line = reader.readLine();

        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream("data/data.xlsx"));
        XSSFSheet sheet = wb.getSheetAt(2);
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while (itr.hasNext()){
            Row row = itr.next();
            Subject subject = null;
            for (Subject s : subjectList) {
                String ssName=row.getCell(0).getStringCellValue();
                if (s.getId().equals(ssName.substring(0, ssName.lastIndexOf("-")))) {
                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms(), s.getExamTime(), s.getLessonTime());
                    break;
                }
            }
            if (subject != null)
                classRooms.add(new RegistrationClass(row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        (int)row.getCell(2).getNumericCellValue(),
                        (int)row.getCell(3).getNumericCellValue(),
                        subject,
                        new Grade(row.getCell(4).getStringCellValue(), (int)row.getCell(6).getNumericCellValue()+""),
                        (int)row.getCell(5).getNumericCellValue()));
            int currentCap = check.get(subject.getId());
            check.put(subject.getId(), currentCap + (int)row.getCell(3).getNumericCellValue());
        }

//        while (line != null) {
//            String[] tokens = line.split(",");
//            Subject subject = null;
//            for (Subject s : subjectList) {
//                if (s.getId().equals(tokens[0].substring(0, tokens[0].lastIndexOf("-")))) {
//                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms(), s.getExamTime(), s.getLessonTime());
//                    break;
//                }
//            }
//
//            if (subject != null) {
//                classRooms.add(new RegistrationClass(tokens[0], tokens[1], Integer.parseInt(tokens[2]),
//                        Integer.parseInt(tokens[3]), subject, new Grade(tokens[4], tokens[6]),Integer.parseInt(tokens[5])));
//                int currentCap = check.get(subject.getId());
//                check.put(subject.getId(), currentCap + Integer.parseInt(tokens[3]));
//            }
//            line = reader.readLine();
//        }
//

        for (DateSchedule ds : dateScheduleList) {
            totalSubjectSchedule.addAll(ds.subjectSchedules);
        }
        for (SubjectSchedule ss : totalSubjectSchedule) {
            String id = ss.getSubject().getId();
            int numberOfStudent = ss.getRoom().getCapacity();
            int oldNumber = check.get(id);
            check.put(id, oldNumber - numberOfStudent);
        }
        int result = 0;
        for (Map.Entry<String, Integer> entry : check.entrySet()) {
            result += entry.getValue();
//            System.out.println(entry.getKey() + " left:" + entry.getValue());
        }
        return result == 0;
    }


    public Schedule() {

    }

    public void setDateScheduleList(List<DateSchedule> dateScheduleList) {
        this.dateScheduleList = new ArrayList<>(dateScheduleList);
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setRemainSubject(int remainSubject) {
        this.remainSubject = remainSubject;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public void setSubjectMap(Map<Subject, Set<String>> subjectMap) {
        this.subjectMap = subjectMap;
    }

    public Schedule clone() throws CloneNotSupportedException {
        Schedule cloneSchedule = (Schedule)super.clone();
        List dateScheduleListClone=new ArrayList();
        for (DateSchedule d:dateScheduleList){
            dateScheduleListClone.add(d.clone());
        }
        cloneSchedule.setDateScheduleList(dateScheduleListClone);
        cloneSchedule.setRemainSubject(this.remainSubject);
        //
        List<Subject> subjectListClone=new ArrayList<>();
        for(Subject s:subjectList){
            subjectListClone.add(s.clone());
        }
        cloneSchedule.setSubjectList(subjectListClone);
        //
        Map<Subject,Set<String>> subjectMapClone=new HashMap<>();
        for (Map.Entry<Subject, Set<String>> entry : subjectMap.entrySet()) {
            subjectMapClone.put(entry.getKey().clone(),new HashSet<>(entry.getValue()));
        }
        cloneSchedule.setSubjectMap(subjectMapClone);
        cloneSchedule.fitness();
        return cloneSchedule;
    }

    public DateSchedule getDateScheduleByDate(String date) {
        for (DateSchedule d : dateScheduleList) {
            if (d.getDate().equals(date))
                return d;
        }
        return null;
    }

    public void changeSchedule(Map.Entry<Subject, Set<String>> subjectChangeEntry) throws IOException {
        Set<String> dateChangeSet = subjectChangeEntry.getValue();
        Subject subjectChange = subjectChangeEntry.getKey();
        for (DateSchedule ds : dateScheduleList) {
            if (ds.isContainSubject(subjectChange)) {
                ds.deleteSubject(subjectChange);
            }
        }
        if (dateChangeSet.toArray().length > 0) {
            DateSchedule d = getDateScheduleByDate((String) dateChangeSet.toArray()[0]);
            long begin_add_new_subject = System.currentTimeMillis();
            d.addNewSubject(subjectChange);
            long end_add_new_subject = System.currentTimeMillis();
//            System.out.println("time add new subject:"+(end_add_new_subject-begin_add_new_subject));

        }
        this.fitness();
    }

    public void changeSchedule2(Map.Entry<Subject, Set<String>> subjectChangeEntry) throws IOException {
        Set<String> dateChangeSet = subjectChangeEntry.getValue();
        Subject subjectChange = subjectChangeEntry.getKey();
        for (DateSchedule ds : dateScheduleList) {
            if (ds.isContainSubject(subjectChange)) {
                ds.deleteSubject(subjectChange);
            }
        }
        if (dateChangeSet.toArray().length > 0) {
            DateSchedule d = getDateScheduleByDate((String) dateChangeSet.toArray()[0]);
            d.addNewSubject(subjectChange);
        }
        this.fitness();
    }
    public void generateSchedule(List<String> dates) throws IOException {
        List<DateSchedule> dateScheduleList = new ArrayList<>();
        List<Subject> remainSubjectList = new ArrayList<>(this.subjectList);

        for (String d : dates) {
            DateSchedule ds = new DateSchedule(d, remainSubjectList, subjectMap);
            remainSubjectList = ds.generateInitialSubjectSchedule();
            dateScheduleList.add(ds);
        }
        this.remainSubject = remainSubjectList.size();
        this.dateScheduleList = dateScheduleList;
    }

    public List<Subject> getSubjectList() throws IOException {
        List<Subject> subjectList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("data/subject"));
        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");
            subjectList.add(new Subject(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5])));
            line = reader.readLine();
        }
        return subjectList;
    }

    public boolean isAccepted() throws IOException {
        return remainSubject == 0 && isFinish();
    }

    public void fitness() {
        double result = 0;



        for (int i = 0; i < dateScheduleList.size(); i++) {
            // ds lịch thi của ngày
            List<SubjectSchedule> newSubjectSchedules = dateScheduleList.get(i).subjectSchedules.stream()
                    .filter(item->item.getSubject().getExamForms()!=2).collect(Collectors.toList());

            // không sắp xếp ca thi rời rạc, làm khó cán bộ coi thi
            // ca 1: 5 phòng thi
            // ca 2: 1 phòng thi
            // ca 3: 5 phòng thi

            // ca 1: 5 phòng thi
            // ca 2: 5 phòng thi
            // ca 3: 1 phòng thi

            // ca 1: 5 phòng thi
            // ca 2: 5 phòng thi
            // ca 3: 4 phòng thi
            // ca 4: 5 phòng thi
            // => ưu tiên xếp ca giảm dần: 4,3,2,1
            Map<Integer, Integer> mapCountRoomOfShift = new HashMap<>(); // key: ca, value: số phòng
            newSubjectSchedules.stream().forEach(e -> {
                if (mapCountRoomOfShift.get(e.getShift()) != null) {
                    mapCountRoomOfShift.put(e.shift, mapCountRoomOfShift.get(e.shift) + 1);
                } else {
                    mapCountRoomOfShift.put(e.shift, 1);
                }
            });

            int h1 = 0;
            boolean isDESC = true; // giảm dần
            int min = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Integer> entry : mapCountRoomOfShift.entrySet()) {
//                if (entry.getValue() / 2 != 0) { //
//                    isASC = false;
//                    h1 ++;
//                }
                if (entry.getValue() <= min) {
                    min = entry.getValue();
                } else {
                    isDESC = false;
                    h1 ++;
                }
            }
            if (isDESC) h1 = 0;
            result += h1 * 10;


            // một ngày 1 khối lớp không nên cho thi nhiều môn, tối đa 2 môn
            // đếm số môn thi của 1 khối lớp trong ngày

            Map<String, Set<String>> mapCountSubjectOfGrade = new HashMap<>();
            newSubjectSchedules.stream().forEach(e -> {
                if (mapCountSubjectOfGrade.get(e.getRoom().getRegistrationClass().getGrade().getId()) != null) {
                    mapCountSubjectOfGrade.get(e.getRoom().getRegistrationClass().getGrade().getId()).add(e.getSubject().getId());
                } else {
                    Set<String> subs = new HashSet<>();
                    subs.add(e.getSubject().getId());
                    mapCountSubjectOfGrade.put(e.getRoom().getRegistrationClass().getGrade().getId(), subs);
                }
            });

            int h2 = 0;
            for (Map.Entry<String, Set<String>> entry : mapCountSubjectOfGrade.entrySet()) {
                // 1 học sinh thi lớn hơn 1 môn trên ngày
                if (entry.getValue().size() > 2) {
                    h2++;
                } else if (entry.getValue().size() > 1) {
                    h2++;
                }
            }
            result += h2 * 10;


            // 1 môn thi không nên chia quá ít phòng thi trong 1 ca thi
            // vd: khi chia 1 môn làm 3 ca thi, mỗi ca thi 2 phòng
            // đếm số phòng thi của môn đó, trong ngày đó
            // đếm số ca thi của môn đó trong ngày đó
            // 1 ca thi của 1 môn phải tối thiếu đạt 25%: ca 1: 4phong, ca2: 3 phong, ca3: 3phongf
            // môn thi A: thi 3 ca, mỗi ca có 5 phòng => tối đa cho môn này là 15 phòng
            // bắt buộc mỗi ca thi của môn nào đó phải đạt tối thiểu 4 phòng thi, đối với môn thi có tổng số phòng thi >4 (môn lý thuyết)
            // nhỏ hơn 4 thì phải chung 1 ca thi
            Map<String, Integer> mapCountRoomOfSubject = new HashMap<>();
            Map<String, Set<Integer>> mapCountShiftOfSubject = new HashMap<>();
            newSubjectSchedules.stream().forEach(e -> {
                if (mapCountRoomOfSubject.get(e.getSubject().getId()) != null) {
                    mapCountRoomOfSubject.put(e.getSubject().getId(), mapCountRoomOfSubject.get(e.getSubject().getId()) + 1);
                } else {
                    mapCountRoomOfSubject.put(e.getSubject().getId(), 1);
                }

                if (mapCountShiftOfSubject.get(e.getSubject().getId()) != null) {
                    mapCountShiftOfSubject.get(e.getSubject().getId()).add(e.getShift());
                } else {
                    Set<Integer> shifts = new HashSet<>();
                    shifts.add(e.getShift());
                    mapCountShiftOfSubject.put(e.getSubject().getId(), shifts);

                }
            });

            int h3 = 0;
            for (Map.Entry<String, Integer> entry : mapCountRoomOfSubject.entrySet()) {
                // nếu môn này có tổng số phòng thi > 4
                // thì xem số ca thi có hợp lí hay không
                if (entry.getValue() > 4) {
                    if (entry.getValue() / mapCountShiftOfSubject.get(entry.getKey()).size() < 3.1) {
                        h3++;
                    }
                } else {
                    if (mapCountShiftOfSubject.get(entry.getKey()).size() > 1) {
                        h3++;
                    }
                }
            }
            result += h3 * 10;


            //Một ca thi hạn chế xếp nhiều hơn 2 môn học tránh bị trùng lặp lịch thi của sinh viên
            // đếm số môn học xuất hiện trong 1 ca thi (cùng ngày)

            // key: ca, value: ds môn học
            Map<String, Set<String>> mapCountSubjectOfShift = new HashMap<>();

            newSubjectSchedules.stream().forEach(e -> {
                if (mapCountSubjectOfShift.get(e.getShift()+"") == null) {
                    Set<String> subjectIds = new HashSet<>();
                    subjectIds.add(e.getSubject().getId());
                    mapCountSubjectOfShift.put(e.getShift()+"", subjectIds);
                } else {
                    mapCountSubjectOfShift.get(e.getShift()+"").add(e.getSubject().getId());
                }
            });

            int h4 = 0;
            for (Map.Entry<String, Set<String>> entry : mapCountSubjectOfShift.entrySet()) {
                if(entry.getValue().size() > 1){
                    h4++;
                }
            }
            result += h4 * 10;


            // Một phòng thi sau khi sắp xếp phải có sv tham dự lớn hơn 50% sức chứa của phòng thi đó
            AtomicInteger h5 = new AtomicInteger();
            newSubjectSchedules.forEach(e -> {
                double rate = (double)e.getRoom().getCapacity() / e.getRoom().getRoom().getCapacityExam();
                // 0 : chỉ xảy ra đối mới môn thi vấn đáp
                if(rate < 0.5 && rate != 0){
                    h5.getAndIncrement();
                }
            });
            result += h5.get() * 10;
//            String ff="";


            // Một lớp đăng kí học phần ưu tiên xếp trong 1 ca
            // => đếm số cả của 1 lớp đăng kí học phần
            // key: lớp đăng kí học phần, value: số ca
            Map<String, Set<Integer>> mapCountShiftOfCourse = new HashMap<>();

            newSubjectSchedules.stream().forEach(e -> {
                if (mapCountShiftOfCourse.get(e.getRoom().getRegistrationClass().getId()) == null) {
                    Set<Integer> shifts = new HashSet<>();
                    shifts.add(e.getShift());
                    mapCountShiftOfCourse.put(e.getRoom().getRegistrationClass().getId(), shifts);
                } else {
                    mapCountShiftOfCourse.get(e.getRoom().getRegistrationClass().getId()).add(e.getShift());
                }
            });
            int h6 = 0;
            for (Map.Entry<String, Set<Integer>> entry : mapCountShiftOfCourse.entrySet()) {
               if(entry.getValue().size() > 1){
                   h6++;
               }
            }
            result += h6 * 10;
        }

        this.fitness = result;
    }

    public List<DateSchedule> getDateScheduleList() {
        return dateScheduleList;
    }

    public Map<Subject, Set<String>> getSubjectMap() {
        return subjectMap;
    }

    public static void main(String[] args) throws IOException {
//
//        BufferedReader reader = new BufferedReader(new FileReader("result/scheduleRan"));
//        String line = reader.readLine();
//        String date="";
//        while (line != null) {
//            if(line.substring(0,1).compareTo("-")==0){
//                date=line.substring(1);
//            }else{
//            String[] tokens = line.split(",");
////            System.out.println("INSERT INTO `classroom`( `capacity_base`, `capacity_exam`, `name`, `classroom_type`) VALUES ("+tokens[2]+","+tokens[3]+",'"+tokens[0]+"',"+"'TH');");
//            System.out.println("INSERT INTO `subject_schedule`( `date_exam`, `shift`, `classroom_id`, `course_id`, `subject_id`,`subject_schedule_index`,`candidate_amount`) VALUES ('"+date+"','"+tokens[6]+"',(select id from `classroom` where name='"+tokens[3]
//                    +"'),(select id from `course` where name='"+tokens[1]+"'),(select id from `subject` where name='"+tokens[0]+"'),"+tokens[4]+","+tokens[5]+");");
//            }
//            line = reader.readLine();
//        }
        Set<String> setA=new HashSet<>();
        setA.add("2022-12-7");
        Set<String> setB=new HashSet<>();
        setB.add("2022-12-7");
        System.out.println(setA.containsAll(setB));
    }



    @Override
    public int compareTo(Schedule o) {
        return Double.compare(this.fitness, o.fitness);
    }
}
