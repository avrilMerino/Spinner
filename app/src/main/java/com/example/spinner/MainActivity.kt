package com.example.spinner
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher//para detectar cuando el usuario escribe en los EditText
import android.view.View
import android.widget.*  //EditText, Spinner, TextView, ArrayAdapter, etc.
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    // Referencias a los elementos del layout
    // "lateinit" = para prometer que las inicializo antes de usarlas (lo haré en onCreate)
    private lateinit var etValor1: EditText
    private lateinit var etValor2: EditText
    private lateinit var spOperacion: Spinner
    private lateinit var tvResultado: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Conectamos las variables de arriba con los widgets del XML
        etValor1 = findViewById(R.id.etValor1)
        etValor2 = findViewById(R.id.etValor2)
        spOperacion = findViewById(R.id.spOperacion)
        tvResultado = findViewById(R.id.tvResultado)

        //lista de textos que aparecerán en el Spinner (las operaciones)
        val operaciones = arrayOf("Suma", "Resta", "Multiplicación", "División")

        // un Adapter "traduce" la lista de Strings a vistas visibles dentro del Spinner.
        // usamos un layout personalizado (spinner_item) para que tenga el tamaño y color pedidos
        val adapter = ArrayAdapter(this, R.layout.spinner_item, operaciones)

        //layout a usar cuando se abre el desplegable del Spinner (lista que cae).
        adapter.setDropDownViewResource(R.layout.spinner_item)

        // Le damos al Spinner su adapter para que pueda mostrar la lista.
        spOperacion.adapter = adapter

        //textWatcher: objeto que "escucha" cambios de texto en los EditText.
        //solo usamos afterTextChanged: cuando el usuario termina de teclear, recalculamos.
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { calcular() }
        }
        // Asociamos el watcher a los dos EditText.
        etValor1.addTextChangedListener(watcher)
        etValor2.addTextChangedListener(watcher)

        // Listener del Spinner: cuando el usuario cambia la opción, recalculamos.
        spOperacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // p2 es la posición elegida (0=Suma, 1=Resta, ...)
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                calcular()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { /* no hacemos nada */ }
        }

        // Hacemos un primer cálculo al iniciar (por si hay valores escritos).
        calcular()
    }

    // Función que:
    // 1)Lee los números de los EditText
    // 2)Mira qué operación está seleccionada en el Spinner
    // 3)Calcula el resultado y lo muestra en el TextView
    private fun calcular() {
        // toDoubleOrNull() intenta convertir el texto a número:
        //   - si puede -> devuelve Double
        //   - si NO puede (vacío o letras) -> devuelve null
        // Elvis operator (?:) pone 0.0 si era null (así no se cae la app).
        val a = etValor1.text.toString().toDoubleOrNull() ?: 0.0
        val b = etValor2.text.toString().toDoubleOrNull() ?: 0.0

        // selectedItem puede ser null si aún no hay nada seleccionado,
        // por eso usamos ?. (safe call) y si fuera null, ponemos "Suma" por defecto.
        val op = spOperacion.selectedItem?.toString() ?: "Suma"

        // Calculamos según el texto de la operación.
        // "when" como un switch
        // Para división controlamos el caso b==0 para no dividir entre cero.
        val res = when (op) {
            "Suma" -> a + b
            "Resta" -> a - b
            "Multiplicación" -> a * b
            "División" -> if (b == 0.0) null else a / b
            else -> a + b   //por si acaso (no debería entrar aquí)
        }
        // Mostramos el resultado en el TextView:
        // - Si res no es null -> lo convertimos a String.
        //   Además, si es un número "entero exacto" (40.0), lo mostramos como "40".
        // - Si res es null (por ejemplo, división por 0) -> mostramos "-".
        tvResultado.text = res?.let {
            val i = it.toLong()
            if (it == i.toDouble()) i.toString() else it.toString()
        } ?: "-"
    }
}
