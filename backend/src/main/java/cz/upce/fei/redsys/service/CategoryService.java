package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Category;
import cz.upce.fei.redsys.dto.CategoryDto;
import cz.upce.fei.redsys.dto.CategoryDto.CategoryResponse;
import cz.upce.fei.redsys.dto.CategoryDto.CreateCategoryRequest;
import cz.upce.fei.redsys.dto.CategoryDto.PaginatedCategoryResponse;
import cz.upce.fei.redsys.dto.CategoryDto.UpdateCategoryRequest;
import cz.upce.fei.redsys.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CreateCategoryRequest req) {
        log.debug("Creating category with name '{}'", req.name());

        if (categoryRepository.existsByName(req.name())) {
            throw new IllegalStateException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(req.name());
        category.setDescription(req.description());

        CategoryResponse response = CategoryDto.toResponse(categoryRepository.save(category));
        log.debug("Category created: {}", response);
        return response;
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long id) {
        log.debug("Getting category with id {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return CategoryDto.toResponse(category);
    }

    @Transactional(readOnly = true)
    public PaginatedCategoryResponse list(Pageable pageable) {
        log.debug("Listing categories: {}", pageable);
        Page<Category> page = categoryRepository.findAll(pageable);
        List<CategoryResponse> categories = page.getContent().stream()
                .map(CategoryDto::toResponse)
                .toList();

        return new PaginatedCategoryResponse(
                categories,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest req) {
        log.debug("Updating category with id {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!category.getName().equals(req.name()) && categoryRepository.existsByName(req.name())) {
            throw new IllegalStateException("Category with this name already exists");
        }

        category.setName(req.name());
        category.setDescription(req.description());

        CategoryResponse response = CategoryDto.toResponse(categoryRepository.save(category));
        log.debug("Category updated: {}", response);
        return response;
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting category with id {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllByIds(Set<Long> ids) {
        return categoryRepository.findAllById(ids);
    }
}
