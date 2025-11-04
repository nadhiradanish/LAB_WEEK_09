package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : ComponentActivity() {

    // Data class untuk item Student
    data class Student(
        var name: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home { navController.navigate("resultContent/?listData=$it") }
        }

        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) {
            ResultContent(it.arguments?.getString("listData").orEmpty())
        }
    }
}

@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {
    val listData = remember {
        mutableStateListOf(
            MainActivity.Student("Tanu"),
            MainActivity.Student("Tina"),
            MainActivity.Student("Tono")
        )
    }

    var inputField by remember { mutableStateOf(MainActivity.Student("")) }

    // 🔹 Setup Moshi untuk konversi ke JSON
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(List::class.java, MainActivity.Student::class.java)
    val adapter = moshi.adapter<List<MainActivity.Student>>(type)

    HomeContent(
        listData = listData,
        inputField = inputField,
        onInputValueChange = { input -> inputField = inputField.copy(name = input) },
        onButtonClick = {
            if (inputField.name.isNotBlank()) {
                listData.add(inputField)
                inputField = MainActivity.Student("")
            }
        },
        navigateFromHomeToResult = {
            // 🔹 Ubah listData ke JSON
            val jsonList = adapter.toJson(listData)
            navigateFromHomeToResult(jsonList)
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<MainActivity.Student>,
    inputField: MainActivity.Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(
                    text = stringResource(id = R.string.enter_item)
                )
                TextField(
                    value = inputField.name,
                    onValueChange = { onInputValueChange(it) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text("Enter item name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click),
                        onClick = onButtonClick
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_navigate),
                        onClick = navigateFromHomeToResult
                    )
                }
            }
        }
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// 🔹 Sekarang ResultContent menampilkan daftar nama hasil parsing JSON
@Composable
fun ResultContent(listData: String) {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(List::class.java, MainActivity.Student::class.java)
    val adapter = moshi.adapter<List<MainActivity.Student>>(type)

    // Parse JSON ke list object
    val studentList = remember(listData) {
        adapter.fromJson(listData) ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OnBackgroundTitleText("Student List")
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(studentList) { student ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    OnBackgroundItemText(text = student.name)
                }
            }
        }
    }
}

@Composable
fun OnBackgroundTitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun PrimaryTextButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun OnBackgroundItemText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    Home {}
}
