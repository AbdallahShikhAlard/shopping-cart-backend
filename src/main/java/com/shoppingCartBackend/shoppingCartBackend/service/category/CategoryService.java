package com.shoppingCartBackend.shoppingCartBackend.service.category;

import com.shoppingCartBackend.shoppingCartBackend.exeptions.AlreadyExistsException;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.ResourceNotFoundException;
import com.shoppingCartBackend.shoppingCartBackend.model.Category;
import com.shoppingCartBackend.shoppingCartBackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category).filter(c-> !categoryRepository.existsByName(c.getName()))
                .map(categoryRepository ::save ).orElseThrow(()-> new AlreadyExistsException(category.getName()+" already exists"));
    }

    /*
    id --> which category to update
    category --> new data to update with

    If the category exsists, it updates its name and saves it.
    If not, it throwa a ResourceNotFoundException.
     */
    @Override
    public Category updateCategory(Category category, Long id) {
        //getCategoryById(id) fetches the existing category
        // and retruns a Category object
        //wraps result in an Optional, so you can safely handle null
        return Optional.ofNullable(getCategoryById(id))
            //sets the name of the oldCategory to the given name
            .map(oldCategory -> {
            oldCategory.setName(category.getName());
            return categoryRepository.save(oldCategory);
        }).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete, ()-> {
            throw new ResourceNotFoundException("Category not found");
        });

    }
}
