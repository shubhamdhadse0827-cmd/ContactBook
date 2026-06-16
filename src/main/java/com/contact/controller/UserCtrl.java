package com.contact.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.contact.dao.ContactRepository;
import com.contact.dao.ReviewRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.Contact;
import com.contact.entities.Review;
import com.contact.entities.User;
import com.contact.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")

public class UserCtrl {

	private static final String UPLOAD_DIR = "uploads/images/";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Value("${app.default.contact.image}")
	private String defaultContactImage;

	@Autowired
	private Cloudinary cloudinary;

	// Method for adding user data

	@ModelAttribute
	public void userData(Model m, Principal p) {

		String username = p.getName();
		System.out.println("Username is : " + username);

		// get user by username(email)
		User user = this.userRepository.getUserByUserName(username);
		System.out.println("User : " + user);

		String[] names = user.getName().trim().split("\\s+");
		String initials = "";

		if (names.length >= 2) {
			initials = ("" + names[0].charAt(0) + names[1].charAt(0)).toUpperCase();
		} else {
			initials = ("" + names[0].charAt(0)).toUpperCase();
		}

		m.addAttribute("user", user);
		m.addAttribute("initials", initials);
	}

	// user dashboard

	@RequestMapping("/index")
	public String userDashboard(Model m, Principal p) {
		String username = p.getName();

		User user = this.userRepository.getUserByUserName(username);
		int u_id = user.getU_id();

		// total users
		long countByUserId = this.contactRepository.countByUserId(u_id);

		long familyContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Family");
		long friendContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Friend");
		long relativeContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Relative");
		long colleagueContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Colleague");
		long clientContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Client");
		long teacherContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Teacher");
		long otherContacts = this.contactRepository.countByUserIDAndRelation(u_id, "Other");
		System.out.println("TotalContact : " + countByUserId);

		m.addAttribute("totalContacts", countByUserId);
		m.addAttribute("familyContacts", familyContacts);
		m.addAttribute("friendContacts", friendContacts);
		m.addAttribute("relativeContacts", relativeContacts);
		m.addAttribute("colleagueContacts", colleagueContacts);
		m.addAttribute("clientContacts", clientContacts);
		m.addAttribute("teacherContacts", teacherContacts);
		m.addAttribute("otherContacts", otherContacts);
		m.addAttribute("title", "ContactBook - UserDashboard");
		return "normalUser/userDashboard";
	}

	// add contact
	@GetMapping("/add_contact")
	public String addContact(Model m) {

		m.addAttribute("title", "ContactBook - AddContact");
		m.addAttribute("contact", new Contact());

		return "normalUser/addContact";
	}

	// process on add contact form
	@PostMapping("/contact_processing")
	public String contactProcess(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal p, HttpSession session) {

		try {
			// User fetch
			String username = p.getName();
			User user = this.userRepository.getUserByUserName(username);

			// Image upload
			if (!file.isEmpty()) {
				// Extension Validation
				String originalFileName = file.getOriginalFilename();

				String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

				if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png"))) {
					session.setAttribute("msg", new Message("Only JPG, JPEG and PNG images are allowed.", "danger"));

					return "redirect:/user/add_contact";
				}

				// Content Type Validation
				String contentType = file.getContentType();

				if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
					session.setAttribute("msg", new Message("Invalid image format.", "danger"));

					return "redirect:/user/add_contact";
				}

				// Size Validation (2 MB)
				long maxSize = 2 * 1024 * 1024;

				if (file.getSize() > maxSize) {
					session.setAttribute("msg", new Message("Image size must be less than 2 MB.", "danger"));

					return "redirect:/user/add_contact";
				}

				/*
				 * // Unique File Name String fileName = UUID.randomUUID().toString() + "_" +
				 * originalFileName;
				 * 
				 * 
				 * Path uploadPath = Paths.get(UPLOAD_DIR);
				 * 
				 * if (!Files.exists(uploadPath)) { Files.createDirectories(uploadPath); }
				 * 
				 * Path path = uploadPath.resolve(fileName);
				 * 
				 * 
				 * 
				 * Path path = Paths.get(UPLOAD_DIR + fileName);
				 * 
				 * 
				 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 * 
				 * contact.setImageurl(fileName);
				 * 
				 * }
				 */

				Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

				String imageUrl = uploadResult.get("secure_url").toString();

				contact.setImageurl(imageUrl);

			}

