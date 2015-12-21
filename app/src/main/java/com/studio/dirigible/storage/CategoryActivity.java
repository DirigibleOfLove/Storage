package com.studio.dirigible.storage;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.dirigible.storage.Adapters.ItemCursorAdapter;
import com.studio.dirigible.storage.Helpers.DBHelper;
import com.studio.dirigible.storage.Models.Category;
import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.newstorage.R;

import java.util.ArrayList;
import java.util.List;


public class CategoryActivity extends AppCompatActivity {

    Button btnAdd;
    Button btnEdit;
    Button btnDel;
    Button btnExit;
    Spinner sr;
    boolean isEdit;
    RelativeLayout container;
    EditText etChange;
    EditText etAdd;

    DBHelper dbh;
    SQLiteDatabase db;
    ItemCursorAdapter itemCursorAdapter;
    CategoryList categories;

    int selectedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //получаем список категорий "оттуда"

        btnAdd = (Button) findViewById(R.id.AddButton);
        btnEdit = (Button) findViewById(R.id.EditButton);
        btnDel = (Button) findViewById(R.id.buttonDel);
        btnExit = (Button) findViewById(R.id.buttonExit);
        sr = (Spinner) findViewById(R.id.spinner);

        dbh = new DBHelper(this);

        db = dbh.getWritableDatabase();

        itemCursorAdapter = new ItemCursorAdapter(db);

        Intent intent = getIntent();

        String jsonCategories = intent.getStringExtra("categories");

        categories = new Gson().fromJson(jsonCategories, new TypeToken<CategoryList>() {
        }.getType());

        final List<String> dataCategory = new ArrayList<>();
        if(categories!=null) {  // категорий нету
            for (Category cat : categories)
                dataCategory.add(cat.Name);
        }

        final ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataCategory);

        sr.setAdapter(SpinnerAdapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // ввести проверку на существующий элемент
                etAdd = (EditText) findViewById(R.id.addEt);
                if (etAdd.getText().toString().isEmpty()) {
                    Toast.makeText(CategoryActivity.this, "Вы ничего не ввели!", Toast.LENGTH_SHORT).show();
                } else {
                    // itemCursorAdapter.InsertCategory(etAdd.getText().toString());
                    dataCategory.add(etAdd.getText().toString());

                    // pizdos-code
                    categories.add(new Category((int)itemCursorAdapter.InsertCategory(etAdd.getText().toString()),etAdd.getText().toString()));

                    etAdd.setText(null);
                    SpinnerAdapter.notifyDataSetChanged();

                    Toast.makeText(CategoryActivity.this, "Категория добавлена:" + etAdd.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit != true) // но лучше проверять инициализирован какой-либо элемент, или нет
                {
                    selectedItemPosition=sr.getSelectedItemPosition();

                    sr.setVisibility(View.GONE);
                    container = (RelativeLayout) findViewById(R.id.Container);
                    etChange = new EditText(CategoryActivity.this);
                    etChange.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    etChange.setText(categories.get(selectedItemPosition).Name);

                    container.addView(etChange);

                    btnEdit.setText("Сохранить");
                    isEdit = true;
                } else
                {

                   // categories.get(selectedItemPosition).Name=etChange.getText().toString();

                    dataCategory.set(selectedItemPosition, etChange.getText().toString());
                    categories.get(selectedItemPosition).Name=etChange.getText().toString();
                    itemCursorAdapter.UpdateCategory(etChange.getText().toString(),categories.get(selectedItemPosition).Id);

                    etChange.setVisibility(View.GONE);
                    sr = new Spinner(CategoryActivity.this);
                    sr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    //  sr.setAdapter();
                    btnEdit.setText("Изменить");

                    SpinnerAdapter.notifyDataSetChanged();
                    sr.setAdapter(SpinnerAdapter);


                    Toast.makeText(CategoryActivity.this, "Категория изменена", Toast.LENGTH_SHORT).show();
                    container.addView(sr);
                    isEdit = false;
                }


            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CategoryActivity.this, "Категория удалена:"+dataCategory.get(sr.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
                dataCategory.remove(sr.getSelectedItemPosition());
                itemCursorAdapter.RemoveCategory(categories.get(sr.getSelectedItemPosition()).Id);
                categories.remove(sr.getSelectedItemPosition());
                SpinnerAdapter.notifyDataSetChanged();

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // btnEXit
                {


                    // добавить обработчик для кнопки back

                    Gson gson=new Gson();

                    Intent intent = new Intent();
                    intent.putExtra("categories", gson.toJson(categories));

                    setResult(RESULT_OK, intent);
                    finish();


                    }

                }
        });



    }
}
