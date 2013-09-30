var app = angular.module('main', [ 'ngSanitize', 'ngResource' ]);

app.config(function($routeProvider) {
	$routeProvider.when('/', {
		controller : 'NewsCtrl',
		templateUrl : 'news.html'
	}).when('/news_today', {
		controller : 'NewsCtrl',
		templateUrl : 'news.html'
	}).when('/news/:id', {
		controller : 'ArticleCtrl',
		templateUrl : 'article.html'
	}).when('/admin/article/new', {
		controller: 'AdminArticleCtrl',
		templateUrl: 'admin/save_article.html'
	}).when('/admin/article/:id', {
		controller: 'AdminArticleCtrl',
		templateUrl: 'admin/save_article.html'
	}).when('/admin/article', {
		controller: 'AdminArticleCtrl',
		templateUrl: 'admin/list_articles.html'
	}).when('/admin/argument/:id', {
		controller: 'AdminArgumentCtrl',
		templateUrl: 'admin/save_argument.html'
	}).when('/admin/argument', {
		controller: 'AdminArgumentCtrl',
		templateUrl: 'admin/list_arguments.html'
	}).when('/login', {
		controller: 'LoginCtrl',
		templateUrl: 'login.html'
	}).otherwise({
		redirectTo : '/'
	});
}).run(function($rootScope, $location) {
	$rootScope.$location = $location;
});

app.factory('Constants', function () {
	return {
		dataServiceBaseUrl : 'http://localhost\\:8080/olasihaber/api',
		maxCharsInArgumentSummary: 140,
		maxCharsInArgumentBody: 500,
		minVotesToDisplay: 10
	};
});

app.factory('I18n', function () {
	return {
		"true": "Doğru",
		"false": "Yanlış",
		"article": "Haber",
		"argument": "Argüman"
	};
});

app.factory('ArticlesPromise', function($http) {
	return $http.get('data/fake_data.json');
});

app.factory('ArticleData', function($resource, Constants) {
	return {
		articles: $resource(Constants.dataServiceBaseUrl + '/article/:id', {}, {update: {method: 'PUT'}}),
		addArticleFromUrl: $resource(Constants.dataServiceBaseUrl + '/add_article'),
		articleArguments: $resource(Constants.dataServiceBaseUrl + '/article/:id/argument'),
		articleArgumentsByType: $resource(Constants.dataServiceBaseUrl + '/argument/article/:articleId/type/:type'),
		arguments: $resource(Constants.dataServiceBaseUrl + '/argument/:id', {}, {update: {method: 'PUT'}}),
		votes: $resource(Constants.dataServiceBaseUrl + '/vote/:id', {}, {update: {method: 'PUT'}}),
		voteCounts: $resource(Constants.dataServiceBaseUrl + '/vote/:articleId/counts'),
		voteByVisitorId: $resource(Constants.dataServiceBaseUrl + '/vote/article/:articleId/visitor/:visitorId'),
		likes: $resource(Constants.dataServiceBaseUrl + '/like/:id', {}, {update: {method: 'PUT'}}),
		likesByArgumentVisitor: $resource(Constants.dataServiceBaseUrl + '/like/argument/:argumentId/visitor/:visitorId'),
		likeCounts: $resource(Constants.dataServiceBaseUrl + '/like/:argumentId/counts')
	};
});

app.factory('AuthData', function ($resource, Constants) {
	return {
		login: $resource(Constants.dataServiceBaseUrl + '/auth/login'),
		sessionUser: $resource(Constants.dataServiceBaseUrl + '/auth/session_user')
	};
});

