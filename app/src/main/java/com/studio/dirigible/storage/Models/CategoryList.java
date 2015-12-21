package com.studio.dirigible.storage.Models;

import java.util.ArrayList;


public class CategoryList extends ArrayList<Category> {

    public Category getById(int id)
    {
        for(Category x:this)
        {
            if(x.Id==id)
                return x;
        }
        return null;
    }

    public int getIndex(int id)
    {

        for(int i=0;i<this.size();i++) {
            if(this.get(i).Id==id)
                return i;
        }
        return 0;
    }
}
