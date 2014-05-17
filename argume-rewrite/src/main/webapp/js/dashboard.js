$(function() {
	$('[name="summary-link"]').click(function () {
		$(this).next().next().slideToggle();
	});
	
	Commons.formatDateCreatedFields('argument-date-created');	
});