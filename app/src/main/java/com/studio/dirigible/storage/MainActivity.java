package com.studio.dirigible.storage;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.studio.dirigible.storage.Adapters.ItemCursorAdapter;
import com.studio.dirigible.storage.Adapters.RVAdapter;
import com.studio.dirigible.storage.Containers.ItemsContainer;
import com.studio.dirigible.storage.Helpers.DBHelper;
import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Item;
import com.studio.dirigible.newstorage.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,NavigationView.OnNavigationItemSelectedListener,
        OutDialog.OnCompleteListener
{

    final String LOG_TAG = "StorageLogs";

    DBHelper dbh;
    SQLiteDatabase db;
    RecyclerView rv;
    RVAdapter rvaAdapter;
    ItemCursorAdapter itemCursorAdapter;

    List<Item> Items;
    CategoryList Categories;
    Item selectedItem;
    ItemsContainer ic;
    Bundle b;

    String jsonCategories;
    String jsonItem;
    String jsonItems;

    int index = 0;

    private SearchView mSearchView;

    String action;
    Intent intent;

    List<Item> mModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                AddItem();


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbh = new DBHelper(this);

        db = dbh.getWritableDatabase();

        itemCursorAdapter = new ItemCursorAdapter(db);


        //если наш контайнер пуст, то мы считываем данные из БД, и попутно заполняем его(контейнер)
        if (getLastCustomNonConfigurationInstance() != null) {

            Log.d(LOG_TAG, "Заполнение из контейнера");
            Items = ((ItemsContainer) getLastCustomNonConfigurationInstance()).items;
            Categories = ((ItemsContainer) getLastCustomNonConfigurationInstance()).categories;
            PutItemsInContainer();
        } else {

            Log.d(LOG_TAG, "Заполнение из базы");
            Items = itemCursorAdapter.ReadAllItems();
            Categories = itemCursorAdapter.ReadAllCategories();
            PutItemsInContainer();
        }


        rvaAdapter = new RVAdapter(Items, Categories,this);
        rv = (RecyclerView) findViewById(R.id.rvItems);


        StaggeredGridLayoutManager glm = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(glm);

        rv.setAdapter(rvaAdapter);


        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        selectedItem = Items.get(position);


                        index = position;

                        //передача через серилизацию
                        Log.d(LOG_TAG, "Передача через сериализацию");
                        b = new Bundle();
                        Gson gson = new Gson();
                        jsonItem = gson.toJson(selectedItem);
                        jsonCategories = gson.toJson(Categories);
                        jsonItems = gson.toJson(Items);
                        b.putString("itm", jsonItem);
                        b.putString("cat", jsonCategories);
                        b.putString("items", jsonItems);
                        b.putInt("position", position);
                        showDialog();

                    }
                })
        );


        // получаем Intent, который вызывал это Activity
        intent = getIntent();
        // читаем из него action
        action = intent.getAction();

        if (action.equals("dirigible.storage.intent.action.additem")) {
            Log.d(LOG_TAG, "action.equals - ADD-ITEM");
            intent.setAction(""); // bydlocoding
          AddItem();
        }
        else if(action.equals("dirigible.storage.intent.action.outitem")) {
            Log.d(LOG_TAG, "action.equals - OUT-ITEM");
            intent.setAction("");
            showOutDialog();
        }

    }

    public ItemsContainer onRetainCustomNonConfigurationInstance() {
        return ic;
    }

    void PutItemsInContainer() // сравнить тестом, что быстрее - этот вариант для сохранение позиции или другой
    {
        ic = new ItemsContainer();
        ic.items = Items;
        ic.categories = Categories;
        ic.selectedItem = selectedItem;
    }


    public void showDialog() {

        Log.d(LOG_TAG, "Вызов диалога");
        ItemDialog newFragment = new ItemDialog();
        if (b != null) { //параметры не передадутся, если мы добавляем новый элемент
            newFragment.setArguments(b);
        }
        newFragment.show(getSupportFragmentManager(), "ItemDialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "MainActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MainActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "MainActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MainActivity: onDestroy()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String jsonCategories = data.getStringExtra("categories");
        if (jsonCategories != null) {
            Categories.clear();  //временное решение до лучших времён
            Categories.addAll(new Gson().fromJson(jsonCategories, CategoryList.class));

        }

        String jsonItem = data.getStringExtra("item");
        selectedItem = new Gson().fromJson(jsonItem, Item.class);
        if (Items.size() <= index)
            Items.add(selectedItem);
        else {
            // if()
            Items.set(index, selectedItem);
        }


        rvaAdapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "MainActivity: OnActivityResult()");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Search Open");

                mModels = new ArrayList<>();

                for (Item item : Items) {
                    mModels.add(item);
                }
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //your code here
                mModels.clear();
                Log.d(LOG_TAG, "Search Close");
                return false;
            }
        });

        if(action.equals("dirigible.storage.intent.action.search")) {
            Log.d(LOG_TAG, "action.equals - OUT-ITEM");
            intent.setAction("");
            mSearchView.setIconified(false);
        }

        return true;
    }




        @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic


            final List<Item> filteredModelList = filter(mModels, query);

        rvaAdapter.animateTo(filteredModelList);
        rv.scrollToPosition(0);
        return true;
    }



    private List<Item> filter(List<Item> models, String query) {
        query = query.toLowerCase();

        final List<Item> filteredModelList = new ArrayList<Item>();


        for (Item model : models) {
            final String text = model.Name.toLowerCase(); //
            if (text.contains(query)) {
                    filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.sort_name:
            {
                Toast.makeText(this,"Сортировка по имени",Toast.LENGTH_SHORT).show();
                NameComparator comp = new NameComparator();
                Collections.sort(Items,comp);
                rvaAdapter.notifyDataSetChanged();
            }
            break;

            case R.id.sort_category:
            {
                Toast.makeText(this,"Сортировка по категории",Toast.LENGTH_SHORT).show();
                CategoryComparator comp = new CategoryComparator();
                Collections.sort(Items,comp);
                rvaAdapter.notifyDataSetChanged();
            }
            break;

            case R.id.sort_date:
            {
                Toast.makeText(this,"Сортировка по дате",Toast.LENGTH_SHORT).show();
                DateComparator comp = new DateComparator();
                Collections.sort(Items,comp);
                rvaAdapter.notifyDataSetChanged();
            }
            break;

        }
       /* if (id == R.id.sort_name) {

            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.nav_add: {
                AddItem();
            }
            break;

            case R.id.nav_remove: {
            showOutDialog();
            }
            break;

            case R.id.nav_journal: {
                db.close();
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
                finish();
            }
            break;

            case R.id.nav_exit: {
            finish();
            }
            break;

            case R.id.nav_search:
            {
                mSearchView.setIconified(false);
            }
            break;


            default:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void AddItem() {
        index = Items.size();      // last item +1

        Gson gson = new Gson();
        jsonCategories = gson.toJson(Categories);

        Intent intent = new Intent(MainActivity.this, ItemActivity.class);
        intent.putExtra("category", jsonCategories);
        startActivityForResult(intent, 1);
    }


    /* Dialog out */
    // с перегрузочкой

    //частичное списание
    public void onComplete(int position, Item item) {
        // After the dialog fragment completes, it calls this callback.
        // use the string here
        Items.set(position, item);
        rvaAdapter.notifyDataSetChanged();

    }

    // полное списание(удаление)
    public void onComplete(int position) {
        Items.remove(position);
        rvaAdapter.notifyDataSetChanged();
    }

    public void showOutDialog() {

        OutDialog newFragment = new OutDialog();

        Gson gson = new Gson();
        jsonCategories = gson.toJson(Categories);
        jsonItems = gson.toJson(Items);
        b = new Bundle();
        b.putString("cat", jsonCategories);
        b.putString("items", jsonItems);
        b.putInt("position", 0);

        if (b != null) { //параметры не передадутся, если мы добавляем новый элемент
            newFragment.setArguments(b);
        }
        newFragment.show(this.getSupportFragmentManager(), "OutDialog");
    }

    public class NameComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.Name.toLowerCase().compareTo(o2.Name.toLowerCase());

        }
    }

    public class CategoryComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return Double.compare(o1.CategoryId,o2.CategoryId);
        }
    }

    public class DateComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.Date.compareTo(o2.Date);
        }
    }


}