package utils;

import controllers.auth.IAuthController;
import factory.ControllerFactory;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    private static final IAuthController authController = ControllerFactory.getAuthController();

    public static boolean isUserAuthenticated(HttpSession session) {
        if (session == null) return false;

        String token = (String) session.getAttribute("jwt");
        return token != null && authController.isAuthenticated(token);
    }

    public static String getNickname(HttpSession session) {
        if (session == null) return null;

        String nickname = (String) session.getAttribute("nickname");
        if (nickname != null && !nickname.isBlank()) return nickname;

        String token = (String) session.getAttribute("jwt");
        if (token != null && authController.isAuthenticated(token)) {
            nickname = authController.getNicknameFromToken(token);
            session.setAttribute("nickname", nickname);
            return nickname;
        }

        return null;
    }
}
