package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    val snackbarHostState = remember { SnackbarHostState() }
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

    LaunchedEffect(Unit) {
        configViewModel.initialize()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add New School", fontSize = 24.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Basic School Information", fontSize = 18.sp, color = Color(0xFF0D47A1))

            CustomTextField("MoE Reg No", moeRegNo) { moeRegNo = it }
            CustomTextField("KPSA Reg No", kpsaRegNo) { kpsaRegNo = it }
            CustomTextField("School Name", schoolName) { schoolName = it }

            DropdownField(
                label = "Curriculum",
                options = configViewModel.curriculums.map { it.name },
                selectedOption = selectedCurriculum
            ) { selectedCurriculum = it }

            DropdownField(
                label = "Category",
                options = configViewModel.categories.map { it.name },
                selectedOption = selectedCategory
            ) { selectedCategory = it }

            DropdownField(
                label = "Type",
                options = configViewModel.types.map { it.name },
                selectedOption = selectedType
            ) { selectedType = it }

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

            Divider()

            Text("School Contact Information", fontSize = 18.sp, color = Color(0xFF0D47A1))
            CustomTextField("Email", email) { email = it }
            CustomTextField("Mobile", mobile, keyboardType = KeyboardType.Phone) { mobile = it }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Assign Admin (Optional)", fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))
            Spacer(modifier = Modifier.height(6.dp))

            AdminDropdown(
                label = "Assign Admin",
                options = schoolViewModel.getUnassignedAdmins(),
                selectedOption = selectedAdmin,
                onSelected = { selectedAdmin = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        schoolName.isBlank() -> scope.launch { snackbarHostState.showSnackbar("School Name is required.") }
                        moeRegNo.isBlank() -> scope.launch { snackbarHostState.showSnackbar("MoE Reg No is required.") }
                        email.isBlank() -> scope.launch { snackbarHostState.showSnackbar("Email is required.") }
                        selectedCurriculum.isBlank() || selectedCategory.isBlank() || selectedType.isBlank() ||
                                selectedRegion.isBlank() || selectedCounty.isBlank() || selectedSubCounty.isBlank() -> {
                            scope.launch { snackbarHostState.showSnackbar("Please select all dropdowns.") }
                        }
                        else -> {
                            val curriculumId = configViewModel.curriculums.find { it.name == selectedCurriculum }?.id ?: return@Button
                            val categoryId = configViewModel.categories.find { it.name == selectedCategory }?.id ?: return@Button
                            val typeId = configViewModel.types.find { it.name == selectedType }?.id ?: return@Button
                            val regionId = configViewModel.regions.find { it.name == selectedRegion }?.id ?: return@Button
                            val countyId = configViewModel.counties.find { it.name == selectedCounty }?.id ?: return@Button
                            val subCountyId = configViewModel.subcounties.find { it.name == selectedSubCounty }?.id ?: return@Button

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

                            schoolViewModel.addSchool(newSchool)
                            navController.navigate("schools") {
                                popUpTo("add_school") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit School")
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
        modifier = Modifier.fillMaxWidth()
    )

}

@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Box {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true}) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown" )

                    }
                }
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onSelected(it)
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
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", Modifier.clickable { expanded = true })
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0D47A1),
                unfocusedBorderColor = Color.LightGray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { adminEmail ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(adminEmail)
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