$(function() {
	$('[name="arg-contd-link"]').click(function (event) {
		$("#" + $(this).data('bodydiv')).slideToggle();
	});
});