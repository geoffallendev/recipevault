package com.redhat.geoallen.view;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import java.io.File;

import java.io.InputStream;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeDTO {

    

    private static final Logger LOG = Logger.getLogger(RecipeDTO.class);
      


    public String title;

    public String description;

    public String userid;

    public String author;

    public String image_name; 

    public String filename; 

    public File  file; 

    public String mimetype; 

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

    
    //public boolean public_access;


    public RecipeDTO() {
    }

    

}
