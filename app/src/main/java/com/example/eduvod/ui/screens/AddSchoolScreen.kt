package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSchoolScreen(navController: NavHostController, prefillSchoolName: String?) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    //Form States
    var moeRegNo by remember { mutableStateOf("") }
    var kpsaRegNo by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf(prefillSchoolName ?: "") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var county by remember { mutableStateOf("") }
    var subCounty by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }


    //Dropdowns (Static options for now)
    val types = listOf("Primary", "Secondary", "Mixed", "Special Needs")
    val categories = listOf("Public", "Private")
    val curriculums = listOf("CBC", "British", "IGCSE", "8-4-4")
    val dioceses = listOf("Nairobi Diocese", "Western Diocese", "Coastal Diocese")

    var selectedType by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf("") }
    var selectedDiocese by remember { mutableStateOf("") }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Add New School", fontSize = 24.sp, color = Color.White)
                },
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
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Basic School Information", fontSize = 18.sp, color = Color(0xFF0D47A1))

            CustomTextField("MoE Reg No", moeRegNo) { moeRegNo = it  }
            CustomTextField("KPSA Reg No", kpsaRegNo) { kpsaRegNo = it }
            CustomTextField("School Name", schoolName) { schoolName = it }

            DropdownField("Curriculum", curriculums,selectedCurriculum) { selectedCurriculum = it }
            DropdownField("Category", categories, selectedCategory) { selectedCategory = it }
            DropdownField("Type", types, selectedType) { selectedType = it }
            DropdownField("Region", listOf("Nairobi", "Mombasa", "Nakuru"), region) { region = it }
            DropdownField("Diocese", dioceses, selectedDiocese) { selectedDiocese = it }
            CustomTextField("County", county) { county = it }
            CustomTextField("Sub-County", subCounty) { subCounty = it }
            CustomTextField("Location", location) { location = it }
            CustomTextField("Address", address) { address = it }
            CustomTextField("Website", website) { website = it }

            Divider()

            Text("School Contact Information", fontSize = 18.sp, color = Color(0xFF0D47A1))

            CustomTextField("Email", email) { email = it }
            CustomTextField("Mobile", mobile, keyboardType = KeyboardType.Phone) { mobile = it }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    when {
                        schoolName.isBlank() -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("School Name is required.")
                            }
                        }
                        moeRegNo.isBlank() -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("MoERegistration Number is required.")
                            }
                        }
                        email.isBlank() -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("Email is required.")
                            }
                        }
                        else -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("School data is valid. Ready to submit")
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
    keyboardOptions?.let {
        OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = it.copy(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
    }
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