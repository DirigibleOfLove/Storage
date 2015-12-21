package com.studio.dirigible.storage.Models;


public class Item implements Cloneable{//implements Parcelable {

    final String LOG_TAG = "StorageLogs";

    public Item() {
    }

    public Item(int ID, String name, String description, int weight, int quantity, int categoryId, String imagePath, String date) {
        this.ID = ID;
        this.Name = name;
        this.Weight = weight;
        this.Quantity = quantity;
        this.CategoryId = categoryId;
        this.ImagePath = imagePath;
        this.Date = date;
        this.Description = description;

    }

    public int ID;
    public String Name;
    public int Weight;
    public int Quantity;
    public int CategoryId;
    public String ImagePath;
    public String Date;
    public String Description;

    public String getName()
    {
        return Name;
    }

    public Item clone() throws CloneNotSupportedException{

        return (Item) super.clone();
    }



}
