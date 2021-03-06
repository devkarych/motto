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
import github.karchx.motto.databinding.FragmentTopicsDashboardBinding
import github.karchx.motto.models.db.saved_motto.SavedMotto
import github.karchx.motto.models.user_settings.UserPrefs
import github.karchx.motto.search_engine.citaty_info_website.UIMotto
import github.karchx.motto.search_engine.citaty_info_website.items.Topic
import github.karchx.motto.viewmodels.SavedMottosViewModel
import github.karchx.motto.viewmodels.dashboard.topics.TopicsDashboardViewModel
import github.karchx.motto.viewmodels.dashboard.topics.TopicsFactory
import github.karchx.motto.views.MainActivity
import github.karchx.motto.views.tools.adapters.MottosRecyclerAdapter
import github.karchx.motto.views.tools.adapters.TopicsRecyclerAdapter
import github.karchx.motto.views.tools.listeners.OnClickAddToFavouritesListener
import github.karchx.motto.views.tools.listeners.OnClickRecyclerItemListener
import github.karchx.motto.views.tools.managers.*

class TopicsDashboardFragment : Fragment(R.layout.fragment_topics_dashboard) {

    private var _binding: FragmentTopicsDashboardBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private lateinit var topicsDashboardViewModel: TopicsDashboardViewModel
    private lateinit var savedMottosViewModel: SavedMottosViewModel

    // Views
    private lateinit var topicsRecycler: RecyclerView
    private lateinit var topicMottosRecycler: RecyclerView
    private lateinit var mottosLoadingProgressBar: ProgressBar
    private lateinit var fullMottoCardView: CardView
    private lateinit var fullMottoDialog: Dialog
    private lateinit var addToFavouritesImageView: ImageView
    private lateinit var notFoundMottosTextView: TextView

    //Data
    private lateinit var userPrefs: UserPrefs
    private lateinit var topics: ArrayList<Topic>
    private lateinit var clickedTopic: Topic
    private lateinit var allDbMottos: List<SavedMotto>
    private lateinit var topicMottos: ArrayList<UIMotto>
    private lateinit var clickedMotto: UIMotto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicsDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        initData()
        initViews()

        observeTopics()
        observeTopicMottos()
        observeDbMottos()

        handleTopicsRecyclerItemClick()
        handleTopicMottosRecyclerItemClick()

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

    private fun observeTopics() {
        topicsDashboardViewModel.topics.observe(viewLifecycleOwner, { _topics ->
            topics = _topics
            displayTopicsRecycler(topics)
        })
    }

    private fun observeTopicMottos() {
        topicsDashboardViewModel.topicMottos.observe(viewLifecycleOwner, { _topicMottos ->
            topicMottos = _topicMottos
            displayTopicMottosRecycler(topicMottos)
        })
    }

    private fun observeDbMottos() {
        savedMottosViewModel.allMottos.observe(viewLifecycleOwner) { allMottos ->
            allDbMottos = allMottos
        }
    }

    private fun displayTopicsRecycler(topics: ArrayList<Topic>) {
        Arrow.hideBackArrow(activity as MainActivity)

        val layoutManager = GridLayoutManager(context, 2)
        val adapter = TopicsRecyclerAdapter(this@TopicsDashboardFragment, topics)

        topicsRecycler.setHasFixedSize(true)
        topicsRecycler.layoutManager = layoutManager
        topicsRecycler.adapter = adapter
    }

    private fun displayTopicMottosRecycler(topicMottos: ArrayList<UIMotto>) {
        Arrow.displayBackArrow(activity as MainActivity)
        if (topicMottos.isEmpty()) {
            topicsRecycler.visibility = View.GONE
            notFoundMottosTextView.visibility = View.VISIBLE
            mottosLoadingProgressBar.visibility = View.INVISIBLE
        } else {
            notFoundMottosTextView.visibility = View.INVISIBLE
            topicsRecycler.visibility = View.GONE
            mottosLoadingProgressBar.visibility = View.INVISIBLE

            val layoutManager = GridLayoutManager(context, 1)
            val adapter = MottosRecyclerAdapter(topicMottos)

            topicMottosRecycler.scheduleLayoutAnimation()
            topicMottosRecycler.setHasFixedSize(true)
            topicMottosRecycler.layoutManager = layoutManager
            topicMottosRecycler.adapter = adapter
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

    private fun handleTopicsRecyclerItemClick() {
        topicsRecycler.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), topicsRecycler, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    clickedTopic = topics[position]
                    topicsDashboardViewModel.putTopicMottosPostValue(clickedTopic)
                    mottosLoadingProgressBar.visibility = View.VISIBLE
                }

                override fun onItemLongClick(view: View, position: Int) {}
            })
        )
    }

    private fun handleTopicMottosRecyclerItemClick() {
        topicMottosRecycler.addOnItemTouchListener(
            OnClickRecyclerItemListener(requireContext(), topicMottosRecycler, object :
                OnClickRecyclerItemListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    clickedMotto = topicMottos[position]

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
        topicsDashboardViewModel = ViewModelProvider(
            this,
            TopicsFactory(requireActivity().application, userPrefs)
        )[TopicsDashboardViewModel::class.java]

        savedMottosViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SavedMottosViewModel(application = requireActivity().application)::class.java)
    }

    private fun initViews() {
        topicsRecycler = binding.recyclerviewTopicsDashboard
        topicMottosRecycler = binding.recyclerviewTopicMottos

        mottosLoadingProgressBar = binding.progressbarMottosLoading

        fullMottoDialog = Dialog(requireActivity())
        fullMottoDialog.setContentView(R.layout.dialog_full_motto)

        fullMottoCardView = fullMottoDialog.findViewById(R.id.cardview_full_motto_item)
        addToFavouritesImageView = fullMottoDialog.findViewById(R.id.imageview_is_saved_motto)

        notFoundMottosTextView = binding.textviewMottosNotFoundDashboard
    }
}
