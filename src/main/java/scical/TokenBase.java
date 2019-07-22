/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

/**
 *
 * @author Administrateur
 */
abstract class TokenBase {
    public enum Type {
        Undefined,
        Num, // number
        Unit, // unit
        Operator, // operator 
        LeftBracket, RightBracket; //left and right parenthesis
    }
        
    String text;
    
    public void setText(String text) { this.text = text; }
    public String getText() { return this.text;}
    
    abstract boolean isType(Type type);
    
    abstract OpBase getOperator();
    
    abstract String getInfo();
}

