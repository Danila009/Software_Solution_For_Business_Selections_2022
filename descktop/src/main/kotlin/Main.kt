// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.Api
import api.model.Department
import api.model.Employee
import api.model.Post
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.resourcesFetcher
import io.kamel.image.lazyImageResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainScreen()
    }
}

@ExperimentalMaterialApi
@Composable
private fun MainScreen(

){
    val api = Api()

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    var employees by rememberSaveable { mutableStateOf(emptyList<Employee>()) }
    var departments by rememberSaveable { mutableStateOf(emptyList<Department>()) }

    var search by rememberSaveable { mutableStateOf("") }

    var sortingDepartment:Department? by rememberSaveable { mutableStateOf(null) }
    var sortingPost:Post? by rememberSaveable { mutableStateOf(null) }
    var sortingDepartmentCheck by rememberSaveable { mutableStateOf(false) }
    var sortingPostCheck by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(
        key1 = search,
        key2 = sortingDepartment,
        key3 = sortingPost
    ){
        try {
            employees = emptyList()
            delay(2000L)
            employees = api.getAllEmployees(
                search = search,
                positionId = sortingDepartment?.id,
                postId = sortingPost?.id
            )
        }catch (_:Exception) {
            employees = emptyList()
            delay(2000L)
            employees = api.getAllEmployees(
                search = search,
                positionId = sortingDepartment?.id,
                postId = sortingPost?.id
            )
        }
    }

    LaunchedEffect(key1 = Unit){
        try {
            departments = api.getDepartment()
        }catch (_:Exception){}
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF23282D)
            ){
                Row {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        label = {
                            Text(
                                text = "Search",
                                color = Color.White
                            )
                        }
                    )

                    Column {
                        TextButton(
                            onClick = {
                                sortingDepartmentCheck = true
                            }
                        ){
                            Text("Sorting отделам")
                        }

                        TextButton(
                            onClick = {
                                sortingPostCheck = true
                            }
                        ){
                            Text("Sorting должностям")
                        }
                    }
                }
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF23282D)
        ){

            if(sortingDepartmentCheck){
                SortingDepartment(
                    onDismissRequest = { sortingDepartmentCheck = false }
                ) {
                    sortingDepartment = it
                }
            }

            if(sortingPostCheck){
                SortingPost(
                    onDismissRequest = { sortingPostCheck = false }
                ) {
                    sortingPost = it
                }
            }


            Column {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    scrollState.scrollBy(-delta)
                                }
                            },
                        )
                ) {
                    item {
                        Row {
                            Column {
                                if (departments.isEmpty()){
                                    CircularProgressIndicator()
                                }else {
                                    Card(
                                        modifier = Modifier
                                            .padding(10.dp),
                                        shape = AbsoluteRoundedCornerShape(20.dp),
                                        backgroundColor = Color(0xFF191E23)
                                    ) {
                                        Column {
                                            repeat(departments.size){ index ->
                                                val item = departments[index]

                                                var expandedState by remember { mutableStateOf(false) }

                                                val check = rememberSaveable{ mutableStateOf(false) }

                                                Box {
                                                    Dialog(
                                                        check = check,
                                                        id = item.id
                                                    )
                                                }


                                                Card(
                                                    modifier = Modifier
                                                        .padding(10.dp)
                                                        .animateContentSize(
                                                            animationSpec = tween(
                                                                durationMillis = 300,
                                                                easing = LinearOutSlowInEasing
                                                            )
                                                        ),
                                                    onClick = {
                                                        expandedState = !expandedState
                                                    },
                                                    backgroundColor = Color(0xFF7A8A99)
                                                ) {
                                                    Column {

                                                        Text(
                                                            text = item.name,
                                                            fontWeight = FontWeight.W900,
                                                            modifier = Modifier
                                                                .padding(5.dp),
                                                            color = Color.White
                                                        )

                                                        if (expandedState) {
                                                            var departmentItem:Department? by rememberSaveable{
                                                                mutableStateOf(null)
                                                            }

                                                            Button(
                                                                onClick = {
                                                                    check.value = true
                                                                }
                                                            ){
                                                                Text("Сотрудники")
                                                            }

                                                            item.departmentId?.let { id ->
                                                                coroutineScope.launch {
                                                                    try {
                                                                        delay(2000L)
                                                                        departmentItem = api.getDepartmentById(id)
                                                                    }catch (_:Exception){}
                                                                }
                                                            }

                                                            departmentItem?.let {
                                                                Text(
                                                                    text = departmentItem!!.name
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Column {
                                if (employees.isEmpty()){
                                    CircularProgressIndicator()
                                }else {
                                    repeat(employees.size){ index ->
                                        val item = employees[index]

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            shape = AbsoluteRoundedCornerShape(20.dp),
                                            backgroundColor = Color(0xFF191E23)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    text = "${item.firstName} ${item.lastName} ${item.lastPatronomic}",
                                                    fontWeight = FontWeight.W900,
                                                    modifier = Modifier
                                                        .padding(5.dp),
                                                    color = Color.White
                                                )

                                                Text(
                                                    text = "Phone: ${item.phone}",
                                                    modifier = Modifier
                                                        .padding(5.dp),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
private fun SortingDepartment(
    onDismissRequest:() -> Unit,
    onClick:(Department?) -> Unit
){
    val scope = rememberCoroutineScope()
    val api = Api()

    var department by rememberSaveable { mutableStateOf(emptyList<Department>()) }

    scope.launch {
        department = api.getDepartment()
    }


    AlertDialog(
        backgroundColor = Color(0xFF23282D),
        onDismissRequest = {
            onDismissRequest()
        },
        buttons = {
            Column {

                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(20.dp),
                    backgroundColor = Color(0xFF191E23)
                ) {
                    TextButton(
                        onClick = {
                            onClick(null)
                            onDismissRequest()
                        }
                    ){
                        Text(
                            text = "All"
                        )
                    }
                }

                if (department.isEmpty()){
                    CircularProgressIndicator()
                }

                repeat(department.size){
                    val item = department[it]
                    Card(
                        modifier = Modifier
                            .padding(5.dp),
                        shape = AbsoluteRoundedCornerShape(20.dp),
                        backgroundColor = Color(0xFF191E23)
                    ) {
                        TextButton(
                            onClick = {
                                onClick(item)
                                onDismissRequest()
                            }
                        ){
                            Text(
                                text = item.name
                            )
                        }
                    }
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun SortingPost(
    onDismissRequest:() -> Unit,
    onClick:(Post?) -> Unit
){
    val scope = rememberCoroutineScope()
    val api = Api()

    var department by rememberSaveable { mutableStateOf(emptyList<Post>()) }

    scope.launch {
        department = api.getPost()
    }


    AlertDialog(
        backgroundColor = Color(0xFF23282D),
        onDismissRequest = {
            onDismissRequest()
        },
        buttons = {
            Column {

                Card(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = AbsoluteRoundedCornerShape(20.dp),
                    backgroundColor = Color(0xFF191E23)
                ) {
                    TextButton(
                        onClick = {
                            onClick(null)
                            onDismissRequest()
                        }
                    ){
                        Text(
                            text = "All"
                        )
                    }
                }

                if (department.isEmpty()){
                    CircularProgressIndicator()
                }

                repeat(department.size){
                    val item = department[it]
                    Card(
                        modifier = Modifier
                            .padding(5.dp),
                        shape = AbsoluteRoundedCornerShape(20.dp),
                        backgroundColor = Color(0xFF191E23)
                    ) {
                        TextButton(
                            onClick = {
                                onClick(item)
                                onDismissRequest()
                            }
                        ){
                            Text(
                                text = item.name
                            )
                        }
                    }
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun Dialog(
    check:MutableState<Boolean>,
    id:Int
){
    if (check.value){

        val api = Api()
        val coroutineScope = rememberCoroutineScope()

        var employeesDepartment by rememberSaveable{ mutableStateOf(
            emptyList<Employee>()
        ) }

        coroutineScope.launch {
            try {
                delay(2000L)
                employeesDepartment = api.getAllEmployees(positionId = id)
            }catch (_:Exception){

            }
        }

        AlertDialog(
            backgroundColor = Color(0xFF23282D),
            modifier = Modifier
                .fillMaxWidth(0.5f),
            onDismissRequest = {
                check.value = false
            },
            buttons = {
                Column {
                    if (employeesDepartment.isEmpty()){
                        CircularProgressIndicator()
                    }
                    repeat(employeesDepartment.size){
                        val item = employeesDepartment[it]
                        Card(
                            modifier = Modifier
                                .padding(5.dp),
                            shape = AbsoluteRoundedCornerShape(20.dp),
                            backgroundColor = Color(0xFF191E23)
                        ) {
                            Row {

//                                val desktopConfig = KamelConfig {
//                                    takeFrom(KamelConfig.Default)
//                                    resourcesFetcher() // Available only on Desktop.
//                                }
//
////                                CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
////                                    lazyImageResource(data = "" +
////                                            "http://localhost:8080/photo?fio=Авдеева Альжбета Игнатьевна")
////                                }


                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                ) {
                                    Text(
                                        text = "${item.firstName} ${item.lastName} ${item.lastPatronomic}",
                                        fontWeight = FontWeight.W900,
                                        modifier = Modifier
                                            .padding(5.dp),
                                        color = Color.White
                                    )

                                    Text(
                                        text = "Phone: ${item.phone}",
                                        modifier = Modifier
                                            .padding(5.dp),
                                        color = Color.White
                                    )

                                    Text(
                                        text = "Post: ${item.post.name}",
                                        modifier = Modifier
                                            .padding(5.dp),
                                        color = Color.White
                                    )

                                    Text(
                                        text = "Position: ${item.post.position.name}",
                                        modifier = Modifier
                                            .padding(5.dp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}