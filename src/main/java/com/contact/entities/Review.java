package com.contact.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity 
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long r_id;
	private int rating;
	
	@NotBlank(message = "Comment is required")	
	private String comment;
	private LocalDateTime createdAt;
	
	@ManyToOne
	@JoinColumn(name = "u_id")
	private User user;

	public Review() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Review(long r_id, int rating, String comment, LocalDateTime createdAt, User user) {
		super();
		this.r_id = r_id;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
		this.user = user;
	}

	public long getR_id() {
		return r_id;
	}

	public void setR_id(long r_id) {
		this.r_id = r_id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Review [r_id=" + r_id + ", rating=" + rating + ", comment=" + comment + ", createdAt=" + createdAt
				+ ", user=" + user + "]";
	}
}
