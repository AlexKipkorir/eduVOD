package com.example.eduvod.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.isPopupLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.repositories.SystemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

//OG
//class SystemConfigViewModel : ViewModel() {
//
//    val types = mutableStateListOf("Primary", "Secondary", "Mixed")
//    val categories = mutableStateListOf("Public", "Private")
//    val curriculums = mutableStateListOf("CBC", "8-4-4", "British", "IGSE")
//    val regions = mutableStateListOf(
//        "Coastal Region", "Eastern Region", "Central Region",
//        "North Eastern Region", "Nairobi Region", "Nyanza Region",
//        "Rift Valley Region", "Western Region"
//    )
//    val counties = mutableStateListOf<String>()
//    val subcounties = mutableStateListOf<String>()
//
//    val selectedRegion = MutableStateFlow("")
//    val selectedCounty = MutableStateFlow("")
//
//    private val regionToCounties = mapOf(
//        "Coastal Region" to listOf("Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita-Taveta"),
//        "Eastern Region" to listOf("Garissa", "Wajir", "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni"),
//        "Central Region" to listOf("Nyandarua", "Nyeri", "Kirinyaga", "Murang’a", "Kiambu"),
//        "North Eastern Region" to listOf("Garissa", "Wajir", "Mandera"),
//        "Nairobi Region" to listOf("Nairobi"),
//        "Nyanza Region" to listOf("Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira"),
//        "Rift Valley Region" to listOf("Turkana", "West Pokot", "Samburu", "Trans-Nzoia", "Uasin Gishu", "Elgeyo-Marakwet", "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet"),
//        "Western Region" to listOf("Kakamega", "Vihiga", "Bungoma", "Busia")
//    )
//
//    private val countyToSubcounties = mapOf(
//        "Baringo" to listOf("Baringo Central", "Baringo North", "Baringo South", "Eldama Ravine", "Mogotio", "Tiaty"),
//        "Bomet" to listOf("Bomet Central", "Bomet East", "Chepalungu", "Konoin", "Sotik"),
//        "Bungoma" to listOf("Bumula", "Kabuchai", "Kanduyi", "Kimilil", "Mt Elgon", "Sirisia", "Tongaren", "Webuye East", "Webuye West"),
//        "Busia" to listOf("Budalangi", "Butula", "Funyula", "Nambele", "Teso North", "Teso South"),
//        "Elgeyo-Marakwet" to listOf("Keiyo North", "Keiyo South", "Marakwet East", "Marakwet West"),
//        "Embu" to listOf("Manyatta", "Mbeere North", "Mbeere South", "Runyenjes"),
//        "Garissa" to listOf("Daadab", "Fafi", "Garissa Township", "Hulugho", "Ijara", "Lagdera", "Balambala"),
//        "Homa Bay" to listOf("Homabay Town", "Kabondo", "Karachwonyo", "Kasipul", "Mbita", "Ndhiwa", "Rangwe", "Suba"),
//        "Isiolo" to listOf("Isiolo", "Merti", "Garbatulla"),
//        "Kajiado" to listOf("Isinya", "Kajiado Central", "Kajiado North", "Loitokitok", "Mashuuru"),
//        "Kakamega" to listOf("Butere", "Kakamega Central", "Kakamega East", "Kakamega North", "Kakamega South", "Khwisero", "Lugari", "Lukuyani", "Lurambi", "Matete", "Mumias", "Mutungu", "Navakholo"),
//        "Kericho" to listOf("Ainamoi", "Belgut", "Bureti", "Kipkelion East", "Kipkelion West", "Soin/Sigowet"),
//        "Kiambu" to listOf("Gatundu North", "Gatundu South", "Githunguri", "Juja", "Kabete", "Kiambaa", "Kiambu", "Kikuyu", "Limuru", "Ruiru", "Thika Town", "Lari"),
//        "Kilifi" to listOf("Ganze", "Kaloleni", "Kilifi North", "Kilifi South", "Magarini", "Malindi", "Rabai"),
//        "Kirinyaga" to listOf("Kirinyaga Central", "Kirinyaga East", "Kirinyaga West", "Mwea East", "Mwea West"),
//        "Kisumu" to listOf("Kisumu Central", "Kisumu East", "Kisumu West", "Muhoroni", "Nyakach", "Nyando", "Seme"),
//        "Kitui" to listOf("Kitui West", "Kitui Central", "Kitui Rural", "Kitui South", "Kitui East", "Mwingi North", "Mwingi West", "Mwingi Central"),
//        "Kwale" to listOf("Kinango", "Lunga Lunga", "Msambweni", "Matuga"),
//        "Laikipia" to listOf("Laikipia Central", "Laikipia East", "Laikipia North", "Laikipia West", "Nyahururu"),
//        "Lamu" to listOf("Lamu East", "Lamu West"),
//        "Machakos" to listOf("Kathiani", "Machakos Town", "Masinga", "Matungulu", "Mavoko", "Mwala", "Yatta"),
//        "Makueni" to listOf("Kaiti", "Kibwezi West", "Kibwezi East", "Kilome", "Makueni", "Mbooni"),
//        "Mandera" to listOf("Banissa", "Lafey", "Mandera East", "Mandera North", "Mandera South", "Mandera West"),
//        "Marsabit" to listOf("Laisamis", "Moyale", "North Hor", "Saku"),
//        "Meru" to listOf("Buuri", "Igembe Central", "Igembe North", "Igembe South", "Imenti Central", "Imenti North", "Imenti South", "Tigania East", "Tigania West"),
//        "Migori" to listOf("Awendo", "Kuria East", "Kuria West", "Mabera", "Ntimaru", "Rongo", "Suna East", "Suna West", "Uriri"),
//        "Mombasa" to listOf("Changamwe", "Jomvu", "Kisauni", "Likoni", "Mvita", "Nyali"),
//        "Murang’a" to listOf("Gatanga", "Kahuro", "Kandara", "Kangema", "Kigumo", "Kiharu", "Mathioya", "Murang’a South"),
//        "Nairobi" to listOf("Dagoretti North", "Dagoretti South", "Embakasi Central", "Embakasi East", "Embakasi North", "Embakasi South", "Embakasi West", "Kamukunji", "Kasarani", "Kibra", "Lang’ata", "Makadara", "Mathare", "Roysambu", "Ruaraka", "Starehe", "Westlands"),
//        "Nakuru" to listOf("Bahati", "Gilgil", "Kuresoi North", "Kuresoi South", "Molo", "Naivasha", "Nakuru Town East", "Nakuru Town West", "Njoro", "Rongai", "Subukia"),
//        "Nandi" to listOf("Aldai", "Chesumei", "Emgwen", "Mosop", "Nandi Hills", "Tindiret"),
//        "Narok" to listOf("Narok East", "Narok North", "Narok South", "Narok West", "Transmara East", "Transmara West"),
//        "Nyamira" to listOf("Borabu", "Manga", "Masaba North", "Nyamira North", "Nyamira South"),
//        "Nyandarua" to listOf("Kinangop", "Kipipiri", "Ndaragwa", "Ol-Kalou", "Ol Joro Orok"),
//        "Nyeri" to listOf("Kieni East", "Kieni West", "Mathira East", "Mathira West", "Mukurweini", "Nyeri Town", "Othaya", "Tetu"),
//        "Samburu" to listOf("Samburu East", "Samburu North", "Samburu West"),
//        "Siaya" to listOf("Alego Usonga", "Bondo", "Gem", "Rarieda", "Ugenya", "Unguja"),
//        "Taita-Taveta" to listOf("Mwatate", "Taveta", "Voi", "Wundanyi"),
//        "Tana River" to listOf("Bura", "Galole", "Garsen"),
//        "Tharaka-Nithi" to listOf("Tharaka North", "Tharaka South", "Chuka", "Igambang’ombe", "Maara", "Chiakariga", "Muthambi"),
//        "Trans-Nzoia" to listOf("Cherangany", "Endebess", "Kiminini", "Kwanza", "Saboti"),
//        "Turkana" to listOf("Loima", "Turkana Central", "Turkana East", "Turkana North", "Turkana South"),
//        "Uasin Gishu" to listOf("Ainabkoi", "Kapseret", "Kesses", "Moiben", "Soy", "Turbo"),
//        "Vihiga" to listOf("Emuhaya", "Hamisi", "Luanda", "Sabatia", "Vihiga"),
//        "Wajir" to listOf("Eldas", "Tarbaj", "Wajir East", "Wajir North", "Wajir South", "Wajir West"),
//        "West Pokot" to listOf("Central Pokot", "North Pokot", "Pokot South", "West Pokot")
//    )
//
//    val snackbarMessage = MutableStateFlow<String?>(null)
//
//    init {
//        loadAll()
//        loadRegions()
//    }
//
//    private fun loadAll() {
//    }
//
//    private fun loadRegions() {
//    }
//
//    fun loadCounties(region: String) {
//        selectedRegion.value = region
//        viewModelScope.launch {
//            counties.clear()
//            counties.addAll(regionToCounties[region] ?: emptyList())
//            subcounties.clear()
//        }
//    }
//
//    fun loadSubcounties(county: String) {
//        selectedCounty.value = county
//        viewModelScope.launch {
//            subcounties.clear()
//            subcounties.addAll(countyToSubcounties[county] ?: emptyList())
//        }
//    }
//
//    fun sectionList(section: String): SnapshotStateList<String> {
//        return when (section) {
//            "School Type" -> types
//            "School Category" -> categories
//            "Curriculum" -> curriculums
//            "Region / Diocese" -> regions
//            else -> mutableStateListOf()
//        }
//    }
//
//    fun addItem(section: String, value: String) {
//        val list = sectionList(section)
//        viewModelScope.launch {
//            if (!list.contains(value)) {
//                list.add(value)
//
//                if (section == "Region / Diocese") {
//                    regionToCounties[value]?.forEach { county ->
//                        if (!counties.contains(county)) counties.add(county)
//                        countyToSubcounties[county]?.forEach { subcounty ->
//                            if (!subcounties.contains(subcounty)) subcounties.add(subcounty)
//                        }
//                    }
//                }
//            } else {
//                snackbarMessage.value = "$value already exists in $section"
//            }
//        }
//    }
//
//    fun updateItem(section: String, oldValue: String, newValue: String) {
//        val list = sectionList(section)
//        viewModelScope.launch {
//            val index = list.indexOf(oldValue)
//            if (index != -1 && !list.contains(newValue)) {
//                list[index] = newValue
//            } else {
//                snackbarMessage.value = "Failed to update $section"
//            }
//        }
//    }
//
//    fun deleteItem(section: String, value: String) {
//        val list = sectionList(section)
//        viewModelScope.launch {
//            list.remove(value)
//        }
//    }
//
//    fun clearSnackbar() {
//        snackbarMessage.value = null
//    }
//}


