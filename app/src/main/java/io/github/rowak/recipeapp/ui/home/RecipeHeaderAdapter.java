package io.github.rowak.recipeapp.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rowak.recipeapp.R;
import io.github.rowak.recipeapp.models.Category;
import io.github.rowak.recipeapp.models.RecipeHeader;

public class RecipeHeaderAdapter extends RecyclerView.Adapter<RecipeHeaderAdapter.ViewHolder> {
	private Category parentCategory;
	private List<RecipeHeader> mData;
	private Map<Integer, Bitmap> recipeImages;
	private SharedPreferences prefs;
	private LayoutInflater mInflater;
	private ItemClickListener mClickListener;
	
	public RecipeHeaderAdapter(Context context, List<RecipeHeader> data,
							   Category parentCategory) {
		this.mInflater = LayoutInflater.from(context);
		this.mData = data;
		this.parentCategory = parentCategory;
		recipeImages = new HashMap<Integer, Bitmap>();
		prefs = PreferenceManager.getDefaultSharedPreferences(
				context.getApplicationContext());
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.recipe_list_row, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final RecipeHeader recipeHeader = mData.get(position);
		holder.txtViewRecipeName.setText(recipeHeader.getName());
		holder.txtViewRecipeDescription.setText(recipeHeader.getDescription());
		holder.rtingBarRecipe.setRating(getRecipeRating(recipeHeader));
		holder.imgViewRecipeImg.setImageResource(R.drawable.ic_help_center_88dp);
		Bitmap recipeImage = recipeImages.get(recipeHeader.getId());
		if (recipeImage != null) {
			holder.imgViewRecipeImg.setImageBitmap(recipeImage);
		}
	}
	
	private float getRecipeRating(RecipeHeader recipeHeader) {
		return prefs.getFloat("recipeRating" + recipeHeader.getId(), 0);
	}
	
	@Override
	public int getItemCount() {
		return mData.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView txtViewRecipeName;
		TextView txtViewRecipeDescription;
		RatingBar rtingBarRecipe;
		ImageView imgViewRecipeImg;
		
		ViewHolder(View itemView) {
			super(itemView);
			txtViewRecipeName = itemView.findViewById(R.id.txtViewRecipeName);
			txtViewRecipeDescription = itemView.findViewById(R.id.txtViewRecipeDescription);
			rtingBarRecipe = itemView.findViewById(R.id.rtingBarRecipe);
			imgViewRecipeImg = itemView.findViewById(R.id.imgViewRecipeImg);
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View view) {
			if (mClickListener != null) {
				mClickListener.onItemClick(view, getAdapterPosition());
			}
		}
	}
	
	public RecipeHeader getItem(int id) {
		return mData.get(id);
	}
	
	public void addItem(RecipeHeader item) {
		mData.add(item);
	}
	
	public void removeItem(RecipeHeader item) {
		mData.remove(item);
	}
	
	public void setData(List<RecipeHeader> data,
						Map<Integer, Bitmap> recipeImages) {
		mData = data;
		this.recipeImages = recipeImages;
	}
	
	public List<RecipeHeader> getData() {
		return mData;
	}
	
	public void setClickListener(ItemClickListener itemClickListener) {
		this.mClickListener = itemClickListener;
	}
	
	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}
}
