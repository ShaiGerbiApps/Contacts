package com.simplemobiletools.contacts.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simplemobiletools.commons.extensions.getIntValue
import com.simplemobiletools.commons.extensions.getStringValue
import com.simplemobiletools.contacts.models.Contact
import com.simplemobiletools.contacts.models.Email
import com.simplemobiletools.contacts.models.Event
import com.simplemobiletools.contacts.models.PhoneNumber


class DBHelper private constructor(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val CONTACTS_TABLE_NAME = "contacts"
    private val COL_ID = "id"
    private val COL_FIRST_NAME = "first_name"
    private val COL_MIDDLE_NAME = "middle_name"
    private val COL_SURNAME = "surname"
    private val COL_PHOTO = "photo"
    private val COL_PHONE_NUMBERS = "phone_numbers"
    private val COL_EMAILS = "emails"
    private val COL_EVENTS = "events"
    private val COL_STARRED = "starred"

    private val mDb: SQLiteDatabase = writableDatabase

    companion object {
        private const val DB_VERSION = 1
        const val DB_NAME = "contacts.db"
        var dbInstance: DBHelper? = null

        fun newInstance(context: Context): DBHelper {
            if (dbInstance == null)
                dbInstance = DBHelper(context)

            return dbInstance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $CONTACTS_TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_FIRST_NAME TEXT, $COL_MIDDLE_NAME TEXT, " +
                "$COL_SURNAME TEXT, $COL_PHOTO BLOB, $COL_PHONE_NUMBERS TEXT, $COL_EMAILS TEXT, $COL_EVENTS TEXT, $COL_STARRED INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun insert(contact: Contact): Boolean {
        val contactValues = fillContactValues(contact)
        val id = mDb.insert(CONTACTS_TABLE_NAME, null, contactValues).toInt()
        return id != -1
    }

    private fun fillContactValues(contact: Contact): ContentValues {
        return ContentValues().apply {
            put(COL_FIRST_NAME, contact.firstName)
            put(COL_MIDDLE_NAME, contact.middleName)
            put(COL_SURNAME, contact.surname)
            put(COL_PHONE_NUMBERS, Gson().toJson(contact.phoneNumbers))
            put(COL_EMAILS, Gson().toJson(contact.emails))
            put(COL_EVENTS, Gson().toJson(contact.events))
            put(COL_STARRED, contact.starred)
        }
    }

    fun getContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>()
        val projection = arrayOf(COL_ID, COL_FIRST_NAME, COL_MIDDLE_NAME, COL_SURNAME, COL_PHONE_NUMBERS, COL_EMAILS, COL_EVENTS, COL_STARRED)
        val cursor = mDb.query(CONTACTS_TABLE_NAME, projection, null, null, null, null, null)
        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getIntValue(COL_ID)
                val firstName = cursor.getStringValue(COL_FIRST_NAME)
                val middleName = cursor.getStringValue(COL_MIDDLE_NAME)
                val surname = cursor.getStringValue(COL_SURNAME)

                val phoneNumbersJson = cursor.getStringValue(COL_PHONE_NUMBERS)
                val phoneNumbersToken = object : TypeToken<List<PhoneNumber>>() {}.type
                val phoneNumbers = Gson().fromJson<ArrayList<PhoneNumber>>(phoneNumbersJson, phoneNumbersToken) ?: ArrayList(1)

                val emailsJson = cursor.getStringValue(COL_EMAILS)
                val emailsToken = object : TypeToken<List<Email>>() {}.type
                val emails = Gson().fromJson<ArrayList<Email>>(emailsJson, emailsToken) ?: ArrayList(1)

                val eventsJson = cursor.getStringValue(COL_EVENTS)
                val eventsToken = object : TypeToken<List<Event>>() {}.type
                val events = Gson().fromJson<ArrayList<Event>>(eventsJson, eventsToken) ?: ArrayList(1)

                val starred = cursor.getIntValue(COL_STARRED)
                val contact = Contact(id, firstName, middleName, surname, "", phoneNumbers, emails, events, SMT_PRIVATE, starred, id, "")
                contacts.add(contact)
            }
        }
        return contacts
    }
}
