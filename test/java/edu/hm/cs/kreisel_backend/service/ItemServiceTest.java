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
        // Arrange
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(mockItem));

        // Act
        List<ItemDto> items = itemService.getAllItems();

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Item Name", items.get(0).getName());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetAllItems_EmptyList() {
        // Arrange
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ItemDto> items = itemService.getAllItems();

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetItemById() {
        // Arrange
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));

        // Act
        ItemDto item = itemService.getItemById(itemId);

        // Assert
        assertNotNull(item);
        assertEquals(itemId, item.getId());
        assertEquals("Item Name", item.getName());
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testGetItemById_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(itemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.getItemById(nonExistentId);
        });
        assertEquals("Item not found", exception.getMessage());
        verify(itemRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testCreateItem_WithCategoryAndSubcategory() {
        // Arrange
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

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(mockSubcategory));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto createdItem = itemService.createItem(createItemDto);

        // Assert
        assertNotNull(createdItem);
        assertEquals("Item Name", createdItem.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(subcategoryRepository, times(1)).findById(subcategoryId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testCreateItem_WithoutCategoryAndSubcategory() {
        // Arrange
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
        createItemDto.setCategoryId(null);
        createItemDto.setSubcategoryId(null);

        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto createdItem = itemService.createItem(createItemDto);

        // Assert
        assertNotNull(createdItem);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(categoryRepository, never()).findById(any());
        verify(subcategoryRepository, never()).findById(any());
    }

    @Test
    void testCreateItem_WithNonExistentCategory() {
        // Arrange
        CreateItemDto createItemDto = new CreateItemDto();
        createItemDto.setName("New Item");
        createItemDto.setCategoryId(categoryId);
        createItemDto.setSubcategoryId(null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto createdItem = itemService.createItem(createItemDto);

        // Assert
        assertNotNull(createdItem);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testCreateItem_WithNonExistentSubcategory() {
        // Arrange
        CreateItemDto createItemDto = new CreateItemDto();
        createItemDto.setName("New Item");
        createItemDto.setCategoryId(null);
        createItemDto.setSubcategoryId(subcategoryId);

        when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto createdItem = itemService.createItem(createItemDto);

        // Assert
        assertNotNull(createdItem);
        verify(subcategoryRepository, times(1)).findById(subcategoryId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateItem_Success() {
        // Arrange
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

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(mockSubcategory));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto updatedItem = itemService.updateItem(itemId, updateDto);

        // Assert
        assertNotNull(updatedItem);
        assertEquals("Item Name", updatedItem.getName());
        verify(itemRepository, times(1)).findById(itemId);
        verify(categoryRepository).findById(categoryId);
        verify(subcategoryRepository).findById(subcategoryId);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testUpdateItem_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        CreateItemDto updateDto = new CreateItemDto();
        when(itemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.updateItem(nonExistentId, updateDto);
        });
        assertEquals("Item nicht gefunden", exception.getMessage());
        verify(itemRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testUpdateItem_WithNullCategoryAndSubcategory() {
        // Arrange
        CreateItemDto updateDto = new CreateItemDto();
        updateDto.setName("Updated Item");
        updateDto.setCategoryId(null);
        updateDto.setSubcategoryId(null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto updatedItem = itemService.updateItem(itemId, updateDto);

        // Assert
        assertNotNull(updatedItem);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(categoryRepository, never()).findById(any());
        verify(subcategoryRepository, never()).findById(any());
    }

    @Test
    void testUpdateItem_WithNonExistentCategory() {
        // Arrange
        CreateItemDto updateDto = new CreateItemDto();
        updateDto.setName("Updated Item");
        updateDto.setCategoryId(categoryId);
        updateDto.setSubcategoryId(null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto updatedItem = itemService.updateItem(itemId, updateDto);

        // Assert
        assertNotNull(updatedItem);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateItem_WithNonExistentSubcategory() {
        // Arrange
        CreateItemDto updateDto = new CreateItemDto();
        updateDto.setName("Updated Item");
        updateDto.setCategoryId(null);
        updateDto.setSubcategoryId(subcategoryId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // Act
        ItemDto updatedItem = itemService.updateItem(itemId, updateDto);

        // Assert
        assertNotNull(updatedItem);
        verify(subcategoryRepository, times(1)).findById(subcategoryId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testDeleteItem_Success() {
        // Arrange
        when(itemRepository.existsById(itemId)).thenReturn(true);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        verify(itemRepository, times(1)).existsById(itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void testDeleteItem_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(itemRepository.existsById(nonExistentId)).thenReturn(false);

        // Act
        itemService.deleteItem(nonExistentId);

        // Assert
        verify(itemRepository, times(1)).existsById(nonExistentId);
        verify(itemRepository, never()).deleteById(any());
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
    void testGetItemsByLocation_EmptyResult() {
        // Arrange
        Item.Location location = Item.Location.Pasing;
        when(itemRepository.findByLocation(location)).thenReturn(Collections.emptyList());

        // Act
        List<ItemDto> items = itemService.getItemsByLocation(location);

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findByLocation(location);
    }

    @Test
    void testSearchItems_WithSearchTerm_InName() {
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
    void testSearchItems_WithSearchTerm_InDescription() {
        // Arrange
        String searchTerm = "Description";
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
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSearchTerm_InBrand() {
        // Arrange
        String searchTerm = "Brand";
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
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSearchTerm_NoMatch() {
        // Arrange
        String searchTerm = "NoMatch";
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
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSearchTerm_NullFields() {
        // Arrange
        Item itemWithNulls = new Item();
        itemWithNulls.setId(UUID.randomUUID());
        itemWithNulls.setName(null);
        itemWithNulls.setDescription(null);
        itemWithNulls.setBrand(null);
        itemWithNulls.setGender(Item.Gender.Damen);
        itemWithNulls.setSize(Item.Size.M);
        itemWithNulls.setStatus(Item.Status.Verfugbar);

        String searchTerm = "Item";
        List<Item> allItems = Collections.singletonList(itemWithNulls);
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
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithGender_Match() {
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
    void testSearchItems_WithGender_NoMatch() {
        // Arrange
        Item.Gender gender = Item.Gender.Herren;
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
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithCategoryId_Match() {
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
    void testSearchItems_WithCategoryId_NoMatch() {
        // Arrange
        UUID differentCategoryId = UUID.randomUUID();
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.of(differentCategoryId),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithCategoryId_ItemHasNullCategory() {
        // Arrange
        Item itemWithoutCategory = new Item();
        itemWithoutCategory.setId(UUID.randomUUID());
        itemWithoutCategory.setCategory(null);

        UUID categoryId = UUID.randomUUID();
        List<Item> allItems = Collections.singletonList(itemWithoutCategory);
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
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSubcategoryId_Match() {
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
    void testSearchItems_WithSubcategoryId_NoMatch() {
        // Arrange
        UUID differentSubcategoryId = UUID.randomUUID();
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(differentSubcategoryId),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSubcategoryId_ItemHasNullSubcategory() {
        // Arrange
        Item itemWithoutSubcategory = new Item();
        itemWithoutSubcategory.setId(UUID.randomUUID());
        itemWithoutSubcategory.setSubcategory(null);

        UUID subcategoryId = UUID.randomUUID();
        List<Item> allItems = Collections.singletonList(itemWithoutSubcategory);
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
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSubcategoryAndCategory_Match() {
        // Arrange
        UUID categoryId = mockCategory.getId();
        UUID subcategoryId = mockSubcategory.getId();
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.of(categoryId),
                Optional.of(subcategoryId),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSubcategoryAndCategory_CategoryMismatch() {
        // Arrange
        UUID differentCategoryId = UUID.randomUUID();
        UUID subcategoryId = mockSubcategory.getId();
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.of(differentCategoryId),
                Optional.of(subcategoryId),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSubcategoryAndCategory_SubcategoryHasNullCategory() {
        // Arrange
        Subcategory subcategoryWithoutCategory = new Subcategory();
        subcategoryWithoutCategory.setId(subcategoryId);
        subcategoryWithoutCategory.setCategory(null);

        Item itemWithSubcategoryWithoutCategory = new Item();
        itemWithSubcategoryWithoutCategory.setId(UUID.randomUUID());
        itemWithSubcategoryWithoutCategory.setSubcategory(subcategoryWithoutCategory);

        UUID categoryId = UUID.randomUUID();
        List<Item> allItems = Collections.singletonList(itemWithSubcategoryWithoutCategory);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.of(categoryId),
                Optional.of(subcategoryId),
                Optional.empty(),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSize_Match() {
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
    void testSearchItems_WithSize_NoMatch() {
        // Arrange
        String size = "XL";
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
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testSearchItems_WithSize_InvalidSize() {
        // Arrange
        String invalidSize = "INVALID";
        List<Item> allItems = Collections.singletonList(mockItem);
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act
        List<ItemDto> items = itemService.searchItems(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(invalidSize),
                Optional.empty()
        );

        // Assert
        assertNotNull(items);
        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

}