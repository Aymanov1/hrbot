package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entities.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
