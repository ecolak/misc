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
			Commons.postJson([Commons.BASE_URL, 'fb_login'].join('/'), {
				userId: resp.userID, 
				accessToken: resp.accessToken
			}).done(function(data, status, xhr) {}).fail(function (xhr, status, error) {});
		} else {
			Commons.postJson([Commons.BASE_URL, 'fb_logout'].join('/'), {})
				.done(function(data, status, xhr) {})
				.fail(function (xhr, status, error) {});
		}
	});
};

// Load the SDK asynchronously
(function(d) {
	var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
	if (d.getElementById(id)) {
		return;
	}
	js = d.createElement('script');
	js.id = id;
	js.async = true;
	js.src = "//connect.facebook.net/tr_TR/all.js";
	ref.parentNode.insertBefore(js, ref);
}(document));