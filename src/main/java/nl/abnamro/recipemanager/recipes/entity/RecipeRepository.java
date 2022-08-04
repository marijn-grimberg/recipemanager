package nl.abnamro.recipemanager.recipes.entity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    @Query("from Recipe as recipe " +
            "where (:isVegetarian is null or recipe.isVegetarian = :isVegetarian)" +
            "and (:servings is null or recipe.servings = :servings)" +
            "and (recipe.instructions like %:instructions%)")
    List<Recipe> findByIsVegestarianAndServingsAndInstructions(@Param("isVegetarian") Boolean isVegetarian,
        @Param("servings") Integer servings, @Param("instructions") String instructions);
}
