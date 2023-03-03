package com.schedule.initialization.data;

import com.schedule.initialization.models.ClassRoom;
import com.schedule.initialization.models.RegistrationClass;
import com.schedule.initialization.models.Subject;
import com.schedule.initialization.utils.ExcelFile;

import java.util.List;

/**
 * @author ThanhLoc
 * @created 2/26/2023
 */
public class InitData {

    public static List<ClassRoom> classRoomsLT;
    public static List<ClassRoom> classRoomsTH;
    public static List<RegistrationClass> registrationClasses;
    public static List<Subject> subjects;
    public static List<String> examDates;

    static {
        // init data
        classRoomsLT = ExcelFile.getClassroomsLT();
        classRoomsTH = ExcelFile.getClassroomsTH();
        subjects = ExcelFile.getSubjects();
        registrationClasses = ExcelFile.getRegistrationClass(subjects);
        examDates = ExcelFile.getDates();
    }


    public static void initData(){
        classRoomsLT = ExcelFile.getClassroomsLT();
        classRoomsTH = ExcelFile.getClassroomsTH();
        subjects = ExcelFile.getSubjects();
        registrationClasses = ExcelFile.getRegistrationClass(InitData.subjects);
        examDates = ExcelFile.getDates();
    }

}
