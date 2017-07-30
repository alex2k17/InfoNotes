package es.whoisalex.infonotes.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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


    public Notas cursorToNotas(Cursor cursorNotas) {

        Notas nota = new Notas();
        nota.setId(cursorNotas.getLong(0));
        nota.setNombre(cursorNotas.getString(1));
        nota.setLocalidad(cursorNotas.getString(2));
        nota.setCategoria(cursorNotas.getString(3));
        nota.setDate(cursorNotas.getString(4));
        nota.setDescripcion(cursorNotas.getString(5));
        nota.setCoordY(cursorNotas.getDouble(6));
        nota.setCoordX(cursorNotas.getDouble(7));

        return nota;
    }

    public Cursor queryAllNotas(String prov) {
        // Se realiza la consulta, retornando el cursor resultado.
        return bd.rawQuery("SELECT * FROM "+DataBaseColumns.Notas.TABLA+" WHERE "+DataBaseColumns.Notas.LOCALIDAD+" = '"+prov+"'",null);
    }

    public List<Notas> getNotasByProvincia(String prov) {
        List<Notas> lista = new ArrayList<Notas>();
        Cursor cursor = this.queryAllNotas(prov);
        // LLena la lista convirtiendo cada registro del cursor
        // en un elemento de la lista.
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Notas nota = cursorToNotas(cursor);
            lista.add(nota);
            cursor.moveToNext();
        }
        // Cierro el cursor (IMPORTANTE).
        cursor.close();
        // Retorno la lista.
        return lista;
    }

    public Notas queryNotaProvincia(String prov){
        Cursor cursor = bd.query(true, DataBaseColumns.Notas.TABLA,
                DataBaseColumns.Notas.TODOS, DataBaseColumns.Notas.LOCALIDAD + " = '"+prov+"'",
                null, null, null, null, null);
        // Se mueve al primer registro del cursor.
        if (cursor != null) {
            cursor.moveToFirst();
            return cursorToNotas(cursor);
        } else {
            return null;
        }
    }

    public void dropBD() {
        String sql="DELETE FROM "+DataBaseColumns.Notas.TABLA;
        // Se realiza la query SQL sobre la BD.
        bd.execSQL(sql);
    }

    public List<Notas> getAllNotas(){
        List<Notas> lista = new ArrayList<Notas>();
        Cursor cursor = this.getAll();
        // LLena la lista convirtiendo cada registro del cursor
        // en un elemento de la lista.
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Notas nota = cursorToNotas(cursor);
            lista.add(nota);
            cursor.moveToNext();
        }
        // Cierro el cursor (IMPORTANTE).
        cursor.close();
        // Retorno la lista.
        return lista;
    }

    public Cursor getAll() {
        String sql = "SELECT * FROM "+DataBaseColumns.Notas.TABLA;
        // Se realiza la query SQL sobre la BD.
        Cursor cursor = bd.rawQuery(sql,null);
        // Se mueve al primer registro del cursor.
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

}
