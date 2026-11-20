package com.example.generadorvideoia

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var imageLabel: TextView
    private lateinit var creditsLabel: TextView
    private var selectedImage: Uri? = null
    private val pickImage = 4102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
    }

    private fun buildScreen() {
        val bg = Color.rgb(11, 14, 27)
        val panel = Color.rgb(25, 31, 52)
        val white = Color.WHITE
        val muted = Color.rgb(180, 190, 210)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 26, 22, 26)
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)

        val title = TextView(this).apply {
            text = "CINEMA AI ULTRA"
            textSize = 27f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 4)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val subtitle = label("Tu estudio de video con inteligencia artificial", muted, 14f)
        subtitle.gravity = Gravity.CENTER
        val badge = TextView(this).apply {
            text = "PRO  •  ULTRA  •  TURBO"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(130, 210, 255))
            setPadding(0, 18, 0, 14)
        }

        val prefs = getSharedPreferences("daily_credits", MODE_PRIVATE)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
        if (prefs.getString("day", "") != today) {
            prefs.edit().putString("day", today).putInt("credits", 3).apply()
        }
        creditsLabel = label("Créditos de demostración: ${prefs.getInt("credits", 3)} / 3", Color.rgb(112, 230, 170), 16f)
        creditsLabel.gravity = Gravity.CENTER

        val prompt = EditText(this).apply {
            hint = "Contá qué video querés crear... Ej.: un guerrero lucha contra monstruos en una ciudad futurista."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 15f
            minLines = 4
            gravity = Gravity.TOP
            setPadding(16, 14, 16, 14)
            setBackgroundColor(panel)
        }
        val imageButton = button("＋  Elegir una imagen")
        imageLabel = label("Ninguna imagen seleccionada", muted, 13f)
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            startActivityForResult(Intent.createChooser(intent, "Elegí una imagen"), pickImage)
        }

        val engine = spinner(listOf("TURBO · rápido y económico", "PRO · cinematográfico", "ULTRA · máxima calidad"))
        val style = spinner(listOf("Cinematográfico realista", "Ciencia ficción", "Acción y peleas", "Fantasía y monstruos", "Terror y suspenso", "Anime realista", "Superhéroes"))
        val duration = spinner(listOf("15 segundos", "30 segundos", "1 minuto", "2 minutos"))
        val format = spinner(listOf("Horizontal 16:9", "Vertical 9:16", "Cuadrado 1:1"))
        val generate = button("PREPARAR MI VIDEO")
        status = label("Listo para preparar tu idea. La generación real requiere conectar un servicio de video IA.", muted, 14f)
        val note = label("Importante: esta versión es una base instalable de prueba. Todavía no genera videos reales ni ofrece eliminación de marca de agua. Los créditos son demostrativos y se reinician por fecha del dispositivo.", Color.rgb(255, 205, 130), 12f)

        content.addView(title); content.addView(subtitle); content.addView(badge)
        content.addView(creditsLabel)
        content.addView(section("DESCRIBÍ TU VIDEO", white)); content.addView(prompt, lp())
        content.addView(section("IMAGEN DE REFERENCIA", white)); content.addView(imageButton, lp()); content.addView(imageLabel)
        content.addView(section("MOTOR DE IA", white)); content.addView(engine, lp())
        content.addView(section("ESTILO VISUAL", white)); content.addView(style, lp())
        content.addView(section("DURACIÓN", white)); content.addView(duration, lp())
        content.addView(section("FORMATO", white)); content.addView(format, lp())
        content.addView(generate, lp()); content.addView(status); content.addView(note)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)

        generate.setOnClickListener {
            if (prompt.text.toString().trim().isEmpty() && selectedImage == null) {
                status.text = "Escribí una idea o elegí una imagen para continuar."
            } else {
                status.text = "Tu proyecto quedó configurado: ${engine.selectedItem}, ${style.selectedItem}, ${duration.selectedItem}. Falta conectar el proveedor de generación para producir y descargar el video."
            }
        }
    }

    private fun label(text: String, color: Int, size: Float) = TextView(this).apply {
        this.text = text; setTextColor(color); textSize = size; setPadding(2, 10, 2, 10)
    }
    private fun section(text: String, color: Int) = TextView(this).apply {
        this.text = text; setTextColor(color); textSize = 13f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        setPadding(2, 18, 2, 7)
    }
    private fun button(textValue: String) = Button(this).apply {
        text = textValue; isAllCaps = false; textSize = 15f
    }
    private fun spinner(items: List<String>) = Spinner(this).apply {
        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, items)
    }
    private fun lp() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 7 }

    @Deprecated("Deprecated in Android API; retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage && resultCode == RESULT_OK) {
            selectedImage = data?.data
            imageLabel.text = if (selectedImage != null) "Imagen seleccionada correctamente" else "No se seleccionó ninguna imagen"
        }
    }
}
