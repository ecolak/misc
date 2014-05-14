$(function() {
	$("#fb-login-link").click(function () {
		FB.login(function(response) {
			if (response.authResponse) {
				var resp = response.authResponse;
				Commons.postJson([Commons.BASE_URL, 'fb_login'], {
					userId: resp.userID, 
					accessToken: resp.accessToken
				}).done(function(data, status, xhr) {
					window.location.href = Commons.BASE_URL;
				}).fail(function (xhr, status, error) {
					$("#login-error-msg-container").text('Facebookla girişte hata oluştu');
				});    	
		    } 
		});	
	});	
});