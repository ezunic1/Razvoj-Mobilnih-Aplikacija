package etf.ri.rma.newsfeedapp.data.network

import android.util.Log
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.network.NewsAPI
import etf.ri.rma.newsfeedapp.network.dto.ApiNewsItem
import etf.ri.rma.newsfeedapp.network.dto.NewsApiResponse
import java.util.*

class NewsDAO {

    private val allNews = LinkedHashMap<String, NewsItem>() // Čuvam sve vijesti po uuid-u
    private val newsOrder = mutableListOf<String>() // Redoslijed prikaza vijesti
    private val categoryTimestamps = mutableMapOf<String, Long>() // Kada je koja kategorija zadnji put dohvaćena
    private val featuredCache = mutableMapOf<String, List<String>>() // UUID-ovi vijesti koje su featured
    private val similarNewsCache = mutableMapOf<String, List<NewsItem>>() // Keširane slične vijesti

    private var apiService: NewsApiService
    private var apiKey: String
    private val locale = "us"

    init {
        apiService = NewsAPI.service
        apiKey = "m4ZntisA5vHPdHIzkrv44iv71l0BgwGoGxSNtWs3"
        val initialNews = listOf(
            NewsItem(
                "fa69da54-922d-4854-9b18-88d1f7b173fc",
                "Something remarkable is happening with violent crime rates in the US",
                "America could have its lowest murder rate on record in 2025 and nobody seems to notice",
                "https://platform.vox.com/wp-content/uploads/sites/2/2025/05/GettyImages-1329585810.jpg?quality=90&strip=all&crop=0%2C10.732984293194%2C100%2C78.534031413613&w=1200",
                "politics",
                false,
                "vox.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "43a9bb01-cc49-4296-a8bf-13f4170bee35",
                "Why Is Zach Gilford Team Craig Conover Over Paige DeSorbo After Split?",
                "'Criminal Minds' star Zach Gilford exclusively told Us Weekly his Bravo hot takes as a fan of reality TV",
                "https://www.usmagazine.com/wp-content/uploads/2025/05/Why-Is-Zach-Gilford-Team-Craig-Conover-Over-Team-Paige-DeSorbo-After-Their-Split.jpg?crop=0px%2C25px%2C2000px%2C1051px&resize=1200%2C630&quality=86&strip=all",
                "entertainment",
                false,
                "usmagazine.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "22826c00-e748-45c6-ab89-43a21b4e1d77",
                "Gerry Turner’s Candid Quotes About His Cancer Diagnosis",
                "Golden Bachelor Gerry Turner reflects on his life with an incurable cancer diagnosis, sharing insights on his journey and health updates",
                "https://www.usmagazine.com/wp-content/uploads/2025/05/GettyImages-1783584690-Gerry-Turners-Candid-Quotes-About-His-Cancer-Journey-.jpg?crop=0px%2C108px%2C1333px%2C699px&resize=1200%2C630&quality=86&strip=all",
                "entertainment",
                false,
                "usmagazine.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "96bd58be-2e79-43ac-bec3-abc68ff1f171",
                "Demerger To Bring Strategic Clarity, Long-Term Returns For Shareholders: Tata Motors Chairman",
                "The proposed demerger of Tata Motors into two listed entities will bring strategic clarity, enabling long-term returns for shareholders",
                "https://media.assettype.com/bloombergquint%2F2025-05-10%2F0i7yzr7y%2FSanand-scaled.jpg?w=1200&auto=format%2Ccompress&ogImage=true",
                "business",
                false,
                "bloombergquint.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "30451809-4edc-446d-99f6-607c733f86a3",
                "NTPC Q4 Results: Profit Rises 23%, Revenue Up 4.6%",
                "NTPC Ltd. posted a rise in consolidated net profits in the fourth quarter of financial year 2025",
                "https://media.assettype.com/bloombergquint%2F2025-05-24%2Fj85sa508%2FNTPCwebsite.jpg?w=1200&auto=format%2Ccompress&ogImage=true",
                "business",
                false,
                "bloombergquint.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "afbce5e0-2714-4f43-9ec6-fa3b43aca6f1",
                "Yunus To Continue As Bangladesh’s Interim Government Chief: Adviser",
                "Muhammad Yunus će i dalje biti na čelu Bangladeške privremene vlade, rekao je savjetnik",
                "https://media.assettype.com/bloombergquint%2F2025-04-03%2F88d8ijhs%2FYunus-Xi-Jinping.jpg?w=1200&auto=format%2Ccompress&ogImage=true",
                "business",
                false,
                "bloombergquint.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "05040542-f1ae-40b6-af8f-4aa1d7f2eca1",
                "They Were So Influential, Paving the Way for the Rise of Guns N' Roses: So Why Have Most Americans Never Heard of Them",
                "As early as the 1970s, this band has been playing sleazy street-level rock.",
                "https://www.ultimate-guitar.com/static/article/news/2/178892_0_wide_ver1748073673.jpg",
                "entertainment",
                false,
                "ultimate-guitar.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "a18ab948-f17f-4098-b964-789c191df9ac",
                "King Charles' Canada Tour Has Donald Trump Hanging Over It",
                "King Charles III će otvoriti kanadski parlament u povijesnoj ceremoniji “jubileja”",
                "https://biztoc.com/cdn/d79f7ca30678bc47_s.webp",
                "entertainment",
                false,
                "upstract.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "37555907-4e98-4f8b-bc1c-cf93ad298a13",
                "A US Navy captain tells BI his heart was racing when his warship came under Houthi fire for the first time",
                "USS Stockdale je jedan od mnogih američkih ratnih brodova koje su Houthi snage napale od kraja 2023.",
                "https://biztoc.com/cdn/5e9a772242eff6da_s.webp",
                "general",
                false,
                "upstract.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "453fb895-2348-4464-b55a-ac072ca192dd",
                "MP Tejasvi Surya presents 15-point action plan to transform Bengaluru",
                "BJP MP Tejasvi Surya predložio 15 koraka za modernizaciju infrastrukture Bengaluru, upravljanje prometom i javni prijevoz",
                "https://img.etimg.com/thumb/msid-121380393,width-1200,height-630,imgsize-78462,overlay-economictimes/articleshow.jpg",
                "politics",
                false,
                "economictimes.indiatimes.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "596e87e3-5daf-427a-9472-2faa3dfb53a8",
                "UPSC CSE Prelims 2025 Live Updates: Check Key Guidelines, Marking Scheme",
                "UPSC će održati preliminarni ispit 25. svibnja 2025.",
                "https://c.ndtvimg.com/2025-05/s94sfk9o_upsc-prelims-2024-image_625x300_24_May_25.jpeg",
                "general",
                false,
                "ndtv.com",
                "24-05-2025",
                arrayListOf()
            ),
            NewsItem(
                "c9c5f62f-1c28-4680-9463-1d906f78a449",
                "Work-life balance: NVIDIA CEO's 7-day grind gets Paytm's Vijay Shekhar Sharma's stamp of approval",
                "Paytm CEO Vijay Shekhar Sharma podijelio video o radnom ritmu NVIDIA CEO-ja",
                "https://www.livemint.com/lm-img/img/2025/05/24/1600x900/nvidia_ceo_merge_1748087683405_1748087689708.jpg",
                "sports",
                false,
                "livemint.com",
                "24-05-2025",
                arrayListOf()
            )
        )
        initialNews.forEach { insertOnTop(it) }


    }

