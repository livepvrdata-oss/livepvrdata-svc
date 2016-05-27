package com.github.livepvrdata.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.livepvrdata.DataStore;
import com.github.livepvrdata.dao.EventParticipantMap;

@WebServlet(urlPatterns={"/overrides"})
public class OverridesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        try(PrintWriter out = new PrintWriter(resp.getOutputStream())) {

            String name = req.getParameter("name");
            if(name != null) {
                EventParticipantMap map = DataStore.getInstance().getAlternatives(name);
                map.getAlternatives();

                out.print("Alternatives for '");
                out.print(map.getName());
                out.println("'");
                out.println();

                for(String alt : map.getAlternatives()) {
                    out.println(alt);
                }

            } else {
                out.println("Parameter 'name' required");
            }
        }
    }
}
