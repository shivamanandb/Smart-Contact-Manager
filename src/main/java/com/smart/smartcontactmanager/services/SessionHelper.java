package com.smart.smartcontactmanager.services;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

    public void removeMessageFromSession() {
        try {

            System.out.println("Message Removing");

            HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes()))
                    .getRequest().getSession();

            session.removeAttribute("message");
            System.out.println("Message Removed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
