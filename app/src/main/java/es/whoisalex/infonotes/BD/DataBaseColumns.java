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

        public static final String NOMBRE = "Nombre";
        public static final String LOCALIDAD = "Localidad";
        public static final String CATEGORIA = "Categoria";
        public static final String FECHA = "Fecha";
        public static final String DESCRIPCION = "Descripcion";
        public static final String COORDY = "CoordY";
        public static final String COORDX = "CoordX";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "idRemota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";


        public static final String[] TODOS = new String[] { _ID, NOMBRE, LOCALIDAD, CATEGORIA, FECHA, DESCRIPCION, COORDY, COORDX};
    }
}
