package io.github.rowak.recipeapp.net;

public enum RequestType {
	VERSION,			// required: *none*
	CATEGORIES,			// required: *none*
	RECIPE_HEADERS,		// required: categoryName
	RECIPE				// required: recipeName
}