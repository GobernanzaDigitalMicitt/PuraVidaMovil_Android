package gov.raon.micitt.models

data class CECIDataModel(
    val apellido: String,
    val cursos: List<Curso>,
    val nid: String,
    val nombre: String
)

data class Curso(
    val codigo: String,
    val curso: String,
    val duracion: String,
    val encargado: String,
    val fechaFin: String?,        // null 허용
    val fechaInicio: String?,     // null 허용
    val identificacion: String,
    val instituto: String,
    val nota: Int,
    val profesor: String,
    val usuario: String,
    val detail: List<CourseDetail>?
)

data class CourseDetail(
    val line: String
)