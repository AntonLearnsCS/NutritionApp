package com.example.nutritionapp.ingredientlist

import android.util.Log
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
    override fun onCurrentListChanged(
        previousList: MutableList<IngredientDataClass>,
        currentList: MutableList<IngredientDataClass>
    ) {
        Log.i("adapter","listchanged called")
        super.onCurrentListChanged(previousList, currentList)
    }

    //NOTE: MutableLiveData<MutableList<IngredientDataClass>>> does not work but mutableListOf() does b/c MutableList is an interface
    var mList = mutableListOf<IngredientDataClass>()
    var mListOfNames = mutableListOf<String>()

        fun getListName() : List<String>{
            for (i in mList)
            {
                mListOfNames.add(i.name)
            }
            return mListOfNames
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : IngredientItemLocalBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ViewHolder.LAYOUT,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: localIngredientAdapter.ViewHolder, position: Int) {
            val ingredientItem = getItem(position)
            holder.bind(ingredientItem)

        holder.binding.checkbox.setOnClickListener{

            if ((holder.binding).checkbox.isChecked()) {
                mList.add(ingredientItem)
            } else {
                Log.i("test","unchecked")
                mList.remove(ingredientItem)
                Log.i("test","mList size: ${mList.size}")
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