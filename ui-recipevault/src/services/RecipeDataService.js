import http from "../http-common";

class RecipeDataService {


  getAll() {
    return http.get("/recipes");
  }

  get(id) {
    return http.get(`/recipes/${id}`);
  }

 /** Create Recipe  */
  create(file, recipe) {

/** TODO: validate input - remove potential null pointers

    /** recipe.ingredients.split is not a function */
    recipe.ingredients = recipe.ingredients.split("\n") 

    recipe.directions = recipe.directions.split("\n") 

    const new_recipe = JSON.stringify(recipe)

    console.log("JSON recipe:" + new_recipe)

    let formData = new FormData();
  
    if (file !== undefined) {
      console.log("File:" + file.name)
      formData.append('filename',file.name);
      formData.append('mimetype',file.type)
      formData.append('file',file)
   }
      formData.append('recipe',new_recipe)
  
      return http.post(`/recipes`, formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      });
    }


 /** Update Recipe  */
  update(file, recipe) {

  recipe.ingredients = recipe.ingredients.split("\n") 

  recipe.directions = recipe.directions.split("\n") 

  const updated_recipe = JSON.stringify(recipe)


  let formData = new FormData();
  let id = recipe.id

  console.log("JSON recipe:" + updated_recipe)
 

  if (file !== undefined) {
    console.log("File:" + file.name)
    formData.append('filename',file.name);
    formData.append('mimetype',file.type)
    formData.append('file',file)
 }

   formData.append('recipe',updated_recipe)

    return http.put(`/recipes/${id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    });
  }




  delete(id) {
    return http.delete(`/recipes/${id}`);
  }

  deleteAll() {
    return http.delete(`/recipes`);
  }

  findByTitle(title) {
    return http.get(`/recipes/title/${title}`);
  }
}

export default new RecipeDataService();