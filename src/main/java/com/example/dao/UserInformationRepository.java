package com.example.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.entities.UserInformation;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, String> {
	@Query("select c from UserInformation c where c.accountName like :x")
	public Page<UserInformation> findUserByName(@Param("x") String userName, Pageable pageable);

}
