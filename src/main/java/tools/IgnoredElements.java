package my.tools;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Хелпер, скрывает элементы инферфейса.
 * Используется для скрытия элементов, которые меняются в тестах.
 * (Например поле с временем создания активности)
 */
class IgnoredElements {
	private IgnoredElements() {
	}

	/**
	 * Список CSS локаторов элементов, которые нужно скрыть
	 *
	 * @return Массив локаторов.
	 */
	private static String[] setIgnoredElements() {
		return new String[] {
				".date",
				".activity-info__date",
				".dialog-parameter__value",
				".message__status",
				".contact-item__info"
		};
	}

	/**
	 * Множество из элементов для игнорирования динамических эелементов в aShot
	 *
	 * @return Множество элементов
	 */
	static Set<By> getIgnoredElements() {
		Set<By> ignoredElements = new HashSet<>();
		for (String cssSelector:setIgnoredElements()) {
			ignoredElements.add(By.cssSelector(cssSelector));
		}
 		return ignoredElements;
	}

	/**
	 * Скрытие элементов с помощью JS
	 *
	 * @param webDriver Web драйвер
	 */
	static void hide(WebDriver webDriver) {
		webDriver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
		for (String cssSelector : setIgnoredElements()) {
			List<WebElement> elementsList = webDriver.findElements(By.cssSelector(cssSelector));
			for (WebElement webElement : elementsList) {
				((JavascriptExecutor) webDriver).executeScript("arguments[0].remove();", webElement);
			}
			elementsList.clear();
		}
		webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
}
