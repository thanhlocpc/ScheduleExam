package ga;

import SA.SA;
import models.DateSchedule;
import models.Schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author ThanhLoc
 * @created 2/1/2023
 */
public class GA {
    public List<String> dates;
    private List<Schedule> population;
    public static final int POP_SIZE = 200;// Population size
    public static final double MUTATION_RATE = 0.03;
    public static final int MAX_ITERATIONS = 4000;
    Random rd = new Random();

    public GA(List<String> dates) throws IOException {
        this.dates = dates;
        population = new ArrayList<>(POP_SIZE);
        for (int i = 0; i < POP_SIZE; i++) {
            population.add(createRandomSchedule());
        }
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

    public Schedule execute() throws CloneNotSupportedException{

        List<Schedule> newPopulation = null;
        int count = 0;
        while(count < MAX_ITERATIONS) {
            newPopulation = new ArrayList<Schedule>();
            for (int i = 0; i < POP_SIZE; i++) {
                Schedule x = getParentByRandomSelection();
                Schedule y = getParentByRandomSelection();

                Schedule child = preproduce(x, y);
                if(Math.random() > MUTATION_RATE) mutate(child);
//                child.fitness();
//                if(child.fitness < 5000) {
//                    return child;
//                }

                newPopulation.add(child);
            }
            population = newPopulation;
            count++;
        }
        Collections.sort(newPopulation);
        return newPopulation.get(0);
    }

    // gây đột biến
    private void mutate(Schedule child) {
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

    // lai 2 cái thế
    public Schedule preproduce(Schedule x, Schedule y) {

        return null;
    }

    public Schedule getParentByRandomSelection() {
        return population.get(rd.nextInt(POP_SIZE));
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
            GA sa = new GA(dates);
            Schedule result = sa.execute();
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
