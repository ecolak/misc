package self.ec.movies.app;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import self.ec.movies.model.Movie;

public class ApplicationConfig extends ResourceConfig {
	
	public static final String MOVIES_API_URL = "http://data.sfgov.org/resource/yitu-d5am.json";
	public static final String GEOCODING_API_URL = "http://maps.googleapis.com/maps/api/geocode/json";
	
	private static Client httpClient;
	private static Set<String> movieTitles;
	
	public ApplicationConfig() {
		packages("self.ec.movies").register(JacksonFeature.class);
		initHttpClient();
		initMovieTitles();
	}

	private static void initHttpClient() {
		httpClient = ClientBuilder.newClient(new ClientConfig().register(JacksonFeature.class));
	}
	
	public static Client getHttpClient() {
		return httpClient;
	}
	
	private static void initMovieTitles() {
		movieTitles = new HashSet<>();
		List<Movie> movieRecords = httpClient.target(MOVIES_API_URL)
				.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Movie>>() {});
		
		for (Movie record : movieRecords) {
			movieTitles.add(record.getTitle());
		}
		System.out.println(movieTitles.size() + " unique movie titles found");
	}
	
	public static Set<String> getMovieTitles() {
		return movieTitles;
	}
}
