package com.studio.dirigible.storage.Adapters.viewholders;

import android.support.v7.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Item;
import com.studio.dirigible.newstorage.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {


    private final TextView ID;
    private final TextView Category;
    private final TextView Name;
    private final TextView Quantity;
    private final TextView Date;
    ImageView iv;

   /*     TextView Weight;

        TextView Size;
        TextView Storage;
*/

    public ItemViewHolder(View itemView) {
        super(itemView);
        ID = (TextView) itemView.findViewById(R.id.tvID);
        Category = (TextView) itemView.findViewById(R.id.tvCategory);
        Name = (TextView) itemView.findViewById(R.id.tvName);
        iv = (ImageView) itemView.findViewById(R.id.photo);
        Quantity = (TextView) itemView.findViewById(R.id.tvCount);
        Date = (TextView) itemView.findViewById(R.id.tvDate);

      /*      Weight = (TextView) itemView.findViewById(R.id.tvWeight);

            Size = (TextView) itemView.findViewById(R.id.tvSize);
            Storage = (TextView) itemView.findViewById(R.id.tvStorage); */



    }

    public void bind(Item model,CategoryList categories,boolean orientation) {


        String name = model.Name;
        int stringlimit;
        if (orientation)
            stringlimit = 38;
        else {
            stringlimit = 18; // если ориентация экрана альбомная, то передать
        }
        if (name.length() > stringlimit)
            name = name.substring(0, stringlimit) + "..";
        ID.setText(String.valueOf(model.ID));
     Category.setText((categories.getById(model.CategoryId).Name));
        Name.setText(name);
        Quantity.setText(String.valueOf(model.Quantity));
        Date.setText(model.Date);

        if (model.ImagePath == null) {
            Bitmap bmp = BitmapFactory.decodeFile(model.ImagePath);
            iv.setImageBitmap(bmp);
            iv.setImageResource(R.drawable.camera_icon);
        } else {


            iv.setImageBitmap(null);
            Bitmap bmp = BitmapFactory.decodeFile(model.ImagePath);
            iv.setImageBitmap(bmp);
        }
    }
}