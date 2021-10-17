package com.example.nutritionapp.ingredientlist


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientItemBinding

class networkIngredientAdapter : RecyclerView.Adapter<networkIngredientAdapter.ViewHolder>() {
    var listIngredients : List<IngredientDataClass> = emptyList()
        set(value) {
            println("set value called for network list adapter")
            field = value
            //not very efficient, should use Diff check
            notifyDataSetChanged()
        }
    /*
    Supplying the parent View lets the inflater know what layoutparams to use. Supplying the false parameter tells it to not
    attach it to the parent just yet. That is what the RecyclerView will do for you.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : IngredientItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ViewHolder.LAYOUT,
            parent,
            false)

        Log.i("test","onCreateViewHolder called")

        /*  val inflater = LayoutInflater.from(parent.context)
        val view = IngredientItemBinding.inflate(inflater,parent,false)*/
        /*
           val withDataBinding: DevbyteItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                DevByteViewHolder.LAYOUT,
                parent,
                false)
        //correct
LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_listitem, parent, false);
         */
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
/*        holder.binding.also {
            it.ingredientItem = listIngredients[position]
        }*/
        Log.i("test","onBindViewHolder called")
        val ingredientItem = listIngredients[position]
        holder.bind(ingredientItem)
    }

    override fun getItemCount() = listIngredients.size


    //implement data binding to avoid using findViewById()
    class ViewHolder(val binding : IngredientItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item : IngredientDataClass)
        {
            binding.ingredientItem = item
        }
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.ingredient_item
        }
        //val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
    }
}