package io.github.rowak.recipeapp.tools;

import io.github.rowak.recipeapp.models.Ingredient;

public class IngredientSort {
	public static void sortByCategory(Ingredient[] ingredients) {
		quickSort(ingredients, 0, ingredients.length-1,
				new SortMethod(SortMethod.CATEGORY_SORT));
	}
	
	public static void sortByName(Ingredient[] ingredients) {
		quickSort(ingredients, 0, ingredients.length-1,
				new SortMethod(SortMethod.NAME_SORT));
	}
	
	private static void quickSort(Ingredient[] list, int l, int r, SortMethod sortMethod) {
		if (l < r) {
			int p = partition(list, l, r, sortMethod);
			quickSort(list, l, p, sortMethod);
			quickSort(list, p+1, r, sortMethod);
		}
	}
	
	private static int partition(Ingredient[] list, int l, int r, SortMethod sortMethod) {
		Ingredient pivot = list[(l+r)/2];
		int i = l-1, j = r+1;
		while (true) {
			do {
				i++;
			} while (sortMethod.compare(list[i], pivot) < 0);
			do {
				j--;
			} while (sortMethod.compare(list[j], pivot) > 0);
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
	
	private static class SortMethod {
		public static final int CATEGORY_SORT = 1;
		public static final int NAME_SORT = 2;
		
		private int type;
		
		public SortMethod(int type) {
			this.type = type;
		}
		
		public int compare(Ingredient ingredient, Ingredient pivot) {
			switch (type) {
				case CATEGORY_SORT:
					return compareByCategory(ingredient, pivot);
				case NAME_SORT:
					return compareByName(ingredient, pivot);
				default:
					return 0;
			}
		}
		
		private int compareByCategory(Ingredient ingredient, Ingredient pivot) {
			String ingredientCategory = ingredient.getCategory() != null ?
					ingredient.getCategory() : "";
			return ingredientCategory.compareTo(pivot.getCategory() != null ? pivot.getCategory() : "");
		}
		
		private int compareByName(Ingredient ingredient, Ingredient pivot) {
			return ingredient.getName().compareTo(pivot.getName());
		}
	}
}
