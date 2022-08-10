package com.ozlem.recipebookwithsqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
// recycler_row xml'ine ulaşabilmek için:
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListRecyclerAdapter (val foodList : ArrayList<String> , val idList : ArrayList<Int>) : RecyclerView.Adapter <ListRecyclerAdapter.foodViewHolder> () {
    class foodViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    // Hangi row'u hangi tasarımla oluşturacağımızı söyleyen fonksiyon:
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): foodViewHolder {
        // from içinde context'i verdik:
        val inflater = LayoutInflater.from(parent.context)
        // Görünümümüzü oluşturalım:
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return foodViewHolder(view)
    }

    override fun onBindViewHolder(holder: foodViewHolder, position: Int) {
        holder.itemView.recycler_row_text_id.text = foodList[position]
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("I came from recycler",idList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    // Kaç tane recycler row oluşturacağımızı söyleyecek:
    override fun getItemCount(): Int {
        return foodList.size
    }
}