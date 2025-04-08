package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {

    fun getAllNews(): List<NewsItem> {
        return listOf(

            NewsItem("1", "Novi zakon o radu usvojen u Skupštini", "Zakon donosi brojne izmjene koje se tiču radnog vremena i prava radnika.", null, "Politika", true, "Klix.ba", "03.04.2025"),
            NewsItem("2", "Povećanje minimalne plate u FBiH", "Nakon višemjesečnih pregovora, postignut je dogovor.", null, "Politika", true, "Klix.ba", "02.04.2025"),
            NewsItem("3", "Vlada FBiH usvojila budžet za 2025.", "U fokusu budžeta su zdravstvo i obrazovanje.", null, "Politika", false, "Klix.ba", "01.04.2025"),
            NewsItem("4", "BiH dobija nove mjere energetske efikasnosti", "Vlada najavila subvencije za izolaciju i grijanje.", null, "Politika", false, "Klix.ba", "31.03.2025"),
            NewsItem("5", "Predstavnički dom izglasao novi set zakona", "Zakoni se odnose na poreske olakšice za firme koje zapošljavaju mlade.", null, "Politika", false, "Klix.ba", "30.03.2025"),
            NewsItem("6", "Zmajevi pobijedili na domaćem terenu", "Reprezentacija BiH savladala je protivnika rezultatom 3:1.", null, "Sport", false, "SportSport.ba", "02.04.2025"),
            NewsItem("7", "Premijer liga BiH: Derbi završio bez pobjednika", "Sarajevo i Željezničar odigrali neriješeno 1:1.", null, "Sport", false, "Klix.ba", "01.04.2025"),
            NewsItem("8", "BiH u četvrtfinalu Evropskog prvenstva", "Sjajna utakmica bh. rukometaša protiv Slovenije.", null, "Sport", true, "Klix.ba", "31.03.2025"),
            NewsItem("9", "Novi rekordi na atletskom mitingu u Zenici", "Mladi takmičari briljirali u disciplini 800 metara.", null, "Sport", false, "Klix.ba", "30.03.2025"),
            NewsItem("10", "Košarkaši Bosne plasirali se u finale Kupa", "Pobjeda protiv Slobode rezultatom 82:77.", null, "Sport", false, "Klix.ba", "29.03.2025"),
            NewsItem("11", "Otvoren novi tehnološki centar u Sarajevu", "Centar će omogućiti nova radna mjesta i razvoj softverske industrije.", null, "Nauka/tehnologija", true, "Al Jazeera Balkans", "01.04.2025"),
            NewsItem("12", "Startup iz BiH razvija aplikaciju za učenje jezika", "Aplikacija koristi AI kako bi pomogla korisnicima u učenju stranih jezika.", null, "Nauka/tehnologija", true, "Start.ba", "01.04.2025"),
            NewsItem("13", "Nova IT konferencija u Mostaru", "Govoriće se o vještačkoj inteligenciji i blockchainu.", null, "Nauka/tehnologija", true, "Klix.ba", "31.03.2025"),
            NewsItem("14", "UNSA pokreće program robotike", "Studenti će imati priliku raditi s najsavremenijim robotima.", null, "Nauka/tehnologija", false, "Klix.ba", "30.03.2025"),
            NewsItem("15", "Mladi istraživači iz BiH osvojili međunarodnu nagradu", "Projekt iz oblasti biotehnologije prepoznat u Londonu.", null, "Nauka/tehnologija", false, "Klix.ba", "29.03.2025"),
            NewsItem("16", "Inovatori iz Tuzle predstavili pametni semafor", "Semafor prilagođava dužinu trajanja u realnom vremenu.", null, "Nauka/tehnologija", false, "Klix.ba", "28.03.2025"),
            NewsItem("17", "BiH postaje član evropske mreže za istraživanje", "Usklađivanje naučnih politika sa EU standardima.", null, "Nauka/tehnologija", false, "Klix.ba", "27.03.2025"),
            NewsItem("18", "Učenici iz BiH razvili mobilnu aplikaciju za slabovide", "Aplikacija koristi tekst-to-speech tehnologiju.", null, "Nauka/tehnologija", true, "Klix.ba", "26.03.2025"),
            NewsItem("19", "Mladi programeri iz Sarajeva kreiraju novu edukativnu igru", "Igra će biti besplatna za sve osnovne škole.", null, "Nauka/tehnologija", false, "Klix.ba", "25.03.2025"),
            NewsItem("20", "BiH tim ide na svjetsku olimpijadu iz informatike", "Nakon odličnih rezultata na regionalnom takmičenju.", null, "Nauka/tehnologija", true, "Klix.ba", "24.03.2025")

        )
    }
}
