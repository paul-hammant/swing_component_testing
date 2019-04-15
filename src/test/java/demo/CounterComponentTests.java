package demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.util.List;

import static demo.CoupledCounterPairComponentTests.makeAndShowTestHarnessFor;
import static demo.CoupledCounterPairComponentTests.setupMarathon;
import static demo.CoupledCounterPairComponentTests.tearDownMarathonAndCloseWindow;
import static demo.WholeAppIntegrationTests.byReflectionFieldName;
import static org.junit.Assert.assertEquals;

public class CounterComponentTests {

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
    public void counterCanHaveInitialValue() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(4321);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        WebElement ctrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        assertEquals("4321", ctrTxtField.getText());
        assertEquals(4321, counterModel.getCount());
    }

    @Test
    public void counterCanBeSetThroughTheView() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        WebElement ctrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        ctrTxtField.sendKeys("5678");
        assertEquals("5678", ctrTxtField.getText());
        assertEquals(5678, counterModel.getCount());

    }

    @Test
    public void counterWontAllowNonNumericCharsThroughTheView() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        WebElement ctrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        ctrTxtField.sendKeys("a-1b2c3d");
        assertEquals("-123", ctrTxtField.getText());
        assertEquals(-123, counterModel.getCount());
    }

    @Test
    public void counterWontAllowAMinusCharAfterInitialPositionThroughTheView() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        WebElement ctrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        ctrTxtField.sendKeys("2-2");
        assertEquals(22, counterModel.getCount());
    }

    @Test
    public void counterViewUpdatesAfterModelChange() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        counterModel.increment();
        counterModel.increment();
        WebElement ctrTxtField = driver.findElement(byReflectionFieldName("counterField", "text-field"));
        assertEquals("2", ctrTxtField.getText());
        assertEquals(2, counterModel.getCount());
    }

    @Test
    public void counterCanBeIncremented() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        WebElement plusButton = driver.findElement(byReflectionFieldName("plus", "button"));
        plusButton.click();
        assertEquals("1", driver.findElement(byReflectionFieldName("counterField", "text-field")).getText());
        assertEquals(1, counterModel.getCount());
        plusButton.click();
        assertEquals("2", driver.findElement(byReflectionFieldName("counterField", "text-field")).getText());
        assertEquals(2, counterModel.getCount());
    }

    @Test
    public void counterCanBeDecremented() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.Counter(counterModel).view);
        WebElement minusButton = driver.findElement(byReflectionFieldName("minus", "button"));
        minusButton.click();
        assertEquals("-1", driver.findElement(byReflectionFieldName("counterField", "text-field")).getText());
        assertEquals(-1, counterModel.getCount());
        minusButton.click();
        assertEquals("-2", driver.findElement(byReflectionFieldName("counterField", "text-field")).getText());
        assertEquals(-2, counterModel.getCount());
    }

    @Test
    public void twoCountersCanHaveTheSameModel() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(
                new Demo.Counter(counterModel).view,
                new Demo.Counter(counterModel).view);
        WebElement plusButton = driver.findElement(byReflectionFieldName("plus", "button"));
        plusButton.click();
        final List<WebElement> counterFields = driver.findElements(byReflectionFieldName("counterField", "text-field"));
        assertEquals(2, counterFields.size());
        assertEquals("1", counterFields.get(0).getText());
        assertEquals("1", counterFields.get(1).getText());
        assertEquals(1, counterModel.getCount());
    }

    @Test
    public void twoCountersCanHaveTheDifferentModels() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(44);
        window = makeAndShowTestHarnessFor(
                new Demo.Counter(counterModel).view,
                new Demo.Counter(new Demo.Counter.Model(999)).view);
        WebElement plusButton = driver.findElement(byReflectionFieldName("plus", "button"));
        plusButton.click();
        final List<WebElement> counterFields = driver.findElements(byReflectionFieldName("counterField", "text-field"));
        assertEquals(2, counterFields.size());
        assertEquals("45", counterFields.get(0).getText());
        assertEquals("999", counterFields.get(1).getText());
        assertEquals(45, counterModel.getCount());
    }

    @Test
    public void threeCountersCanHaveTheSameModel() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(
                new Demo.Counter(counterModel).view,
                new Demo.Counter(counterModel).view,
                new Demo.Counter(counterModel).view);
        WebElement plusButton = driver.findElement(byReflectionFieldName("plus", "button"));
        plusButton.click();
        plusButton.click();
        final List<WebElement> counterFields = driver.findElements(byReflectionFieldName("counterField", "text-field"));
        assertEquals(3, counterFields.size());
        assertEquals("2", counterFields.get(0).getText());
        assertEquals("2", counterFields.get(1).getText());
        assertEquals("2", counterFields.get(2).getText());
        assertEquals(2, counterModel.getCount());
    }

}
