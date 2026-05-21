package uns.sakku.feature.pocket.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allocations")
data class AllocationEntity(
    @PrimaryKey
    val id: String, // String UUID sangat bagus untuk sinkronisasi cloud
    val nama: String,
    val nominal: Double,
    val isTabungan: Boolean
)