package com.schedule.initialization.utils;

import com.schedule.initialization.data.InitData;
import com.schedule.initialization.models.ClassRoom;
import com.schedule.initialization.models.Grade;
import com.schedule.initialization.models.RegistrationClass;
import com.schedule.initialization.models.Subject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelFile {

    static FileInputStream fis;

    static {
        try {
            fis = new FileInputStream("data/data.xlsx");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Workbook wb;

    static {
        try {
            wb = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExcelFile() throws IOException {
    }

    public static List<String> getDates() {
        List<String> date = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(4);
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            date.add(row.getCell(0).getStringCellValue());
        }
        return date;
    }

    public static List<ClassRoom> getClassroomsTH() {
        List<ClassRoom> classRooms = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(1);
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            classRooms.add(new ClassRoom((int) row.getCell(0).getNumericCellValue() + ""
                    , row.getCell(1).getStringCellValue()
                    , (int) row.getCell(2).getNumericCellValue()
                    , (int) row.getCell(3).getNumericCellValue()
                    , (int) row.getCell(4).getNumericCellValue()
                    , (int) row.getCell(5).getNumericCellValue()));
        }
        return classRooms;
    }


    public static List<ClassRoom> getClassroomsLT() {
        List<ClassRoom> classRooms = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            classRooms.add(new ClassRoom((int) row.getCell(0).getNumericCellValue() + ""
                    , row.getCell(1).getStringCellValue()
                    , (int) row.getCell(2).getNumericCellValue()
                    , (int) row.getCell(3).getNumericCellValue()
                    , (int) row.getCell(4).getNumericCellValue()
                    , (int) row.getCell(5).getNumericCellValue()));
        }
        return classRooms;
    }

    public static List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(3);
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            subjects.add(new Subject((int) row.getCell(0).getNumericCellValue() + "",
                    row.getCell(1).getStringCellValue(),
                    (int) row.getCell(2).getNumericCellValue(),
                    (int) row.getCell(3).getNumericCellValue(),
                    (int) row.getCell(4).getNumericCellValue(),
                    (int) row.getCell(5).getNumericCellValue()));
        }
        return subjects;
    }

    public static List<RegistrationClass> getRegistrationClass(List<Subject> subjectList) {
        List<RegistrationClass> registrationClasses = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(2);
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            Subject subject = null;

            for (Subject s : subjectList) {
                String ssName = row.getCell(0).getStringCellValue();
                if (s.getId().equals(ssName.substring(0, ssName.lastIndexOf("-")))) {
                    subject = new Subject(s.getId(), s.getName(), s.getCredit(), s.getExamForms(), s.getExamTime(), s.getLessonTime());
                    break;
                }
            }
            if (subject != null) {
                registrationClasses.add(new RegistrationClass(row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        (int) row.getCell(2).getNumericCellValue(),
                        (int) row.getCell(3).getNumericCellValue(),
                        subject,
                        new Grade(row.getCell(4).getStringCellValue(), (int) row.getCell(6).getNumericCellValue() + ""),
                        (int) row.getCell(5).getNumericCellValue()));
            }

        }
        List<Subject> newSubject = new ArrayList<>();
        for (Subject subject : subjectList) {
            if (subject.getExamForms() == 1) {
                List<RegistrationClass> currentList =
                        registrationClasses.stream().filter(item -> item.getSubject().getId().equals(subject.getId()))
                                .collect(Collectors.toList());
                int sum = currentList.stream().map(RegistrationClass::getEstimatedClassSizeReal).reduce(0, Integer::sum);
                int ratio = (int) Math.ceil((double) sum / (InitData.classRoomsTH.stream().map(ClassRoom::getCapacityExam).reduce(0, Integer::sum)*4));
                if (ratio > 1) {
                    int offset = Math.round(currentList.size() / ratio);
                    registrationClasses.removeAll(currentList);
                    for (int i = 1; i < ratio; i++) {
                        Subject subjectNext = subject.clone();
                        newSubject.add(subjectNext);
                        subjectNext.setId(subjectNext.getId()+"-" + (i + 1));
                        if (i == ratio - 1) {
                            for (int j = offset * i; j < currentList.size(); j++) {
                                currentList.get(j).setSubject(subjectNext);
                            }
                        } else {
                            for (int j = offset * i; j < offset * (i + 1); j++) {
                                currentList.get(j).setSubject(subjectNext);
                            }
                        }
                    }
                    registrationClasses.addAll(currentList);
                }
            }
        }
        InitData.subjects.addAll(newSubject);
        return registrationClasses;
    }

    public static void setWb(Workbook workbook) {
        wb = workbook;
    }

    public static void main(String[] args) {
//        List<String> dates=ExcelFile.getDates();
//        dates.forEach(System.out::println);
//            List<Subject> subjectList=ExcelFile.getSubjects();
//        List<RegistrationClass> classRooms=ExcelFile.getRegistrationClass(subjectList);
//        classRooms.forEach(System.out::println);
        System.out.println(Math.ceil(((double) 3 / 2)));
    }
}
