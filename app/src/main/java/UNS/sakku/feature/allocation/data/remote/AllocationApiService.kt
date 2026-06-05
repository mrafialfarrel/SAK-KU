package UNS.sakku.feature.allocation.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uns.sakku.core.data.remote.BaseResponse

interface AllocationApiService {

    @GET("allocations")
    suspend fun getAllAllocations(): BaseResponse<List<AllocationDto>>

    @POST("allocations")
    suspend fun createAllocation(@Body allocation: AllocationDto): BaseResponse<AllocationDto>

    @PUT("allocations/{id}")
    suspend fun updateAllocation(
        @Path("id") id: String,
        @Body allocation: AllocationDto
    ): BaseResponse<AllocationDto>

    @DELETE("allocations/{id}")
    suspend fun deleteAllocation(@Path("id") id: String): BaseResponse<Any>
}