$(function() {
	
	var getAmount = function() {
		var amountStr = $("#amount").val().trim();
		if (amountStr) return parseFloat(amountStr);
		else return -1;
	};
	
	var processAmount = function (fn) {
		var amount = getAmount();
		if (amount != -1) fn(amount);
	};
	
	$("#enter-amount-form").submit(function (event) {
		processAmount(function (amount) {
			$("#span-tip-pct-10").text((amount * 0.1).toFixed(2));
			$("#span-tip-pct-15").text((amount * 0.15).toFixed(2));
			$("#span-tip-pct-20").text((amount * 0.2).toFixed(2));
			$("#tip-pct-div").show();
		});
		return false;
	});
	
	$("input:radio[name=tip-pct]").click(function() {
   	var tipPct = parseInt($(this).val());
		processAmount(function (amount) {
			$("#total-amount").text((amount + (amount * tipPct / 100)).toFixed(2)); 
			$("#total-div").show();
			$("#divide-by-form").show();
		});
		return false;
	});
	
	$("#divide-by-form").submit(function (event) {
		var divideBy = parseInt($("#divide-by").val().trim());
		processAmount(function (amount) {
			$("#per-person-amount").text((amount / divideBy).toFixed(2));
			$("#per-person-div").show();
		});
		return false;
	});
});