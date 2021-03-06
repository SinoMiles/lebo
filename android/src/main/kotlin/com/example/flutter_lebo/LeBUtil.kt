package com.example.lblelinkplugin

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.hpplay.sdk.source.api.IConnectListener
import com.hpplay.sdk.source.api.ILelinkPlayerListener
import com.hpplay.sdk.source.api.LelinkPlayerInfo
import com.hpplay.sdk.source.api.LelinkSourceSDK
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

class LeBUtil private constructor() {
    var events: EventChannel.EventSink? = null
    val sdk: LelinkSourceSDK = LelinkSourceSDK.getInstance()
    val deviceList = mutableListOf<LelinkServiceInfo>()
    var selectLelinkServiceInfo: LelinkServiceInfo? = null

    var lastLinkIp by SharedPreference("lastLinkIp", "")
    var lastLinkName by SharedPreference("lastLinkName", "")
    var lastLinkUid by SharedPreference("lastLinkUid", "")

    private fun initListener() {

        sdk.run {
            setBrowseResultListener { code, resultList ->
                deviceList.clear()
                deviceList.addAll(resultList)

                var finalList = deviceList.map {
                    mapOf("tvName" to it.name, "tvUID" to it.uid, "ipAddress" to it.ip)
                }.toList()
                Observable.just(resultList).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(
                            buildResult(ResultType.divice, finalList)
                    )
                }

            }
            setConnectListener(object : IConnectListener {
                override fun onConnect(p0: LelinkServiceInfo?, p1: Int) {
                    Observable.just(p0).observeOn(AndroidSchedulers.mainThread()).subscribe {
                        events?.success(
                                buildResult(ResultType.connect, "connect")
                        )
                        p0!!.run {
                            lastLinkIp = ip
                            lastLinkName = name
                            lastLinkUid = if (uid == null) "" else uid
                        }
                        Log.d("?????????", "????????????")
                        playListener()
                    }
                }

                override fun onDisconnect(p0: LelinkServiceInfo?, p1: Int, p2: Int) {
                    events?.success(
                            buildResult(ResultType.disConnect, "disConnect")
                    )
                }
            })

        }
    }

    private fun LelinkSourceSDK.playListener() {
        setPlayListener(object : ILelinkPlayerListener {
            override fun onLoading() {
                Log.d("?????????", "onLoading")
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.load, "load"))
                }
                //                    events?.success( buildResult(ResultType.load))
            }

            override fun onPause() {
                Log.d("?????????", "onPause")
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.pause, "pause"))
                }
//                events?.success(Result().addParam("type", ResultType.pause))
            }

            override fun onCompletion() {
                Log.d("?????????", "onCompletion")
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.complete, "complete"))
                }
//                events?.success(Result().addParam("type", ResultType.complete))
            }

            override fun onStop() {
                Log.d("?????????", "onStop")
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.stop, "stop"))
                }
//                events?.success(Result().addParam("type", ResultType.stop))
            }

            override fun onSeekComplete(p0: Int) {
                Log.d("?????????", "onSeekComplete")
//                events?.success(Result().addParam("type", ResultType.seek))
            }

            override fun onInfo(p0: Int, p1: Int) {
                Log.d("?????????", "onInfo")
//                events?.success(Result().addParam("type", ResultType.info))
            }

            override fun onInfo(p0: Int, p1: String?) {
                Log.d("?????????", "onInfo")
            }

            override fun onVolumeChanged(p0: Float) {
                Log.d("?????????", "onVolumeChanged")
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    //                    events?.success(buildResult(ResultType., "onPositionUpdate"))
                }
            }

            override fun onPositionUpdate(p0: Long, p1: Long) {
                Log.d("?????????", "onPositionUpdate")
//                events?.success(Result().addParam("type", ResultType.position))
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.position, "onPositionUpdate"))
                }
            }

            override fun onError(p0: Int, p1: Int) {
                Log.d("?????????", "onError")
//                events?.success(Result().addParam("type", ResultType.error))
                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.error, "error"))
                }
            }

            override fun onStart() {
                Log.d("?????????", "star");

                Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    events?.success(buildResult(ResultType.start, "start"))
                }

            }
        })
    }

    companion object {
        val instance by lazy {
            LeBUtil()
        }
    }

    ///?????????SDK
    fun initUtil(ctx: Context, appId: String, secret: String, result: MethodChannel.Result) {
        sdk.bindSdk(ctx, appId, secret) {
            Observable.just(it).observeOn(AndroidSchedulers.mainThread()).subscribe { result.success(it) }
            if (it) {
                sdk.setDebugMode(true)
                initListener()
            }
        }
    }

    ///????????????
    fun connectService(id: String, name: String) {
        deviceList.forEach {
            //????????????
            if (id == it.ip && name == it.name) {//???????????????
                selectLelinkServiceInfo = it
            }
        }
       sdk.connect(selectLelinkServiceInfo)
        events?.success(
                buildResult(ResultType.connect, null)
        )
    }

    fun buildResult(type: Int, data: Any?): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        data?.run {
            map["data"] = this
        }
        return map
    }

    ///????????????
    fun disConnect(@NonNull result: MethodChannel.Result) {
        sdk.connectInfos.run {
            if (sdk.disConnect(this[0])) {
                result.success(0)
            } else {
                result.success(-1)
            }

        }
    }

    ///????????????
    fun pause() {
        sdk.pause()
    }

    ///????????????
    fun resumePlay() {
        sdk.resume()
    }

    ///????????????
    fun stop() {
        sdk.stopPlay()
    }

    ///????????????
    fun stopSearch() {
        sdk.stopBrowse()
    }

    ///????????????
    fun searchDevice() {
        deviceList.clear()
        sdk.startBrowse()
    }

    fun play(url: String) {
        sdk.resume()
        var playerInfo = LelinkPlayerInfo()
        playerInfo.url = url
        playerInfo.loopMode = LelinkPlayerInfo.LOOP_MODE_SINGLE
        playerInfo.type = LelinkSourceSDK.MEDIA_TYPE_VIDEO
        playerInfo.lelinkServiceInfo = selectLelinkServiceInfo
        sdk.startPlayMedia(playerInfo)
    }

    fun initEvent(events: EventChannel.EventSink?) {
        this.events = events
    }

    fun removeEvent() {
        events = null
    }

    fun getLastIp(result: MethodChannel.Result) {
        result.success(mapOf("tvName" to lastLinkName, "tvUID" to lastLinkUid, "ipAddress" to lastLinkIp))
    }

}