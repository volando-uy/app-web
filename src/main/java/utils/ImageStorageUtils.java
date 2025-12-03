package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

public class ImageStorageUtils {

    /**
     * Guarda la imagen en una carpeta relativa al contexto del servidor (ej: "images/users/customers").
     *
     * @param context     ServletContext para obtener el realPath
     * @param imagePart   La parte del formulario con la imagen
     * @param relativeDir Carpeta relativa donde guardar (ej: "images/users/customers")
     * @param fileName    Nombre final del archivo (sin extensión)
     * @return File guardado
     */
    public static File saveImage(ServletContext context, Part imagePart, String relativeOrAbsoluteDir, String fileName) throws IOException {
        File imageDir;

        File dir = new File(relativeOrAbsoluteDir);

        // ✅ Verificar si es una ruta absoluta o relativa
        if (dir.isAbsolute()) {
            imageDir = dir;
        } else {
            String realPath = context.getRealPath("/"); // webapps/app-web-jsp/
            imageDir = new File(realPath, relativeOrAbsoluteDir);
        }

        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        String extension = getExtension(imagePart.getSubmittedFileName());

        // 💡 Sanear el nombre del archivo (sin espacios ni caracteres raros)
        String safeFileName = fileName.replaceAll("[^a-zA-Z0-9-_]", "_");

        File finalImage = new File(imageDir, safeFileName + extension);

        System.out.println("📁 Guardando imagen en: " + finalImage.getAbsolutePath());

        imagePart.write(finalImage.getAbsolutePath());

        return finalImage;
    }


    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf(".");
        return (dot >= 0) ? fileName.substring(dot) : "";
    }

    public static void deleteImage(File finalImage) {
        if (finalImage != null && finalImage.exists()) {
            System.out.println("🗑️ Borrando imagen temporal: " + finalImage.getAbsolutePath());
            if (finalImage.delete()) {
                System.out.println("🗑 Imagen temporal borrada...");
            } else {
                System.out.println("⚠️ No se pudo borrar la imagen temporal...");
            }
        }
    }
}
