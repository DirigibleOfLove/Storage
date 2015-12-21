package com.studio.dirigible.storage.Adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.studio.dirigible.storage.Models.CategoryList;
import com.studio.dirigible.storage.Models.Category;
import com.studio.dirigible.storage.Models.Item;

import java.util.ArrayList;
import java.util.List;


public class ItemCursorAdapter
{



    public ItemCursorAdapter(SQLiteDatabase db)
    {
        this.db = db;

    }

    SQLiteDatabase db;

    final String LOG_TAG = "StorageLogs";
    final String TableItems = "Items";
    final String TableCategories = "Categories";



    public long InsertItem(Item item) {
        Log.d(LOG_TAG, "Insert in table " + TableItems + " item: = " + item.Name + " "+ item.Description
                + " " + item.Weight + " " + item.Quantity + " " + item.CategoryId + " " + item.ImagePath +" "+ item.Date);
        ContentValues cv = new ContentValues();
        cv.put("Name", item.Name);
        cv.put("Description",item.Description);
        cv.put("Weight", item.Weight);
        cv.put("Quantity", item.Quantity);
        cv.put("CategoryID", item.CategoryId);
        cv.put("ImagePath", item.ImagePath);
        cv.put("Date",item.Date);

        return db.insert(TableItems, null, cv); // возвращает ID

    }


    public void UpdateItem(Item item) {
        Log.d(LOG_TAG, "Update In table " + TableItems + " item: = " + item.Name + " " + item.Description
                + " " + item.Weight + " " + item.Quantity + " " + item.CategoryId + " " + item.ImagePath + " " + item.Date);
        ContentValues cv = new ContentValues();
        cv.put("Name", item.Name);
        cv.put("Description",item.Description);
        cv.put("Weight", item.Weight);
        cv.put("Quantity", item.Quantity);
        cv.put("CategoryID", item.CategoryId);
        cv.put("ImagePath", item.ImagePath);
        cv.put("Date", item.Date);
        db.update(TableItems, cv, "id = ?",
                new String[]{String.valueOf(item.ID)}); // пиздос как логично
        Log.d(LOG_TAG, "Элемент:" + item.ID + " обновлён");
    }



    // передавать модель, а не кучу параметров
   /* public void UpdateItem(int id,String name, int weight, int quantity,
                           int Xsize, int Ysize, int Zsize, int categoryId, int storageId) {
        Log.d(LOG_TAG, "UpdateInTable " + TableItems + " item: = " + name
                + " " + weight + " " + quantity + " " + Xsize + "x" + Ysize + "x" + Zsize
                + " " + categoryId + " " + storageId);
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        cv.put("Weight", weight);
        cv.put("Quantity", quantity);
        cv.put("Xsize", Xsize);
        cv.put("Ysize", Ysize);
        cv.put("Zsize", Zsize);
        cv.put("CategoryID", categoryId);
        cv.put("StorageID", storageId);

        int updCount = db.update(TableItems, cv, "id = ?",
                new String[] { String.valueOf(id) }); // пиздос как логично
        Log.d(LOG_TAG,"Элемент:"+id+" обновлён");
    } */

    public List<Item> ReadAllItems() {

       Log.d(LOG_TAG, "Read table " + TableItems);
        List<Item> ItemModel = new ArrayList<>();
        Cursor c = db.query(TableItems, null, null, null, null, null, null);
        if (c != null) {
            Log.d(LOG_TAG, "Records count = " + c.getCount());

            if (c.moveToFirst()) {

                int idColIndex = c.getColumnIndex("ID");
                int nameColIndex = c.getColumnIndex("Name");
                int descriptionColIndex = c.getColumnIndex("Description");
                int weightColIndex = c.getColumnIndex("Weight");
                int quantityColIndex = c.getColumnIndex("Quantity");
                int categoryIdColIndex = c.getColumnIndex("CategoryID");
                int ImagePathColIndex=c.getColumnIndex("ImagePath");
                int dateColIndex = c.getColumnIndex("Date");

                do {
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("ID")));
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("Name")));
//                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("Description")));
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("Weight")));
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("Quantity")));
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("CategoryID")));

//                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("Date")));

//                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("ImagePath")));

                    ItemModel.add(new Item(c.getInt(idColIndex), c.getString(nameColIndex),
                            c.getString(descriptionColIndex), c.getInt(weightColIndex),
                            c.getInt(quantityColIndex), c.getInt(categoryIdColIndex),
                            c.getString(ImagePathColIndex), c.getString(dateColIndex)));

                } while (c.moveToNext());
            }
        }

        return ItemModel;


    }



    public CategoryList ReadAllCategories()
    {

        Log.d(LOG_TAG, "Read table " + TableCategories);
        CategoryList ItemModel = new CategoryList();
        Cursor c = db.query(TableCategories, null, null, null, null, null, null);
        if (c != null) {
            Log.d(LOG_TAG, "Records count = " + c.getCount());

            if (c.moveToFirst()) {

                int idColIndex = c.getColumnIndex("ID");
                int nameColIndex = c.getColumnIndex("Name");
                do {
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("ID")));
                    Log.d(LOG_TAG, c.getString(c.getColumnIndex("Name")));

                    ItemModel.add(new Category(c.getInt(idColIndex), c.getString(nameColIndex)));

                } while (c.moveToNext());
            }
        }
        return ItemModel;
    }

    public void RemoveItem(int ID) {
        Log.d(LOG_TAG, "Remove in table " + TableItems + " item: = " + ID);
       /* ContentValues cv = new ContentValues();
        cv.put("Name", name);*/
        db.delete(TableItems, "id = ?",
                new String[]{String.valueOf(ID)}); // пиздос как логично
        Log.d(LOG_TAG, "Элемент:" + ID + " обновлён");
    }

    public void ChangeItemQuantity(int ID,int Quantity)
    {
        Log.d(LOG_TAG, "Out in table "+ TableItems+ "item:= "+ ID+" Quantity get out:"+Quantity);
        ContentValues cv = new ContentValues();
        cv.put("Quantity", Quantity);
        db.update(TableItems, cv, "id = ?",
                new String[] { String.valueOf(ID) }); // пиздос как логично
        Log.d(LOG_TAG, "Элемент:" + ID + " списан");
    }


    public void DropItemTable() {
        Log.d(LOG_TAG, "Delete all from table " + TableItems);
        db.delete(TableItems, null, null);
    }

    public long InsertCategory(String name) {

        Log.d(LOG_TAG, "Insert in table " + TableCategories + " item: = " + name);
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        return db.insert(TableCategories, null, cv);
    }

    public void UpdateCategory(String name,int ID) {
        Log.d(LOG_TAG, "Update in table " + TableCategories + " item: = " + name);
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        db.update(TableCategories, cv, "id = ?",
                new String[]{String.valueOf(ID)}); // пиздос как логично
        Log.d(LOG_TAG, "Элемент:" + ID + " обновлён");
    }

    public void RemoveCategory(int ID) {
        Log.d(LOG_TAG, "Remove in table " + TableCategories + " item: = " + ID);
       /* ContentValues cv = new ContentValues();
        cv.put("Name", name);*/
        db.delete(TableCategories, "id = ?",
                new String[]{String.valueOf(ID)}); // пиздос как логично
        Log.d(LOG_TAG, "Элемент:" + ID + " удалён");
    }

    public int ItemsCount()
    {
        Cursor c = db.rawQuery("select count(*) from"+TableItems,null);
        c.moveToFirst();
       return c.getInt(0);
    }





}