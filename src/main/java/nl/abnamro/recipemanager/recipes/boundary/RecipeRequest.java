package nl.abnamro.recipemanager.recipes.boundary;

import lombok.Data;

import java.util.List;

@Data
public class RecipeRequest {
    private boolean isVegetarian;
    private int servings;
    private String instructions;
    private List<String> ingredients;
}