app.factory('AuthService', function ($location, $rootScope, AuthData) {
	return {
		authenticateAdmin: function () {
			var currentLocation = $location.path();
			AuthData.sessionUser.get({}, function (response) {
				if (!response || !response.admin) {
					$rootScope.redirectUrl = currentLocation;
					$location.path('/login');
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
			var pct_true = pct_false = 0;
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
			scope.openWriteNewModal = scope.$parent.openWriteNewModal;
			scope.showNextPageButton = scope.$parent.showNextPageButton;
			scope.loadMoreArguments = scope.$parent.loadMoreArguments;
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
			var total = article.favorableCnt + article.againstCnt;
			if (total > 0) {
				if (favorable)
					return Math.round(article.favorableCnt / total * 100);
				else
					return Math.round(article.againstCnt / total * 100); 
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

app.filter('remainingChars', function(Constants) {
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
			return head + tail + '<br/><a href="' + $location.absUrl()
					+ 'news/' + articleId + '">Devamı</a>';
		}
		return text;
	};
});

app.controller('NewsCtrl', function($scope, $location, ArticleData, CommonFunc, Constants) {
	var loadPage = function (page) {
		$scope.loading = true;
		var params = {page: page};
		if ('/news_today' === $location.path()) {
			params.search = 'today';
		}
		ArticleData.articles.get(params, function(data) {
			if ($scope.articles == null) {
				$scope.articles = data.objects; 
			} else {
				for (var i in data.objects) {
					$scope.articles.push(data.objects[i]);
				}
			}
			$scope.page = data.page;
			$scope.total_pages = data.totalPages;
			$scope.loading = false;
			
			$scope.pct_loading = true;
			var articles = $scope.articles;
			for (var i in articles) {
				var article = articles[i];
				(function (art) {
					ArticleData.voteCounts.get({articleId: art.id}, function (result) {
						var counts = CommonFunc.calculateVotePct(result);
						art.pct_true = counts.pct_true;
						art.pct_false = counts.pct_false;
						art.totalVotes = counts.total;
						$scope.pct_loading = false;
					}); 
				})(article);
			}
		});
	};
	
	$scope.isTotalVotesGreaterThanMinimum = function (article) {
		return article &&  article.totalVotes > Constants.minVotesToDisplay;
	};
	
	$scope.loadNextPage = function () {
		loadPage($scope.page + 1);
	};
	
	loadPage(1);
});

app.controller('AdminArgumentCtrl', function($scope, $routeParams, $location, ArticleData, AuthService) {
	AuthService.authenticateAdmin();
	
	$scope.loading = true;
	
	if ('id' in $routeParams) {
		ArticleData.arguments.get({
			id : $routeParams.id
		}, function (response) {
			$scope.argument = response;
			$scope.loading = false;
		});
	} else {
		ArticleData.arguments.get({pageSize: 100}, function(data) {
			$scope.arguments = data.objects;
			$scope.loading = false;
		});
	}
	
	$scope.saveArgument = function () {
		$scope.submitting = true;
		
		if (!('id' in $scope.argument)) {
			$scope.argument = {};
		} 
		
		$scope.argument.summary = this.argument.summary;
		$scope.argument.body = this.argument.body;
		$scope.argument.status = this.argument.status;
		$scope.argument.affirmative = this.argument.affirmative;
		
		// remove the transient fields from the object
		// before sending it to the backend
		if ('rank' in $scope.argument) {
			delete $scope.argument.rank;
		}
		if ('likes' in $scope.argument) {
			delete $scope.argument.likes;
		}
		if ('dislikes' in $scope.argument) {
			delete $scope.argument.dislikes;
		}
		
		if ('id' in $scope.argument) {	
			$scope.argument.$update({id: $scope.argument.id}, function (response) {
				$location.path('/admin/argument');
			}, function (response) {
				$scope.saveArgumentMessage = "Hata";
				$scope.success = false;
				$scope.submitting = false;
			});
		} else {
			$scope.argument.$save({}, function (response) {
				$location.path('/admin/argument');
			}, function (response) {
				$scope.saveArgumentMessage = "Hata";
				$scope.success = false;
				$scope.submitting = false;
			});
		}
	};
	
	$scope.deleteArgument = function () {
		$scope.submitting = true;
		var ok = confirm('Emin misin?');
		if (ok) {
			if ('id' in $scope.argument) {
				ArticleData.arguments.remove({id: $scope.argument.id}, function (response) {
					$location.path('/admin/argument');
				}, function (response) {
					$scope.saveArgumentMessage = "Hata";
					$scope.success = false;
					$scope.submitting = false;
				});
			} 
		}
	};
});

app.controller('AdminArticleCtrl', function ($scope, $routeParams, $location, ArticleData, AuthService) {
	AuthService.authenticateAdmin();
	
	var loadPage = function (page) {
		ArticleData.articles.get({pageSize: 50, page: page}, function(data) {
			if ($scope.articles == null) {
				$scope.articles = data.objects; 
			} else {
				for (var i in data.objects) {
					$scope.articles.push(data.objects[i]);
				}
			}
			
			$scope.page = data.page;
			$scope.total_pages = data.totalPages;
			$scope.loading = false;
		});
	};
	
	$scope.loadNextPage = function () {
		loadPage($scope.page + 1);
	};
	
	$scope.saveArticleFromUrl = function () {
		$scope.submitting = true;
		ArticleData.addArticleFromUrl.save({url: this.article.url}, function (response) {
			$location.path('/admin/article');
		}, function (response) {
			$scope.saveArticleMessage = "Hata";
			$scope.success = false;
			$scope.submitting = false;
		});
	},
	
	$scope.saveArticle = function () {
		$scope.submitting = true;
		
		var article = {
			id: this.article.id,
			title: this.article.title,
			imgUrl: this.article.imgUrl,
			location: this.article.location,
			source: this.article.source,
			body: this.article.body
		};
		
		var success = function (response) {
			$location.path('/admin/article');
		}; 
		
		var error = function (response) {
			$scope.saveArticleMessage = "Hata";
			$scope.success = false;
			$scope.submitting = false;
		};
		
		if ('id' in $scope.article) {
			ArticleData.articles.update({id: $scope.article.id}, article, success, error);
		} else {
			ArticleData.articles.save($scope.article, success, error);
		}
	};
	
	$scope.deleteArticle = function () {
		$scope.submitting = true;
		var ok = confirm('Emin misin?');
		if (ok) {
			if ('id' in $scope.article) {
				$scope.article.$delete({id: $scope.article.id}, function (response) {
					$location.path('/admin/article');
				}, function (response) {
					$scope.saveArticleMessage = "Hata";
					$scope.success = false;
					$scope.submitting = false;
				});
			} 
		}
	};
	
	$scope.loading = true;
	if ('id' in $routeParams) {
		ArticleData.articles.get({
			id : $routeParams.id
		}, function (response) {
			$scope.article = response;
			$scope.loading = false;
		});
	} else {
		loadPage(1);
	}
	
});

app.controller('ArticleCtrl', function($scope, $routeParams, ArticleData, CommonFunc, Constants) {
	if (!jQuery.cookie('visitor_fp')) {
		jQuery.cookie('visitor_fp', CommonFunc.generateFingerprint());
	}
	
	var visitorId = jQuery.cookie('visitor_fp');
	var pageSize = 5;
	$scope.limitForSupporting = 5;
	$scope.limitForOpposed = 5;
	
	ArticleData.articles.get({
		id : $routeParams.id
	}, function(dbArticle) {
		$scope.article = dbArticle;
		loadArguments(true);
		loadArguments(false);
		
		ArticleData.voteCounts.get({articleId: dbArticle.id}, function (result) {
			dbArticle.favorableCnt = result.favorable;
			dbArticle.againstCnt = result.against;
		});
		ArticleData.voteByVisitorId.get({articleId: dbArticle.id, visitorId: visitorId}, function (dbVote) {
			$scope.vote = dbVote;
			$scope.voteChecked = true;
		});
	});

	var loadArguments = function (argType) {
		ArticleData.articleArgumentsByType.get({articleId : $scope.article.id, 
			type: (argType === true ? 'supporting' : 'opposed'), 
			limit: (argType === true ? $scope.limitForSupporting : $scope.limitForOpposed)}, 
		function(result) {
			if (argType === true) {
				$scope.totalArgsSupporting = result.total;
			} else if (argType === false) {
				$scope.totalArgsOpposed = result.total;
			}
			
			if (argType === true) {
				$scope.article.arguments_for = [];
			}
			if (argType === false) {
				$scope.article.arguments_against = [];
			}
			
			var dbArgs = result.objects;
			for (var i in dbArgs) {
				var arg = dbArgs[i];
				if (arg.status === 'approved') {
					if (argType === true) {
						$scope.article.arguments_for.push(arg);
					} else if (argType === false){
						$scope.article.arguments_against.push(arg);
					}
					(function (argument) {
						$scope.argLikesMap = [];
						ArticleData.likesByArgumentVisitor.get({argumentId: arg.id, visitorId: visitorId}, function (result) {
							$scope.argLikesMap[result.argumentId] = result;
						});
					})(arg);
				}
			}
		});
	};
	
	$scope.loadMoreArguments = function (argType) {
		if (argType === true) {
			$scope.limitForSupporting += pageSize;
		} else {
			$scope.limitForOpposed += pageSize;
		}
		loadArguments(argType);
	};
	
	$scope.showNextPageButton = function (argType) {
		var limit = 0;
		var totalArgs = 0;
		if (argType === true) {
			limit = $scope.limitForSupporting;
			totalArgs = $scope.totalArgsSupporting;
		} else if (argType === false) {
			limit = $scope.limitForOpposed;
			totalArgs = $scope.totalArgsOpposed;
		}
		return limit > 0 && totalArgs > 0 && limit < totalArgs;
	};
	
	$scope.toggleExpand = function(type, index) {
		var divId = type + '_' + index;
		jQuery("#" + divId).slideToggle();
	};

	$scope.updateArgLikes = function(isTrueArg, argId, isFavorable) {
		var payload = {argumentId: argId, favorable: isFavorable, visitorId: visitorId};
		
		var updateUi = function (isNew, response) {
			$scope.argLikesMap[argId] = response;
			
			var args = isTrueArg == true ? $scope.article.arguments_for : $scope.article.arguments_against;
			
			for (var i in args) {
				var arg = args[i];
				if (arg.id == argId) {
					if (isFavorable) {
						arg.likes += 1;
						if (!isNew) {
							arg.dislikes -= 1;
						}
					} else {
						arg.dislikes += 1;
						if (!isNew) {
							arg.likes -= 1;
						}
					}
					break;
				}
			}
		};
		 		
		var visitorLike = $scope.argLikesMap[argId];
		if (visitorLike == null) {
			ArticleData.likes.save(payload, function (response) {
				updateUi(true, response);
			}, function (response) {
				console.log(response);
			});
		} else if (visitorLike.favorable !== payload.favorable) {
			ArticleData.likes.update({id: visitorLike.id}, payload, function (response) {
				updateUi(false, response);
			}, function (response) {
				console.log(response);
			});
		}
	};
	
	$scope.openWriteNewModal = function (affirmative) {
		$scope.argTypeLabel = affirmative ? 'doğru' : 'yanlış';
		$scope.argument = {affirmative: affirmative};
		jQuery("#writeNewModal").modal();
	};
	
	$scope.submitArgument = function () {
		$scope.submitting = true;
		if (this.argument.summary.length > Constants.maxCharsInArgumentSummary) {
			$scope.submitArgumentMessage = "Özet " + Constants.maxCharsInArgumentSummary + " karakterden fazla olamaz";
			$scope.success = false;
			$scope.submitting = false;
		} else if (this.argument.body.length > Constants.maxCharsInArgumentBody) {
			$scope.submitArgumentMessage = "Detaylı argüman " + Constants.maxCharsInArgumentBody + " karakterden fazla olamaz";
			$scope.success = false;
			$scope.submitting = false;
		} else {
			// replace new lines with html breaks
			this.argument.body = this.argument.body.replace(/\n/g, '<br/>');
			ArticleData.arguments.save({
				articleId: $scope.article.id,
				summary: this.argument.summary,
				body: this.argument.body,
				affirmative: this.argument.affirmative
			}, function (response) {
				$scope.submitArgumentMessage = "Argümanınız moderatörümüze iletildi";
				$scope.success = true;
				$scope.submitting = false;
				setTimeout(function() { 
					jQuery("#writeNewModal").modal('hide');
					$scope.submitArgumentMessage = null;
				}, 750);
			}, function (response) {
				$scope.submitArgumentMessage = "Hata";
				$scope.success = false;
				$scope.submitting = false;
			}); 
		}
	};
	
	$scope.giveVote = function (favorable) {	
		var updateCount = function (isNew) {
			if (favorable) {
				$scope.article.favorableCnt += 1; 
				if (!isNew)
					$scope.article.againstCnt -= 1;
			} else {
				$scope.article.againstCnt += 1;
				if (!isNew)
					$scope.article.favorableCnt -= 1;
			}
		};
		
		var success = function (isNew) { 
			return function (response) {
				$scope.voteMessage = "Oy kullandığınız için teşekkürler";
				$scope.vote = response;
				updateCount(isNew);
				$scope.submittingVote = false;
			};
		};
		
		var error = function (response) {
			$scope.voteMessage = "Hata";
			$scope.submittingVote = false;
		};
		
		var vote = {
			favorable: favorable,
			visitorId: visitorId,
			articleId: $scope.article.id
		};
		
		$scope.voteMessage = null;
		if ($scope.vote == null || !('id' in $scope.vote)) {
			$scope.submittingVote = true;
			ArticleData.votes.save(vote, success(true), error); 
		} else if ($scope.vote.favorable !== vote.favorable) {
			$scope.submittingVote = true;
			ArticleData.votes.update({id: $scope.vote.id}, vote, success(false), error);
		}
	};
	
	$scope.undecide = function () {
		$scope.voteMessage = null;
		if ($scope.vote) {
			$scope.submittingVote = true;
			ArticleData.votes.remove({id: $scope.vote.id}, function (response) {
				$scope.voteMessage = "Olabilir. Argümanları okumaya devam";
				
				if ($scope.vote.favorable) {
					$scope.article.favorableCnt -= 1;
				} else {
					$scope.article.againstCnt -= 1;
				}
				
				$scope.submittingVote = false;
				$scope.vote = null;
			}, function (response) {
				$scope.voteMessage = "Hata";
				$scope.submittingVote = false;
			}); 
		}
	};

	$scope.isTotalVotesGreaterThanMinimum = function () {
		return $scope.article &&  ($scope.article.favorableCnt + $scope.article.againstCnt) > Constants.minVotesToDisplay;
	};
	
});

app.controller('LoginCtrl', function($scope, $rootScope, $location, AuthData) {
	$scope.login = function () {
		$scope.submitting = true;
		
		var success = function (response) {
			if ('redirectUrl' in $rootScope) {
				$location.path($rootScope.redirectUrl); 
			} else {
				$location.path('/'); 
			}
		}; 
		
		var error = function (response) {
			$scope.loginMessage = "Hata";
			if (response.status == 401) {
				$scope.loginMessage = "Hatalı kullanıcı adı veya şifre";
			}
			$scope.success = false;
			$scope.submitting = false;
		};		
		
		AuthData.login.save({username: this.user.username, password: this.user.password}, success, error);
	}
});