$(function() {
	var submitButton = $("#submit-button");
	var deleteButton = $("#delete-button");
	var messageContainer = $("#save-argument-msg-container");
	
	var errorFn = function (button) {
		messageContainer.text('Hata');
		messageContainer.addClass('alert-danger');
		messageContainer.show();
		button.prop('disabled', false);
	};
	
	$("#save-argument-form").submit(function (event) {
		submitButton.prop('disabled', true);
		messageContainer.html('');
		messageContainer.removeClass('alert-danger');
		messageContainer.hide();
		
		var argumentId = $("#argument-id").val();
		var data = {
			summary: $("#summary").val().trim(),
			body: $("#body").val().trim(),
			affirmative: $("#affirmative").is(":checked"),
			verified: $("#verified").is(":checked"),
			articleId: $("#article-id").val()
		};
		
		var urlArr = [Commons.API_BASE_URL, 'admin/arguments'];
		if (argumentId) {
			urlArr.push(argumentId);
		}
		
		var ajaxFn = $.ajax({
			type: argumentId ? 'PUT' : 'POST',
			url: urlArr.join('/'),
			data: JSON.stringify(data),
			contentType: 'application/json'
		});
		
		ajaxFn.done(function(data, status, xhr) {
			window.location.href = [Commons.BASE_URL, 'admin/arguments'].join('/');
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
			
			var argumentId = $("#argument-id").val();
			$.ajax({
				type: 'DELETE',
				url: [Commons.API_BASE_URL, 'admin/arguments', argumentId].join('/')
			}).done(function(data, status, xhr) {
				window.location.href = [Commons.BASE_URL, 'admin/arguments'].join('/');
			}).fail(function (xhr, status, error) {
				errorFn(deleteButton);
			}); 
		}
		event.preventDefault();
	});
});