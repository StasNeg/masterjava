package ru.javaops.masterjava.importhttp;


import ru.javaops.masterjava.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@MultipartConfig
public class ExportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/import.jsp");
        } else if ("upload".equals(action)) {
            Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
            InputStream fileContent = filePart.getInputStream();
            request.setAttribute("users", UserService.getUsers(fileContent));
            request.getRequestDispatcher("/users.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        request.getRequestDispatcher("/import.jsp").forward(request, response);

    }
}
