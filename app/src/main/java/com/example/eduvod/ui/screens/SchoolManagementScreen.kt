package com.example.eduvod.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.model.School
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolManagementScreen(
    navController: NavHostController,
    viewModel: SchoolManagementViewModel = viewModel(),
) {

    val searchQuery by viewModel.searchQuery
    val selectedRegion by viewModel.selectedRegion
    val selectedType by viewModel.selectedType

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val schools = viewModel.schools

    val selectedSchool by remember { mutableStateOf<String?>(null) }
    var showAdminDialog by remember { mutableStateOf(false) }

    val regionOptions = listOf("ALL", "Nairobi", "Mombasa", "Kisumu", "Eldoret", "Garissa", "Isiolo", "Nakuru", "Turkana")
    val typeOptions = listOf("All", "Primary", "Secondary", "Mixed")

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            message?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearSnackbarMessage()
            }
        }
    }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    //OG
//    val filePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            val cursor = context.contentResolver.query(it, null, null, null, null)
//            cursor?.use {
//                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (it.moveToFirst()) {
//                    selectedFileName = it.getString(nameIndex)
//                }
//            }
//        }
//    }
//    LaunchedEffect(selectedFileName) {
//        selectedFileName?.let {
//            snackbarHostState.showSnackbar("Selected file: $it")
//        }
//    }

    //Retrofit
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(it)
            val fileName = contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) cursor.getString(nameIndex) else "file.xlsx"
            } ?: "file.xlsx"

            inputStream?.let { stream ->
                val bytes = stream.readBytes()
                val requestBody = RequestBody.create(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull(),
                    bytes
                )
                val multipart = MultipartBody.Part.createFormData("file", fileName, requestBody)

                scope.launch {
                    val success = viewModel.importSchoolFile(multipart)
                    snackbarHostState.showSnackbar(
                        if (success) "Schools imported successfully." else "Failed to import schools."
                    )
                }
            }
        }
    }
    LaunchedEffect(selectedFileName) {
        selectedFileName?.let {
            snackbarHostState.showSnackbar("Selected file: $it")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                    text = "School Management",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                  )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)

                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("school_admins") }) {
                        Icon(Icons.Default.Person, contentDescription = "Manage Admins", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff0D47A1))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon =  { Icon(Icons.Default.Add, contentDescription = "null") },
                text = { Text("Add New School")},
                onClick = {
                    navController.navigate("add_school")
                }
            )
        },
        containerColor = Color(0xFFF4F9FC),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val filteredSchools = schools.filter {
            it.name.contains(searchQuery, ignoreCase = true) &&
                    (selectedRegion == "ALL" || it.region == selectedRegion) &&
                    (selectedType == "ALL" || it.type == selectedType)
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Import Schools",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF0D47A1),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    //OG
//                    Button(
//                        onClick = {
//                            scope.launch {
//                                snackbarHostState.showSnackbar("School template downloaded.")
//                            }
//                        }
//                    ) {
//                        Icon(Icons.Default.Download, contentDescription = null)
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Download Template")
//                    }

                    //Retrofit
                    Button(
                        onClick = {
                            scope.launch {
                                val response = viewModel.downloadSchoolTemplate()
                                if (response != null && response.isSuccessful) {
                                    val body: ResponseBody? = response.body()
                                    if (body != null) {
                                        val fileName = "school_template.xlsx"
                                        val file = File(context.cacheDir, fileName)
                                        file.outputStream().use { output ->
                                            body.byteStream().copyTo(output)
                                        }
                                        snackbarHostState.showSnackbar("Downloaded: ${file.absolutePath}")
                                    } else {
                                        snackbarHostState.showSnackbar("Empty response body.")
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("Failed to download template.")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download Template")
                    }
                    Button(
                        //OG
//                        onClick = {
//                            filePickerLauncher.launch("*/*")
//                        }

                        //Retrofit
                        onClick = {
                            filePickerLauncher.launch(filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                .toString())
                        }
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Excel")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    label = { Text("Search Schools") },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registered Schools",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterDropdown(
                        label = "Region",
                        options = regionOptions,
                        selectedOption = selectedRegion,
                        onSelected = { viewModel.selectedRegion.value = it }
                    )
                    FilterDropdown(
                        label = "Type",
                        options = typeOptions,
                        selectedOption = selectedType,
                        onSelected = { viewModel.selectedType.value = it }
                    )
                    Button(
                        onClick = {
                            viewModel.searchQuery.value = ""
                            viewModel.selectedRegion.value = "ALL"
                            viewModel.selectedType.value = "ALL"
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Filters", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Filters")
                    }
                }
            }

            items(filteredSchools) { school ->
                SchoolCard(
                    school = school,
                    onView = {
                        navController.navigate("school_details/${Uri.encode(school.name)}")
                    },
                    onEdit = {
                        navController.navigate("edit_school/${Uri.encode(school.name)}")
                    },
                    onManageAdmin = {
                        navController.navigate("manage_admins/${Uri.encode(school.name)}")
                    }
                )
            }


        }
        if (showAdminDialog && selectedSchool != null) {
            AlertDialog(
                onDismissRequest = { showAdminDialog = false },
                title = { Text("Manage Admin for $selectedSchool") },
                text = {
                    Column {
                        Text("• Add Admin")
                        Text("• Block/Disable Admin")
                        Text("• Reset Admin Account")
                    }
                },
                confirmButton =  {
                    TextButton(onClick = { showAdminDialog = false }) {
                        Text("Close")
                    }
                }
            )

        }

    }
}

@Composable
fun SchoolCard(
    school: School,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onManageAdmin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                school.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "MoE REG: ${school.moeRegNo} | Email: ${school.email}",
                fontSize = 13.sp,
                color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionIconWithLabel(
                    icon = Icons.Default.Visibility,
                    label = "View",
                    onClick = onView,
                    contentDescription = "Admin Assigned",
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(24.dp)
                )
                ActionIconWithLabel(
                    icon = Icons.Default.Edit,
                    label = "Edit",
                    onClick = onEdit,
                    contentDescription = "Admin Assigned",
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(24.dp)
                )

                if (!school.hasAdmin) {
                    ActionIconWithLabel(
                        icon = Icons.Default.PersonAdd,
                        label = "Add Admin",
                        tint = Color(0xFF0D47A1),
                        onClick = onManageAdmin
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ActionIconWithLabel(
                            icon = Icons.Default.CheckCircle,
                            label = "View Admin",
                            contentDescription = "Admin Assigned",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp),
                            onClick = onManageAdmin
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionIconWithLabel(
    icon: ImageVector,
    label: String,
    contentDescription: String? = null,
    tint: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (tint != Color.Unspecified) tint else LocalContentColor.current
            )
        }
        Text(text = label, fontSize = 12.sp)
    }
}

@Composable
fun FilterDropdown(
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
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    Modifier.clickable { expanded = true })
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
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
