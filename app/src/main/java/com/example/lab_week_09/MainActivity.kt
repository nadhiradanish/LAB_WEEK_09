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

//Here, we create a composable function called App
//This will be the root composable of the app
@Composable
fun App(navController: NavHostController) {
    //Here, we use NavHost to create a navigation graph
    //We pass the navController as a parameter
    //We also set the startDestination to "home"
    //This means that the app will start with the Home composable
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        //Here, we create a route called "home"
        //We pass the Home composable as a parameter
        //This means that when the app navigates to "home",
        //the Home composable will be displayed
        composable("home") {
            //Here, we pass a lambda function that navigates to "resultContent"
            Home { navController.navigate("resultContent/?listData=$it") }
        }

        //Here, we create a route called "resultContent"
        //We pass the ResultContent composable as a parameter
        //This means that when the app navigates to "resultContent",
        //the ResultContent composable will be displayed
        //You can also define arguments for the route
        //Here, we define a String argument called "listData"
        //We use navArgument to define the argument
        //We use NavType.StringType to define the type of the argument
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) {
            //Here, we pass the value of the argument to the ResultContent
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
            navigateFromHomeToResult(listData.joinToString { it.name })
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

//Here, we create a composable function called ResultContent
//ResultContent accepts a String parameter called listData from the Home
//then displays the value of listData to the screen
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Here, we call the OnBackgroundItemText UI Element
        OnBackgroundItemText(text = listData)
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
