package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timezoneParam = req.getParameter("timezone");

        if (timezoneParam == null) {
            timezoneParam = "Etc/GMT";
        } else {
            timezoneParam = timezoneParam.replace("UTC+", "Etc/GMT-").replace("UTC-", "Etc/GMT+");
        }

        ZoneId zoneId = ZoneId.of(timezoneParam);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
        String formattedTime = currentTime.format(formatter).replace("GMT", "UTC");

        resp.setContentType("text/html; charset=utf-8");
        resp.getWriter().println("<html><body>");
        resp.getWriter().println("Current Time: " + formattedTime);
        resp.getWriter().println("</body></html>");
        resp.getWriter().close();
    }
}