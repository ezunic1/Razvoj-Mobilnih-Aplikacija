package etf.ri.rma.newsfeedapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.ui.theme.NewsFeedAppTheme
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsFeedAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NewsFeedAppLayout()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsFeedAppPreview() {
    NewsFeedAppTheme {
        NewsFeedAppLayout()
    }
}

@Composable
fun NewsFeedAppLayout() {
    //val newsList = remember { NewsData.getAllNews() }
    NewsFeedScreen()
}



