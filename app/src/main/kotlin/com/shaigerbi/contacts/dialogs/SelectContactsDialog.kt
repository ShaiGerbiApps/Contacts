package com.shaigerbi.contacts.dialogs

import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.shaigerbi.contacts.R
import com.shaigerbi.contacts.activities.SimpleActivity
import com.shaigerbi.contacts.adapters.SelectContactsAdapter
import com.shaigerbi.contacts.extensions.getVisibleContactSources
import com.shaigerbi.contacts.models.Contact
import kotlinx.android.synthetic.main.layout_select_contact.view.*

class SelectContactsDialog(val activity: SimpleActivity, initialContacts: ArrayList<Contact>, val allowSelectMultiple: Boolean, val showOnlyContactsWithNumber: Boolean,
                           selectContacts: ArrayList<Contact>? = null, val callback: (addedContacts: ArrayList<Contact>, removedContacts: ArrayList<Contact>) -> Unit) {
    private var dialog: AlertDialog? = null
    private var view = activity.layoutInflater.inflate(R.layout.layout_select_contact, null)
    private var initiallySelectedContacts = ArrayList<Contact>()

    init {
        var allContacts = initialContacts
        if (selectContacts == null) {
            val contactSources = activity.getVisibleContactSources()
            allContacts = allContacts.filter { contactSources.contains(it.source) } as ArrayList<Contact>

            if (showOnlyContactsWithNumber) {
                allContacts = allContacts.filter { it.phoneNumbers.isNotEmpty() }.toMutableList() as ArrayList<Contact>
            }

            initiallySelectedContacts = allContacts.filter { it.starred == 1 } as ArrayList<Contact>
        } else {
            initiallySelectedContacts = selectContacts
        }

        activity.runOnUiThread {
            // if selecting multiple contacts is disabled, react on first contact click and dismiss the dialog
            val contactClickCallback: ((Contact) -> Unit)? = if (allowSelectMultiple) null else { contact ->
                callback(arrayListOf(contact), arrayListOf())
                dialog!!.dismiss()
            }

            view.apply {
                select_contact_list.adapter = SelectContactsAdapter(activity, allContacts, initiallySelectedContacts, allowSelectMultiple,
                    select_contact_list, select_contact_fastscroller, contactClickCallback)

                select_contact_fastscroller.setViews(select_contact_list) {
                    select_contact_fastscroller.updateBubbleText(allContacts[it].getBubbleText())
                }
            }

            val builder = AlertDialog.Builder(activity)
            if (allowSelectMultiple) {
                builder.setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            }
            builder.setNegativeButton(R.string.cancel, null)

            dialog = builder.create().apply {
                activity.setupDialogStuff(view, this)
            }
        }
    }

    private fun dialogConfirmed() {
        ensureBackgroundThread {
            val adapter = view?.select_contact_list?.adapter as? SelectContactsAdapter
            val selectedContacts = adapter?.getSelectedItemsSet()?.toList() ?: ArrayList()

            val newlySelectedContacts = selectedContacts.filter { !initiallySelectedContacts.contains(it) } as ArrayList
            val unselectedContacts = initiallySelectedContacts.filter { !selectedContacts.contains(it) } as ArrayList
            callback(newlySelectedContacts, unselectedContacts)
        }
    }
}
