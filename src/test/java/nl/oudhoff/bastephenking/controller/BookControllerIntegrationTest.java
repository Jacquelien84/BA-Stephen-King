package nl.oudhoff.bastephenking.controller;

import org.junit.jupiter.api.Order;
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
    @Order(1)
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
    @Order(2)
    void shouldGetBookById() throws Exception {
        // Maak het boek via MockMvc POST-aanroep
        String bookJson = """
                {
                "title": "Cujo",
                "author": "Stephen King",
                "originalTitle": "Cujo",
                "released": 1981,
                "movieAdaptation": "Cujo - 1983",
                "description": "Stel je een reusachtige Sint Bernard voor. Een grote lobbes en de beste vriend van de tienjarige Brett Camber. Helaas komt hier verandering in wanneer Cujo in aanraking komt met een ernstig zieke vleermuis. Door de hondsdolheid die hij hierdoor oploopt is niets of niemand meer veilig."
                }
                """;

        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Haal het ID van het aangemaakte boek uit de respons
        String location = result.getResponse().getHeader("Location");
        Long createdId = Long.valueOf(location.split("/books/")[8]);

        // Haal het boek op via de API en valideer de velden
        mockMvc.perform(get("/books/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Cujo"))
                .andExpect(jsonPath("$.author").value("Stephen King"))
                .andExpect(jsonPath("$.originalTitle").value("Cujo"))
                .andExpect(jsonPath("$.released").value(1981))
                .andExpect(jsonPath("$.movieAdaptation").value("Cujo - 1983"))
                .andExpect(jsonPath("$.description").value("Stel je een reusachtige Sint Bernard voor. Een grote lobbes en de beste vriend van de tienjarige Brett Camber. Helaas komt hier verandering in wanneer Cujo in aanraking komt met een ernstig zieke vleermuis. Door de hondsdolheid die hij hierdoor oploopt is niets of niemand meer veilig."));
    }
}

