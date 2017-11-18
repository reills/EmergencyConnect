/**
 * All the searching is in here
 */
// Open and close login form
$("#searchClose").click(function() {
    $("#friendsListModal").hide();
})

// Open and close register form
$("#search_button").click(function() {
    $("#registerModal").show();
})


//Calls search when user clicks the button
$(document).ready(function() {
    $("#search_button").click(function() {
    	console.log("clicked search");
        alert("clicked that search button");
        searchForUsers();
    });
});

//Calls search when user hits enter
$('#searchbar').keydown(function(e) {
    if (e.keyCode == 13) {
        console.log("clicked search");
        alert('you pressed enter ^_^');
        searchForUsers();
    }
});

 function searchForUsers() {
	 console.log($('#searchbar').val());
	 var username;
	 console.log($('#accountButton').text());
     var params = {
    		 	searchInput: $('#searchbar').val(),
             inputType: "searchResults",
            	username: ($('#accountButton').text()).substring(9)
     };
     console.log("username-" + params.username);
     console.log("searching for: " + params.searchInput);
     
     $.post("HomepageServlet", $.param(params), function(responseText) {
         console.log("calledPOST");
         if (responseText) {
        	 console.log("in search if statement");
        	 console.log(responseText);
        	 	console.log("first user id:" + responseText[0].userId);
         } else {
//             $("#friendsList").html("<font color='red'>This username is already taken. Please choose another username.</font>");
             console.log("fail");
         }
     });
 }

//live search: as user types a key into the searchbar, 
//makes an ajax call to retrieve results if user clicks drop down item, search is called
//$(document).ready(function() {
//    $("#searchbar").keyup(function() {
//        var params = {
//            inputType: "liveSearch",
//            value: $("#searchbar").val()
//        };
//        $.post("HomepageServlet", $.param(params), function(responseJson) {
//            var availableTags = $.map(responseJson, function(el) {
//                return el;
//            });
//            
//            $("#searchbar").autocomplete({
//                source: availableTags,
//                select: function(event, ui) {
//                    $("#search_button").click(); }
//            })
//        });
//     });
// });



//Retrieve logged in users friends from the database once user logs in
function getUsersFriends(userName) {
    var params = {
        username: userName,
        inputType: "retrieveFriends"
    };
    $.post("HomepageServlet", $.param(params), function(responseJson) { // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        $("#friendsList").html("");
        var $ul = $("<ul id=friendObjects>").appendTo($("#friendsList")); // Create HTML <ul> element and append it to HTML DOM element with ID "somediv".
        $.each(responseJson, function(index, item) { // Iterate over the JSON array.
            $("<li>").text(item).appendTo($ul); // Create HTML <li> element, set its text content with currently iterated item and append it to the <ul>.
        });
    });
}




//Display message for guest (necesssary if we create cookies, etc. )
$(document).ready(function() {
    $("#friendsList").html(" <em> You are currently accessing the site as a guest. To add or find friends, please log in. </em>");
});