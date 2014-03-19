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

app.directive('fbShare', function() {
	return {
		link : function(scope, element, attr) {	
			var createShareButton = function (title, imageUrl) {
				element[0].innerHTML = '<a class="btn btn-info btn-mini" href="#" onclick="' + 
					    'window.open(\'https://www.facebook.com/dialog/feed?app_id=677061055671921&display=popup&name=' +  
					    encodeURIComponent(title + " - Doğru mu? Yalan mı?").replace(/'/g, "\\'") + 
					    '&caption=' + encodeURIComponent('www.argume.com') + 
					    '&link=' + encodeURIComponent(location.href) + 
					    '&redirect_uri=' + encodeURIComponent(location.protocol + '//' + location.hostname + '/popup_close.html') + 
					    '&picture=' + encodeURIComponent(imageUrl) + 
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

app.directive('fbLike', function($timeout) {
	return {
		restrict: 'E',
		template: '<div class="fb-like" '
					+ 'data-href="{{$location.absUrl()}}" data-send="false" '
					+ 'data-layout="button_count" data-action="like" data-share="false" '
					+ 'data-show-faces="false"></div>',
		link: function(scope, element, attributes) {
			$timeout(function() {
				return typeof FB !== "undefined" && FB !== null ? FB.XFBML
						.parse(element.parent()[0]) : void 0;
			});
		}
	};
});