package com.smart.smartcontactmanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cid;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String secondName;

    @Column(length = 150)
    private String work;

    @Column(length = 50)
    private String email;

    @Column(length = 10)
    private String phone;

    private String image;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JsonIgnore
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCid() {
        return this.cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return this.work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object obj) {

        return this.cid == ((Contact) obj).getCid();
    }

    // @Override
    // public String toString() {
    // return "Contact [cid=" + cid + ", name=" + name + ", secondName=" +
    // secondName + ", work=" + work + ", email="
    // + email + ", phone=" + phone + ", image=" + image + ", description=" +
    // description + ", user=" + user
    // + "]";
    // }

}
