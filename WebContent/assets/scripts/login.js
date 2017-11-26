/**
 * Login and register calls
 */

// Open and close login form
 $("#loginButton").click(function() {
         $("#loginModal").show();
      })
      $("#loginClose").click(function() {
          $("#loginModal").hide();
      })
      // Open and close register form
      $("#registerButton").click(function() {
          $("#registerModal").show();
      })
      $("#registerClose").click(function() {
          $("#registerModal").hide();
      })
      $("#profileClose").click(function() {
          $("#profileModal").hide();
          $("#messageEdit").html("");
      })
  
      $("#accountProfile").click(function() {
    	  		var params = {
    		        username: $("#accountProfile").text().substring(14),
    		        inputType: "getUserInformation"
    		    };   
    	  		console.log("wants to change profile");
    	  		
    	  	 $.post("DatabaseServlet", $.param(params), function(responseJson) { // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
    	  		var friendsJSON = responseJson;
 		    	 
    	  		var name = friendsJSON.fullName;
    	  		var fullName = name.split(" ");
    	  		var firstName = fullName[0];
    	  		var lastName = fullName[1];
    	  		
    	  		 $('#editfName').val(firstName);
    	  		 $('#editlName').val(lastName);
    	  		 $('#editPassword').val("");
    	  		 $('#editEmail').val(friendsJSON.email);
    	  		 $('#editPhoneNumber').val(friendsJSON.phoneNumber);
    	  		 
    	  		$("#profileModal").show();
    	  	
    	  	});
     });
 
 	$("#profileSubmit").click(function() {
 		console.log("clicked AppliedChanges to profile");
 		var firstName = $("#editfName").val(); 
 		var lastName = $("#editlName").val();
 		var wholeName = firstName + " " + lastName;
 		
 		var params = {
		        fullName: wholeName,
		        email:  $('#editEmail').val(),
		        password:  $('#editPassword').val(),
		        phoneNumber: $('#editPhoneNumber').val(),
		        username: $("#accountProfile").text().substring(14),
		        inputType: "updateInfo"
		   };   
	  		  		
	  	 $.post("DatabaseServlet", $.param(params), function(responseJson) { // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
	  		Cookies.set('password', params.password);
	  		 $("#messageEdit").html("<font color='green'> Changes sucessful </font>");
	  		$("#accountWelcome").html("Welcome, " + firstName );
	  	
	  	});
 	});

//call database to check user's login credentials
$(document).ready(function() {
    $("#loginSubmit").click(function() { // When HTML DOM "click" event is invoked on element with ID "login", execute the following function...
        var params = {
            username: $('#loginUsername').val(),
            password: $('#loginPassword').val(),
            inputType: "login"
        };
        $.post("DatabaseServlet", $.param(params), function(responseText) { // Execute Ajax GET request on URL of "DatabaseServlet" and execute the following function with Ajax response text...
            console.log(responseText);

            if (responseText != "FAILURE") {
                $("#loginButton").hide();
                $("#loginModal").hide();
                $("#registerButton").hide();
                $("#accountWelcome").html("Welcome, " + responseText );
                $("#accountMenu").show();
                
                $("#accountProfile").html("Edit Profile, " + params.username);
                $("#loggedInFeeds").show();
                $("#checkinCard").show();
                $("#col-no1").attr("class", "col-md-4");
                $("#col-no2").attr("class", "col-md-4");

                getUsersFriends();
                if (Cookies.get('username') != params.username){
                	Cookies.set('username', params.username);
                	Cookies.set('password', params.password);
                }
            } else {
                $('#messageDiv').html("<font color='red'>Username or password incorrect </font>");
                console.log("false");
            }
        });
    });
})



//Calls database to register user, if username already taken, the user must choose another.
$(document).ready(function() {
    $("#registerSubmit").click(function() {
        var params = {
            username: $('#registerUsername').val(),
            firstName: $('#registerfName').val(),
            lastName: $('#registerlName').val(),
            password: $('#registerPassword').val(),
            email: $('#registerEmail').val(),
            phoneNumber: $('#registerPhoneNumber').val(),
            inputType: "register"
        };
        $.post("DatabaseServlet", $.param(params), function(responseText) {
            console.log("calledPOST");
            if (responseText == 'userRegistered') {
               
                $("#loginButton").hide();
                $("#loginModal").hide();
                $("#registerButton").hide();
                $("#accountMenu").show();
                $("#accountWelcome").html("Welcome, " + params.firstName );
                
                $("#registerModal").hide();
                $("#accountProfile").html("Edit profile, " + params.username );
                $("#accountProfile").show();
                $("#loggedInFeeds").show();
                $("#checkinCard").show();
                $("#col-no1").attr("class", "col-md-4");
                $("#col-no2").attr("class", "col-md-4");
                
             
            } else {
                $("#messageRegister").html("<font color='red'>This username is already taken. Please choose another username.</font>");
                console.log("fail");
            }
        });
    });
})

function currentUserCookies(){
	 //console.log(Cookies.get() === 'undefined');
	 if(Cookies.get('username') === 'undefined'){}
	 else {
		 //console.log("cookies");
		 var params = {
		            username: Cookies.get('username'),
		            password: Cookies.get('password'),
		            inputType: "login"
		        };
		 $.post("DatabaseServlet", $.param(params), function(responseText) { // Execute Ajax GET request on URL of "DatabaseServlet" and execute the following function with Ajax response text...
			 if (responseText != "FAILURE") {
		                $("#loginButton").hide();
		                $("#loginModal").hide();
		                $("#registerButton").hide();
		                $("#accountMenu").show();
		                $("#accountWelcome").html("Welcome, " + responseText);
		                
		                $("#accountProfile").html("Edit profile, " + params.username);
		                $("#loggedInFeeds").show();
		                $("#col-no1").attr("class", "col-md-4");
		                $("#col-no2").attr("class", "col-md-4");

		                getUsersFriends();
		            } else {
		                $('#messageDiv').html("<font color='red'>Username or password incorrect </font>");
		                console.log("false");
		            }
		        });
	 }
 }