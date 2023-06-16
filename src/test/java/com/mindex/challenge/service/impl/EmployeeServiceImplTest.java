package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    private String compensationUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting-structure";
        compensationUrl = "http://localhost:" + port + "/employee/{id}/compensation";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testCreateReportingStructure(){
        Employee employee = createJohnLennonEmployee();
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl.replace("{id}", employee.getEmployeeId()), ReportingStructure.class).getBody();
        assertNotNull(reportingStructure);
        assertEmployeeEquivalence(employee, reportingStructure.getEmployee());
        assertEquals(4, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testCompensationCreateAndRead(){
        String johnLennonId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        URI compensationUri = URI.create(compensationUrl.replace("{id}", johnLennonId));
        Compensation compensation = new Compensation();
        compensation.setSalary("62000");
        compensation.setEmployee(createJohnLennonEmployee());
        compensation.setEffectiveDate(LocalDate.now());
        Compensation compensationResponse = restTemplate.postForEntity(compensationUri, "62000", Compensation.class).getBody();

        assertCompensationEquivalence(compensation, compensationResponse);

        assertCompensationEquivalence(compensationResponse, Objects.requireNonNull(restTemplate.getForEntity(compensationUri, Compensation.class).getBody()));
    }

    private Employee createJohnLennonEmployee(){
        Employee firstDirect = new Employee();
        firstDirect.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        Employee secondDirect = new Employee();
        secondDirect.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        String johnLennonId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        Employee employee = new Employee();
        employee.setEmployeeId(johnLennonId);
        employee.setPosition("Development Manager");
        employee.setDepartment("Engineering");
        employee.setFirstName("John");
        employee.setLastName("Lennon");
        employee.setDirectReports(List.of(firstDirect,secondDirect));
        return employee;
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual){
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getEmployee().getDepartment(), actual.getEmployee().getDepartment());
        assertEquals(expected.getEmployee().getDirectReports().size(), actual.getEmployee().getDirectReports().size());
        assertEquals(expected.getEmployee().getFirstName(), actual.getEmployee().getFirstName());
        assertEquals(expected.getEmployee().getLastName(), actual.getEmployee().getLastName());
        assertEquals(expected.getEmployee().getPosition(), actual.getEmployee().getPosition());
    }
}
