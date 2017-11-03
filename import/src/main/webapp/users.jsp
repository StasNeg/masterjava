<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>User list</title>
</head>
<body>
<section>
    <h3><a href="import.jsp">Import</a></h3>
    <h2>Users</h2>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Flag</th>
        </tr>
        </thead>
        <c:forEach items="${users}" var="user">
            <tr>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.flag}</td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>