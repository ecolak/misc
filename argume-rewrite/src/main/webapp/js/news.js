$(function() { 
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
						dateCreated: Commons.formatDate(article.dateCreated)	
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
	
	Commons.formatDateCreatedFields('article-date-created');
	
	var url = window.location.href;
	if (url.indexOf('search=today') > 0) {
		$("#second-tab").addClass("active");
		$("#first-tab").removeClass("active");
		$("#third-tab").removeClass("active");
	} else {
		$("#first-tab").addClass("active");
		$("#second-tab").removeClass("active");
		$("#third-tab").removeClass("active");
	}
});