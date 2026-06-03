package uns.sakku.feature.pocket.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allocations")
data class AllocationEntity(
    @PrimaryKey
    val id: String, // String UUID
    val nama: String,
    val targetNominal: Double, //Batas kantong atau target Tabungan
    val terkumpulNominal: Double, // Nominal Tabungan atau Kantong
    val isTabungan: Boolean
)