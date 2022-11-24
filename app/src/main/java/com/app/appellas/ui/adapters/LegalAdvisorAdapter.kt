package com.app.appellas.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.appellas.R
import com.app.appellas.data.models.dtos.response.CreateAdvisorResponse
import com.app.appellas.ui.views.fragments.admin.AdminAdvisoryFragmentDirections

class LegalAdvisorAdapter:
    RecyclerView.Adapter<LegalAdvisorAdapter.LegalAdvisorViewHolder>() {

    private var legalList: List<CreateAdvisorResponse>? = null

    private var rol: String = ""

    inner class LegalAdvisorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegalAdvisorViewHolder {
        val card = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_advisory, parent, false)
        return LegalAdvisorViewHolder(card)
    }

    override fun onBindViewHolder(holder: LegalAdvisorViewHolder, position: Int) {
        val medical = legalList?.get(position)
        holder.itemView.findViewById<TextView>(R.id.item_advisory_name).text = medical?.nombre

        val call = holder.itemView.findViewById<ImageButton>(R.id.item_advisory_action_call)
        val message = holder.itemView.findViewById<ImageButton>(R.id.item_advisory_action_message)
        val trash = holder.itemView.findViewById<ImageButton>(R.id.item_advisory_action_delete)

        when(rol) {
            "usuario" -> {
                call.visibility = View.VISIBLE
                message.visibility = View.VISIBLE
                trash.visibility = View.GONE
                call.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel: ${medical?.telefono}")
                    holder.itemView.context.startActivity(intent)
                }
                message.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.data = Uri.parse("smsto: ${medical?.telefono}")
                    holder.itemView.context.startActivity(intent)
                }
            }
            "admin" -> {
                call.visibility = View.GONE
                message.visibility = View.GONE
                trash.visibility = View.VISIBLE
                trash.setOnClickListener {
                    val direction = AdminAdvisoryFragmentDirections
                        .actionAdminAdvisoryFragmentToDialogDeleteAdvisor(medical!!.id_asesor.toLong())
                    holder.itemView.findNavController().navigate(direction)
                }
            }
        }
    }

    override fun getItemCount(): Int = legalList?.size ?: 0

    fun setData(list: List<CreateAdvisorResponse>) {
        legalList = list
        notifyDataSetChanged()
    }

    fun setRol(type: String) {
        rol = type
    }
}