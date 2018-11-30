package com.dcc.ibase.utils

import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram

import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object WeChatUtils {

    var EVENT_TAG = "WXEntryActivity"

    /**
     * 微信分享api接口
     */
    private var wxApi: IWXAPI? = null

    /**
     * 微信分享appId
     */
    private var appId: String? = null

    /**
     * 检测是否安装微信
     */
    val isWeChatInstalled: Boolean
        get() = wxApi!!.isWXAppInstalled

    /**
     * APP注册到微信
     */
    fun registerWeChat(appId: String) {
        WeChatUtils.appId = appId
        wxApi = WXAPIFactory.createWXAPI(AppUtils.app, appId, true)
        wxApi!!.registerApp(appId)
    }

    /**
     * 构建请求唯一标识
     */
    private fun buildTransaction(): String {
        return "text" + System.currentTimeMillis()
    }

    /**
     * 发送通用请求
     */
    private fun requestShare(shareType: Int, shareText: String) {
        // 初始化一个WXTextObject对象，填写分享的文本内容
        val textObject = WXTextObject()
        textObject.text = shareText

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        val msg = WXMediaMessage()
        msg.mediaObject = textObject
        msg.description = shareText

        // 构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction() // 唯一标识一个请求
        req.message = msg
        req.scene = shareType // 分享到好友会话

        wxApi!!.sendReq(req)
    }

    /**
     * 发送微信分享请求
     */
    fun weChatShare(shareInfo: String) {
        requestShare(SendMessageToWX.Req.WXSceneSession, shareInfo)
    }

    /**
     * 发送微信朋友圈分享请求
     */
    fun momentsShare(shareInfo: String) {
        requestShare(SendMessageToWX.Req.WXSceneTimeline, shareInfo)
    }

    /**
     * 发送微信授权登录请求
     */
    fun requestWeChatAuth() {
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wechat_auth_login"
        wxApi!!.sendReq(req)
    }

    /**
     * 处理微信响应事件
     */
    fun handleWxApiEvent(intent: Intent, iwxapiEventHandler: IWXAPIEventHandler) {
        wxApi = WXAPIFactory.createWXAPI(AppUtils.app, appId, false)
        wxApi!!.handleIntent(intent, iwxapiEventHandler)
    }

    /**
     * app打开小程序
     * @param context 上下文
     * @param appId appId
     * @param originId 原始id
     * @param path  拉起小程序页面的可带参路径，不填默认拉起小程序首页
     * @param type  可选打开 开发版1，体验版2 和正式版0
     */
    fun launchMiniProgram(context: Context, appId: String, originId: String, path: String, type: Int) {
        val api = WXAPIFactory.createWXAPI(context, appId)

        val req = WXLaunchMiniProgram.Req()
        req.userName = originId // 填小程序原始id
        /*req.logFileRelativePath = logFileRelativePath;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页*/
        req.miniprogramType = type// 可选打开 开发版，体验版和正式版
        api.sendReq(req)
    }
}
