package com.pen.proyecto;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.View;
=======
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

<<<<<<< HEAD
=======
    public static final String EXTRA_USUARIO = "usuario";

>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
    private EditText etUsuario, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

<<<<<<< HEAD
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validación simple (usuario: admin, password: 1234)
                if (usuario.equals("admin") && password.equals("1234")) {
                    // Login exitoso
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Cierra Login para que no vuelvas con "atrás"
                } else {
                    // Login fallido
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
=======
        // Cargar animación (opcional, si tienes los archivos)
        Animation shakeAnimation = null;
        try {
            shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        } catch (Exception e) {
            // Si no existe el archivo, no hay animación
        }

        Animation buttonClickAnimation = null;
        try {
            buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        } catch (Exception e) {
            // Si no existe el archivo, no hay animación
        }

        final Animation finalShakeAnimation = shakeAnimation;
        final Animation finalButtonClickAnimation = buttonClickAnimation;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Efecto de clic si existe la animación
                if (finalButtonClickAnimation != null) {
                    v.startAnimation(finalButtonClickAnimation);
                }

                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validaciones
                if (TextUtils.isEmpty(usuario)) {
                    etUsuario.setError("Ingresa tu usuario");
                    if (finalShakeAnimation != null) {
                        etUsuario.startAnimation(finalShakeAnimation);
                    }
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Ingresa tu contraseña");
                    if (finalShakeAnimation != null) {
                        etPassword.startAnimation(finalShakeAnimation);
                    }
                    return;
                }

                if (password.length() < 4) {
                    etPassword.setError("La contraseña debe tener al menos 4 caracteres");
                    if (finalShakeAnimation != null) {
                        etPassword.startAnimation(finalShakeAnimation);
                    }
                    return;
                }

                // Mostrar loading (cambiar texto del botón)
                btnLogin.setText("Iniciando sesión...");
                btnLogin.setEnabled(false);

                // Simular proceso de login (1 segundo)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Validación simple (usuario: admin, password: 1234)
                        if (usuario.equals("admin") && password.equals("1234")) {
                            // Login exitoso
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra(EXTRA_USUARIO, usuario);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            // Login fallido
                            btnLogin.setText("Iniciar Sesión");
                            btnLogin.setEnabled(true);
                            Toast.makeText(LoginActivity.this,
                                    "Usuario o contraseña incorrectos",
                                    Toast.LENGTH_SHORT).show();

                            // Animación de error si existe
                            if (finalShakeAnimation != null) {
                                etUsuario.startAnimation(finalShakeAnimation);
                                etPassword.startAnimation(finalShakeAnimation);
                            }
                        }
                    }
                }, 1000);
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
            }
        });
    }
}