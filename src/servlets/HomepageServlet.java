package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import objects.User;

/**
 * Servlet implementation class HomepageServlet
 */
@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection databaseConnection = null;
	private Statement statement = null;
	private ResultSet databaseResults = null;
	private DatabaseServlet databaseInstance;
	
	private String amazonConnection = "jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com/EmergencyConnectStorage"
			+ "?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false";
	
	public HomepageServlet() {
	}
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
		
		//allUsers = databaseInstance.getAllUsers(); This needs to be done once a user logsin or registers.
	}
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		establishConnection();
		System.out.println("===========called HomepageServlet==========");
		
		String currentUser = request.getParameter("username");
		String searchType = request.getParameter("inputType");
		
		databaseInstance = new DatabaseServlet();
		//RESPONSE writes all User FRIENDS, for "username" 
		if( searchType.equals("retrieveFriends") ) {
			System.out.println("Retrieving all " + currentUser + "'s friends");
			databaseInstance.loadAllUsers();
			int userID = databaseInstance.getUserID(currentUser);
	
			ArrayList<User> userFriends = getUserFriends( userID );
			String json = new Gson().toJson(userFriends);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		
		//GET auto complete recommendations for what's currently typed in the search bar. 	
		} else if( searchType.equals("liveSearch"))   {
			String input = request.getParameter("value");
			
			if( input.length() != 0  )  { // TODO, this is never true-- auto-search should be off until the presentation!
				System.out.println("searching for: " + input);
				ArrayList<String> allUserNames = getSearchTerms();
				
				if( !allUserNames.isEmpty() ) {
					String json = new Gson().toJson(allUserNames);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(json);
				}
			}
		
		// Get search results for the given term in the search bar, returns all relevant users not friends with current users	
		} else if( searchType.equals("searchResults") ) {
			String input = request.getParameter("searchInput");
			System.out.println("searching for: " + input);
			
			databaseInstance.loadAllUsers();
			ArrayList<User> searchedUsers = getSearchResults( input, currentUser );
			
			if( !searchedUsers.isEmpty() ) {
				String json = new Gson().toJson(searchedUsers);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				System.out.println("wrote back to search");
			}else {
				System.out.println("users empty!!!!");
			}
		}
	
	closeSQLObjects();
	}
	
	
	/*from a given userID, returns all the Users that are that user's friends, returns an empty set if a user has no friends :'(*/
	public ArrayList<User> getUserFriends(int userId ) {
		ArrayList<User> userFriends = new ArrayList<User>();
		
		if( databaseInstance.getUser(userId) == null ) {
			System.out.println("Looking for friends of user id" + userId + ". This user does not exist in the database");
		}
		else { 
			System.out.println("Getting friends of: " + databaseInstance.getUser(userId).getFullName());
			try {
				databaseResults = statement.executeQuery("SELECT * From Relationship WHERE User_One_ID='" +  userId + "'");
				while( databaseResults.next() ) {
					int friendID = databaseResults.getInt(2);
					
					User tempFriend = databaseInstance.getUser(friendID);
					//System.out.println(friendID);
					userFriends.add(tempFriend);
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		
	return userFriends;
	}
	
	/*returns a List of users (who are not friends with current user) matching what was Searched (looks for similar Usernames or names)
	 * when the user hits enter, or clicks the search button, this method is called and returned to search.js*/
	public ArrayList<User> getSearchResults(String value, String currUsername ) {
		User resultsUser = null;
		ArrayList<User> searchedUsers = new ArrayList<User>();
		try {
			System.out.println("BEGINNING OF GETRESULTS");
			databaseResults = statement.executeQuery("SELECT Username, Name FROM User" + 
					" WHERE Username LIKE '%" + value + "%' OR Name LIKE '%" + value + "%'");
			
			User userSearching = databaseInstance.getUser(currUsername);
			System.out.println(currUsername + ", is now searching for results");
			int userID = userSearching.getUserId();
			
			while( databaseResults.next() ) {
				String username = databaseResults.getString("Username");
				System.out.println("found search result; " + username);
				
				resultsUser = databaseInstance.getUser(username);
				int resultID = resultsUser.getUserId();
				
				//only add the user to the search results, if currUser is not friends with them.
				if( !(databaseInstance.friendAlreadyExists(userID, resultID) ) ) {
					searchedUsers.add(resultsUser);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	System.out.println("addresses of objects returned in search results: " + searchedUsers);
	return searchedUsers;
	}
	
	
	/* Used for the drop down search suggestions in search.js,
	 * creates an array of all the possible search terms (usernames, and names) and returns it to the dashboard for jquery's auto-complete*/
	public ArrayList<String> getSearchTerms( ) {
		ArrayList<String> searchTerms = new ArrayList<String>();
		
		try {
			databaseResults = statement.executeQuery("SELECT Username, Name FROM User ");
			while( databaseResults.next() ) {
				String name = databaseResults.getString ("Username");
				String user = databaseResults.getString ("Name");
				searchTerms.add(databaseResults.getString ("Username"));
				searchTerms.add(databaseResults.getString ("Name"));
				

				System.out.println("String username: " + name +  " String name: "  + user);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	return searchTerms;
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
