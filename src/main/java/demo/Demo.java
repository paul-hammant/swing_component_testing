/*

  A single source file for java is unusual, but you
  can hand it to Java9+ for a seamless compilation and
  launch like so ...

     java Demo.java

  ... and this is just a demo.

  Also we like pseudo-declarative forms :)

 */

package demo;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Demo extends JFrame {

    // See also two named inner classes: CoupledCounterPair, Counter
    // (and inner classes of Counter: Counter.View & Counter.Model)
    // Also see the main() method half way down this source file.

    // Note: non-final fields, because of the nature of Java's anonymous inner classes.
    private JPanel tab1, tab2, tab2Contents;
    private JTabbedPane tabbedPane;
    private JButton addCounterPairToTab2Button;

    public Demo(Counter.Model sharedCounterModel) {{
        // View logic ...
        setTitle("Counter MVC demo using Swing");
        add(new JTabbedPane() {{
            tabbedPane = this;
            addTab("Tab 1", new JPanel() {{
                tab1 = this;
                add(new CoupledCounterPair(sharedCounterModel).view);
                add(new JPanel(){{
                    setBorder(BorderFactory.createTitledBorder("Two Separate Models"));
                    add(new Counter(new Counter.Model(3)).view);
                    add(new Counter(new Counter.Model(17)).view);
                    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                }});
                add(new JButton("Another Counter (shared model) in Tab 2") {{
                    // store for controller access outside of view logic
                    addCounterPairToTab2Button = this;
                }});
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            }});
            addTab("Tab 2", new JPanel() {{
                setName("tab2");
                // store for controller access outside of view logic
                tab2 = this;
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            }});

        }});
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // controller logic
        addCounterPairToTab2Button.addActionListener(e -> addCounterPairToTab2(sharedCounterModel));
        addCloseWindowHandler(sharedCounterModel);
    }}

    private void removeCounterPairToTab2() {{
        addCounterPairToTab2Button.setEnabled(true);
        tab2.remove(tab2Contents);
        tabbedPane.setSelectedComponent(tab1);
    }}

    private void addCounterPairToTab2(final Counter.Model counterModel) {
        tab2.add(new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            tab2Contents = this;
            add(new CoupledCounterPair(counterModel).view);
            add(new JButton("OK I'm done with counter pair in tab 2") {{
                addActionListener(e -> removeCounterPairToTab2());
                setAlignmentX(Component.LEFT_ALIGNMENT);
                setName("back2TabOne");
            }});
        }});
        addCounterPairToTab2Button.setEnabled(false);
        tabbedPane.setSelectedComponent(tab2);
    }

    private void addCloseWindowHandler(final Counter.Model counterModel) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
                System.out.println("Frame closed, views/controllers eligible for GC, model value = "
                        + counterModel.getCount() + ". Now exit."); ;
            }
        });
    }

    public static void main(String[] args) {
        new Demo(new Counter.Model(0));
    }

    public static class CoupledCounterPair {

        final View view;

        public CoupledCounterPair(Counter.Model model) {
            view = new View(model);
        }

        static class View extends JPanel {
            View(Counter.Model counterModel) {{
                setBorder(BorderFactory.createTitledBorder("Single Shared Model"));
                add(new Counter(counterModel).view);
                add(new Counter(counterModel).view);
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            }}
        }
    }

    public static class Counter {

        final View view;

        Counter(Model model) {
            view = new View(model);
            // controller logic
            view.plus.addActionListener(e -> {
                model.increment();
            });
            view.minus.addActionListener(e -> {
                model.decrement();
            });
        }

        static class View extends JPanel {
            final JTextField counterField;
            final JButton plus;
            final JButton minus;

            View(Model model) {{
                add(new JLabel("Count:"));
                counterField = new JTextField("0", 4);
                counterField.setDocument(model);
                add(counterField);
                plus = new JButton("+");
                final int s = (int) (getFont().getSize() * 1.7);
                plus.setPreferredSize(new Dimension(s, s));
                add(plus);
                minus = new JButton("-");
                minus.setPreferredSize(new Dimension(s, s));
                add(minus);
            }}
        }

        public static class Model extends PlainDocument {
            Model(int initialValue) {
                setCount(initialValue);
            }

            void increment() {
                setCount(getCount() + 1);
            }

            void decrement() {
                setCount(getCount() - 1);
            }

            private void setCount(int i) {
                try {
                    super.remove(0, getLength());
                    super.insertString(0, "" + i, null);
                } catch (BadLocationException e) {
                    throw new UnsupportedOperationException(e);
                }
            }

            int getCount() {
                try {
                    return Integer.parseInt(super.getText(0, super.getLength()));
                } catch (BadLocationException e) {
                    throw new UnsupportedOperationException(e);
                }
            }

            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                for (int i = 0; i < str.length(); i++) {
                    final char c = str.charAt(i);
                    if (!"-0123456789".contains(String.valueOf(c))) {
                        return;
                    } else if (offset != 0 && c == '-') {
                        return;
                    }
                }
                super.insertString(offset, str, attr);
            }
        }
    }
}