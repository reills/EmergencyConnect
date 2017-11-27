$("#load-news-button").click(function() {
	console.log("You clicked me.");
	popNews(3);
});

function popNews(num) {
    for (var i = 0; i < num; i++) {
        $("#news-feed").append(cards.pop());
    }
}

$("#enable-location-card").click(function() {
	fetchLocation();
	$("#local-news-card").parent().show();
	$("#nyt-news-card").parent().show();
	$("#enable-location-card").parent().fadeOut(400);
});

$("#local-news-card").click(function() {
	if (city != "Flavortown") {
	    loadTopic(city.replace(" ", "%20") + "%20Emergency");
		$("#local-news-card").parent().fadeOut(400);	
	}
});

$("#nyt-news-card").click(function() {
	if (city != "Flavortown") {
	    loadNYT(city + " Emergency");
		$("#nyt-news-card").parent().fadeOut(400);
	}
});

$("#custom-topic-submit").click(function() {
	topic = $("#custom-topic-input").val();
	console.log("Topic: " + topic);
	if (topic != "") {
	    loadNYT(topic.replace(/ /g,"%20") + " Emergency");
	    loadTopic(topic.replace(/ /g,"%20") + " Emergency");
		$("#custom-topic-card").parent().fadeOut(400);
	}
});