package com.mindex.challenge.data;

import java.time.LocalDate;

public class Compensation {
    private String employee;
    private String salary;
    private LocalDate effectiveDate;

    public Compensation(String employee, String salary) {
        this.employee = employee;
        this.salary = salary;
        this.setEffectiveDate(LocalDate.now());
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }
}
