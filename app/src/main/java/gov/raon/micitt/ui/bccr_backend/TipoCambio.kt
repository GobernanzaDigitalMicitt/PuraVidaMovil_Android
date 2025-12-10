package gov.raon.micitt.ui.bccr_backend

data class TipoCambio(
    val descripcion: String,
    val fechaRige: String,
    val precio: String
){
    fun getPrecioFormateado(): String = "¢${"%.2f".format(precio)}"
}