    private fun insertOnTop(item: NewsItem) {
        allNews[item.uuid] = item
        newsOrder.remove(item.uuid)
        newsOrder.add(0, item.uuid) // Dodajem vijest na početak liste
    }

    fun setApiService(service: NewsApiService) {
        // Resetujem sve kad postavljam novi API servis
        allNews.clear()
        newsOrder.clear()
        categoryTimestamps.clear()
        featuredCache.clear()
        similarNewsCache.clear()
        apiService = service
    }

    fun setApiKey(token: String) {
        apiKey = token // Omogućavam promjenu API ključa
    }

    suspend fun getTopStoriesByCategory(category: String, limit: Int = 3): List<NewsItem> {
        val now = System.currentTimeMillis()
        val lastFetched = categoryTimestamps[category] ?: 0L
        val existing = allNews.values.filter { it.category.equals(category, ignoreCase = true) }

        if (now - lastFetched < 30_000 && existing.isNotEmpty()) {
            // Ako je poziv urađen unutar 30 sekundi, vraćam postojeće vijesti
            val featuredIds = featuredCache[category].orEmpty()
            val featuredNews = existing.filter { it.uuid in featuredIds }
                .map { it.copy(isFeatured = true) }
            val standardNews = existing.filterNot { it.uuid in featuredIds }
                .map { it.copy(isFeatured = false) }
            return featuredNews + standardNews
        }

        return try {
            // Dohvaćam vijesti sa web servisa
            val response: NewsApiResponse = apiService.getTopNews(apiKey, locale, category, limit) /// OVO je bitno, ovako i za ostale linkove
            val newItems = response.data?.map { it.toNewsItem(forcedCategory = category) } ?: emptyList()
            val newFeatured = mutableListOf<NewsItem>()
            for (item in newItems) {
                val prepared = item.copy(isFeatured = true)
                insertOnTop(prepared) // Ubacujem nove vijesti kao featured
                newFeatured.add(prepared)
            }

            categoryTimestamps[category] = now
            featuredCache[category] = newFeatured.map { it.uuid }

            val nonFeatured = newsOrder
                .mapNotNull { allNews[it] }
                .filter {
                    it.category.equals(category, ignoreCase = true) &&
                            it.uuid !in featuredCache[category].orEmpty()
                }
                .map { it.copy(isFeatured = false) }

            newFeatured + nonFeatured // Vraćam nove featured + stare kao obične vijesti
        } catch (e: Exception) {
            existing // Ako poziv ne uspije, vraćam prethodne vijesti
        }
    }

