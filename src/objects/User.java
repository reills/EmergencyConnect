package objects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class User {
	
	private String salt;
	private String hash;
	private String name;
	private String fullName;
	private String username;
	private String hashedPassword;
	private BufferedImage profileImage;
	private int userId;
	private String status;
	private String phoneNumber;
	private String email;
	private Settings userSettings;
	//private ArrayList<Messages> savedMessages();
	private ArrayList<Integer> friendsList;

	
	public User(String real_name, String user_name, String hash, String salt, int id, String status, String phoneNumber, String email ) {
		this.userId = id;
		this.fullName = real_name;
		this.username = user_name;
		this.hashedPassword = hash;
		this.salt = salt;
		this.status = status;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public void setProfileImage(BufferedImage profileImage) {
		this.profileImage = profileImage;
	}


	public void setUserId(int id) {
		this.userId = id;
	}

	public int getUserId() {
		return userId;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUserSettings(Settings userSettings) {
		this.userSettings = userSettings;
	}
	
	public void addFriend(int id) {
		friendsList.add( id );
	}


	public void getFriendsList(ArrayList<Integer> friendsList) {
		this.friendsList = friendsList;
	}

}
