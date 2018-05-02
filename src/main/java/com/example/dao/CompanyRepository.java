package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
