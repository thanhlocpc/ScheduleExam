package gwo;

import models.DateSchedule;
import models.Schedule;
import models.Subject;

import java.io.IOException;
import java.util.*;

public class GWO {
    public static final int N_WOLF = 100;
    public static final int N_ITER = 1000;
    public List<String> dates;

    public GWO(List<String> dates) {
        this.dates = dates;
    }

    public Schedule[] createPopulation() throws IOException {
        Schedule[] schedules = new Schedule[N_WOLF];
        for (int i = 0; i < schedules.length; i++) {
            while (true) {
                schedules[i] = new Schedule(dates);
                if (schedules[i].isAccepted())
                    break;
            }
        }
        return schedules;
    }

    /*
     * Map.Entry<Subject, Set<String>> : set<String>: ds các ngày xếp lịch thi của môn đó
     */
    public List<Map.Entry<Subject, Set<String>>> swapSquence(Schedule a, Schedule b) {
        List<Map.Entry<Subject, Set<String>>> aList = new ArrayList<>(a.getSubjectMap().entrySet());
        List<Map.Entry<Subject, Set<String>>> bList = new ArrayList<>(b.getSubjectMap().entrySet());
        List<Map.Entry<Subject, Set<String>>> sequence = new ArrayList<>();
        for (int i = 0; i < aList.size(); i++) {
            Set<String> setA = aList.get(i).getValue();
            Set<String> setB = bList.get(i).getValue();
            if (setA.size() == setB.size()) {
                if (setA.containsAll(setB)) {
                    continue;
                }
            }
            sequence.add(aList.get(i));

        }
        Random rd = new Random();
        double c = rd.nextDouble();
        int count = (int) (c * sequence.size());
        List<Map.Entry<Subject, Set<String>>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int index = rd.nextInt(sequence.size());
            result.add(sequence.get(index));
            sequence.remove(index);
        }
        return result;
    }

    public void gwo() throws IOException, CloneNotSupportedException {
        Schedule[] schedules = createPopulation();
        Arrays.sort(schedules);
        Schedule alpha = schedules[0];
        System.out.println("alpha:" + alpha.fitness);
        Schedule beta = schedules[1];
        System.out.println("beta:" + beta.fitness);
        Schedule delta = schedules[2];
        System.out.println("delta:" + delta.fitness);
//        System.out.println("alpha");
//        for (Map.Entry<Subject, Set<String>> entry : alpha.getSubjectMap().entrySet()) {
//            System.out.print(entry.getKey().getName() + ":");
//            Set<String> set = entry.getValue();
//            for (String ss : set) {
//                System.out.print(ss + "-");
//            }
//            System.out.println();
//        }
//        System.out.println("beta");
//        for (Map.Entry<Subject, Set<String>> entry : beta.getSubjectMap().entrySet()) {
//            System.out.print(entry.getKey().getName() + ":");
//            Set<String> set = entry.getValue();
//            for (String ss : set) {
//                System.out.print(ss + "-");
//            }
//            System.out.println();
//        }
//        System.out.println("delta");
//        for (Map.Entry<Subject, Set<String>> entry : delta.getSubjectMap().entrySet()) {
//            System.out.print(entry.getKey().getName() + ":");
//            Set<String> set = entry.getValue();
//            for (String ss : set) {
//                System.out.print(ss + "-");
//            }
//            System.out.println();
//        }
//
//        Schedule scheduleInPopulation = schedules[50];
//        System.out.println("scheduleInPopulation");
//        for (Map.Entry<Subject, Set<String>> entry : scheduleInPopulation.getSubjectMap().entrySet()) {
//            System.out.print(entry.getKey().getName() + ":");
//            Set<String> set = entry.getValue();
//            for (String ss : set) {
//                System.out.print(ss + "-");
//            }
//            System.out.println();
//        }
//        System.out.println("scheduleInPopulation:" + scheduleInPopulation.fitness);
//        List<DateSchedule> dses2 = scheduleInPopulation.getDateScheduleList();
//        for (int i = 0; i < dses2.size(); i++) {
//            System.out.println(dses2.get(i).toString());
//
//        }

        int iter = 0;
        int bestIter = 0;
        Random random = new Random();
        whileloop:
        while (iter < N_ITER) {
            iter++;
            for (int i = 3; i < N_WOLF; i++) {
                Schedule scheduleInPopulation = schedules[i];
                List<Map.Entry<Subject, Set<String>>> swapList = new ArrayList<>();
                swapList.addAll(swapSquence(alpha, scheduleInPopulation));
                swapList.addAll(swapSquence(beta, scheduleInPopulation));
                swapList.addAll(swapSquence(delta, scheduleInPopulation));
//        System.out.println(swapList.size());
//        for (int i = 0; i < swapList.size(); i++) {
//            System.out.print(swapList.get(i).getKey() + "-");
//            for (String s : swapList.get(i).getValue()) {
//                System.out.print(s + "/");
//            }
//            System.out.println();
//        }
                Schedule bestChange = scheduleInPopulation.clone();
                for (Map.Entry<Subject, Set<String>> entry : swapList) {
                    scheduleInPopulation.changeSchedule(entry);
//                    scheduleInPopulation.findBestSchedule();
                    scheduleInPopulation.fitness();
                    System.out.println("scheduleInPopulation:" + scheduleInPopulation.fitness + "--- bestChange:" + bestChange.fitness);

                    if (scheduleInPopulation.fitness < bestChange.fitness) {
                        bestChange = scheduleInPopulation.clone();
                        bestChange.fitness();
                    }
                }
                scheduleInPopulation = bestChange.clone();
                scheduleInPopulation.fitness();
                System.out.println(scheduleInPopulation.fitness);
                if (scheduleInPopulation.isAccepted()) {
                    if (scheduleInPopulation.fitness < alpha.fitness) {
                        Schedule temp = delta.clone();
                        delta = beta.clone();
                        beta = alpha.clone();
                        alpha = scheduleInPopulation.clone();
                        schedules[i] = temp;
                        bestIter = iter;
                    } else if (scheduleInPopulation.fitness < beta.fitness) {
                        Schedule temp = delta.clone();
                        delta = beta.clone();
                        beta = scheduleInPopulation.clone();
                        schedules[i] = temp;
//                        bestIter = iter;
                    } else if (scheduleInPopulation.fitness < delta.fitness) {
                        Schedule temp = delta.clone();
                        delta = scheduleInPopulation.clone();
                        schedules[i] = temp;
//                        bestIter = iter;
                    }
                }
            }
        }


        Schedule bestSchedultBeforeChange=alpha.clone();
        bestSchedultBeforeChange.fitness();
        alpha.findBestSchedule();
        alpha.fitness();
        System.out.println("best iter:" + bestIter);
        System.out.println("best schedule fitness:" + bestSchedultBeforeChange.fitness);
        System.out.println("is accepted:" + bestSchedultBeforeChange.isAccepted());
        List<DateSchedule> dses = bestSchedultBeforeChange.getDateScheduleList();
        for (int i = 0; i < dses.size(); i++) {
            System.out.println(dses.get(i).toString());

        }

        System.out.println("best schedule fitness with change date schedule:" + alpha.fitness);
        System.out.println("is accepted:" + alpha.isAccepted());
        List<DateSchedule> dses1 = alpha.getDateScheduleList();
        for (int i = 0; i < dses1.size(); i++) {
            System.out.println(dses1.get(i).toString());

        }
//        for (int i = 0; i < dses.size(); i++) {
//            System.out.println("lt map " + i + ":");
//            System.out.println(dses.get(i).getLtClassMap().toString());
//            System.out.println("th map " + i + ":");
//            System.out.println(dses.get(i).getThClassMap().toString());
//
//        }
//        System.out.println("scheduleInPopulation after");
//        for (Map.Entry<Subject, Set<String>> entry : scheduleInPopulation.getSubjectMap().entrySet()) {
//            System.out.print(entry.getKey().getName() + ":");
//            Set<String> set = entry.getValue();
//            for (String ss : set) {
//                System.out.print(ss + "-");
//            }
//            System.out.println();
//        }
//        System.out.println(scheduleInPopulation.isAccepted());
        //        whileloop:
//        while (iter < N_ITER) {
//            iter++;
//            for (int i = 3; i < N_WOLF; i++) {
//
//            }
//        }

    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        List<String> dates = new ArrayList<>();
        dates.add("12/10/2022");
        dates.add("13/10/2022");
        dates.add("14/10/2022");
        dates.add("15/10/2022");
        dates.add("16/10/2022");
        dates.add("17/10/2022");
        dates.add("18/10/2022");
        GWO gwo = new GWO(dates);
        Schedule[] schedules = gwo.createPopulation();
//        for (Schedule s : schedules) {
//            System.out.println(s.fitness);
//        }
        gwo.gwo();
    }
}
