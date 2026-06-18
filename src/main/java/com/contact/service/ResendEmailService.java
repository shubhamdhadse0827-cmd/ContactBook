package com.contact.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ResendEmailService {
		
	@Value("${resend.api.key}")
	private String apiKey;
	
	
	public void sendRegistrationSuccessEmail(String toEmail) throws Exception {

	    String json = "{"
	            + "\"from\":\"Team ContactBook <onboarding@resend.dev>\","
	            + "\"to\":\"" + toEmail + "\","
	            + "\"subject\":\"Registration Successful - ContactBook\","
	            + "\"html\":\""
	            + "<h2>🎉 Registration Successful</h2>"
	            + "<p>Hello,</p>"
	            + "<p>Your account has been created successfully on <b>ContactBook</b>.</p>"
	            + "<p>You can now log in and start using our services.</p>"
	            + "<br>"
	            + "<p><b>Thank you for joining us!</b></p>"
	            + "<br>"
	            + "<p>Regards,<br>Team ContactBook</p>"
	            + "\"}"
	            ;

	    sendEmail(json);
	}
	
	public void sendOtpEmail(String toEmail, String otp) throws Exception {

	    String json = "{"
	            + "\"from\":\"Team ContactBook <onboarding@resend.dev>\","
	            + "\"to\":\"" + toEmail + "\","
	            + "\"subject\":\"OTP Verification - ContactBook\","
	            + "\"html\":\""
	            + "<h2>🔐 OTP Verification</h2>"
	            + "<p>Your OTP for verification is:</p>"
	            + "<h1 style='color:#2e86de;'>" + otp + "</h1>"
	            + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
	            + "<br>"
	            + "<p>Do not share this OTP with anyone.</p>"
	            + "<br>"
	            + "<p>Regards,<br>Team ContactBook</p>"
	            + "\"}"
	            ;

	    sendEmail(json);
	}
	
	   // ================= COMMON METHOD =================
	  private void sendEmail(String json) throws Exception {

	        OkHttpClient client = new OkHttpClient();

	        RequestBody body = RequestBody.create(
	                json,
	                MediaType.get("application/json")
	        );

	        Request request = new Request.Builder()
	                .url("https://api.resend.com/emails")
	                .addHeader("Authorization", "Bearer " + apiKey)
	                .addHeader("Content-Type", "application/json")
	                .post(body)
	                .build();

	        Response response = client.newCall(request).execute();

	        String res = response.body().string();

	        System.out.println("Resend Response: " + res);
	    }
	  
		/*
		 * @PostConstruct public void testKey() { System.out.println("API KEY = " +
		 * apiKey); }
		 */
}
