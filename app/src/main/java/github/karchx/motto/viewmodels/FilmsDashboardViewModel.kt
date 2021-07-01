package github.karchx.motto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.karchx.motto.models.storages.Constants
import github.karchx.motto.models.storages.FilmsStorage
import github.karchx.motto.search_engine.citaty_info_website.data.Film
import github.karchx.motto.search_engine.citaty_info_website.data.Motto
import github.karchx.motto.search_engine.citaty_info_website.parsers.ByFilmMottosParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FilmsDashboardViewModel : ViewModel() {

    private val filmsStorage = FilmsStorage()

    private val _films = MutableLiveData<ArrayList<Film>>().apply {
        value = filmsStorage.getFilms()
    }

    private val _filmMottos = MutableLiveData<ArrayList<Motto>>().apply {}

    fun putFilmMottosPostValue(film: Film) {
        val parser = ByFilmMottosParser(film)
        GlobalScope.launch {
            _filmMottos.postValue(parser.getMottos(Constants.QUANTITY_MOTTOS_IN_LIST))
        }
    }

    val films: LiveData<ArrayList<Film>> = _films
    val filmMottos: LiveData<ArrayList<Motto>> = _filmMottos
}