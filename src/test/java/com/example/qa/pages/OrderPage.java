package com.example.qa.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class OrderPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы для первой страницы заказа
    private final By nameInput = By.xpath(".//input[@placeholder='* Имя']");
    private final By surnameInput = By.xpath(".//input[@placeholder='* Фамилия']");
    private final By addressInput = By.xpath(".//input[@placeholder='* Адрес: куда привезти заказ']");
    private final By metroInput = By.xpath(".//input[@placeholder='* Станция метро']");
    private final By metroDropdown = By.className("select-search__select");
    private final By phoneInput = By.xpath(".//input[@placeholder='* Телефон: на него позвонит курьер']");
    private final By nextButton = By.xpath(".//button[text()='Далее']");

    // Локаторы для второй страницы заказа
    private final By dateInput = By.xpath(".//input[@placeholder='* Когда привезти самокат']");
    private final By rentalPeriod = By.className("Dropdown-placeholder");
    private final By colorBlack = By.id("black");
    private final By colorGrey = By.id("grey");
    private final By commentInput = By.xpath(".//input[@placeholder='Комментарий для курьера']");
    private final By orderButton = By.xpath(".//button[contains(@class, 'Button_Middle') and text()='Заказать']");
    private final By confirmButton = By.xpath("//button[contains(@class, 'Button_Button__ra12g') and text()='Да']");
    private final By successMessage = By.xpath("//div[contains(@class, 'Order_ModalHeader__3FDaJ') and contains(., 'Заказ оформлен')]");
    private final By modalWindow = By.xpath("//div[contains(@class, 'Order_Modal__')]");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void fillFirstPage(String name, String surname, String address, String metro, String phone) {
        try {
            // Заполнение основных полей с явными ожиданиями
            waitAndSendKeys(nameInput, name);
            waitAndSendKeys(surnameInput, surname);
            waitAndSendKeys(addressInput, address);

            selectMetroStation(metro);

            waitAndSendKeys(phoneInput, phone);
            waitAndClick(nextButton);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при заполнении первой страницы заказа", e);
        }
    }

    private void selectMetroStation(String metro) {
        try {
            WebElement metroField = wait.until(ExpectedConditions.elementToBeClickable(metroInput));
            metroField.click();
            metroField.clear();
            metroField.sendKeys(metro.replace("●", "").trim());

            // Ждем появления dropdown и кликаем по варианту
            By metroOption = By.xpath(String.format(
                    "//div[contains(@class, 'select-search__row')]//*[contains(text(), '%s')]",
                    metro.replace("●", "").trim()
            ));

            WebElement stationOption = wait.until(ExpectedConditions.elementToBeClickable(metroOption));
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", stationOption);
            stationOption.click();

            // Дополнительная проверка, что станция выбрана
            wait.until(ExpectedConditions.attributeContains(metroInput, "value", metro.replace("●", "").trim()));
        } catch (Exception e) {
            // Если не получилось выше, то по другому пробую
            new Actions(driver)
                    .sendKeys(Keys.ESCAPE)
                    .sendKeys(Keys.TAB)
                    .perform();

            WebElement metroField = wait.until(ExpectedConditions.elementToBeClickable(metroInput));
            metroField.sendKeys(metro.replace("●", "").trim());
            metroField.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
        }
    }

    public void fillSecondPage(String date, String period, String color, String comment) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dateInput));

            // Заполнение даты
            WebElement dateField = driver.findElement(dateInput);
            dateField.sendKeys(date);
            dateField.sendKeys(Keys.ENTER);

            // Выбор периода аренды
            waitAndClick(rentalPeriod);
            By periodOption = By.xpath(String.format(".//div[@class='Dropdown-option' and text()='%s']", period));
            waitAndClick(periodOption);

            // Выбор цвета
            if ("black".equalsIgnoreCase(color)) {
                waitAndClick(colorBlack);
            } else if ("grey".equalsIgnoreCase(color)) {
                waitAndClick(colorGrey);
            }

            // Комментарий
            if (comment != null && !comment.isEmpty()) {
                waitAndSendKeys(commentInput, comment);
            }

            waitAndClick(orderButton);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при заполнении второй страницы заказа", e);
        }
    }

    public void confirmOrder() {
        try {
            // 1. Ожидаем появление модального окна подтверждения заказа
            WebElement confirmationModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    modalWindow));

            // 2. Кликаем кнопку "Да" с использованием JavaScript
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    confirmButton));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);

            // 3. Ожидаем либо успешное сообщение, либо исчезновение модального окна подтверждения
            try {
                // Вариант 1: Ждем сообщение об успехе
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                successMessage));
            } catch (TimeoutException e) {
                // Вариант 2: Если сообщение не появилось, проверяем что модальное окно исчезло
                wait.until(ExpectedConditions.invisibilityOf(confirmationModal));

                // Вариант 3: Проверяем URL на случай редиректа
                if (!driver.getCurrentUrl().contains("order")) {
                    return; // Если URL изменился, считаем что заказ оформлен
                }

                throw new RuntimeException("Не удалось подтвердить заказ: ни сообщение об успехе, ни изменение URL не обнаружены");
            }

        } catch (Exception e) {
            System.err.println("Ошибка при подтверждении заказа. Текущий URL: " + driver.getCurrentUrl());
            System.err.println("Содержимое страницы:\n" +
                    driver.findElement(By.tagName("body")).getText());
            throw new RuntimeException("Ошибка при подтверждении заказа", e);
        }
    }


    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return successElement.isDisplayed();
        } catch (TimeoutException e) {

            System.out.println("Сообщение об успехе не найдено. Текущий URL: " + driver.getCurrentUrl());
            System.out.println("Page source: " + driver.getPageSource().substring(0, 1000) + "...");
            return false;
        }
    }

    private void waitAndClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
    }

    private void waitAndSendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
    }
}