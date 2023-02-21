package com.schedule.initialization.gwo;

import com.schedule.initialization.models.DateSchedule;
import com.schedule.initialization.models.Schedule;
import com.schedule.initialization.models.Subject;

import java.io.IOException;
import java.util.*;

public class GWO {
    public static final int N_WOLF = 100;
    public static final int N_ITER = 500;
    public List<String> dates;
    public Schedule finalSchedule;

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
//        System.out.println("alist:"+aList.size());
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
//        System.out.println(sequence.size());
        Random rd = new Random();
        double c = rd.nextDouble();
        if (c > 0.5)
            c -= 0.5;
        int count = (int) (c * sequence.size());
        List<Map.Entry<Subject, Set<String>>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int index = rd.nextInt(sequence.size());
            result.add(sequence.get(index));
            sequence.remove(index);
        }
        Collections.shuffle(result);
        return result;
    }

    public void gwo() throws IOException, CloneNotSupportedException, InterruptedException {
        long begin_create_population = System.currentTimeMillis();
        Schedule[] schedules = createPopulation();
        long end_create_population = System.currentTimeMillis();
        System.out.println("time create population:" + ((end_create_population - begin_create_population) / 1));
        Arrays.sort(schedules);
        Schedule alpha = schedules[0];
        Schedule beta = schedules[1];
        Schedule delta = schedules[2];

        double beginAlphaFitness = alpha.fitness;
        System.out.println("beginAlphaFitness:" + beginAlphaFitness);
//        alpha.getDateScheduleList().forEach(item->{
//            System.out.println(item);
//        });
//
//        double beginBetaFitness=beta.fitness;
//        System.out.println("beginBetaFitness:"+beginBetaFitness);
//        beta.getDateScheduleList().forEach(item->{
//            System.out.println(item);
//        });
//
//        double beginDeltaFitness=delta.fitness;
//        System.out.println("beginDeltaFitness:"+beginDeltaFitness);
//        delta.getDateScheduleList().forEach(item->{
//            System.out.println(item);
//        });

        int iter = 0;
        int bestIter = 0;
        Random random = new Random();
        long begin = System.currentTimeMillis();
        whileloop:
        while (iter < N_ITER) {
//            System.out.println("iter:" + iter + "======");
            long begin_iter = System.currentTimeMillis();


            for (int i = 3; i < N_WOLF; i++) {
                Schedule scheduleInPopulation = (Schedule) schedules[i].clone();
//                System.out.println("is accepted at begining:"+scheduleInPopulation.isAccepted());
//                System.out.println("before swap:" + scheduleInPopulation.fitness);
                List<Map.Entry<Subject, Set<String>>> swapList = new ArrayList<>();
                swapList.addAll(swapSquence(alpha, scheduleInPopulation));
                swapList.addAll(swapSquence(beta, scheduleInPopulation));
                swapList.addAll(swapSquence(delta, scheduleInPopulation));
//                System.out.println("swap amount:"+swapList.size());
                Schedule bestChange = (Schedule) scheduleInPopulation.clone();
//                System.out.println("is bestchange accepted at begining:"+bestChange.isAccepted());
                for (Map.Entry<Subject, Set<String>> entry : swapList) {
                    long begin_change_schedule = System.currentTimeMillis();
//                    scheduleInPopulation=schedules[i].clone();
                    scheduleInPopulation.changeSchedule(entry);
                    long end_change_schedule = System.currentTimeMillis();
//                    System.out.println("time change schedule:"+(end_change_schedule-begin_change_schedule));
                    scheduleInPopulation.fitness();
                    if (scheduleInPopulation.isAccepted()) {
//                        System.out.println("is accepted :");
                        schedules[i]= (Schedule) scheduleInPopulation.clone();
//                        System.out.println("is schedule acepted after accepted :"+schedules[i].isAccepted());
                        if ((scheduleInPopulation.fitness < bestChange.fitness)) {
                            bestChange = (Schedule) scheduleInPopulation.clone();
                            bestChange.fitness();

                        }
                    }
//                    System.out.println("best change after swap:"+bestChange.isAccepted());
                }
//                System.out.println("schedule i is accepted after swap:"+schedules[i].isAccepted());
//                System.out.println("bestchange is accepted after swap:"+bestChange.isAccepted());
//                System.out.println("is point in the same object:"+(bestChange==scheduleInPopulation));
                scheduleInPopulation = bestChange.clone();
                scheduleInPopulation.fitness();
                schedules[i]=scheduleInPopulation;
//                System.out.println("after swap:" + scheduleInPopulation.fitness);
//                System.out.println("is right swap:" + schedules[i].fitness);
//                System.out.println("is accepted:" + bestChange.isAccepted());
//                System.out.println("wolf:" + i + "============");
//                TimeUnit.MILLISECONDS.sleep(500);
                if (scheduleInPopulation.isAccepted()) {
                    if (scheduleInPopulation.fitness < alpha.fitness) {
                        Schedule temp = delta.clone();
                        delta = beta.clone();
                        beta = alpha.clone();
                        alpha = scheduleInPopulation.clone();
                        schedules[i] = temp;
                        bestIter = iter;
                        System.out.println("change at iter:" + (bestIter) + " with fitness:" + alpha.fitness);
//                        alpha.getDateScheduleList().forEach(item->{
//                            System.out.println(item);
//                        });


                    } else if (scheduleInPopulation.fitness < beta.fitness) {
                        Schedule temp = delta.clone();
                        delta = beta.clone();
                        beta = scheduleInPopulation.clone();
                        schedules[i] = temp;
                        System.out.println("change at iter:" + (iter) + " with beta fitness:" + beta.fitness);
//                        beta.getDateScheduleList().forEach(item->{
//                            System.out.println(item);
//                        });
                    } else if (scheduleInPopulation.fitness < delta.fitness) {
                        Schedule temp = delta.clone();
                        delta = scheduleInPopulation.clone();
                        schedules[i] = temp;
                        System.out.println("change at iter:" + (iter) + " with delta fitness:" + delta.fitness);
//                        delta.getDateScheduleList().forEach(item->{
//                            System.out.println(item);
//                        });
                    }
                }
            }
            long end_iter = System.currentTimeMillis();
//            System.out.println("time of iter " + iter + ":" + ((end_iter - begin_iter) / 1));
            iter++;
        }

        Schedule bestSchedultBeforeChange = alpha.clone();
        bestSchedultBeforeChange.fitness();


        System.out.println("begin alpha fitness:" + beginAlphaFitness);
        System.out.println("best iter:" + bestIter);
        System.out.println("best schedule fitness:" + bestSchedultBeforeChange.fitness);
        System.out.println("is accepted:" + bestSchedultBeforeChange.isAccepted());
        this.finalSchedule = bestSchedultBeforeChange.clone();
        finalSchedule.fitness();
//        List<DateSchedule> dses = bestSchedultBeforeChange.getDateScheduleList();
//        for (int i = 0; i < dses.size(); i++) {
//            System.out.println(dses.get(i).toString());
//
//        }

//        System.out.println("best schedule fitness with change date schedule:" + bestSche.fitness);
//        System.out.println("is accepted:" + bestSche.isAccepted());
//        List<DateSchedule> dses1 = bestSche.getDateScheduleList();
//        for (int i = 0; i < dses1.size(); i++) {
//            System.out.println(dses1.get(i).toString());
//
//        }
//        System.out.println("time to get schedule:"+(end_find_schedule-begin));
//        System.out.println("time to get best schedule:"+(end_best_schedule-end_find_schedule));
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

    public static void main(String[] args) throws IOException, CloneNotSupportedException, InterruptedException {
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
        GWO gwo = new GWO(dates);
        long beginTime = 0;
        long endTime = 0;

//        Schedule[] schedules = com.schedule.initialization.gwo.createPopulation();
        beginTime = System.currentTimeMillis();

        gwo.gwo();
        Schedule bestSchedule = gwo.finalSchedule;
        endTime = System.currentTimeMillis();
        System.out.println("iter " + 0 + ":" + (endTime - beginTime) / 60000);
        int at = 0;


        for (int i = 1; i < 10; i++) {
            System.out.println("==========begin " + i + " ==============");
            beginTime = System.currentTimeMillis();
            System.out.println("schedule " + i + ":");
            gwo.gwo();
            if (gwo.finalSchedule.fitness < bestSchedule.fitness) {
                bestSchedule = gwo.finalSchedule.clone();
                bestSchedule.fitness();
                at = i;
            }
            endTime = System.currentTimeMillis();
            System.out.println("iter " + i + ":" + (endTime - beginTime) / 60000);
            System.out.println("==========end==============");
        }
        System.out.println("best schefule at:" + at);
        List<DateSchedule> dses = bestSchedule.getDateScheduleList();
        for (int i = 0; i < dses.size(); i++) {
            System.out.println(dses.get(i).toString());
        }
    }
}
