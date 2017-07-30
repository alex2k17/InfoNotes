package es.whoisalex.infonotes.BD;

import android.provider.BaseColumns;

/**
 * Created by Alex on 20/07/2017.
 */

public class DataBaseColumns {
    public static final String BD_NOMBRE = "InfoNotes";
    public static final int BD_VERSION = 1;

    public static abstract class Notas implements BaseColumns{

        public static final String TABLA = "Nota";

        public static final String NOMBRE = "nombre";
        public static final String LOCALIDAD = "localidad";
        public static final String CATEGORIA = "categoria";
        public static final String FECHA = "fecha";
        public static final String DESCRIPCION = "descripcion";
        public static final String COORDY = "coordY";
        public static final String COORDX = "coordX";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";


        public static final String[] TODOS = new String[] { _ID, NOMBRE, LOCALIDAD, CATEGORIA, FECHA, DESCRIPCION, COORDY, COORDX};
    }
}
