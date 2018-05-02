package com.example.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
public class Authority implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long authorityId;
	private String authority;
	private long ranking;

	@OneToMany(mappedBy = "authority")
	private Collection<UserInformation> users;

	public Authority() {
		super();
	}

	public Authority(long authorityId, String authority, long ranking) {
		super();
		this.authorityId = authorityId;
		this.authority = authority;
		this.ranking = ranking;
	}

	public long getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(long authorityId) {
		this.authorityId = authorityId;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public long getRanking() {
		return ranking;
	}

	public void setRanking(long ranking) {
		this.ranking = ranking;
	}

	@JsonIgnore
	public Collection<UserInformation> getUsers() {
		return users;
	}

	@JsonSetter
	public void setUsers(Collection<UserInformation> users) {
		this.users = users;
	}

}
