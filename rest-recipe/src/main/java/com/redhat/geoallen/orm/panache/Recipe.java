package com.redhat.geoallen.orm.panache;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Query;

import java.time.LocalDateTime;

import com.redhat.geoallen.view.RecipeFormData;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.redhat.geoallen.view.RecipeDTO;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Cacheable
@TypeDef(
    name = "list-array",
    typeClass = ListArrayType.class
)
public class Recipe extends PanacheEntity {

    private static final Logger LOG = Logger.getLogger(Recipe.class);

    @Column(length = 250)
    public String title;

    public String userid;

    // need to use the userid from the authenicated token
    @Column(length = 50)
    public String author;

    //photo 
    @Column(length = 50)
    public String image_name; 

    @Column(length = 50)
    public String cuisine;

    @Column(length = 50)
    public String course;

    @Column(length = 50)
    public String tags;

    
    /* @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "id"))
    public List<Ingredient> ingredients = new ArrayList<Ingredient>(); */
    @Type(type = "list-array")
    @Column(
        name = "ingredients",
        columnDefinition = "text[]"
    )
    public List<String> ingredients;
    
    @Column(length = 250)
    public String description;

    @Type(type = "list-array")
    @Column(
        name = "directions",
        columnDefinition = "text[]"
    )
    public List<String> directions;

    @Column(length = 200)
    public String source;

    // time in minutes
    @Column(name = "prep_time")
    public Integer prep_time;

    // time in minutes
    @Column(name = "cook_time")
    public Integer cook_time;

    @Column(name = "servings")
    public Integer servings;

    @Column(name = "serving_unit")
    public String serving_unit;

    
    @CreationTimestamp
    @Column(name = "create_data_time")
    public LocalDateTime createDateTime;
 
    @UpdateTimestamp
    @Column(name = "update_data_time")
    public LocalDateTime updateDateTime;

    //public boolean public_access;


    public Recipe() {
    }

   

     // include the filename/reference - TODO: update to List
     public Recipe(RecipeDTO recipeDTO) {
        this.title = recipeDTO.title;
        this.image_name = recipeDTO.image_name;
        this.course = recipeDTO.course;
        this.cuisine = recipeDTO.cuisine;
        this.directions = recipeDTO.directions;
        this.description = recipeDTO.description;
        this.prep_time = recipeDTO.prep_time;
        this.cook_time = recipeDTO.cook_time;
        this.servings = recipeDTO.servings;
        this.serving_unit = recipeDTO.serving_unit;
        this.ingredients = recipeDTO.ingredients;
    
        this.source = recipeDTO.source;
        this.tags = recipeDTO.tags;
    }

    

    public static List<Recipe> findByUserID(String userid){
        LOG.info("userid:" + userid);
        return list("userid", userid);
    }

    public static List<Recipe> searchTitle(String searchString){


        LOG.info("searchString: " + searchString);

        String searchInput = "%" + searchString + "%";

        // simple case-insensitive queryr
        return list("lower(title) like lower(?1)", searchInput );

       

    }

    public static List<Recipe> getLatestRecipes() {
        return list("order by date DESC");
    }
    

}
