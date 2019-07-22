/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.Character;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Khoa
 */
public class SciExpression {
    public static HashMap<String, SciValue> baseUnit = new HashMap<String, SciValue>() {
        {
            put("s", new SciValue(1.0, 1));
            put("m", new SciValue(1.0, 0, 1));
            put("g", new SciValue(1.0, 0, 0, 1));
            put("A", new SciValue(1.0, 0, 0, 0, 1));
            put("K", new SciValue(1.0, 0, 0, 0, 0, 1));
            put("mol", new SciValue(1.0, 0, 0, 0, 0, 0, 1));
            put("cd", new SciValue(1.0, 0, 0, 0, 0, 0, 0, 1));
        }
    };
    HashMap<String, OpBase> shortOperators = new HashMap<>();
    HashMap<String, OpBase> longOperators = new HashMap<>();
    
    public SciExpression() {
        initOperators();
    }
    
    private void initOperators() {
        shortOperators.put("/", new OpDivide());
        shortOperators.put("*", new OpMultiply());
        shortOperators.put("%", new OpModulo());
        shortOperators.put("^", new OpPower());
        shortOperators.put("+", new OpAdd());
        shortOperators.put("-", new OpSubtract());

        longOperators.put("ln", new OpLn());        
    }

    public ArrayList<Token> getReversePolishNotation(ArrayList<Token> tokens) throws SciParseException {
        ArrayList<Token> output = new ArrayList<>();
        Stack<Token> operator = new Stack<>();
        
        for (Token tk: tokens) {            
            if (tk.isType(TokenBase.Type.Num) || tk.isType(TokenBase.Type.Unit)) {
                output.add(tk);
                continue;
            }
            if (tk.isType(TokenBase.Type.Operator)) {
                while (!operator.empty() && (!operator.peek().isType(TokenBase.Type.LeftBracket) &&
                        (operator.peek().getOperator().isAssociativeType(OpBase.Type.R2L)
                        || operator.peek().getOperator().precedenceGT(tk.getOperator()) 
                        || (operator.peek().getOperator().precedenceEQ(tk.getOperator()) 
                            && operator.peek().getOperator().isAssociativeType(OpBase.Type.L2R))
                        ))
                      ) 
                {                      
                    output.add(operator.pop());
                }
                operator.push(tk);
                continue;
            }
            if (tk.isType(TokenBase.Type.LeftBracket)) {
                operator.push(tk);
                continue;
            }
            if (tk.isType(TokenBase.Type.RightBracket)) {
                while (!operator.peek().isType(TokenBase.Type.LeftBracket)) {
                    output.add(operator.pop());
                }
                /* if the stack runs out without finding a left paren, then there are mismatched parentheses. */
                if (operator.peek().isType(TokenBase.Type.LeftBracket)) {
                    operator.pop();
                } else {
                    throw new SciParseException("Missing mismatched brackets.");
                }                
            }   
            
        }
        while (!operator.empty()) {
            output.add(operator.pop());
        }
        return output;
    }
    
    public SciValue parseUnit(String unit) throws SciParseException {
        if (unit.length() == 1) {
            SciValue result = baseUnit.get(unit);
            if (result != null) return result;            
        } else if (unit.length() == 2) {
            SciValue result = baseUnit.get(unit);
            if (result != null) return result;
            result = baseUnit.get(unit.substring(1, 1));
            if (result != null) {
                Double prefix = SciUnit.prefix.get(unit.substring(0,0));
                if (prefix != null) return result.multiply(new SciValue(prefix));
            } 
        } else {
            SciValue result = baseUnit.get(unit);
            if (result != null) return result;
            result = baseUnit.get(unit.substring(2, unit.length()-1));
            if (result != null) {
                Double prefix = SciUnit.prefix.get(unit.substring(0,1));
                if (prefix != null) return result.multiply(new SciValue(prefix));
            }
            result = baseUnit.get(unit.substring(1, unit.length()-1));
            if (result != null) {
                Double prefix = SciUnit.prefix.get(unit.substring(0,0));
                if (prefix != null) return result.multiply(new SciValue(prefix));                
            }
        }
        throw new SciParseException("Unit '" + unit + "' is not found in the database");
    }

