package com.redhat.geoallen.view;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import java.io.File;

import java.io.InputStream;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)



public class RecipeView {

    @Schema(type = SchemaType.STRING)
    public String title;

    public String description;

    public String userid;

    public String author;

    public String cuisine;

    public String course;

    public String tags;

    public List<String> ingredients;

    public List<String> directions;

    public String source;

    public Integer prep_time;

    public Integer cook_time;

    public Integer servings;

    public String serving_unit;

    
    public RecipeView() {
    }

    

}
