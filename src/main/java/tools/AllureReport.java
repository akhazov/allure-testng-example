package tools;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Хелпер для создания элементов отчета
 */
class AllureReport {

	private AllureReport() {
	}

	/**
	 *  Прикрепить скриншоты к отчету.
	 *  Задействован Allure screen-diff-plugin
	 *
	 * @param actual   Актуальный скриншот
	 * @param expected Ожидаемый скриншот
	 * @param diff	   Изображение после сравнения
	 */
	static void attachDiffScreen(final Screenshot actual, final Screenshot expected, ImageDiff diff) {
		Allure.label("testType", "screenshotDiff");
		attachScreenshot("actual", actual.getImage());
		attachScreenshot("expected", expected.getImage());
		attachScreenshot("diff", diff.getMarkedImage());
	}

	/**
	 * Привязать скриншот к отчету
	 *
	 * @param name 			Имя скриншота
	 * @param bufferedImage Изображение
	 * @return 				Стрим из байт массива
	 */
	@Attachment(value = "{name}", type = "image/png")
	private static byte [] attachScreenshot(final String name, final BufferedImage bufferedImage) {
		return toByteArray(bufferedImage);
	}

	/**
	 * Привязать скриншот к отчету
	 *
	 * @param name		Имя скриншота
	 * @param webDriver Web драйвер
	 * @return			Стрим из байт массива
	 */
	@Attachment(value = "{name}", type = "image/png")
	static byte [] attachScreenshot(final String name, final WebDriver webDriver) {
		return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
	}

	/**
	 * Перевести буфферизированное изображение в байтовый массив
	 * Требуется для приклепления изображения к отчету Allure
	 *
	 * @param bufferedImage Изображение
	 * @return 				Стрим из байт массива
	 */
	private static byte [] toByteArray(final BufferedImage bufferedImage) {
		try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "png", outputStream);
			return outputStream.toByteArray();
		} catch (IOException ignore) {
			return new byte[0];
		}
	}
}