    public SciValue evaluate(ArrayList<Token> tokens) throws SciParseException{
        ArrayList<Token> rpn = getReversePolishNotation(tokens);
        ArrayList<SciValue> output = new ArrayList<>();
        for (int i = 0; i < rpn.size(); ++i) {
            Token tk = rpn.get(i);
            if (tk.isType(TokenBase.Type.Num)) {
                output.add(new SciValue(Double.valueOf(tk.text)));
            } else if (tk.isType(TokenBase.Type.Unit)) {
                output.add(parseUnit(tk.text));
            } else {
                var op = shortOperators.get(tk.getText());
                List<SciValue> args = output.subList(output.size() - op.operands(), output.size());
                SciValue val = op.execute(args);
                args.clear();
                output.add(val);                
            }
        }
        
        if (output.size() > 1) {
            //kk throw error
            return new SciValue();
        } else return output.get(0);
    }
    
    public ArrayList<Token> parse(String expression) {
        ArrayList<Token> tokens = new ArrayList<>();
        ParseToken parseToken = new ParseToken("", ParseType.None);
        boolean isLastWhiteChar = true;
        try {
            expression += '\0';
            for (int i = 0; i < expression.length(); ++i) {
                char c = expression.charAt(i);
                if (Character.isWhitespace(c)) {
                    isLastWhiteChar = true;
                    continue;
                }
                if (c == '\0') parseLastToken(c, i, parseToken, tokens);
                else if (c == '(' || c == ')') parseParenthesis(c, i, parseToken, tokens);                
                else if (c == '+' || c == '-') parseSignedOperators(c, i, parseToken, tokens, isLastWhiteChar);
                else if (c == '.') parseDot(c, i, parseToken, tokens, isLastWhiteChar);
                else if (c == 'e') parseE(c, i, parseToken, tokens, isLastWhiteChar); 
                else if (shortOperators.containsKey("" + c)) parseShortOperators(c, i, parseToken, tokens);
                else if (Character.isDigit(c)) parseDigit(c, i, parseToken, tokens, isLastWhiteChar);                          
                else if (Character.isAlphabetic(c)) parseAlphabetic(c, i, parseToken, tokens, isLastWhiteChar);
                else throw new SciParseException("The " + String.valueOf(i) + "th character '" + c + "' is illegal.");
                isLastWhiteChar = false;
            }   
        } catch (SciParseException e) {
            for(Token token:tokens) System.out.println(token.getInfo());
            parseToken.print();
            System.out.println(e.getMessage());
            return null;
        }
        
        return tokens;
    }

    private String makeErrorMessage(char c, int pos, String reason) {
        String str = "Expression parsing error. At ";
        if (c == '\0') str += "the end of the expression. Reason: " + reason;
        else str += "the " + String.valueOf(pos) + "th character '" 
                + c + "'. Reason: " + reason;
        return str;
    }

    private void parseLastToken(char c, int pos, ParseToken tk, ArrayList<Token> tokens) throws SciParseException {
        System.out.println("Parsing parenthesis...");
        switch (tk.type) {
            case None: break;
            case Int: case FNum: case FNumEInt: case SNum:
                tokens.add(new TokenNum(tk.str));                
                break;
            case Sign: case Dot: case FNumE: case FNumEDot: case FNumES:
                throw new SciParseException(makeErrorMessage(c, pos, "The number before the character is illegal."));
            case ShortOp: case LongOp:
                throw new SciParseException(makeErrorMessage(c, pos, "The expression must not end with an operator."));    
            case Str:
                OpBase op = longOperators.get(tk.str);
                if (op != null) throw new SciParseException(makeErrorMessage(c, pos, "The expression must not end with an operator."));
                else {
                    //kk maybe check if the unit exist
                    tokens.add(new TokenUnit(tk.str));
                    break;
                }
            case ParenLeft:
                throw new SciParseException(makeErrorMessage(c, pos, "The expression must not end with an open parenthesis."));
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                break;
        }
    }    

