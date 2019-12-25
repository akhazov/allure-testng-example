package tools;

import data.AshotConfig;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Хелпер для работы со скриншотами элементов и страниц
 */
public class LayoutControl {

    private LayoutControl(){}

    private static Screenshot actualScreenshot;
    private static Screenshot expectedScreenshot;
    private static final Logger LOG = LoggerFactory.getLogger(LayoutControl.class);

    /**
     * Метод создает скриншот страницы или элемента и сохраняет его под указанным именем
     *
     * @param fileName   Имя файла скриншота
     * @param webDriver  Web драйвер
     * @param webElement Web элемент
     */
    private static void createActualScreenshot(String fileName, WebDriver webDriver, WebElement webElement) {
        webDriver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        if (webElement==null){
            actualScreenshot = new AShot()
//                    .ignoredElements(IgnoredElements.getIgnoredElements())
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(webDriver);
        }
        else {
            actualScreenshot = new AShot()
//                    .ignoredElements(IgnoredElements.getIgnoredElements())
                    .coordsProvider(new WebDriverCoordsProvider())
                    .takeScreenshot(webDriver,webElement);
        }
        saveCurrentScreenshotAsReference(actualScreenshot, fileName);
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    /**
     * Метод создания эталонных скриншотов.
     * Если файла со скрином не существует, то вместо него записывается актуальный скрин
     *
     * @param screenshot Скриншот
     * @param fileName   Имя файла
     */
    private static void saveCurrentScreenshotAsReference(Screenshot screenshot, String fileName) {
        File expectedFile = new File(AshotConfig.PATH_TO_EXPECTED_SCREENSHOTS + fileName);

        if (!expectedFile.exists()) {
            saveImageToFile(screenshot.getImage(), expectedFile);
        }
    }

    /**
     * Получение ожидаемого скриншота
     *
     * @param fileName Имя файла
     */
    @Step("Сверка с эталонным макетом {fileName}")
    private static void getExpectedScreenshot(String fileName) {
        File expectedFile = new File(AshotConfig.PATH_TO_EXPECTED_SCREENSHOTS + fileName);
        try {
            expectedScreenshot = new Screenshot(ImageIO.read(expectedFile));
            expectedScreenshot.setIgnoredAreas(actualScreenshot.getIgnoredAreas());
        }
        catch (IOException e) {
            LOG.error("Error reading image file" + expectedFile, e);
        }
    }

    /**
     * Метод сравнения скриншотов
     * Если в метод передается элемент, то сравнивается скрин только этого элемента
     * Допускается разница в скриншотах до 16 пикселей (мигающий курсор)
     *
     * @param fileName   Имя файла скриншота
     * @param webElement Web элемент
     * @param webDriver  Web драйвер
     * @return true - если скриншоты совпадают
     *         false - если есть отличия
     */
    public static boolean compareCurrentPageWithExpected(String fileName, WebElement webElement, WebDriver webDriver) {
        wait(1500);
        createActualScreenshot(fileName, webDriver, webElement);
        getExpectedScreenshot(fileName);

        ImageDiff makeDiff = new ImageDiffer()
                .makeDiff(actualScreenshot, expectedScreenshot)
                .withDiffSizeTrigger(20);

//        if(makeDiff.hasDiff()) {
            AllureReport.attachDiffScreen(actualScreenshot, expectedScreenshot, makeDiff);
            File diffFile = new File(AshotConfig.PATH_TO_DIFF_SCREENSHOTS + fileName);
            saveImageToFile(makeDiff.getMarkedImage(),diffFile);
//        }

        if(AshotConfig.ENABLE_SCREENSHOT_VERIFICATION) {
            return !makeDiff.hasDiff();
        } else {
            return true;
        }
    }

    /**
     * Перегруженный метод, сравнивается скрин всей страницы
     *
     * @param fileName  Имя файла скриншота на основе имени тестового класса
     * @param webDriver Web драйвер
     * @return true - если скриншоты совпадают
     *         false - если есть отличия
     */
    public static boolean compareCurrentPageWithExpected(String fileName, WebDriver webDriver) {
        return compareCurrentPageWithExpected(fileName, null,  webDriver);
    }

    /**
     * Проверка пути файла.
     * Если путь не существует, то создается иерархия каталогов согласно указанному пути
     *
     * @param file Файл
     */
    private static void checkFileThePath (File file) {
        if (file.getParentFile() != null && !(file.getParentFile().mkdirs())) {
            LOG.error("Impossible to create a path");
        }
    }

    /**
     * Удаление директории
     *
     * @param path Путь к папке
     */
    public static void removeDirectory(String path) {
        File directory = new File(path);
        try {
            FileUtils.cleanDirectory(directory);
        }
        catch (IOException e) {
            LOG.error("I can not delete the directory", e);
        }
    }

    /**
     * Сохранение скриншота
     *
     * @param bufferedImage Скриншот
     * @param imageFile  Файл, в который нужно сохранить скрин
     */
    private static void saveImageToFile(BufferedImage bufferedImage, File imageFile) {
        checkFileThePath(imageFile);

        try {
            ImageIO.write(bufferedImage, "png", imageFile);
        }
        catch (IOException e) {
            LOG.error("Error writing image file" + imageFile, e);
        }
    }

    /**
     * Пауза, ожидание на случай анимации.
     *
     * @param millis Миллисекунды
     */
    public static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {//NOSONAR
            LOG.error("Interrupted!", e);
        }
    }
}
