package com.shoppingCartBackend.shoppingCartBackend.service.product;

import com.shoppingCartBackend.shoppingCartBackend.dto.ImageDto;
import com.shoppingCartBackend.shoppingCartBackend.dto.ProductDto;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.ResourceNotFoundException;
import com.shoppingCartBackend.shoppingCartBackend.model.Category;
import com.shoppingCartBackend.shoppingCartBackend.model.Image;
import com.shoppingCartBackend.shoppingCartBackend.repository.CategoryRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.ImageRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.ProductRepository;
import com.shoppingCartBackend.shoppingCartBackend.model.Product;
import com.shoppingCartBackend.shoppingCartBackend.request.AddProductRequest;
import com.shoppingCartBackend.shoppingCartBackend.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "products") // تحدد اسم الـ cache مرة وحدة هون
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Override
    public Product addProduct(AddProductRequest request) {
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category);
    }

    @Override
    @Cacheable(key = "#id")
    public Product getProductById(Long id) {
        System.out.println(">>> Cache MISS - Fetching product " + id + " from DB");
        return productRepository.findByIdWithImages(id)   // ← changed from findById
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    @Cacheable(key = "'all'")
    public List<Product> getAllProducts() {
        System.out.println(">>> Cache MISS - Fetching ALL products from DB");
        return productRepository.findAllWithImages();  // ← changed from findAll()
    }

    @Override
    @Cacheable(key = "'category:' + #category")
    public List<Product> getProductsByCategory(String category) {
        System.out.println(">>> Cache MISS - Fetching by category: " + category);
        return productRepository.findByCategoryName(category);
    }

    @Override
    @Cacheable(key = "'brand:' + #brand")
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    @Cacheable(key = "'cat-brand:' + #category + ':' + #brand")
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    @Cacheable(key = "'name:' + #name")
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    @Cacheable(key = "'brand-name:' + #brand + ':' + #name")
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    // allEntries=true يمسح كل الـ cache لأن أي تعديل يأثر على كل الـ lists
    @CacheEvict(allEntries = true)
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () -> { throw new ResourceNotFoundException("Product not found"); });
    }

    @Override
    @CacheEvict(allEntries = true)  // نفس السبب - التعديل يأثر على lists
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());
        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }

    // ---- الـ methods دي ما بتحتاج cache (بتروح للـ DB مباشرة) ----

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }
}
