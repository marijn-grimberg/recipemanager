package nl.abnamro.recipemanager.recipes.control;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.abnamro.recipemanager.recipes.boundary.RecipeResponse;

import java.util.List;

@AllArgsConstructor
@Data
public class SearchCriteria {
    private Boolean isVegetarian;
    private Integer servings;
    private String instructions;
    private List<String> includedIngredients;
    private List<String> excludedIngredients;

    public boolean filterOnIngredients(RecipeResponse recipe) {
        if (includedIngredients != null && !recipe.getIngredients().containsAll(includedIngredients)) {
            return false;
        }
        return recipe.getIngredients().stream().noneMatch(ingredient -> excludedIngredients.contains(ingredient));
    }
}
