app.filter('addIndex', function() {
	return function(text, index) {
		return index + ". " + text;
	};
});

app.filter('euNumber', function($filter) {
	var standardNumberFilterFn = $filter('number');
	return function(text) {
		return standardNumberFilterFn(text).replace(',', '.');
	};
});

app.filter('to_s', function() {
	return function(number) {
		return (number != null ? number.toString() : "");
	};
});

app.filter('toTurkish', function(I18n) {
	return function(text) {
		return (text != null ? I18n[text] : "");
	};
});

app.filter('toPct', function() {
	return function(article, favorable) {
		if (article != null) {
			var total = article.votesFor + article.votesAgainst;
			if (favorable) {
				return total > 0 ? Math.round(article.votesFor / total * 100) : 50;
			} else {
				return total > 0 ? Math.round(article.votesAgainst / total * 100) : 50;
			}
		}
		return "";
	};
});

app.filter('formatDate', function () {
	return function(dateStr) {
		return dateStr != null ? moment(dateStr).fromNow() : "";
	};
});

app.filter('remainingChars', function() {
	return function(text, maxChars, updateElemId) {
		var len = text != null ? text.length : 0;
		var remaining = maxChars - len;
		var elem = "#" + updateElemId;
		if (remaining < 0) {
			jQuery(elem).addClass('label-important');
		} else {
			jQuery(elem).removeClass('label-important');
		}
		return remaining;
	};
});

app.filter('shorten', function($location) {
	return function(text, num_chars, articleId) {
		if (text.length > num_chars) {
			var head = text.substring(0, num_chars);
			var tail = text.substring(num_chars, text.length);
			var firstPeriodInTail = tail.indexOf('. ');
			tail = tail.substring(0, firstPeriodInTail + 1);
			var baseUrl = location.protocol + '//'+ location.hostname + (location.port ? ':' + location.port : '');
			return head + tail + '<br/><a href="' + baseUrl + '#/news/' + articleId + '">Devamı</a>';
		}
		return text;
	};
});