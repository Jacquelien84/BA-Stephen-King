package nl.oudhoff.bastephenking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldCreateBook() throws Exception {

        String requestJson = """
                {
                "id": 8,
                "title": "Cujo",
                "author": "Stephen King",
                "originalTitle": "Cujo",
                "released": 1981,
                "movieAdaptation": "Cujo - 1983",
                "description": "Stel je een reusachtige Sint Bernard voor. Een grote lobbes en de beste vriend van de tienjarige Brett Camber. Helaas komt hier verandering in wanneer Cujo in aanraking komt met een ernstig zieke vleermuis. Door de hondsdolheid die hij hierdoor oploopt is niets of niemand meer veilig."
                }
                """;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String locationHeader = result.getResponse().getHeader("Location");
        assertThat(locationHeader, matchesPattern("^.*/books/\\d+$"));
    }

    @Test
    void shouldGetBookByTitle() throws Exception {
        // Maak het boek via MockMvc POST-aanroep
        String bookJson = """
            {
            "id": 1,
            "title": "Carrie",
            "author": "Stephen King",
            "originalTitle": "Carrie",
            "released": 1974,
            "movieAdaptation": "Carrie - 1976",
            "description": "Carrie White is een buitenbeentje op haar middelbare school. Ze wordt gepest door haar klasgenoten en thuis mishandeld door haar religieuze moeder. Wanneer ze ontdekt dat ze telekinetische krachten heeft, neemt ze wraak op iedereen die haar ooit kwaad heeft gedaan."
            }
            """;

        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Valideer dat het boek correct is aangemaakt
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        // Haal het boek op via de titel en valideer de velden
        mockMvc.perform(get("/books/title/{title}", "Carrie")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Carrie"))
                .andExpect(jsonPath("$.author").value("Stephen King"))
                .andExpect(jsonPath("$.originalTitle").value("Carrie"))
                .andExpect(jsonPath("$.released").value(1974))
                .andExpect(jsonPath("$.movieAdaptation").value("Carrie - 1976"))
                .andExpect(jsonPath("$.description").value("Carrie White is een buitenbeentje op haar middelbare school. Ze wordt gepest door haar klasgenoten en thuis mishandeld door haar religieuze moeder. Wanneer ze ontdekt dat ze telekinetische krachten heeft, neemt ze wraak op iedereen die haar ooit kwaad heeft gedaan."));
    }
}
