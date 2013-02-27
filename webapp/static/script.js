function init() {
    init_long_literals();
    if (document.getElementById("flag") != null) {
        setFlag();
        setRank();
    }
}

function setRank() {
    $.getJSON(
     'http://localhost:8080/pubby/static/rankings',
     { key: 'countryName', otherkey: true },
     function (data) {
         for (var i = 0; i < data.results.bindings.length; i++) {
             if (document.getElementById("title").textContent === data.results.bindings[i].countryName.value) {
                 Array.prototype.slice.call(document.getElementsByClassName("globalRank"), 0).forEach(function (n) {
                     n.textContent = textContent = "" + (i + 1);
                 });
                 Array.prototype.slice.call(document.getElementsByClassName("globalScore"), 0).forEach(function (n) {
                     n.textContent = textContent = "" + Math.round(data.results.bindings[i].value.value * 100) / 100;
                 });
                 }
             }
         }
);
}

function setFlag() {
    var country_name = document.getElementById("title").textContent;
    var query = 'select ?flag where {' +
        '?c rdfs:label "' + country_name + '"@en .' +
        '?c dbpedia-owl:thumbnail ?flag .' +
        '} LIMIT 1';
    var endpoint = "http://dbpedia.org/sparql";
    function c(str) {
        var jsonObj = eval('(' + str + ')');
        document.getElementById("flag").setAttribute("src", jsonObj.results.bindings[0].flag.value);
    }
    sparqlQueryJson(query, endpoint, c);
}

var long_literal_counter = 0;
var long_literal_spans = {};
var long_literal_texts = {};
function init_long_literals() {
    var spans = document.getElementsByTagName('span');
    for (i = 0; i < spans.length; i++) {
        if (spans[i].className != 'literal') continue;
        var span = spans[i];
        var textNode = span.firstChild;
        var text = textNode.data;
        if (text.length < 300) continue;
        var match = text.match(/([^\0]{150}[^\0]*? )([^\0]*)/);
        if (!match) continue;
        span.insertBefore(document.createTextNode(match[1] + ' ... '), span.firstChild);
        span.removeChild(textNode);
        var link = document.createElement('a');
        link.href = 'javascript:expand(' + long_literal_counter + ');';
        link.appendChild(document.createTextNode('\u00BBmore\u00BB'));
        link.className = 'expander';
        span.insertBefore(link, span.firstChild.nextSibling);
        long_literal_spans[long_literal_counter] = span;
        long_literal_texts[long_literal_counter] = textNode;
        long_literal_counter = long_literal_counter + 1;
    }
}

function sparqlQueryJson(queryStr, endpoint, callback) {
    var querypart = "query=" + escape(queryStr);
    var xmlhttp = null;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    } else {
        alert('Perhaps your browser does not support XMLHttpRequests?');
    }
    xmlhttp.open('POST', endpoint, true);
    xmlhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xmlhttp.setRequestHeader("Accept", "application/sparql-results+json");

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            if (xmlhttp.status == 200) {
                callback(xmlhttp.responseText);
            } else {
                console.log("Sparql query error: " + xmlhttp.status + " - "
                    + xmlhttp.responseText);
            }
        }
    };
    xmlhttp.send(querypart);
};

function expand(i) {
    var span = long_literal_spans[i];
    span.removeChild(span.firstChild);
    span.removeChild(span.firstChild);
    span.insertBefore(long_literal_texts[i], span.firstChild);
}

function showAllMetadata(name) {
	var ele = document.getElementById(name);
	if (ele == null) return;
	var tables = document.getElementsByTagName('table');
	for (i = 0; i < tables.length; i++) {
		tables[i].style.display = 'block';
	}
}