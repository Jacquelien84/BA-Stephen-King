package nl.oudhoff.bastephenking.service;

import nl.oudhoff.bastephenking.dto.input.UserInputDto;
import nl.oudhoff.bastephenking.dto.output.UserOutputDto;
import nl.oudhoff.bastephenking.model.User;
import nl.oudhoff.bastephenking.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepo;

    @InjectMocks
    UserService userService;

    User user1;
    User user2;
    User user3;
    UserInputDto userInputDto1;
    UserInputDto userInputDto2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("Jacquelien");
        user1.setEmail("jacquelien@fictief.nl");
        user1.setPassword("catseye");

        user2 = new User();
        user2.setUsername("Karel");
        user2.setEmail("karel@fictief.nl");
        user2.setPassword("icecreamtruck");

        user3 = new User();
        user3.setUsername("Pennywise");
        user3.setEmail("clown@fictief.nl");
        user3.setPassword("balloon");

        userInputDto1 = new UserInputDto();
        userInputDto1.setUsername("Jacquelien");
        userInputDto1.setEmail("jacquelien@fictief.nl");
        userInputDto1.setPassword("catseye");

        userInputDto2 = new UserInputDto();
        userInputDto2.setUsername("Karel");
        userInputDto2.setEmail("karel@fictief.nl");
        userInputDto2.setPassword("icecreamtruck");
    }

    @AfterEach
    void tearDown() {
    }

    //    Test 7
    @Test
    @DisplayName("Should find a user by username")
    void shouldLoadUserByUsername() {

        // Arrange
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user1));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("Jacquelien");

        // Assert
        assertEquals("Jacquelien", userDetails.getUsername());
        verify(userRepo).findByUsername("Jacquelien");
    }

    //    Test 8
    @Test
    @DisplayName("Should find all users")
    void shouldGetAll() {

        // Arrange
        when(userRepo.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserOutputDto> users = userService.getAll();

        // Assert
        assertEquals(2, users.size());
        assertEquals("Jacquelien", user1.getUsername());
        assertEquals("catseye", user1.getPassword());
        assertEquals("Karel", user2.getUsername());
        assertEquals("icecreamtruck", user2.getPassword());
        assertEquals("karel@fictief.nl", user2.getEmail());
    }

    //    Test 9
    @Test
    @DisplayName("Should delete someone as if they encountered Pennywise")
    void shouldDeletePennywise() {

        // Arrange
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user3));

        // Act
        userService.deleteUser("Pennywise");

        // Assert
        verify(userRepo).deleteByUsername("Pennywise");
    }
}
