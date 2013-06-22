package com.scripturesos.tantest.connection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "tantest";
	private static Context mCtx;
    //SQL estado inicial
    String sqlCreate = "CREATE TABLE IF NOT EXISTS options (key INTEGER UNIQUE, value TEXT)";
 
    /*
     * 0 = phoe number
     * 1 = country code
     * 2 = ...
     */
    private DatabaseHelper(Context context)               
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) 
    {   
        db.execSQL(sqlCreate);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva)
    {
        /*
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
 
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
        */
    }
    
    private static class InstanceHolder 
	{
		private static final DatabaseHelper instance = new DatabaseHelper(mCtx);
	}

	public static DatabaseHelper getInstance(Context ctx) 
	{
	    mCtx = ctx;
	    
		return InstanceHolder.instance;
	}
}