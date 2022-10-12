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
    public static List<DateSchedule> generateSchedule(List<Subject> subjectList) {
        List<DateSchedule> dateScheduleList = new ArrayList<>();

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

        for (Subject s : subjectList) {
            System.out.println(s.toString());
        }
    }
}
