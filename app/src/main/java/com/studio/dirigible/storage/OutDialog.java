package com.studio.dirigible.storage;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.studio.dirigible.storage.Adapters.FileReportAdapter;
import com.studio.dirigible.storage.Adapters.ItemCursorAdapter;
import com.studio.dirigible.storage.Helpers.DBHelper;
import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Item;
import com.studio.dirigible.storage.Models.ItemList;
import com.studio.dirigible.newstorage.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirigible on 01.12.15.
 */
public class OutDialog extends DialogFragment
{
    public static interface OnCompleteListener {
        public abstract void onComplete(int position);
        public abstract void onComplete(int position,Item item);
    }

    private OnCompleteListener mListener;

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }


    ItemList items;
    CategoryList categories;
    String jsonItems;
    String jsonCategories;
    int position;


    DBHelper dbh;
    SQLiteDatabase db;
    ItemCursorAdapter itemCursorAdapter;

    Spinner spinner;
    EditText etQuantity;
    Button buttonOutall;

    ArrayAdapter<String> ItemsAdapter;
    List<String> dataItems;

  //  MainActivity.OutDialogListener activity;

    /* Preferences */

    /* Preferences */


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

   //     activity = (MainActivity.OutDialogListener) getActivity();



        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View v =inflater.inflate(R.layout.out_dialog, null);

        etQuantity = (EditText)v.findViewById(R.id.outDialogQuantity);
        buttonOutall = (Button)v.findViewById(R.id.outDialogButton);
        spinner = (Spinner)v.findViewById(R.id.outDialogSpinner);

        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        itemCursorAdapter = new ItemCursorAdapter(db);



        Bundle b = getArguments();
        if(b!=null) {

            // передать CategoryList. в случае передачи с диалога, нужно передать айдишник выделенного элемента,3+36

            jsonItems = b.getString("items");
            position = b.getInt("position");
            jsonCategories=b.getString("cat");

            items = new Gson().fromJson(jsonItems, ItemList.class);
            categories = new Gson().fromJson(jsonCategories,CategoryList.class);


            dataItems = new ArrayList<>();

            for (Item itm : items)
                dataItems.add("ID:"+String.valueOf(itm.ID)+" Name:"+itm.Name);

            ItemsAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dataItems);
            spinner.setAdapter(ItemsAdapter);



           spinner.setSelection(position);



            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    OutDialog.this.position = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }

        builder.setView(v)
                // Add action buttons

                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        OutDialog.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("Списать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // перейти к редактированию фрагмента


                        int etQuantityNumber=Integer.valueOf(etQuantity.getText().toString());

                        int itemQuantityNumber=items.get(position).Quantity;

                        if(etQuantityNumber>itemQuantityNumber)
                            Toast.makeText(getActivity(),"Число превышает количество!",Toast.LENGTH_SHORT).show();
                        if(etQuantityNumber==itemQuantityNumber) // если списываются все, то айтем удаляется
                        {
                            itemCursorAdapter.RemoveItem(items.get(position).ID); // удаляем айтем к праотцам
                            // передаём в b его позицию, и трём в главном активити

                            mListener.onComplete(position);

                            FileReportAdapter reportAdapter = new FileReportAdapter(getActivity());

                            reportAdapter.writeOut(items.get(position),
                                    categories.getById(items.get(position).CategoryId).Name, etQuantityNumber);



                        }
                        else {


                            FileReportAdapter reportAdapter = new FileReportAdapter(getActivity());

                            reportAdapter.writeOut(items.get(position),
                                    categories.getById(items.get(position).CategoryId).Name, etQuantityNumber);

                            items.get(position).Quantity=itemQuantityNumber-etQuantityNumber;

                            itemCursorAdapter.ChangeItemQuantity(items.get(position).ID,
                                    itemQuantityNumber - etQuantityNumber);


                            mListener.onComplete(position, items.get(position));





                        }// отнимаем



                    }
                });


        buttonOutall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etQuantity.setText(String.valueOf(items.get(position).Quantity));
            }
        });

        return builder.create();
    }



}

