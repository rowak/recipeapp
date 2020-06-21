package io.github.rowak.recipeapp.tools;

import java.util.List;

import io.github.rowak.recipeapp.models.Category;

public class CategorySort {
	public static void quickSort(List<Category> list) {
		quickSort(list, 0, list.size()-1);
	}
	
	private static void quickSort(List<Category> list, int l, int r) {
		if (l < r) {
			int p = partition(list, l, r);
			quickSort(list, l, p);
			quickSort(list, p+1, r);
		}
	}
	
	private static int partition(List<Category> list, int l, int r) {
		Category pivot = list.get((l+r)/2);
		int i = l, j = r;
		while (true) {
			while (list.get(i).getName()
					.compareTo(pivot.getName()) < 0) {
				i++;
			}
			while (list.get(j).getName()
					.compareTo(pivot.getName()) > 0) {
				j--;
			}
			if (i >= j) {
				return i;
			}
			swap(list, i, j);
		}
	}
	
	private static void swap(List<Category> list, int i, int j) {
		Category temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}
}
