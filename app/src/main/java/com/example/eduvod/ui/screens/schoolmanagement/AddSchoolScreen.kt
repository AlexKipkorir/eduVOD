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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.ui.screens.AppScaffold
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import com.example.eduvod.viewmodel.SchoolRequest
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch
import com.example.eduvod.ui.theme.responsiveFontSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSchoolScreen(
    navController: NavHostController,
    prefillSchoolName: String?,
    schoolViewModel: SchoolManagementViewModel
) {
    val configViewModel: SystemConfigViewModel = viewModel()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
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

    AppScaffold(
        title = "Add New School",
        snackbarHostState = snackbarHostState,
        showTopBar = true,
        showLogout = false,
        showBackButton = true,
        onBack = { navController.popBackStack() }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // === Basic School Info Card ===
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Basic School Information", fontSize = responsiveFontSize(18f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))

                    CustomTextField("MoE Reg No", moeRegNo) { moeRegNo = it }
                    CustomTextField("KPSA Reg No", kpsaRegNo) { kpsaRegNo = it }
                    CustomTextField("School Name", schoolName) { schoolName = it }

                    DropdownField("Curriculum", configViewModel.curriculums.map { it.name }, selectedCurriculum) {
                        selectedCurriculum = it
                    }
                    DropdownField("Category", configViewModel.categories.map { it.name }, selectedCategory) {
                        selectedCategory = it
                    }
                    DropdownField("Type", configViewModel.types.map { it.name }, selectedType) {
                        selectedType = it
                    }

                    DropdownField("Region", configViewModel.regions.map { it.name }, selectedRegion) {
                        selectedRegion = it
                        configViewModel.loadCounties(it)
                        selectedCounty = ""
                        selectedSubCounty = ""
                    }

                    DropdownField("County", configViewModel.counties.map { it.name }, selectedCounty) {
                        selectedCounty = it
                        configViewModel.loadSubcounties(it)
                        selectedSubCounty = ""
                    }

                    DropdownField("Sub-County", configViewModel.subcounties.map { it.name }, selectedSubCounty) {
                        selectedSubCounty = it
                    }

                    CustomTextField("Location", location) { location = it }
                    CustomTextField("Address", address) { address = it }
                    CustomTextField("Website", website) { website = it }
                }
            }

            // === Contact Info Card ===
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Contact Information", fontSize = responsiveFontSize(18f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))

                    CustomTextField("Email", email, keyboardType = KeyboardType.Email) { email = it }
                    CustomTextField("Mobile", mobile, keyboardType = KeyboardType.Phone) { mobile = it }
                }
            }

            // === Assign Admin Card ===
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Assign Admin (Optional)", fontSize = responsiveFontSize(18f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))

                    AdminDropdown(
                        label = "Assign Admin",
                        options = schoolViewModel.getUnassignedAdmins(),
                        selectedOption = selectedAdmin,
                        onSelected = { selectedAdmin = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // === Submit Button ===
            Button(
                onClick = {
                    scope.launch {
                        if (schoolName.isBlank()) {
                            snackbarHostState.showSnackbar("School Name is required."); return@launch
                        }
                        if (moeRegNo.isBlank()) {
                            snackbarHostState.showSnackbar("MoE Reg No is required."); return@launch
                        }
                        if (email.isBlank()) {
                            snackbarHostState.showSnackbar("Email is required."); return@launch
                        }
                        if (selectedCurriculum.isBlank() || selectedCategory.isBlank() || selectedType.isBlank() ||
                            selectedRegion.isBlank() || selectedCounty.isBlank() || selectedSubCounty.isBlank()
                        ) {
                            snackbarHostState.showSnackbar("Please select all dropdowns."); return@launch
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
                                snackbarHostState.showSnackbar("School added & admin assigned.")
                            } else {
                                snackbarHostState.showSnackbar("School added successfully.")
                            }
                            navController.navigate("schools") {
                                popUpTo("add_school") { inclusive = true }
                            }
                        } else {
                            snackbarHostState.showSnackbar("Failed to add school.")
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text("Submit School", fontSize = responsiveFontSize(16f))
                }
            }
        }
    }
}



@Composable
fun CustomTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0D47A1),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF0D47A1),
            cursorColor = Color(0xFF0D47A1)
        )
    )
}

@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onSelected: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val baseFontSize = when {
        screenWidth < 360 -> 12.sp
        screenWidth < 400 -> 13.sp
        screenWidth < 480 -> 14.sp
        else -> 16.sp
    }

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = baseFontSize,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box {
            OutlinedTextField(
                value = selectedOption ?: "",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand dropdown",
                        modifier = Modifier.clickable { expanded = !expanded },
                        tint = Color(0xFF0D47A1)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0D47A1),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF0D47A1),
                    cursorColor = Color(0xFF0D47A1)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = baseFontSize)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(density) { textFieldSize.width.toDp() })
                    .heightIn(max = 300.dp)
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                fontSize = baseFontSize
                            )
                        },
                        onClick = {
                            onSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun AdminDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val labelFontSize = (screenWidth * 0.035).sp   // responsive text size
    val valueFontSize = (screenWidth * 0.04).sp

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = labelFontSize,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = TextStyle(fontSize = valueFontSize),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Admin",
                        modifier = Modifier.clickable { expanded = true },
                        tint = Color(0xFF0D47A1)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0D47A1),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF0D47A1),
                    cursorColor = Color(0xFF0D47A1)
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                options.forEach { adminEmail ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF0D47A1),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = adminEmail,
                                    fontSize = valueFontSize
                                )
                            }
                        },
                        onClick = {
                            onSelected(adminEmail)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
