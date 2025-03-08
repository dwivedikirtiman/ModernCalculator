import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ModernCalculator extends JFrame {
    private JTextField display;
    private String currentExpression = "";
    private double result = 0;
    private double memory = 0;

    // Color scheme
    private final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private final Color BUTTON_COLOR = new Color(50, 50, 50);
    private final Color NUMBER_COLOR = new Color(70, 70, 70);
    private final Color FUNCTION_COLOR = new Color(51, 51, 51);
    private final Color DISPLAY_COLOR = new Color(40, 40, 40);
    private final Color TEXT_COLOR = new Color(200, 200, 200);

    public ModernCalculator() {
        setTitle("Modern Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setResizable(true);

        display = new JTextField("0");
        display.setEditable(true);
        display.setBackground(DISPLAY_COLOR);
        display.setForeground(TEXT_COLOR);
        display.setFont(new Font("Arial", Font.PLAIN, 30));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                currentExpression = display.getText();
            }
        });

        add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 5, 5, 5));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // HIGHLIGHT START - Swapped positions of √ and X
        String[] buttonLabels = {
                "MC", "MR", "M+", "M-", "C",
                "sin", "cos", "tan", "X", "±",  // Moved X to where √ was
                "7", "8", "9", "/", "%",
                "4", "5", "6", "*", "1/x",
                "1", "2", "3", "-", "x²",
                "0", ".", "=", "+", "√"   // Moved √ to where X was
        };
        // HIGHLIGHT END

        for (String label : buttonLabels) {
            JButton button = createButton(label);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, 500));
        pack();
        setLocationRelativeTo(null);
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);

        if ("0123456789.".contains(label)) {
            button.setBackground(NUMBER_COLOR);
        } else if ("+-*/=".contains(label)) {
            button.setBackground(FUNCTION_COLOR);
        } else {
            button.setBackground(BUTTON_COLOR);
        }

        button.addActionListener(e -> processInput(label));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if ("0123456789.".contains(label)) {
                    button.setBackground(NUMBER_COLOR);
                } else if ("+-*/=".contains(label)) {
                    button.setBackground(FUNCTION_COLOR);
                } else {
                    button.setBackground(BUTTON_COLOR);
                }
            }
        });

        return button;
    }

    private void processInput(String command) {
        DecimalFormat df = new DecimalFormat("#.########");

        if ("0123456789.+-*/".contains(command)) {
            currentExpression += command;
            display.setText(currentExpression);
        } else if (command.equals("X")) {
            if (!currentExpression.isEmpty()) {
                currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                display.setText(currentExpression.isEmpty() ? "0" : currentExpression);
            }
        } else {
            switch (command) {
                case "MC": memory = 0; break;
                case "MR":
                    currentExpression += df.format(memory);
                    display.setText(currentExpression);
                    break;
                case "M+":
                    try { memory += evaluateExpression(currentExpression); } catch (Exception e) {}
                    break;
                case "M-":
                    try { memory -= evaluateExpression(currentExpression); } catch (Exception e) {}
                    break;
                case "sin":
                    try {
                        result = Math.sin(Math.toRadians(evaluateExpression(currentExpression)));
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "cos":
                    try {
                        result = Math.cos(Math.toRadians(evaluateExpression(currentExpression)));
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "tan":
                    try {
                        result = Math.tan(Math.toRadians(evaluateExpression(currentExpression)));
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "√":
                    try {
                        result = Math.sqrt(evaluateExpression(currentExpression));
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "x²":
                    try {
                        result = Math.pow(evaluateExpression(currentExpression), 2);
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "1/x":
                    try {
                        result = 1 / evaluateExpression(currentExpression);
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "%":
                    try {
                        result = evaluateExpression(currentExpression) / 100;
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "±":
                    try {
                        result = -evaluateExpression(currentExpression);
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {}
                    break;
                case "C":
                    result = 0;
                    currentExpression = "";
                    display.setText("0");
                    break;
                case "=":
                    try {
                        result = evaluateExpression(currentExpression);
                        currentExpression = df.format(result);
                        display.setText(currentExpression);
                    } catch (Exception e) {
                        display.setText("Error");
                        currentExpression = "";
                    }
                    break;
            }
        }
    }

    private double evaluateExpression(String expression) {
        if (expression.isEmpty()) return 0;

        List<String> tokens = tokenize(expression);

        List<String> firstPass = new ArrayList<>();
        double tempResult = 0;
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("*") || token.equals("/")) {
                double left = Double.parseDouble(firstPass.remove(firstPass.size() - 1));
                double right = Double.parseDouble(tokens.get(i + 1));
                if (token.equals("*")) {
                    tempResult = left * right;
                } else {
                    if (right == 0) throw new ArithmeticException("Division by zero");
                    tempResult = left / right;
                }
                firstPass.add(String.valueOf(tempResult));
                i++;
            } else {
                firstPass.add(token);
            }
        }

        result = Double.parseDouble(firstPass.get(0));
        for (int i = 1; i < firstPass.size(); i += 2) {
            String operator = firstPass.get(i);
            double nextNumber = Double.parseDouble(firstPass.get(i + 1));
            if (operator.equals("+")) {
                result += nextNumber;
            } else if (operator.equals("-")) {
                result -= nextNumber;
            }
        }

        return result;
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else if ("+-*/".indexOf(c) != -1) {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }
        if (number.length() > 0) {
            tokens.add(number.toString());
        }

        return tokens;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModernCalculator calc = new ModernCalculator();
            calc.setVisible(true);
        });
    }
}