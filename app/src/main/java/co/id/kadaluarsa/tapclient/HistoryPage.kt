package co.id.kadaluarsa.tapclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.id.kadaluarsa.tapdetector.model.ResultsItem


class HistoryPage : Fragment() {

    companion object {
        fun getInstance(bundle: Bundle): HistoryPage {
            val history = HistoryPage()
            history.arguments = bundle
            return history
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.history_page, container, false)
    }

    override fun onViewCreated(_view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(_view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData(){
        SampleApp.getAppContext().tap.getInstance().dumpedData { data ->
            requireActivity().runOnUiThread {
                val list = requireView().findViewById<RecyclerView>(R.id.list_item)
                list.layoutManager =
                    LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
                list.adapter = object : RecyclerView.Adapter<EventViewHolder>() {
                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): EventViewHolder {
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_event, parent, false)
                        return EventViewHolder(view)
                    }

                    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
                        holder.bindData(data.results[position])
                    }

                    override fun getItemCount(): Int = data.results.size
                }
            }
        }
    }

    inner class EventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(data: ResultsItem) {
            val text = view.findViewById<TextView>(R.id.text)
            text.text = data.toString()
        }
    }
}