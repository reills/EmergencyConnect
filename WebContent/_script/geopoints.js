 /**
  *  for Geopoints and weather
  */
 function fetchLocation() {
     if (navigator.geolocation) {
         navigator.geolocation.getCurrentPosition(getPositon);
     } else {
         // Not supported
     }

     function getPositon(position) {
         lat = position.coords.latitude;
         long = position.coords.longitude;
         $.ajax({
             dataType: "json",
             // Grabs JSON from url
             url: "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + long + "&key=AIzaSyBybJlDH9Ioc3s3Xi8GagmzxL3D19pU8QM",
             success: function(response) {
                 // "response" is a JSON with the returned data
                 city = response.results[3].formatted_address;
                 console.log("from Google Reverse Geocoding API, city: " + city);
             }
         });
     }
 }

 function fetchWeather() {
     var key = "91153d239d63d420";
     $.ajax({
         dataType: "json",
         // Grabs JSON from url
         url: "http://api.wunderground.com/api/" + key + "/forecast/geolookup/conditions/q/" + lat + "," + long + ".json",
         success: function(response) {
             // "response" is a JSON with the returned data
             console.log(response);
             $("#weatherTemperature").html(response.current_observation.temperature_string);

             $("#weatherImage").html("<img style='margin: 20px' src=" + response.current_observation.icon_url + ">");

             $("#weatherCity").html(response.location.city);
         }
     });
 }