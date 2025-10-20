package utils;


import java.io.File;

public class ImageFunctions {


    public static String getImage(String relativePath) {
        File tomcatRoot = PathResolver.getTomcatRootFromExecution();
        if (tomcatRoot == null || relativePath == null) return "";

        File imageFile = new File(tomcatRoot, relativePath);
        return imageFile.getAbsolutePath();
    }
}
