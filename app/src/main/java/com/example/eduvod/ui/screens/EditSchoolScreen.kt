package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
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
    var subCounty by remember { mutableStateOf(originalSchool?.county?: "") }
    var location by remember { mutableStateOf(originalSchool?.location?: "") }
    var address by remember { mutableStateOf(originalSchool?.address?: "") }
    var website by remember { mutableStateOf(originalSchool?.website?: "") }

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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        //Update school logic here
                        snackbarHostState.showSnackbar("Changes saved for ${originalSchool?.name}")
                        navController.popBackStack()
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
