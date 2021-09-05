package co.id.kadaluarsa.tapclient

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(activity: FragmentManager,life : Lifecycle) :
    FragmentStateAdapter(activity,life) {
    private val list = listOf(
        TouchPage.getInstance(bundleOf()),
        HistoryPage.getInstance(bundleOf())
    )

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}