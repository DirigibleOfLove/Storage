package com.studio.dirigible.storage;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.dirigible.storage.Adapters.FileReportAdapter;
import com.studio.dirigible.storage.Adapters.ItemCursorAdapter;
import com.studio.dirigible.storage.Helpers.DBHelper;
import com.studio.dirigible.storage.Models.Category;
import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Item;
import com.studio.dirigible.newstorage.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemActivity extends AppCompatActivity {

    DBHelper dbh;
    SQLiteDatabase db;
    ItemCursorAdapter itemCursorAdapter;

    EditText etName;
    EditText etDescription;
    EditText etWeight;
    EditText etQuantity;
    EditText etDate;
    Spinner spinnerCategory;
    Button SaveorAdd;
    ImageView iv;

    Item selectedItem;
    CategoryList categories;

    String jsonCategories;

    ArrayAdapter<String> CategoryAdapter;
    List<String> dataCategory;

    /* OpenFileDialog */

    Button buttonOpenDialog;
    Button buttonUp;
    TextView textFolder;

    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;

    File root;
    File curFolder;

    private List<String> fileList = new ArrayList<String>();
    File selected;

    final String LOG_TAG = "StorageLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);




        dbh = new DBHelper(this);

        db = dbh.getWritableDatabase();

        itemCursorAdapter = new ItemCursorAdapter(db);

        etName = (EditText) findViewById(R.id.etName);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etDate = (EditText) findViewById(R.id.etDate);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        SaveorAdd = (Button) findViewById(R.id.button);

        iv = (ImageView) findViewById(R.id.imageView);

        etDate.setText(getDateTime());


        Intent intent = getIntent();

        jsonCategories = intent.getStringExtra("category");

        categories = new Gson().fromJson(jsonCategories, new TypeToken<CategoryList>() {
        }.getType());


        dataCategory = new ArrayList<>();

        FillDataCategory();

        CategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataCategory);
        spinnerCategory.setAdapter(CategoryAdapter);



        if (intent.getStringExtra("item") != null) {
            String jsonItem = intent.getStringExtra("item");
            selectedItem = new Gson().fromJson(jsonItem, Item.class);

            etName.setText(selectedItem.Name);
            etDescription.setText(selectedItem.Description);
            etWeight.setText(String.valueOf(selectedItem.Weight));
            etQuantity.setText(String.valueOf(selectedItem.Quantity));
            etDate.setText(selectedItem.Date);
            spinnerCategory.setSelection(categories.getIndex(selectedItem.CategoryId));

            Bitmap bmp = BitmapFactory.decodeFile(selectedItem.ImagePath);
            iv.setImageBitmap(bmp);


            SaveorAdd.setText("Сохранить");


            SaveorAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FileReportAdapter reportAdapter = new FileReportAdapter(ItemActivity.this,selectedItem);

                    FillItem();

                    reportAdapter.writeEdit(selectedItem, categories.getById(selectedItem.CategoryId).Name);

                    itemCursorAdapter.UpdateItem(selectedItem);


                    // после сохранение, добавим элемент в коллекцию главного активити, что бы не дёргать зря базу

                    Gson gson = new Gson();
                    Intent intent = new Intent(ItemActivity.this, MainActivity.class);

                    intent.putExtra("item", gson.toJson(selectedItem));
                    intent.putExtra("categories", gson.toJson(categories));

                    setResult(RESULT_OK, intent);

                    Toast.makeText(ItemActivity.this, "Запись изменена", Toast.LENGTH_LONG).show();
                    finish();

                }
            });
        } else //
        {
            selectedItem = new Item();

            SaveorAdd.setText("Добавить");
            SaveorAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FillItem();

                    selectedItem.ID = (int) itemCursorAdapter.InsertItem(selectedItem);


                    // после сохранение, добавим элемент в коллекцию главного активити, что бы не дёргать зря базу

                    Gson gson = new Gson();
                    Intent intent = new Intent();

                    intent.putExtra("item", gson.toJson(selectedItem));
                    intent.putExtra("categories", gson.toJson(categories));

                    setResult(RESULT_OK, intent);


                    FileReportAdapter reportAdapter = new FileReportAdapter(ItemActivity.this);

                    reportAdapter.writeInsert(selectedItem,categories.getById(selectedItem.CategoryId).Name);

                    Toast.makeText(ItemActivity.this, "Запись добавлена", Toast.LENGTH_LONG).show();

                    finish();

                }
            });


        }


        Button buttonCategory = (Button) findViewById(R.id.buttonCategory);
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ItemActivity.this, CategoryActivity.class);
                intent.putExtra("categories", jsonCategories);
                startActivityForResult(intent, 1);
            }
        });


        /* FileDialog */

        buttonOpenDialog = (Button) findViewById(R.id.buttonfiledialog);
        buttonOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(CUSTOM_DIALOG_ID);
            }
        });

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        curFolder = root;


    }

    @Override
    protected void onStart() {
        super.onStart();


        // Items.
        Log.d(LOG_TAG, "ItemActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "ItemActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "ItemActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "ItemActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "ItemActivity: onDestroy()");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        jsonCategories = data.getStringExtra("categories");
        if (jsonCategories != null) {
            CategoryList dataCat = new Gson().fromJson(jsonCategories, CategoryList.class);
            categories = dataCat;

            FillDataCategory();
            CategoryAdapter.notifyDataSetChanged();
        }


    }


    void FillDataCategory() {
        // Category
        dataCategory.clear(); // на случай, если всех их удалили к чёртовой мамке
        if (categories != null) {  // категорий нету
            for (Category cat : categories)
                dataCategory.add(cat.Name);
        } else {
            Toast.makeText(this, "Отсутствуют категории. Добавьте!", Toast.LENGTH_LONG).show();
        }


    }

    void FillItem() {

        selectedItem.Name = etName.getText().toString();
        selectedItem.Description = etDescription.getText().toString();
        selectedItem.Weight = Integer.valueOf(etWeight.getText().toString());
        selectedItem.Quantity = Integer.valueOf(etQuantity.getText().toString());
        selectedItem.CategoryId = categories.get((spinnerCategory.getSelectedItemPosition())).Id;
        selectedItem.Date = etDate.getText().toString();

        if (selected != null) {
            //    File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kaic1/imagem.jpg");
            // нужно создать папочку, если нету

            File folder = new File(root + "/StoragesImage");
            if (folder.exists() != true)
                new File(folder.getPath()).mkdir();

            File from = selected;
            File to = new File(root + "/StoragesImage/" + java.util.UUID.randomUUID().toString() + ".jpg"); //
            from.renameTo(to);
            selectedItem.ImagePath = to.toString();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;

        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(ItemActivity.this);
                dialog.setContentView(R.layout.filedialoglayout);
                dialog.setTitle("Выберите изображение");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                textFolder = (TextView) dialog.findViewById(R.id.folder);
                buttonUp = (Button) dialog.findViewById(R.id.up);
                buttonUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListDir(curFolder.getParentFile());
                    }
                });

                dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
                dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected = new File(fileList.get(position));
                        if (selected.isDirectory()) { //
                            ListDir(selected);
                        } else {
                            Toast.makeText(ItemActivity.this, selected.toString() + " selected",
                                    Toast.LENGTH_LONG).show();
                            iv = (ImageView) findViewById(R.id.imageView);
                            Bitmap bmp = BitmapFactory.decodeFile(selected.toString());
                            iv.setImageBitmap(bmp);

                            dismissDialog(CUSTOM_DIALOG_ID);
                        }
                    }
                });

                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG_ID:
                ListDir(curFolder);
                break;
        }
    }

    void ListDir(File f) {
        if (f.equals(root)) {
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();

        for (File file : files) {
            fileList.add(file.getPath());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList);
        dialog_ListView.setAdapter(directoryList);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


}