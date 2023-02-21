package com.schedule.initialization.sa;

import com.schedule.initialization.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SA {
    public List<String> dates;
    public Schedule finalSchedule;

    public SA(List<String> dates) {
        this.dates = dates;
    }

    public Schedule createRandomSchedule() throws IOException {
        Schedule result = null;
        while (true) {
            result = new Schedule(dates);
            if (result.isAccepted())
                break;
        }
        return result;
    }

    public Schedule sa() throws IOException, CloneNotSupportedException {
        int T = 10000;
        Schedule current = createRandomSchedule();
        Schedule next = null;
        while (current.fitness > 5000) {
            next = createRandomSchedule();
            double delta = next.fitness - current.fitness;
            if (delta < 0) {
                current = next.clone();
            } else if (Math.exp(delta / (T * 1.0)) > Math.random()) {
                current = next.clone();
            }
            T--;
        }
        System.out.println("T:" + T);
        return current;
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
        for (int i = 0; i < 10; i++) {
            System.out.println("==========begin "+ i+" ==============");
            beginTime = System.currentTimeMillis();
            System.out.println("schedule " + i + ":");
            SA sa = new SA(dates);
            Schedule result = sa.sa();
            System.out.println(result.fitness);
            result.getDateScheduleList().forEach(item -> {
                System.out.println(item);
            });
            endTime = System.currentTimeMillis();
            System.out.println("iter "+i+":"+(endTime-beginTime)/60000);
            System.out.println("==========end==============");
        }
    }
}
