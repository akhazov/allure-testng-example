package my.company;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;
import tools.LayoutControl;

import java.util.concurrent.TimeUnit;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertTrue;

/**
 * @author baev (Dmitry Baev)
 */
public class SimpleTest {

    @Test
    public void simpleTest() {
        step("step 1");
        step("step 2");
        WebDriverManager.firefoxdriver().setup();
//        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        WebDriver webDriver = new FirefoxDriver();
        webDriver.manage().window().setSize(new Dimension(1366,768));
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        webDriver.get("http://ya.ru");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(webDriver.getTitle().contains("Яндекс"));
        LayoutControl.compareCurrentPageWithExpected("screenshot.png", webDriver);
        LayoutControl.compareCurrentPageWithExpected("screenshot1.png", webDriver);
        LayoutControl.compareCurrentPageWithExpected("screenshot2.png", webDriver);
        webDriver.close();
    }
}

