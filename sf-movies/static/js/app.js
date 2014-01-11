$(document).ready(function() {

	var map;
	var markers = [];
	function initialize() {
		var myLatlng = new google.maps.LatLng(37.7833, -122.4167);
		var mapOptions = {
			zoom : 12,
			center : myLatlng
		}
		map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	}

	google.maps.event.addDomListener(window, 'load', initialize);

	function getMovieString(movie) {
		var result = '';
		if (movie.release_year) {
			result += '<strong>Release year: </strong>' + movie.release_year;
		}
		if (movie.director) {
			result += '<br/><strong>Director: </strong>' + movie.director;
		}
		if (movie.writer) {
			result += '<br/><strong>Writer: </strong>' + movie.writer; 
		}
		if (movie.actor_1 || movie.actor_2 || movie.actor_3) {
			result += '<br/><strong>Actors: </strong>';
			if (movie.actor_1) result += movie.actor_1;
			if (movie.actor_2) result += ', ' + movie.actor_2;
			if (movie.actor_3) result += ', ' + movie.actor_3;
		}
		if (movie.production_company) {
			result += '<br/><strong>Production company: </strong>' + movie.production_company;
		}
		if (movie.distributor) {
			result += '<br/><strong>Distributor: </strong>' + movie.distributor;
		}
		return result;		
	}
	
	function displayError(message) {
		var alertMessage = '<div class="alert alert-danger">' +
			'<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>' + 
			message +	
			'</div>';
		$('#alert-bar').html(alertMessage);
	}
	
	var apiBaseUrl = 'http://localhost:9000/api';
	$('#movie-search').typeahead({
		name : 'SF Movies',
		prefetch : {
			url: apiBaseUrl + '/movies/titles'
		} 
	});

	$('#movie-search').on('typeahead:initialized',
		function() {
			$.ajax({
				url : apiBaseUrl + '/movies/count'
			}).done(function(data) {
				$('#num-movies').text(data);
			}).fail(function(error) {
				displayError('Error while getting number of movies');
			});
		});
	
	$('#movie-search').on('typeahead:selected',
		function(object, datum) {
			console.log("Movie selected: " + datum.value);
			$('#spinner').show();
			$.ajax({
				url : apiBaseUrl + '/movies?title=' + datum.value
			}).done(function(data) {
				$('#spinner').hide();
				var alertMessage = '<div class="alert alert-info">' +
					'<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>' + 
  					datum.value + ' was filmed in ' + data.length + ' location' + (data.length > 1 ? "s" : "") + '<br/>' +
  					getMovieString(data[0])		
  					'</div>';
				$('#alert-bar').html(alertMessage);
				
				// clear current map
				for (var i in markers) {
					markers[i].setMap(null);
				}
				
				var infoWindow = new google.maps.InfoWindow();
				
				// add markers to the map
				for (var i in data) {
					var movie = data[i];
					var marker = new google.maps.Marker({
						position : new google.maps.LatLng(movie.latitude, movie.longitude),
						map : map
					});
					markers.push(marker);
					
					google.maps.event.addListener(marker, 'click', (function(marker, movie) {
						return function() {
							infoWindow.setContent('<strong>' + movie.title + '</strong>' +
									(movie.locations ? '<br/>' + movie.locations : '') + 
									(movie.fun_facts ? '<br/><em>' + movie.fun_facts + '</em>': ''));
							infoWindow.open(map, marker);
						}
					})(marker, movie));
				}				
						
			}).fail(function(error) {
				$('#spinner').hide();
				displayError("Error while searching for movie");
			});
		});
});