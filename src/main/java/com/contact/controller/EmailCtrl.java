package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.contact.service.EmailService;



@RestController
public class EmailCtrl {
	
	@Autowired
	private EmailService emailService;

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to Smart ContactBook System. This is my web api";
	}
	
	@GetMapping("/test-mail")
	@ResponseBody
	public String testMail() {

	    try {
	        emailService.sendEmail("dhadse141@gmail.com");
	        return "Mail sent successfully";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Mail sending failed";
	    }
	}
}
