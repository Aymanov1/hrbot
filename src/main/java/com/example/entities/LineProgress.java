package com.example.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class LineProgress implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long idProgress;

	@ManyToOne
	@JoinColumn(name = "User_Progress")
	@JsonIgnoreProperties({ "status", "email", "emailVerified", "password", "createdAt", "lastLogin",
			"systemLanguage", "initialSetting", "authority", "lineProgresses", "Requests" })
	private UserInformation userLine;

	private String statusLine = "Default";

	public LineProgress() {
		super();
	}

	public LineProgress(UserInformation userLine, String statusLine) {
		super();
		this.userLine = userLine;
		this.statusLine = statusLine;
	}

	public UserInformation getUserLine() {
		return userLine;
	}

	public void setUserLine(UserInformation userLine) {
		this.userLine = userLine;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	public long getIdProgress() {
		return idProgress;
	}

	public void setIdProgress(long idProgress) {
		this.idProgress = idProgress;
	}

}
