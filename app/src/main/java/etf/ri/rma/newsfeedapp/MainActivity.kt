package etf.ri.rma.newsfeedapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import etf.ri.rma.newsfeedapp.model.ui.theme.NewsFeedAppTheme
import etf.ri.rma.newsfeedapp.navigation.NewsFeedNavGraph





class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            NewsFeedAppTheme {
                NewsFeedNavGraph()
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

