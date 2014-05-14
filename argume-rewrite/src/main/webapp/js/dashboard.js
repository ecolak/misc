$(function() {
	var formatDate = function (dateStr) {
		return dateStr ? moment(parseInt(dateStr)).fromNow() : "";
	}
	
	var formatDateCreatedFields = function () {
		$.each($('[name="argument-date-created"]'), function (index, value) {
			var dc = value.innerText;
			value.innerText = formatDate(dc);
		});
	};	 
	
	$('[name="summary-link"]').click(function () {
		$(this).next().next().slideToggle();
	});
	
	formatDateCreatedFields();
	
});