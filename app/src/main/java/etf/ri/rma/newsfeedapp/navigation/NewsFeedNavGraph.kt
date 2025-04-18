package etf.ri.rma.newsfeedapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NewsFeedNavGraph(startText: String? = null) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(
            route = "home?category={category}&startDate={startDate}&endDate={endDate}&unwanted={unwanted}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = "Sve"
                },
                navArgument("startDate") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("endDate") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("unwanted") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryEncoded = backStackEntry.arguments?.getString("category") ?: "Sve"
            val startDateEncoded = backStackEntry.arguments?.getString("startDate")
            val endDateEncoded = backStackEntry.arguments?.getString("endDate")
            val unwantedEncoded = backStackEntry.arguments?.getString("unwanted")

            val category = URLDecoder.decode(categoryEncoded, StandardCharsets.UTF_8.toString())
            val startDate = startDateEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val endDate = endDateEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val unwantedString = unwantedEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val unwantedList = unwantedString?.split(',')?.filter { it.isNotBlank() } ?: emptyList()

            NewsFeedScreen(
                navController = navController,
                initialSearch = startText ?: "",
                currentCategory = category,
                currentStartDate = startDate,
                currentEndDate = endDate,
                currentUnwantedWords = unwantedList
            )
        }

        composable(
            "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val news = NewsData.getAllNews().firstOrNull { it.id == id }

            if (news != null) {
                NewsDetailsScreen(news = news, navController = navController)
            } else {
                Text(text = "Vijest nije pronađena")
            }
        }

        composable(
            route = "filters?category={category}&startDate={startDate}&endDate={endDate}&unwanted={unwanted}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = "Sve"
                },
                navArgument("startDate") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("endDate") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("unwanted") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryEncoded = backStackEntry.arguments?.getString("category") ?: "Sve"
            val startDateEncoded = backStackEntry.arguments?.getString("startDate")
            val endDateEncoded = backStackEntry.arguments?.getString("endDate")
            val unwantedEncoded = backStackEntry.arguments?.getString("unwanted")

            val category = URLDecoder.decode(categoryEncoded, StandardCharsets.UTF_8.toString())
            val startDate = startDateEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val endDate = endDateEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val unwantedString = unwantedEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val unwantedList = unwantedString?.split(',')?.filter { it.isNotBlank() } ?: emptyList()

            FilterScreen(
                navController = navController,
                initialSearch = startText ?: "",
                initialCategory = category,
                initialStartDate = startDate,
                initialEndDate = endDate,
                initialUnwantedWords = unwantedList
            )
        }
    }
}
