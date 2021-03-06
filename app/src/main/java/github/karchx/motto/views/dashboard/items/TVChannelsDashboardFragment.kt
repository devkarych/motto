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
import github.karchx.motto.databinding.FragmentTvChannelsDashboardBinding
import github.karchx.motto.models.db.saved_motto.SavedMotto
import github.karchx.motto.models.user_settings.UserPrefs
import github.karchx.motto.search_engine.citaty_info_website.UIMotto
import github.karchx.motto.search_engine.citaty_info_website.items.TVChannel
import github.karchx.motto.viewmodels.SavedMottosViewModel
import github.karchx.motto.viewmodels.dashboard.tv_channels.TVChannelsDashboardViewModel
import github.karchx.motto.viewmodels.dashboard.tv_channels.TVChannelsFactory
import github.karchx.motto.views.MainActivity
import github.karchx.motto.views.tools.adapters.ChannelsRecyclerAdapter
import github.karchx.motto.views.tools.adapters.MottosRecyclerAdapter
import github.karchx.motto.views.tools.listeners.OnClickAddToFavouritesListener
import github.karchx.motto.views.tools.listeners.OnClickRecyclerItemListener
import github.karchx.motto.views.tools.managers.*

class TVChannelsDashboardFragment : Fragment(R.layout.fragment_tv_channels_dashboard) {

    private var _binding: FragmentTvChannelsDashboardBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private lateinit var tvChannelsDashboardViewModel: TVChannelsDashboardViewModel
    private lateinit var savedMottosViewModel: SavedMottosViewModel

    // Views
    private lateinit var tvChannelsRecycler: RecyclerView
    private lateinit var tvChannelMottosRecycler: RecyclerView
    private lateinit var mottosLoadingProgressBar: ProgressBar
    private lateinit var fullMottoCardView: CardView
    private lateinit var fullMottoDialog: Dialog
    private lateinit var addToFavouritesImageView: ImageView
    private lateinit var notFoundMottosTextView: TextView

    //Data
    private lateinit var userPrefs: UserPrefs
    private lateinit var tvChannels: ArrayList<TVChannel>
    private lateinit var clickedTVChannel: TVChannel
    private lateinit var allDbMottos: List<SavedMotto>
    private lateinit var tvChannelMottos: ArrayList<UIMotto>
    private lateinit var clickedMotto: UIMotto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvChannelsDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        initData()
        initViews()

        observeTVChannels()
        observeTVChannelMottos()
        observeDbMottos()

        handleTVChannelsRecyclerItemClick()
        handleTVChannelMottosRecyclerItemClick()

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

    private fun observeTVChannels() {
        tvChannelsDashboardViewModel.tvChannels.observe(viewLifecycleOwner, { _tvChannels ->
            tvChannels = _tvChannels
            displayTVChannelsRecycler(tvChannels)
        })
    }

    private fun observeTVChannelMottos() {
        tvChannelsDashboardViewModel.tvChannelMottos.observe(
            viewLifecycleOwner,
            { _tvChannelMottos ->
                tvChannelMottos = _tvChannelMottos
                displayTVChannelMottosRecycler(tvChannelMottos)
            })
    }

    private fun observeDbMottos() {
        savedMottosViewModel.allMottos.observe(viewLifecycleOwner) { allMottos ->
            allDbMottos = allMottos
        }
    }

    private fun displayTVChannelsRecycler(tvChannels: ArrayList<TVChannel>) {
        Arrow.hideBackArrow(activity as MainActivity)

        val layoutManager = GridLayoutManager(context, 2)
        val adapter = ChannelsRecyclerAdapter(this@TVChannelsDashboardFragment, tvChannels)

        tvChannelsRecycler.setHasFixedSize(true)
        tvChannelsRecycler.layoutManager = layoutManager
        tvChannelsRecycler.adapter = adapter
    }

    private fun displayTVChannelMottosRecycler(tvChannelMottos: ArrayList<UIMotto>) {
        Arrow.displayBackArrow(activity as MainActivity)
        if (tvChannelMottos.isEmpty()) {
            tvChannelsRecycler.visibility = View.GONE
            notFoundMottosTextView.visibility = View.VISIBLE
            mottosLoadingProgressBar.visibility = View.INVISIBLE
        } else {
            notFoundMottosTextView.visibility = View.INVISIBLE
            tvChannelsRecycler.visibility = View.GONE
            mottosLoadingProgressBar.visibility = View.INVISIBLE

            val layoutManager = GridLayoutManager(context, 1)
            val adapter = MottosRecyclerAdapter(tvChannelMottos)

            tvChannelMottosRecycler.scheduleLayoutAnimation()
            tvChannelMottosRecycler.setHasFixedSize(true)
            tvChannelMottosRecycler.layoutManager = layoutManager
            tvChannelMottosRecycler.adapter = adapter
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

    private fun handleTVChannelsRecyclerItemClick() {
        tvChannelsRecycler.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), tvChannelsRecycler, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    clickedTVChannel = tvChannels[position]
                    tvChannelsDashboardViewModel.putTVChannelMottosPostValue(clickedTVChannel)
                    mottosLoadingProgressBar.visibility = View.VISIBLE
                }

                override fun onItemLongClick(view: View, position: Int) {}
            })
        )
    }

    private fun handleTVChannelMottosRecyclerItemClick() {
        tvChannelMottosRecycler.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), tvChannelMottosRecycler, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    clickedMotto = tvChannelMottos[position]

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
        tvChannelsDashboardViewModel = ViewModelProvider(
            this,
            TVChannelsFactory(requireActivity().application, userPrefs)
        )[TVChannelsDashboardViewModel::class.java]

        savedMottosViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SavedMottosViewModel(application = requireActivity().application)::class.java)
    }

    private fun initViews() {
        tvChannelsRecycler = binding.recyclerviewTvChannelsDashboard
        tvChannelMottosRecycler = binding.recyclerviewTvChannelsMottos

        mottosLoadingProgressBar = binding.progressbarMottosLoading

        fullMottoDialog = Dialog(requireActivity())
        fullMottoDialog.setContentView(R.layout.dialog_full_motto)

        fullMottoCardView = fullMottoDialog.findViewById(R.id.cardview_full_motto_item)
        addToFavouritesImageView = fullMottoDialog.findViewById(R.id.imageview_is_saved_motto)

        notFoundMottosTextView = binding.textviewMottosNotFoundDashboard
    }
}
