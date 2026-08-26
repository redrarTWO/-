package com.openai.simplecalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculatorEngine {
    private static final int MAX_INPUT = 15;
    private String display = "0";
    private BigDecimal accumulator;
    private Character pendingOperation;
    private boolean startNewNumber = true;
    private boolean error = false;

    public String getDisplay() { return display; }

    public void inputDigit(char digit) {
        if (digit < '0' || digit > '9') return;
        resetAfterErrorIfNeeded();
        if (startNewNumber) {
            display = String.valueOf(digit);
            startNewNumber = false;
            return;
        }
        if (display.equals("0")) display = String.valueOf(digit);
        else if (display.equals("-0")) display = "-" + digit;
        else if (display.replace("-", "").replace(".", "").length() < MAX_INPUT) display += digit;
    }

    public void inputDecimal() {
        resetAfterErrorIfNeeded();
        if (startNewNumber) {
            display = "0.";
            startNewNumber = false;
        } else if (!display.contains(".")) display += ".";
    }

    public void chooseOperation(char operation) {
        if (operation != '+' && operation != '-' && operation != '×' && operation != '÷') return;
        if (error) return;
        BigDecimal current = currentValue();
        if (accumulator == null) accumulator = current;
        else if (!startNewNumber && pendingOperation != null) {
            BigDecimal result = apply(accumulator, current, pendingOperation);
            if (result == null) return;
            accumulator = result;
            display = format(result);
        }
        pendingOperation = operation;
        startNewNumber = true;
    }

    public void equalsPress() {
        if (error || accumulator == null || pendingOperation == null || startNewNumber) return;
        BigDecimal result = apply(accumulator, currentValue(), pendingOperation);
        if (result == null) return;
        display = format(result);
        accumulator = null;
        pendingOperation = null;
        startNewNumber = true;
    }

    public void toggleSign() {
        resetAfterErrorIfNeeded();
        if (display.equals("0")) return;
        display = display.startsWith("-") ? display.substring(1) : "-" + display;
        startNewNumber = false;
    }

    public void backspace() {
        if (error) { clear(); return; }
        if (startNewNumber) return;
        if (display.length() <= 1 || (display.startsWith("-") && display.length() == 2)) {
            display = "0";
            startNewNumber = true;
        } else {
            display = display.substring(0, display.length() - 1);
            if (display.equals("-") || display.isEmpty()) {
                display = "0";
                startNewNumber = true;
            }
        }
    }

    public void clear() {
        display = "0";
        accumulator = null;
        pendingOperation = null;
        startNewNumber = true;
        error = false;
    }

    private BigDecimal currentValue() {
        String normalized = display.endsWith(".") ? display.substring(0, display.length() - 1) : display;
        if (normalized.isEmpty() || normalized.equals("-")) normalized = "0";
        return new BigDecimal(normalized);
    }

    private BigDecimal apply(BigDecimal left, BigDecimal right, char operation) {
        switch (operation) {
            case '+': return left.add(right);
            case '-': return left.subtract(right);
            case '×': return left.multiply(right);
            case '÷':
                if (right.compareTo(BigDecimal.ZERO) == 0) { setError(); return null; }
                return left.divide(right, 12, RoundingMode.HALF_UP).stripTrailingZeros();
            default: return right;
        }
    }

    private String format(BigDecimal value) {
        BigDecimal normalized = value.stripTrailingZeros();
        if (normalized.scale() < 0) normalized = normalized.setScale(0);
        String out = normalized.toPlainString();
        return out.equals("-0") ? "0" : out;
    }

    private void setError() {
        display = "Ошибка";
        accumulator = null;
        pendingOperation = null;
        startNewNumber = true;
        error = true;
    }

    private void resetAfterErrorIfNeeded() { if (error) clear(); }
}
