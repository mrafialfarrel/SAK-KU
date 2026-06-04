package uns.sakku.feature.transaction.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uns.sakku.core.data.remote.BaseResponse

/**
 * Interface Retrofit untuk mendefinisikan endpoint API.
 * Di Java, ini mirip dengan interface biasa, tapi Retrofit akan membuatkan
 * implementasi "proxy"-nya secara otomatis di belakang layar.
 */
interface TransactionApiService {

    // Mengambil daftar transaksi (Contoh method GET)
    @GET("transactions")
    suspend fun getAllTransactions(): BaseResponse<List<TransactionDto>>

    // Menambah transaksi baru (Contoh method POST)
    @POST("transactions")
    suspend fun createTransaction(@Body transaction: TransactionDto): BaseResponse<TransactionDto>

    // Mengubah transaksi (Contoh method PUT, mengirim ID di URL)
    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body transaction: TransactionDto
    ): BaseResponse<TransactionDto>

    // Menghapus transaksi
    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): BaseResponse<Any>
}