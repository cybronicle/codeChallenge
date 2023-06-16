package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeRepository employeeRepository;
    private CompensationRepository compensationRepository;

    @Autowired
    EmployeeServiceImpl(EmployeeRepository employeeRepository, CompensationRepository compensationRepository){
        this.employeeRepository = employeeRepository;
        this.compensationRepository = compensationRepository;
    }

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure createReportingStructure(String id) {
        Employee employee = this.read(id);
        return new ReportingStructure(employee, this.countNumberOfReports(employee, new AtomicInteger(0), List.of()));
    }

    @Override
    public Compensation create(String id, String salary) {
        //  creating compensation and saving into mongodb
        Compensation compensation = new Compensation();
        // grabbing employee
        compensation.setEmployee(employeeRepository.findByEmployeeId(id));
        compensation.setEffectiveDate(LocalDate.now());
        compensation.setSalary(salary);
        return compensationRepository.save(compensation);
    }

    @Override
    public Compensation readCompensation(String id) {
        return compensationRepository.findCompensationByEmployeeEmployeeId(id);
    }

    /**
     * @param employee -
     * @return - recursively counts number of reports for a given employee based on tree traversal
     */
    private int countNumberOfReports(Employee employee, AtomicInteger numberOfReports, List<Employee> employeeList) {
        // if direct reports list exists and is not empty
        if (null != employee.getDirectReports() && !employee.getDirectReports().isEmpty()){
//            iterrate through list of reports, incrementing and recursively calling for the depth of the next
            employee.getDirectReports()
                .forEach(directReport -> {
                    // checking for duplicate reports for examples of cross-team employees
                    numberOfReports.incrementAndGet();
                    countNumberOfReports(this.read(directReport.getEmployeeId()), numberOfReports,employeeList);
                });
        }
        return numberOfReports.get();
    }
}
