package com.contact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contact.dao.ContactRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.Contact;
import com.contact.entities.User;

@RestController
public class SearchCtrl {
	
	@Autowired
	public UserRepository userRepository;

	@Autowired
	public ContactRepository contactRepository;
	
    @GetMapping("/searchContacts/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal p){
		
		System.out.println(query);
		
		String username = p.getName();
		User user = this.userRepository.getUserByUserName(username);
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
    
      	
	
}
