package com.contact.controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.dao.ReviewRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.Review;
import com.contact.entities.User;
import com.contact.helper.Message;
import com.contact.service.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class DashboardCtrl {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	
	
	public String generateOTP(){
		SecureRandom random = new SecureRandom();
		List<Character> otp = new ArrayList<>();
	
		for(int i=0; i<3; i++) {
			otp.add((char) ('0' + random.nextInt(10)));
		}
		
		for(int i=0; i<3; i++) {
			otp.add((char) ('A' + random.nextInt(26)));
		}
		
		Collections.shuffle(otp);
		
		StringBuilder sb = new StringBuilder();
		for(char c : otp) {
			sb.append(c);
		}
		
		return sb.toString();	
	}

	@GetMapping("/")
	public String home(Model m) {

	    List<Review> reviews = this.reviewRepository.getReviewsByMinRating(4);

	    m.addAttribute("reviews", reviews);
	    m.addAttribute("reviewCount", reviews.size());

	    m.addAttribute("title", "ContactBook - Home");

	    return "home";
	}

	
	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "ContactBook - About");
		return "about";
	}
	
	@GetMapping("/contact")
	public String contact(Model m) {
		m.addAttribute("title", "ContactBook - Contact");
		return "contact";
	}
	
	@GetMapping("/login")
	public String login(Model m) {
		m.addAttribute("title", "ContactBook - Login");
		return "login";
	}
	
	@GetMapping("/register")
	public String register(Model m) {

	    m.addAttribute("title", "ContactBook - Register");
	    m.addAttribute("user", new User());

	    return "register";
	}
	
	// this handler for send otp for user registration
	@GetMapping("/register-send-otp")
	@ResponseBody
	public String sendRegisterOTP(@RequestParam("email") String email,
	        HttpSession session) {

	    User existUser = this.userRepository.getUserByUserName(email);

	    if(existUser != null) {

	        return "Email already registered!";
	    }
	    
	 // OTP Cooldown Check
	    Long otpTime = (Long) session.getAttribute("otpTime");

	    if(otpTime != null) {

	        long currentTime = System.currentTimeMillis();

	        long diff = currentTime - otpTime;

	        if(diff < 5 * 60 * 1000) {

	            long remainingSeconds =
	                    (5 * 60 * 1000 - diff) / 1000;

	            return "Please wait " + remainingSeconds
	                    + " seconds before requesting a new OTP.";
	        }
	    }

	    String otp = generateOTP();

	    session.setAttribute("registerOTP", otp);
	    session.setAttribute("otpTime",
	            System.currentTimeMillis());

	    try {

	        emailService.sendOtpEmail(email, otp);

	        return "OTP sent successfully!";

	    }
	    catch (Exception e) {

	        e.printStackTrace();

	        return "Unable to send OTP!";
	    }
	}
	
	// this handler for user registration
	@PostMapping("/register_done")
	public String userRegister(@Valid @ModelAttribute("user") User user, 
			BindingResult result1, 
			@RequestParam("otp") String otp,
			@RequestParam(value = "agree", defaultValue = "false") boolean agree,
			Model m, HttpSession session) {
		
		try {
			if(result1.hasErrors()) {
				System.out.println("Error"+result1.toString());
				m.addAttribute("user",user);
				return "register";
			}
			
			if(!agree) {
				System.out.println("Please accept the Terms & Conditions to continue.");
				throw new Exception("Please accept the Terms & Conditions to continue.");
			}
		
			// OTP Verification
			String sessionOtp = (String) session.getAttribute("registerOTP");

			if(sessionOtp == null ||
			   !sessionOtp.equalsIgnoreCase(otp)) {

			    session.setAttribute("msg",
			            new Message("Invalid OTP!", "danger"));

			    return "redirect:/register";
			}	
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageurl("Profile.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agree "+agree);
			System.out.println("User"+user);
			
			User result = this.userRepository.save(user);
			session.removeAttribute("registerOTP");
			session.removeAttribute("otpSent");
			session.removeAttribute("otpTime");
			System.out.println(result.getEmail());
			
			try {
			    emailService.sendEmail(result.getEmail());
			} catch (Exception e) {
			    e.printStackTrace();
			}
			
			m.addAttribute("user", new User());
			session.setAttribute("msg", new Message("Your account has been created successfully.", "alert-success"));
			
			return "redirect:/register";
			
		} catch (Exception e) {

		    e.printStackTrace();

		    m.addAttribute("user", user);

		    String msg = e.getMessage();

		    if(msg.contains("Duplicate entry")) {

		        msg = "Email already registered. Please use another email.";
		    }

		    session.setAttribute("msg",
		            new Message(msg, "alert-danger"));

		    return "redirect:/register";
		}
		
	}
	
	@GetMapping("/remove-message")
	@ResponseBody
	public void removeMessage(HttpSession session) {

	    session.removeAttribute("msg");
	}
}
