package uns.sakku.feature.allocation.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) untuk memetakan JSON Alokasi (Pocket/Saving) dari backend.
 */
data class AllocationDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("target_nominal")
    val targetNominal: Double,

    @SerializedName("is_tabungan")
    val isTabungan: Boolean
)