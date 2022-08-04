package nl.abnamro.recipemanager.recipes.control;

import nl.abnamro.recipemanager.config.ApplicationConfig;
import nl.abnamro.recipemanager.recipes.boundary.RecipeRequest;
import nl.abnamro.recipemanager.recipes.entity.Ingredient;
import nl.abnamro.recipemanager.recipes.entity.IngredientRepository;
import nl.abnamro.recipemanager.recipes.entity.Recipe;
import nl.abnamro.recipemanager.recipes.entity.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipesServiceTest {
    @Spy
    private ModelMapper modelMapper = new ApplicationConfig().modelMapper();

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipesService recipesService;

    @Test
    void getRecipes_withIncludedIngredients_returnsCorrectRecipes() {
        // Arrange
        when(recipeRepository.findByIsVegestarianAndServingsAndInstructions(null, null, null))
                .thenReturn(createRecipes());

        var includedIngredients = List.of(
                "ingredient1"
        );
        var excludedIngredients = Collections.<String>emptyList();
        var searchCriteria = new SearchCriteria(null, null, null, includedIngredients, excludedIngredients);

        // Act
        var recipes = recipesService.getRecipes(searchCriteria);

        // Assert
        assertEquals(1, recipes.size());
        assertEquals("instructions1", recipes.get(0).getInstructions());
    }

    @Test
    void getRecipes_withExcludedIngredients_returnsCorrectRecipes() {
        // Arrange
        when(recipeRepository.findByIsVegestarianAndServingsAndInstructions(null, null, null))
                .thenReturn(createRecipes());

        var includedIngredients = Collections.<String>emptyList();
        var excludedIngredients = List.of(
                "ingredient1"
        );
        var searchCriteria = new SearchCriteria(null, null, null, includedIngredients, excludedIngredients);

        // Act
        var recipes = recipesService.getRecipes(searchCriteria);

        // Assert
        assertEquals(2, recipes.size());
        assertEquals("instructions2", recipes.get(0).getInstructions());
        assertEquals("instructions3", recipes.get(1).getInstructions());
    }

    @Test
    void addRecipe_existingRecipe_returnsAddedRecipe() {
        // Arrange
        when(recipeRepository.save(any())).thenReturn(createRecipeWithId());
        when(ingredientRepository.findByName("ingredient1")).thenReturn(Optional.of(new Ingredient(1L, "ingredient1")));
        when(ingredientRepository.save(new Ingredient("ingredient2"))).thenReturn(new Ingredient(1L, "ingredient2"));

        // Act
        var addedRecipe = recipesService.addRecipe(createRecipeRequest());

        // Assert
        ArgumentCaptor<Recipe> argument = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(argument.capture());
        assertEquals("instructions", argument.getValue().getInstructions());
        assertEquals("ingredient1", argument.getValue().getIngredients().get(0).getName());

        assertEquals("instructions", addedRecipe.getInstructions());
        assertEquals("ingredient1", addedRecipe.getIngredients().get(0));
        assertNotNull(addedRecipe.getId());
    }

    @Test
    void updateRecipe_nonExistingRecipe_returnsNull() {
        // Act
        var updatedRecipe = recipesService.updateRecipe(1, createRecipeRequest());

        // Assert
        assertNull(updatedRecipe);
    }

    @Test
    void updateRecipe_existingAndNonExistingIngredient_returnsUpdatedRecipe() {
        // Arrange
        var oldRecipe = createRecipeWithId();
        var newRecipe = createRecipeWithId();
        newRecipe.setInstructions("instructions2");
        newRecipe.setIngredients(List.of(
                new Ingredient("ingredient2"),
                new Ingredient("ingredient3")
        ));

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(oldRecipe));
        when(recipeRepository.save(any())).thenReturn(newRecipe);
        when(ingredientRepository.findByName("ingredient1")).thenReturn(Optional.of(new Ingredient(1L, "ingredient1")));
        when(ingredientRepository.save(new Ingredient("ingredient2"))).thenReturn(new Ingredient(1L, "ingredient2"));

        // Act
        var updatedRecipe = recipesService.updateRecipe(1, createRecipeRequest());

        // Assert
        ArgumentCaptor<Recipe> argument = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(argument.capture());
        assertEquals("instructions", argument.getValue().getInstructions());
        assertEquals("ingredient1", argument.getValue().getIngredients().get(0).getName());
        assertEquals("ingredient2", argument.getValue().getIngredients().get(1).getName());

        assertEquals("instructions2", updatedRecipe.getInstructions());
        assertEquals("ingredient2", updatedRecipe.getIngredients().get(0));
        assertEquals("ingredient3", updatedRecipe.getIngredients().get(1));
        assertNotNull(updatedRecipe.getId());
    }

    @Test
    void deleteRecipe_nonExistingRecipe_returnsFalse() {
        // Act
        var deleted = recipesService.deleteRecipe(1);

        // Assert
        assertFalse(deleted);
    }

    @Test
    void deleteRecipe_existingRecipe_returnsTrue() {
        // Arrange
        var recipe = createRecipeWithId();
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        // Act
        var deleted = recipesService.deleteRecipe(1);

        // Assert
        verify(recipeRepository).delete(recipe);
        assertTrue(deleted);
    }

    @Test
    void getRecipe_nonExistingRecipe_returnsNull() {
        // Act
        var recipe = recipesService.getRecipe(1);

        // Assert
        assertNull(recipe);
    }

    @Test
    void getRecipe_existingRecipe_returnsRecipe() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(createRecipeWithId()));

        // Act
        var recipe = recipesService.getRecipe(1);

        // Assert
        assertEquals("instructions", recipe.getInstructions());
        assertEquals("ingredient1", recipe.getIngredients().get(0));
    }

    private RecipeRequest createRecipeRequest() {
        var recipe = new RecipeRequest();
        recipe.setVegetarian(true);
        recipe.setServings(0);
        recipe.setInstructions("instructions");
        recipe.setIngredients(Arrays.asList(
                "ingredient1",
                "ingredient2"
        ));
        return recipe;
    }

    private Recipe createRecipe() {
        var recipe = new Recipe();
        recipe.setVegetarian(true);
        recipe.setServings(0);
        recipe.setInstructions("instructions");
        recipe.setIngredients(Arrays.asList(
                new Ingredient("ingredient1"),
                new Ingredient("ingredient2")
        ));
        return recipe;
    }

    private Recipe createRecipeWithId() {
        var recipe = createRecipe();
        recipe.setId(1L);
        return recipe;
    }

    private List<Recipe> createRecipes() {
        var recipes = new ArrayList<Recipe>();

        var recipe = new Recipe();
        recipe.setVegetarian(true);
        recipe.setInstructions("instructions1");
        recipe.setServings(3);
        recipe.setIngredients(Arrays.asList(
                new Ingredient("ingredient1"),
                new Ingredient("ingredient2")
        ));
        recipes.add(recipe);

        var recipe2 = new Recipe();
        recipe2.setVegetarian(false);
        recipe2.setInstructions("instructions2");
        recipe2.setServings(5);
        recipe2.setIngredients(Arrays.asList(
                new Ingredient("ingredient2"),
                new Ingredient("ingredient3")
        ));
        recipes.add(recipe2);

        var recipe3 = new Recipe();
        recipe3.setVegetarian(false);
        recipe3.setInstructions("instructions3");
        recipe3.setServings(0);
        recipe3.setIngredients(Arrays.asList(
                new Ingredient("ingredient4"),
                new Ingredient("ingredient5")
        ));
        recipes.add(recipe3);

        return recipes;
    }
}
