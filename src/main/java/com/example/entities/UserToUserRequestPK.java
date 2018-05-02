package com.example.entities;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class UserToUserRequestPK implements Serializable {

	private static final long serialVersionUID = 1L;

	private String idUserFrom;
	private String idUserTo;
	private long idRequest;

	
	public UserToUserRequestPK() {
		super();
	}

	public UserToUserRequestPK(String idUserFrom, String idUserTo, long idRequest) {
		super();
		this.idUserFrom = idUserFrom;
		this.idUserTo = idUserTo;
		this.idRequest = idRequest;
	}

	public String getIdUserFrom() {
		return idUserFrom;
	}

	public void setIdUserFrom(String idUserFrom) {
		this.idUserFrom = idUserFrom;
	}

	public String getIdUserTo() {
		return idUserTo;
	}

	public void setIdUserTo(String idUserTo) {
		this.idUserTo = idUserTo;
	}

	public long getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(long idRequest) {
		this.idRequest = idRequest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (idRequest ^ (idRequest >>> 32));
		result = prime * result + ((idUserFrom == null) ? 0 : idUserFrom.hashCode());
		result = prime * result + ((idUserTo == null) ? 0 : idUserTo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserToUserRequestPK other = (UserToUserRequestPK) obj;
		if (idRequest != other.idRequest)
			return false;
		if (idUserFrom == null) {
			if (other.idUserFrom != null)
				return false;
		} else if (!idUserFrom.equals(other.idUserFrom))
			return false;
		if (idUserTo == null) {
			if (other.idUserTo != null)
				return false;
		} else if (!idUserTo.equals(other.idUserTo))
			return false;
		return true;
	}

}