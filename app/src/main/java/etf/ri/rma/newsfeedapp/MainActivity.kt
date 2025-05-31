package etf.ri.rma.newsfeedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.ui.theme.NewsFeedAppTheme
import etf.ri.rma.newsfeedapp.navigation.NewsFeedNavGraph

class MainActivity : ComponentActivity() {


    private val newsDAO = NewsDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NewsFeedAppTheme {
                NewsFeedNavGraph(newsDAO = newsDAO)
            }
        }
    }
}
