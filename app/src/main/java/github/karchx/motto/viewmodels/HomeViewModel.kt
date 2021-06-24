package github.karchx.motto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.karchx.motto.model.storages.Constants
import github.karchx.motto.search_engine.citaty_info_website.data.Motto
import github.karchx.motto.search_engine.citaty_info_website.parsers.ByRandomMottosParser
import github.karchx.motto.search_engine.citaty_info_website.parsers.ByRequestMottosParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val byRandomParser = ByRandomMottosParser()

    private val _randomMottos = MutableLiveData<ArrayList<Motto>>().apply {
        GlobalScope.launch {
            putRandomMottosPostValue()
        }
    }
    private val _requestMottos = MutableLiveData<ArrayList<Motto>>().apply {}
    fun putRequestMottosPostValue(request: String) {
        val parser = ByRequestMottosParser(request)
        GlobalScope.launch {
            _requestMottos.postValue(parser.getMottos(Constants.QUANTITY_MOTTOS_IN_LIST))
        }
    }

    fun putRandomMottosPostValue() {
        GlobalScope.launch {
            _randomMottos.postValue(byRandomParser.getMottos(Constants.QUANTITY_MOTTOS_IN_LIST))
        }
    }

    val randomMottos: LiveData<ArrayList<Motto>> = _randomMottos
    val requestMottos: LiveData<ArrayList<Motto>> = _requestMottos
}