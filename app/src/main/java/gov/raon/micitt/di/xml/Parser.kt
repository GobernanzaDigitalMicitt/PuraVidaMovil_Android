package gov.raon.micitt.di.xml

import com.google.gson.Gson
import gov.raon.micitt.ui.certificate.model.ChildItem
import gov.raon.micitt.ui.certificate.model.ParentItem
import gov.raon.micitt.ui.certificate.model.infoData
import gov.raon.micitt.ui.certificate.model.toXmlData
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

class Parser {
    private lateinit var infoDataObj: infoData
    private lateinit var document: Document
    private val editedList = mutableListOf<Pair<String, String>>()

    fun parse(xmlContent: String) {
        val removeEscape = removeEscapeChars(xmlContent)
        val strXml = getXml(removeEscape)
        val editTag = editTag(strXml)

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        val inputStream = String(editTag.toByteArray(Charsets.UTF_8)).byteInputStream()

        document = builder.parse(inputStream)
    }

    fun getTable(document: Document, table: String): MutableList<Map<String, String>> {
        document.documentElement.normalize()
        val tableElements = document.getElementsByTagName(table)
        val tables = mutableListOf<Map<String, String>>()

        for (i in 0 until tableElements.length) {
            val element = tableElements.item(i) as Element
            val tableData = mutableMapOf<String, String>()

            val children = element.childNodes
            for (j in 0 until children.length) {
                val child = children.item(j)
                if (child is Element) {
                    tableData[child.tagName] = child.textContent
                }
            }

            tables.add(tableData)
        }

        return tables
    }

    fun getEdited(): List<Pair<String, String>> {
        return editedList
    }

    private fun getDocument(): Document {
        return this.document
    }

    private fun editTag(input: String): String {
        val tagRegex = """<(/?)(\w+)>""".toRegex()
        val tagNameMap = mutableMapOf<String, String>()

        val matches = tagRegex.findAll(input)
        for (match in matches) {
            val tagName = match.groupValues[2]

            if (tagName.any { char -> accentMap.containsKey(char) }) {
                val newTagName = removeAccent(tagName)
                if (!tagNameMap.containsKey(tagName)) {
                    tagNameMap[tagName] = newTagName
                    editedList.add(Pair(tagName, newTagName))
                }
            }
        }

        var result = input
        for ((oldTagName, newTagName) in tagNameMap) {
            result = result.replace("<$oldTagName>", "<$newTagName>")
            result = result.replace("</$oldTagName>", "</$newTagName>")
        }

        return result

    }

    fun getElements(): List<ParentItem> {
        val result = mutableListOf<ParentItem>()

        val targetTable = "Table"
        for (i in 0..7) {
            if (i == 3) {
                continue
            }
            val table: MutableList<Map<String, String>> = if (i == 0) {
                getTable(getDocument(), targetTable)
            } else {
                getTable(getDocument(), targetTable.plus(i))
            }

            /* Table 1 ~ 7*/
            if (table.isNotEmpty()) {
                for (j in 1..table.size) {
                    val elements = table[j - 1]

                    val cItem = elements.map { (key, value) ->
                        ChildItem(key, value)
                    }.toMutableList()
                    val pItem = ParentItem(i, cItem)
                    result.add(0, pItem)
                }
            } else {
                result.add(ParentItem(i,null))
            }
        }

        result.sortBy { it.tableNum }

        /* Table 8 Information */
        val infoMap = infoDataToMap(infoDataObj)
        val cItem = infoMap.map{ (key, value) ->
            ChildItem(key, value)
        }.toMutableList()
        val pItem = ParentItem(8,cItem)
        result.add(0,pItem)

        return result
    }


    /*        스페인어 발음기호          */
    private val accentChars = listOf('á', 'é', 'í', 'ó', 'ú', 'ñ', 'ü')
    private val accentMap = mapOf(
        'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o',
        'ú' to 'u', 'ñ' to 'n', 'ü' to 'u'
    )

    fun removeAccent(tagName: String): String {
        val strEdited = tagName.map { char -> accentMap.getOrDefault(char, char) }.joinToString("")
        return removeEscapeChars(strEdited)
    }

    fun removeEscapeChars(input: String): String {
        val escapePattern = Pattern.compile("\\\\.")

        val matcher = escapePattern.matcher(input)
        return matcher.replaceAll("")
    }

    fun getXml(str: String): String {
        val gson = Gson()
        val result = gson.fromJson(str, toXmlData::class.java)
        val infoData = gson.fromJson(str,infoData::class.java)
        setInfoData(infoData)

        return result.strXml
    }

    private fun setInfoData(infoData: infoData) {
        this.infoDataObj = infoData
    }

    private fun infoDataToMap(data: infoData): Map<String, String> {
        return buildMap {
            put("strCondicion", data.strCondicion.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strEsMoroso", data.strEsMoroso.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strEsOmiso", data.strEsOmiso.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strFechaActualizacion", data.strFechaActualizacion.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strFechaDesinscripcion", data.strFechaDesinscripcion.takeIf { it?.isNotEmpty() == true} ?: "-")
            put("strFechaInscripcion", data.strFechaInscripcion.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strIdentificacion", data.strIdentificacion.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("nroInternoID", data.nroInternoID.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strAdministracion", data.strAdministracion.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strEstadoTributario", data.strEstadoTributario.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strNombreComercial", data.strNombreComercial.takeIf { it?.isNotEmpty() == true } ?: "-")
            put("strRazonSocial", data.strRazonSocial.takeIf { it?.isNotEmpty() == true } ?: "-")
        }
    }


}


