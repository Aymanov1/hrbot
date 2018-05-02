package com.example.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LogDecision implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long idDecision;
	private Timestamp createdAt;
	private String status;
	private long idRequest;
	private String fromUser;
	private String toUser;
	
	public LogDecision() {
		super();
	}

	public long getIdDecision() {
		return idDecision;
	}

	public void setIdDecision(long idDecision) {
		this.idDecision = idDecision;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(long idRequest) {
		this.idRequest = idRequest;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

}
