package com.ipca.socialstore.data.enums

enum class DatabaseTables(val tableName: String) {
    USER("user"),
    CAMPAIGN("campaign"),
    DONATION("donation"),
    DONATION_ITEM("donation_Item"),
    ITEM("item"),
    STOCK("stock"),
    APPLICATION("application"),
    APPLICATION_DOCUMENT_TYPE("application_document_type"),
    APPLICATION_DOCUMENT("application_document"),
    APPLICATION_STATE("application_state"),
    APPLICATION_DATA_STATE("application_data_state"),
    ACADEMIC("academic"),
    DOCUMENT("document"),
    DOCUMENT_STATE("document_state"),
    SCHEDULING ("scheduling"),
    BENEFICIARY("beneficiary"),
    SCHEDULING_DATE("scheduling_date"),
    SCHEDULING_NOTIFICATION("notification_scheduling"),
    NOTIFICATION_SCHEDULED("notification_scheduled"),

    DELIVERY("deliveries"),

    DELIVERY_ITEMS("delivery_items");
	
    override fun toString() = tableName
}