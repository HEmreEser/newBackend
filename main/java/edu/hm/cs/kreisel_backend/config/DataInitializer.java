package edu.hm.cs.kreisel_backend.config;

import edu.hm.cs.kreisel_backend.model.*;
import edu.hm.cs.kreisel_backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DataInitializer für die "Kreisel"-Applikation.
 * Erstellt die initialen Kategorien, Unterkategorien, Benutzer, Items und Rentals.
 * Wird bei der ersten Ausführung ausgeführt und lädt Demodaten.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            CategoryRepository categoryRepository,
            SubcategoryRepository subcategoryRepository,
            ItemRepository itemRepository,
            UserRepository userRepository,
            RentalRepository rentalRepository,
            PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Prüfen, ob bereits Daten existieren. Initialisierung nur bei leerer Datenbank.
        if (categoryRepository.count() > 0 || userRepository.count() > 0) {
            return;
        }

        // Kategorien und Unterkategorien anlegen
        Map<String, Category> categories = new HashMap<>();
        categories.put("Kleidung", createCategory("Kleidung"));
        categories.put("Schuhe", createCategory("Schuhe"));
        categories.put("Accessoires", createCategory("Accessoires"));

        Subcategory jacken = createSubcategory("Jacken", categories.get("Kleidung"));
        Subcategory hosen = createSubcategory("Hosen", categories.get("Kleidung"));
        Subcategory sportschuhe = createSubcategory("Sport-Schuhe", categories.get("Schuhe"));

        // Benutzer erstellen
        User adminUser = createUser("admin@kreisel.de", "password123", User.Role.ADMIN);
        User standardUser = createUser("user@kreisel.de", "password123", User.Role.USER);

        // Items erstellen
        Item winterjacke = createItem(
                "Winterjacke", "Warme Jacke für den Winter", "North Face", LocalDate.now().minusDays(5),
                "https://example.com/winterjacke.jpg", Item.Size.L, Item.Gender.Herren,
                Item.Condition.Gebraucht, Item.Status.Verfugbar, Item.Location.Lothstraße,
                categories.get("Kleidung"), jacken
        );

        Item laufschuhe = createItem(
                "Laufschuhe", "Leichte Laufschuhe", "Adidas", LocalDate.now().minusDays(2),
                "https://example.com/laufschuhe.jpg", Item.Size.M, Item.Gender.Damen,
                Item.Condition.Neu, Item.Status.Verfugbar, Item.Location.Pasing,
                categories.get("Schuhe"), sportschuhe
        );

        Item stiefel = createItem(
                "Stiefel", "Wasserfeste Winterstiefel", "Timberland", LocalDate.now().minusDays(8),
                "https://example.com/stiefel.jpg", Item.Size.XL, Item.Gender.Herren,
                Item.Condition.Neu, Item.Status.Verfugbar, Item.Location.Karlstraße,
                categories.get("Schuhe"), null
        );

        // Rentals erstellen
        createRental(standardUser, winterjacke, LocalDate.now().minusDays(2), LocalDate.now().plusDays(3));
        createRental(standardUser, laufschuhe, LocalDate.now().minusDays(5), LocalDate.now().plusDays(1));
        createRental(standardUser, stiefel, LocalDate.now().minusDays(7), LocalDate.now().plusDays(2));
    }

    // Hilfsmethoden zur Erstellung von Entitäten für die Datenbank

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    private Subcategory createSubcategory(String name, Category category) {
        Subcategory subcategory = new Subcategory();
        subcategory.setName(name);
        subcategory.setCategory(category);
        return subcategoryRepository.save(subcategory);
    }

    private User createUser(String email, String password, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, String brand, LocalDate availableFrom, String imageUrl,
                            Item.Size size, Item.Gender gender, Item.Condition condition, Item.Status status,
                            Item.Location location, Category category, Subcategory subcategory) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setBrand(brand);
        item.setAvailableFrom(availableFrom);
        item.setImageUrl(imageUrl);
        item.setSize(size);
        item.setGender(gender);
        item.setCondition(condition);
        item.setStatus(status);
        item.setLocation(location);
        item.setCategory(category);
        item.setSubcategory(subcategory);
        return itemRepository.save(item);
    }

    private void createRental(User user, Item item, LocalDate startDate, LocalDate endDate) {
        Rental rental = new Rental();
        rental.setId(UUID.randomUUID());
        rental.setUser(user);
        rental.setItem(item);
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setReturned(false);
        rentalRepository.save(rental);
    }
}