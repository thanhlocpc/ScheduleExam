package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
 * lịch thi
 *
 */
public class Schedule implements Comparable<Schedule> {
    private List<DateSchedule> dateScheduleList;
    public double fitness;
    int remainSubject;
    List<Subject> subjectList;
    Map<Subject, Set<String>> subjectMap;
    List<SubjectSchedule> totalSubjectSchedule;

    public Schedule(List<String> dates) throws IOException {

        this.subjectList = getSubjectList();
        subjectMap = new HashMap<>();
        for (Subject s : subjectList) {
            subjectMap.put(s, new HashSet<>());
        }
        generateSchedule(dates);
        fitness();
    }

    public boolean isFinish() throws IOException {
        totalSubjectSchedule = new ArrayList<>();

        Map<String, Integer> check = new HashMap<>();
        for (Subject s : subjectList) {
            check.put(s.getId(), 0);
        }
        //list registration class
        List<RegistrationClass> classRooms = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("data/RegistrationClass"));
        String line = reader.readLine();

        while (line != null) {
            String[] tokens = line.split(",");
            Subject subject = null;
            for (Subject s : subjectList) {
                if (s.getId().equals(tokens[0].substring(0, tokens[0].lastIndexOf("-")))) {
                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms());
                    break;
                }
            }

            if (subject != null) {
                classRooms.add(new RegistrationClass(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), subject));
                int currentCap = check.get(subject.getId());
                check.put(subject.getId(), currentCap + Integer.parseInt(tokens[3]));
            }
            line = reader.readLine();


        }
