package com.widget.listenerscontainer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.widget.listenerscontainer.ui.theme.ListenersContainerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: AppViewModel by lazy {
            ViewModelProvider(this)[AppViewModel::class.java]
        }

        super.onCreate(savedInstanceState)
        setContent {
            ListenersContainerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Application(viewModel)
                }
            }
        }
    }
}

@Composable
fun Application(bridge: AppViewModel) {

    val itemsList = remember { mutableStateListOf<DataWrapper>() }
    val lazyListState = rememberLazyListState()

    backPressSupport()

    LaunchedEffect(Unit) {
        ChangesAdapter.newInstance(bridge)
        ChangesAdapter.instance?.registerListener(bridge)
        println("Application.LaunchedEffect: done")
    }

    LaunchedEffect(lazyListState.canScrollForward) {
        if (lazyListState.canScrollForward && itemsList.isNotEmpty()) {
            lazyListState.scrollToItem(itemsList.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ChangesAdapter.instance?.unregisterListener(bridge)
            println("Application.DisposableEffect: Cleaned up")
        }
    }

    MaterialTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val dataWrapper = DataWrapper()

                    println("dataWrapper->[${dataWrapper.uuid}:${dataWrapper.counter}]")

                    bridge.append(dataWrapper)
                    itemsList.add(dataWrapper)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            bottomBar = {
                AppBottomBar(bridge)
            }
        ) { innerPadding ->
            Column (
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyColumn(
                    modifier = Modifier.weight(10f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    state = lazyListState,
                ) {

                    items(itemsList) { item ->
                        WidgetCard(model = bridge, itemsList = itemsList, item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetCard(model: AppViewModel, itemsList: SnapshotStateList<DataWrapper>, item: DataWrapper) {
    println(" [WC] ${item.uuid} ${item.counter}")

    val counters by model.container.collectAsState()
    val isRun : Boolean by model.isStarted.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        println("WidgetCard.LaunchedEffect: $item.uuid")
    }

    DisposableEffect(Unit) {
        onDispose {
            println("WidgetCard.DisposableEffect: $item.uuid")
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRun) MaterialTheme.colorScheme.background else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
    ) {
        ItalicText(text = item.uuid,
            modifier = Modifier.padding(start = 8.dp))

        HorizontalLine(color = Color.Black)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            ItalicText(text = compose(item.uuid, counters),
                modifier = Modifier.padding(start = 72.dp))

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    showDeleteDialog = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(SwitchDefaults.IconSize)
                )
            }
        }
    }

    if (showDeleteDialog) {
        showAlertDialog(
            title = "Delete [${item.uuid}]",
            text = "Are you sure delete?",
            onDismiss = { showDeleteDialog = false },
            onOkClick = {
                Log.d("Back", "delete item")
                itemsList.remove(item)
                model.delete(item)
            }
        )
    }
}

@Composable
fun HorizontalLine(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

@Composable
fun ItalicText(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp),
        modifier = modifier
    )
}

fun compose(uuid: String, counters: List<DataWrapper>): String {
    var result = ""
    counters.forEach{
        if (uuid == it.uuid) {
            result = it.counter.toString()
            return result
        }
    }
    return result
}

@Composable
fun AppBottomBar(viewModel: AppViewModel) {

    var isStarted by remember { mutableStateOf(false) }

    BottomAppBar(
        containerColor = Color.LightGray,
        modifier = Modifier.height(56.dp)
    ) {
        // Spacer to push the Button to the center
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                isStarted = !isStarted
                if (isStarted) {
                    viewModel.setStart(true)
                    ChangesAdapter.instance?.start()
                }
                else {
                    viewModel.setStart(false)
                    ChangesAdapter.instance?.stop()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.LightGray),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) // Adjust padding as needed
        ) {
            Text(if (isStarted) "Stop" else "Start")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
