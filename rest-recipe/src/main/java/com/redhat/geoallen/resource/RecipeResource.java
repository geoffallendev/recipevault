package com.redhat.geoallen.resource;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
//import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import org.jboss.resteasy.reactive.MultipartForm;

import com.redhat.geoallen.view.RecipeDTO;
import com.redhat.geoallen.view.RecipeFormData;
import com.redhat.geoallen.view.RecipeView;

import com.redhat.geoallen.view.RecipeFormToDTOMapper;
import com.redhat.geoallen.view.RecipeDTOToEntityMapper;


import com.redhat.geoallen.orm.panache.Recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import io.quarkus.panache.common.Sort;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("recipes")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class RecipeResource extends CommonResource {
    @Inject
    S3Client s3;

    @Inject
   Logger log; 

   @Inject
   RecipeFormToDTOMapper recipeFormToDTOMapper;

   @Inject
   RecipeDTOToEntityMapper recipeDTOToEntityMapper;

  

   
  



    @GET
    public List<Recipe> get() {
        return Recipe.listAll(Sort.by("title"));
        
    }

    @GET
    @Path("/userid/{userid}")
    public List<Recipe> getByUserId( String userid) {  
        return Recipe.findByUserID(userid);
    }

    @GET
    @Path("{id}")
    public Recipe getSingle( Long id) {
        Recipe entity = Recipe.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Recipe with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

     @POST
    @Transactional
    public Response create(Recipe Recipe) {
        if (Recipe.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        Recipe.persist();
        return Response.ok(Recipe).status(201).build();
    } 


    /**
     * This method accepts the FormData with image and creats an S3 Object for the image and inserts 
     * an Entity in the DB
     * 
     * @param formData
     * @return
     * @throws Exception
     */
    @POST
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRecipeWithImage(@MultipartForm RecipeFormData formData) throws Exception {


        try { 

        log.info(formData);

        Recipe newRecipe = null;
        UUID imageFileUuid = null;
        PutObjectResponse putResponse = null;

        RecipeView recipe = formData.recipe;
        //ObjectMapper objectMapper = new ObjectMapper();
       
        RecipeDTO recipeDTO = recipeFormToDTOMapper.RecipeFormToDTO(recipe);
        
        //create the DTO to use in the conditional statements
       // RecipeDTO recipeDTO = objectMapper.readValue(recipeJSON, RecipeDTO.class);
        recipeDTO.file = formData.file;
        recipeDTO.filename = formData.filename;
        recipeDTO.mimetype = formData.mimetype;
        log.info("From DTO: " + recipeDTO.title);

         // need some type of data validation as can't count on the UI
         // If there isn't an image, we can skip S3
         // otherwise, we need to create a unique object/image name per Recipe
       

                if (recipeDTO.filename != null && recipeDTO.filename.length() > 0 ) {
                    // generate unique file name 
                        imageFileUuid = UUID.randomUUID();
                        String[] mimetypeArray = recipeDTO.mimetype.split("/");
                        String subtype = mimetypeArray[1];
                        String imageName = imageFileUuid + "." + subtype;
                        
                        //String newFileName = recipeTitle.replaceAll("\\s+", ""); 
                        log.info("new uuid FileName: " + imageFileUuid);

                        recipeDTO.image_name = imageName;
                        
                        putResponse = s3.putObject(buildPutRequest(recipeDTO.image_name,recipeDTO.mimetype),
                        RequestBody.fromFile(recipeDTO.file));

                        // Check the S3 Put Response
                        if (putResponse != null) {

                            log.info(putResponse.eTag());

                            //newRecipe = new Recipe(recipeDTO);
                            newRecipe = recipeDTOToEntityMapper.RecipeDTOToRecipeEntity(recipeDTO);

                            newRecipe.persist();

                         }

                         else {
                            newRecipe = new Recipe(recipeDTO);

                            newRecipe.image_name = "default.jpg";
                            newRecipe.persist();
                         }

                         return Response.ok(newRecipe).status(Status.CREATED).build();

                   } // end if fileName != null

             else {
                    log.info(toString());
                    newRecipe = new Recipe(recipeDTO);
                    newRecipe.image_name = "default.jpg";
                    newRecipe.persist();

                     return Response.ok(newRecipe).status(Status.CREATED).build();
             }

            }

            catch (Exception e) {
                log.error(e.getMessage());
                return Response.serverError().build();

            }

    }


    @PUT
    @Transactional
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateRecipe( @RestPath Long id, @MultipartForm RecipeFormData updatedRecipe) throws Exception {

        try { 

        log.info("id: " +  id);

        RecipeView recipe = updatedRecipe.recipe;

        PutObjectResponse putResponse = null;
        
        RecipeDTO recipeDTO = recipeFormToDTOMapper.RecipeFormToDTO(recipe);

        recipeDTO.file = updatedRecipe.file;
        recipeDTO.filename = updatedRecipe.filename;
        recipeDTO.mimetype = updatedRecipe.mimetype;
        log.info("From DTO: " + recipeDTO.title);
        
       
        Recipe existingRecipe= Recipe.findById(id);

        if (existingRecipe == null) {
            throw new WebApplicationException("Recipe with id of " + id + " does not exist.", 404);
        }

        // if there is new file uploaded, then replace old one.
        if (recipeDTO.filename != null && recipeDTO.filename.length() > 0 ) {

          putResponse = handleImage(recipeDTO);


          if (putResponse != null) {

            log.info(putResponse.eTag());

           
                log.info("From DTO: " + recipeDTO.title);

                recipeDTOToEntityMapper.merge(existingRecipe,recipeDTO);

                log.info(existingRecipe);

          }
        }

          else {
            log.info(toString());
            recipeDTOToEntityMapper.merge(existingRecipe,recipeDTO);

            if (existingRecipe.image_name == null || existingRecipe.image_name.length()== 0) {

            existingRecipe.image_name = "default.jpg";
            }
            
             
                }
        

        return Response.ok(existingRecipe).status(Status.CREATED).build();

    }

        catch (Exception e) {
            log.error(e);
            return Response.serverError().build();

        }

       

    }



    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete( Long id) {
        Recipe entity = Recipe.findById(id);
        DeleteObjectResponse deleteResponse = null;
        boolean fileDeleted = false;


        if (entity == null) {
            throw new WebApplicationException("Recipe with id of " + id + " does not exist.", 404);
        }

        // if using S3
        if (entity.image_name != null)  {
        deleteResponse = s3.deleteObject(buildDeleteRequest(entity.image_name));
        log.info(deleteResponse.toString());
        }

        // if either were successfull, delete the DB entity
        if (deleteResponse != null) {
        entity.delete(); 
        }
    return Response.status(204).build();
}

  @GET
    @Path("/title/{name}")
    public List<Recipe> search(String name) {
        log.info("search info: " + name);
        String title = name;
        return Recipe.searchTitle(title);
    }  

    @GET
    @Path("/latest/")
    public List<Recipe> getLatest() {
        
        return Recipe.getLatestRecipes();
    }  
 

    private PutObjectResponse handleImage(RecipeDTO recipe) {

        PutObjectResponse putResponse = null;
        UUID imageFileUuid = null;

        if (recipe.filename != null && recipe.filename.length() > 0 ) {
            // generate unique file name 
                imageFileUuid = UUID.randomUUID();
                String[] mimetypeArray = recipe.mimetype.split("/");
                String subtype = mimetypeArray[1];
                String imageName = imageFileUuid + "." + subtype;
                
                //String newFileName = recipeTitle.replaceAll("\\s+", ""); 
                log.info("new uuid FileName: " + imageFileUuid);

                recipe.image_name = imageName;
                
                putResponse = s3.putObject(buildPutRequest(recipe.image_name,recipe.mimetype),
                RequestBody.fromFile(recipe.file));
        }


        return putResponse;
    }





    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            //log.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }

    

}
