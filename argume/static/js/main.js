var app = angular.module('main', [ 'ngSanitize', 'ngResource', 'ngRoute', 'analytics' ]);

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
	}).when('/signup', {
		controller: 'SignupCtrl',
		templateUrl: 'signup.html'
	}).when('/forgotpwd', {
		controller: 'ForgotPwdCtrl',
		templateUrl: 'forgotpwd.html'
	}).when('/dashboard', {
		controller: 'DashboardCtrl',
		templateUrl: 'dashboard.html'
	}).when('/suggest_article', {
		controller: 'SuggestArticleCtrl',
		templateUrl: 'suggest_article.html'
	}).when('/settings', {
		controller: 'SettingsCtrl',
		templateUrl: 'settings.html'
	}).otherwise({
		redirectTo : '/'
	});
}).run(function($rootScope, $location, AuthData) {
	$rootScope.$location = $location;
	
	// Initialize Facebook SDK and subscribe to auth change event
	window.fbAsyncInit = function() {
		FB.init({
			appId : '677061055671921',
			status : true, // check login status
			cookie : true, // enable cookies to allow the server to access the session
			xfbml : true
		});

		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				var resp = response.authResponse;
				AuthData.fbLogin.save({
					userId: resp.userID, 
					accessToken: resp.accessToken
				}, function (response, status) {
					$rootScope.userInSession = response;
				}, function (response, status) {});	
			} else {
				AuthData.fbLogout.save({}, function (response, status) {
					$rootScope.userInSession = null;
				}, function (response, status) {
					// nobody got logged out
				});
			}
		});
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
			
			$scope.articles.forEach(function (article) {
				var counts = CommonFunc.calculateVotePct({
					favorable: article.votesFor,
					against: article.votesAgainst
				});
				article.pct_true = counts.pct_true;
				article.pct_false = counts.pct_false;
				article.totalVotes = counts.total;
			});
			
			$scope.page = data.page;
			$scope.total_pages = data.totalPages;
			$scope.loading = false;
		});
	};
	
	$scope.isTotalVotesGreaterThanMinimum = function (article) {
		return article &&  article.totalVotes >= Constants.minVotesToDisplay;
	};
	
	$scope.loadNextPage = function () {
		loadPage($scope.page + 1);
	};
	
	if ('/news_today' === $location.path()) {
		$scope.title = 'Bugünkü Haberler';
	} else {
		$scope.title = 'Tüm Haberler';
	}
	
	loadPage(1);
});

