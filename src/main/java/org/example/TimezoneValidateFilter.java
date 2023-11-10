package org.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Set;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String timezoneParam = request.getParameter("timezone");

        if (timezoneParam == null || isValidTimezone(timezoneParam)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid timezone");
            response.getWriter().println("Please write zone in valid format");
            response.getWriter().close();
        }
    }

    private boolean isValidTimezone(String timezone) {
        String zone = timezone.replace("UTC+", "Etc/GMT-").replace("UTC-", "Etc/GMT+");
        Set<String> availableTimezones = ZoneId.getAvailableZoneIds();

        return availableTimezones.contains(zone);
    }
}