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

app.directive('twitterShare', function() {
	return {
		link : function(scope, element, attr) {					
			var createShareButton = function (articleTitle) {
				twttr.widgets.createShareButton(attr.url, element[0], function(el) {}, {
					count : 'horizontal',
					text : articleTitle + ' - Doğru mu? Yalan mı?',
					url: attr.url,
					lang: 'tr',
					via: 'ArgumeHaber'
				});
			};
			
			attr.$observe('text', function(value) {
				if (value) {
					createShareButton(value); 
				}
			});
		}
	}
});

app.directive('facebookShare', function() {
	return {
		link : function(scope, element, attr) {					
			var createShareButton = function (title, imageUrl) {
				element[0].innerHTML = '<a class="btn btn-info btn-mini" href="#" onclick="' + 
					    'window.open(\'https://www.facebook.com/sharer/sharer.php?s=100&p[url]=' + 
					    encodeURIComponent(location.href) + '&p[title]=' + encodeURIComponent(title).replace("'", "\\'") + 
					    '&p[images][0]=' + encodeURIComponent(imageUrl) + 
					    '\', \'facebook-share-dialog\', \'width=626,height=436\'); return false;"> Facebook\'ta paylaş </a>';
			};
			
			var articleTitle = '';
			var imageUrl = '';
			
			var callCreateShareButton = function () {
				if (articleTitle && imageUrl) {
					createShareButton(articleTitle, imageUrl); 
				}
			};
			
			attr.$observe('title', function(title) {
				if (title) {
					articleTitle = title;
					callCreateShareButton(); 
				}
			});
			
			attr.$observe('image', function(image) {
				if (image) {
					imageUrl = image;
					callCreateShareButton(); 
				}
			});
		}
	}
});