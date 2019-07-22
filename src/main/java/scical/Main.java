/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrateur
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            System.out.println("Hello world.");
            SciValue valueA = new SciValue(3., 0, 1, 2, 3, 4, 5, 6);
            SciValue valueB = new SciValue(5., 0, 2);
            System.out.println("value A = " + valueA);
            System.out.println("value B = " + valueB);
            
            //String expression = "2+3 * (5-  7)/4+1";  // valid expression of integer, operator: + - * /
            //String expression = "2+3 * (5-  7)/4+";   // invalid expression. Ending with a short operator
            //String expression = "2+3 * (";              // invalid expression. Ending with a left parenthesis
            //String expression = " 2.1 * ln5 + 7/ ln(2) + .10"; // valid expression of floating point, long and short operators
            //String expression = " 2.1 * ln"; // invalid expression of floating point, ending with long operator
            //String expression = "  2.1 * ln5 + . * 2"; // invalid expression of floating point, error parsing dot
            //String expression = " -2.1 * ln5 + (+2)*7+  6/(-5.1)"; // valid expression with signed number
            //String expression = " -2.1 * ln5 + (+)*7+  6/(-5.1)"; // invalid expression with signed number
            //String expression = " -2.1 * ln5 + (+2)*7+  6/(-.)"; //invalid expression with signed number, error parsing at -.
            //String expression = " -2.1 * ln5 + (+2)*7+  6/(-.5)"; // valid expression with signed number -.5
            //String expression = " -2e3 + 4.1e6.5 * -.1e-.7 + (-2.1e+.6) / 2.e-8."; // valid with exponent number
            //String expression = " -2e + 4.1e6.5 * -.1e-.7 + (-2.1e+.6) / 2.e-8."; // invalid exponent number -2e+4.1
            //String expression = " -2e3 + 4.1e6.5 * -.1e-. + (-2.1e+.6) / 2.e-8."; // invalid with exponent number -.1e-.
            //String expression = " -2e32 + 4.1e6.5 * -.1e-.7 + (-2.1e+) / 2.e-8."; // invalid with exponent number -2.1e+
            String expression = "3.2 km * ln(2-1) + 5.2 km/s - 2e3^1.2 / 7 A"; // valid expression with unit
            System.out.println("Parsing expression '" + expression + "'");
            SciExpression express = new SciExpression();
            ArrayList<Token> tokens = express.parse(expression);
            System.out.println("-------------- Token list ----------------------");
            printTokenList(tokens);
            System.out.println("-------------- Reverse Polish notation ----------------------");
            var rpn = express.getReversePolishNotation(tokens);
            printTokenList(rpn);
            SciValue result = express.evaluate(tokens);
            System.out.println("Result: " + result.toString());
        } catch (SciParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public static void printTokenList(ArrayList<Token> tokens ) {
        if (tokens != null) {
            tokens.forEach((token) -> {
                System.out.println(token.getInfo());
            });
        } else {
            System.out.println("No tokens were found.");
        }
    }
}
