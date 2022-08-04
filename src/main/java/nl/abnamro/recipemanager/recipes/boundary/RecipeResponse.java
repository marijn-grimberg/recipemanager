package nl.abnamro.recipemanager.recipes.boundary;

import lombok.Data;

import java.util.List;

@Data
public class RecipeResponse {
    private Long id;
    private boolean isVegetarian;
    private int servings;
    private String instructions;
    private List<String> ingredients;
}
