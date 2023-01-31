package GA;

import SA.SA;
import models.Schedule;
import models.Subject;

import java.io.IOException;
import java.util.*;

public class GA {
    public static final int POP_SIZE = 100;
    public static final int N_ITER = 500;
    public List<String> dates;
    List<Schedule> schedules =new ArrayList<>();
    Random rd = new Random();
    public GA(List<String> dates) {
        this.dates = dates;
    }

    public List<Schedule> createPopulation() throws IOException {
         Schedule temp;
        for (int i = 0; i < POP_SIZE; i++) {
            while (true) {
                temp = new Schedule(dates);
                if (temp.isAccepted()){
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
            List<Schedule> newSchedule=new ArrayList<>();
            for(int i=0;i<POP_SIZE;i++){
                Schedule x=getParentByRandomSelection();
                Schedule y=getParentByRandomSelection();
                Schedule child=preproduce3(x,y);
//                if (rd.nextInt(100) / 100 <= 0.03)
//                    mutate(child);

                if (child.fitness <4500) {
                    System.out.println("iter: " + iter);
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
                return Double.compare(o1.fitness,o2.fitness);
            }
        });
        return schedules.get(0);
    }

    public Schedule getParentByRandomSelection() {
        // Enter your code here
        return schedules.get(rd.nextInt(POP_SIZE));
    }
    public Schedule preproduce3(Schedule x, Schedule y) throws CloneNotSupportedException, IOException {
        Schedule result=x.clone();
        Schedule changeSchedule=x.clone();
        List<Map.Entry<Subject, Set<String>>> sequence = new ArrayList<>();
        List<Map.Entry<Subject, Set<String>>> subjectMap1=new ArrayList<>(x.getSubjectMap().entrySet());
        List<Map.Entry<Subject, Set<String>>> subjectMap2=new ArrayList<>(y.getSubjectMap().entrySet());
        for(int i=0;i<subjectMap1.size();i++){
            if(rd.nextInt(10) < 5){
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

    public void mutate(Schedule node) {

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
            GA ga = new GA(dates);
            Schedule result = ga.ga();
            System.out.println(result.fitness);
            System.out.println("is accepted:"+result.isAccepted());
            result.getDateScheduleList().forEach(item -> {
                System.out.println(item);
            });
            endTime = System.currentTimeMillis();
            System.out.println("iter "+i+":"+(endTime-beginTime)/60000);
            System.out.println("==========end==============");
        }
    }
}
