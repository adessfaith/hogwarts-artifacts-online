package edu.tcu.cs.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;

import edu.tcu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class WizardControllerTest {



    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

  @MockBean
   WizardService wizardService;

    List<Wizard> wizards;

//    @Value("${api.endpoint.base-url}")
//    String baseUrl;






    @BeforeEach
    void setUp() {


        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");


        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");


        Artifact a3 = new Artifact();
        a3.setId("1250808601744904193");
        a3.setName("Elder Wand");
        a3.setDescription("The Elder Wand, known throughout history as the Deathstick or the Wand of Destiny, is an extremely powerful wand made of elder wood with a core of Thestral tail hair.");
        a3.setImageUrl("ImageUrl");


        Artifact a4 = new Artifact();
        a4.setId("1250808601744904194");
        a4.setName("The Marauder's Map");
        a4.setDescription("A magical map of Hogwarts created by Remus Lupin, Peter Pettigrew, Sirius Black, and James Potter while they were students at Hogwarts.");
        a4.setImageUrl("ImageUrl");


        Artifact a5 = new Artifact();
        a5.setId("1250808601744904195");
        a5.setName("The Sword Of Gryffindor");
        a5.setDescription("A goblin-made sword adorned with large rubies on the pommel. It was once owned by Godric Gryffindor, one of the medieval founders of Hogwarts.");
        a5.setImageUrl("ImageUrl");


        Artifact a6 = new Artifact();
        a6.setId("1250808601744904196");
        a6.setName("Resurrection Stone");
        a6.setDescription("The Resurrection Stone allows the holder to bring back deceased loved ones, in a semi-physical form, and communicate with them.");
        a6.setImageUrl("ImageUrl");
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
    void findWizardByIdSuccess() throws Exception {
       //given
        given(this.wizardService.findById(1)).willReturn(this.wizards.get(0));


       //when and then
        this.mockMvc.perform(get("/api/v1/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));


    }
    @Test
    void findWizardByIdNotFound() throws Exception {
        given(this.wizardService.findById(1)).willThrow(new WizardNotFoundException(1));
        this.mockMvc.perform(get("/api/v1/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
        @Test
        void testFindAllWizardsSuccess () throws Exception {
            //GIVEN
            given(this.wizardService.findAll()).willReturn(this.wizards);
            //WHEN AND THEN
            this.mockMvc.perform(get("/api/v1/wizards").accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value("Find All Success"))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.wizards.size())))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].name").value("Harry Potter"));


        }
            @Test
            void addWizardSuccess () throws Exception {
                WizardDto wizardDto = new WizardDto( 4, "Hermione Granger", 0);
                String json = this.objectMapper.writeValueAsString(wizardDto);
                Wizard savedWizard = new Wizard();

                savedWizard.setId(4);
                savedWizard.setName("Hermione Granger");
                given(this.wizardService.save(Mockito.any(Wizard.class))).willReturn(savedWizard);

                //WHEN AND THEN
                this.mockMvc.perform(post("/api/v1/wizards").contentType(MediaType.APPLICATION_JSON).content(json))
                        .andExpect( jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value("Add Success"))
                        .andExpect(jsonPath("$.data.id").value(4))
                        .andExpect(jsonPath("$.data.name").value("Hermione Granger"));


            }
            @Test
            void updateWizardSuccess () throws Exception {
                WizardDto wizardDto = new WizardDto( 5, "Hermione Granger", 0);
                String json = this.objectMapper.writeValueAsString(wizardDto);

                Wizard updatedWizard = new Wizard();

                updatedWizard.setId(4);
                updatedWizard.setName("Hermione Granger");
                given(this.wizardService.update(eq(4),Mockito.any(Wizard.class))).willReturn(updatedWizard);
                this.mockMvc.perform(put("/api/v1/wizards/4").contentType(MediaType.APPLICATION_JSON).content(json))
                        .andExpect( jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value("Update Success"))
                        .andExpect(jsonPath("$.data.id").value(4))
                        .andExpect(jsonPath("$.data.name").value("Hermione Granger"));



            }
            @Test
            void testUpdateWizardErrorWithNonExistentID() throws Exception {
                // Given. Arrange inputs and targets. Define the behavior of Mock object wizardService.
                given(this.wizardService.update(eq(5), Mockito.any(Wizard.class))).willThrow(new WizardNotFoundException(5));

                WizardDto wizardDto = new WizardDto(5, // This id does not exist in the database.
                        "Updated wizard name",
                        0);

                String json = this.objectMapper.writeValueAsString(wizardDto);

                // When and then
                this.mockMvc.perform(put( "/api/v1/wizards/5").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(false))
                        .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                        .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                        .andExpect(jsonPath("$.data").isEmpty());

            }

            @Test
                void testDeleteWizardSuccess () throws Exception {
        //given
        doNothing().when(this.wizardService).delete(1);

        //when and then
                this.mockMvc.perform(delete("/api/v1/wizards/1").accept(MediaType.APPLICATION_JSON))
                        .andExpect( jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value("Delete Success"))
                        .andExpect(jsonPath("$.data").isEmpty());



            }

            @Test
            void testDeleteWizardErrorWithNonexistentId() throws Exception {
                doThrow(new WizardNotFoundException(5)).when(this.wizardService).delete(5);

                //WHEN AND THEN
                this.mockMvc.perform(delete("/api/v1/wizards/5").accept(MediaType.APPLICATION_JSON))
                        .andExpect( jsonPath("$.flag").value(false))
                        .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                        .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                        .andExpect(jsonPath("$.data").isEmpty());
            }







    }
//    @Test
//    public void findAllWizardsSuccess(){}


