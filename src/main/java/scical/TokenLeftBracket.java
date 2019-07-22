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
public class TokenLeftBracket extends Token {
        
    TokenLeftBracket(String text) { super(text); }
    
    @Override
    public boolean isType(TokenBase.Type type) { return type == TokenBase.Type.LeftBracket; }
    
    @Override
    public String getInfo() {
        return "'" + text + "': LeftBracket";
    }

}
