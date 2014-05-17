$(function() {
	var nextPageBtn = $("#next-page-btn");
	var spinner = $("#admin-articles-spinner");
	
	nextPageBtn.click(function () {
		nextPageBtn.hide();
		spinner.show();
		
		var nextPage = $(this).data("page") + 1;
		var pageSize = $(this).data("pagesize");
		var totalPages = $(this).data("totalpages")
		
		$.get([Commons.API_BASE_URL, 'arguments'].join('/'), {
			page: nextPage, 
			pagesize: pageSize
		}, function(data) {		
			$.each(data.objects, function (index, argument) {
				var html = '<tr>' +
								'<td><a href="/admin/arguments/' + argument.id + '">' 
								+ argument.summary + '</a></td>' +
								'<td><a href="/admin/articles/' + argument.articleId + '">' 
								+ argument.articleId + '</a></td>' +
								'<td>' + argument.affirmativeInTurkish + '</td>' +
								'<td>' + argument.verifiedInTurkish + '</td>' +
							'</tr>';
				$('#arguments-table').append(html);
			});
			
			nextPageBtn.data("page", nextPage);
			if (nextPage < totalPages) {
				nextPageBtn.show(); 
			}
			spinner.hide();
		});
	});
	
	$("#argument-status-dropdown").change(function () {
		$("#argument-status-form").submit();
	});
	
	Commons.formatDateCreatedFields('argument-date-created');
});