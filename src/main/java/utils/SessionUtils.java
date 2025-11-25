package utils;

import com.labpa.appweb.auth.AuthSoapAdapter;
import com.labpa.appweb.auth.AuthSoapAdapterService;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

//    private static final IAuthController authController = ControllerFactory.getAuthController();
    private static final AuthSoapAdapter authSoapAdapter = new AuthSoapAdapterService().getAuthSoapAdapterPort();

    public static boolean isUserAuthenticated(HttpSession session) {
        if (session == null) return false;

        String token = (String) session.getAttribute("jwt");
        return token != null && authSoapAdapter.isAuthenticated(token);
    }

    public static String getNickname(HttpSession session) {
        if (session == null) return null;

        String nickname = (String) session.getAttribute("nickname");
        if (nickname != null && !nickname.isBlank()) return nickname;

        String token = (String) session.getAttribute("jwt");
        if (token != null && authSoapAdapter.isAuthenticated(token)) {
            nickname = authSoapAdapter.getNicknameFromToken(token);
            session.setAttribute("nickname", nickname);
            return nickname;
        }

        return null;
    }


    public static boolean isAirline(HttpSession session) {
        if (session == null) return false;

        String token = (String) session.getAttribute("jwt");
        return token != null && authSoapAdapter.isAirline(token);
    }

    public static boolean isCustomer(HttpSession session) {
        if (session == null) return false;

        String token = (String) session.getAttribute("jwt");
        return token != null && authSoapAdapter.isCustomer(token);
    }
}
