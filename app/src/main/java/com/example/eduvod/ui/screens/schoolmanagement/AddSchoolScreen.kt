package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import com.example.eduvod.viewmodel.SchoolRequest
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSchoolScreen(
    navController: NavHostController,
    prefillSchoolName: String?,
    schoolViewModel: SchoolManagementViewModel
) {
    val configViewModel: SystemConfigViewModel = viewModel()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var moeRegNo by remember { mutableStateOf("") }
    var kpsaRegNo by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf(prefillSchoolName ?: "") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    var selectedType by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("") }
    var selectedCounty by remember { mutableStateOf("") }
    var selectedSubCounty by remember { mutableStateOf("") }
    var selectedAdmin by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        configViewModel.initialize()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
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
                tint = Color(0xFF111418)
            )

            Text(
                text = "Add School",
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
            value = moeRegNo,
            onValueChange = { moeRegNo = it },
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
            value = kpsaRegNo,
            onValueChange = { kpsaRegNo = it },
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
            value = schoolName,
            onValueChange = { schoolName = it },
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
        SchoolDropdownField(
            label = "School Type",
            options = configViewModel.types.map { it.name },
            selectedOption = selectedType,
            onSelected = { selectedType = it }
        )

        // Curriculum Dropdown
        SchoolDropdownField(
            label = "Curriculum",
            options = configViewModel.curriculums.map { it.name },
            selectedOption = selectedCurriculum,
            onSelected = { selectedCurriculum = it }
        )

        // Category Dropdown
        SchoolDropdownField(
            label = "Category",
            options = configViewModel.categories.map { it.name },
            selectedOption = selectedCategory,
            onSelected = { selectedCategory = it }
        )

        // Region Dropdown
        SchoolDropdownField(
            label = "Region",
            options = configViewModel.regions.map { it.name },
            selectedOption = selectedRegion,
            onSelected = {
                selectedRegion = it
                configViewModel.loadCounties(it)
                selectedCounty = ""
                selectedSubCounty = ""
            }
        )

        // County Dropdown
        SchoolDropdownField(
            label = "County",
            options = configViewModel.counties.map { it.name },
            selectedOption = selectedCounty,
            onSelected = {
                selectedCounty = it
                configViewModel.loadSubcounties(it)
                selectedSubCounty = ""
            }
        )

        // Sub-County Dropdown
        SchoolDropdownField(
            label = "Sub-County",
            options = configViewModel.subcounties.map { it.name },
            selectedOption = selectedSubCounty,
            onSelected = { selectedSubCounty = it }
        )

        // Location
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
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
            value = address,
            onValueChange = { address = it },
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
            value = mobile,
            onValueChange = { mobile = it },
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
            value = email,
            onValueChange = { email = it },
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

        // Website
        OutlinedTextField(
            value = website,
            onValueChange = { website = it },
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
        SchoolDropdownField(
            label = "Assign Admin",
            options = schoolViewModel.getUnassignedAdmins(),
            selectedOption = selectedAdmin,
            onSelected = { selectedAdmin = it }
        )

        // Save Button
        Button(
            onClick = {
                scope.launch {
                    if (schoolName.isBlank()) {
                        schoolViewModel.snackbarMessage.value = "School Name is required."
                        return@launch
                    }
                    if (moeRegNo.isBlank()) {
                        schoolViewModel.snackbarMessage.value = "MoE Reg No is required."
                        return@launch
                    }
                    if (email.isBlank()) {
                        schoolViewModel.snackbarMessage.value = "Email is required."
                        return@launch
                    }
                    if (selectedCurriculum.isBlank() || selectedCategory.isBlank() || selectedType.isBlank() ||
                        selectedRegion.isBlank() || selectedCounty.isBlank() || selectedSubCounty.isBlank()
                    ) {
                        schoolViewModel.snackbarMessage.value = "Please select all dropdowns."
                        return@launch
                    }

                    val curriculumId = configViewModel.curriculums.find { it.name == selectedCurriculum }?.id ?: return@launch
                    val categoryId = configViewModel.categories.find { it.name == selectedCategory }?.id ?: return@launch
                    val typeId = configViewModel.types.find { it.name == selectedType }?.id ?: return@launch
                    val regionId = configViewModel.regions.find { it.name == selectedRegion }?.id ?: return@launch
                    val countyId = configViewModel.counties.find { it.name == selectedCounty }?.id ?: return@launch
                    val subCountyId = configViewModel.subcounties.find { it.name == selectedSubCounty }?.id ?: return@launch

                    val newSchool = SchoolRequest(
                        name = schoolName,
                        moeRegNo = moeRegNo,
                        kpsaRegNo = kpsaRegNo,
                        curriculumId = curriculumId,
                        categoryId = categoryId,
                        typeId = typeId,
                        composition = "Mixed",
                        phone = mobile,
                        email = email,
                        regionId = regionId,
                        countyId = countyId,
                        subCountyId = subCountyId,
                        location = location,
                        address = address,
                        website = website
                    )

                    isLoading = true
                    val success = schoolViewModel.addSchool(newSchool)
                    if (success != null) {
                        if (selectedAdmin.isNotBlank()) {
                            schoolViewModel.assignAdminToSchool(selectedAdmin, schoolName, onDone = {})
                        }
                        navController.navigate("schools") {
                            popUpTo("add_school") { inclusive = true }
                        }
                    }
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D80F2),
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Save",
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
fun SchoolDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

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
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F2F5)
                )
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(options) { option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected(option)
                                    expanded = false
                                }
                                .background(
                                    if (option == selectedOption) {
                                        Color(0xFF0D80F2).copy(alpha = 0.1f)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = option,
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
    }
}

//Preview Screen

@Preview(showBackground = true)
@Composable
fun AddSchoolScreenPreview() {
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
                    text = "Add School",
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
                value = "",
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
                value = "",
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
                value = "",
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
                selectedOption = "Primary"
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

            // Region Dropdown
            PreviewSchoolDropdownField(
                label = "Region",
                options = listOf("Coastal", "Central", "Nairobi"),
                selectedOption = "Nairobi"
            )

            // County Dropdown
            PreviewSchoolDropdownField(
                label = "County",
                options = listOf("Mombasa", "Nairobi", "Kiambu"),
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
                value = "",
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
                value = "",
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
                value = "",
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
                value = "",
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

            // Website
            OutlinedTextField(
                value = "",
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
                label = "Assign Admin",
                options = listOf("admin1@example.com", "admin2@example.com"),
                selectedOption = null
            )

            // Save Button
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
                    text = "Save",
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
        )

        if (expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F2F5)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    options.forEach { option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (option == selectedOption) {
                                        Color(0xFF0D80F2).copy(alpha = 0.1f)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = option,
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
    }
}