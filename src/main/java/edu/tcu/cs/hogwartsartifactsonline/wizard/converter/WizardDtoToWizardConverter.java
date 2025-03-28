package edu.tcu.cs.hogwartsartifactsonline.wizard.converter;

import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import edu.tcu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDto, Wizard>
{

    @Override
    public Wizard convert(WizardDto dto){
        Wizard wizard = new Wizard();
        wizard.setId(dto.id());
        wizard.setName(dto.name());
        return wizard;


    }
}
