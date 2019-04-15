package demo;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static demo.WholeAppIntegrationTests.byReflectionFieldName;
import static org.junit.Assert.assertEquals;

public class CoupledCounterPairComponentTests {

    private Window window;
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = setupMarathon();
    }

    static WebDriver setupMarathon() {
        JavaProfile profile = new JavaProfile(LaunchMode.EMBEDDED);
        profile.setLaunchType(LaunchType.SWING_APPLICATION);
        return new JavaDriver(profile);
    }

    @After
    public void tearDown() throws Exception {
        tearDownMarathonAndCloseWindow(window, driver);
    }

    static void tearDownMarathonAndCloseWindow(Window window, WebDriver driver) throws InterruptedException, InvocationTargetException {
        if (window != null)
            SwingUtilities.invokeAndWait(window::dispose);
        if (driver != null)
            driver.quit();
    }

    @Test
    public void counterPairCanHaveInitialValue() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(4321);
        window = makeAndShowTestHarnessFor(new Demo.CoupledCounterPair(counterModel).view);
        List<WebElement> ctrTxtField = driver.findElements(byReflectionFieldName("counterField", "text-field"));
        assertEquals("4321", ctrTxtField.get(0).getText());
        assertEquals("4321", ctrTxtField.get(1).getText());
        assertEquals(4321, counterModel.getCount());
    }

    @Test
    public void counterPairCanBeSetThroughTheView() {
        final Demo.Counter.Model counterModel = new Demo.Counter.Model(0);
        window = makeAndShowTestHarnessFor(new Demo.CoupledCounterPair(counterModel).view);
        List<WebElement> ctrTxtField = driver.findElements(byReflectionFieldName("counterField", "text-field"));
        ctrTxtField.get(0).sendKeys("5678");
        assertEquals("5678", ctrTxtField.get(1).getText());
        assertEquals(5678, counterModel.getCount());
        ctrTxtField.get(1).sendKeys("2345");
        assertEquals("2345", ctrTxtField.get(0).getText());
    }

    static Window makeAndShowTestHarnessFor(final JComponent... views) {
        JFrame f = new JFrame() {{
            getContentPane().add(new JPanel() {{
                for (JComponent view : views) {
                    add(view);
                }
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            }});
            pack();
            setLocationRelativeTo(null);
        }};
        SwingUtilities.invokeLater(() -> f.setVisible(true));
        return f;
    }

}
