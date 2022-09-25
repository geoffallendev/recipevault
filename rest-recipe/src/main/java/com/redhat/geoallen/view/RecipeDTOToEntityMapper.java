package com.redhat.geoallen.view;


import org.mapstruct.*;
import com.redhat.geoallen.orm.panache.Recipe;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RecipeDTOToEntityMapper {
 

    Recipe RecipeDTOToRecipeEntity(RecipeDTO recipe); 


    void merge(@MappingTarget Recipe target, RecipeDTO source);
}