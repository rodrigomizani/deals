package com.mizanidev.deals.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anjlab.android.iab.v3.BillingProcessor
import com.mizanidev.deals.R
import com.mizanidev.deals.model.generalapi.GamesRequest
import com.mizanidev.deals.util.CToast
import com.mizanidev.deals.util.SharedPreferenceUtil
import com.mizanidev.deals.view.activity.HomeActivityAdapter
import com.mizanidev.deals.viewmodel.ViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment : Fragment(), TextWatcher {
    var listener: DealsInterface? = null
    var billingProcessor: BillingProcessor? = null

    private val viewModel: BaseFragmentViewModel by viewModel()

    private lateinit var searchProgress: ProgressBar
    private lateinit var recyclerViewSearch: RecyclerView
    private lateinit var adapter: HomeActivityAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(IS_FIRST_TIME) {
            setAuthObservables()
            viewModel.createUser()
            IS_FIRST_TIME = false
        }

    }

    fun showSearchGame() {
        val alertView = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_search, null)

        val editGameName: EditText = alertView.findViewById(R.id.edit_type_game)
        editGameName.addTextChangedListener(this)

        recyclerViewSearch = alertView.findViewById(R.id.recycler_search)
        searchProgress = alertView.findViewById(R.id.search_progress)

        val dialogView = AlertDialog.Builder(requireContext())
        dialogView.setView(alertView)
        dialogView.setTitle(R.string.search_game_title)
        val dialog = dialogView.create()
        alertView.setPadding(50, 0, 50, 0)
        dialog.show()

        setSearchObservables()

    }

    private fun setAuthObservables() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ViewState.UserLogged -> userLoggedToServer()
            }
        })

    }

    private fun setSearchObservables() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ViewState.RefreshSearch -> showList(it.items)
                is ViewState.SearchLoading -> showSearchLoading()
                is ViewState.SearchLoaded -> hideSearchLoading()
            }
        })
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0?.length!! > 2) {
            viewModel.searchGame(p0.toString(), requireContext())
        }
    }

    private fun showSearchLoading() {
        searchProgress.visibility = View.VISIBLE
    }

    private fun hideSearchLoading() {
        searchProgress.visibility = View.GONE
    }

    private fun userLoggedToServer() {
        Log.i("AUTH", "Connected")
        val cToast = CToast(requireContext())
        cToast.showInfo(getString(R.string.connected_to_server))
    }

    private fun showList(gamesRequest: GamesRequest?) {
        adapter = HomeActivityAdapter(requireContext(),
            gamesRequest!!.gameLists, fromSearch = true)
        adapter.notifyDataSetChanged()
        recyclerViewSearch.adapter = adapter

        mLayoutManager = LinearLayoutManager(requireContext())
        recyclerViewSearch.layoutManager = mLayoutManager
        recyclerViewSearch.setHasFixedSize(true)
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    companion object {
        var IS_FIRST_TIME = true
    }
}

interface DealsInterface {
    fun sharedPreference(): SharedPreferenceUtil

}