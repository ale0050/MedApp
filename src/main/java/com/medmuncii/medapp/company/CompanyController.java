package com.medmuncii.medapp.company;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public List<Company> getAll() {
        return companyService.getAllCompanies();
    }

    @PostMapping
    public Company create(@RequestBody Company company) {
        return companyService.createCompany(company);
    }
}
