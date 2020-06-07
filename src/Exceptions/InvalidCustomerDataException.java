/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author tim
 */
public class InvalidCustomerDataException extends Exception {

    public InvalidCustomerDataException(String message) {
        super(message);
    }
    
}
