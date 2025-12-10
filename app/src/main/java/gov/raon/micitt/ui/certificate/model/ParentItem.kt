package gov.raon.micitt.ui.certificate.model

data class ParentItem(
    val tableNum: Int,
    val elements: MutableList<ChildItem>?
)

data class ChildItem(
    val key : String?,
    val value : String?
)
