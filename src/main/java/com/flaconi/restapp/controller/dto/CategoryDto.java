package com.flaconi.restapp.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO that models a Category
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto implements Serializable {

    private static final long serialVersionUID = 12368946595652735L;

    private String id;

    private String name;

    private String slug;

    private boolean isVisible;

    private CategoryDto parentCategory;

    public CategoryDto() {
    }

    public CategoryDto(String id) {
        this.id = id;
    }

    public CategoryDto(String id, String slug) {
        this.id = id;
        this.slug = slug;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public CategoryDto getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(CategoryDto parentCategory) {
        this.parentCategory = parentCategory;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDto categoryDto = (CategoryDto) o;
        return Objects.equals(id, categoryDto.id) &&
                Objects.equals(name, categoryDto.name) &&
                Objects.equals(slug, categoryDto.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, slug);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CategoryDto{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", slug='").append(slug).append('\'');
        sb.append(", isVisible=").append(isVisible);
        sb.append(", parentCategory=").append(parentCategory == null ? null : parentCategory.getId());        ;
        sb.append('}');
        return sb.toString();
    }

}
