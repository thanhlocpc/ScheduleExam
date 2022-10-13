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
    //    private List<DateSchedule> dateScheduleList;
    public static List<DateSchedule> generateSchedule(List<Subject> subjectList, List<String> dates) {
        List<DateSchedule> dateScheduleList = new ArrayList<>();
        List<Subject> remainSubjectList = new ArrayList<>(subjectList);

        for (String d : dates) {
            dateScheduleList.add(new DateSchedule(d, remainSubjectList));
        }
        return dateScheduleList;
    }

    public static List<Subject> getSubjectList() throws IOException {
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


    public static void main(String[] args) throws IOException {
        List<Subject> subjectList = getSubjectList();
        List<String> dates = new ArrayList<>();
        dates.add("12/10/2022");
        dates.add("13/10/2022");
        dates.add("14/10/2022");
        dates.add("15/10/2022");
        dates.add("16/10/2022");
        dates.add("17/10/2022");
        List<DateSchedule> dses = new ArrayList<>();
//        while (subjectList.size() > 0) {
//
//        }
        for (String d : dates) {
            DateSchedule ds = new DateSchedule(d, subjectList);
            subjectList = ds.generateSubjectSchedule();
            dses.add(ds);
            System.out.println(ds.toString());
        }
    }
}
