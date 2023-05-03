package ru.inspirationpoint.scrollsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import ru.inspirationpoint.scrollsapp.QRProto.Companion.cmdField
import ru.inspirationpoint.scrollsapp.R
import ru.inspirationpoint.scrollsapp.ui.MainActivity

class ScanFragment : Fragment(R.layout.fragment_scan) {
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            Log.wtf("SCAN RESULT", it.text)
            if (it.text.startsWith(cmdField)) {
                (activity as MainActivity).scanResultGained(it.text)
            } else
            activity.runOnUiThread {
                Toast.makeText(activity, "неправильный код", Toast.LENGTH_LONG).show()
                codeScanner.startPreview()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}