package com.redhat.geoallen.view;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;


@Schema(type = SchemaType.STRING, format = "binary")
public class RecipeFormData {


// We instruct OpenAPI to use the schema provided by the 'UploadFormSchema'
// class implementation and thus define a valid OpenAPI schema for the Swagger
// UI


    @Schema(implementation = RecipeFormData.class)
    @RestForm("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public File file;

    @RestForm("filename")
    @PartType(MediaType.TEXT_PLAIN)
    public String filename;

    @RestForm("mimetype")
    @PartType(MediaType.TEXT_PLAIN)
    public String mimetype;


    @RestForm("recipe")
    @PartType(MediaType.APPLICATION_JSON)
    public RecipeView recipe;



}
