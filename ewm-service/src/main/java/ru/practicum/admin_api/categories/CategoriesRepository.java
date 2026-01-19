package ru.practicum.admin_api.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.admin_api.categories.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    @Override
    Page<Category> findAll(Pageable pageable);
}
