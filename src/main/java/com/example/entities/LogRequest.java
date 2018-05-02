package com.example.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LogRequest implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long idLogRequest;
	private String title;
	private String detail;
	private Timestamp createdAt;
	private long idRequest;
	private String statusRequest;
	private String fromUser;
	private String toUser;
	
	public LogRequest() {
		super();
	}

	public long getIdLogRequest() {
		return idLogRequest;
	}

	public void setIdLogRequest(long idLogRequest) {
		this.idLogRequest = idLogRequest;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public long getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(long idRequest) {
		this.idRequest = idRequest;
	}

	public String getStatusRequest() {
		return statusRequest;
	}

	public void setStatusRequest(String statusRequest) {
		this.statusRequest = statusRequest;
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
