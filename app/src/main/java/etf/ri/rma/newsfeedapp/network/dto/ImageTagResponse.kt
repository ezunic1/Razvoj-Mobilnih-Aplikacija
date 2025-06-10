package etf.ri.rma.newsfeedapp.network.dto

data class ImageTagResponse(
    val result: TagResult // Cijeli odgovor sadrži rezultat
)

data class TagResult(
    val tags: List<TagEntry> // Lista tagova koji su pridruženi slici
)

data class TagEntry(
    val tag: Map<String, String>, // Mapirani tagovi po jeziku
    val confidence: Double // Pouzdanost predikcije
)
