package models;

import java.io.*;
import java.util.*;
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

        this.subjectList = getSubjectList();
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

            if (subject != null) {
                classRooms.add(new RegistrationClass(tokens[0], tokens[1], Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]), subject, new Grade(tokens[4], tokens[4])));
                int currentCap = check.get(subject.getId());
                check.put(subject.getId(), currentCap + Integer.parseInt(tokens[3]));
            }
            line = reader.readLine();


        }
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
            subjectList.add(new Subject(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3])));
            line = reader.readLine();
        }
        return subjectList;
    }

    public boolean isAccepted() throws IOException {
        return remainSubject == 0 && isFinish();
    }

    public void fitness() {
        double result = 0;

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

        for (int i = 0; i < dateScheduleList.size(); i++) {
            List<SubjectSchedule> newSubjectSchedules = dateScheduleList.get(i).subjectSchedules.stream().map(e -> e).collect(Collectors.toList());
            Map<Integer, Integer> mapCountRoomOfShift = new HashMap<>();
            newSubjectSchedules.stream().forEach(e -> {
                if (mapCountRoomOfShift.get(e.getShift()) != null) {
                    mapCountRoomOfShift.put(e.shift, mapCountRoomOfShift.get(e.shift) + 1);
                } else {
                    mapCountRoomOfShift.put(e.shift, 1);
                }
            });

            // tính weight
            int weight = 0;
            boolean isASC = true;
            int min = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Integer> entry : mapCountRoomOfShift.entrySet()) {
                if (entry.getValue() / 2 != 0) {
                    isASC = false;
                    weight += 50;
                }
                if (entry.getValue() <= min) {
                    min = entry.getValue();
                } else {
                    isASC = false;
                    weight += 450;
                }
            }
            if (isASC) weight = 0;
            result += weight;
        }


        // một ngày 1 khối lớp không nên cho thi nhiều môn, tối đa 2 môn
        // đếm số môn thi của 1 khối lớp trong ngày
        for (int i = 0; i < dateScheduleList.size(); i++) {

            List<SubjectSchedule> newSubjectSchedules = dateScheduleList.get(i).subjectSchedules.stream().map(e -> e).collect(Collectors.toList());

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

            int weight = 0;
            for (Map.Entry<String, Set<String>> entry : mapCountSubjectOfGrade.entrySet()) {
                // 1 học sinh thi lớn hơn 1 môn trên ngày
                if (entry.getValue().size() > 2) {
                    weight += 1500;
                } else if (entry.getValue().size() > 1) {
                    weight += 500;
                }
            }
            ;
            result += weight;
        }

        // 1 môn thi không nên chia quá ít phòng thi trong 1 ca thi
        // vd: khi chia 1 môn làm 3 ca thi, mỗi ca thi 2 phòng
        // đếm số phòng thi của môn đó, trong ngày đó
        // đếm số ca thi của môn đó trong ngày đó
        // 1 ca thi của 1 môn phải tối thiếu đạt 25%: ca 1: 4phong, ca2: 3 phong, ca3: 3phongf
        // môn thi A: thi 3 ca, mỗi ca có 5 phòng => tối đa cho môn này là 15 phòng
        // bắt buộc mỗi ca thi của môn nào đó phải đạt tối thiểu 4 phòng thi, đối với môn thi có tổng số phòng thi >4 (môn lý thuyết)
        // nhỏ hơn 4 thì phải chung 1 ca thi
        for (int i = 0; i < dateScheduleList.size(); i++) {

            List<SubjectSchedule> newSubjectSchedules = dateScheduleList.get(i).subjectSchedules.stream().map(e -> e).filter(item->item.getSubject().getExamForms()!=2).collect(Collectors.toList());

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

            int weight = 0;
            for (Map.Entry<String, Integer> entry : mapCountRoomOfSubject.entrySet()) {
                // nếu môn này có tổng số phòng thi > 4
                // thì xem số ca thi có hợp lí hay không
                if (entry.getValue() > 4) {
                    if (entry.getValue() / mapCountShiftOfSubject.get(entry.getKey()).size() < 3.1) {
                        weight += 500;
                    }
                } else {
                    if (mapCountShiftOfSubject.get(entry.getKey()).size() > 1) {
                        weight += 1500;
                    }
                }
            }
            result += weight;
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
