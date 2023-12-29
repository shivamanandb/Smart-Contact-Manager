package com.smart.smartcontactmanager.dao;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query("from Contact as c where c.user.id =:userId")
    // two information in Pageable - Contact per page & current page
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

    // serach Contacts by the name of person
    // @Query("SELECT c FROM Contact c WHERE c.name LIKE %:name% AND c.user = :user")
    public List<Contact> findByNameContainingAndUser(String name, User user);
}
