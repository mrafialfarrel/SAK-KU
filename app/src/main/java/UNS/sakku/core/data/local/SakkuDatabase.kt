package uns.sakku.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uns.sakku.feature.pocket.data.local.AllocationDao
import uns.sakku.feature.pocket.data.local.AllocationEntity
import uns.sakku.feature.transaction.data.local.TransactionDao
import uns.sakku.feature.transaction.data.local.TransactionEntity

@Database(
    entities = [AllocationEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SakkuDatabase : RoomDatabase() {

    abstract fun allocationDao(): AllocationDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: SakkuDatabase? = null

        fun getInstance(context: Context): SakkuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SakkuDatabase::class.java,
                    "sakku_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
