package es.whoisalex.infonotes.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.sql.SQLException;

import es.whoisalex.infonotes.POJOS.Notas;

/**
 * Created by Alex on 20/07/2017.
 */

public class DAO {
    private Helper helper;
    private SQLiteDatabase bd;

    public DAO (Context context){
        helper = new Helper(context, DataBaseColumns.BD_NOMBRE, null, DataBaseColumns.BD_VERSION);
    }

    //Abrimos la BD, se retorna a si mismo.
    public DAO open() throws SQLException{
        bd = helper.getWritableDatabase();
        return this;
    }

    //Cerramos la BD
    public void close(){
        //Se cierra la BD a través del helper
        helper.close();
    }

    public long createNota(Notas nota){
        ContentValues valores = new ContentValues();
        valores.put(DataBaseColumns.Notas.NOMBRE, nota.getNombre());
        valores.put(DataBaseColumns.Notas.LOCALIDAD, nota.getLocalidad());
        valores.put(DataBaseColumns.Notas.CATEGORIA, nota.getCategoria());
        valores.put(DataBaseColumns.Notas.FECHA, nota.getDate());
        valores.put(DataBaseColumns.Notas.DESCRIPCION, nota.getDescripcion());
        valores.put(DataBaseColumns.Notas.COORDY, nota.getCoordY());
        valores.put(DataBaseColumns.Notas.COORDX, nota.getCoordX());

        return bd.insert(DataBaseColumns.Notas.TABLA, null, valores);
    }

}
