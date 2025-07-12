package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSchoolScreen(
    navController: NavController,
    schoolName: String,
    viewModel: SchoolManagementViewModel = viewModel(),
    configViewModel: SystemConfigViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val originalSchool = remember { viewModel.getSchoolByName(schoolName) }

    // Initialize region/county/subcounty lists
    LaunchedEffect(originalSchool) {
        configViewModel.initialize()
        originalSchool?.region?.let { configViewModel.loadCounties(it) }
        originalSchool?.county?.let { configViewModel.loadSubcounties(it) }
    }

    // Dropdown options
    val curriculumOptions = configViewModel.curriculums.map { it.name }
    val categoryOptions = configViewModel.categories.map { it.name }
    val typeOptions = configViewModel.types.map { it.name }
    val regionOptions = configViewModel.regions.map { it.name }
    val countyOptions = configViewModel.counties.map { it.name }
    val subCountyOptions = configViewModel.subcounties.map { it.name }

    // Form fields
    var moeRegNo by remember { mutableStateOf("") }
    var kpsaRegNo by remember { mutableStateOf("") }
    var schoolCurriculum by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var composition by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var diocese by remember { mutableStateOf("") }
    var county by remember { mutableStateOf("") }
    var subCounty by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var selectedAdmin by remember { mutableStateOf("") }
    val adminOptions = viewModel.getUnassignedAdmins()
    var isAdminDropdownExpanded by remember { mutableStateOf(false) }

    // Prefill only once
    LaunchedEffect(Unit) {
        originalSchool?.let {
            moeRegNo = it.moeRegNo
            kpsaRegNo = it.kpsaRegNo
            schoolCurriculum = it.curriculum
            category = it.category
            type = it.type
            composition = it.composition
            mobile = it.mobile
            email = it.email
            website = it.website
            region = it.region
            diocese = it.diocese
            county = it.county
            subCounty = it.subCounty
            location = it.location
            address = it.address
        }
    }

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

            DropdownField("Curriculum", curriculumOptions, schoolCurriculum) { schoolCurriculum = it }
            DropdownField("Category", categoryOptions, category) { category = it }
            DropdownField("Type", typeOptions, type) { type = it }
            SectionInputField("Composition", composition) { composition = it }

            Divider()
            Text("Contact Info", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
            SectionInputField("Mobile", mobile, KeyboardType.Phone) { mobile = it }
            SectionInputField("Email", email, KeyboardType.Email) { email = it }
            SectionInputField("Website", website) { website = it }

            Divider()
            Text("Location Info", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
            DropdownField("Region", regionOptions, region) {
                region = it
                configViewModel.loadCounties(it)
                county = ""
                subCounty = ""
            }
            DropdownField("County", countyOptions, county) {
                county = it
                configViewModel.loadSubcounties(it)
                subCounty = ""
            }
            DropdownField("Sub-County", subCountyOptions, subCounty) { subCounty = it }

            SectionInputField("Diocese", diocese) { diocese = it }
            SectionInputField("Location", location) { location = it }
            SectionInputField("Address", address) { address = it }

            Divider()
            Text("Assign Admin (Optional)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))

            Box {
                OutlinedTextField(
                    value = selectedAdmin,
                    onValueChange = {},
                    label = { Text("Select Admin Email") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAdminDropdownExpanded = true },
                    trailingIcon = {
                        IconButton(onClick = { isAdminDropdownExpanded = !isAdminDropdownExpanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    }
                )

                DropdownMenu(
                    expanded = isAdminDropdownExpanded,
                    onDismissRequest = { isAdminDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    adminOptions.forEach { adminEmail ->
                        DropdownMenuItem(
                            text = { Text(adminEmail) },
                            onClick = {
                                selectedAdmin = adminEmail
                                isAdminDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedSchool = originalSchool?.copy(
                        moeRegNo = moeRegNo,
                        kpsaRegNo = kpsaRegNo,
                        curriculum = schoolCurriculum,
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
                            viewModel.updateSchool(it.id, it)
                            if (selectedAdmin.isNotBlank()) {
                                viewModel.reassignAdmin(selectedAdmin, it.name)
                            }
                            snackbarHostState.showSnackbar("Changes saved for ${it.name}")
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
