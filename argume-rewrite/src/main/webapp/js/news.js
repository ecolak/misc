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
	var newsSpinner = $("#news-spinner");
	
	nextPageBtn.click(function () {
		nextPageBtn.hide();
		newsSpinner.show();
		
		var nextPage = $(this).data("page") + 1;
		var pageSize = $(this).data("pagesize");
		var totalPages = $(this).data("totalpages")
		
		$.get([Commons.API_BASE_URL, 'articles'].join('/'), {
			page: nextPage, 
			pagesize: pageSize
		}, function(data) {
			$.get([Commons.BASE_URL, 'html/partials/homepage_article.mustache'].join('/'), function(template) {
				Mustache.parse(template); // optional, speeds up future uses
				$.each(data.objects, function (index, article) {
					var rendered = Mustache.render(template, {
						id: article.id,
						title: article.title,
						body: article.body,
						imgUrl: (article.img2Url ? article.img2Url : (article.imgUrl ? article.imgUrl : 'img/resimsiz.gif')),
						source: article.source,
						location: article.location,
						votesForPct: article.votesForPct,
						votesAgainstPct: article.votesAgainstPct,
						dateCreated: formatDate(article.dateCreated)	
					});
					
					$('#news-container').append(rendered);
				});
			});
			
			nextPageBtn.data("page", nextPage);
			if (nextPage < totalPages) {
				nextPageBtn.show(); 
			}
			newsSpinner.hide();
		});
	});
	
	formatDateCreatedFields();
	
});