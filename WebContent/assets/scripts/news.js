/**
 * All the javascript and jQuery for News API
 */

const news_api_key = "4a4f44aaabb84ddb9f8523e725b22757";
const nyt_api_key = "888fd10628734e11810b44d6df4480f3";

var cards = [];
const maxArticles = 10;

// Loads news from the Associated Press into the news feed
function loadAP() {
    $.ajax({
        dataType: "json",
        url: "https://newsapi.org/v1/articles?source=associated-press&sortBy=top&apiKey=" + news_api_key,
        success: function(response) {
            // console.log(response);

            articles = response.articles;
            for (var i = 0; i < articles.length && i < 5; i++) {

                currentArticle = articles[i];
                title = JSON.stringify(currentArticle.title);
                url = JSON.stringify(currentArticle.url);
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";
                description = clean(currentArticle.description);

                // cards.push(generateCard(title, url, "Associated Press", img + "<p>" + description + "</p>"));
                // console.log(cards);
                $("#news-feed").append(generateCard(title, url, "Associated Press", img + "<p>" + description + "</p>"));
                console.log("Received card: Associated Press");
            }
        }
    });
}

// Loads articles from all sources based on query into cards
function loadTopic(topic) {
    $.ajax({
        url: "https://newsapi.org/v2/everything?q=" + topic + "&language=en&apiKey=" + news_api_key,
        dataType: "json",
        success: function(response) {
            // console.log(response);

            articles = response.articles;
            for (var i = 0; i < articles.length && i < maxArticles; i++) {

                currentArticle = articles[i];
                title = currentArticle.title;
                url = currentArticle.url;
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";

                cards.push(generateCard(title, url, topic.replace(/%20/g," "), img + "<p>" + clean(description) + "</p>"));
                console.log("Received card: " + topic.replace(/%20/g," "));
            }
        }
    });
}

// Loads articles from the New York Times based on query into cards
function loadNYT(city) {
    $.ajax({
        url: "https://api.nytimes.com/svc/search/v2/articlesearch.json?" + $.param({'api-key': nyt_api_key, 'q': city }),
        dataType: "json",
        method: "GET",
        success: function(response) {
            // console.log(response);

            articles = response.response.docs;
            for (var i = 0; i < articles.length && i < maxArticles; i++) {

                currentArticle = articles[i];
                if (currentArticle.multimedia.length != 0) {
                    headline = currentArticle.headline.main;
                    webURL = JSON.stringify(currentArticle.web_url);
                    webURL = webURL.substring(1, webURL.length - 1);
                    imgURL = "https://www.nytimes.com/" + clean(currentArticle.multimedia[0].url);
                    content = generateContent(imgURL, currentArticle.snippet);

                    cards.push(generateCard(headline, webURL, "The New York Times", content));
                    console.log("Received card: The New York Times");
                }

            }
        }
    });
}