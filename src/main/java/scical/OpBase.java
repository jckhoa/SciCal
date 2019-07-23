/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

import java.util.List;

/**
 *
 * @author Administrateur
 */
abstract class OpBase {
    public enum Type {
        L2R, R2L;
    }

    abstract public String name();
    
    abstract public int precedence();
    
    abstract public Type associative();
    
    abstract public int operands();
    
    @Override
    public String toString() {
        return name() + ": precedence=" + precedence() + ", associative=" + associative() + ", no. operands=" + operands();
    }
    
    public SciValue execute(List<SciValue> params) {
        if (params.size() == operands()) {
            return compute(params);
        } else {
            // throw error
            return new SciValue();
        }
    }
    
    abstract protected SciValue compute(List<SciValue> params);
    
    /**
     * 
     * @param other another SciOperator
     * @return true if the precedence is greater than that of other
     */
    public boolean precedenceGT(OpBase other) {
        return precedence() > other.precedence();
    }
    
    /**
     * 
     * @param other another SciOperator
     * @return true if the precedence is greater or equal than that of other
     */
    public boolean precedenceGE(OpBase other) {
        return precedence() >= other.precedence();
    }
    
    public boolean precedenceEQ(OpBase other) {
        return precedence() == other.precedence();
    }
    
    public boolean isAssociativeType(Type type) {
        return associative() == type;
    }
    
    

}
