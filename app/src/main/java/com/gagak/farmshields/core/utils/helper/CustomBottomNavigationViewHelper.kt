package com.gagak.farmshields.core.utils.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.MenuRes
import com.gagak.farmshields.R
import com.google.android.material.bottomnavigation.BottomNavigationView

object CustomBottomNavigationViewHelper {
    fun setupBottomNavigationView(context: Context, bottomNavigationView: BottomNavigationView) {
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
                for (j in 0 until menuView.childCount) {
                    val otherItemView = menuView.getChildAt(j) as ViewGroup
                    val otherLabel = otherItemView.findViewById<TextView>(R.id.label)
                    val otherBackground = otherItemView.findViewById<View>(R.id.item_background)
                    otherLabel.visibility = if (i == j) View.VISIBLE else View.GONE
                    otherBackground.setBackgroundResource(if (i == j) R.drawable.shape_bottom_nav_item_background else android.R.color.transparent)
                }
                bottomNavigationView.selectedItemId = menuItem.itemId
            }
        }
    }
}
