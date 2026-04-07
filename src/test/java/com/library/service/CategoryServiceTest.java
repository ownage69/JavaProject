package com.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.library.dto.CategoryCreateDto;
import com.library.dto.CategoryDto;
import com.library.entity.Category;
import com.library.repository.CategoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAllShouldReturnMappedCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                createCategory(1L, "Classic"),
                createCategory(2L, "Science Fiction")
        ));

        List<CategoryDto> result = categoryService.findAll();

        assertThat(result)
                .extracting(CategoryDto::getName)
                .containsExactly("Classic", "Science Fiction");
    }

    @Test
    void findByIdShouldReturnCategoryDto() {
        when(categoryRepository.findById(4L))
                .thenReturn(Optional.of(createCategory(4L, "Mystery")));

        CategoryDto result = categoryService.findById(4L);

        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("Mystery");
    }

    @Test
    void createShouldSaveCategoryAndInvalidateCache() {
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(createCategory(7L, "Fantasy"));

        CategoryDto result = categoryService.create(new CategoryCreateDto("Fantasy"));

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getName()).isEqualTo("Fantasy");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void updateShouldModifyCategoryAndInvalidateCache() {
        Category category = createCategory(5L, "Old");
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto result = categoryService.update(5L, new CategoryCreateDto("New"));

        assertThat(result.getName()).isEqualTo("New");
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void deleteShouldRemoveCategoryAndInvalidateCache() {
        when(categoryRepository.existsById(6L)).thenReturn(true);

        categoryService.delete(6L);

        verify(categoryRepository).deleteById(6L);
        verify(bookService).invalidateFilterCache();
    }

    @Test
    void deleteShouldThrowWhenCategoryMissing() {
        when(categoryRepository.existsById(66L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(66L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Category not found with id: 66");
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
