package uns.sakku.feature.allocation.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AllocationDao {
    // Return Flow agar otomatis ter-emit jika ada perubahan data di tabel
    @Query("SELECT * FROM allocations")
    fun getAllAllocations(): Flow<List<AllocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: AllocationEntity): Long

    @Update
    suspend fun updateAllocation(allocation: AllocationEntity): Int

    @Delete
    suspend fun deleteAllocation(allocation: AllocationEntity): Int
    @Query("DELETE FROM allocations")
    suspend fun deleteAllAllocations()
}
