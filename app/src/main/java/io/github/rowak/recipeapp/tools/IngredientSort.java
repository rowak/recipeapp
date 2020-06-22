package io.github.rowak.recipeapp.tools;

import io.github.rowak.recipeapp.models.Ingredient;

public class IngredientSort {
	public static void sortByCategory(Ingredient[] ingredients) {
		quickSort(ingredients, 0, ingredients.length-1);
	}
	
	private static void quickSort(Ingredient[] list, int l, int r) {
		if (l < r) {
			int p = partition(list, l, r);
			quickSort(list, l, p);
			quickSort(list, p+1, r);
		}
	}
	
	private static int partition(Ingredient[] list, int l, int r) {
		Ingredient pivot = list[(l+r)/2];
		int i = l-1, j = r+1;
		while (true) {
			do {
				i++;
//			} while (list[i].getCategory() != null && pivot.getCategory() != null &&
//					list[i].getCategory().compareTo(pivot.getCategory()) < 0);
			} while (compareToPivot(list[i], pivot) < 0);
			do {
				j--;
			} while (compareToPivot(list[j], pivot) > 0);
			if (i >= j) {
				return j;
			}
			swap(list, i, j);
		}
	}
	
	private static void swap(Object[] list, int i, int j) {
		Object temp = list[i];
		list[i] = list[j];
		list[j] = temp;
	}
	
	private static int compareToPivot(Ingredient ingredient, Ingredient pivot) {
		String ingredientCategory = ingredient.getCategory() != null ?
				ingredient.getCategory() : "other";
		return ingredientCategory.compareTo(pivot.getCategory() != null ? pivot.getCategory() : "other");
	}
}