//Retrofit
class SystemConfigViewModel(
    private val repository: SystemRepository = SystemRepository()
) : ViewModel() {

    val types = mutableStateListOf<String>()
    val categories = mutableStateListOf<String>()
    val curriculums = mutableStateListOf<String>()
    val regions = mutableStateListOf<String>()

    val counties = mutableStateListOf<String>()
    val subcounties = mutableStateListOf<String>()

    val selectedRegion = MutableStateFlow("")
    val selectedCounty = MutableStateFlow("")

    val snackbarMessage = MutableStateFlow<String?>(null)

    private val regionToCounties = mapOf(
        "Coastal Region" to listOf("Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita-Taveta"),
        "Eastern Region" to listOf("Garissa", "Wajir", "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni"),
        "Central Region" to listOf("Nyandarua", "Nyeri", "Kirinyaga", "Murang’a", "Kiambu"),
        "North Eastern Region" to listOf("Garissa", "Wajir", "Mandera"),
        "Nairobi Region" to listOf("Nairobi"),
        "Nyanza Region" to listOf("Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira"),
        "Rift Valley Region" to listOf("Turkana", "West Pokot", "Samburu", "Trans-Nzoia", "Uasin Gishu", "Elgeyo-Marakwet", "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet"),
        "Western Region" to listOf("Kakamega", "Vihiga", "Bungoma", "Busia")
    )

    private val countyToSubcounties = mapOf(
        "Baringo" to listOf("Baringo Central", "Baringo North", "Baringo South", "Eldama Ravine", "Mogotio", "Tiaty"),
        "Bomet" to listOf("Bomet Central", "Bomet East", "Chepalungu", "Konoin", "Sotik"),
        "Bungoma" to listOf("Bumula", "Kabuchai", "Kanduyi", "Kimilil", "Mt Elgon", "Sirisia", "Tongaren", "Webuye East", "Webuye West"),
        "Busia" to listOf("Budalangi", "Butula", "Funyula", "Nambele", "Teso North", "Teso South"),
        "Elgeyo-Marakwet" to listOf("Keiyo North", "Keiyo South", "Marakwet East", "Marakwet West"),
        "Embu" to listOf("Manyatta", "Mbeere North", "Mbeere South", "Runyenjes"),
        "Garissa" to listOf("Daadab", "Fafi", "Garissa Township", "Hulugho", "Ijara", "Lagdera", "Balambala"),
        "Homa Bay" to listOf("Homabay Town", "Kabondo", "Karachwonyo", "Kasipul", "Mbita", "Ndhiwa", "Rangwe", "Suba"),
        "Isiolo" to listOf("Isiolo", "Merti", "Garbatulla"),
        "Kajiado" to listOf("Isinya", "Kajiado Central", "Kajiado North", "Loitokitok", "Mashuuru"),
        "Kakamega" to listOf("Butere", "Kakamega Central", "Kakamega East", "Kakamega North", "Kakamega South", "Khwisero", "Lugari", "Lukuyani", "Lurambi", "Matete", "Mumias", "Mutungu", "Navakholo"),
        "Kericho" to listOf("Ainamoi", "Belgut", "Bureti", "Kipkelion East", "Kipkelion West", "Soin/Sigowet"),
        "Kiambu" to listOf("Gatundu North", "Gatundu South", "Githunguri", "Juja", "Kabete", "Kiambaa", "Kiambu", "Kikuyu", "Limuru", "Ruiru", "Thika Town", "Lari"),
        "Kilifi" to listOf("Ganze", "Kaloleni", "Kilifi North", "Kilifi South", "Magarini", "Malindi", "Rabai"),
        "Kirinyaga" to listOf("Kirinyaga Central", "Kirinyaga East", "Kirinyaga West", "Mwea East", "Mwea West"),
        "Kisumu" to listOf("Kisumu Central", "Kisumu East", "Kisumu West", "Muhoroni", "Nyakach", "Nyando", "Seme"),
        "Kitui" to listOf("Kitui West", "Kitui Central", "Kitui Rural", "Kitui South", "Kitui East", "Mwingi North", "Mwingi West", "Mwingi Central"),
        "Kwale" to listOf("Kinango", "Lunga Lunga", "Msambweni", "Matuga"),
        "Laikipia" to listOf("Laikipia Central", "Laikipia East", "Laikipia North", "Laikipia West", "Nyahururu"),
        "Lamu" to listOf("Lamu East", "Lamu West"),
        "Machakos" to listOf("Kathiani", "Machakos Town", "Masinga", "Matungulu", "Mavoko", "Mwala", "Yatta"),
        "Makueni" to listOf("Kaiti", "Kibwezi West", "Kibwezi East", "Kilome", "Makueni", "Mbooni"),
        "Mandera" to listOf("Banissa", "Lafey", "Mandera East", "Mandera North", "Mandera South", "Mandera West"),
        "Marsabit" to listOf("Laisamis", "Moyale", "North Hor", "Saku"),
        "Meru" to listOf("Buuri", "Igembe Central", "Igembe North", "Igembe South", "Imenti Central", "Imenti North", "Imenti South", "Tigania East", "Tigania West"),
        "Migori" to listOf("Awendo", "Kuria East", "Kuria West", "Mabera", "Ntimaru", "Rongo", "Suna East", "Suna West", "Uriri"),
        "Mombasa" to listOf("Changamwe", "Jomvu", "Kisauni", "Likoni", "Mvita", "Nyali"),
        "Murang’a" to listOf("Gatanga", "Kahuro", "Kandara", "Kangema", "Kigumo", "Kiharu", "Mathioya", "Murang’a South"),
        "Nairobi" to listOf("Dagoretti North", "Dagoretti South", "Embakasi Central", "Embakasi East", "Embakasi North", "Embakasi South", "Embakasi West", "Kamukunji", "Kasarani", "Kibra", "Lang’ata", "Makadara", "Mathare", "Roysambu", "Ruaraka", "Starehe", "Westlands"),
        "Nakuru" to listOf("Bahati", "Gilgil", "Kuresoi North", "Kuresoi South", "Molo", "Naivasha", "Nakuru Town East", "Nakuru Town West", "Njoro", "Rongai", "Subukia"),
        "Nandi" to listOf("Aldai", "Chesumei", "Emgwen", "Mosop", "Nandi Hills", "Tindiret"),
        "Narok" to listOf("Narok East", "Narok North", "Narok South", "Narok West", "Transmara East", "Transmara West"),
        "Nyamira" to listOf("Borabu", "Manga", "Masaba North", "Nyamira North", "Nyamira South"),
        "Nyandarua" to listOf("Kinangop", "Kipipiri", "Ndaragwa", "Ol-Kalou", "Ol Joro Orok"),
        "Nyeri" to listOf("Kieni East", "Kieni West", "Mathira East", "Mathira West", "Mukurweini", "Nyeri Town", "Othaya", "Tetu"),
        "Samburu" to listOf("Samburu East", "Samburu North", "Samburu West"),
        "Siaya" to listOf("Alego Usonga", "Bondo", "Gem", "Rarieda", "Ugenya", "Unguja"),
        "Taita-Taveta" to listOf("Mwatate", "Taveta", "Voi", "Wundanyi"),
        "Tana River" to listOf("Bura", "Galole", "Garsen"),
        "Tharaka-Nithi" to listOf("Tharaka North", "Tharaka South", "Chuka", "Igambang’ombe", "Maara", "Chiakariga", "Muthambi"),
        "Trans-Nzoia" to listOf("Cherangany", "Endebess", "Kiminini", "Kwanza", "Saboti"),
        "Turkana" to listOf("Loima", "Turkana Central", "Turkana East", "Turkana North", "Turkana South"),
        "Uasin Gishu" to listOf("Ainabkoi", "Kapseret", "Kesses", "Moiben", "Soy", "Turbo"),
        "Vihiga" to listOf("Emuhaya", "Hamisi", "Luanda", "Sabatia", "Vihiga"),
        "Wajir" to listOf("Eldas", "Tarbaj", "Wajir East", "Wajir North", "Wajir South", "Wajir West"),
        "West Pokot" to listOf("Central Pokot", "North Pokot", "Pokot South", "West Pokot")
    )

    init {
        loadAll()
        loadRegions()
    }

    fun sectionList(section: String): MutableList<String> {
        return when (section) {
            "School Type" -> types
            "School Category" -> categories
            "Curriculum" -> curriculums
            "Region / Diocese" -> regions
            else -> mutableStateListOf()
        }
    }

    private fun loadAll() {
        loadConfig("School Type", types)
        loadConfig("School Category", categories)
        loadConfig("Curriculum", curriculums)
    }

    private fun loadConfig(section: String, list: MutableList<String>) {
        viewModelScope.launch {
            try {
                val fetched = repository.fetchSystemConfig(section)
                list.clear()
                list.addAll(fetched)
            } catch (e: Exception) {
                snackbarMessage.value = "Failed to load $section: ${e.localizedMessage}"
            }
        }
    }

    private fun loadRegions() {
        viewModelScope.launch {
            try {
                val fetched = repository.fetchRegions()
                regions.clear()
                regions.addAll(fetched)
            } catch (e: Exception) {
                snackbarMessage.value = "Failed to load regions: ${e.localizedMessage}"
            }
        }
    }

    fun loadCounties(region: String) {
        selectedRegion.value = region
        viewModelScope.launch {
            try {
                val fetched = repository.fetchCounties(region)
                counties.clear()
                counties.addAll(fetched)
                subcounties.clear()
            } catch (e: Exception) {
                snackbarMessage.value = "Failed to load counties: ${e.localizedMessage}"
            }
        }
    }

    fun loadSubcounties(county: String) {
        selectedCounty.value = county
        viewModelScope.launch {
            try {
                val fetched = repository.fetchSubcounties(county)
                subcounties.clear()
                subcounties.addAll(fetched)
            } catch (e: Exception) {
                snackbarMessage.value = "Failed to load subcounties: ${e.localizedMessage}"
            }
        }
    }

    fun addItem(section: String, value: String) {
        val list = sectionList(section)
        viewModelScope.launch {
            try {
                val success = repository.addSystemConfig(section, value)
                if (success) {
                    list.add(value)

                    if (section == "Region / Diocese") {
                        regionToCounties[value]?.forEach { county ->
                            val countySuccess = repository.addSystemConfig("County", county)
                            if (countySuccess && !counties.contains(county)) {
                                counties.add(county)
                            }

                            countyToSubcounties[county]?.forEach { subcounty ->
                                val subSuccess = repository.addSystemConfig("Subcounty", subcounty)
                                if (subSuccess && !subcounties.contains(subcounty)) {
                                    subcounties.add(subcounty)
                                }
                            }
                        }
                    }

                } else {
                    snackbarMessage.value = "Failed to add item to $section"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error adding to $section: ${e.localizedMessage}"
            }
        }
    }

    fun updateItem(section: String, oldValue: String, newValue: String) {
        val list = sectionList(section)
        viewModelScope.launch {
            try {
                val success = repository.updateSystemConfig(section, oldValue, newValue)
                if (success) {
                    val index = list.indexOf(oldValue)
                    if (index != -1) {
                        list[index] = newValue
                    }
                } else {
                    snackbarMessage.value = "Failed to update $section"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error updating $section: ${e.localizedMessage}"
            }
        }
    }

    fun deleteItem(section: String, value: String) {
        val list = sectionList(section)
        viewModelScope.launch {
            try {
                val success = repository.deleteSystemConfig(section, value)
                if (success) {
                    list.remove(value)
                } else {
                    snackbarMessage.value = "Failed to delete item from $section"
                }
            } catch (e: Exception) {
                snackbarMessage.value = "Error deleting from $section: ${e.localizedMessage}"
            }
        }
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }
}
