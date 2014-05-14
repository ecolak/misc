$(function() {
	var formatDate = function (dateStr) {
		return dateStr ? moment(parseInt(dateStr)).fromNow() : "";
	}
	
	var formatDateCreatedFields = function () {
		$.each($('[name="article-date-created"]'), function (index, value) {
			var dc = value.innerText;
			value.innerText = formatDate(dc);
		});
	};	 
	
	var nextPageBtn = $("#next-page-btn");
	var spinner = $("#admin-articles-spinner");
	
	nextPageBtn.click(function () {
		nextPageBtn.hide();
		spinner.show();
		
		var nextPage = $(this).data("page") + 1;
		var pageSize = $(this).data("pagesize");
		var totalPages = $(this).data("totalpages")
		
		$.get([Commons.API_BASE_URL, 'admin/articles'].join('/'), {
			page: nextPage, 
			pagesize: pageSize
		}, function(data) {		
			$.each(data.objects, function (index, article) {
				var html = '<tr>' +
								'<td>' + article.id + '</td>' +
								'<td><a href="/admin/articles/' + article.id + '">' 
								+ article.title + '</a></td>' +
								'<td>' + article.source + '</td>' +
								'<td><span name="article-date-created">' 
								+ formatDate(article.dateCreated) + '</span></td>' +
								'<td>' + article.verifiedInTurkish + '</td>' +
							'</tr>';
				$('#articles-table').append(html);
			});
			
			nextPageBtn.data("page", nextPage);
			if (nextPage < totalPages) {
				nextPageBtn.show(); 
			}
			spinner.hide();
		});
	});
	
	$("#article-status-dropdown").change(function () {
		$("#article-status-form").submit();
	});
	
	formatDateCreatedFields();
	
});