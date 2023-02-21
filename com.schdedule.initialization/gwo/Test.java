package gwo;

import models.ChangeScheduleRequest;
import models.DateSchedule;
import models.Schedule;
import models.Subject;

import java.io.*;
import java.util.*;




public class Test {


//    public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException {
//        FileInputStream fileInt = new FileInputStream("data/result");
//        ObjectInputStream objectInputStream = new ObjectInputStream(fileInt);
//        Schedule readSchedule = (Schedule) objectInputStream.readObject();
//        objectInputStream.close();
//        System.out.println("========schedule read from file");
//        List<DateSchedule> dses1 = readSchedule.getDateScheduleList();
//        for (int i = 0; i < dses1.size(); i++) {
//            System.out.println(dses1.get(i).toString());
//        }
//        System.out.println("===========================");
//
//        List<ChangeScheduleRequest> changeScheduleRequests = new ArrayList<>();
//        changeScheduleRequests.add(new ChangeScheduleRequest("214463", "2022-10-13"));//ai
////        changeScheduleRequests.add(new ChangeScheduleRequest("214483", "2022-10-12"));//tmdt
//
//        Schedule scheduleAfterChange = changeSchedule(changeScheduleRequests, readSchedule,"data");
//        System.out.println("========schedule read from file");
//        List<DateSchedule> dses = scheduleAfterChange.getDateScheduleList();
//        System.out.println(scheduleAfterChange.isAccepted());
//        System.out.println(scheduleAfterChange.fitness);
//        for (int i = 0; i < dses.size(); i++) {
//            System.out.println(dses.get(i).toString());
//        }
//        System.out.println("===========================");
//    }
}
