package com.example.dao;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.entities.UserToUserRequest;

public interface UserToUserRequestRepository extends JpaRepository<UserToUserRequest, Serializable>{

	@Query("select c from UserToUserRequest c where c.userTo.idUser = :x and (c.request.status='pending' or c.request.status='passed')")
	public List<UserToUserRequest> findPendingRequestByToUser(@Param("x") String idUser);
	
	@Query("select c from UserToUserRequest c where c.userFrom.idUser =:x and (c.request.status='approved' or c.request.status='disapproved')")
	public List<UserToUserRequest> findMyRequests(@Param("x") String idUser);
	
	@Query("select c from UserToUserRequest c where c.request.idRequest =:x ")
	public UserToUserRequest findRequest(@Param("x") long idRequest);

}
