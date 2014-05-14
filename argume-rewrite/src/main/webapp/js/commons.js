function Commons () {};

Commons.BASE_URL = 'http://localhost:8080';
Commons.API_BASE_URL = 'http://localhost:8080/api';

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
