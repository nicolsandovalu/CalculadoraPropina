package com.example.calculadorapropinas

import android.os.Bundle
import android.view.View
import  android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadorapropinas.utils.CalculadoraUtils


class MainActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var rgPorcentaje: RadioGroup
    private lateinit var tvPropina: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnCalcular: Button
    private lateinit var btnLimpiar: Button
    private lateinit var etPersonalizado: EditText

    companion object {
        private const val PREFS_NAME = "PropinaPrefs"
        private const val PREF_RADIO_ID = "radioId"
        private const val PREF_CUSTOM = "customTip"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Vincular vistas
        etMonto = findViewById(R.id.etMonto)
        rgPorcentaje = findViewById(R.id.rgPorcentaje)
        etPersonalizado = findViewById(R.id.etPersonalizado)
        tvPropina = findViewById(R.id.tvPropina)
        tvTotal = findViewById(R.id.tvTotal)
        btnCalcular = findViewById(R.id.btnCalcular)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        //Restaturar preferencias guardadas
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val radioId = prefs.getInt(PREF_RADIO_ID, -1)
        val customTip = prefs.getFloat(PREF_CUSTOM, -1f)

        if (radioId != -1){
            rgPorcentaje.check(radioId)
        }

        if (customTip != -1f){
            etPersonalizado.setText((customTip * 100).toString())
            etPersonalizado.visibility = View.VISIBLE
        }

        //Mostrar/ocultar campo personalizado
        rgPorcentaje.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbPersonalizado) {
                etPersonalizado.visibility = View.VISIBLE
            } else {
                etPersonalizado.text.clear()
                etPersonalizado.visibility = View.GONE
            }
        }

        //acciones de los botones
        btnCalcular.setOnClickListener{
            calcularPropina()
        }

        btnLimpiar.setOnClickListener{
            limpiarCampos()
        }
    }

    private fun calcularPropina() {
        val montoTexto = etMonto.text.toString().trim()
        val porcentajeTexto = etPersonalizado.text.toString().trim()


        if (montoTexto.isEmpty()) {
            Toast.makeText(  this,  "Por favor ingresa un monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(  this, "Ingresa un monto válido mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val porcentaje = if (porcentajeTexto.isNotEmpty()){
            val personalizado = porcentajeTexto.toDoubleOrNull()
            if (personalizado == null || personalizado <= 0) {
                Toast.makeText(this, "Ingresa un porcentaje personalizado válido", Toast.LENGTH_SHORT).show()
                return
            }
            personalizado / 100
        } else {
            when (rgPorcentaje.checkedRadioButtonId) {
                R.id.rb10 -> 0.10
                R.id.rb15 -> 0.15
                R.id.rb20 -> 0.20
                else -> {
                    Toast.makeText(this,"Selecciona un porcentaje o ingresa uno personalizado", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        //Guardar preferencia

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()

        if (porcentajeTexto.isNotEmpty()) {
            editor.putFloat(PREF_CUSTOM, porcentaje.toFloat())
            editor.putInt(PREF_RADIO_ID, -1)
        } else {
            editor.putInt(PREF_RADIO_ID, rgPorcentaje.checkedRadioButtonId)
            editor.remove(PREF_CUSTOM)
        }

        editor.apply()

        //Cálculo y resultado

        val propina = CalculadoraUtils.calcularPropina(monto, porcentaje)
        val total = monto + propina

        tvPropina.alpha = 0f
        tvTotal.alpha = 0f

        tvPropina.text = "Propina: $%.2f".format(propina)
        tvTotal.text = "Total Final: $%.2f".format(total)

        tvPropina.animate().alpha(1f).setDuration(400).start()
        tvTotal.animate().alpha( 1f).setDuration(400).start()
    }

    private fun limpiarCampos() {
        etMonto.text.clear()
        rgPorcentaje.clearCheck()
        etPersonalizado.text.clear()
        tvPropina.text = "Propina: $0.00"
        tvTotal.text = "Total Final: $0.00"
        tvPropina.animate().alpha( 0f).setDuration(300).start()
        tvTotal.animate().alpha(0f).setDuration(300).start()
    }
}