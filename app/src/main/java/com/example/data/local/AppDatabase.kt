package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ContactMethod
import com.example.data.model.FollowUpTimeline
import com.example.data.model.LeadActivityEntity
import com.example.data.model.LeadEntity
import com.example.data.model.LeadSource
import com.example.data.model.LeadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [LeadEntity::class, LeadActivityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leadDao(): LeadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leadcapture_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial sample leads for instant demonstration
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).leadDao().insertLeads(getSampleLeads())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getSampleLeads(): List<LeadEntity> {
            val now = System.currentTimeMillis()
            val hour = 3600_000L
            val day = 86400_000L

            return listOf(
                LeadEntity(
                    fullName = "Elena Rostova",
                    company = "Apex Cloud Technologies",
                    jobTitle = "Chief Technology Officer",
                    email = "elena.rostova@apexcloud.io",
                    phone = "+1 (415) 890-2341",
                    status = LeadStatus.HOT.name,
                    dealValue = 48500.0,
                    interestCategory = "Enterprise Solutions",
                    source = LeadSource.TRADE_SHOW.name,
                    preferredContactMethod = ContactMethod.EMAIL.name,
                    followUpTimeline = FollowUpTimeline.IMMEDIATE.name,
                    notes = "Very urgent migration request. Needs 100+ seats, enterprise SLA guarantee. Scheduled demo next Tuesday.",
                    tags = "Decision Maker, Enterprise, Urgent",
                    capturedBy = "Exhibition Booth #14",
                    createdAt = now - (2 * hour),
                    updatedAt = now - (2 * hour)
                ),
                LeadEntity(
                    fullName = "Marcus Vance",
                    company = "Vance Global Logistics",
                    jobTitle = "VP Operations & Fleet",
                    email = "m.vance@vancelogistics.com",
                    phone = "+1 (212) 555-0198",
                    status = LeadStatus.QUALIFIED.name,
                    dealValue = 32000.0,
                    interestCategory = "AI Automation",
                    source = LeadSource.CONFERENCE.name,
                    preferredContactMethod = ContactMethod.PHONE.name,
                    followUpTimeline = FollowUpTimeline.WITHIN_24H.name,
                    notes = "Looking to automate tracking dispatch alerts and customer intake workflows across 4 distribution hubs.",
                    tags = "Budget Approved, Logistics",
                    capturedBy = "Keynote Hall",
                    createdAt = now - (6 * hour),
                    updatedAt = now - (6 * hour)
                ),
                LeadEntity(
                    fullName = "Sophia Chen",
                    company = "HyperScale Retail Labs",
                    jobTitle = "Director of Digital Experience",
                    email = "sophia.chen@hyperscalelabs.co",
                    phone = "+1 (650) 443-8890",
                    status = LeadStatus.WARM.name,
                    dealValue = 18000.0,
                    interestCategory = "Mobile CRM",
                    source = LeadSource.KIOSK.name,
                    preferredContactMethod = ContactMethod.EMAIL.name,
                    followUpTimeline = FollowUpTimeline.WITHIN_WEEK.name,
                    notes = "Interested in integrating our lead intake API with their in-store retail tablet kiosks.",
                    tags = "Retail, Kiosk Tech",
                    capturedBy = "Self-Service Kiosk",
                    createdAt = now - (1 * day),
                    updatedAt = now - (1 * day)
                ),
                LeadEntity(
                    fullName = "Devon Wright",
                    company = "Wright & Co Consulting",
                    jobTitle = "Managing Partner",
                    email = "devon@wrightpartners.org",
                    phone = "+1 (312) 774-2100",
                    status = LeadStatus.CONVERTED.name,
                    dealValue = 25000.0,
                    interestCategory = "Custom Integration",
                    source = LeadSource.REFERRAL.name,
                    preferredContactMethod = ContactMethod.PHONE.name,
                    followUpTimeline = FollowUpTimeline.WITHIN_WEEK.name,
                    notes = "Contract signed yesterday for annual subscription. Onboarding kickoff call pending.",
                    tags = "VIP, Existing Partner",
                    capturedBy = "Partner Referral",
                    createdAt = now - (3 * day),
                    updatedAt = now - (3 * day)
                ),
                LeadEntity(
                    fullName = "Liam O'Connor",
                    company = "NextGen Robotics",
                    jobTitle = "Procurement Specialist",
                    email = "liam.oc@nextgenrobotics.dev",
                    phone = "+1 (206) 912-3490",
                    status = LeadStatus.COLD.name,
                    dealValue = 9500.0,
                    interestCategory = "Hardware & Sensors",
                    source = LeadSource.WALK_IN.name,
                    preferredContactMethod = ContactMethod.EMAIL.name,
                    followUpTimeline = FollowUpTimeline.NEXT_MONTH.name,
                    notes = "Gathering vendor pricing information for Q1 budget review. Send product brochure.",
                    tags = "Early Stage, Research",
                    capturedBy = "Floor Rep",
                    createdAt = now - (5 * day),
                    updatedAt = now - (5 * day)
                )
            )
        }
    }
}
