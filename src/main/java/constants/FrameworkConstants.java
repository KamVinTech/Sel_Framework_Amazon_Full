package constants;

public final class FrameworkConstants {
    private FrameworkConstants() {
        // Private constructor to prevent instantiation
    }

    // Timeouts
    public static final int EXPLICIT_WAIT = 10;
    public static final int IMPLICIT_WAIT = 10;
    public static final int PAGE_LOAD_TIMEOUT = 30;
    public static final int SCRIPT_TIMEOUT = 20;

    // File Paths
    public static final String RESOURCES_PATH = "src/test/resources/";
    public static final String CONFIG_FILE_PATH = RESOURCES_PATH + "config/config.properties";
    public static final String TEST_DATA_PATH = RESOURCES_PATH + "testdata/";
    public static final String DOWNLOAD_PATH = "downloads/";

    // Report Constants
    public static final String EXTENT_REPORT_FOLDER = "reports/";
    public static final String EXTENT_REPORT_NAME = "AutomationReport.html";
    public static final String SCREENSHOT_FOLDER = "screenshots/";
    
    // Common Strings
    public static final String PASS = "PASS";
    public static final String FAIL = "FAIL";
    public static final String SKIP = "SKIP";
    
    // Browser Options
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String EDGE = "edge";
    
    // Test Data
    public static final String DEFAULT_USERNAME = "testuser";
    public static final String DEFAULT_PASSWORD = "testpass";
}
