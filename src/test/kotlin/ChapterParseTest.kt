
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

//data class Chapter(val book: String, val id: Int, val category: String, val verses: ArrayList<Verse>)
data class Verse(val book: String, val chapter: Int, val no: Int, val chunks: ArrayList<Chunk>)
data class Chunk(val eText: String, val kText: String, val audio: String)

class ChapterParseTest {

    private val driver: WebDriver = chrome()

    private fun chrome(): WebDriver {
        WebDriverManager.chromedriver().setup();
        return ChromeDriver();
    }

    @BeforeTest
    fun setup() {
        driver.get("http://www.webstone.kr/ap_shop/um_webstone/content_view.php?category=D16168185609")
    }

    @Test
    fun categoryExtractTest()  {
        var chapters = HashMap<Int, String>()

        val chapDiv = driver.findElement(By.id("div_chapter"))
        val els = chapDiv.findElements(By.xpath("//td[@width='21']"))
        for (el in els) {
            val chapNo = el.text.toInt()
            val category = el.getAttribute("onclick").subSequence(13..24).toString()
//            val chapter = Chapter("Genesis", chapNo, category)
            chapters[chapNo] = category
//            println("Genesis %2d %2s".format(chapNo, category))
        }
        for ((k,v) in chapters) {
            println("Genesis %2d %2s".format(k, v))
        }
    }

    @Test
    fun textExtractTest()  {
        val book = "Genesis"
        val chapter = 1
        val chapDiv = driver.findElement(By.id("div_chapter"))
        val els = chapDiv.findElements(By.xpath("//td[@width='45']"))
        val verses = ArrayList<Verse>()
        for (el in els) {
            val chunks = ArrayList<Chunk>()
            val verseNo = el.text.toInt()
            val textEls = el.findElements(By.xpath("following-sibling::td//div"))
            for (e in textEls) {
                val eText = e.findElement(By.className("conEngTx")).text
                val kEl = e.findElement(By.className("conKorTx"))
                val kText = kEl.text
                val audio = kEl.findElement(By.cssSelector("span[onclick]")).getAttribute("onclick").split("'")[1].split("_")[1]
                val chunk = Chunk(eText, kText, audio)
                chunks.add(chunk)
//                println("$verseNo $eText $kText")
            }
            val verse = Verse(book, chapter, verseNo, chunks)
            verses.add(verse)
        }
        for (v in verses) {
            println("%s %2d %2d %s".format(v.book, v.chapter, v.no, v.chunks))
        }
    }

    @AfterTest
    fun tearDown() {
        driver.quit()
    }
}