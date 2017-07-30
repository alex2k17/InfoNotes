package es.whoisalex.infonotes.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 20/07/2017.
 */

public class Helper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_NOTAS =
            "CREATE TABLE " + DataBaseColumns.Notas.TABLA + " (" +
                    DataBaseColumns.Notas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DataBaseColumns.Notas.NOMBRE + " TEXT, " +
                    DataBaseColumns.Notas.LOCALIDAD + " TEXT, " +
                    DataBaseColumns.Notas.CATEGORIA  + " TEXT, " +
                    DataBaseColumns.Notas.FECHA + " TEXT, " +
                    DataBaseColumns.Notas.DESCRIPCION + " TEXT, " +
                    DataBaseColumns.Notas.COORDY + " NUMERIC, " +
                    DataBaseColumns.Notas.COORDX + " NUMERIC )";

    public Helper (Context context, String nombreBD, SQLiteDatabase.CursorFactory factory, int versionBD){
        super(context, nombreBD, factory, versionBD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NOTAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseColumns.Notas.TABLA);
        db.execSQL(SQL_CREATE_NOTAS);
    }
}
