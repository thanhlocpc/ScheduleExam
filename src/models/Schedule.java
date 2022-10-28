package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
 * lịch thi
 *
 */
public class Schedule implements Comparable<Schedule> {
    private List<DateSchedule> dateScheduleList;
    public double fitness;
    int remainSubject;
    List<Subject> subjectList;
    Map<Subject, Set<String>> subjectMap;

    public Schedule(List<String> dates) throws IOException {

        this.subjectList = getSubjectList();
        subjectMap = new HashMap<>();
        for (Subject s : subjectList) {
            subjectMap.put(s, new HashSet<>());
        }
        generateSchedule(dates);
        fitness();
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

    public Schedule clone() {
        Schedule cloneSchedule = new Schedule();
        cloneSchedule.setDateScheduleList(this.dateScheduleList);
        cloneSchedule.setRemainSubject(this.remainSubject);
        cloneSchedule.setSubjectList(this.subjectList);
        cloneSchedule.setSubjectMap(new HashMap<>(this.subjectMap));
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

    //    public void changeSchedule(List<Map.Entry<Subject, Set<String>>> subjectListChange) throws IOException {
//        List<Map.Entry<Subject, Set<String>>> subjectListChangeTmp = new ArrayList<>(subjectListChange);
//        for (Map.Entry<Subject, Set<String>> entry : subjectListChangeTmp) {
//            Set<String> dateChangeSet = entry.getValue();
//            Subject subjectChange = entry.getKey();
//            for (DateSchedule ds : dateScheduleList) {
//                if (ds.isContainSubject(subjectChange)) {
//                    ds.deleteSubject(subjectChange);
//                    break;
//                }
//            }
//            DateSchedule d = getDateScheduleByDate((String) dateChangeSet.toArray()[0]);
//            d.addNewSubject(subjectChange);
//            this.fitness();
//            System.out.println(subjectChange.getName()+" "+fitness);
//        }
//
//    }
    public void changeSchedule(Map.Entry<Subject, Set<String>> subjectChangeEntry) throws IOException {
        Set<String> dateChangeSet = subjectChangeEntry.getValue();
        Subject subjectChange = subjectChangeEntry.getKey();
        for (DateSchedule ds : dateScheduleList) {
            if (ds.isContainSubject(subjectChange)) {
                ds.deleteSubject(subjectChange);
                break;
            }
        }
        DateSchedule d = getDateScheduleByDate((String) dateChangeSet.toArray()[0]);
        d.addNewSubject(subjectChange);
        this.fitness();
        System.out.println(subjectChange.getName() + " " + fitness);
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

    public void fitness() {
        double result = 0;

        //tính sự chênh lêch số tiết thi giữa các ngày thi
        double examAmountDiifPerDay = 0;
        for (int i = 0; i < dateScheduleList.size(); i++) {
            examAmountDiifPerDay += dateScheduleList.get(i).subjectSchedules.size();
        }
        examAmountDiifPerDay /= dateScheduleList.size();

        for (int i = 0; i < dateScheduleList.size(); i++) {
            result += Math.abs(dateScheduleList.get(i).subjectSchedules.size() - examAmountDiifPerDay);
            dateScheduleList.get(i).fitness();
            result += dateScheduleList.get(i).fitness;
        }
        //////////////////////////
//        System.out.println("remain subject:" + remainSubject);
        result += remainSubject * 1000;

        this.fitness = result;
    }

    public List<DateSchedule> getDateScheduleList() {
        return dateScheduleList;
    }

    public Map<Subject, Set<String>> getSubjectMap() {
        return subjectMap;
    }

    public static void main(String[] args) throws IOException {

//        List<Subject> subjectList = s.getSubjectList();
        List<String> dates = new ArrayList<>();
        dates.add("12/10/2022");
        dates.add("13/10/2022");
        dates.add("14/10/2022");
        dates.add("15/10/2022");
        dates.add("16/10/2022");
        dates.add("17/10/2022");
        Schedule s = new Schedule(dates);
//        s.generateSchedule(dates);
//        List<DateSchedule> dses = s.getDateScheduleList();
//        for (int i = 0; i < dses.size(); i++) {
//            System.out.println(dses.get(i).toString());
////            dses.get(i).deleteSubject(new Subject("214462","Ltw",4,2));
//            System.out.println("2"+dses.get(i).toString());
//            System.out.println("prepareSubject count: " + dses.get(i).preparedSubject.size());
//
//        }
//        s.fitness();
//        System.out.println("remainSubject count: " + s.remainSubject);
//        System.out.println("fitness: " + s.fitness);
        Map<Subject, Set<String>> subjectMap = s.subjectMap;
        Subject sub = new Subject("214462", "Ltw", 4, 2);
        Set<String> set = subjectMap.get(sub);
        System.out.println(set.size());

//        for (Map.Entry<Subject, Set<String>> entry : s.subjectMap.entrySet()) {
//            System.out.println(entry.getKey().getName());
//            Set<String> set = entry.getValue();
//            for (String ss : set) {
//                System.out.println(ss);
//            }
//        }
    }


    @Override
    public int compareTo(Schedule o) {
        return Double.compare(this.fitness, o.fitness);
    }
}
