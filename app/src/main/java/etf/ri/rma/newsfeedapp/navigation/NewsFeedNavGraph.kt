package etf.ri.rma.newsfeedapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import etf.ri.rma.newsfeedapp.data.FilterData
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NewsFeedNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(
            route = "home?category={category}&startDate={startDate}&endDate={endDate}&unwanted={unwanted}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType; defaultValue = "Sve" },
                navArgument("startDate") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("endDate") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("unwanted") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Sve"
            val startDate = backStackEntry.arguments?.getString("startDate")
            val endDate = backStackEntry.arguments?.getString("endDate")
            val unwantedList = backStackEntry.arguments?.getString("unwanted")
                ?.split(',')?.filter { it.isNotBlank() } ?: emptyList()

            val filterData = FilterData(
                category = URLDecoder.decode(category, StandardCharsets.UTF_8.toString()),
                startDate = startDate?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) },
                endDate = endDate?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) },
                unwantedWords = unwantedList
            )

            NewsFeedScreen(
                navController = navController,
                filterData = filterData
            )
        }

        composable(
            route = "details/{id}?category={category}&startDate={startDate}&endDate={endDate}&unwanted={unwanted}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType; defaultValue = "Sve" },
                navArgument("startDate") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("endDate") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("unwanted") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            val category = backStackEntry.arguments?.getString("category") ?: "Sve"
            val startDate = backStackEntry.arguments?.getString("startDate")
            val endDate = backStackEntry.arguments?.getString("endDate")
            val unwanted = backStackEntry.arguments?.getString("unwanted")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

            val filterData = FilterData(
                category = URLDecoder.decode(category, StandardCharsets.UTF_8.toString()),
                startDate = startDate?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) },
                endDate = endDate?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) },
                unwantedWords = unwanted
            )

            val news = NewsData.getAllNews().firstOrNull { it.id == id }
            if (news != null) {
                NewsDetailsScreen(
                    news = news,
                    navController = navController,
                    filters = filterData
                )
            } else {
                Text("Vijest nije pronađena")
            }

        }

        composable(
            route = "filters?category={category}&startDate={startDate}&endDate={endDate}&unwanted={unwanted}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType; defaultValue = "Sve" },
                navArgument("startDate") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("endDate") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("unwanted") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Sve"
            val startDate = backStackEntry.arguments?.getString("startDate")
            val endDate = backStackEntry.arguments?.getString("endDate")
            val unwanted = backStackEntry.arguments?.getString("unwanted")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

            val filterData = FilterData(
                category = URLDecoder.decode(category, StandardCharsets.UTF_8.toString()),
                startDate = startDate?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) },
                endDate = endDate?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) },
                unwantedWords = unwanted
            )

            FilterScreen(
                navController = navController,
                initialFilter = filterData
            )
        }
    }
}