app.factory('Constants', function () {
	return {
		//dataServiceBaseUrl : 'http://localhost:8080/api',
		dataServiceBaseUrl: 'http://www.argume.com/api',
		maxCharsInArgumentSummary: 140,
		maxCharsInArgumentBody: 500,
		minVotesToDisplay: 0
	};
});

app.factory('ArticlesPromise', function($http) {
	return $http.get('data/fake_data.json');
});

app.factory('ArticleData', function($resource, Constants) {
	return {
		articles: $resource(Constants.dataServiceBaseUrl + '/articles/:id'),
		articleSuggestion: $resource(Constants.dataServiceBaseUrl + '/articles/suggest'),
		adminArticles: $resource(Constants.dataServiceBaseUrl + '/admin/articles/:id', {}, {update: {method: 'PUT'}}),
		articleArguments: $resource(Constants.dataServiceBaseUrl + '/articles/:id/arguments'),
		arguments: $resource(Constants.dataServiceBaseUrl + '/arguments/:id', {}, {update: {method: 'PUT'}}),
		votes: $resource(Constants.dataServiceBaseUrl + '/articles/:id/vote', {}, {update: {method: 'PUT'}}),
		voteCounts: $resource(Constants.dataServiceBaseUrl + '/articles/:id/vote_counts'),
		likes: $resource(Constants.dataServiceBaseUrl + '/arguments/:id/like', {}, {update: {method: 'PUT'}}),
		dashboard: $resource(Constants.dataServiceBaseUrl + '/dashboard')
	};
});

app.factory('AuthData', function ($resource, Constants) {
	return {
		session: $resource(Constants.dataServiceBaseUrl + '/session/:id'),
		sessionUser: $resource(Constants.dataServiceBaseUrl + '/session/user'),
		user: $resource(Constants.dataServiceBaseUrl + '/users')
	};
});

app.factory('AuthService', function ($location, $rootScope, AuthData) {
	return {
		authenticateAdmin: function () {
			var currentLocation = $location.path();
			AuthData.sessionUser.get({}, function (response) {
				if (!response || response.role != 'ADMIN') {
					$rootScope.redirectUrl = currentLocation;
					$location.path('/login');
				}
			}, function (error) {
				$location.path('/login');
			});			
		},
		
		authenticateUser: function () {
			// check Facebook login too
			$rootScope.redirectUrl = $location.path();
			AuthData.sessionUser.get({}, function (response) {
				if (!response) {
					$location.path('/login');
				} else {
					$rootScope.redirectUrl = null;
				}
			}, function (error) {
				$location.path('/login');
			});			
		}
	}
});

app.factory('CommonFunc', function () {
	return {
		generateGuid: function () { 
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
				var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
				return v.toString(16);
			});
		},
		
		generateFingerprint: function () {
			return new Fingerprint().get();
		},
		
		calculateVotePct: function (counts) {
			var total = counts.favorable + counts.against;
			var pct_true = pct_false = 50;
			if (total > 0) {
				pct_true = Math.round(counts.favorable / (counts.favorable + counts.against) * 100);
				pct_false = Math.round(counts.against / (counts.favorable + counts.against) * 100); 
			}
			return {
				pct_true: pct_true,
				pct_false: pct_false,
				total: total
			};
		}
	};
});

app.factory('I18n', function () {
	return {
		"true": "Doğru",
		"false": "Yalan",
		"article": "Haber",
		"argument": "Argüman"
	};
});