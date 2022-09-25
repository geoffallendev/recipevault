package com.redhat.geoallen.view;


import org.mapstruct.*;
import com.redhat.geoallen.view.RecipeView;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RecipeFormToDTOMapper {
 

    RecipeDTO RecipeFormToDTO(RecipeView recipe); 


    void merge(@MappingTarget RecipeDTO target, RecipeView source);
}