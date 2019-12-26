package com.example.misapps.ui.juegos;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;



public class ControladorBD extends SQLiteOpenHelper {

    //Sentencia que creerá la tabla
    private final static String CREATE_TABLA_JUEGO = "CREATE TABLE Juego (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR, " +
            "plataforma VARCHAR, precio FLOAT, imagen BLOB, fechaLanzamiento DATE, FOREIGN KEY (plataforma) REFERENCES Plataforma(nombre) )";
    private final static String CREATE_TABLA_PLATAFORMA = "CREATE TABLE Plataforma(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(8))";


    public ControladorBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Cuando se llame al onCreate se realiza la sentencia
        db.execSQL(CREATE_TABLA_PLATAFORMA);
        db.execSQL(CREATE_TABLA_JUEGO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se borra y se vuelve a crear para "actualizar" la versión de la tabla
        db.execSQL("DROP TABLE IF EXISTS Juego");
        db.execSQL(CREATE_TABLA_PLATAFORMA);
        db.execSQL(CREATE_TABLA_JUEGO);
    }

}
