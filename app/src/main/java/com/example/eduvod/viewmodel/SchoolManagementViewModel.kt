package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.model.School
import com.example.eduvod.repositories.SchoolRepository
import com.example.eduvod.retrofit.response.toSchool
import com.example.eduvod.ui.screens.schoolmanagement.AdminAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import androidx.compose.runtime.State

data class SchoolRequest(
    val moeRegNo: String,
    val kpsaRegNo: String,
    val name: String,
    val curriculumId: Int,
    val categoryId: Int,
    val typeId: Int,
    val composition: String,
    val phone: String,
    val email: String,
    val regionId: Long,
    val countyId: Long,
    val subCountyId: Long,
    val location: String,
    val address: String,
    val website: String
)

data class AdminCreateRequest(
    val username: String,
    val email: String,
    val password: String,
    val schoolId: String
)
data class AdminAssignRequest(
    val schoolAdminId: Long,
    val schoolId: Int
)

data class AdminUnassignRequest(
    val schoolId: String
)
data class AdminStatusUpdateRequest(
    val status: String
)

data class AdminResetRequest(
    val adminId: Int,
    val newPassword: String
)

data class SchoolAdmin(
    val id: Int,
    val username: String,
    val email: String,
    val schoolName: String?,
    val status: String
)

class SchoolManagementViewModel(
    private val repository: SchoolRepository = SchoolRepository()
) : ViewModel() {

    var searchQuery = mutableStateOf("")
    var selectedRegion = mutableStateOf("ALL")
    var selectedType = mutableStateOf("ALL")

    val snackbarMessage = MutableStateFlow<String?>(null)

    val admins = mutableStateListOf<AdminAccount>()

    val schools = mutableStateListOf<School>()
    val schoolAdmins = mutableStateListOf<SchoolAdmin>()
    private val _selectedSchool = mutableStateOf<School?>(null)
    val selectedSchool: State<School?> = _selectedSchool

    fun setLoading(value: Boolean) {
        isLoading.value = value
    }
    val isLoading = mutableStateOf(false)

    fun fetchSchoolsWithMinimumDelay() {
        viewModelScope.launch {
            isLoading.value = true
            val startTime = System.currentTimeMillis()

            fetchSchools() // your existing fetch method

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = 1000 - elapsed
            if (remaining > 0) delay(remaining)
            isLoading.value = false
        }
    }

    init {
        fetchSchools()
        fetchAdmins()
    }

    // --- SCHOOL Functions --//
    fun fetchSchools() {
        viewModelScope.launch {
            try {
                val schoolsResponse = repository.getSchools()
                val adminsResponse = repository.getAllSchoolAdmins()

                if (schoolsResponse.isSuccessful && adminsResponse.isSuccessful) {
                    val schoolList = schoolsResponse.body()?.data ?: emptyList()
                    val adminList = adminsResponse.body()?.data ?: emptyList()

                    val schoolsWithAdmins = adminList.mapNotNull { it.schoolName }.toSet()

                    val updatedSchools = schoolList.map { school ->
                        val hasAdmin = school.name in schoolsWithAdmins
                        school.copy(hasAdmin = hasAdmin)
                    }

                    schools.clear()
                    schools.addAll(updatedSchools)
                } else {
                    snackbarMessage.value = "Failed to load schools or admins."
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error fetching schools: ${e.localizedMessage}"
            }
        }
    }

    suspend fun addSchool(request: SchoolRequest): School? {
        return try {
            val response = repository.addSchool(request)
            if (response.isSuccessful) {
                val addedResponse = response.body()?.data
                val addedSchool = addedResponse?.toSchool()
                fetchSchools()
                addedSchool
            } else {
                snackbarMessage.value = "Failed to add school."
                null
            }
        } catch (e: Exception) {
            snackbarMessage.value = "Error adding school: ${e.localizedMessage}"
            null
        }
    }

    fun updateSchool(id: Int, request: School) {
        viewModelScope.launch {
            try {
                val response = repository.updateSchool(id, request)
                if (response.isSuccessful) {
                    val updated = response.body()?.data
                    val index = schools.indexOfFirst { it.id == id }
                    if (index != -1 && updated != null) {
                        schools[index] = updated
                    }
                } else {
                    snackbarMessage.value = "Failed to update school."
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error updating school: ${e.localizedMessage}"
            }
        }
    }
    fun fetchSchoolById(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getSchoolById(id)
                if (response.isSuccessful) {
                    val schoolResponse = response.body()?.data
                    _selectedSchool.value = schoolResponse?.toSchool()
                } else {
                    _selectedSchool.value = null
                }
            } catch (e: Exception) {
                _selectedSchool.value = null
            }
        }
    }

    suspend fun downloadSchoolTemplate(): Response<ResponseBody>? {
        return try {
            repository.downloadTemplate()
        } catch (e: Exception) {
            snackbarMessage.value = "Error downloading template: ${e.localizedMessage}"
            null
        }
    }

    suspend fun importSchoolFile(file: MultipartBody.Part): Boolean {
        return try {
            val response = repository.importSchools(file)
            val success = response.isSuccessful && response.body()?.statusCode == 200
            if (!success) {
                snackbarMessage.value = "Failed to import schools."
            }
            success
        } catch (e: Exception) {
            snackbarMessage.value = "Error importing schools: ${e.localizedMessage}"
            false
        }
    }

    fun assignAdmin(schoolName: String) {
        val index = schools.indexOfFirst { it.name == schoolName }
        if (index != -1) {
            schools[index] = schools[index].copy(hasAdmin = true)
        }
    }

    fun getSchoolByName(name: String): School? {
        return schools.find { it.name == name }
    }

    fun deleteSchool(schoolId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteSchool(schoolId)
                if (response.isSuccessful) {
                    schools.removeIf { it.id == schoolId }
                } else {
                    snackbarMessage.value = "Failed to delete school."
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error deleting school: ${e.localizedMessage}"
            }
        }
    }
    // --- ADMIN Functions ---//
    fun fetchAdmins() {
        viewModelScope.launch {
            try {
                val response = repository.getAllSchoolAdmins()
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        schoolAdmins.clear()
                        schoolAdmins.addAll(it)
                    }
                } else {
                    snackbarMessage.value = "Failed to load admins."
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error fetching admins: ${e.localizedMessage}"
            }
        }
    }
    fun addAdmin(username: String, email: String, password: String, schoolId: String): Boolean {
        if (schoolAdmins.any { it.email.equals(email, ignoreCase = true) }) return false

        viewModelScope.launch {
            try {
                val request = AdminCreateRequest(
                    username = username,
                    email = email,
                    password = "123456",
                    schoolId = schoolId
                )
                repository.addSchoolAdmin(request)
                fetchAdmins()
            } catch (e: Exception) {
                snackbarMessage.value = "Error adding admin: ${e.localizedMessage}"
            }
        }
        return true
    }
    fun unassignAdmin(adminEmail: String, onDone: () -> Unit) {
        val admin = schoolAdmins.find { it.email.equals(adminEmail, ignoreCase = true) } ?: return onDone()

        viewModelScope.launch {
            try {
                val response = repository.unassignAdmin(admin.id.toLong())
                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    val index = schoolAdmins.indexOfFirst { it.email == adminEmail }
                    if (index != -1) {
                        schoolAdmins[index] = schoolAdmins[index].copy(schoolName = null)
                    }
                } else {
                    snackbarMessage.value = "Failed to unassign admin"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error unassigning admin: ${e.localizedMessage}"
            } finally {
                onDone()
            }
        }
    }

    fun assignAdminToSchool(email: String, schoolName: String, onDone: () -> Unit) {
        val schoolId = getSchoolByName(schoolName)?.id ?: return onDone()
        val admin = schoolAdmins.find { it.email.equals(email, ignoreCase = true) } ?: return onDone()

        viewModelScope.launch {
            try {
                val response = repository.assignAdminToSchool(
                    AdminAssignRequest(
                        schoolAdminId = admin.id.toLong(),
                        schoolId = schoolId
                    )
                )
                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    snackbarMessage.value = "Admin assigned successfully"
                    fetchAdmins()
                } else {
                    snackbarMessage.value = "Failed to assign admin"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error assigning admin: ${e.localizedMessage}"
            } finally {
                onDone()
            }
        }
    }

    fun getUnassignedAdmins(): List<String> {
        return schoolAdmins.filter { it.schoolName == null }.map { it.email }
    }
    fun blockAdmin(email: String, block: Boolean) {
        viewModelScope.launch {
            try {
                val admin = schoolAdmins.find { it.email.equals(email, ignoreCase = true) }
                if (admin != null) {
                    val status = if (block) "BLOCKED" else "ACTIVE"
                    repository.updateAdminStatus(admin.id, status)
                    fetchAdmins()
                } else {
                    snackbarMessage.value = "Admin not found."
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error blocking admin: ${e.localizedMessage}"
            }
        }
    }

    fun resetAdmin(email: String) {
        viewModelScope.launch {
            try {
                val admin = schoolAdmins.find { it.email.equals(email, ignoreCase = true) }
                if (admin != null) {
                    repository.resetAdminPassword(admin.id, AdminResetRequest(admin.id, "123456"))
                    snackbarMessage.value = "Password reset."
                } else {
                    snackbarMessage.value = "Admin not found."
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error resetting password: ${e.localizedMessage}"
            }
        }
    }
    fun deleteAdmin(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.deleteSchoolAdmin(email)
                if (response.isSuccessful) {
                    schoolAdmins.removeAll { it.email == email }
                    snackbarMessage.value = "Admin deleted successfully"
                } else {
                    snackbarMessage.value = "Failed to delete admin"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error deleting admin: ${e.localizedMessage}"
            }
        }
    }

    fun clearSnackbarMessage() {
        snackbarMessage.value = null
    }
}

