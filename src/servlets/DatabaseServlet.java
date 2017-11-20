package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.javafx.webkit.KeyCodeMap.Entry;

import objects.*;

/**
 * Servlet implementation class DatabaseServlet
 */
@WebServlet("/DatabaseServlet")
public class DatabaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private ArrayList<User> allUsers = null;
	private Connection databaseConnection = null;
	private Statement statement = null;
	private ResultSet databaseResults = null;
	private String amazonConnection = "jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com/EmergencyConnectStorage"
			+ "?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false";
	private HashMap<String, User > usernameMap;
	private HashMap<Integer, User > userIdMap;
	private HashMap<Integer, List<Integer> > friendsList;
	
	public DatabaseServlet () {
		usernameMap = new HashMap<String, User>();
		userIdMap = new HashMap<Integer, User>();
		friendsList = new HashMap< Integer, List<Integer> >();
	}
	
	/*connect to our amazon database - don't overload it pls my credit card isn't fancy*/
	public void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			databaseConnection = DriverManager.getConnection(amazonConnection);
			statement = databaseConnection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*make sure the user exsits and that his password matches that in the database
	 * Returns true for good login in, and false for bad login. */
	public boolean verifyUser(String enteredUsername, String enteredPassword) {

		try {
			if( enteredUsername != null &&  enteredUsername.length() > 0 ) {
				databaseResults = statement.executeQuery("SELECT * FROM User WHERE Username='" +  enteredUsername + "'");
	
				if(databaseResults.next()) { // there should only be one row, since usernames are unique. 
					String databaseSalt = databaseResults.getString("Salt");
					String databaseHash = databaseResults.getString("Hash");
					
					String saltyPassword = databaseSalt + enteredPassword;
					String tempHash = LoginHash.generateHash( saltyPassword );
					
					System.out.println("database: " + databaseHash);
					System.out.println("tempHash: " + tempHash);
					
					
					if( databaseHash.equals(tempHash)) {
						System.out.println("Username and password correct");
						loadAllUsers();
						return true;
					}
					
				}
			}
		} catch( SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		
		establishConnection();
		System.out.println("===========called DatabaseServlet==========");
		
		String enteredUsername = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		String checkingAccountDetails = request.getParameter("inputType");
		
		if( checkingAccountDetails.equals("login") ) {
			System.out.println("goingToLoginVerfiication");
			if(  verifyUser( enteredUsername, enteredPassword ) ) {
				response.getWriter().write("VALID");
			} 
			else { 
				response.getWriter().write("FAILURE");
			}
		} else if ( checkingAccountDetails.equals("register") ) {
			registerUser(request, response); 
		} 
		
	closeSQLObjects();
	}
	
	/* Creating all the user objects with all their shit, like name/id etc. 
	 * These objects are set to an request object so they can be accessed in the jsp if needed */
	public void loadAllUsers() {
		
		try {
				establishConnection();
				System.out.println("Loading all users");
				
			databaseResults = statement.executeQuery("SELECT * FROM User");
			
			while(databaseResults.next()) { //while more rows, it goes to the next row at rs.next
				
				int userID = databaseResults.getInt("UserID");
				String name = databaseResults.getString("Name");
				String username = databaseResults.getString("UserName");
				String userStatus = databaseResults.getString("UserStatus");
				String salt =  databaseResults.getString("Salt");
				String hash =  databaseResults.getString("Hash");
				String email = databaseResults.getString("Email");
				String phoneNumber = databaseResults.getString("PhoneNumber");
				
				//System.out.println(userID + name + username + userStatus + salt + hash + email + phoneNumber);
				
				User tempUser = new User(name, username, hash, salt, userID, userStatus, phoneNumber, email);
				//System.out.println("in load users, username:" + username);
				
				usernameMap.put(username, tempUser);
				userIdMap.put(userID, tempUser);
				
			}
			System.out.println(" finished loading all users");
		//databaseResults = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getAllFriends();
		closeSQLObjects();
	}
	
	//put all the user's friends into a list, and assign it to the userID, in the friendList map
	public void getAllFriends() {
		try {
			establishConnection();
			ResultSet databaseResults1;
			databaseResults1 = statement.executeQuery("SELECT * FROM Relationship");
      
			while(databaseResults1.next()) { //while more rows, it goes to the next row at rs.next
				int userID = databaseResults1.getInt("User_One_ID");
				int friendID = databaseResults1.getInt("User_Two_ID");
				
				if( friendsList.containsKey( userID )) { //if user has a key already
					friendsList.get(userID).add(friendID);
				} else {
					List<Integer> usersFriends = new ArrayList<Integer>();
					usersFriends.add(friendID);
					friendsList.put(userID, usersFriends);
				}
			
				System.out.println("Getting *all* friends... user ID: " + userID + ", friendID " + friendID);
			}
			System.out.println("End of getAllFriends()");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    closeSQLObjects();
	}
	
	/* returns whether or not userID has added friendID as a a friend */
	public boolean friendAlreadyExists( int userID, int friendID) {
		if(friendsList.get(userID) == null) {
			return false;
		}else if( friendsList.get(userID).contains(friendID )) {
			return true;
		} else {
			return false;
		}
	}
	
	/* Creates all the user information using the servlet request objects. 
	 * */
	public void registerUser(HttpServletRequest request, HttpServletResponse response) {
		
		String enteredUsername = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		
		if(usernameMap == null || usernameMap.isEmpty()) {
			loadAllUsers();
		}
		
		if(!userExists(enteredUsername)){
			
			try{
				response.getWriter().write("userRegistered");
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
			// All info from registration form. 
			int userID =0; //databse keeps track of userId's using auto_increment.
			String fName = request.getParameter("firstName");
			String lName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String fullName = fName + " " + lName;
			String phoneNumber = request.getParameter("phoneNumber");
			
			System.out.println("Attempting to add: firstname: " + fName + ", lastname: " + lName + ", email: " +
			fullName + ", phoneNumber: " + phoneNumber + ", username: " + enteredUsername + ", password: " + enteredPassword 
			+ ", UserID: " + userID);

			String userStatus = "pending";
			String salt =  LoginHash.getSalt();
			String hash =  LoginHash.generateHash(salt + enteredPassword);
			
			 // add User to the database (the insert statement returns the ID of newly added element)
			try { 
				statement.executeUpdate("INSERT INTO User (Name, Username, UserStatus, Salt, Hash, Email, PhoneNumber ) VALUES ( '" + fullName + "','" + enteredUsername + "','" +  userStatus + "','" + 
						salt + "','" + hash + "','" + email + "','" + phoneNumber + "')", Statement.RETURN_GENERATED_KEYS);
				ResultSet rs = statement.getGeneratedKeys();
				rs.next();
				userID = rs.getInt(1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			User tempUser = new User(fullName, enteredUsername, hash, salt, userID, userStatus, phoneNumber, email );
			usernameMap.put(enteredUsername, tempUser);
			userIdMap.put(userID, tempUser);
		}else{
			try{
				response.getWriter().write("userExists");
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
			
		}
	}
	
	/*
	 * Given the parameter userID, adds a friend ( based on the parameter friendID) to that Id's friends list
	 * */
	public void addUserFriend(int userId, int friendId) {
		establishConnection();
		
		if( friendsList.containsKey( userId )) { //if user has a key already
			friendsList.get(userId).add(friendId);
		} else {
			List<Integer> usersFriends = new ArrayList<Integer>();
			usersFriends.add(friendId);
			friendsList.put(userId, usersFriends);
		}
	
		try {
			statement.executeUpdate("INSERT INTO Relationship VALUES ( '" + userId + "','" + friendId + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*returns a user from a given id, returns null if id is not found */
	public User getUser(int id) {
		System.out.println(userIdMap.get(id));
		return userIdMap.get(id);
	}
	
	/*returns a user with a given username, returns null if id is not found */
	public User getUser(String username) {
	
		User temp = usernameMap.get(username);
		if(temp == null) {
			System.out.println("User doesn't exist!");
		}
		
		return temp;
	}
	
	/*returns an Id for a given userName, if not found- > returns null */
	public int getUserID( String username ) { 
		
		User tempUser = usernameMap.get(username);
		
		if( tempUser != null ) {
			return tempUser.getUserId();
		}
		
		return 0;
	}
	
	/*Check if user exists returns true, if the username is already in the database, false otherwise */
	public boolean userExists(String username){
		if(usernameMap.containsKey(username) ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*Getters for the User containers. */
	public HashMap<String, User> getUsernameMap() {
		return usernameMap;
	}

	public HashMap<Integer, User> getUserIdMap() {
		return userIdMap;
	}
	
	/* Close all the SQL objects!!*/
	public void closeSQLObjects() {
		try { 
			
			if ( databaseResults != null ) {
				databaseResults.close();
			}
			if( statement != null ) {
				statement.close();
			}
			if( databaseConnection != null ) {
				databaseConnection.close();
			}
			
		} catch (SQLException sqle) {
			System.out.println("trying to close sql objects: " + sqle.getMessage() );
		}
	}
	
	public void updateStatus(int userID, String status){
		establishConnection();
		try {
			statement.executeQuery("UPDATE User SET UserStatus='" + status + "' WHERE UserID='" + userID + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeSQLObjects();
	}
//	public followPerson(String ) {
//		
//	}
}