    fun getAllStories(): List<NewsItem> {
        return newsOrder.mapNotNull { allNews[it] } // Vraćam sve vijesti redoslijedom ubacivanja
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> {
        if (!uuid.matches(Regex("^[a-fA-F0-9\\-]{36}|uuid-[0-9]+\$"))) {
            throw InvalidUUIDException() // Validiram uuid
        }

        similarNewsCache[uuid]?.let {
            return it // Ako sam već dohvaćao, koristim keširane slične vijesti
        }

        return try {
            val response = apiService.getSimilarNews(uuid, apiKey, "en")
            val result = response.data
                ?.map { it.toNewsItem() }
                ?.filter { it.uuid != uuid }
                ?.take(2) ?: emptyList()

            result.forEach {
                if (!allNews.containsKey(it.uuid)) {
                    insertOnTop(it) // Ubacujem slične vijesti ako ih već nemam
                    Log.d("NewsDAO", "Inserted:: ${it.uuid} ${it.category}")
                }
            }

            similarNewsCache[uuid] = result
            result
        } catch (e: Exception) {
            emptyList() // Ako poziv ne uspije, vraćam praznu listu
        }
    }

    private fun ApiNewsItem.toNewsItem(forcedCategory: String? = null): NewsItem {
        // Pretvaram API model u aplikacijski model
        val chosenCategory = forcedCategory ?: (categories.firstOrNull() ?: "Ostalo")
        return NewsItem(
            uuid = uuid,
            title = title,
            snippet = description ?: "",
            imageUrl = image_url,
            category = chosenCategory,
            isFeatured = false,
            source = source,
            publishedDate = published_at.take(10).split("-").reversed().joinToString("-"),
            imageTags = arrayListOf()
        )
    }

    /*suspend fun getSimilarWithSource(uuidnews: String, sourceId: String): List<String> {
        return try {
            val response = apiService.getSimilarWithSource(apiKey, uuidnews, sourceId)
            response.data?.mapNotNull { it.title } ?: emptyList()
        } catch (e: Exception) {
            Log.e("NewsDAO", "Greška prilikom dohvatanja naslova po izvoru: $e")
            emptyList()
        }
    }*/

    suspend fun getSimilarWithSource(uuidNews: String, source: String): String? {
        /*return try {
            val response = apiService.getSimilarWithSource(
                uuid = uuidNews,
                apiKey = apiKey,
                language = "en",
                limit = 5
            )
            response.data
                ?.firstOrNull { it.source == source }
                ?.url
                ?: run {
                    Log.e("NewsDAO", "No similar news from the same source")
                    null
                }
        } catch (e: Exception) {
            Log.e("NewsDAO", "Error fetching similar news: $e")
            null
        }*/

        try {
            UUID.fromString(uuidNews)
        }catch (e: IllegalArgumentException) {
            throw InvalidUUIDException()
        }
        val response = apiService.getSimilarWithSource(
            uuid = uuidNews,
            apiKey = apiKey,
            limit = 5
        )
        val similar = response.data?.firstOrNull { it.source == source }
        return similar?.url
    }

}