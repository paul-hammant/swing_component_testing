package demo;

import org.junit.Test;

import javax.swing.text.BadLocationException;

import static org.junit.Assert.assertEquals;

public class CounterModelUnitTests {

    @Test
    public void modelCanHoldValue() {
        Demo.Counter.Model model = new Demo.Counter.Model(14);
        assertEquals(14, model.getCount());
    }

    @Test
    public void modelCanTakeANewValue() throws BadLocationException {
        Demo.Counter.Model model = new Demo.Counter.Model(444);
        model.insertString(1,"0", null);
        assertEquals(4044, model.getCount());
    }

    @Test
    public void modelCantTakeNonNumericValues() throws BadLocationException {
        Demo.Counter.Model model = new Demo.Counter.Model(555);
        model.insertString(1,"a", null);
        assertEquals(555, model.getCount());
        model.insertString(1,"-", null);
        assertEquals(555, model.getCount());
        model.insertString(0,"-", null);
        assertEquals(-555, model.getCount());
    }

    @Test
    public void modelCanBeIncremeneted() {
        Demo.Counter.Model model = new Demo.Counter.Model(24);
        model.increment();
        assertEquals(25, model.getCount());
    }

    @Test
    public void modelCanBeDecremeneted() {
        Demo.Counter.Model model = new Demo.Counter.Model(34);
        model.decrement();
        assertEquals(33, model.getCount());
    }


}
