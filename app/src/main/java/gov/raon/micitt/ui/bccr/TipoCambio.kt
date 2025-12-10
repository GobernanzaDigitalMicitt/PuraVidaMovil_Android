package gov.raon.micitt.ui.bccr

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root


data class ListaTipoCambio(
    var listado: List<IndicadorEconomico> = emptyList()
)

@Root(name = "Datos_de_INGC011_CAT_INDICADORECONOMIC", strict = false)
data class DatosIndicadorEconomico(
    @field:Element(name = "INGC011_CAT_INDICADORECONOMIC", required = false)
    var indicadorEconomico: IndicadorEconomico? = null
)

@Root(name = "INGC011_CAT_INDICADORECONOMIC", strict = false)
data class IndicadorEconomico(
    @field:Element(name = "COD_INDICADORINTERNO", required = false)
    var codIndicadorInterno: String? = null,
    @field:Element(name = "DES_FECHA", required = false)
    var desFecha: String? = null,
    @field:Element(name = "NUM_VALOR", required = false)
    var numValor: String? = null
)
{
    fun getPrecioFormateado(): String {
        val valorNumerico = numValor?.toDoubleOrNull() ?: return "Valor no disponible"
        return "¢${"%.2f".format(valorNumerico)}"
    }

    fun getDescripcionIndicador(): String {
        return when (codIndicadorInterno) {
            "317" -> "Compra"
            "318" -> "Venta"
            else -> "Indicador desconocido"
        }
    }
}