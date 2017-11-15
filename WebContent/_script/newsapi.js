/**
 * All the javascript and jQuery for News API
 */

function fetchAP() {
    console.log("Fetching news from AP...");
    $.ajax({
        dataType: "json",
        // Grabs JSON from url
        url: "https://newsapi.org/v1/articles?source=associated-press&sortBy=top&apiKey=4a4f44aaabb84ddb9f8523e725b22757",
        success: function(response) {
            // "response" is a JSON with the returned data
            console.log(response);
            output_element = "#APNews";
            $(output_element).html("");
            // "articles" is an array of articles
            articles = response.articles;
            for (var i = 0; i < articles.length; i++) {
                currentArticle = articles[i];
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";
                title = JSON.stringify(currentArticle.title);
                url = JSON.stringify(currentArticle.url);
                description = "<p>" + clean_stringify(currentArticle.description) + "</p>";
                $(output_element).append("<div class='card'>" + "<div class='header'>" + "<h4 class='title'>" + "<a href=" + url + ">" + title + "<a/></h4>" + "<p class='category'>Associated Press</p>" + "</div>" + "<div class='content'>" + img + description + "</div>" + "</div>");
            }
        }
    });
}

function fetchNYT() {
    console.log("Fetching news...");
    var url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    url += '?' + $.param({
        'api-key': "888fd10628734e11810b44d6df4480f3",
        'q': "disaster"
    });
    $.ajax({
        url: url,
        dataType: "json",
        method: 'GET',
    }).done(function(response) {
        console.log(response);
        output_element = "#NYTNews"
        articles = response.response.docs;
        for (var i = 0; i < articles.length; i++) {
            currentArticle = articles[i];
            var webURL, imgURL;
            if (currentArticle.multimedia.length != 0) {
                webURL = JSON.stringify(currentArticle.web_url);
                webURL = webURL.substring(1, webURL.length - 1);
                imgURL = JSON.stringify(currentArticle.multimedia[0].url);
                imgURL = imgURL.slice(1, -1);
                title =
                    $("#NYTNews").append(
                        "<div class='card'>" +
                        "<div class='header'" +
                        "<h4 class='title'>" +
                        "<a href='" +
                        webURL + "'>" +
                        JSON.stringify(currentArticle.headline.main) +
                        "</a>" +
                        "</h4>" +
                        "<p class='category'>New York Times</p>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<img height='120px' src='https://www.nytimes.com/" + imgURL + "'>" +
                        "<p>" +
                        JSON.stringify(currentArticle.snippet) +
                        "</p>" +
                        "</div>" +
                        "</div>"
                    );
            }
        }
    }).fail(function(err) {
        throw err;
    });
}

function clean_stringify(input_string) {
    return JSON.stringify(input_string).replace("\\", "").replace("\\", "").replace("\\", "").replace("\\", "");
}