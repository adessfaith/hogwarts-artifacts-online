package edu.tcu.cs.hogwartsartifactsonline.wizard;

import org.springframework.web.bind.annotation.ExceptionHandler;


public class WizardNotFoundException extends RuntimeException {
    public WizardNotFoundException(Integer id) {

            super("Could not find wizard with Id " + id + " :(");

    }
}
