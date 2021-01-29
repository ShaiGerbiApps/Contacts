package com.shaigerbi.contacts.fragments

import android.content.Context
import android.util.AttributeSet
import com.simplemobiletools.commons.helpers.TAB_GROUPS
import com.shaigerbi.contacts.activities.MainActivity
import com.shaigerbi.contacts.activities.SimpleActivity
import com.shaigerbi.contacts.dialogs.CreateNewGroupDialog

class GroupsFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    override fun fabClicked() {
        finishActMode()
        showNewGroupsDialog()
    }

    override fun placeholderClicked() {
        showNewGroupsDialog()
    }

    private fun showNewGroupsDialog() {
        CreateNewGroupDialog(activity as SimpleActivity) {
            (activity as? MainActivity)?.refreshContacts(TAB_GROUPS)
        }
    }
}
