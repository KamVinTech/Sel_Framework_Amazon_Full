package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.ConfigReader;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = ConfigReader.getIntValue("max.retry.count", 2);

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            logger.info("Retrying test " + result.getName() + " for " + retryCount + " time");
            return true;
        }
        logger.info("Test " + result.getName() + " has reached maximum retry count: " + MAX_RETRY_COUNT);
        return false;
    }
}
