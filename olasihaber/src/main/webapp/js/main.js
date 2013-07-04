var app = angular.module('main', [ 'ngSanitize', 'ngResource' ]);

app.config(function($routeProvider) {
	$routeProvider.when('/', {
		controller : 'NewsCtrl',
		templateUrl : 'news.html'
	}).when('/news/:id', {
		controller : 'ArticleCtrl',
		templateUrl : 'article.html'
	}).otherwise({
		redirectTo : '/'
	});
}).run(function($rootScope, $location) {
	$rootScope.location = $location;
});

app.factory('Constants', function() {
	return {
		dataServiceBaseUrl : 'http://localhost\\:8080/olasihaber/api'
		//dataServiceBaseUrl: 'http://shrouded-basin-5617.herokuapp.com/api'
	};
});

app.factory('ArticlesPromise', function($http) {
	return $http.get('data/fake_data.json');
});

app.factory('ArticleData', function($resource, Constants) {
	return {
		articles: $resource(Constants.dataServiceBaseUrl + '/articles/:id', 
					{}, 
					{update: {method: 'PUT'}}),
		arguments: $resource(Constants.dataServiceBaseUrl + '/articles/:id/arguments', 
					{}, 
					{update: {method: 'PUT'}}),
		votes: $resource(Constants.dataServiceBaseUrl + '/articles/:id/vote', 
					{}, 
					{update: {method: 'PUT'}})
	};
});

app.directive('argumentsPane', function() {
	return {
		restrict : 'E',
		scope : {
			title : '=',
			type : '=',
			arguments : '='
		},
		link : function(scope, element, attrs) {
			scope.toggleExpand = scope.$parent.toggleExpand;
			scope.updateArgLikes = scope.$parent.updateArgLikes;
		},
		templateUrl : 'partials/arguments.html'
	};
});

app.filter('cleanse', function($sanitize) {
	return function(text) {
		return $sanitize(text);
	};
});

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
		return number.toString();
	};
});

app.filter('shorten', function($location) {
	return function(text, num_chars, articleId) {
		if (text.length > num_chars) {
			var head = text.substring(0, num_chars);
			var tail = text.substring(num_chars, text.length);
			var firstPeriodInTail = tail.indexOf('. ');
			tail = tail.substring(0, firstPeriodInTail + 1);
			return head + tail + '<br/><a href="' + $location.absUrl()
					+ 'news/' + articleId + '">Devamı</a>';
		}
		return text;
	};
});

app.filter('selectVote', function() {
	return function(actual, expected) {
		return (actual == expected ? "active" : "");
	};
});

app.controller('NewsCtrl', function($scope, ArticleData) {
	/*
	 * ArticlesPromise.then(function (result){ $scope.articles = result.data;
	 * });
	 */

	ArticleData.articles.query(function(data) {
		$scope.articles = data;
	});
});

app.controller('ArticleCtrl', function($scope, $routeParams, ArticleData) {
	/*
	 * ArticlesPromise.then(function (result){ $scope.article =
	 * result.data[$routeParams.id]; });
	 */

	ArticleData.articles.get({
		id : $routeParams.id
	}, function(dbArticle) {
		ArticleData.arguments.get({id : dbArticle.id}, function(dbArgs) {
			dbArticle.arguments_for = dbArgs.arguments_for;
			dbArticle.arguments_against = dbArgs.arguments_against;
			$scope.article = dbArticle;
		});
		ArticleData.votes.get({id: dbArticle.id}, function (dbVote) {
			$scope.vote = dbVote;
		});
	});

	$scope.toggleExpand = function(type, index) {
		var divId = type + '_' + index;
		jQuery("#" + divId).slideToggle();
	};

	$scope.updateArgLikes = function(isTrueArg, argId, isLike) {
		var args = isTrueArg == true ? $scope.article.arguments_for
				: $scope.article.arguments_against;
		var arg = args[argId];
		if (isLike) {
			arg.likes += 1;
		} else {
			arg.dislikes += 1;
		}
	};
	
	$scope.giveVote = function (articleId, voteType) {
		$scope.vote.article_id = articleId;
		$scope.vote.vote_type = voteType;
		if ('id' in $scope.vote) {
			$scope.vote.$update({id: articleId});
		} else {
			$scope.vote.$save();
		}
		$scope.voteMessage = "Oy kullandığınız için teşekkürler";
	};
	
	$scope.undecide = function (articleId) {
		if ('id' in $scope.vote) {
			$scope.vote.$remove({id: articleId});
		}
		$scope.voteMessage = null;
	};
});