package com.example.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
public class UserToUserRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private UserToUserRequestPK userToUserRequestPK;
	private Request request;
	private UserInformation userFrom;
	private UserInformation userTo;

	public UserToUserRequest() {
		super();
	}

	@EmbeddedId
	public UserToUserRequestPK getUserToUserRequestPK() {
		return userToUserRequestPK;
	}

	public void setUserToUserRequestPK(UserToUserRequestPK userToUserRequestPK) {
		this.userToUserRequestPK = userToUserRequestPK;
	}

	@ManyToOne
	@JoinColumn(name = "idRequest", referencedColumnName = "idRequest", insertable = false, updatable = false)
	@JsonIgnore
	public Request getRequest() {
		return request;
	}

	@JsonSetter
	public void setRequest(Request request) {
		this.request = request;
	}

	@ManyToOne
	@JoinColumn(name = "idUserFrom", referencedColumnName = "idUser", insertable = false, updatable = false)
	public UserInformation getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(UserInformation userFrom) {
		this.userFrom = userFrom;
	}

	@ManyToOne
	@JoinColumn(name = "idUserTo", referencedColumnName = "idUser", insertable = false, updatable = false)
	public UserInformation getUserTo() {
		return userTo;
	}

	public void setUserTo(UserInformation userTo) {
		this.userTo = userTo;
	}

}
