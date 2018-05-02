package com.example.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idCompany;
	private String nameCompany;
	private String detail;
	private String adress;

	// @ManyToMany
	// @JoinTable(name = "company_user")
	// private Collection<UserInformation> users;

	public Company() {
		super();
	}

	public Company(long idCompany, String nameCompany, String detail) {
		super();
		this.idCompany = idCompany;
		this.nameCompany = nameCompany;
		this.detail = detail;
	}

	public long getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(long idCompany) {
		this.idCompany = idCompany;
	}

	public String getNameCompany() {
		return nameCompany;
	}

	public void setNameCompany(String nameCompany) {
		this.nameCompany = nameCompany;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	// public Collection<UserInformation> getUsers() {
	// return users;
	// }
	//
	// public void setUsers(Collection<UserInformation> users) {
	// this.users = users;
	// }

}
