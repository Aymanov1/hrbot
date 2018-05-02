package com.example.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
public class UserInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String idUser;
	private String accountName;
	private String status;
	private String email;
	private boolean emailVerified;
	private String password;
	private Timestamp createdAt;
	private Timestamp lastLogin;
	private String systemLanguage = "English";
	private boolean initialSetting = true;

	@ManyToOne
	@JoinColumn(name = "authority")
	@JsonIgnoreProperties({ "role", "ranking" })
	private Authority authority;

	@OneToMany(mappedBy = "userLine")
	private Collection<LineProgress> lineProgresses;

	@OneToMany(mappedBy = "userFrom")
	private Collection<UserToUserRequest> fromUsers;

	@OneToMany(mappedBy = "userTo")
	private Collection<UserToUserRequest> toUsers;

	// @ManyToMany(mappedBy = "users")
	// private Collection<Company> companies;

	public UserInformation() {
		super();
	}

	public UserInformation(String idUser, String accountName, Authority authority) {
		super();
		this.idUser = idUser;
		this.accountName = accountName;
		this.authority = authority;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getSystemLanguage() {
		return systemLanguage;
	}

	public void setSystemLanguage(String systemLanguage) {
		this.systemLanguage = systemLanguage;
	}

	public boolean isInitialSetting() {
		return initialSetting;
	}

	public void setInitialSetting(boolean initialSetting) {
		this.initialSetting = initialSetting;
	}

	public Authority getUserRole() {
		return authority;
	}

	public void setUserRole(Authority authority) {
		this.authority = authority;
	}

	// public Collection<Company> getCompanies() {
	// return companies;
	// }
	//
	// public void setCompanies(Collection<Company> companies) {
	// this.companies = companies;
	// }

	@JsonIgnore
	public Collection<LineProgress> getLineProgresses() {
		return lineProgresses;
	}

	@JsonSetter
	public void setLineProgresses(Collection<LineProgress> lineProgresses) {
		this.lineProgresses = lineProgresses;
	}

	@JsonIgnore
	public Collection<UserToUserRequest> getFromUsers() {
		return fromUsers;
	}

	@JsonSetter
	public void setFromUsers(Collection<UserToUserRequest> fromUsers) {
		this.fromUsers = fromUsers;
	}

	@JsonIgnore
	public Collection<UserToUserRequest> getToUsers() {
		return toUsers;
	}

	@JsonSetter
	public void setToUsers(Collection<UserToUserRequest> toUsers) {
		this.toUsers = toUsers;
	}

}
