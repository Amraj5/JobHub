/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.exception;

/**
 *
 * @author ADAMS
 */
public class UnauthorizedActionException extends RuntimeException{
        public UnauthorizedActionException(String message) {
        super(message);
    }
}
