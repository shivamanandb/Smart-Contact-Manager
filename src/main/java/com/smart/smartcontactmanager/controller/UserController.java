package com.smart.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.*;
import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    private Order order;

    // mothod for adding common data to response
    @ModelAttribute // used to add data into all handlers
    public void addCommonData(Model model, Principal principal) {
        String username = principal.getName();
        System.out.println("Username : " + username);

        User user = userRepository.getUserByEmail(username);

        System.out.println("User :" + user);

        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        // get the user using username

        return "normal/user_dashboard";
    }

    // open add form handler

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    // processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile imageFile,
            Principal principal, HttpSession session) {
        // System.out.println("DATA: " + contact);

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByEmail(name);

            // processing and uploading file...
            if (imageFile.isEmpty()) {
                // If file is empty then try our message
                contact.setImage("contact.png");
            }

            else {
                // upload the file to folder and update the name to contact
                contact.setImage(imageFile.getOriginalFilename());
                System.out.println("Uploaded image: " + imageFile.getOriginalFilename());

                File saveFile = new ClassPathResource("static/images").getFile();

                Files.copy(imageFile.getInputStream(),
                        Paths.get(saveFile.getAbsolutePath() + File.separator
                                + imageFile.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is successfully uploaded");

            }

            user.getContacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);

            // success message...
            session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));
        }

        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

            // error message...
            session.setAttribute("message", new Message("Something went wrong !! Try again..", "danger"));

        }

        return "normal/add_contact_form";
    }

    // show contacts handler
    // per page = 5 [n]
    // current page = 0 [page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

        m.addAttribute("titile", "Show User Contacts");

        // contact ki list bhejni hai
        String username = principal.getName();
        User user = this.userRepository.getUserByEmail(username);

        Pageable pageable = PageRequest.of(page, 5);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    // Showing Particular contact
    @GetMapping("/contact/{cid}")
    public String showContactDetail(@PathVariable("cid") Integer cid, Model m, Principal principal) {

        Optional<Contact> contactDetails = this.contactRepository.findById(cid);

        Contact contact = contactDetails.get();

        String userName = principal.getName();

        User user = this.userRepository.getUserByEmail(userName);

        if (user.getId() == contact.getUser().getId()) {
            m.addAttribute("contact", contact);
            m.addAttribute("title", contact.getName());
        }
        return "normal/contact_detail";
    }

    // Deleting contacts
    @GetMapping("/delete/{cid}")
    @Transactional
    public String deleteContacts(@PathVariable Integer cid, HttpSession session, Principal principal) {

        Contact contact = this.contactRepository.findById(cid).get();

        User user = this.userRepository.getUserByEmail(principal.getName());

        user.getContacts().remove(contact);

        this.userRepository.save(user);

        session.setAttribute("message", new Message("Contact deleted successfully...", "success"));

        return "redirect:/user/show-contacts/0";
    }

    // Open update form from handler

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model m) {

        m.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);

        return "normal/update_contact";
    }

    // update contact handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile imageFile,
            Principal principal, HttpSession session) {
        Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();

        try {

            if (!imageFile.isEmpty()) {

                // delete old photo
                File deleteFile = new ClassPathResource("static/images").getFile();
                File file1 = new File(deleteFile, oldContactDetail.getImage());

                file1.delete();

                // upload new photo

                File saveFile = new ClassPathResource("static/images").getFile();

                Files.copy(imageFile.getInputStream(),
                        Paths.get(saveFile.getAbsolutePath() + File.separator
                                + imageFile.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(imageFile.getOriginalFilename());
            }

            else {
                contact.setImage(oldContactDetail.getImage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Error !! Something went Wrong... Try again", "danger"));

        }

        String username = principal.getName();
        User user = this.userRepository.getUserByEmail(username);
        contact.setUser(user);
        this.contactRepository.save(contact);

        session.setAttribute("message", new Message("Your Contact hes been Updated...", "success"));

        return "redirect:/user/contact/" + contact.getCid();
    }

    // your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model m) {

        m.addAttribute("title", "Profile Page");
        return "normal/profile";
    }

    @GetMapping("/settings")
    public String openSettings() {

        return "normal/settings";
    }

    // change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

        // System.out.println("PLD PASSWORD: "+ oldPassword);
        // System.out.println("New PASSWORD: "+ newPassword);

        User currentUser = this.userRepository.getUserByEmail(principal.getName());

        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {

            // change Password
            currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            System.out.println("HELLLOOOOOO");
            session.setAttribute("message", new Message("Your Passwrod has been changed Successfully !!", "success"));

        } else {

            // error
            session.setAttribute("message", new Message("Please enter correct old Password !!", "warning"));

        }

        return "redirect:/user/settings";
    }

    @GetMapping("/support")
    public String support() {

        return "normal/support";
    }

    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data) throws Exception {

        // System.out.println("Hey, payment option is working");
        // System.out.println(data);

        int amt = Integer.parseInt(data.get("amount").toString());

        var client = new RazorpayClient("rzp_test_6jqRVfvybOgQ7q", "uJlsiVrS7tIp0ZYLLcmyK1tC");

        JSONObject ob = new JSONObject();
        ob.put("amount", amt * 100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn123545");

        // System.out.println("helllllooooooo");

        Order order = client.Orders.create(ob);

        // System.out.println("ORDER :" + order);
        return order.toString();
    }
}
