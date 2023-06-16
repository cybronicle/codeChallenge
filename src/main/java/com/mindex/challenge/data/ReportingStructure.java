package com.mindex.challenge.data;

/**
 * Class used to showcase an employee and provide quick access to their number of reports
 */
public class ReportingStructure {

    public ReportingStructure(String employee, int numberOfReports){
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }

    private String employee;
    private int numberOfReports;

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
