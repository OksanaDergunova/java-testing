package com.example.qa.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.example.qa.pages.MainPage;
import com.example.qa.pages.OrderPage;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class OrderTest {
    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;

    private final String name;
    private final String surname;
    private final String address;
    private final String metro;
    private final String phone;
    private final String date;
    private final String period;
    private final String color;
    private final String comment;
    private final boolean fromTopButton;

    public OrderTest(String name, String surname, String address, String metro, String phone,
                     String date, String period, String color, String comment, boolean fromTopButton) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.metro = metro;
        this.phone = phone;
        this.date = date;
        this.period = period;
        this.color = color;
        this.comment = comment;
        this.fromTopButton = fromTopButton;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"Иван", "Иванов", "ул. Ленина, 1", "Черкизовская", "89991112233", "01.01.2023", "сутки", "black", "Позвонить за час", true},
                {"Анна", "Петрова", "пр. Мира, 10", "Сокольники", "89994445566", "15.01.2023", "двое суток", "grey", "Оставить у двери", false}
        });
    }

    @Before
    public void setUp() {
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        driver.get("https://qa-scooter.praktikum-services.ru/");
        mainPage.acceptCookies();
    }

    @Test
    public void testOrderCreation() {
        if (fromTopButton) {
            mainPage.clickOrderButtonTop();
        } else {
            mainPage.clickOrderButtonBottom();
        }

        orderPage.fillFirstPage(name, surname, address, metro, phone);
        orderPage.fillSecondPage(date, period, color, comment);
        orderPage.confirmOrder();

        assertTrue("Сообщение об успешном создании заказа не отображается", orderPage.isSuccessMessageDisplayed());
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}