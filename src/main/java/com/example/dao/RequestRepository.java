package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entities.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

}
