package com.pen.proyecto;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private MySQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new MySQLiteHelper(this);
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                int userId = obtenerUserId(usuario, password);
                if (userId != -1) {
                    // Login exitoso
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // Pasamos el ID y el Usuario (DNI)
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("USUARIO", usuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int obtenerUserId(String user, String pass) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + MySQLiteHelper.COLUMN_ADMIN_ID + " FROM " + MySQLiteHelper.TABLE_ADMIN + 
                       " WHERE " + MySQLiteHelper.COLUMN_ADMIN_USER + "=? AND " + 
                       MySQLiteHelper.COLUMN_ADMIN_PASS + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{user, pass});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
}
