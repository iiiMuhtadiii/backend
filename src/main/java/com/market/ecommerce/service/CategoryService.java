package com.market.ecommerce.service;

import com.market.ecommerce.entity.Category;
import com.market.ecommerce.exception.BadRequestException;
import com.market.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {

        if (categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("هذا القسم موجود مسبقاً");
        }

        category.setName(category.getName().trim());

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}