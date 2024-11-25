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
import androidx.fragment.app.viewModels
import com.example.hoophubskeleton.ViewModel.GameViewModel
import com.example.hoophubskeleton.model.Game
import com.example.hoophubskeleton.repository.GamesRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


class InviteBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var selectDateButton: Button
    private lateinit var selectTimeButton: Button
    private lateinit var locationEditText: EditText
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var currentUserId: String
    private lateinit var invitedUserId: String


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

        val currentId = arguments?.getString("currentUserId")
        val invitedId = arguments?.getString("invitedUserId")

        // Ensure we have currentUserId and invitedUser
        if (currentId == null || invitedId == null) {
            Toast.makeText(requireContext(), "Error: Missing user data.", Toast.LENGTH_SHORT).show()
            dismiss() // Close the fragment since this is useless without IDs
            return
        }

        currentUserId = currentId
        invitedUserId = invitedId

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

        // Validate inputs
        if (date.isBlank() || time.isBlank() || location.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val gameDateTime = convertDateTimeToTimestamp(date, time)
            val geoPoint = convertLocationToGeoPoint(location)

            val game = Game(
                createdBy = currentUserId,
                sentTo = invitedUserId,
                gameDateTime = gameDateTime,
                location = geoPoint
            )

            gameViewModel.createInvite(game) { success ->
                context?.let { safeContext ->
                    if (success) {
                        Toast.makeText(safeContext, "Invite sent.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(safeContext, "Failed to send invite.", Toast.LENGTH_SHORT).show()
                    }
                }
                dismiss()
            }

        } catch (e: IllegalArgumentException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    // This lets us call newInstance on the class itself rather than on an instance of the class.
    // It wouldn't make sense to first create an instance of the class and then call a method on that
    // instance to create a new instance.
    companion object {
        fun newInstance(currentUserId: String, invitedUserId: String): InviteBottomSheetFragment {
            val fragment = InviteBottomSheetFragment()
            val args = Bundle()
            args.putString("currentUserId", currentUserId)
            args.putString("invitedUserId", invitedUserId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun convertDateTimeToTimestamp(date: String, time: String): Timestamp {
        val dateTimeString = "$date $time"
        val dateTimeFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        return try {
            val dateTime = dateTimeFormat.parse(dateTimeString)
            Timestamp(dateTime!!)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid date/time format")
        }
    }


    private fun convertLocationToGeoPoint(location: String): GeoPoint {
        val latLng = location.split(",")
        if (latLng.size != 2) throw IllegalArgumentException("Invalid location format")

        val latitude = latLng[0].toDoubleOrNull()
        val longitude = latLng[1].toDoubleOrNull()
        if (latitude == null || longitude == null) throw IllegalArgumentException("Invalid latitude/longitude")

        return GeoPoint(latitude, longitude)
    }


}