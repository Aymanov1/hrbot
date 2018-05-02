package com.example.service;

import java.util.List;
import com.example.dao.CompanyRepository;
import com.example.entities.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyService {
	@Autowired
	private CompanyRepository companyRepository;

	@RequestMapping(value = "/Company", method = RequestMethod.POST)
	public Company saveCompany(@RequestBody Company c) {
		return companyRepository.save(c);
	}

	@RequestMapping(value = "/Company", method = RequestMethod.GET)
	public List<Company> listCompany() {
		return companyRepository.findAll();
	}

}