app.controller('AdminArgumentCtrl', function($scope, $routeParams, $location, ArticleData, AuthService) {
	AuthService.authenticateAdmin();
	
	var loadPage = function (page) {
		ArticleData.arguments.get({pagesize: 50, page: page}, function(data) {
			if ($scope.arguments == null) {
				$scope.arguments = data.objects; 
			} else {
				for (var i in data.objects) {
					$scope.arguments.push(data.objects[i]);
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
	
	$scope.loading = true;
	if ('id' in $routeParams) {
		ArticleData.arguments.get({
			id : $routeParams.id
		}, function (response) {
			$scope.argument = response;
			$scope.loading = false;
		});
	} else {
		loadPage(1);
	}
});

app.controller('AdminArticleCtrl', function ($scope, $routeParams, $location, ArticleData, AuthService) {
	AuthService.authenticateAdmin();
	
	var loadPage = function (page) {
		ArticleData.adminArticles.get({pagesize: 50, page: page}, function(data) {
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
			img2Url: this.article.img2Url,
			location: this.article.location,
			source: this.article.source,
			body: this.article.body,
			verified: this.article.verified
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
			ArticleData.adminArticles.update({id: $scope.article.id}, article, success, error);
		} else {
			ArticleData.adminArticles.save($scope.article, success, error);
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
		ArticleData.adminArticles.get({
			id : $routeParams.id
		}, function (response) {
			$scope.article = response;
			$scope.loading = false;
		});
	} else {
		loadPage(1);
	}
	
});

app.controller('ArticleCtrl', function($scope, $http, $routeParams, ArticleData, CommonFunc, Constants) {
	if (!jQuery.cookie('visitor_id')) {
		jQuery.cookie('visitor_id', CommonFunc.generateFingerprint());
	}
	
	var pageSize = 5;
	$scope.limitForSupporting = 5;
	$scope.limitForOpposed = 5;
	
	ArticleData.articles.get({
		id : $routeParams.id
	}, function(dbArticle) {
		$scope.article = dbArticle;
		loadArguments(true);
		loadArguments(false);
		
		ArticleData.votes.get({id: dbArticle.id}, function (dbVote) {
			$scope.vote = dbVote;
			$scope.voteChecked = true;
		});
	});
	
	var loadArguments = function (argType) {
		ArticleData.articleArguments.get({id : $scope.article.id, 
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
				if (argType === true) {
					$scope.article.arguments_for.push(arg);
				} else if (argType === false){
					$scope.article.arguments_against.push(arg);
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
		var payload = {argumentId: argId, favorable: isFavorable};
		
		var updateUi = function (status) {
			if (status === 304) {
				return;
			}
			
			var args = isTrueArg == true ? $scope.article.arguments_for : $scope.article.arguments_against;
			
			for (var i in args) {
				var arg = args[i];
				if (arg.id == argId) {
					if (isFavorable) {
						arg.likes += 1;
						if (status === 200) {
							arg.dislikes -= 1;
						} 
					} else {
						arg.dislikes += 1;
						if (status === 200) {
							arg.likes -= 1;
						}
					}
					break;
				}
			}
		};
		 		
		// angular's $resource.save does not return the correct response status for some reason
		// using $http instead
		$http.post([Constants.dataServiceBaseUrl, 'arguments', argId, 'like'].join('/'), payload).
		success(function(data, status) {
			updateUi(status);
	    }).error(function(data, status) {
	    	  console.log(data);
	    });
		
	};
	
	$scope.openWriteNewModal = function (affirmative) {
		$scope.argTypeLabel = affirmative ? 'doğru' : 'yalan';
		$scope.argument = {affirmative: affirmative};
		jQuery("#writeNewModal").modal();
	};
	
	$scope.submitArgument = function () {
		$scope.submitting = true;
		if (this.argument.summary.length > Constants.maxCharsInArgumentSummary) {
			$scope.submitArgumentMessage = "Özet " + Constants.maxCharsInArgumentSummary + " karakterden fazla olamaz";
			$scope.success = false;
			$scope.submitting = false;
		} else if (this.argument.body && this.argument.body.length > Constants.maxCharsInArgumentBody) {
			$scope.submitArgumentMessage = "Detaylı argüman " + Constants.maxCharsInArgumentBody + " karakterden fazla olamaz";
			$scope.success = false;
			$scope.submitting = false;
		} else {
			// replace new lines with html breaks
			if (this.argument.body) {
				this.argument.body = CommonFunc.stripHtmlTags(this.argument.body);
				this.argument.body = this.argument.body.replace(/\n/g, '<br/>'); 
				this.argument.body = CommonFunc.expandHrefs(this.argument.body);
			}
			var argument = {
				articleId: $scope.article.id,
				summary: CommonFunc.expandHrefs(CommonFunc.stripHtmlTags(this.argument.summary)),
				body: this.argument.body,
				affirmative: this.argument.affirmative
			};
			
			var successFn = function () {
				$scope.submitArgumentMessage = "Argümanınız moderatörümüze iletildi";
				$scope.success = true;
				$scope.submitting = false;
				setTimeout(function() { 
					jQuery("#writeNewModal").modal('hide');
					$scope.submitArgumentMessage = null;
				}, 750);
			};
			
			var errorFn = function () {
				$scope.submitArgumentMessage = response.data;
				$scope.success = false;
				$scope.submitting = false;
			};
			
			ArticleData.arguments.save(argument, successFn, errorFn); 		
		}
	};
	
	$scope.giveVote = function (favorable) {	
		var updateCount = function (isNew) {
			if (favorable) {
				$scope.article.votesFor += 1; 
				if (!isNew)
					$scope.article.votesAgainst -= 1;
			} else {
				$scope.article.votesAgainst += 1;
				if (!isNew)
					$scope.article.votesFor -= 1;
			}
		};
		
		var success = function (isNew) { 
			return function (response, status) {
				$scope.voteMessage = "Oy kullandığınız için teşekkürler";
				$scope.vote = response;
				updateCount(isNew);
				$scope.submittingVote = false;
			};
		};
		
		var error = function (response) {
			$scope.submittingVote = false;
			// For some reason, Angular treats response 304 as error
			if (response.status !== 304) {
				$scope.voteMessage = "Hata";
			}
		};
		
		var vote = {
			favorable: favorable,
			articleId: $scope.article.id
		};
		
		$scope.voteMessage = null;
		$scope.submittingVote = true;
		var isNew = $scope.vote == null || !('id' in $scope.vote);
		ArticleData.votes.save({id: $scope.article.id}, vote, success(isNew), error); 
	};
	
	$scope.undecide = function () {
		$scope.voteMessage = null;
		if ($scope.vote) {
			$scope.submittingVote = true;
			ArticleData.votes.remove({id: $scope.article.id}, function (response) {
				$scope.voteMessage = "Olabilir. Argümanları okumaya devam";
				
				if ($scope.vote.favorable) {
					$scope.article.votesFor -= 1;
				} else {
					$scope.article.votesAgainst -= 1;
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
		return $scope.article &&  ($scope.article.votesFor + $scope.article.votesAgainst) >= Constants.minVotesToDisplay;
	};
});

app.controller('LoginCtrl', function($scope, $rootScope, $location, $window, AuthData, CommonFunc) {
	var loginErrorFn = function (response) {
		$scope.loginMessage = "Hata";
		if (response.status == 401) {
			$scope.loginMessage = "Hatalı kullanıcı adı veya şifre";
		}
		$scope.success = false;
		$scope.submitting = false;
	};		
	
	$scope.login = function () {
		$scope.submitting = true;
		
		var success = function (response) {
			$rootScope.userInSession = response;
			if ('redirectUrl' in $rootScope) {
				$location.path($rootScope.redirectUrl); 
			} else {
				$window.location.href = CommonFunc.getHomeUrl();
			}
		}; 	
		
		AuthData.session.save({email: this.user.email, password: this.user.password}, success, loginErrorFn);
	};
	
	$scope.showFbLogin = function () {
		FB.login(function(response) {
			if (response.authResponse) {
				var resp = response.authResponse;
				AuthData.fbLogin.save({
					userId: resp.userID, 
					accessToken: resp.accessToken
				}, function (response, status) {
					$rootScope.userInSession = response;
					$window.location.href = CommonFunc.getHomeUrl();
				}, loginErrorFn);	    	
		    } 
		});
	};
	
	$scope.logout = function () {
		AuthData.session.remove(function (response, status) {
			$rootScope.userInSession = null;
			$window.location.href = CommonFunc.getHomeUrl();
		}, function (response, status) {
			console.log(response);
		});
	};

	AuthData.sessionUser.get({}, function (response) {
		$rootScope.userInSession = response;
	}, function (error) {});	
	
});

app.controller('SignupCtrl', function($scope, $location, AuthData) {
	$scope.signup = function () {
		$scope.submitting = true;
		
		var success = function (response) {
			$location.path('/login'); 
		}; 
		
		var error = function (response) {
			$scope.signupMessage = response.data;
			$scope.success = false;
			$scope.submitting = false;
		};		
		
		var payload = {
			email: this.user.email, 
			password: this.user.password, 
			passwordConfirmation: this.user.passwordConfirmation
		};
		
		if (this.user.password !== this.user.passwordConfirmation) {
			$scope.signupMessage = "Hatalı şifre tekrarı";
			$scope.success = false;
			$scope.submitting = false;
		} else {
			AuthData.user.save(payload, success, error);
		}		
	}
});

app.controller('ForgotPwdCtrl', function($scope, $http, Constants) {
	$scope.resetPwd = function () {
		$scope.submitting = true;	
		
		$http.post([Constants.dataServiceBaseUrl, 'users' , 'reset_pwd'].join('/'), this.user.email).
		success(function(data, status) {
			$scope.forgotPwdMessage = 'Şifre talimatları email adresinize gönderilmiştir'
			$scope.success = true;
			$scope.submitting = false;
	    }).error(function(response, status) {
	    	$scope.forgotPwdMessage = response;
			$scope.success = false;
			$scope.submitting = false;
	    });	
	}
});

app.controller('DashboardCtrl', function($scope, $http, AuthService, Constants) {
	AuthService.authenticateUser();
	
	var pageSize = 10;
	var loadArguments = function (page) {
		$http.get([Constants.dataServiceBaseUrl, 'dashboard' , 'arguments'].join('/'), {params: {limit: (page * pageSize)}})
		.success(function (response, status) {
			$scope.arguments = response.objects;
			$scope.page = response.page;
			$scope.total_pages = response.totalPages;
			$scope.loading = false;
		}).error(function (response, status) {
			console.log(response);
		});
	};
	
	$scope.loadMoreArguments = function () {
		loadArguments($scope.page + 1);
	};
	
	$scope.loading = true;
	
	// load arguments
	loadArguments(1);
	
	// load argume score
	$http.get([Constants.dataServiceBaseUrl, 'dashboard' , 'argume_score'].join('/'))
	.success(function (response, status) {
		$scope.argumeScore = response;
	}).error(function (response, status) {
		console.log(response);
	});
});

app.controller('SuggestArticleCtrl', function($scope,  ArticleData) {
	$scope.saveArticle = function () {
		$scope.submitting = true;
		
		var article = {
			title: this.article.title,
			imgUrl: this.article.imgUrl,
			location: this.article.location,
			source: this.article.source,
			body: this.article.body
		};
		
		var success = function (response) {
			$scope.suggestArticleMessage = 'Tavsiyeniz moderatörümüze iletilmiştir, teşekkür ederiz.';
			$scope.success = true;
			$scope.submitting = false;
		}; 
		
		var error = function (response) {
			$scope.saveArticleMessage = "Hata";
			$scope.success = false;
			$scope.submitting = false;
		};
		
		ArticleData.articleSuggestion.save($scope.article, success, error);
	};
});

app.controller('SettingsCtrl', function($scope, $rootScope, $location, $http, Constants) {
	if (!$rootScope.userInSession) {
		$location.path('/login');
	}
	
	$scope.user = $rootScope.userInSession;
	
	var successFn = function (message) {
		$scope.success = true;
		$scope.actionMessage = message;
		$scope.submitting = false;
	};
	
	var errorFn = function (message) {
		$scope.success = false;
		$scope.actionMessage = message;
		$scope.submitting = false;
	};
	
	$scope.updatePassword = function () {
		$scope.submitting = true;
		
		if (this.user.change.newPassword !== this.user.change.newPasswordConfirmation) {
			$scope.actionMessage = "Hatalı şifre tekrarı";
			$scope.success = false;
			$scope.submitting = false;
		} else {	
			$http.put([Constants.dataServiceBaseUrl, 'users/password'].join('/'), {
				oldPassword: this.user.change.oldPassword,
				newPassword: this.user.change.newPassword
			}).success(function(data, status) {
				successFn('Şifreniz başarıyla değiştirildi');
		    }).error(function(data, status) {
		    	errorFn(data);
		    });
		}
	};
	
	$scope.updateDetails = function () {
		$scope.submitting = true;
		
		$http.put([Constants.dataServiceBaseUrl, 'users'].join('/'), {
			firstName: this.user.firstName,
			lastName: this.user.lastName,
			imgUrl: this.user.imgUrl
		}).success(function(data, status) {
			successFn('Kişisel bilgileriniz başarıyla değiştirildi');
	    }).error(function(data, status) {
	    	errorFn('Hata');
	    });
	};
});