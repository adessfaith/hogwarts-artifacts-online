package edu.tcu.cs.hogwartsartifactsonline.wizard;


import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {
    @Mock
    WizardRepository wizardRepository;

    @Mock IdWorker idWorker;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;
    @BeforeEach
    void setUp() {
//        {
//            "flag": true,
//                "code": 200,
//                "message": "Find All Success",
//                "data": [
//            {
//                "id": 1,
//                    "name": "Albus Dumbledore",
//                    "numberOfArtifacts": 2
//            },
//            {
//                "id": 2,
//                    "name": "Harry Potter",
//                    "numberOfArtifacts": 2
//            },
//            {
//                "id": 3,
//                    "name": "Neville Longbottom",
//                    "numberOfArtifacts": 1
//            }
//  ]
//        }
                Wizard a = new Wizard();
                a.setName("Albus Dumbledore");
                a.setId(1);

        Wizard b = new Wizard();
        b.setName("Harry Potter");
        b.setId(2);


        Wizard c = new Wizard();
        c.setName("Neville Longbottom");
        c.setId(3);


        this.wizards = new ArrayList<>();
        this.wizards.add(a);
        this.wizards.add(b);
        this.wizards.add(c);
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess(){
        //given
        Wizard Dumbledore = new Wizard();
        Dumbledore.setId(1);
        Dumbledore.setName("Albus Dumbledore");

        given(wizardRepository.findById(1)).willReturn(Optional.of(Dumbledore));

        //when
        Wizard returned_wizard = wizardService.findById(1);

        assertThat(returned_wizard).isEqualTo(Dumbledore);

        verify(wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound(){
        //GIVEN

        given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        //WHEN
        Throwable thrown = catchThrowable(() -> { Wizard returnedWizard = wizardService.findById(9);});

        //THEN
        assertThat(thrown).isInstanceOf(WizardNotFoundException.class).hasMessage("Could not find wizard with Id 9 :(");
        verify(wizardRepository, times(1)).findById(Mockito.any(Integer.class));

    }
    @Test
    void testFindAllSuccess(){

        //Given
        given(wizardRepository.findAll()).willReturn(this.wizards);


        //When

        List<Wizard> actualWizards = wizardService.findAll();

        //Then
        assertThat(actualWizards.size()).isEqualTo(this.wizards.size());
        verify(wizardRepository, times(1)).findAll();

    }
    @Test
    void testSaveSuccess(){
        //Given
        Wizard newWizard = new Wizard();
        newWizard.setName("Albus Dumbledore");
        newWizard.setId(1);
        //given(idWorker.nextId()).willReturn(999L);
        given(wizardRepository.save(newWizard)).willReturn(newWizard);





        //When
        Wizard savedWizard = wizardService.save(newWizard);


        //Then
        assertThat(savedWizard.getId()).isEqualTo(1);
        assertThat(savedWizard.getName()).isEqualTo("Albus Dumbledore");
        verify(wizardRepository, times(1)).save(newWizard);



    }
    @Test
    void testUpdateSuccess(){
        //Given

        Wizard oldWizard = new Wizard();
        oldWizard.setName("Harry Potter");
        oldWizard.setId(2);

        Wizard update = new Wizard();

        update.setName("Harry Potter-update");

        given(this.wizardRepository.findById(2)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);
        // update.setId(2);
        //When
        Wizard updatedWizard;
        updatedWizard = wizardService.update(2,update);


        //Then
        assertThat(updatedWizard.getName()).isEqualTo(oldWizard.getName());
        assertThat(updatedWizard.getId()).isEqualTo(2);
        verify(wizardRepository, times(1)).findById(2);
        verify(wizardRepository, times(1)).save(oldWizard);

    }
    @Test
    void testUpdateNotFound(){

        //Given
        Wizard update = new Wizard();
        update.setName("Harry Potter");
        update.setId(2);

        given(wizardRepository.findById(9)).willReturn(Optional.empty());

        //When
        assertThrows(WizardNotFoundException.class,()->{wizardService.update(9,update);});

        //Then
        verify(wizardRepository, times(1)).findById(9);



    }
    @Test
    void testDeleteSuccess(){

        //Given
        Wizard wizard = new Wizard();
        wizard.setName("Harry Potter");
        wizard.setId(2);

        given(wizardRepository.findById(2)).willReturn(Optional.of(wizard));
        doNothing().when(wizardRepository).deleteById(2);

        //When

        wizardService.delete(2);

        //Then
        verify(wizardRepository, times(1)).deleteById(2);
    }
    @Test
    void testDeleteNotFound(){
        //Given
        given(wizardRepository.findById(9)).willReturn(Optional.empty());

        //When
        assertThrows(WizardNotFoundException.class,()->{wizardService.delete(9);});


        //Then
        verify(wizardRepository, times(1)).findById(9);
    }


}
