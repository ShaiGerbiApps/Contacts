package com.shaigerbi.contacts.interfaces

import com.shaigerbi.contacts.models.Contact

interface RemoveFromGroupListener {
    fun removeFromGroup(contacts: ArrayList<Contact>)
}
