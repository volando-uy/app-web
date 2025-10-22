

package servlets.packageservlet;

import controllers.buypackage.IBuyPackageController;
import controllers.flightroutepackage.IFlightRoutePackageController;

import domain.dtos.buypackage.BaseBuyPackageDTO;
import domain.dtos.flightroutepackage.BaseFlightRoutePackageDTO;

import factory.ControllerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@WebServlet("/package/buypackage")
public class BuyPackageServlet extends HttpServlet {

    private final IBuyPackageController buyCtrl =
            ControllerFactory.getBuyPackageController();
    private final IFlightRoutePackageController pkgCtrl =
            ControllerFactory.getFlightRoutePackageController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setupEncoding(resp, req);

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nickname");

        if (nick == null || nick.isBlank()) {
            String current = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
            req.getSession(true).setAttribute("redirectAfterLogin", current);
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        String pkgName = trim(req.getParameter("pkg"));
        if (pkgName == null || pkgName.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/packages/list");
            return;
        }

        BaseFlightRoutePackageDTO pkg = null;
        try { pkg = pkgCtrl.getFlightRoutePackageSimpleDetailsByName(pkgName); }
        catch (Exception ignored) {}

        if (pkg == null) {
            toast(req, "Paquete no encontrado", "error");
            resp.sendRedirect(req.getContextPath() + "/packages/list");
            return;
        }

        LocalDate purchaseDate = LocalDate.now(ZoneId.systemDefault());
        LocalDate expiry       = purchaseDate.plusDays(Math.max(0, pkg.getValidityPeriodDays()));
        ZoneId zone = ZoneId.systemDefault();
        Date purchaseLegacy = Date.from(purchaseDate.atStartOfDay(zone).toInstant());
        Date expiryLegacy   = Date.from(expiry.atStartOfDay(zone).toInstant());

        req.setAttribute("pkg", pkg);
        req.setAttribute("purchaseDateLegacy", purchaseLegacy);
        req.setAttribute("expiryDateLegacy", expiryLegacy);
        req.setAttribute("customerNickname", nick);

        req.getRequestDispatcher("/src/views/buypackage/buypackage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setupEncoding(resp, req);

        HttpSession s = req.getSession(false);
        String nick = (s == null) ? null : (String) s.getAttribute("nickname");
        String pkgName = trim(req.getParameter("pkgName"));

        if (nick == null || nick.isBlank()) {
            String target = req.getContextPath() + "/package/buypackage" +
                    (pkgName != null && !pkgName.isBlank() ? "?pkg=" + url(pkgName) : "");
            req.getSession(true).setAttribute("redirectAfterLogin", target);
            resp.sendRedirect(req.getContextPath() + "/users/login");
            return;
        }

        if (pkgName == null || pkgName.isBlank()) {
            toast(req, "Falta paquete", "error");
            resp.sendRedirect(req.getContextPath() + "/packages/list");
            return;
        }

        try {
            BaseBuyPackageDTO purchase = buyCtrl.createBuyPackage(nick, pkgName);
            toast(req, "Compra realizada (ID " + (purchase != null ? purchase.getId() : "-") + ")", "success");
            resp.sendRedirect(req.getContextPath() + "/perfil");

        } catch (Exception e) {
            // No adivinar por texto. Mostrar el mensaje real si existe.
            getServletContext().log("createBuyPackage error", e);
            String raw = e.getMessage();
            String msg = (raw == null || raw.isBlank()) ? "No se pudo registrar la compra." : raw.trim();

            // Volver a la pantalla de confirmación, no al listado
            toast(req, msg, "warning");
            resp.sendRedirect(req.getContextPath() + "/packages/list");
        }
    }

    private static void setupEncoding(HttpServletResponse resp , HttpServletRequest req) {
        try { req.setCharacterEncoding("UTF-8"); } catch (Exception ignored) {}
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
    }
    private static String trim(String s) { return (s == null) ? null : s.trim(); }
    private static String url(String s){
        try { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }
    private static void toast(HttpServletRequest req, String msg, String type) {
        HttpSession session = req.getSession();
        session.setAttribute("toastMessage", msg);
        session.setAttribute("toastType", type);
    }
}
