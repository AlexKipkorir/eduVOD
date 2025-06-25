package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api ::class)
@Composable
fun EditSchoolScreen(
    navController: NavController,
    schoolName: String,
    viewModel: SchoolManagementViewModel = viewModel()
) {
    val originalSchool = viewModel.getSchoolByName(schoolName)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    //Form state variables
    var moeRegNo by remember { mutableStateOf(originalSchool?.moeRegNo?: "") }
    var kpsaRegNo by remember { mutableStateOf(originalSchool?.kpsaRegNo?: "") }
    var curriculum by remember { mutableStateOf(originalSchool?.curriculum?: "") }
    var category by remember { mutableStateOf(originalSchool?.category?: "") }
    var type by remember { mutableStateOf(originalSchool?.type?: "") }
    var composition by remember { mutableStateOf(originalSchool?.composition?: "") }
    var mobile by remember { mutableStateOf(originalSchool?.mobile?: "") }
    var email by remember { mutableStateOf(originalSchool?.email?: "") }
    var region by remember { mutableStateOf(originalSchool?.region?: "") }
    var diocese by remember { mutableStateOf(originalSchool?.diocese?: "") }
    var county by remember { mutableStateOf(originalSchool?.county?: "") }
    var subCounty by remember { mutableStateOf(originalSchool?.subCounty?: "") }
    var location by remember { mutableStateOf(originalSchool?.location?: "") }
    var address by remember { mutableStateOf(originalSchool?.address?: "") }
    var website by remember { mutableStateOf(originalSchool?.website?: "") }
    var selectedAdmin by remember { mutableStateOf("") }
    val adminOptions = viewModel.getUnassignedAdmins()
    var isDropdownExpanded by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit School", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Update School Details", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF0D47A1))

            SectionInputField("MoE REG NO", moeRegNo) { moeRegNo = it }
            SectionInputField("KPSA REG NO", kpsaRegNo) { kpsaRegNo = it }
            SectionInputField("Curriculum", curriculum) { curriculum = it }
            SectionInputField("Category", category) { category = it }
            SectionInputField("Type", type) { type = it }
            SectionInputField("Composition", composition) { composition = it }


            Divider()
            Text("Contact Info", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
            SectionInputField("Mobile", mobile, KeyboardType.Phone) { mobile = it }
            SectionInputField("Email", email, KeyboardType.Email) { email = it }
            SectionInputField("Website", website) { website = it }


            Divider()
            Text("Location Info", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
            SectionInputField("Region", region) { region = it }
            SectionInputField("Diocese", diocese) { diocese = it }
            SectionInputField("County", county) { county = it }
            SectionInputField("SubCounty", subCounty) { subCounty = it }
            SectionInputField("Location", location) { location = it }
            SectionInputField("Address", address) { address = it }

            Divider()
            Text("Assign Admin (Optional)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))

            OutlinedTextField(
                value = selectedAdmin,
                onValueChange = { selectedAdmin = it },
                label = { Text("Select Admin Email") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                trailingIcon = {
                    IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Dropdown"
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                adminOptions.forEach { adminEmail ->
                    DropdownMenuItem(
                        text = { Text(adminEmail) },
                        onClick = {
                            selectedAdmin = adminEmail
                            isDropdownExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedSchool = originalSchool?.copy(
                        moeRegNo = moeRegNo,
                        kpsaRegNo = kpsaRegNo,
                        curriculum = curriculum,
                        category = category,
                        type = type,
                        composition = composition,
                        mobile = mobile,
                        email = email,
                        website = website,
                        region = region,
                        diocese = diocese,
                        county = county,
                        subCounty = subCounty,
                        location = location,
                        address = address
                    )

                    updatedSchool?.let {
                        scope.launch {
                            viewModel.updateSchool(it.moeRegNo.toIntOrNull() ?: return@launch, it)
                            if (selectedAdmin.isNotBlank()) {
                                viewModel.reassignAdmin(selectedAdmin, it.name)
                            }
                            snackbarHostState.showSnackbar("Changes saved for ${originalSchool?.name}")
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("Save Changes", color = Color.White)
            }
        }
    }
}

@Composable
fun SectionInputField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}
