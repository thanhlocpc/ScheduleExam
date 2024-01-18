package com.schedule.initialization.dto;

import com.schedule.initialization.models.RegistrationClass;
import com.schedule.initialization.models.Subject;

import java.util.List;

public class TempGenerateInitSchedule {
    List<Subject> remainSubject;
    List<RegistrationClass> registrationClasses;

    public TempGenerateInitSchedule(List<Subject> remainSubject, List<RegistrationClass> registrationClasses) {
        this.remainSubject = remainSubject;
        this.registrationClasses = registrationClasses;
    }

    public List<Subject> getRemainSubject() {
        return remainSubject;
    }

    public List<RegistrationClass> getRegistrationClasses() {
        return registrationClasses;
    }

    public void setRemainSubject(List<Subject> remainSubject) {
        this.remainSubject = remainSubject;
    }

    public void setRegistrationClasses(List<RegistrationClass> registrationClasses) {
        this.registrationClasses = registrationClasses;
    }
}
