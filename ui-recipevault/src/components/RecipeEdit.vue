<template>

<div class="modal-dialog modal-dialog-scrollable">
    
    <div class="modal-content">

      <div class="modal-header">
      
    <form>
      <div>
        <label for="title">Title</label>
        <input type="text" class="form-control" id="title" v-model=store.recipe.title />
      </div>
    </form>
             
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
         
      </div>

    <div v class="modal-body">

    <div class="vstack gap-3">

    <img v-if=url :src="url" />
    <img v-else v-bind:src="store.image_url + store.recipe.image_name" class="card-img-top" />

    <div class="mb-3">
  <label for="formFile" class="form-label">Recipe Image</label>
  <input type="file" ref="file" class="form-control" @change="selectFile" />
</div>

    </div>
    

       <div>
        <label for="description">Description</label>
        <input type="text" class="form-control" id="description" v-model=store.recipe.description />
      </div>

    

      <hr>

         <div class="vstack gap-3">

         <div class="hstack gap-2">

            <div>
             <strong>Prep Time:</strong>
               <input type="text" class="form-control" id="prep_time" v-model=store.recipe.prep_time />
            </div>

              <div>
              <strong>  Cook Time: </strong>
              <input type="text" class="form-control" id="cook_time" v-model=store.recipe.cook_time />
              </div>

              <div>
              <strong>  Servings: </strong>
              <input type="text" class="form-control" id="servings" v-model=store.recipe.servings />
              </div>

              <div>
              <strong>  Servings Unit: </strong>
              <input type="text" class="form-control" id="serving_unit" v-model=store.recipe.serving_unit />
              </div>

        </div>

              

              <div>
              <strong> Cuisine: </strong>
              <input type="text" class="form-control" id="cuisine" v-model=store.recipe.cuisine />
              </div>

              <div>
              <strong>Course: </strong>
              <input type="text" class="form-control" id="course" v-model=store.recipe.course />
              </div>

              <div>
                <strong>Ingredients: </strong>
                  

                  <textarea
          class="form-control"
          id="ingredients"
          required
          v-model="store.recipe.ingredients"
          name="ingredients"
        />
                 
              </div>

              <div>
                <strong>Directions: </strong>
                 <textarea
          class="form-control"
          id="directions"
          required
          v-model="store.recipe.directions"
          name="directions"
        />
              </div>

          </div>
          <div class="modal-footer">
        
        <button type="button" class="btn btn-primary" @click="$emit('deleteRecipe')">Delete</button>
        
        <button type="button" class="btn btn-primary" @click="$emit('updateRecipe')">Save</button>
        
        <button type="button" class="btn btn-primary" @click="$emit('viewMode')">View Recipe</button>
      </div>

    </div>
    </div>
    </div>
    

</template>

<script>

import { store } from '../store.js';

export default {
  name: "recipe-edit",
  modal: null,

  data() {
    return {
      message: '',
      store,
      url: null,
      selectedFiles: undefined,
      currentFile: undefined,
      progress: 0,
      fileInfos: [],
    };
  },
  methods: {
    

selectFile() {
      this.selectedFiles = this.$refs.file.files;
      this.currentFile = this.selectedFiles.item(0)
      this.url = URL.createObjectURL(this.currentFile);
      store.currentFile = this.currentFile
    },

formatList() {


}


  },
  mounted() {
    this.message = '';
    store.recipe.directions = store.recipe.directions.join('\n');
    store.recipe.ingredients = store.recipe.ingredients.join('\n');
   
  
  }
};
</script>

<style>
.edit-form {
  max-width: 300px;
  margin: auto;
}
</style>