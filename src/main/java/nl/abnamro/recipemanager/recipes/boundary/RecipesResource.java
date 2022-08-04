package nl.abnamro.recipemanager.recipes.boundary;

import nl.abnamro.recipemanager.recipes.control.RecipesService;
import nl.abnamro.recipemanager.recipes.control.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/recipes")
@RestController
public class RecipesResource {
    private final RecipesService recipesService;

    @Autowired
    public RecipesResource(RecipesService recipesService) {
        this.recipesService = recipesService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getRecipes(
            @RequestParam(required = false) Boolean isVegetarian,
            @RequestParam(required = false) Integer servings,
            @RequestParam(required = false, defaultValue = "") String instructions,
            @RequestParam(required = false, defaultValue = "") List<String> includedIngredients,
            @RequestParam(required = false, defaultValue = "") List<String> excludedIngredients) {
        var searchCriteria = new SearchCriteria(isVegetarian, servings, instructions, includedIngredients, excludedIngredients);
        var recipes = recipesService.getRecipes(searchCriteria);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> addRecipe(@RequestBody RecipeRequest recipeRequest) {
        var recipe = recipesService.addRecipe(recipeRequest);
        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable int id, @RequestBody RecipeRequest recipeRequest) {
        var recipe = recipesService.updateRecipe(id, recipeRequest);
        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id) {
        var deleted = recipesService.deleteRecipe(id);
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable int id) {
        var recipe = recipesService.getRecipe(id);
        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }
}
