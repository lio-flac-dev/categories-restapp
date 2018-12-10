package com.flaconi.restapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Liodegar on 12/10/2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Category implements Serializable {

    private static final long serialVersionUID = 8483342465956521524L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false, unique = true)
    @ColumnTransformer(
            read = "BIN_TO_UUID(id)",
            write = "uuid_to_bin(?)")
    private UUID id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "slug", columnDefinition = "VARCHAR(50)", nullable = false, unique = true)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "parent_category_id", columnDefinition = "BINARY(16)")
    private Category parentCategory;

    @Column(name = "is_visible", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isVisible;

    public Category() {
    }

    public Category(UUID id) {
        this.id = id;
    }

    public Category(UUID id, String slug) {
        this.id = id;
        this.slug = slug;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Transient
    @JsonIgnore
    public String getIdAsString() {
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(name, category.name) &&
                Objects.equals(slug, category.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, slug);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Category{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", slug='").append(slug).append('\'');
        sb.append(", isVisible=").append(isVisible);
        sb.append(", parentCategory=").append(parentCategory == null ? null : parentCategory.getId());
        sb.append('}');
        return sb.toString();
    }
}
