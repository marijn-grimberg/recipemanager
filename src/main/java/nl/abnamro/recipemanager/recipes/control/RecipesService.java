package nl.abnamro.recipemanager.recipes.control;

import nl.abnamro.recipemanager.recipes.boundary.RecipeRequest;
import nl.abnamro.recipemanager.recipes.boundary.RecipeResponse;
import nl.abnamro.recipemanager.recipes.entity.Ingredient;
import nl.abnamro.recipemanager.recipes.entity.IngredientRepository;
import nl.abnamro.recipemanager.recipes.entity.Recipe;
import nl.abnamro.recipemanager.recipes.entity.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipesService {
    private final ModelMapper modelMapper;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public RecipesService(ModelMapper modelMapper, RecipeRepository recipeRepository,
                          IngredientRepository ingredientRepository) {
        this.modelMapper = modelMapper;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<RecipeResponse> getRecipes(SearchCriteria searchCriteria) {
        var recipes = recipeRepository.findByIsVegestarianAndServingsAndInstructions(
                searchCriteria.getIsVegetarian(), searchCriteria.getServings(), searchCriteria.getInstructions());
        return recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponse.class))
                .filter(searchCriteria::filterOnIngredients)
                .collect(Collectors.toList());
    }

    public RecipeResponse addRecipe(RecipeRequest recipeRequest) {
        var recipe = modelMapper.map(recipeRequest, Recipe.class);
        recipe.setIngredients(recipeRequest.getIngredients().stream().map(this::fetchIngredient).collect(Collectors.toList()));
        return modelMapper.map(recipeRepository.save(recipe), RecipeResponse.class);
    }

    public RecipeResponse updateRecipe(long id, RecipeRequest recipeRequest) {
        if (recipeRepository.findById(id).isEmpty()) {
            return null;
        }
        var recipe = modelMapper.map(recipeRequest, Recipe.class);
        recipe.setId(id);
        recipe.setIngredients(recipeRequest.getIngredients().stream().map(this::fetchIngredient).collect(Collectors.toList()));
        return modelMapper.map(recipeRepository.save(recipe), RecipeResponse.class);
    }

    public boolean deleteRecipe(long id) {
        var recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            return false;
        }
        recipeRepository.delete(recipe.get());
        return true;
    }

    public RecipeResponse getRecipe(long id) {
        var recipe = recipeRepository.findById(id);
        return recipe.map(value -> modelMapper.map(value, RecipeResponse.class)).orElse(null);
    }

    private Ingredient fetchIngredient(String ingredientName) {
        var ingredient = ingredientRepository.findByName(ingredientName);
        return ingredient.orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
    }
}
