$("#load-news-button").click(function() {
	console.log("You clicked me.");
	popNews(3);
});

function popNews(num) {
    for (var i = 0; i < num; i++) {
        $("#news-feed").append(cards.pop());
    }
}

