package ga;

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
        for (int i = 0; i < population.size(); i++) {
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
                child.fitness();
                if(child.fitness < 5000) {
                    return child;
                }

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
}
