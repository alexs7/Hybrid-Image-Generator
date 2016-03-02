package com.alexs7;

/**
 * Created by ar1v13 on 02/03/16.
 */
public class InvalidKernelSize extends Exception {

    public String getMessage(){
        return "Make sure both dimensions are even!";
    }
}
