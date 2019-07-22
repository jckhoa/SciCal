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

public class Token extends TokenBase{
    Token(String text) { this.text = text; }
    
    @Override
    public boolean isType(TokenBase.Type type) { return false; }
    
    @Override
    public OpBase getOperator() { return null; }
    
    @Override
    public String getInfo() {
        return "'" + text + "': null";
    }
}

