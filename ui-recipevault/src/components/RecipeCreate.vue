<template>
  <div class="modal-dialog modal-dialog-scrollable">
    
    <div class="modal-content">

      <div class="modal-header">
      
    <form>
      <div>
        <label for="title">Title</label>
        <input type="text" class="form-control" id="title" v-model=recipe.title />
      </div>
    </form>
             
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
         
      </div>

    <div v class="modal-body">

    <div class="vstack gap-3">
        
        <div class="mb-3">
            <label for="formFile" class="form-label">Recipe Image</label>
            <input
              type="file"
              ref="file"
              class="form-control"
              @change="selectFile"
            required>
            <div class="invalid-feedback">Example invalid form file feedback</div>
          </div>

          <div class="mb-3">
            <div id="preview">
              <img v-if="url" :src="url" />
            </div>
          </div>

          <div class="mb-3">
            <div class="form-group">
              <label for="description">Description</label>
              <textarea
                class="form-control"
                id="info"
                required
                v-model="recipe.description"
                name="info"
              />
            </div>
          </div>

<div class="vstack gap-3">

         <div class="hstack gap-2">

            <div>
             <strong>Prep Time:</strong>
               <input type="text" class="form-control" id="prep_time" v-model=recipe.prep_time />
            </div>

              <div>
              <strong>  Cook Time: </strong>
              <input type="text" class="form-control" id="cook_time" v-model=recipe.cook_time />
              </div>

              <div>
              <strong>  Servings: </strong>
              <input type="text" class="form-control" id="servings" v-model=recipe.servings />
              </div>

              <div>
              <strong>  Servings Unit: </strong>
              <input type="text" class="form-control" id="serving_unit" v-model=recipe.serving_unit />
              </div>

        </div>


              <div>
              <strong> Cuisine: </strong>
              <input type="text" class="form-control" id="cuisine" v-model=recipe.cuisine />
              </div>

              <div>
              <strong>Course: </strong>
              <input type="text" class="form-control" id="course" v-model=recipe.course />
              </div>


          <div class="mb-3">
            <div class="form-group">
              <label for="ingredients">Ingredients - 1 per line</label>
              <textarea
                class="form-control"
                id="info"
                required
                v-model="recipe.ingredients"
                name="ingredients"
                rows="7"
              />
            </div>
          </div>

          <div class="mb-3">
            <div class="form-group">
              <label for="directions">Directions</label>
              <textarea
                class="form-control"
                id="directions"
                required
                v-model="recipe.directions"
                name="directions"
                rows="7"
              />
            </div>
          </div>

</div>

          <!--  End modal Body-->
        </div>
  </div>
        <div class="modal-footer">
           <button class="btn btn-success" @click="testUI">Test Recipe</button>
          <button class="btn btn-success" @click="saveRecipe">Add</button>
        </div>
      </div>
    </div>
    
 


</template>

<script>
import RecipeDataService from "../services/RecipeDataService";

import { Modal } from 'bootstrap';

import { store } from '../store.js';

export default {
  name: "add-recipe",
  modal: null,

  data() {
    return {
      recipe: {
        title: "",
        description: "",
        cuisine: "",
        course: "",
        tags: "",
        ingredients: "",
        directions: "",
        source: "",
        prep_time: "",
        cook_time: "",
        servings: "",
        serving_unit: "",
        ingredient_list: "",
        direction_list: ""
      },
      selectedFiles: undefined,
      currentFile: undefined,
      progress: 0,
      message: "",
      fileInfos: [],
      submitted: false,
      published: false,
      url: null,
      store 
    };
  },
  methods: {
    saveRecipe() {
      RecipeDataService.create(this.currentFile, this.recipe)
        .then((response) => {
          this.recipe.id = response.data.id;
          console.log(response.data);
          this.submitted = true;
          this.recipe = {}
          this.selectedFiles = undefined
          this.currentFile = undefined
          this.url = null
          this.fileInfos = []
          this.$refs.file.value = ""
          this.retrieveRecipes()
          this.modal.hide()
           })
        .catch((e) => {
          console.log(e);
        });
    },

     retrieveRecipes() {

       console.log("retrieve recipes")
      RecipeDataService.getAll()
        .then(response => {
          store.recipes = response.data;
          
          console.log(response.data);
        })
        .catch(e => {
          console.log(e);
        });
    },

    selectFile() {
      this.selectedFiles = this.$refs.file.files;
      this.currentFile = this.selectedFiles.item(0);
      this.url = URL.createObjectURL(this.currentFile);
    },

    newRecipe() {
      this.submitted = false;
      this.recipe = {};
    },

testUI() {
      this.submitted = false;
      this.recipe = {
        title: "Blueberry Kuechen",
        description: "Test Description",
        cuisine: "American",
        course: "Breakfast",
        tags: "",
        ingredients: "first line\nsecond line",
        directions: "first line\nsecond line",
        source: "Friend",
        prep_time: "30",
        cook_time: "45",
        servings: "12",
        serving_unit: "pieces"
    }
}

  },

  mounted() {
    this.recipe = {};
    
    this.modal = new Modal(document.getElementById('createRecipe'))
    
  
  }
};
</script>

<style>
.submit-form {
  max-width: 300px;
  margin: auto;
}
</style>