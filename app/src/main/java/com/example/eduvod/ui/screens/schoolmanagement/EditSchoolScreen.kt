package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.eduvod.model.School
import com.example.eduvod.ui.theme.responsiveFontSize
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSchoolScreen(
    navController: NavController,
    schoolId: Int,
    viewModel: SchoolManagementViewModel,
    configViewModel: SystemConfigViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val selectedSchool by viewModel.selectedSchool
    val isLoading by viewModel.isLoading

    // State for minimum loading time to prevent flickering
    var showLoading by remember { mutableStateOf(true) }

    LaunchedEffect(schoolId) {
        if (schoolId > 0) {
            val startTime = System.currentTimeMillis()
            viewModel.fetchSchoolById(schoolId)
            configViewModel.initialize()

            // Ensure loading screen shows for at least 500ms
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 500) delay(500 - elapsed)
            showLoading = false
        }
    }

    // Handle invalid school ID
    if (schoolId <= 0) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Invalid school ID", color = Color.Red)
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    // Loading state
    if (showLoading || (isLoading && selectedSchool == null)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading school details...", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    // Error state
    if (selectedSchool == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Failed to load school details", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    showLoading = true
                    viewModel.fetchSchoolById(schoolId)
                }) {
                    Text("Retry")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    // Main content (only shown when data is loaded)
    EditSchoolContent(
        navController = navController,
        selectedSchool = selectedSchool!!,
        snackbarHostState = snackbarHostState,
        scope = scope,
        viewModel = viewModel,
        configViewModel = configViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSchoolContent(
    navController: NavController,
    selectedSchool: School,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    viewModel: SchoolManagementViewModel,
    configViewModel: SystemConfigViewModel
) {
    // Create mutable states for all editable fields
    var moeRegNo by remember { mutableStateOf(selectedSchool.moeRegNo) }
    var kpsaRegNo by remember { mutableStateOf(selectedSchool.kpsaRegNo ?: "") }
    var schoolName by remember { mutableStateOf(selectedSchool.name) }
    var schoolCurriculum by remember { mutableStateOf(selectedSchool.curriculum ?: "") }
    var category by remember { mutableStateOf(selectedSchool.category) }
    var type by remember { mutableStateOf(selectedSchool.type) }
    var composition by remember { mutableStateOf(selectedSchool.composition) }
    var mobile by remember { mutableStateOf(selectedSchool.mobile ?: "") }
    var email by remember { mutableStateOf(selectedSchool.email) }
    var website by remember { mutableStateOf(selectedSchool.website ?: "") }
    var region by remember { mutableStateOf(selectedSchool.region) }
    var diocese by remember { mutableStateOf(selectedSchool.diocese ?: "") }
    var county by remember { mutableStateOf(selectedSchool.county) }
    var subCounty by remember { mutableStateOf(selectedSchool.subCounty) }
    var location by remember { mutableStateOf(selectedSchool.location ?: "") }
    var address by remember { mutableStateOf(selectedSchool.address ?: "") }

    var selectedAdmin by remember { mutableStateOf("") }
    var isAdminDropdownExpanded by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Load dropdown options
    val adminOptions = viewModel.getUnassignedAdmins()
    val curriculumOptions = configViewModel.curriculums.map { it.name }
    val categoryOptions = configViewModel.categories.map { it.name }
    val typeOptions = configViewModel.types.map { it.name }
    val regionOptions = configViewModel.regions.map { it.name }
    val countyOptions = configViewModel.counties.map { it.name }
    val subCountyOptions = configViewModel.subcounties.map { it.name }

    // Load dependent dropdowns when region or county changes
    LaunchedEffect(region) {
        if (region.isNotEmpty()) {
            configViewModel.loadCounties(region)
        }
    }

    LaunchedEffect(county) {
        if (county.isNotEmpty()) {
            configViewModel.loadSubcounties(county)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() },
                tint = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Edit School",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.015).sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // School Information Section
        Text(
            text = "Basic Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // MoE REG NO
        OutlinedTextField(
            value = moeRegNo,
            onValueChange = { moeRegNo = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("MoE REG NO", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter MoE REG NO", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // KPSA REG NO
        OutlinedTextField(
            value = kpsaRegNo,
            onValueChange = { kpsaRegNo = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("KPSA REG NO", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter KPSA REG NO", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // School Name
        OutlinedTextField(
            value = schoolName,
            onValueChange = { schoolName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("School Name", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter School Name", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // School Type Dropdown
        SchoolDropdownField(
            label = "School Type",
            options = typeOptions,
            selectedOption = type,
            onSelected = { type = it }
        )

        // Curriculum Dropdown
        SchoolDropdownField(
            label = "Curriculum",
            options = curriculumOptions,
            selectedOption = schoolCurriculum,
            onSelected = { schoolCurriculum = it }
        )

        // Category Dropdown
        SchoolDropdownField(
            label = "Category",
            options = categoryOptions,
            selectedOption = category,
            onSelected = { category = it }
        )

        // Composition
        OutlinedTextField(
            value = composition,
            onValueChange = { composition = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Composition", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Composition", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // Contact Information Section
        Text(
            text = "Contact Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Phone Number
        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Phone Number", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Phone Number", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Email", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Email", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // Location Information Section
        Text(
            text = "Location Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Region Dropdown
        SchoolDropdownField(
            label = "Region",
            options = regionOptions,
            selectedOption = region,
            onSelected = {
                region = it
                county = ""
                subCounty = ""
            }
        )

        // Diocese
        OutlinedTextField(
            value = diocese,
            onValueChange = { diocese = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Diocese", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Diocese", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // County Dropdown
        SchoolDropdownField(
            label = "County",
            options = countyOptions,
            selectedOption = county,
            onSelected = {
                county = it
                subCounty = ""
            }
        )

        // Sub-County Dropdown
        SchoolDropdownField(
            label = "Sub-County",
            options = subCountyOptions,
            selectedOption = subCounty,
            onSelected = { subCounty = it }
        )

        // Location
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Location", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Location", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Address", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Address", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // Website
        OutlinedTextField(
            value = website,
            onValueChange = { website = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            label = { Text("Website", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Enter Website URL", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // Assign Admin Section
        Text(
            text = "Assign Admin (Optional)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Admin Dropdown
        SchoolDropdownField(
            label = "Select Admin Email",
            options = adminOptions,
            selectedOption = selectedAdmin,
            onSelected = { selectedAdmin = it }
        )

        // Update Button
        Button(
            onClick = {
                val updatedSchool = selectedSchool.copy(
                    moeRegNo = moeRegNo,
                    kpsaRegNo = kpsaRegNo,
                    name = schoolName,
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

                scope.launch {
                    isSaving = true
                    try {
                        viewModel.updateSchool(updatedSchool.id, updatedSchool)
                        if (selectedAdmin.isNotBlank()) {
                            viewModel.assignAdminToSchool(selectedAdmin, updatedSchool.name) {}
                        }
                        snackbarHostState.showSnackbar("Changes saved for ${updatedSchool.name}")
                        navController.popBackStack()
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Error saving changes: ${e.message}")
                    } finally {
                        isSaving = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Saving...",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Text(
                    "Update",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

//Preview Screen
@Preview(showBackground = true)
@Composable
fun EditSchoolScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color(0xFF111418)
                )

                Text(
                    text = "Edit School",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color(0xFF111418)
                )
            }

            // School Information Section
            Text(
                text = "Basic Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF111418)
            )

            // MoE REG NO
            OutlinedTextField(
                value = "MOE/1234/2023",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("MoE REG NO") },
                placeholder = { Text("Enter MoE REG NO") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // KPSA REG NO
            OutlinedTextField(
                value = "KPSA/5678/2023",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("KPSA REG NO") },
                placeholder = { Text("Enter KPSA REG NO") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // School Name
            OutlinedTextField(
                value = "Greenwood High",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("School Name") },
                placeholder = { Text("Enter School Name") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // School Type Dropdown
            PreviewSchoolDropdownField(
                label = "School Type",
                options = listOf("Primary", "Secondary", "Mixed"),
                selectedOption = "Secondary"
            )

            // Curriculum Dropdown
            PreviewSchoolDropdownField(
                label = "Curriculum",
                options = listOf("CBC", "8-4-4", "IGCSE"),
                selectedOption = "CBC"
            )

            // Category Dropdown
            PreviewSchoolDropdownField(
                label = "Category",
                options = listOf("Public", "Private", "International"),
                selectedOption = "Public"
            )

            // Composition
            OutlinedTextField(
                value = "Mixed",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Composition") },
                placeholder = { Text("Enter Composition") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Contact Information Section
            Text(
                text = "Contact Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF111418)
            )

            // Phone Number
            OutlinedTextField(
                value = "+254712345678",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Phone Number") },
                placeholder = { Text("Enter Phone Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Email
            OutlinedTextField(
                value = "info@greenwoodhigh.edu",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Email") },
                placeholder = { Text("Enter Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Location Information Section
            Text(
                text = "Location Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF111418)
            )

            // Region Dropdown
            PreviewSchoolDropdownField(
                label = "Region",
                options = listOf("Nairobi", "Central", "Coastal"),
                selectedOption = "Nairobi"
            )

            // Diocese
            OutlinedTextField(
                value = "Nairobi Diocese",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Diocese") },
                placeholder = { Text("Enter Diocese") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // County Dropdown
            PreviewSchoolDropdownField(
                label = "County",
                options = listOf("Nairobi", "Kiambu", "Mombasa"),
                selectedOption = "Nairobi"
            )

            // Sub-County Dropdown
            PreviewSchoolDropdownField(
                label = "Sub-County",
                options = listOf("Westlands", "Dagoretti", "Langata"),
                selectedOption = "Westlands"
            )

            // Location
            OutlinedTextField(
                value = "Parklands",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Location") },
                placeholder = { Text("Enter Location") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Address
            OutlinedTextField(
                value = "123 Oak St, Anytown",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Address") },
                placeholder = { Text("Enter Address") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Website
            OutlinedTextField(
                value = "www.greenwoodhigh.edu",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 64.dp),
                label = { Text("Website") },
                placeholder = { Text("Enter Website URL") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Assign Admin Section
            Text(
                text = "Assign Admin (Optional)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF111418)
            )

            // Admin Dropdown
            PreviewSchoolDropdownField(
                label = "Select Admin Email",
                options = listOf("admin1@example.com", "admin2@example.com"),
                selectedOption = null
            )

            // Update Button
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp, 16.dp, 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D80F2),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Update",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewSchoolDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .heightIn(min = 64.dp)
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Select $label") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F2F5),
                focusedContainerColor = Color(0xFFF0F2F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F2F5))
                    .padding(vertical = 8.dp)
            ) {
                options.forEach { option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = false }
                            .padding(16.dp),
                        color = if (option == selectedOption) {
                            Color(0xFF0D80F2)
                        } else {
                            Color(0xFF111418)
                        }
                    )
                }
            }
        }
    }
}