    private void parseParenthesis(char c, int pos, ParseToken tk, ArrayList<Token> tokens) throws SciParseException {
        System.out.println("Parsing parenthesis...");
        switch (tk.type) {
            case None: break;
            case Int: case FNum: case FNumEInt: case SNum:
                if (c == ')') tokens.add(new TokenNum(tk.str));
                else tokens.add(new TokenOperator("*", shortOperators.get("*")));
                break;
            case Sign: case Dot: case FNumE: case FNumEDot: case FNumES:
                throw new SciParseException(makeErrorMessage(c, pos, "The number before the character is illegal.'"));
            case ShortOp:
                if (c == '(') tokens.add(new TokenOperator(tk.str, shortOperators.get(tk.str)));
                else throw new SciParseException(makeErrorMessage(c, pos, " An operator before ')' is illegal"));
                break;
            case LongOp:
                if (c == '(') {
                    OpBase op = longOperators.get(tk.str);
                    if (op != null) tokens.add(new TokenOperator(tk.str, op));
                    else {
                        throw new SciParseException(makeErrorMessage(c, pos
                                , "The '" + tk.str + "' operator is not found in the database."));
                    }
                }
                else throw new SciParseException(makeErrorMessage(c, pos, " An operator before ')' is illegal"));
                break;
            case Str:
                if (c == '(') {
                    OpBase op = longOperators.get(tk.str);
                    if (op != null) tokens.add(new TokenOperator(tk.str, op));
                    else {
                        throw new SciParseException(makeErrorMessage(c, pos
                                , "The '" + tk.str + "' operator is not found in the database."));
                    }
                } else {
                    //kk maybe check if the unit exist
                    tokens.add(new TokenUnit(tk.str));
                }
                break;
            case ParenLeft:
                tokens.add(new TokenLeftBracket(tk.str));
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                break;
        }
       
        tk.str = "" + c;
        tk.type = (c == '(')? ParseType.ParenLeft: ParseType.ParenRight;
    }
    
    private void parseShortOperators(char c, int pos, ParseToken tk, ArrayList<Token> tokens) throws SciParseException {
        System.out.println("Parsing short operator...");
        switch (tk.type) {
            case None: case Sign: case Dot: case FNumE: case FNumEDot: case FNumES: case ShortOp: case LongOp: case ParenLeft:
                throw new SciParseException(makeErrorMessage(c, pos, "The item before the character is illegal."));
            case Int: case FNum: case FNumEInt: case SNum:
                tokens.add(new TokenNum(tk.str));
                break;
            case Str:
                //kk maybe check if the unit exist
                tokens.add(new TokenUnit(tk.str));
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                break;
        }
        tk.str = "" + c;
        tk.type = ParseType.ShortOp;
    }
    
    private void parseSignedOperators(char c, int pos, ParseToken tk, ArrayList<Token> tokens, boolean isLastWhiteChar) throws SciParseException {
        System.out.println("Parsing signed operator...");
        switch (tk.type) {
            case None: break;
            case Int: case FNum: case FNumEInt: case SNum:
                tokens.add(new TokenNum(tk.str));
                tk.str = "" + c;
                tk.type = ParseType.ShortOp;
                return;
            case Sign: case Dot: case FNumEDot: case FNumES:
                throw new SciParseException(makeErrorMessage(c, pos, "The item before the character is illegal."));
            case FNumE:
                if (isLastWhiteChar) 
                    throw new SciParseException(makeErrorMessage(c, pos, "The item before the character is illegal."));
                else {
                    tk.str += c;
                    tk.type = ParseType.FNumES;
                }
                return;            
            case ShortOp:
                tokens.add(new TokenOperator(tk.str, shortOperators.get(tk.str)));
                break;
            case LongOp:
                OpBase op = longOperators.get(tk.str);
                if (op != null) tokens.add(new TokenOperator(tk.str, op));
                else {        
                    throw new SciParseException(makeErrorMessage(c, pos
                            , "The '" + tk.str + "' operator is not found in the database."));
                }
                break;
            case Str:
                OpBase op1 = longOperators.get(tk.str);
                if (op1 != null) tokens.add(new TokenOperator(tk.str, op1));
                else {                    
                    //kk maybe check if the unit exist
                    tokens.add(new TokenUnit(tk.str));
                    tk.str = "" + c;
                    tk.type = ParseType.ShortOp;
                    return;
                }
                break;
            case ParenLeft:
                tokens.add(new TokenLeftBracket(tk.str));        
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                tk.str = "" + c;
                tk.type = ParseType.ShortOp;
                return;
        }       
        tk.str = (c == '-')? "-": "";
        tk.type = ParseType.Sign;
    }

