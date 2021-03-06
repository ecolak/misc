app.directive('argumentsPane', function() {
	return {
		restrict : 'E',
		scope : {
			title : '=',
			type : '=',
			arguments : '=',
			article : "="
		},
		link : function(scope, element, attrs) {
			scope.toggleExpand = scope.$parent.toggleExpand;
			scope.updateArgLikes = scope.$parent.updateArgLikes;
			scope.openWriteNewModal = scope.$parent.openWriteNewModal;
			scope.showNextPageButton = scope.$parent.showNextPageButton;
			scope.loadMoreArguments = scope.$parent.loadMoreArguments;
			scope.location = scope.$parent.$location;
		},
		templateUrl : 'partials/arguments.html'
	};
});

app.directive('twitterShare', function() {
	return {
		link : function(scope, element, attr) {					
			var createShareButton = function (articleTitle) {
				twttr.widgets.createShareButton(attr.url, element[0], function(el) {}, {
					count : attr.count,
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
				element[0].innerHTML = '<a href="#" onclick="' + 
					    'window.open(\'https://www.facebook.com/dialog/feed?app_id=677061055671921&display=popup&name=' +  
					    encodeURIComponent(title + " - Doğru mu? Yalan mı?").replace(/'/g, "\\'") + 
					    '&caption=' + encodeURIComponent('www.argume.com') + 
					    '&link=' + encodeURIComponent(location.href) + 
					    '&redirect_uri=' + encodeURIComponent(location.protocol + '//' + location.hostname + '/popup_close.html') + 
					    '&picture=' + encodeURIComponent(imageUrl) + 
					    '\', \'facebook-share-dialog\', \'width=626,height=436\'); return false;">' + 
					    '<img src="img/fb-share.png"></a>';
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

app.directive('formAutofillFix', function() {
	return function(scope, elem, attrs) {
		// Fixes Chrome bug: https://groups.google.com/forum/#!topic/angular/6NlucSskQjY
	    elem.prop('method', 'POST');

	    // Fix autofill issues where Angular doesn't know about autofilled inputs
	    if(attrs.ngSubmit) {
	    	setTimeout(function() {
	        elem.unbind('submit').submit(function(e) {
	          e.preventDefault();
	          elem.find('input, textarea, select').trigger('input').trigger('change').trigger('keydown');
	          scope.$apply(attrs.ngSubmit);
	        });
	      }, 0);
	    }
	};
});