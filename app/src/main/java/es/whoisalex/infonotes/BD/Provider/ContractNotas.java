package es.whoisalex.infonotes.BD.Provider;

import android.content.UriMatcher;
import android.net.Uri;

/**
 * Created by Alex on 20/07/2017.
 */

public class ContractNotas {

    public final static String AUTHORITY
            ="es.whoisalex.infonotes";

    public final static String NOTAS
            = "Nota";

    public final static String SINGLE_MINE =
            "vnd.android.cursor.item/vnd." + AUTHORITY + NOTAS;

    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + NOTAS;

    public final static Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + NOTAS);

    public static final UriMatcher uriMatcher;

    public static final int ALLROWS = 1;

    public static final int SINGLE_ROW = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, NOTAS, ALLROWS);
        uriMatcher.addURI(AUTHORITY, NOTAS + "/#", SINGLE_ROW);
    }

    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;
}
