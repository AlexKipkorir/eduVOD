package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.eduvod.model.School

class SchoolManagementViewModel : ViewModel() {

    val schools = mutableStateListOf(
        School("Green Ivy High", "MOE1001", "KPSA1001", "CBC", "Public", "Secondary", "Mixed", "0700000001", "ivy@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Westlands", "Kangemi", "P.O. Box 123", "www.greenivy.ac.ke", false),
        School("St. Monica Academy", "MOE1002", "KPSA1002", "British", "Private", "Primary", "Girls", "0700000002", "monica@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Langata", "Karen", "P.O. Box 124", "www.stmonica.ac.ke", true),
        School("Hope Junior School", "MOE1003", "KPSA1003", "CBC", "Public", "Primary", "Mixed", "0700000003", "hope@edu.org", "Mombasa", "Coastal Diocese", "Mombasa", "Changamwe", "Port Reitz", "P.O. Box 125", "www.hopejunior.ac.ke", false),
        School("Future Stars Academy", "MOE1004", "KPSA1004", "IGCSE", "Private", "Secondary", "Boys", "0700000004", "stars@edu.org", "Kisumu", "Western Diocese", "Kisumu", "Kisumu East", "Manyatta", "P.O. Box 126", "www.futurestars.ac.ke", true),
        School("Golden Valley School", "MOE1005", "KPSA1005", "CBC", "Private", "Mixed", "Mixed", "0700000005", "golden@edu.org", "Eldoret", "Western Diocese", "Uasin Gishu", "Kapseret", "Langas", "P.O. Box 127", "www.goldenvalley.ac.ke", false),
        School("Coastal Springs", "MOE1006", "KPSA1006", "8-4-4", "Public", "Secondary", "Girls", "0700000006", "coast@edu.org", "Mombasa", "Coastal Diocese", "Mombasa", "Likoni", "Mtongwe", "P.O. Box 128", "www.coastalsprings.ac.ke", false),
        School("Urban Roots School", "MOE1007", "KPSA1007", "CBC", "Private", "Primary", "Mixed", "0700000007", "roots@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Embakasi", "Umoja", "P.O. Box 129", "www.urbanroots.ac.ke", false),
        School("Royal Crest Academy", "MOE1008", "KPSA1008", "British", "Private", "Mixed", "Boys", "0700000008", "royal@edu.org", "Nakuru", "Western Diocese", "Nakuru", "Nakuru East", "Kivumbini", "P.O. Box 130", "www.royalcrest.ac.ke", true),
        School("Northern Light High", "MOE1009", "KPSA1009", "CBC", "Public", "Secondary", "Mixed", "0700000009", "north@edu.org", "Garissa", "Northern Diocese", "Garissa", "Garissa Township", "Bura", "P.O. Box 131", "www.northernlight.ac.ke", true),
        School("Unity Scholars", "MOE1010", "KPSA1010", "IGCSE", "Private", "Mixed", "Mixed", "0700000010", "unity@edu.org", "Kisumu", "Western Diocese", "Kisumu", "Nyando", "Awasi", "P.O. Box 132", "www.unityscholars.ac.ke", false),
        School("Silver Oak School", "MOE1011", "KPSA1011", "8-4-4", "Public", "Primary", "Girls", "0700000011", "oak@edu.org", "Isiolo", "Northern Diocese", "Isiolo", "Isiolo North", "Kulamawe", "P.O. Box 133", "www.silveroak.ac.ke", true),
        School("Grace River High", "MOE1012", "KPSA1012", "CBC", "Private", "Secondary", "Boys", "0700000012", "grace@edu.org", "Eldoret", "Western Diocese", "Uasin Gishu", "Soy", "Ziwa", "P.O. Box 134", "www.graceriver.ac.ke", false),
        School("Beacon Hill Primary", "MOE1013", "KPSA1013", "8-4-4", "Public", "Primary", "Mixed", "0700000013", "beacon@edu.org", "Turkana", "Northern Diocese", "Turkana", "Lodwar", "Kakuma", "P.O. Box 135", "www.beaconhill.ac.ke", false),
        School("Nairobi East Academy", "MOE1014", "KPSA1014", "CBC", "Private", "Mixed", "Mixed", "0700000014", "east@edu.org", "Nairobi", "Nairobi Diocese", "Nairobi", "Embakasi East", "Donholm", "P.O. Box 136", "www.nairobiaeast.ac.ke", true),
        School("Southern View", "MOE1015", "KPSA1015", "British", "Private", "Secondary", "Girls", "0700000015", "south@edu.org", "Mombasa", "Coastal Diocese", "Mombasa", "Jomvu", "Miritini", "P.O. Box 137", "www.southernview.ac.ke", false),
        School("Western Springs", "MOE1016", "KPSA1016", "CBC", "Public", "Primary", "Mixed", "0700000016", "west@edu.org", "Nakuru", "Western Diocese", "Nakuru", "Rongai", "Menengai", "P.O. Box 138", "www.westernsprings.ac.ke", true),
        School("Lakeside Scholars", "MOE1017", "KPSA1017", "IGCSE", "Private", "Mixed", "Mixed", "0700000017", "lake@edu.org", "Kisumu", "Western Diocese", "Kisumu", "Muhoroni", "Ombeyi", "P.O. Box 139", "www.lakesidescholars.ac.ke", false),
        School("Hilltop Academy", "MOE1018", "KPSA1018", "8-4-4", "Public", "Secondary", "Boys", "0700000018", "hill@edu.org", "Eldoret", "Western Diocese", "Uasin Gishu", "Ainabkoi", "Kapsoya", "P.O. Box 140", "www.hilltopacademy.ac.ke", false),
        School("Forest Edge School", "MOE1019", "KPSA1019", "CBC", "Private", "Mixed", "Girls", "0700000019", "forest@edu.org", "Isiolo", "Northern Diocese", "Isiolo", "Isiolo South", "Modogashe", "P.O. Box 141", "www.forestedge.ac.ke", true),
        School("Starlight Primary", "MOE1020", "KPSA1020", "CBC", "Public", "Primary", "Mixed", "0700000020", "star@edu.org", "Garissa", "Northern Diocese", "Garissa", "Fafi", "Dadaab", "P.O. Box 142", "www.starlightprimary.ac.ke", false)
    )


    fun assignAdmin(schoolName: String) {
        val index = schools.indexOfFirst { it.name == schoolName }
        if (index != -1) {
            schools[index] = schools[index].copy(hasAdmin = true)
        }
    }

    fun getSchoolByName(name: String): School? {
        return schools.find { it.name == name }
    }
}