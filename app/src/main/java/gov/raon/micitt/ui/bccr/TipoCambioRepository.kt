package gov.raon.micitt.ui.bccr

import androidx.core.text.HtmlCompat
import gov.raon.micitt.di.network.BccrClient
import gov.raon.micitt.di.network.TipoCambioService
import org.simpleframework.xml.core.Persister

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TipoCambioRepository {
    private val service = BccrClient.instance.create(TipoCambioService::class.java)

    suspend fun getIndicadoresEconomicosXML(): ListaTipoCambio? {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaHoy = dateFormat.format(Date())

        // Realizar ambas solicitudes
        val response317 = service.obtenerIndicadores(
            indicador = "317",
            fechaInicio = fechaHoy,
            fechaFinal = fechaHoy,
            nombre = "ANGD",
            subNiveles = "N",
            correoElectronico = "gobernanzadigital@micitt.go.cr",
            token = "M1EBM212OO"
        )

        val response318 = service.obtenerIndicadores(
            indicador = "318",
            fechaInicio = fechaHoy,
            fechaFinal = fechaHoy,
            nombre = "ANGD",
            subNiveles = "N",
            correoElectronico = "gobernanzadigital@micitt.go.cr",
            token = "M1EBM212OO"
        )

        if (response317.isSuccessful && response318.isSuccessful) {

            val datos317 = response317.body()?.string()?.let { rawXml ->
                    val xmlDecoded = decodeXml(rawXml)
                    parseXml(xmlDecoded)
            }

            val datos318 = response318.body()?.string()?.let { rawXml ->
                val xmlDecoded = decodeXml(rawXml)
                parseXml(xmlDecoded)
            }

            // Combinar los resultados en una lista
            return combinarDatos(datos317, datos318)

        } else {
            return null
        }
    }

    private fun decodeXml(encodedXml: String): String {
        return HtmlCompat.fromHtml(encodedXml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun parseXml(xmlString: String): DatosIndicadorEconomico? {
        val serializer = Persister()
        return try {
            serializer.read(DatosIndicadorEconomico::class.java, xmlString)
        } catch (e: Exception) {
            var x = e.message
            null
        }
    }

    private fun combinarDatos(datos317: DatosIndicadorEconomico?, datos318: DatosIndicadorEconomico?): ListaTipoCambio? {
        // Si ambos son nulos, retorna nulo
        if (datos317 == null && datos318 == null) return null

        // Combinar ambos indicadores en una lista
        val listaIndicadores = mutableListOf<IndicadorEconomico>()
        datos317?.indicadorEconomico?.let { listaIndicadores.add(it) }
        datos318?.indicadorEconomico?.let { listaIndicadores.add(it) }

        return ListaTipoCambio(
            listado = listaIndicadores
        )
    }
}