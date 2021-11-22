package com.example.restaurante.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.restaurante.model.FoodModel;

import java.sql.Blob;
import java.util.ArrayList;

public class DatabaseHelper {

    public DatabaseHelper() {

    }

    public void createDatabase(Context ctx) {
        try {
            SQLiteDatabase db = ctx.openOrCreateDatabase("foods", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS food (name VARCHAR, description VARCHAR, price VARCHAR, hasGluten VARCHAR, calories VARCHAR, picture VARCHAR, picture_blob BLOB)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean insert(Context ctx, FoodModel foodModel) {
        SQLiteDatabase db = ctx.openOrCreateDatabase("foods", Context.MODE_PRIVATE, null);
        String sql = "INSERT INTO food (name, description, price, hasGluten, calories, picture, picture_blob) VALUES (";
        sql += "'" + foodModel.getName() + "', ";
        sql += "'" + foodModel.getDescription() + "', ";
        sql += "'A confirmar', ";
        sql += "'" + foodModel.getHasGluten() + "', ";
        sql += "'" + foodModel.getCalories() + "', ";
        sql += "'" + foodModel.getImgUrl() + "', ";
        sql += "?)";

        try {
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindBlob(1, foodModel.getPictureBlob());
            stmt.executeInsert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAll(Context ctx) {
        SQLiteDatabase db = ctx.openOrCreateDatabase("foods", Context.MODE_PRIVATE, null);
        String sql = "DELETE FROM food";
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteTable(Context ctx, String tableName) {
        SQLiteDatabase db = ctx.openOrCreateDatabase("foods", Context.MODE_PRIVATE, null);
        String sql = "DROP TABLE " + tableName;
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<FoodModel> getAll(Context ctx) {
        SQLiteDatabase db = ctx.openOrCreateDatabase("foods", Context.MODE_PRIVATE, null);
        String sql = "SELECT name, description, price, hasGluten, calories, picture, picture_blob FROM food";
        ArrayList<FoodModel> list = new ArrayList<FoodModel>();

        try {
            Cursor cursor = db.rawQuery(sql, null);

            int iName = cursor.getColumnIndex("name");
            int idescription = cursor.getColumnIndex("description");
            int iPrice = cursor.getColumnIndex("price");
            int iHasGluten = cursor.getColumnIndex("hasGluten");
            int iCalories = cursor.getColumnIndex("calories");
            int iPicture = cursor.getColumnIndex("picture");
            int iPictureBlob = cursor.getColumnIndex("picture_blob");

            cursor.moveToFirst();
            while(cursor != null) {

                FoodModel foodObj = new FoodModel();
                foodObj.setName(cursor.getString(iName));
                foodObj.setDescription(cursor.getString(idescription));
                foodObj.setPrice(cursor.getString(iPrice));
                foodObj.setHasGluten(cursor.getString(iHasGluten));
                foodObj.setCalories(cursor.getString(iCalories));
                foodObj.setImgUrl(cursor.getString(iPicture));
                foodObj.setPictureBlob(cursor.getBlob(iPictureBlob));
                list.add(foodObj);

                cursor.moveToNext();
            }
            return list;
        } catch(Exception e) {
            return list;
        }
    }

}
