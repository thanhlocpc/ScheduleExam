package com.schedule.initialization.ga;

import com.schedule.initialization.models.*;


import java.io.IOException;
import java.util.*;


/**
 * @author ThanhLoc
 * @created 2/6/2023
 */
public class GA {
    public static final int POP_SIZE = 500;
    public static final int N_ITER = 1000;
    public List<String> dates;
    List<Schedule> schedules = new ArrayList<>();
    Random rd = new Random();
    public List<Integer> scList;
    public GA(List<String> dates,List<Integer> scList) {
        this.dates = dates;
        this.scList=scList;
    }

    public List<Schedule> createPopulation() throws IOException {
        Schedule temp;
        for (int i = 0; i < POP_SIZE; i++) {
            while (true) {
                temp = new Schedule(dates,scList);
                if (temp.isAccepted()) {
                    schedules.add(temp);
                    break;
                }
            }
        }
        return schedules;
    }

    public Schedule ga() throws IOException, CloneNotSupportedException {
        createPopulation();
        int iter = 0;
        while (iter < N_ITER) {
            List<Schedule> newSchedule = new ArrayList<>();
            for (int i = 0; i < POP_SIZE; i++) {
                Schedule x = getParentByRandomSelection();
                Schedule y = getParentByRandomSelection();
                Schedule child = preproduce3(x, y);
                if (rd.nextInt(100) <= 0.3)
                    mutate(child);

                if (child.fitness < 200) {
//                    System.out.println("iter: " + iter);
                    return child;
                }
                newSchedule.add(child);
            }
            schedules = newSchedule;
            iter++;
        }
        Collections.sort(schedules, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule o1, Schedule o2) {
                return Double.compare(o1.fitness, o2.fitness);
            }
        });
        return schedules.get(0);
    }

    public Schedule getParentByRandomSelection() {
        // Enter your code here
        return schedules.get(rd.nextInt(POP_SIZE));
    }

    public Schedule preproduce3(Schedule x, Schedule y) throws CloneNotSupportedException, IOException {
        Schedule result = x.clone();
        Schedule changeSchedule = x.clone();
        List<Map.Entry<Subject, Set<String>>> sequence = new ArrayList<>();
        List<Map.Entry<Subject, Set<String>>> subjectMap1 = new ArrayList<>(x.getSubjectMap().entrySet());
        List<Map.Entry<Subject, Set<String>>> subjectMap2 = new ArrayList<>(y.getSubjectMap().entrySet());
        for (int i = 0; i < subjectMap2.size(); i++) {
            if (rd.nextInt(10) < 5) {
                sequence.add(subjectMap2.get(i));
            }
        }
        for (Map.Entry<Subject, Set<String>> entry : sequence) {
            changeSchedule.changeSchedule(entry);
            changeSchedule.fitness();
            if (changeSchedule.isAccepted()) {

                if ((changeSchedule.fitness < result.fitness)) {
                    result = (Schedule) changeSchedule.clone();
                    result.fitness();

                }
            }
        }
        return result;
    }


    public Schedule preproduce2(Schedule x, Schedule y) throws CloneNotSupportedException, IOException {
        Schedule result = x.clone();
        Schedule changeSchedule = x.clone();
        List<Map.Entry<Subject, Set<String>>> sequence = new ArrayList<>();
        List<Map.Entry<Subject, Set<String>>> subjectMap1 = new ArrayList<>(x.getSubjectMap().entrySet());
        List<Map.Entry<Subject, Set<String>>> subjectMap2 = new ArrayList<>(y.getSubjectMap().entrySet());
        for (int i = 0; i < subjectMap2.size(); i++) {
            if (rd.nextInt(10) < 5) {
                sequence.add(subjectMap2.get(i));
            }
        }
        for (int i = 0; i < subjectMap1.size(); i++) {
            if (!sequence.contains(subjectMap2.get(i))) {
                sequence.add(subjectMap1.get(i));
            }
        }
        for (Map.Entry<Subject, Set<String>> entry : sequence) {
            changeSchedule.changeSchedule(entry);
            changeSchedule.fitness();
            if (changeSchedule.isAccepted()) {

                if ((changeSchedule.fitness < result.fitness)) {
                    result = (Schedule) changeSchedule.clone();
                    result.fitness();

                }
            }
        }
        return result;
    }

    public void mutate(Schedule child) {
        int count = rd.nextInt(5);
        for (int i = 0; i < count; i++) {
            int a = rd.nextInt(dates.size());
            int b = rd.nextInt(dates.size());
            DateSchedule dateSchedule1 = child.getDateScheduleByDate(dates.get(a));
            DateSchedule dateSchedule2 = child.getDateScheduleByDate(dates.get(b));
            dateSchedule1.setDate(dates.get(a));
            dateSchedule2.setDate(dates.get(b));
        }
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        List<String> dates = new ArrayList<>();
        dates.add("2022-10-12");
        dates.add("2022-10-13");
        dates.add("2022-10-14");
        dates.add("2022-10-15");
        dates.add("2022-10-16");
        dates.add("2022-10-17");
        dates.add("2022-10-18");
        dates.add("2022-10-19");
        dates.add("2022-10-20");
        long beginTime = 0;
        long endTime = 0;
        List properties= Arrays.asList(10,10,10,10,10,10);
        for (int i = 0; i < 30; i++) {
//            System.out.println("==========begin " + i + " ==============");
            beginTime = System.currentTimeMillis();
//            System.out.println("schedule " + i + ":");
            GA ga = new GA(dates,properties);
            Schedule result = ga.ga();
//            System.out.println(result.fitness);
//            System.out.println("is accepted:" + result.isAccepted());
//            result.getDateScheduleList().forEach(item -> {
//                System.out.println(item);
//            });
            endTime = System.currentTimeMillis();
            System.out.println(i+","+((endTime-beginTime)/1000)+","+result.fitness);
//            System.out.println("iter " + i + ":" + (endTime - beginTime) / 60000);
//            System.out.println("==========end==============");
        }
    }
}