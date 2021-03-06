package github.karchx.motto.search_engine.citaty_info_website.parsers.by_sources

import github.karchx.motto.models.storages.Constants
import github.karchx.motto.search_engine.citaty_info_website.UIMotto
import github.karchx.motto.search_engine.citaty_info_website.items.Topic
import github.karchx.motto.search_engine.citaty_info_website.parsers.HtmlMottosParser
import github.karchx.motto.search_engine.citaty_info_website.parsers.MottosParser
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class ByTopicMottosParser(private val topic: Topic, private val shuffle: Boolean) : MottosParser {

    override fun getMottos(quantityMottos: Int): ArrayList<UIMotto> {
        val mottos = ArrayList<UIMotto>()
        val uriToParse = getUriToParse(topic)

        try {
            val okHttp = OkHttpClient()
            val request: Request = Request.Builder().url(uriToParse).get().build()
            val doc: Document = Jsoup.parse(okHttp.newCall(request).execute().body!!.string())
            val articles: Elements = doc.select(Constants.ARTICLE_ROOT_ELEMENT_NAME)

            val limitedQuantityMottos = getLimitedMottosQuantity(articles, quantityMottos)

            for (mottoIndex in 0 until limitedQuantityMottos) {
                try {
                    mottos.add(HtmlMottosParser.getMottoFromHtml(doc, mottoIndex))
                } catch (ex: Exception) {
                }
            }

            if (shuffle) {
                mottos.shuffle()
                return mottos
            }
            return mottos
        } catch (ex: Exception) {
            return mottos
        }
    }

    private fun getLimitedMottosQuantity(articles: Elements, quantityMottos: Int): Int {
        return if (articles.size < quantityMottos) articles.size
        else quantityMottos
    }

    private fun getUriToParse(topic: Topic): String {
        val baseUri = Constants.DOMAIN
        val topicUri = topic.topicUri
        val sortType = Constants.MOTTOS_SORT_TYPE
        return "$baseUri$topicUri$sortType"
    }
}
