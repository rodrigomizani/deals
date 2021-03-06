package com.mizanidev.deals.api

import android.content.Context
import com.mizanidev.deals.model.game.SingleGameRequest
import com.mizanidev.deals.model.generalapi.GamesRequest
import com.mizanidev.deals.model.others.CurrencyData
import com.mizanidev.deals.util.SharedPreferenceConstants
import com.mizanidev.deals.util.SharedPreferenceUtil
import com.mizanidev.deals.util.Url
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Request{
    companion object{
        private fun retrofitInstance(): Retrofit{
            return Retrofit.Builder()
                .baseUrl(Url.baseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    fun requestCurrencies() : Call<CurrencyData> {
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        return endpoint.currencies()
    }

    fun requestRecentReleases(currency: String, locale: String, platform: String) : Call<GamesRequest>{
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        val data: MutableMap<String, String> = HashMap()
        data["currency"] = currency
        data["locale"] = locale
        data["filter[platform]"] = platform

        return endpoint.recentReleases(data, true)
    }

    fun requestOnSale(currency: String, locale: String, platform: String) : Call<GamesRequest>{
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        val data: MutableMap<String, String> = HashMap()
        data["currency"] = currency
        data["locale"] = locale
        data["filter[platform]"] = platform

        return endpoint.gamesOnSale(data, true)
    }

    fun requestSoonGames(currency: String, locale: String, platform: String) : Call<GamesRequest>{
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        val data: MutableMap<String, String> = HashMap()
        data["currency"] = currency
        data["locale"] = locale
        data["filter[platform]"] = platform

        return endpoint.soonGames(data, true)
    }

    fun requestGame(title: String, context: Context) : Call<SingleGameRequest>{
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        val sharedPreferenceUtil = SharedPreferenceUtil(context)

        val data: MutableMap<String, String> = HashMap()

        if(sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.CURRENCY).isEmpty()) {
            data["currency"] = "USD"
        } else {
            data["currency"] = sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.CURRENCY)
        }

        if(sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.REGION).isEmpty()) {
            data["locale"] = "en"
        } else {
            data["locale"] = sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.REGION)
        }

        return endpoint.showGame(title, data)

    }

    fun requestMoreGames(nextPage: String, context: Context): Call<GamesRequest> {
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        val linkToCall = nextPage.replace(Url.baseUrl(), "")
            .replace("%5B", "[")
            .replace("%5D", "]")

        val pageNumber = linkToCall.substring(linkToCall.indexOf("[number]=") + 9,
            linkToCall.indexOf("&page[size]"))
        val pageSize = linkToCall.substring(linkToCall.indexOf("[size]=") + 7,
            linkToCall.length)

        val sharedPreferenceUtil = SharedPreferenceUtil(context)

        val data: MutableMap<String, String> = HashMap()

        if(sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.CURRENCY).isEmpty()) {
            data["currency"] = "USD"
        } else {
            data["currency"] = sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.CURRENCY)
        }

        if(sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.REGION).isEmpty()) {
            data["locale"] = "en"
        } else {
            data["locale"] = sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.REGION)
        }

        data["page[number]"] = pageNumber
        data["page[size]"] = pageSize

        return endpoint.showMoreGames(data)
    }

    fun requestSearchGame(nameToSearch: String, context: Context): Call<GamesRequest> {
        val retrofitClient = retrofitInstance()
        val endpoint = retrofitClient.create(NintendoEndpoint::class.java)

        val data: MutableMap<String, String> = HashMap()
        data["filter[platform]"] = "nintendo"
        data["filter[title]"] = nameToSearch


        val sharedPreferenceUtil = SharedPreferenceUtil(context)

        if(sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.CURRENCY).isEmpty()) {
            data["currency"] = "USD"
        } else {
            data["currency"] = sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.CURRENCY)
        }

        if(sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.REGION).isEmpty()) {
            data["locale"] = "en"
        } else {
            data["locale"] = sharedPreferenceUtil.stringConfig(SharedPreferenceConstants.REGION)
        }

        return endpoint.requestSearchGame(data)
    }

}