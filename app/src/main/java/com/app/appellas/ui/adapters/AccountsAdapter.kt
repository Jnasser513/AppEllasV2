package com.app.appellas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.appellas.R
import com.app.appellas.data.models.dtos.response.UserResponse

class AccountsAdapter(private val deleteHandler: (Int) -> Unit): RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>() {

    private var userList: List<UserResponse>? = ArrayList()

    private var rol: String = ""

    inner class AccountsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)

        return AccountsViewHolder(card)
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val user = userList?.get(position)
        holder.itemView.findViewById<TextView>(R.id.item_account_name).text = user?.nombre
        holder.itemView.findViewById<Button>(R.id.item_account_action_delete).setOnClickListener {
            deleteHandler(user!!.id)
        }
    }

    override fun getItemCount() = userList?.size ?: 0

    fun setData(list: List<UserResponse>) {
        userList = list
        notifyDataSetChanged()
    }

}