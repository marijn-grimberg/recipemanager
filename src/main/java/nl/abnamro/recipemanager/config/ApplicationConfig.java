package nl.abnamro.recipemanager.config;

import nl.abnamro.recipemanager.recipes.boundary.RecipeResponse;
import nl.abnamro.recipemanager.recipes.entity.Ingredient;
import nl.abnamro.recipemanager.recipes.entity.Recipe;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        var recipeTypeMap = modelMapper.createTypeMap(Recipe.class, RecipeResponse.class);
        recipeTypeMap.setPostConverter(recipeConverter);

        return modelMapper;
    }

    Converter<Recipe, RecipeResponse> recipeConverter = context -> {
        var recipe = context.getDestination();
        recipe.setIngredients(context.getSource().getIngredients().stream().map(Ingredient::getName).toList());
        return recipe;
    };
}
