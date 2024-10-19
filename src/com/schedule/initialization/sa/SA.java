package com.schedule.initialization.sa;

import com.schedule.initialization.ga.GA;
import com.schedule.initialization.models.*;
import com.schedule.initialization.utils.ExcelFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SA {
    public List<String> dates;
    public Schedule finalSchedule;
    public List<Integer> scList;
    public SA(List<String> dates,List<Integer> scList) {
        this.dates = dates;
        this.scList=scList;
    }

    public Schedule createRandomSchedule() throws IOException {
        Schedule result = null;
        while (true) {
            result = new Schedule(dates,scList);
            if (result.isAccepted())
                break;
        }
        return result;
    }

    public Schedule sa() throws IOException, CloneNotSupportedException {
        int T = 3000;
        Schedule current = createRandomSchedule();
        Schedule next = null;
        while (current.fitness > 200) {
            if(T==0)
                break;
            next = createRandomSchedule();
            double delta = next.fitness - current.fitness;
            if (delta < 0) {
                current = next.clone();
            } else if (Math.exp(delta / (T * 1.0)) > Math.random()) {
                current = next.clone();
            }
            T--;
        }
        return current;
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        List<String> dates = ExcelFile.getDates();

        long beginTime = 0;
        long endTime = 0;
        List properties= Arrays.asList(10,10,10,10,10,10);
        beginTime = System.currentTimeMillis();
        Schedule bestSchedule= new SA(dates,properties).sa();
        endTime = System.currentTimeMillis();
        double bestFitness = bestSchedule.fitness;
        double average = bestSchedule.fitness;
        int runTime=30;
        double averageRuntime=(endTime-beginTime)/1000;

        for (int i = 0; i < runTime-1; i++) {
            System.out.println("==========begin " + i + " ==============");
            beginTime = System.currentTimeMillis();
//            System.out.println("schedule " + i + ":");
            SA sa = new SA(dates,properties);
            Schedule result = sa.sa();
            System.out.println(result.fitness);
//            System.out.println("is accepted:" + result.isAccepted());
//            result.getDateScheduleList().forEach(item -> {
//                System.out.println(item);
//            });
            average+=result.fitness;
            if(result.fitness<bestFitness){
                bestFitness=result.fitness;
                bestSchedule=result;
            }
            endTime = System.currentTimeMillis();
            averageRuntime+=(endTime-beginTime)/1000;

            System.out.println(i+","+((endTime-beginTime)/1000));
//            System.out.println("iter " + i + ":" + (endTime - beginTime) / 60000);
            System.out.println("==========end==============");
        }
        System.out.println("best schedule fitness:" + bestSchedule.fitness);
        System.out.println("average fitness after run "+ runTime+":" + average/runTime);
        System.out.println("average runtime after run "+ runTime+":" + averageRuntime/runTime);

        List<DateSchedule> dses1 = bestSchedule.getDateScheduleList();
        for (int i = 0; i < dses1.size(); i++) {
            System.out.println(dses1.get(i).toString());
        }
    }
}
