package io.github.rowak.recipeapp.net;

public enum ResponseType {
	VERSION,  // return server version
	CATEGORIES,  // return recipe category names
	RECIPE_HEADERS,  // return minimal recipe data for a specific category of recipes
	RECIPE,  // return all recipe data for a specific recipe
	INVALID_REQUEST,
	INVALID_PARAMETER,
	DATABASE_ERROR,
	RESOURCE_NOT_FOUND
}
