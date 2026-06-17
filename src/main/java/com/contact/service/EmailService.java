package com.contact.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender javaMailSender;
	
	public void sendEmail(String toEmail) throws Exception {
		
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, false);
 		helper.setTo(toEmail);
		helper.setSubject("Welcome to ContactBook");
		helper.setText("""
				Hello,

				Thank you for registering with us.

				Your account has been created successfully. You can now log in and start using our services.

				If you did not create this account, please contact our support team immediately.

				Regards,
				Team ContactBook
				""");
		
		helper.setFrom(new InternetAddress("shubhamdhadse328@gmail.com",
				"ContactBook")
				);
		
		  	javaMailSender.send(msg);   	

	        System.out.println("Email sent successfully");
	}
	
	public void sendOtpEmail(String toEmail, String otp) throws Exception {

	    

	        MimeMessage msg = javaMailSender.createMimeMessage();

	        MimeMessageHelper helper =
	                new MimeMessageHelper(msg, false);

	        helper.setTo(toEmail);
	        helper.setSubject("ContactBook Password Reset OTP");

	        helper.setText(
	                "Your OTP for password reset is: "
	                + otp +
	                "\n\nThis OTP is valid for 5 minutes."
	        );

	        helper.setFrom(
	                new InternetAddress(
	                        "shubhamdhadse328@gmail.com",
	                        "ContactBook"));

	        javaMailSender.send(msg);
	}
	
	
}
