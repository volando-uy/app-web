<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="domain.dtos.user.UserDTO" %>
<%@ page import="java.util.List" %>
<html>
<head>
  <title>Lista de Usuarios</title>
</head>
<body>
<h2>Usuarios registrados</h2>
<ul>
  <%
    List<UserDTO> users = (List<UserDTO>) request.getAttribute("users");
    if (users != null) {
      for (UserDTO u : users) {
  %>
  <li><%= u.getNickname() %> — <%= u.getMail() %></li>
  <%
    }
  } else {
  %>
  <li>No hay usuarios para mostrar.</li>
  <%
    }
  %>
</ul>
</body>
</html>
