package com.smart.smartcontactmanager.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    // search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {

        System.out.println(query);
        User user = this.userRepository.getUserByEmail(principal.getName());

        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);

        return ResponseEntity.ok(contacts);
    }
}
