/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import dao.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ecolak
 */
public class AuthenticationFilter implements Filter {

    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            boolean authorized = false;
            if (request instanceof HttpServletRequest) {
                HttpSession session = ((HttpServletRequest) request).getSession(false);
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    if (user != null) {
                        authorized = true;
                    }
                }
            }

            if (authorized) {
                chain.doFilter(request, response);
            } else if (filterConfig != null) {
                ServletContext context = filterConfig.getServletContext();
                String loginPage = context.getInitParameter("loginPage");

                if (loginPage != null && !"".equals(loginPage)) {
                    HttpSession session = ((HttpServletRequest) request).getSession();
                    String redirectUrl = ((HttpServletRequest) request).getRequestURI();
                    String queryString = ((HttpServletRequest) request).getQueryString();
                    if(queryString != null && queryString.length() > 0)
                        redirectUrl += "?" + queryString;
                    session.setAttribute("redirectUrl", redirectUrl);
                    ((HttpServletResponse)response).sendRedirect(loginPage);
                    //context.getRequestDispatcher(loginPage).forward(request, response);
                }
            } else {
                throw new ServletException("Unauthorized access, unable to forward to login page");
            }
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("IOException raised in AuthenticationFilter");
        } catch (ServletException se) {
            se.printStackTrace();
            System.out.println("ServletException raised in AuthenticationFilter");
        }
    }

}
