 /**
  *  for Geopoints and weather
  */

const google_key = "AIzaSyBybJlDH9Ioc3s3Xi8GagmzxL3D19pU8QM";
const wunderground_key = "91153d239d63d420";
var lat = 0;
var long = 0;
var city = "Flavortown";

function fetchLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(fetchLocationServices);
    } else {
        // Not supported
    }
}

function fetchLocationServices(position) {
    lat = position.coords.latitude;
    long = position.coords.longitude;
    fetchWeather();

    $.ajax({
        dataType: "json",
        url: "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + long + "&key=" + google_key,
        success: function(response) {
            city = response.results[3].formatted_address;
            console.log("Google Reverse Geocoding API, city: " + city);
        }
    });
}

function fetchWeather() {
    $.ajax({
        dataType: "json",
        url: "http://api.wunderground.com/api/" + wunderground_key + "/forecast/geolookup/conditions/q/" + lat + "," + long + ".json",
        success: function(response) {
            console.log("Wunderground API, weather conditions: " + response);
            weatherCity = response.location.city;
            temp = response.current_observation.temperature_string;
            weatherIcon = "<img style='margin: 20px' src=" + response.current_observation.icon_url + ">";
            $("#weatherCard").html("<div class='card'><div class='header'><h4 class='title'>" + weatherCity + "</h4><p class='category'>" + temp + "</p></div><div>" + weatherIcon + "</div></div>");
        }
    });
}