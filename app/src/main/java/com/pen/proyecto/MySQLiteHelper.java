package com.pen.proyecto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gestion_residuos.db";
    private static final int DATABASE_VERSION = 4; // Incrementado para añadir sucursal y comentario

    // Tabla Admin
    public static final String TABLE_ADMIN = "admin";
    public static final String COLUMN_ADMIN_ID = "id";
    public static final String COLUMN_ADMIN_USER = "usuario";
    public static final String COLUMN_ADMIN_PASS = "password";

    // Tabla Registros
    public static final String TABLE_REGISTROS = "registros";
    public static final String COLUMN_REG_ID = "id";
    public static final String COLUMN_REG_DESCRIPCION = "descripcion"; // Usado para Tipo de Residuo
    public static final String COLUMN_REG_PESO = "peso";
    public static final String COLUMN_REG_FECHA = "fecha";
    public static final String COLUMN_REG_HORA = "hora";
    public static final String COLUMN_REG_IMAGEN = "imagen";
    public static final String COLUMN_REG_EMPLEADO_ID = "empleado_id";
    public static final String COLUMN_REG_SUCURSAL = "sucursal";
    public static final String COLUMN_REG_COMENTARIO = "comentario";

    private static final String CREATE_TABLE_ADMIN = "CREATE TABLE " + TABLE_ADMIN + " (" +
            COLUMN_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ADMIN_USER + " TEXT UNIQUE, " +
            COLUMN_ADMIN_PASS + " TEXT);";

    private static final String CREATE_TABLE_REGISTROS = "CREATE TABLE " + TABLE_REGISTROS + " (" +
            COLUMN_REG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_REG_DESCRIPCION + " TEXT, " +
            COLUMN_REG_PESO + " REAL, " +
            COLUMN_REG_FECHA + " TEXT, " +
            COLUMN_REG_HORA + " TEXT, " +
            COLUMN_REG_IMAGEN + " TEXT, " +
            COLUMN_REG_EMPLEADO_ID + " INTEGER, " +
            COLUMN_REG_SUCURSAL + " TEXT, " +
            COLUMN_REG_COMENTARIO + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_REG_EMPLEADO_ID + ") REFERENCES " + TABLE_ADMIN + "(" + COLUMN_ADMIN_ID + "));";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ADMIN);
        db.execSQL(CREATE_TABLE_REGISTROS);
        
        // Cuentas por defecto
        db.execSQL("INSERT INTO admin (usuario, password) VALUES ('admin', 'admin123')");
        db.execSQL("INSERT INTO admin (usuario, password) VALUES ('71234567', 'emp123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_REGISTROS + " ADD COLUMN " + COLUMN_REG_SUCURSAL + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_REGISTROS + " ADD COLUMN " + COLUMN_REG_COMENTARIO + " TEXT;");
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTROS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
            onCreate(db);
        }
    }
}
