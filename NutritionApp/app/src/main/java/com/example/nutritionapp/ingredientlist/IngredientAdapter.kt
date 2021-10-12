package com.example.nutritionapp.ingredientlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientItemBinding

class IngredientAdapter : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {
    var listIngredients = listOf<IngredientDataClass>()
        set(value) {
            field = value
            //not very efficient, should use Diff check
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = IngredientItemBinding.inflate(inflater,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ingredientItem = listIngredients[position]
            holder.bind(ingredientItem)
    }

    override fun getItemCount(): Int {
        return listIngredients.size
    }

    //implement data binding to avoid using findViewById()
    class ViewHolder(val binding : IngredientItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val name = binding.ingredientName
        val quantity = binding.quantity

        fun bind(item : IngredientDataClass)
        {
            binding.ingredientItem = item
        }
        //val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
    }
}