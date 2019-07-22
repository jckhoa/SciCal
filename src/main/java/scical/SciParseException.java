/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

/**
 *
 * @author Khoa
 */
public class SciParseException extends Exception {
    public SciParseException(String errorMessage) {
        super(errorMessage);
    }
    
    public SciParseException(String errorMessage, Throwable err) {
        super(errorMessage);
    }

}
