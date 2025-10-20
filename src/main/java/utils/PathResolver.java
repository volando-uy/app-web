package utils;

import java.io.File;

public class PathResolver {

    public static File getTomcatBaseFromWebapps(String realPathFromServletContext) {
        File webapps = new File(realPathFromServletContext).getParentFile();     // .../webapps
        return webapps.getParentFile();                                          // .../tomcat10x
    }

    public static File resolveUserImagePath(String realPath, String relativePath) {
        File tomcatBase = getTomcatBaseFromWebapps(realPath);
        return new File(tomcatBase, relativePath);
    }
    /**
     * Devuelve el path absoluto al directorio base del Tomcat,
     * como: C:/.../tomcat10x/
     */

    public static File getTomcatBase(String realPathFromServletContext) {
        File webapps = new File(realPathFromServletContext).getParentFile(); // .../webapps
        return webapps.getParentFile(); // .../tomcat10x
    }

    /**
     * Devuelve el path absoluto de la imagen del usuario.
     * @param realPathFromServletContext getServletContext().getRealPath("/")
     * @param relativeImagePath el valor de usuario.getImage() (ej. "images/users/customers/nickname.jpg")
     */
    public static File resolveUserImage(String realPathFromServletContext, String relativeImagePath) {
        File tomcatBase = getTomcatBase(realPathFromServletContext);
        return new File(tomcatBase, relativeImagePath);
    }
    public static File getTomcatRootFromExecution() {
        // Esto obtiene algo como:
        // .../target/cargo/configurations/tomcat10x/webapps/app-web-jsp/WEB-INF/classes
        String path = PathResolver.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        File current = new File(path).getAbsoluteFile();

        // Subir hasta encontrar "webapps"
        while (current != null && !new File(current, "webapps").exists()) {
            current = current.getParentFile();
        }

        return current; // debería ser .../tomcat10x
    }
}
