package com.schedule.initialization.enums;

/**
 * @author : Thành Lộc
 * @since : 11/29/2022, Tue
 **/
public class EnumConst {

    public enum ClassRoomTypeEnum {
        LAB_ROOM("Phòng thực hành"),
        THEORY_ROOM("Phòng lý thuyết");

        String description;

        ClassRoomTypeEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ExamFormsTypeEnum {
        LAB_EXAM("Thi thực hành"),
        THEORY_EXAM("Thi lý thuyết"),
        ORAL_EXAM("Thi vấn đáp");
        String description;

        ExamFormsTypeEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

}
