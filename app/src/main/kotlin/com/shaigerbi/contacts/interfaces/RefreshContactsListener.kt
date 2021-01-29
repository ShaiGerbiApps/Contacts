package com.shaigerbi.contacts.interfaces

import com.shaigerbi.contacts.models.Contact

interface RefreshContactsListener {
    fun refreshContacts(refreshTabsMask: Int)

    fun contactClicked(contact: Contact)
}
