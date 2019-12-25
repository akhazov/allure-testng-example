package data;

/**
 * Конфигурация для aShot
 */
public class AshotConfig {

    private AshotConfig(){}

    public static final boolean ENABLE_SCREENSHOT_VERIFICATION = false;
    public static final String PATH_TO_EXPECTED_SCREENSHOTS = "src/test/resources/screenshots/";
    public static final String PATH_TO_DIFF_SCREENSHOTS = "src/test/resources/screenshots/diff/";
}
