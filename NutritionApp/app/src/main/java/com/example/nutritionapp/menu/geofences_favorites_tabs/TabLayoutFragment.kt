package com.example.nutritionapp.menu.geofences_favorites_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nutritionapp.R
import com.example.nutritionapp.databinding.TabLayoutFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

//note: Cannot create a fragment with a constructer parameter, otherwise will receive error:
// could not find Fragment constructor
class TabLayoutFragment() : Fragment() {
    private lateinit var listFragments: List<Fragment>
    private lateinit var binding: TabLayoutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tab_layout_fragment, container, false)
        listFragments = listOf(ListOfActiveGeofence(), ListOfActiveGeofence())

        val adapter = PageAdapter(this)
        binding.viewPager2Layout.adapter = adapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager2Layout) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Active Location Reminders"
                    tab.setIcon(R.drawable.ic_baseline_location_on_24)
                }
                1 -> {
                    tab.text = "Favorite Recipes"
                    tab.setIcon(R.drawable.ic_baseline_bookmark_24)
                }
            }
        }.attach()
        return binding.root
    }


    //FragmentStateAdapter is for the ViewPager2
    inner class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return listFragments.size
        }

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> ListOfActiveGeofence()
            else -> FavoriteRecipes()
        }

    }
    /*inner class pageAdapter : RecyclerView.Adapter<pageAdapter.PageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        inner class PageViewHolder(val fragment: Fragment) :
            RecyclerView.ViewHolder(fragment.requireView().rootView) {

        }
    }*/
}