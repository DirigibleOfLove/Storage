package com.studio.dirigible.storage.Adapters;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.studio.dirigible.storage.Models.Item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dirigible on 03.12.15.
 */
public class FileReportAdapter {

    Context context;
    Item olditem;

    final String inventoryName= "Инвентаризация:";
    final String arrivalName = "Приход:";
    final String outName="Списание:";

    public FileReportAdapter(Context contex) {
        this.context = context;
    }

    public FileReportAdapter(Context context,Item olditem)
    {
        this.context=context;
        this.olditem=olditem;
    }

    final String LOG_TAG = "myLogs";

    final String FILENAME_SD = "report";

    final String DIR_SD = "Storage/Reports";

    BufferedWriter bw;

    public void writeInsert(Item item, String category) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);

        // создаем каталог
        sdPath.mkdirs();

        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            bw = new BufferedWriter(new FileWriter(sdFile,true));
            // пишем данные
            bw.write(arrivalName + "\nДата операции:"+getDateTime()+
                    "\nДата:"+item.Date +
                    " ID:"+item.ID+" Наименоване:"+item.Name+" Категория:"+category+
                    " Описание:"+item.Description+" Вес:" +item.Weight+" Количество:"+item.Quantity+"\n");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeEdit(Item item, String category) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);

        // создаем каталог
        sdPath.mkdirs();

        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            bw = new BufferedWriter(new FileWriter(sdFile,true));
            // пишем данные
            bw.write(inventoryName + "\nДата операции:"+getDateTime()+
                    "\nДата:"+olditem.Date +
                    " ID:"+olditem.ID+" Наименоване:"+olditem.Name+" Категория:"+category+
                    " Описание:"+olditem.Description+" Вес:" +olditem.Weight+" Количество:"+olditem.Quantity);
            bw.write("\nЗаменён на:");
            bw.write("\nДата:"+item.Date +
                    " ID:"+item.ID+" Наименоване:"+item.Name+" Категория:"+category+
                    " Описание:"+item.Description+" Вес:" +item.Weight+" Количество:"+item.Quantity+"\n");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDelete(String category) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);

        // создаем каталог
        sdPath.mkdirs();

        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            bw = new BufferedWriter(new FileWriter(sdFile,true));
            // пишем данные
            bw.write(inventoryName + "\nУдаление" + "\nДата операции:"+getDateTime()+
                    "\nДата:"+olditem.Date +
                    " ID:"+olditem.ID+" Наименоване:"+olditem.Name+" Категория:"+category+
                    " Описание:"+olditem.Description+" Вес:" +olditem.Weight+" Количество:"+olditem.Quantity);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOut(Item item, String category,int count)
    {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);

        // создаем каталог
        sdPath.mkdirs();

        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            bw = new BufferedWriter(new FileWriter(sdFile,true));
            // пишем данные
            bw.write(outName + "\nДата операции:"+getDateTime()+
                    "\nДата:"+item.Date +
                    " ID:"+item.ID+" Наименоване:"+item.Name+" Категория:"+category+
                    " Описание:"+item.Description+" Вес:" +item.Weight+"\n"+
            "Списано "+count+" из "+item.Quantity+"\n");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }



    public void readFileSD(TextView tv) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));

            List<String> ls =new  ArrayList<String>();
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
             /*   if(br.readLine().compareTo(inventoryName)==0)
                    ; */

                tv.setText(tv.getText()+"\n-----------------------------\n"+str);
                Log.d(LOG_TAG, str);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
