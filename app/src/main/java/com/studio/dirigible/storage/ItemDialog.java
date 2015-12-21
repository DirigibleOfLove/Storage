package com.studio.dirigible.storage;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Item;
import com.studio.dirigible.newstorage.R;


public class ItemDialog extends DialogFragment


{

    Item selectedItem;
    CategoryList categories;


    String jsonItem;
    String jsonCategories;


    Bundle b;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View v =inflater.inflate(R.layout.item_dialog, null);
        TextView tvID =(TextView)v.findViewById(R.id.dialogTvID);
        TextView tvName =(TextView)v.findViewById(R.id.dialogTvName);
        TextView tvDescription = (TextView)v.findViewById(R.id.dialogTvDescription);
        TextView tvCat =(TextView)v.findViewById(R.id.dialogTvCategory);
        TextView tvWeight =(TextView)v.findViewById(R.id.dialogTvWeight);
        TextView tvQuantity =(TextView)v.findViewById(R.id.dialogTvQuantity);
        TextView tvDate = (TextView)v.findViewById(R.id.dialogTvDate);



        b = getArguments();
        if(b!=null) {
            jsonItem = b.getString("itm");
            jsonCategories = b.getString("cat");
            selectedItem = new Gson().fromJson(jsonItem, Item.class);
            categories = new Gson().fromJson(jsonCategories, new TypeToken<CategoryList>() {
            }.getType());

            tvID.setText(String.valueOf(selectedItem.ID));
            tvName.setText(selectedItem.Name);
            tvDescription.setText(selectedItem.Description);
            tvCat.setText(categories.getById(selectedItem.CategoryId).Name);
            tvWeight.setText(String.valueOf(selectedItem.Weight)+" грамм");
            tvQuantity.setText(String.valueOf(selectedItem.Quantity)+" штук");
            tvDate.setText(selectedItem.Date);

        }



        builder.setView(v)
                // Add action buttons

                .setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItemDialog.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("Редактировать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // перейти к редактированию фрагмента
                        Intent intent = new Intent(getActivity(), ItemActivity.class);
                        intent.putExtra("item", jsonItem);
                        intent.putExtra("category", jsonCategories);
                        startActivityForResult(intent, 1);
                        //    startActivity(intent);
                    }
                })
                .setNeutralButton("Списать", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    showDialog();
                    }
                });



        return builder.create();
    }

    public void showDialog() {

        OutDialog newFragment = new OutDialog();
        if (b != null) { //параметры не передадутся, если мы добавляем новый элемент
            newFragment.setArguments(b);
        }
        newFragment.show(getActivity().getSupportFragmentManager(), "OutDialog");
    }

}
