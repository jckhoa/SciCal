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
public class OpSubtract extends OpBase{
    
    @Override
    public int precedence() { return 5; }

    @Override
    public Type associative() { return OpBase.Type.L2R; }

    @Override
    public int operands() { return 2; }

    @Override
    protected SciValue compute(List<SciValue> params) {
        return params.get(0).minus(params.get(1));
    }

    @Override
    public String name() {
        return "Subtraction";
    }
}
