package nl.abnamro.recipemanager.recipes.boundary;

import nl.abnamro.recipemanager.recipes.entity.Ingredient;
import nl.abnamro.recipemanager.recipes.entity.IngredientRepository;
import nl.abnamro.recipemanager.recipes.entity.Recipe;
import nl.abnamro.recipemanager.recipes.entity.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RecipesResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void givenRecipes_whenGetRecipesWithoutFilter_thenReturnAll() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].ingredients[0]", is("ingredient2")));
    }

    @Test
    void givenRecipes_whenGetRecipesWithIncludedIngredients_thenReturnFilteredRecipes() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?includedIngredients=ingredient2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].instructions", is("instructions2")));
    }

    @Test
    void givenRecipes_whenGetRecipesWithNonExistingIncludedIngredients_thenReturnEmptyResult() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?includedIngredients=ingredient2&includedIngredients=ingredient6"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    void givenRecipes_whenGetRecipesWithExcludedIngredients_thenReturnFilteredRecipes() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?excludedIngredients=ingredient2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].instructions", is("instructions3")));
    }

    @Test
    void givenRecipes_whenGetRecipesWithNonExistingExcludedIngredients_thenReturnAll() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?excludedIngredients=ingredient6"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void givenRecipes_whenGetRecipesWithInstructions_thenReturnFilteredRecipes() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?instructions=tions3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].servings", is(0)));
    }

    @Test
    void givenRecipes_whenGetRecipesWithNonExistingInstructions_thenReturnEmptyResult() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?instructions=tions33"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    void givenRecipes_whenGetRecipesWithServings_thenReturnFilteredRecipes() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?servings=3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].instructions", is("instructions1")));
    }

    @Test
    void givenRecipes_whenGetRecipesWithNonExistingServings_thenReturnEmptyResult() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?servings=4"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    void givenRecipes_whenGetVegetarianRecipes_thenReturnFilteredRecipes() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?isVegetarian=true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].instructions", is("instructions1")));
    }

    @Test
    void givenRecipes_whenGetNonVegetarianRecipes_thenReturnFilteredRecipes() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes?isVegetarian=false"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].instructions", is("instructions3")));
    }

    @Test
    void givenRecipes_whenAddRecipe_thenReturnAddedRecipe() throws Exception {
        createTestRecipes();

        mockMvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "servings": 7,
                          "instructions": "instructions4",
                          "ingredients": [
                            "ingredient5",
                            "ingredient6"
                          ],
                          "vegetarian": true
                        }"""))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.instructions", is("instructions4")))
                .andExpect(jsonPath("$.ingredients[1]", is("ingredient6")));

        var recipes = recipeRepository.findAll();
        assertTrue(StreamSupport.stream(recipes.spliterator(), false).anyMatch(recipe -> "instructions4".equals(recipe.getInstructions())));
        assertEquals(4, StreamSupport.stream(recipes.spliterator(), false).count());
    }

    @Test
    void givenRecipes_whenUpdateRecipe_thenReturnUpdatedRecipe() throws Exception {
        var recipes = createTestRecipes();
        var recipeId = recipes.get(0).getId();

        mockMvc.perform(put("/api/recipes/" + recipeId).contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "servings": 7,
                          "instructions": "instructions4",
                          "ingredients": [
                            "ingredient5",
                            "ingredient6"
                          ],
                          "vegetarian": true
                        }"""))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.instructions", is("instructions4")))
                .andExpect(jsonPath("$.ingredients[1]", is("ingredient6")));

        var newRecipes = recipeRepository.findAll();
        assertTrue(StreamSupport.stream(newRecipes.spliterator(), false).anyMatch(recipe -> "instructions4".equals(recipe.getInstructions())));
        assertEquals(3, StreamSupport.stream(newRecipes.spliterator(), false).count());
    }

    @Test
    void givenRecipes_whenUpdateNonExistingRecipe_thenReturnNotFound() throws Exception {
        createTestRecipes();

        mockMvc.perform(put("/api/recipes/123456").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "servings": 7,
                          "instructions": "instructions4",
                          "ingredients": [
                            "ingredient5",
                            "ingredient6"
                          ],
                          "vegetarian": true
                        }"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenRecipes_whenDeleteRecipe_thenReturnOk() throws Exception {
        var recipes = createTestRecipes();
        var recipeId = recipes.get(0).getId();

        mockMvc.perform(delete("/api/recipes/" + recipeId))
                .andExpect(status().isOk());

        var newRecipes = recipeRepository.findAll();
        assertTrue(StreamSupport.stream(newRecipes.spliterator(), false).noneMatch(recipe -> "instructions1".equals(recipe.getInstructions())));
        assertEquals(2, StreamSupport.stream(newRecipes.spliterator(), false).count());
    }

    @Test
    void givenRecipes_whenDeleteNonExistingRecipe_thenReturnNotFound() throws Exception {
        createTestRecipes();

        mockMvc.perform(delete("/api/recipes/123456"))
                .andExpect(status().isNotFound());

        var recipes = recipeRepository.findAll();
        assertEquals(3, StreamSupport.stream(recipes.spliterator(), false).count());
    }

    @Test
    void givenRecipes_whenGetRecipe_thenReturnRecipe() throws Exception {
        var recipes = createTestRecipes();
        var recipeId = recipes.get(2).getId();

        mockMvc.perform(get("/api/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.instructions", is("instructions3")));
    }

    @Test
    void givenRecipes_whenGetNonExistingRecipe_thenReturnNotFound() throws Exception {
        createTestRecipes();

        mockMvc.perform(get("/api/recipes/123456"))
                .andExpect(status().isNotFound());
    }

    private List<Recipe> createTestRecipes() {
        var recipes = new ArrayList<Recipe>();

        var ingredient1 = createTestIngredient("ingredient1");
        var ingredient2 = createTestIngredient("ingredient2");
        var ingredient3 = createTestIngredient("ingredient3");
        var ingredient4 = createTestIngredient("ingredient4");
        var ingredient5 = createTestIngredient("ingredient5");

        var recipe = new Recipe();
        recipe.setVegetarian(true);
        recipe.setInstructions("instructions1");
        recipe.setServings(3);
        recipe.setIngredients(Arrays.asList(
                ingredient1,
                ingredient2
        ));
        recipes.add(recipeRepository.save(recipe));

        var recipe2 = new Recipe();
        recipe2.setVegetarian(false);
        recipe2.setInstructions("instructions2");
        recipe2.setServings(5);
        recipe2.setIngredients(Arrays.asList(
                ingredient2,
                ingredient3
        ));
        recipes.add(recipeRepository.save(recipe2));

        var recipe3 = new Recipe();
        recipe3.setVegetarian(false);
        recipe3.setInstructions("instructions3");
        recipe3.setServings(0);
        recipe3.setIngredients(Arrays.asList(
                ingredient4,
                ingredient5
        ));
        recipes.add(recipeRepository.save(recipe3));

        return recipes;
    }

    private Ingredient createTestIngredient(String name) {
        var ingredient = new Ingredient(name);
        return ingredientRepository.save(ingredient);
    }
}
