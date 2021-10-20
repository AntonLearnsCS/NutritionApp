package com.example.nutritionapp.ingredientlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.database.IngredientDataClass
import com.example.nutritionapp.databinding.IngredientItemLocalBinding


class localIngredientAdapter(val onClickListener: LocalIngredientListener) : ListAdapter<IngredientDataClass, localIngredientAdapter.ViewHolder>(
    LocalIngredienttDiffCallback()
)
{

    constructor(
        selectedIngredientList : ArrayList<IngredientDataClass>,
         OnItemCheckListenerVar : localIngredientAdapter.OnItemCheckListener) : this()
    //Callback method to be invoked when an item in this AdapterView has been clicked.
/*@Override
override fun onItemClick()
{

}*/


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : IngredientItemLocalBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ViewHolder.LAYOUT,
            parent,
            false
        )
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
        return ViewHolder(view, onClickListener)
    }
    interface OnItemCheckListener {
        fun onItemCheck(item: IngredientDataClass)
        fun onItemUncheck(item: IngredientDataClass)
    }

    @NonNull
    private val onItemClick: OnItemCheckListener? = null

    override fun onBindViewHolder(holder: localIngredientAdapter.ViewHolder, position: Int) {
            val ingredientItem = getItem(position)
            holder.bind(ingredientItem)
        holder.setOnClickListener{
            (holder.binding).checkbox.setChecked(
                !(holder.binding).checkbox.isChecked()
            )
            if ((holder.binding).checkbox.isChecked()) {
                onItemClick!!.onItemCheck(ingredientItem)
            } else {
                onItemClick!!.onItemUncheck(ingredientItem)
            }
        }
    }


    //implement data binding to avoid using findViewById()
    class ViewHolder(
        val binding: IngredientItemLocalBinding,
        val clickListener: LocalIngredientListener
    ) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind(item: IngredientDataClass)
        {
            binding.clickListenerLocal = clickListener
            binding.ingredientItem = item
        }
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.ingredient_item_local
        }
        //val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
         fun setOnClickListener(onClickListener: View.OnClickListener?) {
            itemView.setOnClickListener(onClickListener)
        }
    }
    class LocalIngredienttDiffCallback :
        DiffUtil.ItemCallback<IngredientDataClass>() {
        override fun areItemsTheSame(
            oldItem: IngredientDataClass,
            newItem: IngredientDataClass
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: IngredientDataClass,
            newItem: IngredientDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }
    //Note: There are multiple ways to implement onClick Listener for recyclerViews, this is just one of them
    class LocalIngredientListener(val clickListener: (ingredientItem: IngredientDataClass) -> Unit) {
        //it's a bit strange b/c usually we use the parameter inside the class but here we are using the body of the class
        //to define the value of the parameter
        fun onClick(ingredient: IngredientDataClass) = clickListener(ingredient)
    }
}