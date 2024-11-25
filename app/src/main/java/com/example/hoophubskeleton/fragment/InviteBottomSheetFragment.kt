package com.example.hoophubskeleton.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hoophubskeleton.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast

class InviteBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var selectDateButton: Button
    private lateinit var selectTimeButton: Button
    private lateinit var locationEditText: EditText
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invite_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectDateButton = view.findViewById(R.id.selectDateButton)
        selectTimeButton = view.findViewById(R.id.selectTimeButton)
        locationEditText = view.findViewById(R.id.locationEditText)
        confirmButton = view.findViewById(R.id.inviteConfirmButton)
        cancelButton = view.findViewById(R.id.inviteCancelButton)

        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        selectTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        confirmButton.setOnClickListener {
            onConfirmClicked()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                selectDateButton.text = "${selectedMonth+1}/$selectedDay/$selectedMonth"
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), {_, selectedHour, selectedMinute ->
            selectTimeButton.text = String.format("%02d:%02d", selectedHour, selectedMinute)},
            hour, minute, true)

        timePickerDialog.show()
        }

    private fun onConfirmClicked() {
        val date = selectDateButton.text.toString()
        val time = selectTimeButton.text.toString()
        val location = locationEditText.text.toString()

        Toast.makeText(requireContext(),
            "Date: $date\nTime: $time\nLocation: $location", Toast.LENGTH_LONG).show()

        dismiss()
    }
}