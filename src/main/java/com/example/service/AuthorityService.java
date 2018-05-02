package com.example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.dao.AuthorityRepository;
import com.example.entities.Authority;

@RestController
public class AuthorityService {
	@Autowired
	private AuthorityRepository authorityRepository;

	@RequestMapping(value = "/Authority", method = RequestMethod.POST)
	public Authority saveUserRole(@RequestBody Authority a) {
		return authorityRepository.save(a);
	}

	@RequestMapping(value = "/getUserRole", method = RequestMethod.GET)
	public List<Authority> listUserRole() {
		return authorityRepository.findAll();
	}

	public Page<Authority> findAllAuthority(@RequestParam(name = "num", defaultValue = "0") int num,
			@RequestParam(name = "size", defaultValue = "3") int size) {
		return authorityRepository.findAll(new PageRequest(num, size));
	}

}