    private void parseDot(char c, int pos, ParseToken tk, ArrayList<Token> tokens, boolean isLastWhiteChar) throws SciParseException {
        System.out.println("Parsing dot character...");
        switch (tk.type) {
            case None: break;
            case Int:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.FNum;
                    return;
                }                
            case Sign:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.Dot;
                    return;
                }
            case Dot: case FNum: case FNumEDot: case SNum:
                throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
            case FNumE: case FNumES:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.FNumEDot;
                    return;
                }
            case FNumEInt:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.SNum;
                    return;
                }
            case ShortOp:
                tokens.add(new TokenOperator(tk.str, shortOperators.get(tk.str)));
                break;
            case LongOp:
                OpBase op = longOperators.get(tk.str);
                if (op != null) {
                    tokens.add(new TokenOperator(tk.str, op));
                    tk.str = ".";
                    tk.type = ParseType.Dot;
                } else {
                    throw new SciParseException(makeErrorMessage(c, pos
                            , "The '" + tk.str + "' operator is not found in the database."));
                }
                break;
            case Str:
                OpBase op1 = longOperators.get(tk.str);
                if (op1 != null) tokens.add(new TokenOperator(tk.str, op1));
                else {
                    //kk maybe check if the unit exists
                    tokens.add(new TokenUnit(tk.str));
                    tokens.add(new TokenOperator("*", shortOperators.get("*")));
                }
                break;
            case ParenLeft:
                tokens.add(new TokenLeftBracket(tk.str));
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                break;
        }        
        tk.str = ".";
        tk.type = ParseType.Dot;
                
    }
    
    private void parseE(char c, int pos, ParseToken tk, ArrayList<Token> tokens, boolean isLastWhiteChar) throws SciParseException {
        System.out.println("Parsing 'e' character...");
        switch (tk.type) {
            case None: break;
            case Int: case FNum:
                if (isLastWhiteChar) {
                    tokens.add(new TokenOperator("*", shortOperators.get("*")));
                    tokens.add(new TokenNum(tk.str));
                    tk.str = "e";
                    tk.type = ParseType.Str;
                }
                else {
                    tk.str += c;
                    tk.type = ParseType.FNumE;                
                }
                return;
            case Sign: case Dot: case FNumE: case FNumEInt: case FNumEDot: case FNumES: case SNum:
                throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
            case ShortOp:
                tokens.add(new TokenOperator(tk.str, shortOperators.get(tk.str)));
                break;
            case LongOp: case Str:
                if (isLastWhiteChar) {
                    OpBase op = longOperators.get(tk.str);
                    if (op != null) {
                        tokens.add(new TokenOperator(tk.str, op));
                        tk.type = ParseType.LongOp;
                    }
                    else {
                        //kk maybe check if the unit exists
                        tokens.add(new TokenUnit(tk.str));
                        tokens.add(new TokenOperator("*", shortOperators.get("*")));                        
                        tk.type = ParseType.Str;
                    }   
                    tk.str = "e";
                }
                else {
                    tk.str += c;
                }
                return;
            case ParenLeft:
                tokens.add(new TokenLeftBracket(tk.str));
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                tokens.add(new TokenOperator("*", shortOperators.get("*")));
                break;
        }       
        tk.str = "e";
        tk.type = ParseType.LongOp;        
    }

    private void parseDigit(char c, int pos, ParseToken tk, ArrayList<Token> tokens, boolean isLastWhiteChar) throws SciParseException {
        System.out.println("Parsing a digit...");
        switch (tk.type) {
            case None: break;
            case Sign: case Int:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.Int;
                    return;
                }
            case Dot: case FNum:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.FNum;
                    return;
                }
            case FNumEInt: case SNum: 
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c; 
                    return;
                }
            case FNumE: case FNumES:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.FNumEInt;
                    return;
                }
            case FNumEDot:
                if (isLastWhiteChar) throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
                else {
                    tk.str += c;
                    tk.type = ParseType.SNum;
                    return;
                }
            case ShortOp:
                tokens.add(new TokenOperator(tk.str, shortOperators.get(tk.str)));
                break;
            case LongOp:
                OpBase op = longOperators.get(tk.str);
                if (op != null) tokens.add(new TokenOperator(tk.str, op));
                else {
                    throw new SciParseException(makeErrorMessage(c, pos
                            , "The '" + tk.str + "' operator is not found in the database."));
                }
                break;
            case Str:
                OpBase op1 = longOperators.get(tk.str);
                if (op1 != null) tokens.add(new TokenOperator(tk.str, op1));
                else {
                    // kk maybe check if the unit exists
                    tokens.add(new TokenUnit(tk.str));
                    tokens.add(new TokenOperator("*", shortOperators.get("*")));
                }
                break;
            case ParenLeft:
                tokens.add(new TokenLeftBracket(tk.str));
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                tokens.add(new TokenOperator("*", shortOperators.get("*")));
                break;
        }        
        tk.str = "" + c;
        tk.type = ParseType.Int;
    }
        
    private void parseAlphabetic(char c, int pos, ParseToken tk, ArrayList<Token> tokens, boolean isLastWhiteChar) throws SciParseException {
        System.out.println("Parsing an alphabet...");
        switch (tk.type) {
            case None:
                tk.str = "" + c;
                tk.type = ParseType.Str;
                return;
            case Int: case FNum: case FNumEInt: case SNum:
                tokens.add(new TokenNum(tk.str));
                tokens.add(new TokenOperator("*", shortOperators.get("*")));
                tk.str = "" + c;
                tk.type = ParseType.Str;
                return;
            case Sign: case Dot: case FNumE: case FNumEDot: case FNumES:
                throw new SciParseException(makeErrorMessage(c, pos, "Illegal parsing the character."));
            case ShortOp:
                tokens.add(new TokenOperator(tk.str, shortOperators.get(tk.str)));
                tk.str = "" + c;
                tk.type = ParseType.Str;
                return;
            case LongOp: case Str:
                if (isLastWhiteChar) {
                    OpBase op = longOperators.get(tk.str);
                    if (op != null) {
                        tokens.add(new TokenOperator(tk.str, op));
                        tk.type = ParseType.LongOp;
                    } else {
                        // kk maybe check if the unit exists
                        tokens.add(new TokenUnit(tk.str));
                        tokens.add(new TokenOperator("*", shortOperators.get("*")));
                        tk.type = ParseType.Str;
                    }   
                    tk.str = "" + c;
                } else {
                    tk.str += c;
                }
                return;
            case ParenLeft:
                tokens.add(new TokenLeftBracket(tk.str));
                break;
            case ParenRight:
                tokens.add(new TokenRightBracket(tk.str));
                tokens.add(new TokenOperator("*", shortOperators.get("*")));
                break;
        }        
        tk.str = "" + c;
        tk.type = ParseType.LongOp;
    }
    
    private class ParseToken {
        public String str;
        public ParseType type = ParseType.None;
        
        public ParseToken(String str, ParseType type) {
            this.str = str;
            this.type = type;
        }
        
        public void print() {
            String output = "parseToken: '" + str + "', type: ";
            switch (type) {
                case None: output += "None"; break;
                case Sign: output += "Sign"; break;
                case Int: output += "Int"; break;
                case Dot: output += "Dot"; break;
                case FNum: output += "FNum"; break;
                case FNumE: output += "FNumE"; break;
                case FNumEInt: output += "FnumEInt"; break;
                case FNumES: output += "FNumES"; break;
                case FNumEDot: output += "FNumEDot"; break;
                case SNum: output += "SNum"; break;
                case LongOp: output += "LongOp"; break;
                case ShortOp: output += "ShortOp"; break;
                case Str: output += "Str"; break;
                case ParenLeft: output += "ParenLeft"; break;
                case ParenRight: output += "ParenRight"; break;
            }
            System.out.println(output);
        }
    }

    private enum ParseType {
        None, // start parsing
        Sign, // '+' or '-' sign, expect a digit as the following character 
        Int,  // integer number
        Dot,  // '.' or '+.' or '-.', expect a digit as the next character
        FNum, // floating point number
        FNumE, // floating point number ending with 'e' for exponent, expecting a sign or digit as the following character
        FNumEInt, // floating point number with exponent and ending with a digit
        FNumEDot, // floating point number ending with 'e.', 'e+.' or 'e-.', expecting a digit as the following character
        FNumES, // floating point-number ending with 'e+' or 'e-' for exponent, expecting a digit or a dot as the following character
        SNum, // scientific number containing exponent 'e' and floating point number in the exponent 
        LongOp, // expecting a long operator (operator with more than 1 character) 
        ShortOp, // expecting s short operator (operator with only 1 character)
        Str, // a string, wait to be classified as LongOp or Unit
        ParenLeft, ParenRight; // left and right paraenthesis 
    }
    
    private enum TokenType {
        Num, // number
        Unit, // unit
        Operator, // operator 
        ParenthesisLeft, ParenthesisRight; //left and right parenthesis
    }
    
    /*
    public class Token {
        String text;
        TokenType tokenType;
        SciOperator opType;
        
        public Token(String text, TokenType tokenType, SciOperator opType) {
            this.text = text;
            this.tokenType = tokenType;
            this.opType = opType;
        }
        
        public boolean isNumber() {
            return tokenType != null && tokenType == TokenType.Num;
        }
        
        public boolean isUnit() {
            return tokenType != null && tokenType == TokenType.Unit;
        }
        
        public boolean isOperator() {
            return opType != null;
        }
        
        public boolean isNumberOrUnit() {
            return tokenType != null && (tokenType == TokenType.Num || tokenType == TokenType.Unit);
        }
        
        public boolean isLeftBracket() {
            return tokenType != null && tokenType == TokenType.ParenthesisLeft;
        }
        
        public boolean isRightBracket() {
            return tokenType != null && tokenType == TokenType.ParenthesisRight;
        }
        
        
        public void print() {
            String str = "'" + text + "': ";
            if (tokenType == TokenType.Num) str += "Num";
            else if (tokenType == TokenType.Unit) str += "Unit";
            else if (tokenType == TokenType.Operator) str += "Operator";
            else if (tokenType == TokenType.ParenthesisLeft) str += "Left Parenthesis";
            else if (tokenType == TokenType.ParenthesisRight) str += "Right Parenthesis";
            else str += "Unknown token type";
            str += ", ";
            
            if (opType != null) {
                str += "pred=" + String.valueOf(opType.getPrecedence());
                if (opType.getAssociative() == SciOperator.AssociativeType.L2R) str += " L2R";
                else if (opType.getAssociative() == SciOperator.AssociativeType.R2L) str += " R2L";
                else str += " unknown associative type";
            }
            else str += "not an operator";
            str += ".";
            System.out.println(str);
        }
    }
    */
}
