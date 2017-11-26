/**
 * All the javascript and jQuery for News API
 */

const news_api_key = "4a4f44aaabb84ddb9f8523e725b22757";
const nyt_api_key = "888fd10628734e11810b44d6df4480f3";

const ap_news_element = "#APNews";
const nyt_news_element = "#NYTNews";

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
        for (var i = 0; i < articles.length; i++) {
            currentArticle = articles[i];
            var webURL, imgURL;
            if (currentArticle.multimedia.length != 0) {
                webURL = JSON.stringify(currentArticle.web_url);
                webURL = webURL.substring(1, webURL.length - 1);
                imgURL = JSON.stringify(currentArticle.multimedia[0].url);
                imgURL = imgURL.slice(1, -1);
                title =
                    $(nyt_news_element).append(
                        "<div class='card'>" +
                            "<div class='header'>" +
                                "<h4 class='title'>" +
                                "<a href='" + webURL + "'>" + JSON.stringify(currentArticle.headline.main) + "</a>" +
                                "</h4>" + "<p class='category'>New York Times</p>" + "</div>" +
                                "<div class='content'>" +
                                    "<img height='120px' src='https://www.nytimes.com/" + imgURL + "'>" +
                                    "<p>" + JSON.stringify(currentArticle.snippet) + "</p>" +
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
    return JSON.stringify(input_string).replace(/\\/g, "");
}