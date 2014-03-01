package self.ec.btcbots.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import self.ec.btcbots.entity.User;
import self.ec.btcbots.util.AuthUtils;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
	 
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	private static class RouteRequest {
		private String routePattern;
		private String[] methods;
		
		public RouteRequest(String routePattern, String[] methods) {
			this.routePattern = routePattern;
			this.methods = methods;
		}

		public String getRoutePattern() {
			return routePattern;
		}

		public String[] getMethods() {
			return methods;
		}		
	}
	
	private static final String[] GET_POST_PUT_DELETE = new String[]{"GET", "POST", "PUT", "DELETE"};
	private static final List<RouteRequest> authRoutes = new ArrayList<RouteRequest>();
	private static final List<RouteRequest> adminRoutes = new ArrayList<RouteRequest>();
	static {
		authRoutes.add(new RouteRequest("/session/user", new String[]{"GET"}));
		authRoutes.add(new RouteRequest("/bots.*", GET_POST_PUT_DELETE));
		authRoutes.add(new RouteRequest("/accounts.*", GET_POST_PUT_DELETE));
		authRoutes.add(new RouteRequest("/transactions.*", GET_POST_PUT_DELETE));
		
		adminRoutes.add(new RouteRequest("/admin.*", GET_POST_PUT_DELETE));
	}
	
	@Context
	HttpServletRequest request;
	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	System.out.println("Request coming from " + requestContext.getUriInfo().getPath());
        System.out.println(request.getMethod() + " " + request.getRequestURI());
        
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();
        User userInSession = AuthUtils.getUserInSession(request);
        for (RouteRequest rr : authRoutes) {
        	if (path.matches(rr.getRoutePattern()) && stringArrayContains(rr.getMethods(), method) && userInSession == null) {
        		throw new WebApplicationException(Status.UNAUTHORIZED);
        	}
        }
        
        for (RouteRequest rr : adminRoutes) {
        	if (path.matches(rr.getRoutePattern()) && stringArrayContains(rr.getMethods(), method)) {
        		if (userInSession == null) 	
        			throw new WebApplicationException(Status.UNAUTHORIZED);
        		else if (!userInSession.isAdmin()) {
        			throw new WebApplicationException(Status.FORBIDDEN);
        		}
        	}
        }
    }
    
    private boolean stringArrayContains(String[] arr, String search) {
    	for (String s : arr) {
    		if (s.equals(search))
    			return true;
    	}
    	return false;
    }
}
