package github.karchx.motto.views.dashboard.items

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.karchx.motto.R
import github.karchx.motto.ads.AdViewer
import github.karchx.motto.copying.Copier
import github.karchx.motto.databinding.FragmentFilmsDashboardBinding
import github.karchx.motto.models.db.saved_motto.SavedMotto
import github.karchx.motto.models.user_settings.UserPrefs
import github.karchx.motto.search_engine.citaty_info_website.UIMotto
import github.karchx.motto.search_engine.citaty_info_website.items.Film
import github.karchx.motto.viewmodels.SavedMottosViewModel
import github.karchx.motto.viewmodels.dashboard.films.FilmsDashboardViewModel
import github.karchx.motto.viewmodels.dashboard.films.FilmsFactory
import github.karchx.motto.views.MainActivity
import github.karchx.motto.views.tools.adapters.FilmsRecyclerAdapter
import github.karchx.motto.views.tools.adapters.MottosRecyclerAdapter
import github.karchx.motto.views.tools.listeners.OnClickAddToFavouritesListener
import github.karchx.motto.views.tools.listeners.OnClickRecyclerItemListener
import github.karchx.motto.views.tools.managers.*

class FilmsDashboardFragment : Fragment(R.layout.fragment_films_dashboard) {

    private var _binding: FragmentFilmsDashboardBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private lateinit var filmsDashboardViewModel: FilmsDashboardViewModel
    private lateinit var savedMottosViewModel: SavedMottosViewModel

    // Views
    private lateinit var filmsRecycler: RecyclerView
    private lateinit var filmMottosRecycler: RecyclerView
    private lateinit var mottosLoadingProgressBar: ProgressBar
    private lateinit var fullMottoCardView: CardView
    private lateinit var fullMottoDialog: Dialog
    private lateinit var addToFavouritesImageView: ImageView
    private lateinit var notFoundMottosTextView: TextView

    //Data
    private lateinit var userPrefs: UserPrefs
    private lateinit var films: ArrayList<Film>
    private lateinit var clickedFilm: Film
    private lateinit var allDbMottos: List<SavedMotto>
    private lateinit var filmMottos: ArrayList<UIMotto>
    private lateinit var clickedMotto: UIMotto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmsDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        initData()
        initViews()

        observeAuthors()
        observeFilmMottos()
        observeDbMottos()

        handleAuthorsRecyclerItemClick()
        handleFilmMottosRecyclerItemClick()

        handleCopyMottoData()
        setAddToFavouritesBtnClickListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            reloadFragment()
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeAuthors() {
        filmsDashboardViewModel.films.observe(viewLifecycleOwner, { _films ->
            films = _films
            displayFilmsRecycler(films)
        })
    }

    private fun observeFilmMottos() {
        filmsDashboardViewModel.filmMottos.observe(viewLifecycleOwner, { _filmMottos ->
            filmMottos = _filmMottos
            displayFilmMottosRecycler(filmMottos)
        })
    }

    private fun observeDbMottos() {
        savedMottosViewModel.allMottos.observe(viewLifecycleOwner) { allMottos ->
            allDbMottos = allMottos
        }
    }

    private fun displayFilmsRecycler(films: ArrayList<Film>) {
        Arrow.hideBackArrow(activity as MainActivity)

        val layoutManager = GridLayoutManager(context, 2)
        val adapter = FilmsRecyclerAdapter(this@FilmsDashboardFragment, films)

        filmsRecycler.setHasFixedSize(true)
        filmsRecycler.layoutManager = layoutManager
        filmsRecycler.adapter = adapter
    }

    private fun displayFilmMottosRecycler(filmMottos: ArrayList<UIMotto>) {
        Arrow.displayBackArrow(activity as MainActivity)
        if (filmMottos.isEmpty()) {
            filmsRecycler.visibility = View.GONE
            notFoundMottosTextView.visibility = View.VISIBLE
            mottosLoadingProgressBar.visibility = View.INVISIBLE
        } else {
            notFoundMottosTextView.visibility = View.INVISIBLE
            filmsRecycler.visibility = View.GONE
            mottosLoadingProgressBar.visibility = View.INVISIBLE

            val layoutManager = GridLayoutManager(context, 1)
            val adapter = MottosRecyclerAdapter(filmMottos)

            filmMottosRecycler.scheduleLayoutAnimation()
            filmMottosRecycler.setHasFixedSize(true)
            filmMottosRecycler.layoutManager = layoutManager
            filmMottosRecycler.adapter = adapter
        }
    }

    private fun setAddToFavouritesBtnClickListener() {
        addToFavouritesImageView.setOnClickListener {
            OnClickAddToFavouritesListener.handleMotto(
                requireContext(),
                savedMottosViewModel,
                addToFavouritesImageView,
                allDbMottos,
                clickedMotto
            )
        }
    }

    private fun handleAuthorsRecyclerItemClick() {
        filmsRecycler.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), filmsRecycler, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    clickedFilm = films[position]
                    filmsDashboardViewModel.putFilmMottosPostValue(clickedFilm)
                    mottosLoadingProgressBar.visibility = View.VISIBLE
                }

                override fun onItemLongClick(view: View, position: Int) {}
            })
        )
    }

    private fun handleFilmMottosRecyclerItemClick() {
        filmMottosRecycler.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), filmMottosRecycler, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    clickedMotto = filmMottos[position]

                    AdViewer(activity as MainActivity, requireContext()).displayFullMottoAd()
                    DialogViewer.displayFullMottoDialog(
                        requireActivity(),
                        requireContext(),
                        fullMottoDialog,
                        clickedMotto,
                        allDbMottos
                    )
                    observeDbMottos()
                }

                override fun onItemLongClick(view: View, position: Int) {}
            })
        )
    }

    private fun handleCopyMottoData() {
        fullMottoCardView.setOnClickListener {
            val text = Copier(activity as MainActivity, requireContext()).getMottoDataToCopy(
                quote = clickedMotto.quote,
                source = clickedMotto.source,
                isCopyWithAuthor = userPrefs.copySettings.isWithSource()
            )

            Copier(activity as MainActivity, requireContext()).copyText(text)
            Toaster.displayTextIsCopiedToast(requireContext())
        }
    }

    private fun reloadFragment() {
        findNavController().navigate(
            R.id.navigation_dashboard,
            arguments,
            NavOptions.Builder()
                .setPopUpTo(R.id.navigation_dashboard, true)
                .build()
        )
    }


    private fun initData() {
        userPrefs = UserPrefs(activity as MainActivity)
        filmsDashboardViewModel = ViewModelProvider(
            this,
            FilmsFactory(requireActivity().application, userPrefs)
        )[FilmsDashboardViewModel::class.java]

        savedMottosViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SavedMottosViewModel(application = requireActivity().application)::class.java)
    }

    private fun initViews() {
        filmsRecycler = binding.recyclerviewFilmsDashboard
        filmMottosRecycler = binding.recyclerviewFilmMottos

        mottosLoadingProgressBar = binding.progressbarMottosLoading

        fullMottoDialog = Dialog(requireActivity())
        fullMottoDialog.setContentView(R.layout.dialog_full_motto)

        fullMottoCardView = fullMottoDialog.findViewById(R.id.cardview_full_motto_item)
        addToFavouritesImageView = fullMottoDialog.findViewById(R.id.imageview_is_saved_motto)

        notFoundMottosTextView = binding.textviewMottosNotFoundDashboard
    }
}
