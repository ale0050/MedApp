package com.medmuncii.medapp.employee;

import com.medmuncii.medapp.company.Company;
import com.medmuncii.medapp.company.CompanyRepository;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    public EmployeeService(EmployeeRepository employeeRepository, CompanyRepository companyRepository) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getEmployeesByCompany(Long companyId) {
        return employeeRepository.findByCompanyId(companyId);
    }
    public void importEmployeesFromExcel(Long companyId, MultipartFile file) throws IOException {
             System.out.println("Incepe importul pentru compania ID: " + companyId);
             
             Company company = companyRepository.findById(companyId)
                     .orElseThrow(() -> new RuntimeException("Compania nu a fost găsită"));

             try (InputStream is = file.getInputStream();
                  Workbook workbook = new XSSFWorkbook(is)) {

                     Sheet sheet = workbook.getSheetAt(0);
                     System.out.println("Am gasit foaia: " + sheet.getSheetName() + " cu " + sheet.getLastRowNum() + " randuri");

                     for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                             Row row = sheet.getRow(i);
                             if (row == null) continue;

                             Employee employee = new Employee();
                             DataFormatter formatter = new DataFormatter();
                             
                             String firstname = formatter.formatCellValue(row.getCell(0));
                             String lastname = formatter.formatCellValue(row.getCell(1));
                             
                             if (firstname.isEmpty() && lastname.isEmpty()) continue;

                             employee.setFirstname(firstname);
                             employee.setLastname(lastname);
                             employee.setCnp(formatter.formatCellValue(row.getCell(2)));
                             employee.setPosition(formatter.formatCellValue(row.getCell(3)));
                             employee.setWorkplace(formatter.formatCellValue(row.getCell(4)));
                             employee.setEmail(formatter.formatCellValue(row.getCell(5)));
                             employee.setPhone(formatter.formatCellValue(row.getCell(6)));
                             
                             employee.setCompany(company);
                             employeeRepository.save(employee);
                             System.out.println("Salvat angajat: " + firstname + " " + lastname);
                         }
                 } catch (Exception e) {
                     System.err.println("Eroare la procesarea Excel-ului: " + e.getMessage());
                     e.printStackTrace();
                     throw e;
                 }
         }
}
