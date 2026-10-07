package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LeadStatus(val label: String, val emoji: String) {
    HOT("Hot", "🔥"),
    WARM("Warm", "☀️"),
    COLD("Cold", "❄️"),
    QUALIFIED("Qualified", "🎯"),
    CONVERTED("Converted", "🏆"),
    LOST("Lost", "❌");

    companion object {
        fun fromString(value: String): LeadStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: WARM
        }
    }
}

enum class LeadSource(val displayName: String) {
    TRADE_SHOW("Trade Show / Expo"),
    KIOSK("Event Kiosk"),
    WALK_IN("Walk-in / In-Store"),
    REFERRAL("Client Referral"),
    CONFERENCE("Tech Conference"),
    WEB_INQUIRY("Web Inquiry"),
    OTHER("Other")
}

enum class ContactMethod(val displayName: String) {
    EMAIL("Email"),
    PHONE("Phone Call"),
    WHATSAPP("WhatsApp / SMS"),
    IN_PERSON("In Person")
}

enum class FollowUpTimeline(val displayName: String) {
    IMMEDIATE("Immediate (Today)"),
    WITHIN_24H("Within 24 Hours"),
    WITHIN_WEEK("Within 7 Days"),
    NEXT_MONTH("Next Month")
}

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val company: String = "",
    val jobTitle: String = "",
    val email: String,
    val phone: String = "",
    val status: String = LeadStatus.WARM.name,
    val dealValue: Double = 0.0,
    val interestCategory: String = "Enterprise Solutions",
    val source: String = LeadSource.TRADE_SHOW.name,
    val preferredContactMethod: String = ContactMethod.EMAIL.name,
    val followUpTimeline: String = FollowUpTimeline.WITHIN_24H.name,
    val notes: String = "",
    val tags: String = "", // Comma-separated: "Decision Maker, Budget Approved"
    val capturedBy: String = "Agent Intake",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lead_activities")
data class LeadActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val leadId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // "CREATED", "NOTE", "STATUS_CHANGE", "CALL", "EMAIL"
    val description: String
)
