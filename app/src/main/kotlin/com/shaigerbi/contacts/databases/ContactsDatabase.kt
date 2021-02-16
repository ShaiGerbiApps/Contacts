package com.shaigerbi.contacts.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shaigerbi.contacts.helpers.Converters
import com.shaigerbi.contacts.helpers.FIRST_CONTACT_ID
import com.shaigerbi.contacts.helpers.FIRST_GROUP_ID
import com.shaigerbi.contacts.helpers.getEmptyLocalContact
import com.shaigerbi.contacts.interfaces.ContactsDao
import com.shaigerbi.contacts.interfaces.GroupsDao
import com.shaigerbi.contacts.models.Group
import com.shaigerbi.contacts.models.LocalContact
import java.util.concurrent.Executors

@Database(entities = [LocalContact::class, Group::class], version = 2)
@TypeConverters(Converters::class)
abstract class ContactsDatabase : RoomDatabase() {

    abstract fun ContactsDao(): ContactsDao

    abstract fun GroupsDao(): GroupsDao

    companion object {
        private var db: ContactsDatabase? = null

        fun getInstance(context: Context): ContactsDatabase {
            if (db == null) {
                synchronized(ContactsDatabase::class) {
                    if (db == null) {
                        db = Room.databaseBuilder(context.applicationContext, ContactsDatabase::class.java, "local_contacts.db")
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    increaseAutoIncrementIds()
                                }
                            })
                            .addMigrations(MIGRATION_1_2)
                            .build()
                    }
                }
            }
            return db!!
        }

        fun destroyInstance() {
            db = null
        }

        // start autoincrement ID from FIRST_CONTACT_ID/FIRST_GROUP_ID to avoid conflicts
        // Room doesn't seem to have a built in way for it, so just create a contact/group and delete it
        private fun increaseAutoIncrementIds() {
            Executors.newSingleThreadExecutor().execute {
                val emptyContact = getEmptyLocalContact()
                emptyContact.id = FIRST_CONTACT_ID
                db!!.ContactsDao().apply {
                    insertOrUpdate(emptyContact)
                    deleteContactId(FIRST_CONTACT_ID)
                }

                val emptyGroup = Group(FIRST_GROUP_ID, "")
                db!!.GroupsDao().apply {
                    insertOrUpdate(emptyGroup)
                    deleteGroupId(FIRST_GROUP_ID)
                }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL("ALTER TABLE contacts ADD COLUMN photo_uri TEXT NOT NULL DEFAULT ''")
                }
            }
        }
    }
}