			else {
				// Default Image
				contact.setImageurl(defaultContactImage);
			}

			// Contact Save
			contact.setUser(user);

			this.contactRepository.save(contact);

			session.setAttribute("msg", new Message("Contact added successfully.", "success"));

		}

		catch (Exception e) {
			e.printStackTrace();

			session.setAttribute("msg", new Message("Unable to add contact. Please try again.", "danger"));
		}

		return "redirect:/user/add_contact";
	}

	/*
	 * @GetMapping("/remove-message")
	 * 
	 * @ResponseBody public void removeMessage(HttpSession session) {
	 * session.removeAttribute("msg"); }
	 */

	// handler for showing contacts
	// per page per contacts = 5( 5[n])
	// current page = 0

	@RequestMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal p) {
		m.addAttribute("title", "ContactBook - ViewContact");

		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// CurrentPage = page
		// Contact per page =5
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUserId(user.getU_id(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "/normalUser/showContacts";
	}

	@GetMapping("/contact/{c_id}")
	public String showContactDetails(@PathVariable("c_id") Integer c_id, Model m, Principal p) {
		Contact contact = contactRepository.findById(c_id).orElseThrow(() -> new RuntimeException("Contact not Found"));

		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getU_id() == contact.getUser().getU_id()) {
			m.addAttribute("contact", contact);
		}

		m.addAttribute("title", "ContactBook - ContactDetails");
		return "normalUser/contactDetails";
	}

	// Delete operation
	@GetMapping("/delete_contact/{c_id}")
	public String deleteContact(@PathVariable("c_id") Integer c_id, Principal p, HttpSession session) {

		Contact contact = contactRepository.findById(c_id).orElseThrow(() -> new RuntimeException("Contact Not Found"));

		String userName = p.getName();
		User user = userRepository.getUserByUserName(userName);

		if (user.getU_id() == contact.getUser().getU_id()) {
			try {
				// Remove from user's contact list
				user.getContacts().remove(contact);

				/*
				 * // Delete image if (contact.getImageurl() != null &&
				 * !contact.getImageurl().isBlank() &&
				 * !contact.getImageurl().equals("default-contact.png")) { Path path =
				 * Paths.get(UPLOAD_DIR + contact.getImageurl());
				 * 
				 * Files.deleteIfExists(path); }
				 */

				userRepository.save(user);

				contactRepository.delete(contact);

				session.setAttribute("msg", new Message("Contact Deleted Successfully", "success"));
			}

			catch (Exception e) {
				e.printStackTrace();
				session.setAttribute("msg", new Message("Unable to delete contact", "danger"));
			}
		}
		return "redirect:/user/show_contacts/0";
	}

	// perform update operation
	@PostMapping("/update_contact/{c_id}")
	public String updateContact(@PathVariable("c_id") Integer c_id, Model m) {
		Contact contact = contactRepository.findById(c_id).orElseThrow(() -> new RuntimeException("Contact Not Found"));

		m.addAttribute("contact", contact);
		m.addAttribute("title", "ContactBook - UpdateContact");
		return "normalUser/updateContact";
	}

	@PostMapping("/update_processing")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal p) {

		// old contact details
		Contact oldContact = contactRepository.findById(contact.getC_id())
				.orElseThrow(() -> new RuntimeException("Contact Not Found"));

		try {
			if (!file.isEmpty()) {
				// Extension Validation
				String originalFileName = file.getOriginalFilename();

				String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

				if (!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png"))) {

					session.setAttribute("msg", new Message("Only JPG, JPEG and PNG images are allowed.", "danger"));

					return "redirect:/user/update_contact/" + contact.getC_id();
				}

				// Content Type Validation
				String contentType = file.getContentType();

				if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
					session.setAttribute("msg", new Message("Invalid image format.", "danger"));

					return "redirect:/user/update_contact/" + contact.getC_id();
				}

				// Size Validation (2 MB)
				long maxSize = 2 * 1024 * 1024;

				if (file.getSize() > maxSize) {
					session.setAttribute("msg", new Message("Image size must be less than 2 MB.", "danger"));

					return "redirect:/user/update_contact/" + contact.getC_id();
				}

				/*
				 * // Unique File Name String fileName = UUID.randomUUID().toString() + "_" +
				 * originalFileName;
				 * 
				 * Path uploadPath = Paths.get(UPLOAD_DIR);
				 * 
				 * if (!Files.exists(uploadPath)) { Files.createDirectories(uploadPath); }
				 * 
				 * Path path = uploadPath.resolve(fileName); ;
				 * 
				 * Files.createDirectories(path.getParent());
				 * 
				 * if (oldContact.getImageurl() != null &&
				 * !oldContact.getImageurl().equals("default-contact.png")) {
				 * 
				 * Path oldPath = Paths.get( "src/main/resources/static/image/" +
				 * oldContact.getImageurl());
				 * 
				 * 
				 * Path oldPath = Paths.get(UPLOAD_DIR + oldContact.getImageurl());
				 * 
				 * Files.deleteIfExists(oldPath); }
				 * 
				 * Files.createDirectories(path.getParent());
				 * 
				 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 * 
				 * Path targetPath = Paths.get(UPLOAD_DIR + fileName);
				 * 
				 * System.out.println("TARGET Exists : " + Files.exists(targetPath));
				 * 
				 * contact.setImageurl(fileName);
				 */
				
				Map<?, ?> uploadResult =
				        cloudinary.uploader().upload(
				                file.getBytes(),
				                ObjectUtils.emptyMap());

				String imageUrl =
				        uploadResult.get("secure_url").toString();

				contact.setImageurl(imageUrl);
			} else {
				contact.setImageurl(oldContact.getImageurl());
			}

			User user = this.userRepository.getUserByUserName(p.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);

			session.setAttribute("msg", new Message("Contact Updated Successfully.", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/contact/" + contact.getC_id();
	}

	// MY Profile handler
	@GetMapping("/myProfile")
	public String myProfile(Model m, Principal p) {

		String username = p.getName();
		User user = this.userRepository.getUserByUserName(username);

		m.addAttribute("user", user);
		m.addAttribute("title", "ContactBook - MyProfile");
		return "normalUser/myProfile";
	}

	@PostMapping("/save_review")
	public String saveReview(@ModelAttribute Review review, BindingResult result, Principal p) {

		System.out.println("🔥 REVIEW CONTROLLER HIT");

		if (result.hasErrors()) {
			return "normalUser/userDashboard"; // ya review page
		}

		String username = p.getName();
		User user = userRepository.getUserByUserName(username);

		System.out.println("User found: " + user);

		review.setUser(user);
		review.setCreatedAt(LocalDateTime.now());

		reviewRepository.save(review);

		System.out.println("✅ REVIEW SAVED");

		return "redirect:/user/index";
	}

	// setting handler
	@GetMapping("/settings")
	public String settings(Model m) {
		m.addAttribute("title", "ContactBook - Settings");
		return "normalUser/settings";
	}

	// change password
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			Principal p, HttpSession session) {
		String username = p.getName();
		User user = this.userRepository.getUserByUserName(username);
		System.out.println("User : " + user);

		String oldpassword = user.getPassword();

		System.out.println("mera password" + oldpassword);

		System.out.println("Current Password : " + currentPassword);
		System.out.println("New Password : " + newPassword);
		System.out.println("Confirm Password : " + confirmPassword);

		if (!this.bCryptPasswordEncoder.matches(currentPassword, oldpassword)) {
			System.out.println("Current Password Incorrect");
			session.setAttribute("msg", new Message("Current Password is incorrect!", "danger"));
			return "normalUser/settings";
		}

		if (!newPassword.equals(confirmPassword)) {
			System.out.println("New and Confirm Password do not match!");
			session.setAttribute("msg", new Message("New and Confirm Password do not match!", "danger"));
			return "normalUser/settings";
		}

		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		session.setAttribute("msg", new Message("Password changed successfully!", "success"));
		return "normalUser/settings";
	}
}
