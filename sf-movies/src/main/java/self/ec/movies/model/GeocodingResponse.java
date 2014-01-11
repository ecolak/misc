package self.ec.movies.model;

import java.util.List;

public class GeocodingResponse {

	public static class Result {	
		public static class AddressComponent {
			public String long_name;
			public String short_name;
			public List<String> types;
		}
		
		public static class Geometry {
			public static class Bounds {
				public Location northeast;
				public Location southwest;
			}
			
			public static class Location {
				public double lat;
				public double lng;
			}
			
			public static class ViewPort {
				public Location northeast;
				public Location southwest;
			}
			
			public Bounds bounds;
			public Location location;
			public String location_type;
			public ViewPort viewport;
		} 
		
		public List<AddressComponent> address_components;
		public String formatted_address;
		public Geometry geometry;
		public List<String> types;
		public boolean partial_match;
	}
	
	public List<Result> results;
	public String status;
	public String error_message;
}
