package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static ExtentTest test;

    public static void initExtentReports() {
        if (extent == null) {
            // Create extent-reports directory if it doesn't exist
            String reportDir = "reports/extent-reports/";
            try {
                Files.createDirectories(Paths.get(reportDir));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportDir + "ExtentReport.html");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Amazon Search Test Report");
            spark.config().setReportName("Amazon Search Functionality Test");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    public static void startTest(String testName) {
        test = extent.createTest(testName);
    }

    public static void logInfo(String message) {
        test.info(message);
    }

    public static void logPass(String message, String screenshotPath) {
        if (screenshotPath != null) {
            test.pass(message).addScreenCaptureFromPath(screenshotPath);
        } else {
            test.pass(message);
        }
    }

    public static void logFail(String message, String screenshotPath, Throwable throwable) {
        if (screenshotPath != null) {
            test.fail(message + "<br>Exception: " + throwable.getMessage()).addScreenCaptureFromPath(screenshotPath);
        } else {
            test.fail(message + "<br>Exception: " + throwable.getMessage());
        }
    }

    public static void endTest() {
        extent.flush();
    }
}