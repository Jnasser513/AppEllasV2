package com.app.appellas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.appellas.R
import com.app.appellas.data.models.dtos.response.ContactResponse
import com.app.appellas.data.models.dtos.response.UserResponse
import com.app.appellas.ui.views.fragments.user.ContactsFragmentDirections

class ContactAdapter: RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactList: List<ContactResponse>? = ArrayList()

    inner class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        return ContactViewHolder(card)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = contactList?.get(position)
        holder.itemView.findViewById<TextView>(R.id.item_contact_name).text = user?.nombre
        holder.itemView.findViewById<TextView>(R.id.item_contact_phone).text = user?.telefono.toString()
        holder.itemView.findViewById<ImageButton>(R.id.item_contact_action_delete).setOnClickListener {
            val direction = ContactsFragmentDirections
                .actionContactsFragmentToDialogDeleteContact(user?.id_contacto!!)
            holder.itemView.findNavController().navigate(direction)
        }
    }

    override fun getItemCount() = contactList?.size ?: 0

    fun setData(list: List<ContactResponse>) {
        contactList = list
        notifyDataSetChanged()
    }

}