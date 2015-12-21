package com.studio.dirigible.storage.Adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.dirigible.storage.Adapters.viewholders.ItemViewHolder;
import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Item;
import com.studio.dirigible.newstorage.R;

import java.util.List;

/**
 * Created by dirigible on 20.10.15.
 */
public class RVAdapter extends RecyclerView.Adapter<ItemViewHolder> {



    final private LayoutInflater mInflater;
    private final List<Item> items;

    private final CategoryList categories;
    View itemView;
    Context context;




    public RVAdapter(List<Item> items, CategoryList categories, Context context) {
        this.items = items;
        this.categories = categories;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(getScreenOrientation())
            itemView = mInflater.inflate(R.layout.item_land, parent, false);
        else
        {
            itemView = mInflater.inflate(R.layout.item, parent, false);
        }
        return new ItemViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final Item model = items.get(position);
        holder.bind(model, categories, getScreenOrientation());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

   /* public void setModels(List<Item> models) {
        items = new ArrayList<>(models);
    } */

    public void animateTo(List<Item> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Item> newModels) {
        for (int i = items.size() - 1; i >= 0; i--) {
            final Item model = items.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Item> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Item model = newModels.get(i);
            if (!items.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Item> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Item model = newModels.get(toPosition);
            final int fromPosition = items.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Item removeItem(int position) {
        final Item model = items.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position,Item model) {
        items.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Item model = items.remove(fromPosition);
        items.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    private boolean getScreenOrientation() { // true - альбомная, false - портретная
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false; // не удалось определить
    }
}