<template>

<div class="container-fluid">
  <div>

<div class="card-group">


<div v-for="(recipe, index) in this.store.recipes" :key="index"  @click="setActiveRecipe(recipe, index)">
  <div class="col">
    <div class="card">
      
      <img v-bind:src="imageURL(recipe)" class="card-img-top" width="240" height="160"/> 

        <div class="card-body">
            <h5 class="card-title">{{recipe.title}}</h5> 
            
            
        </div>
    </div>
  </div>
</div>
</div>


</div>

</div>

<!-- Recipe Modal -->
<div class="modal fade" id="recipeModal" tabindex="-1" aria-labelledby="recipe" aria-hidden="true">
  
        <RecipeView v-if="component === 'RecipeView'" @editMode="setEditRecipe" />
        <RecipeEdit v-if="component === 'RecipeEdit'" @viewMode="setViewMode" @updateRecipe="updateRecipe" @deleteRecipe="deleteRecipe"/>
    

  </div>

  

</template>



<script>
import RecipeDataService from "../services/RecipeDataService";

import RecipeEdit from "./RecipeEdit.vue";
import RecipeView from "./RecipeView.vue";

import { Modal } from 'bootstrap';

import { store } from '../store.js';

export default {
  name: "recipe-list",
  modal: null,
  
  components: { RecipeEdit,RecipeView },
  data() {
    
    return {
      component: "RecipeView",
      recipes: [],
      currentRecipe: null,
      currentIndex: -1,
      title: "",
      image_url: "",
      store
    };
  },


  methods: {
    retrieveRecipes() {
      RecipeDataService.getAll()
        .then(response => {
          this.store.recipes = response.data;
          this.recipes = this.store.recipes
          this.component = "RecipeView"
          console.log(response.data);
        })
        .catch(e => {
          console.log(e);
        });
    },

    updateRecipe() {
      RecipeDataService.update(store.currentFile,store.recipe)
        .then(response => {
          console.log(response.data);
          this.message = 'The recipe was updated successfully!';
          this.modal.hide();
          this.component = "RecipeView"
          this.retrieveRecipes();
        })
        .catch(e => {
          console.log(e);
        });
    },

     deleteRecipe() {
      RecipeDataService.delete(store.recipe.id)
        .then(response => {
          console.log(response.data);
          this.modal.hide();
          this.retrieveRecipes();
          
          
        })
        .catch(e => {
          console.log(e);
        });
    },

    refreshList() {
      this.retrieveRecipes();
      this.currentRecipe = null;
      this.currentIndex = -1;
    },

    setActiveRecipe(recipe, index) {
      this.currentRecipe = recipe;
      this.currentIndex = recipe ? index : -1;
      this.store.recipe = this.currentRecipe;
      this.currentFile = undefined
      this.store.currentFile = undefined;
      this.component = "RecipeView"
      this.modal.show();
    
    },

    setEditRecipe() {
      console.log("setEditRecipe")
      this.component = "RecipeEdit"
      
      

    },

    setViewMode () {
    console.log("setViewMode")
    this.component = "RecipeView"
    },

    displayImage(image_name) {
      console.log(image_name)
      // `this` points to the component instance
      if (image_name != "default.jpg")
        return true
      else
      return false
      
    },

     imageURL(recipe) {
      if (recipe.image_name != "" && recipe.image_name !== null) {
      return  this.image_url + recipe.image_name
      }
      else  {
      return "./images/no_image.jpg"
      }
    },

  
    removeAllRecipes() {
      RecipeDataService.deleteAll()
        .then(response => {
          console.log(response.data);
          this.refreshList();
        })
        .catch(e => {
          console.log(e);
        });
    },
    
    searchTitle() {
      RecipeDataService.findByTitle(this.title)
        .then(response => {
          this.recipes = response.data;
          console.log(response.data);
        })
        .catch(e => {
          console.log(e);
        });
    }
  },
  mounted() {
    this.retrieveRecipes();
    this.image_url = window.VUE_APP_IMAGE_SERVER_URL
    this.modal = new Modal(document.getElementById('recipeModal'))
    
  }
};


</script>
