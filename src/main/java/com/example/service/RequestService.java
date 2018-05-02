package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.dao.RequestRepository;
import com.example.entities.Request;

@RestController
public class RequestService {
	@Autowired
	private RequestRepository requestRepository;

	@RequestMapping(value = "/Request", method = RequestMethod.POST)
	public Request saveRequest(@RequestBody Request r) {
		return requestRepository.save(r);
	}

	@RequestMapping(value = "/getRequest", method = RequestMethod.GET)
	public List<Request> listRequest() {
		return requestRepository.findAll();
	}

}
