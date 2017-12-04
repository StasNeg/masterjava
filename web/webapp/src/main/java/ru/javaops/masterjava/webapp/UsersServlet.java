package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableMap;
import org.thymeleaf.context.WebContext;

import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.DBIProvider;
import ru.javaops.masterjava.persist.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.javaops.masterjava.web.ThymeleafListener.engine;

@WebServlet("")
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);
    private boolean [] sendEmail;
    private Emails emails;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userDao.getWithLimit(20);
        sendEmail =  new boolean[users.size()];
        sendEmail[0] = true;
        sendEmail[3] = true;
        emails = new Emails(sendEmail);
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20),
                        "emails", emails));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        List<Emails> users= (List<Emails>) req.getAttribute("emails");

    }
}
