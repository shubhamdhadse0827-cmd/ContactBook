package com.contact.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int c_id;
	private String name;
	private String secondName;
	
	@Column(unique = true)
	private String email;
	
	@Column(length = 10)
	private String phone;
	private String relation;
	private String imageurl;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	@Column(length = 500)
	private String description;

	public Contact() {
		super();
	
	}

	public Contact(int c_id, String name, String secondName, String email, String phone, String relation,
			String imageurl, String description) {
		super();
		this.c_id = c_id;
		this.name = name;
		this.secondName = secondName;
		this.email = email;
		this.phone = phone;
		this.relation = relation;
		this.imageurl = imageurl;
		this.description = description;
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	@Override
//	public String toString() {
//		return "Contact [c_id=" + c_id + ", name=" + name + ", secondName=" + secondName + ", email=" + email
//				+ ", phone=" + phone + ", relation=" + relation + ", imageurl=" + imageurl + ", user=" + user
//				+ ", description=" + description + "]";
//	}
}
