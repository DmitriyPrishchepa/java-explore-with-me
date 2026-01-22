package ru.practicum.private_api.events;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.private_api.events.location.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
