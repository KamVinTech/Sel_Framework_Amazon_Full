package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = ConfigReader.getValue("screenshot.path", "screenshots");

    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            createScreenshotDirectory();

            // Generate timestamp for unique file name
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = String.format("%s_%s.png", testName, timestamp);
            String filePath = Paths.get(SCREENSHOT_DIR, fileName).toString();

            // Capture screenshot
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshotFile.toPath(), Paths.get(filePath));

            logger.info("Screenshot captured: " + filePath);
            return filePath;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        }
    }

    private static void createScreenshotDirectory() {
        try {
            Path dirPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.info("Created screenshots directory: " + SCREENSHOT_DIR);
            }
        } catch (Exception e) {
            logger.error("Failed to create screenshots directory: " + e.getMessage(), e);
        }
    }
}
