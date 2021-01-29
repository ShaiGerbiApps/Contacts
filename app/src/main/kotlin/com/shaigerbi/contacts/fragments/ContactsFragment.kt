package com.shaigerbi.contacts.fragments

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import com.shaigerbi.contacts.activities.EditContactActivity
import com.shaigerbi.contacts.activities.InsertOrEditContactActivity
import com.shaigerbi.contacts.activities.MainActivity

class ContactsFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    override fun fabClicked() {
        Intent(context, EditContactActivity::class.java).apply {
            context.startActivity(this)
        }
    }

    override fun placeholderClicked() {
        if (activity is MainActivity) {
            (activity as MainActivity).showFilterDialog()
        } else if (activity is InsertOrEditContactActivity) {
            (activity as InsertOrEditContactActivity).showFilterDialog()
        }
    }
}
