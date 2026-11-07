package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.ui.PigeonverseViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.PigeonBorder
import com.example.ui.theme.PigeonEmerald
import com.example.ui.theme.PigeonGold
import com.example.ui.theme.PigeonGoldLight
import com.example.ui.theme.PigeonSurfaceCard
import com.example.ui.theme.PigeonSurfaceDark
import com.example.ui.theme.PigeonSurfaceElevated
import com.example.ui.theme.PigeonTextPrimary
import com.example.ui.theme.PigeonTextSecondary
import com.example.ui.theme.PigeonViolet

@Composable
fun DoctorsScreen(viewModel: PigeonverseViewModel) {
    val doctors by viewModel.doctors.collectAsState()
    val userAppointments by viewModel.userAppointments.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Doctors, 1: My Appointments
    var doctorToBook by remember { mutableStateOf<Doctor?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PigeonSurfaceDark,
            contentColor = PigeonGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PigeonGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Avian Veterinarians", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "My Appointments (${userAppointments.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    LuxuryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(PigeonViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.doctor_avian_portrait_1793978115798),
                                    contentDescription = "Pigeon Health Telehealth",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Certified Avian Telehealth",
                                    color = PigeonGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Book video consultations, flight performance diagnostics and loft health audits.",
                                    color = PigeonTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                items(doctors, key = { it.id }) { doctor ->
                    DoctorCard(doctor = doctor, onBook = { doctorToBook = doctor })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (userAppointments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = PigeonTextSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Appointments Booked",
                                    color = PigeonTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Schedule a consultation with our verified pigeon specialists.",
                                    color = PigeonTextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(userAppointments, key = { it.id }) { appt ->
                        AppointmentCard(appt = appt)
                    }
                }
            }
        }
    }

    if (doctorToBook != null) {
        val doctor = doctorToBook!!
        BookAppointmentDialog(
            doctor = doctor,
            onDismiss = { doctorToBook = null },
            onConfirmBooking = { date, time, type, notes ->
                viewModel.bookDoctorAppointment(doctor, date, time, type, notes)
                doctorToBook = null
            }
        )
    }
}

@Composable
private fun DoctorCard(doctor: Doctor, onBook: () -> Unit) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.name,
                        color = PigeonTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = doctor.title,
                        color = PigeonGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${doctor.qualification} • ${doctor.experienceYears} Years Exp",
                        color = PigeonTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PigeonGold.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = PigeonGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "%.1f".format(doctor.rating), color = PigeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PigeonTextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = doctor.clinicLocation, color = PigeonTextSecondary, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Consultation Fee (INR / USD)", color = PigeonTextSecondary, fontSize = 10.sp)
                    Text(
                        text = "₹%,d ($%.0f)".format((doctor.fee * 86.0).toLong(), doctor.fee),
                        color = PigeonEmerald,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Button(
                    onClick = onBook,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Book Consult", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(appt: Appointment) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = appt.doctorName, color = PigeonTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = appt.specialty, color = PigeonGoldLight, fontSize = 11.sp)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PigeonEmerald.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PigeonEmerald.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = appt.status.uppercase(),
                        color = PigeonEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "📅 ${appt.date} • ${appt.timeSlot}", color = PigeonTextSecondary, fontSize = 11.sp)
                Text(text = "🎥 ${appt.consultType}", color = PigeonViolet, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            if (appt.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${appt.notes}",
                    color = PigeonTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun BookAppointmentDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onConfirmBooking: (date: String, timeSlot: String, type: String, notes: String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("2026-11-12") }
    var selectedTime by remember { mutableStateOf("10:00 AM") }
    var selectedType by remember { mutableStateOf("Video Call") }
    var symptomsNotes by remember { mutableStateOf("Routine pre-flight respiratory check and vaccination booster.") }

    val consultTypes = listOf("Video Call", "Clinic Visit", "Loft Inspection")
    val timeSlots = listOf("09:30 AM", "11:00 AM", "02:30 PM", "04:00 PM")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Book Avian Consultation",
                color = PigeonGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Doctor: ${doctor.name}",
                    color = PigeonTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Consultation Fee: $${"%,.0f".format(doctor.fee)}",
                    color = PigeonEmerald,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )

                Text(text = "Consultation Type", color = PigeonTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    consultTypes.forEach { type ->
                        val isSel = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) PigeonViolet else PigeonSurfaceCard,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedType = type }
                        ) {
                            Text(
                                text = type,
                                color = if (isSel) Color.White else PigeonTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Consultation Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Select Time Slot", color = PigeonTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    timeSlots.forEach { slot ->
                        val isSel = selectedTime == slot
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) PigeonGold else PigeonSurfaceCard,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTime = slot }
                        ) {
                            Text(
                                text = slot,
                                color = if (isSel) Color.Black else PigeonTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = symptomsNotes,
                    onValueChange = { symptomsNotes = it },
                    label = { Text("Pigeon Symptoms / Clinical Concerns") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmBooking(selectedDate, selectedTime, selectedType, symptomsNotes) },
                colors = ButtonDefaults.buttonColors(containerColor = PigeonGold, contentColor = Color(0xFF140D00))
            ) {
                Text("Confirm & Pay $${"%,.0f".format(doctor.fee)}", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PigeonTextSecondary)
            }
        },
        containerColor = PigeonSurfaceDark
    )
}
