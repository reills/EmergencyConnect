const google_key = "AIzaSyBybJlDH9Ioc3s3Xi8GagmzxL3D19pU8QM";
const wunderground_key = "91153d239d63d420";
var lat = 0;
var long = 0;
var city = "Flavortown";

function fetchLocation() {
    console.log("Fetching location...");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(fetchLocationServices);
    } else {
        // Not supported
    }
}

function loadTwitterCard() {
    // var cityName = city.split(",")[0].replace(" ","_");
    $("#twitterCard").html("<a class='twitter-timeline' href='https://twitter.com/search?q=Los%20Angeles' data-widget-id='934751764481376256'>Tweets about Los Angeles</a><script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','twitter-wjs');</script>");
}

function fetchLocationServices(position) {
    lat = position.coords.latitude;
    long = position.coords.longitude;
    fetchWeather();
    initMap();
    $.ajax({
        dataType: "json",
        url: "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + long + "&key=" + google_key,
        success: function(response) {
            // console.log(response);
            city = response.results[3].formatted_address.split(",")[0];
            loadNYT(city + " Emergency");
            loadTopic(city.replace(" ", "%20") + "%20Emergency");
        }
    });
}

function initMap() {
    var uluru = {
        lat: lat,
        lng: long
    };
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 15,
        center: uluru
    });
    var marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
}

function fetchWeather() {
    $.ajax({
        dataType: "json",
        url: "http://api.wunderground.com/api/" + wunderground_key + "/forecast/geolookup/conditions/q/" + lat + "," + long + ".json",
        success: function(response) {
            // console.log(response);
            weather_city = response.location.city;
            temp = response.current_observation.temperature_string;
            weatherIcon = "<img style='margin: 20px' src=" + response.current_observation.icon_url + ">";
            $("#weatherCard").html(generateCard(weather_city, "#", temp, weatherIcon));
        }
    });
}
// CARD UTILITY FUNCTIONS
function generateCard(title, url, subtitle, content) {
    var card = "<div class='card'>" + "<div class='header'>" + "<h4 class='title'>";
    if (url != "#") {
        card += "<a href='" + clean(url) + "'>" + clean(title) + "</a>";
    } else {
        card += clean(title);
    }
    card += "</h4>" + "<p class='category'>" + clean(subtitle) + "</p>" + "</div>" + "<div class='content'>" + content + "</div>" + "</div>";
    return card;
}

function generateContent(imgURL, body) {
    var content = "<img height='120px' src='" + imgURL + "'>" + "<p>" + clean(body) + "</p>"
    return content;
}

function clean(input_string) {
    return JSON.stringify(input_string).replace(/\\/g, "").replace(/"/g, "");
}