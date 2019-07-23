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
public class OpLn extends OpBase {
    
    @Override
    public int precedence() { return 7; }

    @Override
    public Type associative() { return OpBase.Type.R2L; }

    @Override
    public int operands() { return 1; }

    @Override
    protected SciValue compute(List<SciValue> params) {
        return params.get(0).ln();
    }

    @Override
    public String name() {
        return "Natural logarithm";
    }
}
