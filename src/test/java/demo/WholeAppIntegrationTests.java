package demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.swing.*;
import java.awt.*;

import static demo.CoupledCounterPairComponentTests.setupMarathon;
import static demo.CoupledCounterPairComponentTests.tearDownMarathonAndCloseWindow;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WholeAppIntegrationTests {

    private Window window;
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = setupMarathon();
    }

    @After
    public void tearDown() throws Exception {
        tearDownMarathonAndCloseWindow(window, driver);
    }

    @Test
    public void basicCounterTestInWholeApp() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = new Demo(counterModel);
        WebElement ctrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        ctrTxtField.sendKeys("444");
        assertEquals("444", ctrTxtField.getText());
        assertEquals(444, counterModel.getCount());
    }

    @Test
    public void clickToSecondTabAndBackWorks() throws InterruptedException {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = new Demo(counterModel);
        SwingUtilities.invokeLater(() -> window.setVisible(true));
        WebElement secondTabButton = driver.findElements(By.tagName("button")).get(8);
        secondTabButton.click();
        WebElement tab2 = driver.findElement(By.name("tab2"));
        WebElement ctrTxtField = tab2.findElement(byReflectionFieldName("counterField", "text-field"));
        assertTrue(ctrTxtField.isDisplayed());
        ctrTxtField.sendKeys("765");
        assertEquals(765, counterModel.getCount());
        tab2.findElement(By.name("back2TabOne")).click();
        assertFalse(ctrTxtField.isDisplayed());
        WebElement tab1CtrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        assertEquals("765", tab1CtrTxtField.getText());
    }


    static By byReflectionFieldName(String fName, final String type) {
        return By.cssSelector(type + "[fieldName='" + fName + "']");
    }

}
