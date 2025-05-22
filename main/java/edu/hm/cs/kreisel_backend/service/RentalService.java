package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateRentalDto;
import edu.hm.cs.kreisel_backend.dto.RentalDto;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Rental;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import edu.hm.cs.kreisel_backend.repository.RentalRepository;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired private RentalRepository rentalRepository;
    @Autowired private UserRepository   userRepository;
    @Autowired private ItemRepository   itemRepository;

    /* ---------- Public Queries ---------- */

    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll()
                .stream().map(this::toDto).toList();
    }

    public List<RentalDto> getAllActiveRentals() {
        return rentalRepository.findAllByReturnedFalse()
                .stream().map(this::toDto).toList();
    }

    public List<RentalDto> getRentalsByUser(UUID userId) {
        return rentalRepository.findByUserId(userId)
                .stream().map(this::toDto).toList();
    }

    public List<RentalDto> getActiveRentalsByUser(UUID userId) {
        return rentalRepository.findByUserIdAndReturnedFalse(userId)
                .stream().map(this::toDto).toList();
    }

    /* ---------- Create & Return ---------- */

    public RentalDto createRental(CreateRentalDto dto) {
        /* ── 1. User prüfen ───────────────────────────────────── */
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        long active = rentalRepository.countByUserIdAndReturnedFalse(user.getId());
        if (active >= 5)  // max 5 gleichzeitige Leihen
            throw new RuntimeException("Maximal 5 aktive Ausleihen erlaubt");

        /* ── 2. Item prüfen ───────────────────────────────────── */
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.isAvailable())
            throw new RuntimeException("Item ist derzeit nicht verfügbar");

        /* ── 3. Zeitraum prüfen (≤ 120 Tage) ─────────────────── */
        if (ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) > 120)
            throw new RuntimeException("Leihdauer max. 4 Monate");

        /* ── 4. Rental anlegen und speichern ─────────────────── */
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setItem(item);
        rental.setStartDate(dto.getStartDate());
        rental.setEndDate(dto.getEndDate());
        rental.setReturned(false);

        item.setStatus(Item.Status.NichtVerfugbar);
        itemRepository.save(item);

        return toDto(rentalRepository.save(rental));
    }

    public RentalDto returnItem(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental nicht gefunden"));

        if (rental.isReturned())
            throw new RuntimeException("Item wurde bereits zurückgegeben");

        rental.setReturned(true);
        rental.setReturnedAt(LocalDate.now());

        Item item = rental.getItem();
        item.setStatus(Item.Status.Verfugbar);
        itemRepository.save(item);

        return toDto(rentalRepository.save(rental));
    }

    /* ---------- Overdue Rentals ---------- */
    public void updateOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository
                .findAllByReturnedFalseAndEndDateBefore(LocalDate.now());

        overdueRentals.forEach(rental -> {
            rental.setReturned(true);
            rental.setReturnedAt(LocalDate.now());
            rental.getItem().setStatus(Item.Status.Verfugbar);
        });

        rentalRepository.saveAll(overdueRentals);
        itemRepository.saveAll(overdueRentals
                .stream().map(Rental::getItem).collect(Collectors.toList()));
    }

    /* ---------- Mapper ---------- */

    private RentalDto toDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.setId(rental.getId());
        dto.setUserId(rental.getUser().getId());
        dto.setItemId(rental.getItem().getId());
        dto.setStartDate(rental.getStartDate());
        dto.setEndDate(rental.getEndDate());
        dto.setReturned(rental.isReturned());
        dto.setReturnedAt(rental.getReturnedAt());
        return dto;
    }
}