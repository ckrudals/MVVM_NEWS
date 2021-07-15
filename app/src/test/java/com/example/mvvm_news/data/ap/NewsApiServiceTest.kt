package com.example.mvvm_news.data.ap

import com.example.mvvm_news.data.api.NewsApiInterface
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.text.Charsets.UTF_8


// test 서버와 통신이 잘되는지 테스트
class NewsApiServiceTest {

    private lateinit var service: NewsApiInterface
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiInterface::class.java)
    }


    private fun enqueueMockResponse(
        fileName: String
    ) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockResponse.setBody(source.readString(Charsets.UTF_8))
        server.enqueue(mockResponse)
    }

    @Test
    fun getTopHeadlines_sendRequest_receivedExpected() {
        runBlocking {
            enqueueMockResponse("newsresponse.json")
            val responseBody = service.getTopHeadlines("us", 1).body()
            val request = server.takeRequest()
            assertThat(responseBody).isNotNull()
            assertThat(request.path).isEqualTo("/v2/top-headlines?country=us&page=1&apiKey=e024f486089e4493b040879919b421ab")

        }

    }

    @Test
    fun getTopHeadlines_receviedResponse_correctPageSize(){
        runBlocking {
            enqueueMockResponse("newsresponse.json")
            val responseBody = service.getTopHeadlines("us", 1).body()
            val articlesList=responseBody!!.articles
            assertThat(articlesList.size).isEqualTo(20)
        }
    }
    @Test
    fun getTopHeadlines_receviedResponse_correctContent(){
        runBlocking {
            enqueueMockResponse("newsresponse.json")
            val responseBody = service.getTopHeadlines("us", 1).body()
            val articlesList=responseBody!!.articles
          val article=articlesList[0]
            assertThat(article.author).isEqualTo("Hunter Felt")
            assertThat(article.url).isEqualTo("https://www.theguardian.com/sport/live/2021/jul/14/nba-finals-2021-game-4-phoenix-suns-v-milwaukee-bucks-live")
            assertThat(article.publishedAt).isEqualTo("2021-07-15T00:43:38Z")
        }
    }


    @After
    fun tearDown() {
        server.shutdown()
    }
}