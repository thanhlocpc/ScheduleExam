package models;

/**
 * @author : Thành Lộc
 * @since : 10/7/2022, Fri
 **/

// LỚP ĐĂNG KÍ HỌC PHẦN
public class RegistrationClass {
    private String id;
    private String name;
    private int estimatedClassSize; // sĩ số lớp dự kiến
    private int estimatedClassSizeReal; // sĩ số lớp thực tế
    private int date; // ngày ... trong tuần
    private int beginLearning; // tiết bắt đầu 1 2 3...
    private int amountLearning; // số tiết học

    private ClassRoom classRoom; // lớp này học phòng này

}
