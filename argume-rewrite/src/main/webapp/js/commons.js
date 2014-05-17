function Commons () {};

Commons.BASE_URL = 'http://www.argume.com';
Commons.API_BASE_URL = Commons.BASE_URL + '/api';

Commons.postJson = function(url, data) { 
	return $.ajax({
		type: 'POST',
		url: url,
		data: JSON.stringify(data),
		contentType: 'application/json'
	});
};

Commons.generateGuid = function () { 
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		return v.toString(16);
	});
};

Commons.generateFingerprint = function () {
	return new Fingerprint().get();
};

Commons.formatDate = function (dateStr) {
	return dateStr ? moment(parseInt(dateStr)).fromNow() : "";
}

Commons.formatDateCreatedFields = function (elemName) {
	$.each($('[name=' + elemName + ']'), function (index, value) {
		var dc = value.textContent;
		value.textContent = Commons.formatDate(dc);
	});
};	
