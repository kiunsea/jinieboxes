package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/keycloak")
public class KeyCloakServlet extends HttpServlet {
    
    public void init() {;}

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        // request parameter 로깅
        String pName = null;
        String pValue = null;
        Enumeration<String> enums = req.getParameterNames();
        while (enums.hasMoreElements()) {
            pName = enums.nextElement().toString();
            pValue = req.getParameter(pName);
            out.println("<p>(param) " + pName + " - " + pValue + "</p>");
//            System.out.println("<p>(param) " + pName + " - " + pValue + "</p>");
        }
        out.close();
    }
}
