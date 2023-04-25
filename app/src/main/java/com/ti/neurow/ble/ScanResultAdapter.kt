package com.ti.neurow.ble

import android.bluetooth.le.ScanResult
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ti.neurow.R
import kotlinx.android.synthetic.main.row_scan_result.view.device_name
import kotlinx.android.synthetic.main.row_scan_result.view.mac_address
import kotlinx.android.synthetic.main.row_scan_result.view.signal_strength
import org.jetbrains.anko.layoutInflater

/*
* Here is the custom class of ScanResultAdapter, an adapter for the recyclerview
* So in part this is an extension of that class
 */
class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.context.layoutInflater.inflate(
            R.layout.row_scan_result,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.ViewHolder(view) {

        fun bind(result: ScanResult) {
            view.device_name.text = result.device.name ?: "Unnamed"
            view.mac_address.text = result.device.address
            view.signal_strength.text = "${result.rssi} dBm"
            view.setOnClickListener { onClickListener.invoke(result) }
        }
    }
}


/*
This is a Kotlin code that defines a ScanResultAdapter class for a RecyclerView.

The ScanResultAdapter class takes two parameters in its constructor - a list of ScanResult objects
and an onClickListener lambda function that takes a ScanResult object as input and returns no output.


The ScanResultAdapter class extends the RecyclerView.Adapter class and overrides three methods:

onCreateViewHolder(): This method inflates the layout for a single row of the RecyclerView
and returns a new ViewHolder object that holds the inflated layout and the onClickListener function.

getItemCount(): This method returns the number of items in the list of ScanResult objects.

onBindViewHolder(): This method updates the view holder with
data from the ScanResult object at the given position.

The ViewHolder class is defined inside the ScanResultAdapter class and
takes two parameters - a View object and the onClickListener lambda function.
It extends the RecyclerView.ViewHolder class and overrides the bind() method to populate
the view with data from a ScanResult object.



Inside the bind() method, the view's device_name, mac_address, and signal_strength fields
are populated with data from the ScanResult object. The view object is also set to respond to
click events by setting an onClickListener using the setOnClickListener() method.
When the view is clicked, the onClick() method of the onClickListener
is called with the ScanResult object as an argument.

Overall, this code defines a ScanResultAdapter class for a RecyclerView
and sets up a listener to respond to click events on the view it contains.
The adapter is used to populate the RecyclerView with data from a list of ScanResult objects.
 */