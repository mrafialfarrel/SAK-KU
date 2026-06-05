package uns.sakku.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uns.sakku.feature.notification.data.local.NotificationDao
import uns.sakku.feature.notification.data.local.NotificationEntity
import uns.sakku.feature.allocation.data.local.AllocationDao
import uns.sakku.feature.allocation.data.local.AllocationEntity
import uns.sakku.feature.transaction.data.local.TransactionDao
import uns.sakku.feature.transaction.data.local.TransactionEntity

@Database(
    entities = [AllocationEntity::class, TransactionEntity::class, NotificationEntity::class],
    version = 3,
    exportSchema = false
)
abstract class SakkuDatabase : RoomDatabase() {

    abstract fun allocationDao(): AllocationDao
    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: SakkuDatabase? = null

        fun getInstance(context: Context): SakkuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SakkuDatabase::class.java,
                    "sakku_database.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
