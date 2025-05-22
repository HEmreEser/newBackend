package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.CreateItemDto;
import edu.hm.cs.kreisel_backend.dto.ItemDto;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void testCreateItem_ShouldReturnCreatedItem() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ItemDto responseDto = new ItemDto();
        responseDto.setId(itemId);
        responseDto.setName("New Item");

        Mockito.when(itemService.createItem(any(CreateItemDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Item\", \"status\": \"Verfugbar\", \"location\": \"Lothstraße\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.name").value("New Item"));
    }

    @Test
    void testUpdateItem_ShouldReturnUpdatedItem() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ItemDto responseDto = new ItemDto();
        responseDto.setId(itemId);
        responseDto.setName("Updated Item");

        Mockito.when(itemService.updateItem(eq(itemId), any(CreateItemDto.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(put("/api/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Item\", \"status\": \"Verfugbar\", \"location\": \"Lothstraße\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void testGetAllItems_ShouldReturnListOfItems() throws Exception {
        // Arrange
        ItemDto itemDto = new ItemDto();
        itemDto.setId(UUID.randomUUID());
        itemDto.setName("Item 1");

        Mockito.when(itemService.getAllItems()).thenReturn(List.of(itemDto));

        // Act & Assert
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Item 1"));
    }

    @Test
    void testDeleteItem_ShouldReturnOk() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/items/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());

        Mockito.verify(itemService).deleteItem(any(UUID.class));
    }
}