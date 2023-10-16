package util;

public class PathUtils {

    private static final String PATH = "D:\\java代码\\IdeaProjects\\final project\\images\\";

    public static String getRealPath(String relativePath) {
        return PATH + relativePath;
    }
}
