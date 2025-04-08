package edu.tcu.cs.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import edu.tcu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Wizard API endpoints")
@Tag("integration")
public class WizardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String token;


    @Value("${api.endpoint.base-url}")
    String baseUrl;



    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("john", "123456"))); // httpBasic() is from spring-security-test.
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("token");



    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Check find Wizard  with valid input (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)

    void testFindWizardByIdSuccess() throws Exception {


        //when and then
        this.mockMvc.perform(get(this.baseUrl+"/wizards/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));


    }

    @Test
    @DisplayName("Check findWizardById with non-existent id (GET)")
    void testFindWizardByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/wizards/5").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Find all wizards with valid input (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllWizardsSuccess () throws Exception {

        //WHEN AND THEN
        this.mockMvc.perform(get(this.baseUrl+"/wizards").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Harry Potter"));


    }
    @Test
    @DisplayName("Check addWizard with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddWizardSuccess () throws Exception {
        WizardDto wizardDto = new WizardDto( 4, "Hermione Granger", 0);
        String json = this.objectMapper.writeValueAsString(wizardDto);
        Wizard savedWizard = new Wizard();

        savedWizard.setId(4);
        savedWizard.setName("Hermione Granger");


        //WHEN AND THEN
        this.mockMvc.perform(post(this.baseUrl+"/wizards").contentType(MediaType.APPLICATION_JSON).content(json).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect( jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.name").value("Hermione Granger"));


    }
    @Test
    @DisplayName("Check updateWizard with valid input (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateWizardSuccess() throws Exception {
        Wizard a = new Wizard();
        a.setId(1);
        a.setName("Updated wizard name");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Updated wizard name"));
    }
    @Test
    @DisplayName("Check Update Artifact with invalid input (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateWizardErrorWithNonExistentID() throws Exception {

        WizardDto wizardDto = new WizardDto(5, // This id does not exist in the database.
                "Updated wizard name",
                0);

        String json = this.objectMapper.writeValueAsString(wizardDto);

        // When and then
        this.mockMvc.perform(put( this.baseUrl+"/wizards/5").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    @DisplayName("Check deleteWizard with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteWizardSuccess () throws Exception {

        //when and then
        this.mockMvc.perform(delete(this.baseUrl+"/wizards/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect( jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());



    }

    @Test
    @DisplayName("Check deleteWizard  with invalid input (DELETE)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteWizardErrorWithNonexistentId() throws Exception {


        //WHEN AND THEN
        this.mockMvc.perform(delete(this.baseUrl+"/wizards/5").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect( jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check assignArtifact with valid input (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void  testAssignArtifactSuccess () throws Exception {


        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }
    @Test
    @DisplayName("Check assignArtifact with invalid wizardID (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception {


        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/wizards/5/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }


    @Test
    @DisplayName("Check assignArtifact with invalid artifactID (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception {


        this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/1250808601744904199").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }


}
