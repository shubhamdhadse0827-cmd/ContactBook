package com.contact.controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.dao.UserRepository;
import com.contact.entities.User;
import com.contact.helper.Message;
import com.contact.service.EmailService;
import com.contact.service.ResendEmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotCtrl {
	
	@Autowired
	private UserRepository userRepository;
	
	/*
	 * @Autowired private EmailService emailService;
	 */
	
	@Autowired
	private ResendEmailService resendEmailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
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
	
	@RequestMapping("/forgot")
	public String forgotForm() {
		return "forgotForm";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,
	                      HttpSession session) {

	    User user = this.userRepository.getUserByUserName(email);

	    if(user == null) {

	        session.setAttribute("msg",
	                new Message(
	                        "No account found with this email address!",
	                        "danger"));

	        return "redirect:/forgot";
	    }

	    String otp = generateOTP();

	    session.setAttribute("otp", otp);
	    session.setAttribute("email", email);

	    try {

			/* emailService.sendOtpEmail(email, otp); */
	    	
	    	resendEmailService.sendOtpEmail(email, otp);
	        session.setAttribute("msg",
	                new Message(
	                        "OTP has been sent to your email!",
	                        "success"));

	    } catch (Exception e) {

	        e.printStackTrace();
	        return "redirect:/forgot";
	    }

	    return "verifyOTP";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String otp,
	                        HttpSession session) {

	    String sessionOtp =
	            (String) session.getAttribute("otp");

	    if(sessionOtp != null &&
	    	       sessionOtp.equalsIgnoreCase(otp)) {

	    	        return "resetPassword";
	    	    }

	    
	    session.setAttribute("msg",
	            new Message(
	                    "Invalid OTP, please try again!",
	                    "danger"));

	    return "redirect:/verify-otp";
	}
	
	
	@GetMapping("/verify-otp")
	public String verifyOtpPage() {
	    return "verifyOTP";
	}
	
	@PostMapping("/change-Password")
	public String changePassword(@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			HttpSession session) {
		
		 if(!newPassword.equals(confirmPassword))
		 {
			 session.setAttribute("msg", new Message
			  ("New and Confirm Password do not match!", "danger"));
		    
			 return "resetPassword";
		 }

		
		 String email= (String)session.getAttribute("email");
		 User user = this.userRepository.getUserByUserName(email);
		 user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		 this.userRepository.save(user);
		 session.setAttribute("msg", 
				 new Message("Password changed successfully!", "success"));
		 return "redirect:/login";
	}
	
	/*
	 * @GetMapping("/forgot/remove-message")
	 * 
	 * @ResponseBody public void removeMessage(HttpSession session) {
	 * session.removeAttribute("msg"); }
	 */
}
