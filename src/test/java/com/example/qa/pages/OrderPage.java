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
    private final By locator = By.className("select-search__select");
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
    private final By modalWindow = By.xpath("//div[contains(@class, 'Order_Modal__YZ-d3')]");

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

    //изменила метод выбора станции метро
    private void selectMetroStation(String metro) {
        driver.findElement(metroInput).sendKeys(metro);
        driver.findElement(locator).click();
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

    //здесь у меня тоже возникала ошибка, упростила метод
    public void confirmOrder() {
        driver.findElement(confirmButton).click();
    }

    //при проверки теперь ожидаю не само сообщение, а просто модельное окно, что заказ оформлен
    public boolean isSuccessMessageDisplayed() {
        WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(modalWindow));
        return successElement.isDisplayed();
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