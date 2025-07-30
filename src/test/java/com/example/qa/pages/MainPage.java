package com.example.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainPage {
    private final WebDriver driver;

    private final By orderButtonTop = By.xpath(".//button[@class='Button_Button__ra12g']");
    private final By orderButtonBottom = By.xpath(".//button[@class='Button_Button__ra12g Button_Middle__1CSJM']");
    private final By cookieButton = By.id("rcc-confirm-button");
    private final By accordionItems = By.xpath(".//div[@class='accordion__item']");
    private final By accordionPanel = By.xpath(".//div[@class='accordion__panel']");

    public static final String MAIN_URL = "https://qa-scooter.praktikum-services.ru/";

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickOrderButtonTop() {
        driver.findElement(orderButtonTop).click();
    }

    public void clickOrderButtonBottom() {
        driver.findElement(orderButtonBottom).click();
    }

    public void acceptCookies() {
        driver.findElement(cookieButton).click();
    }

    public void clickAccordionItem(int index) {
        driver.findElements(accordionItems).get(index).click();
    }

    public String getAccordionItemText(int index) {
        return driver.findElements(accordionItems).get(index)
                .findElement(accordionPanel).getText();
    }
}

