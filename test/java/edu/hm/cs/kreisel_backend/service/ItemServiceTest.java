package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateItemDto;
import edu.hm.cs.kreisel_backend.dto.ItemDto;
import edu.hm.cs.kreisel_backend.model.Category;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Subcategory;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import edu.hm.cs.kreisel_backend.repository.CategoryRepository;
import edu.hm.cs.kreisel_backend.repository.SubcategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubcategoryRepository subcategoryRepository;

    private UUID itemId;
    private UUID categoryId;
    private UUID subcategoryId;
    private Item mockItem;
    private Category mockCategory;
    private Subcategory mockSubcategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        itemId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        subcategoryId = UUID.randomUUID();

        mockCategory = new Category();
        mockCategory.setId(categoryId);

        mockSubcategory = new Subcategory();
        mockSubcategory.setId(subcategoryId);
        mockSubcategory.setCategory(mockCategory);

        mockItem = new Item();
        mockItem.setId(itemId);
        mockItem.setName("Item Name");
        mockItem.setDescription("Item Description");
        mockItem.setBrand("Brand");
        mockItem.setAvailableFrom(LocalDate.now());
        mockItem.setImageUrl("http://example.com/image.jpg");
        mockItem.setSize(Item.Size.M);
        mockItem.setGender(Item.Gender.Damen);
        mockItem.setCondition(Item.Condition.Neu);
        mockItem.setStatus(Item.Status.Verfugbar);
        mockItem.setLocation(Item.Location.Lothstraße);
        mockItem.setCategory(mockCategory);
        mockItem.setSubcategory(mockSubcategory);
    }

    @Test
    void testGetAllItems() {
        // Stub Repository-Methode
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(mockItem));

        // Teste die Service-Methode
        List<ItemDto> items = itemService.getAllItems();

        // Assertions
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Item Name", items.get(0).getName());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetItemById() {
        // Stub Repository-Methode
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));

        // Teste die Service-Methode
        ItemDto item = itemService.getItemById(itemId);

        // Assertions
        assertNotNull(item);
        assertEquals(itemId, item.getId());
        assertEquals("Item Name", item.getName());
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testCreateItem() {
        // Vorbereiten des DTOs
        CreateItemDto createItemDto = new CreateItemDto();
        createItemDto.setName("New Item");
        createItemDto.setDescription("New Description");
        createItemDto.setBrand("New Brand");
        createItemDto.setAvailableFrom(LocalDate.now());
        createItemDto.setImageUrl("http://example.com/new-image.jpg");
        createItemDto.setSize(Item.Size.S);
        createItemDto.setGender(Item.Gender.Herren);
        createItemDto.setCondition(Item.Condition.Gebraucht);
        createItemDto.setStatus(Item.Status.NichtVerfugbar);
        createItemDto.setLocation(Item.Location.Pasing);
        createItemDto.setCategoryId(categoryId);
        createItemDto.setSubcategoryId(subcategoryId);

        // Mocking der Repository-Interaktionen
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(mockSubcategory));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Teste die Service-Methode
        ItemDto createdItem = itemService.createItem(createItemDto);

        // Assertions
        assertNotNull(createdItem);
        assertEquals("Item Name", createdItem.getName()); // Name des mockItem
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(subcategoryRepository, times(1)).findById(subcategoryId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateItem() {
        // Vorbereiten des DTOs
        CreateItemDto updateDto = new CreateItemDto();
        updateDto.setName("Updated Item");
        updateDto.setDescription("Updated Description");
        updateDto.setBrand("Updated Brand");
        updateDto.setAvailableFrom(LocalDate.now());
        updateDto.setImageUrl("http://example.com/updated-image.jpg");
        updateDto.setSize(Item.Size.XL);
        updateDto.setGender(Item.Gender.Damen);
        updateDto.setCondition(Item.Condition.Neu);
        updateDto.setStatus(Item.Status.Verfugbar);
        updateDto.setLocation(Item.Location.Karlstraße);
        updateDto.setCategoryId(categoryId);
        updateDto.setSubcategoryId(subcategoryId);

        // Mocking der Repository-Interaktionen
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(mockSubcategory));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Teste die Service-Methode
        ItemDto updatedItem = itemService.updateItem(itemId, updateDto);

        // Assertions
        assertNotNull(updatedItem);
        assertEquals("Item Name", updatedItem.getName()); // Mock-Wert aus mockItem
        verify(itemRepository, times(1)).findById(itemId);
        verify(categoryRepository).findById(categoryId);
        verify(subcategoryRepository).findById(subcategoryId);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testDeleteItem() {
        // Mocking der Repository-Methode
        when(itemRepository.existsById(itemId)).thenReturn(true);

        // Teste die Service-Methode
        itemService.deleteItem(itemId);

        // Verifizierungen
        verify(itemRepository, times(1)).existsById(itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
    }
    @Test
    void testGetItemsByLocation() {
        // Arrange
        Item.Location location = Item.Location.Lothstraße;
        when(itemRepository.findByLocation(location)).thenReturn(Collections.singletonList(mockItem));

        // Act
        List<ItemDto> items = itemService.getItemsByLocation(location);

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(mockItem.getName(), items.get(0).getName());
        assertEquals(location, items.get(0).getLocation());
        verify(itemRepository, times(1)).findByLocation(location);
    }

    @Test
    void testSearchItems_WithSearchTerm() {
        // Arrange
        String searchTerm = "Item";
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.of(searchTerm),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithGender() {
        // Arrange
        Item.Gender gender = Item.Gender.Damen;
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.of(gender),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(gender, items.get(0).getGender());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithCategoryId() {
        // Arrange
        UUID categoryId = mockCategory.getId();
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.of(categoryId),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(mockCategory.getId(), items.get(0).getCategoryId());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSubcategoryId() {
        // Arrange
        UUID subcategoryId = mockSubcategory.getId();
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(subcategoryId),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(mockSubcategory.getId(), items.get(0).getSubcategoryId());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSize() {
        // Arrange
        String size = "M";
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(size),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(Item.Size.M, items.get(0).getSize());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithStatus() {
        // Arrange
        Item.Status status = Item.Status.Verfugbar;
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(status)
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(status, items.get(0).getStatus());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithMultipleParameters() {
        // Arrange
        Item.Gender gender = Item.Gender.Damen;
        Item.Status status = Item.Status.Verfugbar;
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.of(gender),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(status)
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(mockItem.getId(), items.get(0).getId());
        assertEquals(gender, items.get(0).getGender());
        assertEquals(status, items.get(0).getStatus());
        verify(itemRepository, times(1)).findAll();
    }
}
