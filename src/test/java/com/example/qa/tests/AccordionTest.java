package com.example.qa.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.example.qa.pages.MainPage;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AccordionTest {
    private MainPage mainPage;
    private WebDriver driver;

    private final int itemIndex;
    private final String expectedText;

    public AccordionTest(int itemIndex, String expectedText) {
        this.itemIndex = itemIndex;
        this.expectedText = expectedText;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {0, "Сутки — 400 рублей. Оплата курьеру — наличными или картой."},
                {1, "Пока что у нас так: один заказ — один самокат. Если хотите покататься с друзьями, можете просто сделать несколько заказов — один за другим."},
                {2, "Допустим, вы оформляете заказ на 8 мая. Мы привозим самокат 8 мая в течение дня. Отсчёт времени аренды начинается с момента, когда вы оплатите заказ курьеру. Если мы привезли самокат 8 мая в 20:30, суточная аренда закончится 9 мая в 20:30."},
                {3, "Только начиная с завтрашнего дня. Но скоро станем расторопнее."},
                {4, "Пока что нет! Но если что-то срочное — всегда можно позвонить в поддержку по красивому номеру 1010."},
                {5, "Самокат приезжает к вам с полной зарядкой. Этого хватает на восемь суток — даже если будете кататься без передышек и во сне. Зарядка не понадобится."},
                {6, "Да, пока самокат не привезли. Штрафа не будет, объяснительной записки тоже не попросим. Все же свои."},
                {7, "Да, обязательно. Всем самокатов! И Москве, и Московской области."}
        });
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().driverVersion("137.0.7151.120").setup();
        driver = new ChromeDriver();
        mainPage = new MainPage(driver);
        driver.get(MainPage.MAIN_URL);
        mainPage.acceptCookies();
    }

    @Test
    public void testAccordionItem() {
        mainPage.clickAccordionItem(itemIndex);
        String actualText = mainPage.getAccordionItemText(itemIndex);
        assertEquals("Текст в аккордеоне не соответствует ожидаемому", expectedText, actualText);
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}