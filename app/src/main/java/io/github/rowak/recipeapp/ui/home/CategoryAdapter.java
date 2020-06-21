package io.github.rowak.recipeapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.rowak.recipeapp.R;
import io.github.rowak.recipeapp.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
	private Stack<Category> parentCategories;
	private List<Category> mData, mDataOrig;
	private LayoutInflater mInflater;
	private ItemClickListener mClickListener;
	
	public CategoryAdapter(Context context, ArrayList<Category> data) {
		this.mInflater = LayoutInflater.from(context);
		this.mData = data;
		mDataOrig = (List<Category>)data.clone();
		parentCategories = new Stack<Category>();
		parentCategories.push(null);
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.category_list_row, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Category category = mData.get(position);
		holder.txtViewCategoryName.setText(category.getName());
		String numRecipesText = category.getNumRecipes() + " recipe";
		if (category.getNumRecipes() != 1) {
			numRecipesText += "s";
		}
		holder.txtViewNumCategoryRecipes.setText(numRecipesText);
	}
	
	private boolean categoryMatchesParent(Category category, Category parent) {
		return ((parent == null || (parent != null && parent.getName() == null)) &&
				category.getParent() == null) ||
				(parent != null && parent.getName() != null &&
						parent.getName().equals(category.getParent()));
	}
	
	public Category getCurrentParent() {
		return parentCategories.peek();
	}
	
	public int getNumChildren(Category parent) {
		int num = 0;
		for (Category category : mDataOrig) {
			if (category != null && categoryMatchesParent(category, parent)) {
				num++;
			}
		}
		return num;
	}
	
	public void moveToPreviousParent() {
		if (parentCategories.peek() != null) {
			parentCategories.pop();
			updateChildren();
			notifyDataSetChanged();
		}
	}
	
	private void updateChildren() {
		mData.clear();
		for (Category category : mDataOrig) {
			if (categoryMatchesParent(category,
					parentCategories.peek())) {
				mData.add(category);
			}
		}
	}
	
	@Override
	public int getItemCount() {
		return mData.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView txtViewCategoryName;
		TextView txtViewNumCategoryRecipes;
		
		ViewHolder(View itemView) {
			super(itemView);
			txtViewCategoryName = itemView.findViewById(R.id.txtViewCategoryName);
			txtViewNumCategoryRecipes = itemView.findViewById(R.id.txtViewNumCategoryRecipes);
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View view) {
			if (mClickListener != null) {
				mClickListener.onItemClick(view, getAdapterPosition());
				int pos = getAdapterPosition();
				if (pos >= 0 /*&& getNumChildren(mData.get(pos)) > 0*/) {
					parentCategories.push(mData.get(pos));
					updateChildren();
					notifyDataSetChanged();
				}
			}
		}
	}
	
	public Category getItem(int id) {
		return mData.get(id);
	}
	
	public void addItem(Category item) {
		mData.add(item);
	}
	
	public void removeItem(Category item) {
		mData.remove(item);
	}
	
	public void setData(ArrayList<Category> data) {
		mData = data;
		mDataOrig = (List<Category>)data.clone();
		updateChildren();
	}
	
	public List<Category> getData() {
		return mData;
	}
	
	public void setClickListener(ItemClickListener itemClickListener) {
		this.mClickListener = itemClickListener;
	}
	
	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}
}
