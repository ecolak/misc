$(function() {
	var submitButton = $("#submit-button");
	var deleteButton = $("#delete-button");
	var messageContainer = $("#save-article-msg-container");
	
	var errorFn = function (button) {
		messageContainer.text('Hata');
		messageContainer.addClass('alert-danger');
		messageContainer.show();
		button.prop('disabled', false);
	};
	
	$("#save-article-form").submit(function (event) {
		submitButton.prop('disabled', true);
		messageContainer.html('');
		messageContainer.removeClass('alert-danger');
		messageContainer.hide();
		
		var articleId = $("#article-id").val();
		var data = {
			title: $("#title").val().trim(),
			imgUrl: $("#img_url").val().trim(),
			img2Url: $("#img2_url").val().trim(),
			source: $("#source").val().trim(),
			location: $("#location").val().trim(),
			body: $("#body").val().trim(),
			verified: $("#verified").is(":checked")
		};
		var urlArr = articleId ? [Commons.API_BASE_URL, 'admin/articles', articleId] : [Commons.API_BASE_URL, 'admin/articles'];
		
		var ajaxFn = $.ajax({
			type: articleId ? 'PUT' : 'POST',
			url: urlArr.join('/'),
			data: JSON.stringify(data),
			contentType: 'application/json'
		});
		
		ajaxFn.done(function(data, status, xhr) {
			window.location.href = [Commons.BASE_URL, 'admin/articles'].join('/');
		}).fail(function (xhr, status, error) {
			errorFn(submitButton);
		});
		event.preventDefault();
	});
	
	var deleteButton = $("#delete-button");
	deleteButton.click(function (event) {
		messageContainer.removeClass('alert-danger');
		var sure = window.confirm('Emin misin?');
		if (sure) {
			deleteButton.prop('disabled', true);
			messageContainer.html('');
			messageContainer.hide();
			
			var articleId = $("#article-id").val();
			$.ajax({
				type: 'DELETE',
				url: [Commons.API_BASE_URL, 'admin/articles', articleId].join('/')
			}).done(function(data, status, xhr) {
				window.location.href = [Commons.BASE_URL, 'admin/articles'].join('/');
			}).fail(function (xhr, status, error) {
				errorFn(deleteButton);
			}); 
		}
		event.preventDefault();
	});
});