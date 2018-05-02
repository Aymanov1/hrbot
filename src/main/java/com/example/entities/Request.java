package com.example.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Request implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idRequest;
	private String titleRequest;
	private boolean hidden = false;
	private String status = "pending";
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private String detailRequest;
	private long visibility;

	@OneToMany(mappedBy = "request")
	private Collection<Comments> comments;

	@OneToMany(mappedBy = "request")
	private Collection<UserToUserRequest> fromAndToUsers;

	public Request() {
		super();
	}

	public Request(long idRequest, boolean hidden, long visibility) {
		super();
		this.idRequest = idRequest;
		this.hidden = hidden;
		this.visibility = visibility;
	}

	public long getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(long idRequest) {
		this.idRequest = idRequest;
	}

	public String getTitleRequest() {
		return titleRequest;
	}

	public void setTitleRequest(String titleRequest) {
		this.titleRequest = titleRequest;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getDetailRequest() {
		return detailRequest;
	}

	public void setDetailRequest(String detailRequest) {
		this.detailRequest = detailRequest;
	}

	public long getVisibility() {
		return visibility;
	}

	public void setVisibility(long visibility) {
		this.visibility = visibility;
	}

	public Collection<Comments> getComments() {
		return comments;
	}

	public void setComments(Collection<Comments> comments) {
		this.comments = comments;
	}

	public Collection<UserToUserRequest> getFromAndToUsers() {
		return fromAndToUsers;
	}

	public void setFromAndToUsers(Collection<UserToUserRequest> fromAndToUsers) {
		this.fromAndToUsers = fromAndToUsers;
	}

}