package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * lịch thi
 *
 */
public class Schedule {
    private List<DateSchedule> dateScheduleList;
    double fitness;
    int remainSubject;

    public void generateSchedule(List<Subject> subjectList, List<String> dates) throws IOException {
        List<DateSchedule> dateScheduleList = new ArrayList<>();
        List<Subject> remainSubjectList = new ArrayList<>(subjectList);

        for (String d : dates) {
            DateSchedule ds = new DateSchedule(d, remainSubjectList);
            remainSubjectList = ds.generateSubjectSchedule();
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
        System.out.println("remain subject:" + remainSubject);
        result += remainSubject * 1000;

        this.fitness = result;
    }

    public List<DateSchedule> getDateScheduleList() {
        return dateScheduleList;
    }

    public static void main(String[] args) throws IOException {
        Schedule s = new Schedule();
        List<Subject> subjectList = s.getSubjectList();
        List<String> dates = new ArrayList<>();
        dates.add("12/10/2022");
        dates.add("13/10/2022");
        dates.add("14/10/2022");
        dates.add("15/10/2022");
        dates.add("16/10/2022");
        dates.add("17/10/2022");
        s.generateSchedule(subjectList, dates);
        List<DateSchedule> dses = s.getDateScheduleList();
        for (int i = 0; i < dses.size(); i++) {
            System.out.println(dses.get(i).toString());
        }
        s.fitness();
        System.out.println("fitness: " + s.fitness);
    }


}
