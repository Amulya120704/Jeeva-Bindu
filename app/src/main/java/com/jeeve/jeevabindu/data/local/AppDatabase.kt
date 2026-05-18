package com.jeeve.jeevabindu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jeeve.jeevabindu.data.BloodGroup
import java.time.LocalDate

@Database(
    entities = [DonorEntity::class, EmergencyPostEntity::class, AlertResponseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun emergencyPostDao(): EmergencyPostDao
    abstract fun alertResponseDao(): AlertResponseDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context.applicationContext).also { instance = it }
            }

        private fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "jeeva_bindu.db")
                .addCallback(SeedCallback())
                .build()
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Room runs onCreate before instance is set; seed via direct SQL for demo donors
            val donors = listOf(
                Triple("Ravi Kumar", BloodGroup.O_POS.label, "Hassan Panchayat"),
                Triple("Lakshmi Devi", BloodGroup.A_NEG.label, "Arsikere Town"),
                Triple("Manjunath G", BloodGroup.B_POS.label, "Channarayapatna"),
                Triple("Priya S", BloodGroup.AB_NEG.label, "Hassan Panchayat"),
                Triple("Suresh B", BloodGroup.O_NEG.label, "Arsikere Town")
            )
            donors.forEachIndexed { index, (name, group, location) ->
                db.execSQL(
                    """
                    INSERT INTO donors (name, phone, bloodGroup, age, location, lastDonationEpochDay, isCurrentUser)
                    VALUES (?, ?, ?, ?, ?, ?, 0)
                    """.trimIndent(),
                    arrayOf(
                        name,
                        "900000000${index + 1}",
                        group,
                        28 + index,
                        location,
                        if (index == 2) LocalDate.now().minusDays(30).toEpochDay() else null
                    )
                )
            }
        }
    }
}
