package self.ec.movies.resource;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import self.ec.movies.app.ApplicationConfig;
import self.ec.movies.model.GeocodingResponse;
import self.ec.movies.model.Movie;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MoviesResource {

	@GET
	public List<Movie> getByTitle(@QueryParam("title") String title) throws Exception {
		System.out.println("Get data for movie '" + title + "'");
		Client client = ApplicationConfig.getHttpClient();
		List<Movie> movies = client.target(ApplicationConfig.MOVIES_API_URL)
				.queryParam("title", title).request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Movie>>() {
				});

		// Get latitude and longitude for movies
		for (Movie movie : movies) {
			GeocodingResponse response = client
					.target(ApplicationConfig.GEOCODING_API_URL)
					.queryParam("address", movie.getLocations() + " San Francisco, CA")
					.queryParam("sensor", false)
					.request(MediaType.APPLICATION_JSON)
					.get(GeocodingResponse.class);
			
			if (response.error_message != null) {
				System.err.println("Error: " + response.error_message);
			}
			if (response != null && response.results != null && response.results.size() > 0) {
				movie.setLatitude(response.results.get(0).geometry.location.lat);
				movie.setLongitude(response.results.get(0).geometry.location.lng);
			}
		}
		return movies;
	}

	@GET
	@Path("/titles")
	public Set<String> getMovieTitles() {
		return ApplicationConfig.getMovieTitles();
	}

	@GET
	@Path("/count")
	public int getMovieTitlesCount() {
		return ApplicationConfig.getMovieTitles().size();
	}
}