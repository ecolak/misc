$(function() {
	var maxCharsInArgumentSummary = 140;
	var maxCharsInArgumentBody = 500;

	if (!$.cookie('visitor_id')) {
		$.cookie('visitor_id', Commons.generateFingerprint());
	}
	
	var articleId = $("#article-id").text();
	var dateCreated = $("#article-date-created");
	dateCreated.text(dateCreated.text() ? moment(parseInt(dateCreated.text())).fromNow() : ""); 	
	
	var registerLikeEvents = function() {
		$('[name="like-it-button"]').click(function () {
			updateLikes($(this).data("id"), true);
		});
		
		$('[name="dislike-it-button"]').click(function () {
			updateLikes($(this).data("id"), false);
		});
	};
	
	var loadMoreArguments = function (type, button, container, spinner) {
		button.hide();
		spinner.show();
			
		var nextPage = button.data("page") + 1;
		var pageSize = button.data("pagesize");
		var totalPages = button.data("totalpages")
		
		$.get([Commons.API_BASE_URL, 'articles', articleId, 'arguments'].join('/'), {
			type: type,
			limit: (parseInt(nextPage) * parseInt(pageSize))
		}, function(data) {
			container.html('');
			
			$.get([Commons.BASE_URL, 'html/partials/argument.mustache'].join('/'), function(template) {
				Mustache.parse(template); // optional, speeds up future uses
				$.each(data.objects, function (index, argument) {
					var rendered = Mustache.render(template, {
						id: argument.id,
						summary: argument.summary,
						body: argument.body,
						likes: argument.likes,
						dislikes: argument.dislikes,	
						hasBody: argument.hasBody
					});
					
					container.append(rendered);
					
					if (index === data.objects.length - 1) {
						registerLikeEvents();
					}
				});
			});
			
			button.data("page", nextPage);
			if (nextPage < totalPages) {
				button.show(); 
			}
			spinner.hide();
		});
	}
	
	$("#load-more-args-for-btn").click(function () {
		loadMoreArguments('supporting', $(this), $("#args-for-container"), $("#args-for-spinner"));
	});
	
	$("#load-more-args-against-btn").click(function () {
		loadMoreArguments('opposed', $(this), $("#args-against-container"), $("#args-against-spinner"));
	});
	
	var openModalFn = function (label, affirmative) {
		$("#writeNewModal").modal();
		var labelEl = $("#myModalLabel");
		labelEl.text(label);
		$("#affirmative-input").val(affirmative);
	};
	
	$("#open-arg-for-modal-btn").click(function () {
		openModalFn("Sizce neden doğru?", true);
	});
	
	$("#open-arg-against-modal-btn").click(function () {
		openModalFn("Sizce neden yalan?", false);
	});
	
	var expandHrefs = function (text) {
		return text.replace(/(https?:\/\/.+?)(\s|$)/g, '<a href="$1" target="_blank">$1</a>');
	};
	
	var stripHtmlTags = function (text) {
		return text.replace(/<\/?[^>]+(>|$)/g, '');
	};
	
	$("#submit-argument-form").submit(function (event) {
		var summary = $("#arg-summary").val().trim();
		var body = $("#arg-body").val().trim();
		var alertContainer = $("#alert-container");
		var submitArgumentSpinner = $("#submit-argument-spinner");
		var submitArgumentBtn = $("#submit-argument-btn");
		
		submitArgumentBtn.prop('disabled', true);
		submitArgumentSpinner.show();
		
		if (summary.length > maxCharsInArgumentSummary) {
			alertContainer.text("Özet " + Constants.maxCharsInArgumentSummary + " karakterden fazla olamaz");
			alertContainer.show();
		} else if (body.length > maxCharsInArgumentBody) {
			alertContainer.text("Detaylı argüman " + Constants.maxCharsInArgumentBody + " karakterden fazla olamaz");
			alertContainer.show();
		} else {
			if (body) {
				body = stripHtmlTags(body);
				body = body.replace(/\n/g, '<br/>'); 
				body = expandHrefs(body);
			}
	
			// submit argument with Ajax
			Commons.postJson([Commons.API_BASE_URL, 'arguments'].join('/'), {
				articleId: parseInt(articleId),
				summary: expandHrefs(stripHtmlTags(summary)),
				body: body,
				affirmative: 'true' === $("#affirmative-input").val()
			}).done(function(data) {
				alertContainer.text("Argümanınız moderatörümüze iletildi");
				alertContainer.addClass('alert-success');
				alertContainer.removeClass('alert-danger');
				alertContainer.show();
					
				setTimeout(function() { 
					$("#writeNewModal").modal('hide');
					alertContainer.text("");
				}, 750);
			}).fail(function (data) {
				alertContainer.text(data.responseText);
				alertContainer.addClass('alert-danger');
				alertContainer.removeClass('alert-success');
				alertContainer.show();
			}).always(function () {
				submitArgumentBtn.prop('disabled', false);
				submitArgumentSpinner.hide();
			});
		}
		event.preventDefault(); // so that the browser doesn't submit the form
	});
	
	var updateRemainingChars = function (elem, numCharsAllowed, updateElem) {
		var text = elem.val();
		var len = text != null ? text.length : 0;
		var remaining = numCharsAllowed - len;
		if (remaining < 0) {
			updateElem.addClass('label-important');
		} else {
			updateElem.removeClass('label-important');
		}
		updateElem.text(remaining);
	};
	
	$("#arg-summary").bind('input propertychange', function () {
		updateRemainingChars($(this), maxCharsInArgumentSummary, $("#remaining-chars-span-for-summary"));
	});
	
	$("#arg-body").bind('input propertychange', function () {
		updateRemainingChars($(this), maxCharsInArgumentBody, $("#remaining-chars-span-for-body"));
	});
	
	// Likes/dislikes
	var updateLikes = function (argumentId, favorable) {
		
		var updateLikeCountsOnUI = function (argumentId, favorable, isNew) {
			var likesElem = $("#likes-span-" + argumentId);
			var dislikesElem = $("#dislikes-span-" + argumentId);
			var numLikes = parseInt(likesElem.text());
			var numDislikes = parseInt(dislikesElem.text());
			if (favorable === true) {
				likesElem.text(++numLikes);
				if (!isNew) {
					dislikesElem.text(--numDislikes);
				}
			} else if (favorable === false) {
				dislikesElem.text(++numDislikes);
				if (!isNew) {
					likesElem.text(--numLikes);
				}
			}
		};
		
		// submit argument with Ajax
		Commons.postJson([Commons.API_BASE_URL, 'arguments', argumentId, 'like'].join('/'), {
			favorable: favorable
		}).done(function(data, status, xhr) {
			// update UI if status is not NOT_MODIFIED (304)
			if (xhr.status !== 304) {
				updateLikeCountsOnUI(argumentId, favorable, (xhr.status === 201));
			}
		});
	};
	
	// Voting
	var voteForBtn = $("#vote-for-btn");
	var voteAgainstBtn = $("#vote-against-btn");
	var voteUndecidedBtn = $("#vote-undecided-btn");
	var votesForBar = $("#votes-for-bar");
	var votesAgainstBar = $("#votes-against-bar");
	
	var updateVoteCount = function (favorable, isNew) {	
		var numVotesFor = parseInt(votesForBar.data("numvotes"));
		var numVotesAgainst = parseInt(votesAgainstBar.data("numvotes"));
		
		var updatePct = function (numFor, numAgainst) {
			var total = numFor + numAgainst;
			var forPct = (total > 0 ? Math.round(numFor / total * 100) : 0) + '%';
			var againstPct = (total > 0 ? Math.round(numAgainst / total * 100) : 0) + '%';
			votesForBar.text(forPct);
			votesForBar.css("width", forPct);
			votesAgainstBar.text(againstPct);
			votesAgainstBar.css("width", againstPct);
			$("#total-votes-container").text(total);
		};
		
		if (favorable === true) {
			votesForBar.data("numvotes", ++numVotesFor);
			if (!isNew) {
				votesAgainstBar.data("numvotes", --numVotesAgainst);
			}		
		} else if (favorable === false) {
			votesAgainstBar.data("numvotes", ++numVotesAgainst);
			if (!isNew) {
				votesForBar.data("numvotes", --numVotesFor);
			}		
		} else {
			if (currentVote) {
				if (currentVote.favorable === true) {
					votesForBar.data("numvotes", --numVotesFor);
				} else if (currentVote.favorable === false) {
					votesAgainstBar.data("numvotes", --numVotesAgainst);
				}
			}
		}
		updatePct(numVotesFor, numVotesAgainst);	
	};
	
	var updateVoteButtons = function () {
		if (currentVote == null) {
			voteUndecidedBtn.addClass('active');
			voteForBtn.removeClass('active');
			voteAgainstBtn.removeClass('active');
		} else if (currentVote.favorable === true) {
			voteForBtn.addClass('active');
			voteAgainstBtn.removeClass('active');
			voteUndecidedBtn.removeClass('active');			
		} else if (currentVote.favorable === false) {
			voteAgainstBtn.addClass('active');
			voteForBtn.removeClass('active');
			voteUndecidedBtn.removeClass('active');
		} 
 	};
	
 	var voteMessageContainer = $("#vote-message-container");
	var voteSpinner = $("#vote-spinner");
	
	var giveVote = function (favorable) {
		voteMessageContainer.text('');
		voteMessageContainer.hide();
		voteSpinner.show();
		
		// submit argument with Ajax
		Commons.postJson([Commons.API_BASE_URL, 'articles', articleId, 'vote'].join('/'), {
			favorable: favorable
		}).done(function(data, status, xhr) {
			// update UI if status is not NOT_MODIFIED (304)
			if (xhr.status !== 304) {
				currentVote = data;
				voteMessageContainer.text("Oy kullandığınız için teşekkürler");
				voteMessageContainer.show();
				updateVoteCount(favorable, (xhr.status === 201)); 	
				updateVoteButtons(); 
			}
		}).fail(function (xhr, status, error) {
			voteMessageContainer.text('Hata');
			voteMessageContainer.show();
		}).always(function (data, status, xhr) {
			// status 304 sometimes comes here
			voteSpinner.hide();
		});
	};
	
	var undecide = function () {
		voteMessageContainer.text('');
		voteMessageContainer.hide();
		voteSpinner.show();
		
		 $.ajax({
			type: 'DELETE',
			url: [Commons.API_BASE_URL, 'articles', articleId, 'vote'].join('/'),
		}).done(function(data, status, xhr) {		
			voteMessageContainer.text("Olabilir. Argümanları okumaya devam");
			voteMessageContainer.show();
			updateVoteCount(); 	
			currentVote = null;
			updateVoteButtons(); 
		}).fail(function (xhr, status, error) {
			voteMessageContainer.text('Hata');
			voteMessageContainer.show();
		}).always(function (data, status, xhr) {
			voteSpinner.hide();
		});
	};
	
	voteForBtn.click(function () {
		giveVote(true);
	});
	
	voteAgainstBtn.click(function () {
		giveVote(false);
	});
	
	voteUndecidedBtn.click(function () {
		undecide();
	});
	
	// mark the vote buttons for current user
	var currentVote = null;
	$.get([Commons.API_BASE_URL, 'articles', articleId, 'vote'].join('/'), function(data) {
		currentVote = data;
		updateVoteButtons(data);
	});
	
	// register like events
	registerLikeEvents();
});