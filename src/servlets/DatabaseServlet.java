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
     
	
	/*connect to our amazon database - don't overload it pls my credit card isn't fancy*/
	public void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			databaseConnection = DriverManager.getConnection("jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com/EmergencyConnectStorage"
					+ "?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false"); //using amazon connection
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
	
				if (!databaseResults.isBeforeFirst() ) {    
				    System.out.println("No data"); 
				    return false;
				} 
			
				if(databaseResults.next()) { // there should only be one row, since usernames are unique. 
					String databaseSalt = databaseResults.getString("Salt");
					String databaseHash = databaseResults.getString("Hash");
					
					String saltyPassword = databaseSalt + enteredPassword;
					String tempHash = LoginHash.generateHash( saltyPassword );
					
					if( databaseHash.equals(tempHash)) {
						return true;
					}
					
				}
			}
		} catch( SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		establishConnection();
		String enteredUsername = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		String checkingAccountDetails = request.getParameter("inputType");
		
		response.getWriter().write("FAILURE");
		
//		if( checkingAccountDetails.equals("login") ) {
//			if(  verifyUser( enteredUsername, enteredPassword ) ) {
//				//loadAllUsers();
//			} 
//			else { 
//				request.setAttribute("login_err", "please enter a valid username or password");
//			}
//		} else if ( checkingAccountDetails.equals("register") ) {
//			registerUser(request, response); 
//		}
		
	closeSQLObjects();
	}
	
	/* Creating all the user objects with all their shit, like name/id etc. 
	 * These objects are set to an request object so they can be accessed in the jsp if needed */
	public void loadAllUsers() {
		allUsers = new ArrayList<User>();
		 
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
		
		String enteredUsername = request.getParameter("username");
		String enteredPassword = request.getParameter("password");
		
		// All info from registration form. 
		String id = request.getParameter("UserID");
		String name = request.getParameter("Name");
		String username = request.getParameter("UserName");
		String email = request.getParameter("Email");
		String phoneNumber = request.getParameter("PhoneNumber");
		int userID = Integer.parseInt(id);
		
		String userStatus = "Just signed up! ";
		String salt =  LoginHash.getSalt();
		String hash =  LoginHash.generateHash(salt + enteredPassword);
		
		User tempUser = new User(name, username, hash, salt, userID, userStatus, phoneNumber, email );
		allUsers.add(tempUser);
		
		try { // add User to the database.
			statement.executeUpdate("INSERT INTO User VALUES ( '" + userID + "'," +  name + "'," + username + "'," +  userStatus + "'," + 
					salt + "'," + hash + "'," + email + "'," + phoneNumber + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Given the parameter userID, adds a friend ( based on the parameter friendID) to that Id's friends list
	 * */
	public void addUser(int userId, int friendId) {
		establishConnection();
		
		User temp = getUser( userId);
		
		if( temp != null ) {
			temp.addFriend(friendId);
		}
		
		try {
			statement.executeUpdate("INSERT INTO Relationship VALUES ( '" + userId + "'," + friendId + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*returns a user from a given id*/
	public User getUser(int id) {
		User temp = null; 
		
		for( int i = 0; i < allUsers.size(); i++ ) {
			if( allUsers.get(i).getUserId() == id) {
				temp = allUsers.get(i);
			}
		}
		
	return temp;
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
