package com.shaigerbi.contacts.fragments

import android.content.Context
import android.util.AttributeSet
import com.simplemobiletools.commons.helpers.TAB_FAVORITES
import com.shaigerbi.contacts.activities.MainActivity
import com.shaigerbi.contacts.activities.SimpleActivity
import com.shaigerbi.contacts.dialogs.SelectContactsDialog
import com.shaigerbi.contacts.helpers.ContactsHelper

class FavoritesFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    override fun fabClicked() {
        finishActMode()
        showAddFavoritesDialog()
    }

    override fun placeholderClicked() {
        showAddFavoritesDialog()
    }

    private fun showAddFavoritesDialog() {
        SelectContactsDialog(activity!!, allContacts, true, false) { addedContacts, removedContacts ->
            ContactsHelper(activity as SimpleActivity).apply {
                addFavorites(addedContacts)
                removeFavorites(removedContacts)
            }

            (activity as? MainActivity)?.refreshContacts(TAB_FAVORITES)
        }
    }
}
