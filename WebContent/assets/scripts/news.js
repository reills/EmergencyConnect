/**
 * All the javascript and jQuery for News API
 */

const news_api_key = "4a4f44aaabb84ddb9f8523e725b22757";
const nyt_api_key = "888fd10628734e11810b44d6df4480f3";

const ap_news_element = "#APNews";
const nyt_news_element = "#NYTNews";
const all_news_element = "#allNews";

function fetchAP() {
    console.log("Fetching news from Associated Press...");
    $.ajax({
        dataType: "json",
        url: "https://newsapi.org/v1/articles?source=associated-press&sortBy=top&apiKey=" + news_api_key,
        success: function(response) {
            console.log(response);
            $(ap_news_element).html("");

            articles = response.articles;
            for (var i = 0; i < articles.length; i++) {
                currentArticle = articles[i];
                title = JSON.stringify(currentArticle.title);
                url = JSON.stringify(currentArticle.url);
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";
                description = clean_stringify(currentArticle.description);
                $(ap_news_element).append(
                    "<div class='card'>" + 
                        "<div class='header'>" + 
                            "<h4 class='title'>" + "<a href=" + url + ">" + title + "<a/></h4>" + 
                            "<p class='category'>Associated Press</p>" + 
                        "</div>" + 
                        "<div class='content'>" + 
                            img + 
                            "<p>" + description + "</p>" + 
                        "</div>" + 
                    "</div>");
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
            for (var i = 0; i < articles.length; i++) {
                currentArticle = articles[i];
                title = JSON.stringify(currentArticle.title);
                url = JSON.stringify(currentArticle.url);
                img = "<img height='120px' src='" + currentArticle.urlToImage + "'>";
                description = clean_stringify(currentArticle.description);
                $(ap_news_element).append(
                    "<div class='card'>" + 
                        "<div class='header'>" + 
                            "<h4 class='title'>" + "<a href=" + url + ">" + title + "<a/></h4>" + 
                            "<p class='category'>" + topic.replace(/%20/g," ") + "</p>" + 
                        "</div>" + 
                        "<div class='content'>" + 
                            img + 
                            "<p>" + description + "</p>" + 
                        "</div>" + 
                    "</div>");
            }
        }
    });
}

function fetchNYT(city) {
    console.log("Fetching news from New York Times...");
    var url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    url += '?' + $.param({
        'api-key': nyt_api_key,
        'q': city
    });

    $.ajax({
        url: url,
        dataType: "json",
        method: 'GET',
    }).done(function(response) {
        console.log(response);
        articles = response.response.docs;
        for (var i = 0; i < articles.length && i < 5; i++) {
            currentArticle = articles[i];
            var webURL, imgURL;
            if (currentArticle.multimedia.length != 0) {
                headline = currentArticle.headline.main;
                webURL = JSON.stringify(currentArticle.web_url);
                webURL = webURL.substring(1, webURL.length - 1);
                imgURL = JSON.stringify(currentArticle.multimedia[0].url);
                imgURL = "https://www.nytimes.com/" + imgURL.slice(1, -1);
                $(nyt_news_element).append(generateCard(headline,webURL,"The New York Times", generateContent(imgURL, currentArticle.snippet)));
            }
        }
    }).fail(function(err) {
        throw err;
    });
}


function generateCard(title, url, subtitle, content) {
    var card = "<div class='card'>" +
                    "<div class='header'>" +
                        "<h4 class='title'>" +"<a href='" + url + "'>" + clean_stringify(title) + "</a>" + "</h4>" + 
                        "<p class='category'>" + subtitle + "</p>" +
                    "</div>" +
                    "<div class='content'>" + content + "</div>" +
                "</div>";
    return card;    
}

function generateContent(imgURL, body) {
    var content =   "<img height='120px' src='" + imgURL + "'>" +
                    "<p>" + JSON.stringify(body) + "</p>"
    return content;
}

function clean_stringify(input_string) {
    return JSON.stringify(input_string).replace(/\\/g, "");
}