package com.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;
import com.contact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	//method for finding contacts based on user
	@Query("from Contact as c where c.user.id=:u_id")
	public Page<Contact> findContactsByUserId(@Param("u_id") int u_id, Pageable pageable);
	
	@Query("select count(c) from Contact c where c.user.id = :u_id")
	public long countByUserId(@Param("u_id") int u_id);
	
	 @Query("select count(c) from Contact c where c.user.id = :u_id and c.relation = :relation")
	public long countByUserIDAndRelation(@Param("u_id") int u_id,  @Param("relation") String relation);
	 
	public List<Contact> findByNameContainingAndUser(String name, User user);
}
