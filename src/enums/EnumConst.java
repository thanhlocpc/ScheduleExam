package enums;

/**
 * @author : Thành Lộc
 * @since : 11/29/2022, Tue
 **/
public class EnumConst {

    public enum ClassRoomTypeEnum {
        LAB("Phòng thực hành"),
        THEORY("Phòng lý thuyết");

        String description;

        ClassRoomTypeEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

}
