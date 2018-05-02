package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

}
