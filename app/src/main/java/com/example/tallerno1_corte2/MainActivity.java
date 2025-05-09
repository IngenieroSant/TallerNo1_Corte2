package com.example.tallerno1_corte2;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText etX1, etY1, etX2, etY2;
    Button btnPendiente, btnPuntoMedio, btnEcuacionRecta, btnCuadrantes;
    TextView tvResultado;
    View selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etX1 = findViewById(R.id.etX1);
        etY1 = findViewById(R.id.etY1);
        etX2 = findViewById(R.id.etX2);
        etY2 = findViewById(R.id.etY2);
        tvResultado = findViewById(R.id.tvResultado);

        btnPendiente = findViewById(R.id.btnPendiente);
        btnPuntoMedio = findViewById(R.id.btnPuntoMedio);
        btnEcuacionRecta = findViewById(R.id.btnEcuacionRecta);
        btnCuadrantes = findViewById(R.id.btnCuadrantes);

        // Registra los EditText para el menú contextual
        registerForContextMenu(etX1);
        registerForContextMenu(etY1);
        registerForContextMenu(etX2);
        registerForContextMenu(etY2);

        // Cuadrantes
        registerForContextMenu(btnCuadrantes);

        btnPendiente.setOnClickListener(v -> {
            Double[] puntos = obtenerPuntos();
            if (puntos == null) return;

            double pendiente = (puntos[3] - puntos[1]) / (puntos[2] - puntos[0]);
            tvResultado.setText("Pendiente: " + pendiente);
        });

        btnPuntoMedio.setOnClickListener(v -> {
            Double[] puntos = obtenerPuntos();
            if (puntos == null) return;

            double xm = (puntos[0] + puntos[2]) / 2;
            double ym = (puntos[1] + puntos[3]) / 2;
            tvResultado.setText("Punto Medio: (" + xm + ", " + ym + ")");
        });

        btnEcuacionRecta.setOnClickListener(v -> {
            Double[] puntos = obtenerPuntos();
            if (puntos == null) return;

            double m = (puntos[3] - puntos[1]) / (puntos[2] - puntos[0]);
            double b = puntos[1] - m * puntos[0];
            tvResultado.setText("Ecuación: y = " + m + "x + " + b);
        });

        btnCuadrantes.setOnClickListener(v -> {
            Double[] puntos = obtenerPuntos();
            if (puntos == null) return;

            String cuadrante1 = obtenerCuadrante(puntos[0], puntos[1]);
            String cuadrante2 = obtenerCuadrante(puntos[2], puntos[3]);
            tvResultado.setText("P1: " + cuadrante1 + "\nP2: " + cuadrante2);
        });
    }

    private Double[] obtenerPuntos() {
        try {
            String x1Text = etX1.getText().toString();
            String y1Text = etY1.getText().toString();
            String x2Text = etX2.getText().toString();
            String y2Text = etY2.getText().toString();

            // Verifica si los campos están vacíos
            if (x1Text.isEmpty() || y1Text.isEmpty() || x2Text.isEmpty() || y2Text.isEmpty()) {
                tvResultado.setText("⚠️ Error: Todos los campos deben ser llenados.");
                return null;
            }

            double x1 = Double.parseDouble(x1Text);
            double y1 = Double.parseDouble(y1Text);
            double x2 = Double.parseDouble(x2Text);
            double y2 = Double.parseDouble(y2Text);
            return new Double[]{x1, y1, x2, y2};
        } catch (NumberFormatException e) {
            tvResultado.setText("⚠️ Error: Ingresa solo números válidos.");
            return null;
        }
    }

    private String obtenerCuadrante(double x, double y) {
        if (x > 0 && y > 0) return "Primer cuadrante";
        if (x < 0 && y > 0) return "Segundo cuadrante";
        if (x < 0 && y < 0) return "Tercer cuadrante";
        if (x > 0 && y < 0) return "Cuarto cuadrante";
        if (x == 0 && y == 0) return "Origen";
        if (x == 0) return "Sobre eje Y";
        if (y == 0) return "Sobre eje X";
        return "Indefinido";
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        selectedView = v;

        // Verifica que solo los EditText sean registrados
        if (v instanceof EditText) {
            menu.setHeaderTitle("Opciones del campo");
            menu.add(0, 1, 0, "Aleatorio");
            menu.add(0, 2, 0, "Cambiar Signo");
        } else if (v.getId() == R.id.btnCuadrantes) {
            menu.setHeaderTitle("Generar puntos por cuadrante");
            menu.add(1, 1, 0, "Primer Cuadrante");
            menu.add(1, 2, 0, "Segundo Cuadrante");
            menu.add(1, 3, 0, "Tercer Cuadrante");
            menu.add(1, 4, 0, "Cuarto Cuadrante");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (item.getGroupId() == 0) {
                if (id == 1) { // Aleatorio
                    double aleatorio = new Random().nextInt(201) - 100; // Genera número entre -100 y 100
                    ((EditText) selectedView).setText(String.valueOf(aleatorio));
                } else if (id == 2) { // Cambiar signo
                    try {
                        double value = Double.parseDouble(((EditText) selectedView).getText().toString());
                        ((EditText) selectedView).setText(String.valueOf(value * -1));
                    } catch (NumberFormatException e) {
                        tvResultado.setText("⚠️ Campo vacío o inválido.");
                    }
                }
            } else if (item.getGroupId() == 1) {
                generarCuadrante(id); // No restar 2, el id ahora es correcto
            }
        } catch (Exception e) {
            tvResultado.setText("⚠️ Ocurrió un error: " + e.getMessage());
        }
        return true;
    }

    private void generarCuadrante(int cuadrante) {
        Random rand = new Random();
        int signX = 1, signY = 1;

        switch (cuadrante) {
            case 1: signX = 1; signY = 1; break; // Primer cuadrante
            case 2: signX = -1; signY = 1; break; // Segundo cuadrante
            case 3: signX = -1; signY = -1; break; // Tercer cuadrante
            case 4: signX = 1; signY = -1; break; // Cuarto cuadrante
        }

        etX1.setText(String.valueOf(signX * (rand.nextInt(9) + 1)));
        etY1.setText(String.valueOf(signY * (rand.nextInt(9) + 1)));
        etX2.setText(String.valueOf(signX * (rand.nextInt(9) + 1)));
        etY2.setText(String.valueOf(signY * (rand.nextInt(9) + 1)));
        tvResultado.setText("🎯 Puntos generados en el cuadrante " + cuadrante);
    }
}
