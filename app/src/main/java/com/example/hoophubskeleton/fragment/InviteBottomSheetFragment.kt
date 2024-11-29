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
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.hoophubskeleton.viewmodel.GameViewModel
import com.example.hoophubskeleton.model.Game
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import com.example.hoophubskeleton.data.BasketballCourt
import com.example.hoophubskeleton.fragment.TopMenu.MapWithMarkersForSelection

class InviteBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var selectDateButton: Button
    private lateinit var selectTimeButton: Button
//    private lateinit var locationEditText: EditText
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var currentUserId: String
    private lateinit var invitedUserId: String
    private lateinit var geoPoint: GeoPoint

    private lateinit var selectOnMapButton: Button
    private lateinit var mapPopupContainer: ViewGroup

    private lateinit var playersPerTeamSpinner: Spinner
    private var playersPerTeam: Int = 1
    private lateinit var courtName: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invite_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        selectDateButton = view.findViewById(R.id.selectDateButton)
        selectTimeButton = view.findViewById(R.id.selectTimeButton)
        confirmButton = view.findViewById(R.id.inviteConfirmButton)
        cancelButton = view.findViewById(R.id.inviteCancelButton)
        selectOnMapButton = view.findViewById(R.id.selectOnMapButton)
        mapPopupContainer = view.findViewById(R.id.map_popup_container)
        playersPerTeamSpinner = view.findViewById(R.id.playersPerTeamSpinner)

        // Retrieve user IDs from arguments
        val currentId = arguments?.getString("currentUserId")
        val invitedId = arguments?.getString("invitedUserId")

        if (currentId == null || invitedId == null) {
            Toast.makeText(requireContext(), "Error: Missing user data.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        currentUserId = currentId
        invitedUserId = invitedId

        // Set up button listeners
        selectDateButton.setOnClickListener { showDatePickerDialog() }
        selectTimeButton.setOnClickListener { showTimePickerDialog() }
        selectOnMapButton.setOnClickListener { showMapPopup() }
        confirmButton.setOnClickListener { onConfirmClicked() }
        cancelButton.setOnClickListener { dismiss() }

        // Set up Spinner
        playersPerTeamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                playersPerTeam = parent?.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Default 1 if nothing selected
                playersPerTeam = 1
            }
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

        // Validate inputs
        if (date.isBlank() || time.isBlank() || !this::geoPoint.isInitialized) {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val gameDateTime = convertDateTimeToTimestamp(date, time)
            val maxParticipants = playersPerTeam*2
            val participants = if (invitedUserId.isNotEmpty()) {
                listOf(currentUserId, invitedUserId)
            } else {
                listOf(currentUserId)
            }

            val game = Game(
                participants = participants,
                gameDateTime = gameDateTime,
                location = geoPoint,
                timestamp = Timestamp.now(),
                maxParticipants = maxParticipants,
                courtName = courtName
            )

            gameViewModel.createInvite(game) { success ->
                context?.let { safeContext ->
                    if (success) {
                        Toast.makeText(safeContext, "Game created.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(safeContext, "Failed to create game.", Toast.LENGTH_SHORT).show()
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


    private fun showMapPopup() {
        mapPopupContainer.isVisible = true

        val composeView = ComposeView(requireContext()).apply {
            setContent {
                MapWithMarkersForSelection(
                    onCourtSelected = { court ->
                        handleCourtSelection(court)
                    },
                    onDismiss = {
                        hideMapPopup()
                    }
                )
            }
        }

        mapPopupContainer.removeAllViews()
        mapPopupContainer.addView(composeView)
    }


    private fun hideMapPopup() {
        mapPopupContainer.isVisible = false // Hide container
    }

    private fun handleCourtSelection(court: BasketballCourt) {
        geoPoint = GeoPoint(court.latitude, court.longitude)
        courtName = court.name

//        val formattedLocation = "${court.name} (${court.latitude}, ${court.longitude})"
//        locationEditText.setText(formattedLocation) // Show name and coordinates in the EditText
        hideMapPopup()
        Toast.makeText(requireContext(), "Selected: ${court.name}", Toast.LENGTH_SHORT).show()


    }



}