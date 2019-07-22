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
public class TokenOperator extends Token {
        private OpBase operator;
    
    TokenOperator(String text, OpBase operator) {
        super(text);
        this.operator = operator;
    }
    
    @Override
    public OpBase getOperator() { return this.operator; }
    
    public void setOperator(OpBase operator) { this.operator = operator; }
    
    @Override
    public boolean isType(TokenBase.Type type) { return type == TokenBase.Type.Operator; }
    
    @Override
    public String getInfo() {
        String str = "'" + text + "': Operator";
        str += "(pred=" + String.valueOf(operator.precedence());
        if (null == operator.associative()) str += ", assoc=unknown)";
        else switch (operator.associative()) {
            case L2R:
                str += ", assoc=L2R)";
                break;
            case R2L:
                str += ", assoc=R2L)";
                break;
            default:
                str += ", assoc=unknown)";
                break;
        }
        return str;
    }
}