//OG
//data class SchoolAdmin(
//    val email: String,
//    val assignedSchool: String? = null,
//    var isBlocked: Boolean = false
//)
//
//class SchoolManagementViewModel : ViewModel() {
//
//    var searchQuery = mutableStateOf("")
//    var selectedRegion = mutableStateOf("ALL")
//    var selectedType = mutableStateOf("ALL")
//
//    val snackbarMessage = MutableStateFlow<String?>(null)
//
//    val schools = mutableStateListOf(
//        School("1", "Green Ivy High", "MOE1001", "KPSA1001", "CBC", "Public", "Secondary", "Mixed", "0700000001", "ivy@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Westlands", "Kangemi", "P.O. Box 123", "www.greenivy.ac.ke", false),
//        School("2", "St. Monica Academy", "MOE1002", "KPSA1002", "British", "Private", "Primary", "Girls", "0700000002", "monica@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Langata", "Karen", "P.O. Box 124", "www.stmonica.ac.ke", true),
//        School("3", "Hope Junior School", "MOE1003", "KPSA1003", "CBC", "Public", "Primary", "Mixed", "0700000003", "hope@edu.org", "Mombasa", "Coastal Diocese", "Mombasa", "Changamwe", "Port Reitz", "P.O. Box 125", "www.hopejunior.ac.ke", false),
//        School("4", "Future Stars Academy", "MOE1004", "KPSA1004", "IGCSE", "Private", "Secondary", "Boys", "0700000004", "stars@edu.org", "Kisumu", "Western Diocese", "Kisumu", "Kisumu East", "Manyatta", "P.O. Box 126", "www.futurestars.ac.ke", true),
//        School("5", "Golden Valley School", "MOE1005", "KPSA1005", "CBC", "Private", "Mixed", "Mixed", "0700000005", "golden@edu.org", "Eldoret", "Western Diocese", "Uasin Gishu", "Kapseret", "Langas", "P.O. Box 127", "www.goldenvalley.ac.ke", false),
//        School("6", "Coastal Springs", "MOE1006", "KPSA1006", "8-4-4", "Public", "Secondary", "Girls", "0700000006", "coast@edu.org", "Mombasa", "Coastal Diocese", "Mombasa", "Likoni", "Mtongwe", "P.O. Box 128", "www.coastalsprings.ac.ke", false),
//        School("7", "Urban Roots School", "MOE1007", "KPSA1007", "CBC", "Private", "Primary", "Mixed", "0700000007", "roots@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Embakasi", "Umoja", "P.O. Box 129", "www.urbanroots.ac.ke", false),
//        School("8", "Royal Crest Academy", "MOE1008", "KPSA1008", "British", "Private", "Mixed", "Boys", "0700000008", "royal@edu.org", "Nakuru", "Western Diocese", "Nakuru", "Nakuru East", "Kivumbini", "P.O. Box 130", "www.royalcrest.ac.ke", true),
//        School("9", "Northern Light High", "MOE1009", "KPSA1009", "CBC", "Public", "Secondary", "Mixed", "0700000009", "north@edu.org", "Garissa", "Northern Diocese", "Garissa", "Garissa Township", "Bura", "P.O. Box 131", "www.northernlight.ac.ke", true),
//        School("10", "Unity Scholars", "MOE1010", "KPSA1010", "IGCSE", "Private", "Mixed", "Mixed", "0700000010", "unity@edu.org", "Kisumu", "Western Diocese", "Kisumu", "Nyando", "Awasi", "P.O. Box 132", "www.unityscholars.ac.ke", false),
//        School("11", "Silver Oak School", "MOE1011", "KPSA1011", "8-4-4", "Public", "Primary", "Girls", "0700000011", "oak@edu.org", "Isiolo", "Northern Diocese", "Isiolo", "Isiolo North", "Kulamawe", "P.O. Box 133", "www.silveroak.ac.ke", true),
//        School("12", "Grace River High", "MOE1012", "KPSA1012", "CBC", "Private", "Secondary", "Boys", "0700000012", "grace@edu.org", "Eldoret", "Western Diocese", "Uasin Gishu", "Soy", "Ziwa", "P.O. Box 134", "www.graceriver.ac.ke", false),
//        School("13", "Beacon Hill Primary", "MOE1013", "KPSA1013", "8-4-4", "Public", "Primary", "Mixed", "0700000013", "beacon@edu.org", "Turkana", "Northern Diocese", "Turkana", "Lodwar", "Kakuma", "P.O. Box 135", "www.beaconhill.ac.ke", false),
//        School("14", "Nairobi East Academy", "MOE1014", "KPSA1014", "CBC", "Private", "Mixed", "Mixed", "0700000014", "east@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Embakasi East", "Donholm", "P.O. Box 136", "www.nairobiaeast.ac.ke", true),
//        School("15", "Southern View", "MOE1015", "KPSA1015", "British", "Private", "Secondary", "Girls", "0700000015", "south@edu.org", "Mombasa", "Coastal Diocese", "Mombasa", "Jomvu", "Miritini", "P.O. Box 137", "www.southernview.ac.ke", false),
//        School("16", "Western Springs", "MOE1016", "KPSA1016", "CBC", "Public", "Primary", "Mixed", "0700000016", "west@edu.org", "Nakuru", "Western Diocese", "Nakuru", "Rongai", "Menengai", "P.O. Box 138", "www.westernsprings.ac.ke", true),
//        School("17", "Lakeside Scholars", "MOE1017", "KPSA1017", "IGCSE", "Private", "Mixed", "Mixed", "0700000017", "lake@edu.org", "Kisumu", "Western Diocese", "Kisumu", "Muhoroni", "Ombeyi", "P.O. Box 139", "www.lakesidescholars.ac.ke", false),
//        School("18", "Hilltop Academy", "MOE1018", "KPSA1018", "8-4-4", "Public", "Secondary", "Boys", "0700000018", "hill@edu.org", "Eldoret", "Western Diocese", "Uasin Gishu", "Ainabkoi", "Kapsoya", "P.O. Box 140", "www.hilltopacademy.ac.ke", false),
//        School("19", "Forest Edge School", "MOE1019", "KPSA1019", "CBC", "Private", "Mixed", "Girls", "0700000019", "forest@edu.org", "Isiolo", "Northern Diocese", "Isiolo", "Isiolo South", "Modogashe", "P.O. Box 141", "www.forestedge.ac.ke", true),
//        School("20", "Starlight Primary", "MOE1020", "KPSA1020", "CBC", "Public", "Primary", "Mixed", "0700000020", "star@edu.org", "Garissa", "Northern Diocese", "Garissa", "Fafi", "Dadaab", "P.O. Box 142", "www.starlightprimary.ac.ke", false)
//    )
//    val countiesByRegion = mapOf(
//        "Coast" to listOf("Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta"),
//        "Eastern" to listOf("Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni"),
//        "Central" to listOf("Nyandarua", "Nyeri", "Kirinyaga", "Murang'a", "Kiambu"),
//        "North Eastern" to listOf("Garissa", "Wajir", "Mandera"),
//        "Nairobi" to listOf("Nairobi City"),
//        "Nyanza" to listOf("Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira"),
//        "Rift Valley" to listOf("Turkana", "West Pokot", "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo Marakwet",
//            "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet"),
//        "Western" to listOf("Kakamega", "Vihiga", "Bungoma", "Busia")
//    )
//
//    val schoolAdmins = mutableStateListOf(
//        SchoolAdmin(email = "lucy@edu.org"),
//        SchoolAdmin("mike@edu.org"),
//        SchoolAdmin("susan@edu.org")
//    )
//
//    fun assignAdmin(schoolName: String) {
//        val index = schools.indexOfFirst { it.name == schoolName }
//        if (index != -1) {
//            schools[index] = schools[index].copy(hasAdmin = true)
//        }
//    }
//
//    fun getSchoolByName(name: String): School? {
//        return schools.find { it.name == name }
//    }
//
//    fun unassignAdmin(adminEmail: String) {
//        val index = schoolAdmins.indexOfFirst { it.email == adminEmail }
//        if (index != -1) {
//            schoolAdmins[index] = schoolAdmins[index].copy(assignedSchool = null)
//        }
//    }
//
//    private fun assignAdminToSchool(adminEmail: String, schoolName: String) {
//        val index = schoolAdmins.indexOfFirst { it.email == adminEmail }
//        if (index != -1) {
//            schoolAdmins[index] = schoolAdmins[index].copy(assignedSchool = schoolName)
//        }
//    }
//
//    fun addAdmin(email: String): Boolean {
//        if (schoolAdmins.any { it.email.equals(email, ignoreCase = true) }) return false
//        schoolAdmins.add(SchoolAdmin(email))
//        return true
//    }
//
//    fun getUnassignedAdmins(): List<String> {
//        return schoolAdmins.filter { it.assignedSchool == null }.map { it.email }
//    }
//
//    fun reassignAdmin(email: String, schoolName: String) {
//        assignAdminToSchool(email, schoolName)
//    }
//
//    fun blockAdmin(email: String, block: Boolean) {
//        snackbarMessage.value = if (block) "Admin $email blocked" else "Admin $email unblocked"
//    }
//
//    fun resetAdmin(email: String) {
//        snackbarMessage.value = "Password reset for $email"
//    }
//
//    fun clearSnackbarMessage() {
//        snackbarMessage.value = null
//    }
//
//    fun deleteSchoolByName(name: String) {
//        val index = schools.indexOfFirst { it.name == name }
//        if (index != -1) {
//            schools.removeAt(index)
//            snackbarMessage.value = "School deleted"
//        } else {
//            snackbarMessage.value = "School not found"
//        }
//    }
//
//    fun updateSchool(id: Int, school: School) {
//        val index = schools.indexOfFirst { it.id == id.toString() }
//        if (index != -1) {
//            schools[index] = school.copy(id = id.toString())
//            snackbarMessage.value = "School updated"
//        } else {
//            snackbarMessage.value = "School not found"
//        }
//    }
//    fun addSchool(school: School, adminEmail: String) {
//        val exists = schools.any { it.name == school.name }
//        if (!exists) {
//            schools.add(school)
//            assignAdminToSchool(adminEmail, school.name)
//            assignAdmin(school.name)
//            snackbarMessage.value = "School added"
//        } else {
//            snackbarMessage.value = "School already exists"
//        }
//    }
//
//    fun fetchAdmins() {
//        snackbarMessage.value = "Admins refreshed"
//    }
//
//    fun fetchSchools() {
//        snackbarMessage.value = "Schools refreshed"
//    }
//
//    fun downloadSchoolTemplate(): Boolean {
//        snackbarMessage.value = "Template downloaded"
//        return true
//    }
//
//    fun importSchoolFile(fileName: String): Boolean {
//        snackbarMessage.value = "Imported school data from $fileName"
//        return true
//    }
//}
