<template>

<div class="modal-dialog modal-dialog-scrollable">
    <div class="modal-content">

      <div class="modal-header">
      <div><strong>{{store.recipe.title}}</strong></div>
      
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>

            
         
        </div>

    <div class="modal-body">

    <div class="vstack gap-4">

    <img v-bind:src="store.image_url + store.recipe.image_name" class="card-img-top" /> 

       <div>
                {{store.recipe.description}}
      </div>
    </div>

      <hr>

         <div class="vstack gap-3">

         <div class="hstack gap-3">

            <div>
               <strong> Prep Time: </strong>{{store.recipe.prep_time}}
              </div>

              <div>
              <strong>  Cook Time: </strong>{{store.recipe.cook_time}}
              </div>

              <div>
              <strong>  Servings: </strong>{{store.recipe.servings}} {{store.recipe.serving_unit}}
              </div>

        </div>

              

              <div>
              <strong> Cuisine: </strong> {{store.recipe.cuisine}}
              </div>

              <div>
              <strong>Course: </strong>{{store.recipe.course}}
              </div>

              <div>
                <strong>Ingredients: </strong>
                  <div v-for="(ingredient, index) in store.recipe.ingredients" :key="index">
                  {{ingredient}}
                  </div>
              </div>

              <div>
                <strong>Directions: </strong>
                 <div v-for="(direction, index) in store.recipe.directions" :key="index">
                  {{direction}}
                  </div>
              </div>

          </div>
          <div class="modal-footer">
        
        <button type="button" class="btn btn-primary" @click="$emit('editMode')">Edit</button>
      </div>

    </div>
      </div>

      
       </div>

      

</template>


<script>

import { store } from '../store.js';

export default {
  name: "recipe-view",
  emits: ['editMode'],
  data() {
    return {
      message: '',
      store
    };
  }
}

</script>