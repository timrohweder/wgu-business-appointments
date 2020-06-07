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
public class InvalidAppointmentHoursException extends Exception {

    public InvalidAppointmentHoursException(String message) {
        super(message);
    }
    
}