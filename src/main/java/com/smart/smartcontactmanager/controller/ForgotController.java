package com.smart.smartcontactmanager.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.services.MailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Random random = new Random(10000);

    @Autowired
    private MailService mailService;

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @GetMapping("/forgot")
    public String forgotPassword() {

        return "forgot_password_form";
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("email") String email, HttpSession session) {

        User validUser = this.userRepository.getUserByEmail(email);

        System.out.println("Email: " + email);

        // System.out.println("OTP:" + otp);

        if (validUser != null) {
            
            // int otp = random.nextInt(999999);
            int otp = getRandomNumber(100000, 999999);            

            String subject = "OTP from SCM";

            String message = "Here your OTP for forgot password is: " + otp;
            
            mailService.sendEmail(email, subject, message);
            
            session.setAttribute("otp", otp);
            session.setAttribute("userEmail", email);

            return "verify_otp";

        } else {

            session.setAttribute("message", new Message("Email ID is not Registered !!", "danger"));
            return "forgot_password_form";
        }
    }

    @PostMapping("/check-otp")
    public String checkOTP(@RequestParam("otp") int otp, HttpSession session) {

        Integer myOtp = (int)session.getAttribute("otp");

        System.out.println("My OTP is: "+myOtp);
        System.out.println("Your OTP is: " + otp);
        System.out.println("Email Id" + (String)session.getAttribute("userEmail"));

        if(myOtp == otp)
            return "change_password";
        else {
            session.setAttribute("message", new Message("OTP mismatch !!", "warning"));
            return "verify_otp";
        }
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, HttpSession session) {

        if(!newPassword.equals(confirmPassword)) {
            session.setAttribute("message", new Message("New Password and Confirm Password Mismatch !!", "warning"));
            return "change_password";
        }
        else {
            String email = (String) session.getAttribute("userEmail");

            User user = this.userRepository.getUserByEmail(email);

            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(user);     

            System.out.println("Password: "+newPassword);
        }
        return "password_change_successfully_page";
    }
}
