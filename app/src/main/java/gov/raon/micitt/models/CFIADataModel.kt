package gov.raon.micitt.models

data class CFIADataModel(
    val cedula: String,
    val carne: List<String>,
    val nombre: String,
    val apellidoUno: String,
    val apellidoDos: String,
    val fechaNacimiento: String,
    val nacionalidad: String,
    val foto: String,
    val telefonos: List<String>,
    val lugarResidencia: String,
    val colegiosIncoporado: List<String>,
    val ramas: List<String>,
    val fechasIncorporacion: List<String>,
    val correosElectronicos: String,
    val miembroAlDia: String,
    val condicionMiembro: String,
    val formaPago: String,
    val periodicidadPago: String,
    val ultPeriodoPagado: String,
    val exentoPago: String,
    val criterioAceptacion: Int
)
