package etf.ri.rma.newsfeedapp

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.ui.theme.NewsFeedAppTheme
import etf.ri.rma.newsfeedapp.navigation.NewsFeedNavGraph





class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)
        //enableEdgeToEdge()
        setContent {
            NewsFeedAppTheme {
                NewsFeedNavGraph(startText = sharedText)
            }
        }
    }
}
/*
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

*/

