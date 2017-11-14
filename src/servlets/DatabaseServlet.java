package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import objects.*;

/**
 * Servlet implementation class DatabaseServlet
 */
@WebServlet("/DatabaseServlet")
public class DatabaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArrayList<User> allUsers = null;
	private Connection databaseConnection = null;
	private Statement statement = null;
	private ResultSet databaseResults = null;
	private String mySqlUsername = "root";
	private String mySqlPassword= "Mochidog2017!";
	private String amazonConnection = "jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com/EmergencyConnectStorage"
			+ "?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false";
	
	/*connect to our amazon database - don't overload it pls my credit card isn't fancy*/
	public void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//databaseConnection = DriverManager.getConnection("jdbc:mysql://localhost/EmergencyConnect?user=" + mySqlUsername + "&password=" + mySqlPassword + "useSSL=false"); //uses the last file
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
						System.out.println("they are equal. returnign true");
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
		} else if( checkingAccountDetails.equals("search"))   {
			
		
		} else if ( checkingAccountDetails.equals("register") ) {
			registerUser(request, response); 
		}
		
	closeSQLObjects();
	}
	
	/* Creating all the user objects with all their shit, like name/id etc. 
	 * These objects are set to an request object so they can be accessed in the jsp if needed */
	public void loadAllUsers() {
		
		if( allUsers == null ) {
			allUsers = new ArrayList<User>();
		}
		 
		try {
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
				
				User tempUser = new User(name, username, hash, salt, userID, userStatus, phoneNumber, email );
				allUsers.add(tempUser);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Creates all the user information using the servlet request objects. 
	 * 
	 * */
	public void registerUser(HttpServletRequest request, HttpServletResponse response) {
		
		loadAllUsers();
		String enteredUsername = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		
		//check if the username already exists
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

			String userStatus = "Just signed up! ";
			String salt =  LoginHash.getSalt();
			String hash =  LoginHash.generateHash(salt + enteredPassword);
			
			try { // add User to the database (the insert statment returns the ID of newly added element)
				userID = statement.executeUpdate("INSERT INTO User VALUES ( '" + userID + "','" +  fullName + "','" + enteredUsername + "','" +  userStatus + "','" + 
						salt + "','" + hash + "','" + email + "','" + phoneNumber + "')");
				System.out.println("UserID: " + userID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			User tempUser = new User(fullName, enteredUsername, hash, salt, userID, userStatus, phoneNumber, email );
			allUsers.add(tempUser);
			
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
		
		User temp = getUser( userId);
		
		if( temp != null ) {
			temp.addFriend(friendId);
		}
		
		try {
			statement.executeUpdate("INSERT INTO Relationship VALUES ( '" + userId + "','" + friendId + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*returns a user from a given id, makes the array list in case this user is first user*/
	public User getUser(int id) {
		User temp = null; 
		
		if( allUsers != null ) {
			for( int i = 0; i < allUsers.size(); i++ ) {
				if( allUsers.get(i).getUserId() == id) {
					temp = allUsers.get(i);
				}
			}
		} 
		
	return temp;
	}
	
	/*Check if user exists*/
	public boolean userExists(String username){
		boolean userExists = false;
		User currUser = null;
		
		if( allUsers != null ) {
			for(int i=0; i<allUsers.size(); i++){
				currUser = allUsers.get(i);
				if(currUser.getUsername().equals(username)){
					userExists = true;
				}
			}
		} else {
			allUsers = new ArrayList<User>();
		}
		return userExists;
	}
	
	// getters and setters!
	public ArrayList<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(ArrayList<User> allUsers) {
		this.allUsers = allUsers;
	}
	
	/*
	 * Close all the SQL objects!!
	 * */
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
}
