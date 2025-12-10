package gov.raon.micitt.ui.salud

import com.google.gson.annotations.SerializedName

data class TarifaCombustible(
    @SerializedName("id") val id: String,
    @SerializedName("id_TipoCombustible") val tipoCombustibleId: String,
    @SerializedName("descripcion") val nombre: String,
    @SerializedName("precio") val precio: Double, // Cambiado a Double para cálculos
    @SerializedName("fechaRige") val fechaVigencia: String
) {
    fun getPrecioFormateado(): String = "¢${"%.2f".format(precio)}"
}

data class ResponseCombustibles(
    @SerializedName("value") val tarifas: List<TarifaCombustible>
)