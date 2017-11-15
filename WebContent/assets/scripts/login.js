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

            if (responseText == "VALID") {
                $("#loginButton").hide();
                $("#loginModal").hide();
                $("#registerButton").hide();
                $("#accountButton").html("Welcome, " + params.username);
                $("#accountButton").show();
                //TODO:::::: getUsersFriends(username);
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
                $("#registerModal").hide();
                $("#registerButton").hide();
                $("#accountButton").html("Welcome, " + params.username);
                $("#accountButton").show();
                //TODO:::::: getUsersFriends(username);
            } else {
                $("#messageRegister").html("<font color='red'>This username is already taken. Please choose another username.</font>");
                console.log("fail");
            }
        });
    });
})