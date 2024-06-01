package com.gagak.farmshields.core.utils.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import com.gagak.farmshields.R
import com.google.android.material.bottomnavigation.BottomNavigationView

object CustomBottomNavigationViewHelper {
    fun setupBottomNavigationView(
        context: Context,
        bottomNavigationView: BottomNavigationView,
        navController: NavController
    ) {
        val menuView = bottomNavigationView.getChildAt(0) as ViewGroup
        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i)
            val customView: View = LayoutInflater.from(context).inflate(R.layout.bottom_nav_item, menuView, false)
            val icon: ImageView = customView.findViewById(R.id.icon)
            val label: TextView = customView.findViewById(R.id.label)

            // Set the icon and label from the menu item
            val menuItem = bottomNavigationView.menu.getItem(i)
            icon.setImageDrawable(menuItem.icon)
            label.text = menuItem.title

            // Remove the old view
            (itemView as ViewGroup).removeAllViews()

            // Add the custom view
            itemView.addView(customView)

            // Handle click events to show/hide the label
            itemView.setOnClickListener {
                updateSelectedItem(bottomNavigationView, menuItem.itemId)
                navController.navigate(menuItem.itemId)
            }
        }

        // Set initial selection
        menuView.getChildAt(0).performClick()
    }

    fun updateSelectedItem(bottomNavigationView: BottomNavigationView, itemId: Int) {
        val menuView = bottomNavigationView.getChildAt(0) as ViewGroup
        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i) as ViewGroup
            val label = itemView.findViewById<TextView>(R.id.label)
            val background = itemView.findViewById<View>(R.id.item_background)
            val menuItem = bottomNavigationView.menu.getItem(i)

            if (menuItem.itemId == itemId) {
                label.visibility = View.VISIBLE
                background.setBackgroundResource(R.drawable.shape_bottom_nav_item_background)
            } else {
                label.visibility = View.GONE
                background.setBackgroundResource(android.R.color.transparent)
            }
        }
    }
}
