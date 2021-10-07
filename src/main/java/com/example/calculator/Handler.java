package com.example.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Handler {
    boolean isDecimal = false;
    double finalNum;

    @FXML
    private TextField display;

    @FXML
    private TextField secondaryDisplay;

    @FXML
    void ButtonHandler(ActionEvent e) {
        Button button = (Button) e.getSource();
        if(button.getText().equals("AC") ){
            display.setText("");
            secondaryDisplay.setText("");
        }else{
            display.setText(display.getText() + button.getText());
        }
    }

    @FXML
    void PeriodButtonHandler(ActionEvent e){
        Button button = (Button) e.getSource();
        if(!isDecimal){
            display.setText(display.getText() + button.getText());
            isDecimal = true;
        }

    }
    @FXML
    void EvaluateButtonHandler(){
        String finalDisplay = "";
        finalNum = expressionSort(display.getText());
        if(finalNum % 1 == 0) {
            finalDisplay = String.valueOf(finalNum);
            finalDisplay = finalDisplay.substring( 0, finalDisplay.indexOf("."));
        }else {
            finalDisplay = String.valueOf(finalNum);
        }
        secondaryDisplay.setText(display.getText()+"=");
        display.setText(finalDisplay);

    }

    double expressionSort(String equation){
        return new Object() {
            int position = -1;
            int character;

            //generates a char value based on positions value, if positions value is greater than the length of the
            // equation, then it returns a -1
            void nextChar() {
                character = (++position < equation.length()) ? equation.charAt(position) : -1;
            }

            boolean isConsumedNextChar(int consumedChar) {
                while (character == ' ') nextChar();
                if (character == consumedChar) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (position < equation.length()) throw new RuntimeException("Unexpected: " + (char) character);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (isConsumedNextChar('+')) x += parseTerm(); // addition
                    else if (isConsumedNextChar('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (isConsumedNextChar('*')) x *= parseFactor(); // multiplication
                    else if (isConsumedNextChar('/')) x /= parseFactor(); // division
                    else return x;
                }
            }
            double parseFactor() {
                if (isConsumedNextChar('+')) return parseFactor(); // unary plus
                if (isConsumedNextChar('-')) return -parseFactor(); // unary minus

                double x;
                int startPosition = this.position;

                if (isConsumedNextChar('(')) { // parentheses
                    x = parseExpression();

                    isConsumedNextChar(')');
                } else if ((character >= '0' && character <= '9') || character == '.') { // numbers
                    while ((character >= '0' && character <= '9') || character == '.') nextChar();
                    x = Double.parseDouble(equation.substring(startPosition, this.position));
                }
                else {
                    throw new RuntimeException("Unexpected: " + (char) character);
                }

                return x;
            }
        }.parse();

    }
}
