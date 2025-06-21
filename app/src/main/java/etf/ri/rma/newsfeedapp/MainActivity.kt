package etf.ri.rma.newsfeedapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.ui.theme.NewsFeedAppTheme
import etf.ri.rma.newsfeedapp.navigation.NewsFeedNavGraph
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var newsDAO: NewsDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = NewsDatabase.getDatabase(applicationContext)
        val savedNewsDAO = database.savedNewsDAO()
        newsDAO = NewsDAO(savedNewsDAO)

        lifecycleScope.launch {
            val sve = savedNewsDAO.allNews()
            Log.d("Baza", "Ukupno vijesti: ${sve.size}")
            for (news in sve) {
                Log.d("Baza", "Vijest: ${news.title}")
            }
        }

        setContent {
            NewsFeedAppTheme {
                NewsFeedNavGraph(newsDAO = newsDAO)
            }
        }
    }
}
