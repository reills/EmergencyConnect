/**
 * All the javascript and jQuery for News API
 */

const news_api_key = "4a4f44aaabb84ddb9f8523e725b22757";
const nyt_api_key = "888fd10628734e11810b44d6df4480f3";

const ap_news_element = "#APNews";
const nyt_news_element = "#NYTNews";
const all_news_element = "#allNews";

var cards = [];

function fetchAP() {
    console.log("Fetching news from Associated Press...");
    $.ajax({
        dataType: "json",
        url: "https://newsapi.org/v1/articles?source=associated-press&sortBy=top&apiKey=" + news_api_key,
        success: function(response) {
            console.log(response);
            $(ap_news_element).html("");

            articles = response.articles;
            for (var i = 0; i < articles.length && i < 3; i++) {
                currentArticle = articles[i];
                title = JSON.stringify(currentArticle.title);
                url = JSON.stringify(currentArticle.url);
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";
                description = clean(currentArticle.description);
                cards.push(generateCard(title, url, "Associated Press", img + "<p>" + description + "</p>"));
                console.log(cards);
                $(ap_news_element).append(generateCard(title, url, "Associated Press", img + "<p>" + description + "</p>"));
            }
        }
    });
}

function loadAllNewsTopic(topic) {
    console.log("Fetching news from all sources by topic...");
    $.ajax({
        url: "https://newsapi.org/v2/everything?q=" + topic + "&language=en&apiKey=" + news_api_key,
        dataType: "json",
        success: function(response) {
            console.log(response);
            $(all_news_element).html("");

            articles = response.articles;
            for (var i = 0; i < articles.length && i < 3; i++) {
                currentArticle = articles[i];
                title = currentArticle.title;
                url = currentArticle.url;
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";
                $(ap_news_element).append(generateCard(title, url, topic.replace(/%20/g," "), img + "<p>" + clean(description) + "</p>"));
            }
        }
    });
}

function fetchNYT(city) {
    console.log("Fetching news from New York Times...");

    $.ajax({
        url: "https://api.nytimes.com/svc/search/v2/articlesearch.json?" + $.param({'api-key': nyt_api_key, 'q': city }),
        dataType: "json",
        method: "GET",
        success: function(response) {
            console.log(response);

            articles = response.response.docs;
            for (var i = 0; i < articles.length && i < 3; i++) {

                currentArticle = articles[i];
                if (currentArticle.multimedia.length != 0) {
                    headline = currentArticle.headline.main;
                    webURL = JSON.stringify(currentArticle.web_url);
                    webURL = webURL.substring(1, webURL.length - 1);
                    imgURL = "https://www.nytimes.com/" + clean(currentArticle.multimedia[0].url).slice(1, -1);
                    content = generateContent(imgURL, currentArticle.snippet);
                    $(nyt_news_element).append(generateCard(headline, webURL, "The New York Times", content));
                }

            }
        }
    });
}

function generateCard(title, url, subtitle, content) {
    var card = "<div class='card'>" +
                    "<div class='header'>" +
                        "<h4 class='title'>" +"<a href='" + clean(url) + "'>" + clean(title) + "</a>" + "</h4>" + 
                        "<p class='category'>" + clean(subtitle) + "</p>" +
                    "</div>" +
                    "<div class='content'>" + content + "</div>" +
                "</div>";
    return card;    
}

// String imgURL, json body
function generateContent(imgURL, body) {
    var content =   "<img height='120px' src='" + imgURL + "'>" +
                    "<p>" + clean(body) + "</p>"
    return content;
}

function clean(input_string) {
    return JSON.stringify(input_string).replace(/\\/g, "");
}