package co.id.kadaluarsa.tapclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

class TouchPage : Fragment() {

    companion object {
        fun getInstance(bundle: Bundle): TouchPage {
            val touchArea = TouchPage()
            touchArea.arguments = bundle
            return touchArea
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.touch_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(SampleApp.getAppContext().tap.getInstance().touchListener())

    }
}