//

        for (DateSchedule ds : dateScheduleList) {
            totalSubjectSchedule.addAll(ds.subjectSchedules);
        }
        for (SubjectSchedule ss : totalSubjectSchedule) {
            String id = ss.getSubject().getId();
            int numberOfStudent = ss.getRoom().getCapacity();
            int oldNumber = check.get(id);
            check.put(id, oldNumber - numberOfStudent);
        }
        int result = 0;
        for (Map.Entry<String, Integer> entry : check.entrySet()) {
            result += entry.getValue();
            System.out.println(entry.getKey() + " left:" + entry.getValue());
        }
        return result == 0;
    }


    public Schedule() {

    }

    public void setDateScheduleList(List<DateSchedule> dateScheduleList) {
        this.dateScheduleList = new ArrayList<>(dateScheduleList);
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setRemainSubject(int remainSubject) {
        this.remainSubject = remainSubject;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public void setSubjectMap(Map<Subject, Set<String>> subjectMap) {
        this.subjectMap = subjectMap;
    }

    public Schedule clone() {
        Schedule cloneSchedule = new Schedule();
        cloneSchedule.setDateScheduleList(this.dateScheduleList);
        cloneSchedule.setRemainSubject(this.remainSubject);
        cloneSchedule.setSubjectList(this.subjectList);
        cloneSchedule.setSubjectMap(new HashMap<>(this.subjectMap));
        cloneSchedule.fitness();
        return cloneSchedule;
    }

    public DateSchedule getDateScheduleByDate(String date) {
        for (DateSchedule d : dateScheduleList) {
            if (d.getDate().equals(date))
                return d;
        }
        return null;
    }

    public void findBestSchedule() throws IOException {
        for (int i = 0; i < dateScheduleList.size(); i++) {
            DateSchedule best = findBestDateScheduleByDate(dateScheduleList.get(i).getDate());
            dateScheduleList.set(i, best);
        }
    }

    public DateSchedule findBestDateScheduleByDate(String date) throws IOException {
        DateSchedule dateSchedule = getDateScheduleByDate(date);
//        System.out.println("dateSchedule first:" + dateSchedule.fitness);
        List<Subject> subjectList = new ArrayList<>(dateSchedule.getSubjectList());
        List<SubjectSchedule> subjectSchedules = new ArrayList<>(dateSchedule.getSubjectSchedules());
        Set<Subject> preparedSubjectSet = new HashSet<>();
        for (SubjectSchedule s : subjectSchedules) {
            preparedSubjectSet.add(s.getSubject());
        }
        Map<Subject, Set<String>> subjectMapBest = new HashMap<>();
        for (Subject s : subjectList) {
            subjectMapBest.put(s, new HashSet<>());
        }
        System.out.println(dateSchedule.getSubjectSchedules());
        List<Subject> preparedSubjectList = new ArrayList<>(preparedSubjectSet);
        int N_WOLF = 50;
        final int N_ITER = 100;
        List<DateSchedule> population = new ArrayList<>();
        for (int i = 0; i < N_WOLF; i++) {
            DateSchedule ds = new DateSchedule(date, subjectList, subjectMapBest);
            ds.setPreparedSubject(new ArrayList<>(preparedSubjectList));
            ds.generateSchedule();
            ds.fitness();
            population.add(ds);
        }
        population = population.stream().sorted().collect(Collectors.toList());
        DateSchedule alpha = population.get(0);
        DateSchedule beta = population.get(1);
        DateSchedule delta = population.get(2);
//        System.out.println(alpha);
//        System.out.println(alpha.fitness);
//        System.out.println("====");
//        System.out.println(beta);
//        System.out.println(beta.fitness);
//        System.out.println("====");
//        System.out.println(delta);
//        System.out.println(delta.fitness);
//        System.out.println("====");
//        System.out.println(population.get(4));
//        System.out.println(population.get(4).fitness);
//        System.out.println("====");

        ArrayList<int[]> map;
        int iter = 0;
        int bestIter = 0;
        while (iter < N_ITER) {
            iter++;
            for (int i = 3; i < population.size(); i++) {
        map = new ArrayList<>();
        DateSchedule current = population.get(i);
//                System.out.println(current.toString());

        map.addAll(swapLtSequence(alpha, current));
        map.addAll(swapLtSequence(beta, current));
        map.addAll(swapLtSequence(delta, current));
        for (int l = 0; l < map.size(); l++) {
            System.out.println(map.get(l)[0] + "-" + map.get(l)[1]);
        }

//                System.out.println("population " + i + "-----------------");
        DateSchedule bestDs = bestSwap(map, current);
        bestDs.fitness();
        System.out.println(bestDs);
        System.out.println(bestDs.fitness);
        Map<String, SubjectSchedule> mm = current.getLtClassMap();
        List<Map.Entry<String, SubjectSchedule>> entryList = new ArrayList<>(mm.entrySet());
        for(int j=0;j<entryList.size();j++){
            System.out.println(j+"=="+entryList.get(j).getKey()+"=="+entryList.get(j).getValue());
        }
//                bestDs.countNumberOfNun();
                if (bestDs.fitness < alpha.fitness) {
                    DateSchedule temp = delta.clone();
                    delta = beta.clone();
                    beta = alpha.clone();
                    alpha = bestDs.clone();
                    population.set(i, temp);
                    bestIter = iter;
                } else if (bestDs.fitness < beta.fitness) {
                    DateSchedule temp = delta.clone();
                    delta = beta.clone();
                    beta = bestDs.clone();
                    population.set(i, temp);
//                        bestIter = iter;
                } else if (bestDs.fitness < delta.fitness) {
                    DateSchedule temp = delta.clone();
                    delta = bestDs.clone();
                    population.set(i, temp);
//                        bestIter = iter;
                }
            }
        }
        System.out.println("best iter:" + bestIter);
        System.out.println("best schedule fitness:" + alpha.fitness);
        System.out.println(alpha.toString());
        return alpha;
    }


    public DateSchedule bestSwap(ArrayList<int[]> map, DateSchedule dateSchedule) throws IOException {
        DateSchedule bestDs = dateSchedule.clone();
        DateSchedule newDs = dateSchedule.clone();
        double bestF = dateSchedule.fitness;
        for (int i = 0; i < map.size(); i++) {
            int[] swapIndex = map.get(i);
//            System.out.println("swap:" + i);
            newDs = newDs.swap(swapIndex);
            if (bestF > newDs.fitness) {
                bestDs = newDs.clone();
                bestF = newDs.fitness;
            }
        }
        return bestDs.clone();
    }
//    public List<Map.Entry<String, SubjectSchedule>> swapThSequence(DateSchedule a, DateSchedule b) {
//        Map<String, SubjectSchedule> thClassMapA = a.getThClassMap();
//        Map<String, SubjectSchedule> thClassMapB = b.getThClassMap();
//
//    }

    public List<int[]> swapLtSequence(DateSchedule a, DateSchedule b) {
        List<Map.Entry<String, SubjectSchedule>> aList = new ArrayList<>(a.getLtClassMap().entrySet());
        List<Map.Entry<String, SubjectSchedule>> bList = new ArrayList<>(b.getLtClassMap().entrySet());
        List<int[]> sequence = new ArrayList<>();
        for (int i = 0; i < aList.size() - 1; i++) {
            Map.Entry<String, SubjectSchedule> eA = aList.get(i);

            for (int j = i + 1; j < bList.size(); j++) {
                Map.Entry<String, SubjectSchedule> eB = bList.get(j);
                if (eA.getValue() != null && eB.getValue() != null && eA.getValue().equals(eB.getValue())) {
                    bList.remove(j);
                    bList.add(j, eA);
                    bList.remove(i);
                    bList.set(i, eB);
                    sequence.add(new int[]{i, j});
                }
            }
        }
        return sequence;
    }


    //    public void changeSchedule(List<Map.Entry<Subject, Set<String>>> subjectListChange) throws IOException {
//        List<Map.Entry<Subject, Set<String>>> subjectListChangeTmp = new ArrayList<>(subjectListChange);
//        for (Map.Entry<Subject, Set<String>> entry : subjectListChangeTmp) {
//            Set<String> dateChangeSet = entry.getValue();
//            Subject subjectChange = entry.getKey();
//            for (DateSchedule ds : dateScheduleList) {
//                if (ds.isContainSubject(subjectChange)) {
//                    ds.deleteSubject(subjectChange);
//                    break;
//                }
//            }
//            DateSchedule d = getDateScheduleByDate((String) dateChangeSet.toArray()[0]);
//            d.addNewSubject(subjectChange);
//            this.fitness();
//            System.out.println(subjectChange.getName()+" "+fitness);
//        }
//
//    }
    public void changeSchedule(Map.Entry<Subject, Set<String>> subjectChangeEntry) throws IOException {
        Set<String> dateChangeSet = subjectChangeEntry.getValue();
        Subject subjectChange = subjectChangeEntry.getKey();
        for (DateSchedule ds : dateScheduleList) {
            if (ds.isContainSubject(subjectChange)) {
//                System.out.println(ds.getDate());
                ds.deleteSubject(subjectChange);
//                break;
            }
        }
        if (dateChangeSet.toArray().length > 0) {
            DateSchedule d = getDateScheduleByDate((String) dateChangeSet.toArray()[0]);
            System.out.println("date to add:" + d.getDate());
            System.out.println("ss size before:" + d.subjectSchedules.size());
            d.addNewSubject(subjectChange);
            System.out.println("ss size after:" + d.subjectSchedules.size());
        }
        this.fitness();
        System.out.println(subjectChange.getName() + " " + fitness);
    }


    public void generateSchedule(List<String> dates) throws IOException {
        List<DateSchedule> dateScheduleList = new ArrayList<>();
        List<Subject> remainSubjectList = new ArrayList<>(this.subjectList);

        for (String d : dates) {
            DateSchedule ds = new DateSchedule(d, remainSubjectList, subjectMap);
            remainSubjectList = ds.generateInitialSubjectSchedule();
            dateScheduleList.add(ds);
        }
        this.remainSubject = remainSubjectList.size();
        this.dateScheduleList = dateScheduleList;
    }

    public List<Subject> getSubjectList() throws IOException {
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

    public boolean isAccepted() throws IOException {
        System.out.println("remain subject:" + remainSubject);
        return remainSubject == 0 && isFinish();
    }

    public void fitness() {
        double result = 0;

        //tính sự chênh lêch số tiết thi giữa các ngày thi
        double examAmountDiifPerDay = 0;
        for (int i = 0; i < dateScheduleList.size(); i++) {
            examAmountDiifPerDay += dateScheduleList.get(i).subjectSchedules.size();
        }
        examAmountDiifPerDay /= dateScheduleList.size();

        for (int i = 0; i < dateScheduleList.size(); i++) {
            result += Math.abs(dateScheduleList.get(i).subjectSchedules.size() - examAmountDiifPerDay);
            dateScheduleList.get(i).fitness();
            result += dateScheduleList.get(i).fitness;
        }
        //////////////////////////
//        System.out.println("remain subject:" + remainSubject);
//        result += remainSubject * 1000;

        this.fitness = result;
    }

    public List<DateSchedule> getDateScheduleList() {
        return dateScheduleList;
    }

    public Map<Subject, Set<String>> getSubjectMap() {
        return subjectMap;
    }

    public static void main(String[] args) throws IOException {

//        List<Subject> subjectList = s.getSubjectList();
        List<String> dates = new ArrayList<>();
        dates.add("12/10/2022");
        dates.add("13/10/2022");
        dates.add("14/10/2022");
        dates.add("15/10/2022");
        dates.add("16/10/2022");
        dates.add("17/10/2022");
        Schedule s = null;
        while (true) {
            s = new Schedule(dates);
            if (s.isAccepted()) break;
        }
//        for (String date : dates) {
//        DateSchedule ds = s.getDateScheduleByDate("13/10/2022");
//        System.out.println(ds);
//        System.out.println("====================");
//        DateSchedule bestDate = s.findBestDateScheduleByDate("13/10/2022");
//        System.out.println(bestDate);
//        System.out.println("------------------------------------------------------");
//
//        }
//        s.findBestDateScheduleByDate("13/10/2022");
//        s.generateSchedule(dates);
//        List<DateSchedule> dses = s.getDateScheduleList();
//        for (int i = 0; i < dses.size(); i++) {
//            System.out.println(dses.get(i).toString());
////            dses.get(i).deleteSubject(new Subject("214462","Ltw",4,2));
//            System.out.println("2"+dses.get(i).toString());
//            System.out.println("prepareSubject count: " + dses.get(i).preparedSubject.size());
//
//        }
//        s.fitness();
//        System.out.println("remainSubject count: " + s.remainSubject);
//        System.out.println("fitness: " + s.fitness);
//        Map<Subject, Set<String>> subjectMap = s.subjectMap;
//        Subject sub = new Subject("214462", "Ltw", 4, 2);
//        Set<String> set = subjectMap.get(sub);
        //System.out.println(set.size());
//        s.isFinish();
//        for (Map.Entry<Subject, Set<String>> entry : s.getSubjectMap().entrySet()) {
//            System.out.print(entry.getKey().getName() + ":");
//            Set<String> sets = entry.getValue();
//            for (String ss : sets) {
//                System.out.print(ss + "-");
//            }
//            System.out.println();
//        }
        List<DateSchedule> dses = s.getDateScheduleList();
        for (int i = 0; i < dses.size(); i++) {
            System.out.println(dses.get(i).toString());
        }
        System.out.println(s.isAccepted());
        s.fitness();
        System.out.println(s.fitness);
        System.out.println("===============================");
        s.findBestSchedule();
//        s.findBestDateScheduleByDate("13/10/2022");
//        s.findBestDateScheduleByDate("14/10/2022");
//        s.findBestDateScheduleByDate("15/10/2022");
//        s.findBestDateScheduleByDate("16/10/2022");
//        s.findBestDateScheduleByDate("17/10/2022");
//
        List<DateSchedule> dses2 = s.getDateScheduleList();
        for (int i = 0; i < dses2.size(); i++) {
            System.out.println(dses2.get(i).toString());
        }
        System.out.println(s.isAccepted());
        s.fitness();
        System.out.println(s.fitness);
        System.out.println("===============================");
    }


    @Override
    public int compareTo(Schedule o) {
        return Double.compare(this.fitness, o.fitness);
    }
}
