package edu.tcu.cs.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import org.hamcrest.Matchers;
import org.hibernate.ObjectNotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;
    final String baseUrl = "/api/v1";

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();
        Artifact a1 = new Artifact("1250808601744904191", "Deluminator", "A device invented by Dumbledore.", "ImageUrl");
        Artifact a2 = new Artifact("1250808601744904192", "Invisibility Cloak", "Makes the wearer invisible.", "ImageUrl");
        Artifact a3 = new Artifact("1250808601744904193", "Elder Wand", "Powerful wand made of elder wood.", "ImageUrl");
        Artifact a4 = new Artifact("1250808601744904194", "The Marauder's Map", "Magical map of Hogwarts.", "ImageUrl");
        Artifact a5 = new Artifact("1250808601744904195", "The Sword Of Gryffindor", "Goblin-made sword.", "ImageUrl");
        Artifact a6 = new Artifact("1250808601744904196", "Resurrection Stone", "Brings back deceased loved ones.", "ImageUrl");

        this.artifacts.addAll(List.of(a1, a2, a3, a4, a5, a6));
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    void testFindArtifactByIdSuccess() throws Exception{
        // Given
        given(this.artifactService.findById("1250808601744904191")).willReturn(this.artifacts.get(0));

        // When and then
        this.mockMvc.perform(get("/api/v1/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));

    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        given(this.artifactService.findById("1250808601744904194")).willReturn(this.artifacts.get(0));

        this.mockMvc.perform(delete(baseUrl + "/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
//    @Test
//    void testFindArtifactByIdNotFound() throw Exception{
//        this.mockMvc.perform(get("/api/v1/artifacts/1250808601744904194")).accept(MediaType.APPLICATION_JSON).
//                andExpect(jsonPath("$.flag").value(true))
//                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
//                .andExpect(jsonPath("$.message").value("Find One Success"))
//                .andExpect(jsonPath("$.data").value("1250808601744904194"));
//    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        given(this.artifactService.findAll()).willReturn(this.artifacts);

        this.mockMvc.perform(get("/api/v1/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data[0].name").value("Deluminator"))
                .andExpect(jsonPath("$.data[1].id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data[1].name").value("Invisibility Cloak"));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {
        ArtifactDto artifactDto = new ArtifactDto(null, "Remembrall",
                "A Remembrall turns red when something is forgotten.", "ImageUrl", null);
        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("1250808601744904197");
        savedArtifact.setName("Remembrall");
        savedArtifact.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered.");
        savedArtifact.setImageUrl("ImageUrl");

        given(this.artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);

        this.mockMvc.perform(post("/api/v1/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        ArtifactDto artifactDto = new ArtifactDto("1250808601744904192", "Invisibility Cloak",
                "A new description.", "ImageUrl", null);
        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setId("1250808601744904192");
        updatedArtifact.setName("Invisibility Cloak");
                updatedArtifact.setDescription("A new description.");
    updatedArtifact.setImageUrl("ImageUrl");

        given(this.artifactService.update(eq("1250808601744904192"), Mockito.any(Artifact.class)))
                .willReturn(updatedArtifact);

        this.mockMvc.perform(put(baseUrl + "/artifacts/1250808601744904192")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(updatedArtifact.getId()))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
    }
    @Test
    void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto("1250808601744904192",
                "Invisibility Cloak",
                "A new description.",
                "ImageUrl",
                null);
        String json = this.objectMapper.writeValueAsString(artifactDto);

        given(this.artifactService.update(eq("1250808601744904192"), Mockito.any(Artifact.class))).willThrow(new ObjectNotFoundException("artifact", "1250808601744904192"));

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1250808601744904192").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904192 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }



    @Test
    void testDeleteArtifactSuccess() throws Exception{
        //Given
            doNothing().when(this.artifactService).delete("1250808601744904191");

        //When and then
        this.mockMvc.perform(delete(baseUrl + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191 :("))
                .andExpect(jsonPath("$.data").isEmpty());



    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        //Given
        doThrow(new ArtifactNotFoundException("1250808601744904191")).when(this.artifactService).delete("1250808601744904191");

        //When and then
        this.mockMvc.perform(delete(baseUrl + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191 :("))
                .andExpect(jsonPath("$.data").isEmpty());



    }




}
