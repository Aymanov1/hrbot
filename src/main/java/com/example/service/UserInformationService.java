package com.example.service;

import com.example.dao.UserInformationRepository;
import com.example.entities.UserInformation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInformationService {
	@Autowired
	private UserInformationRepository userInformationRepository;

	@RequestMapping(value = "/UserInfo", method = RequestMethod.POST)
	public UserInformation saveUserInformation(@RequestBody UserInformation u) {
		String accountName = u.getAccountName();
		u.setAccountName(accountName.toLowerCase());
		return userInformationRepository.save(u);
	}

	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	public List<UserInformation> listUserInformation() {
		return userInformationRepository.findAll();
	}

	@RequestMapping(value = "/getUser", method = RequestMethod.GET)
	public Page<UserInformation> findUserByName(@RequestParam(name = "accountName") String accountName,
			@RequestParam(name = "numPage", defaultValue = "0") int numPage,
			@RequestParam(name = "size", defaultValue = "3") int size) {
		return userInformationRepository.findUserByName("%" + accountName + "%", new PageRequest(numPage, size));
	}

}
