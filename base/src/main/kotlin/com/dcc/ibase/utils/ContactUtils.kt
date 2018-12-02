package com.dcc.ibase.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.text.TextUtils
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：联系人工具
 */
object ContactUtils {

    private val PHONES_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)

    /**
     * 联系人显示名称
     */
    private const val PHONES_DISPLAY_NAME_INDEX = 0

    /**
     * 电话号码
     */
    private const val PHONES_NUMBER_INDEX = 1


    /**
     * 得到所有的联系人信息
     */
    fun getAllPhoneContacts(context: Context): List<PinYinUtils.Companion.PhoneContact> {
        val phoneContactList = getPhoneContacts(context)
        val simContactList = getSIMContacts(context)
        for (contact in simContactList) {
            if (!isContainObject(phoneContactList, contact)) {
                phoneContactList.add(contact)
            }
        }
        return phoneContactList
    }

    /**
     * 某个联系人是否已经存在于联系人列表中
     */
    private fun isContainObject(phoneContactList: List<PinYinUtils.Companion.PhoneContact>, contact: PinYinUtils.Companion.PhoneContact): Boolean {
        for (i in phoneContactList.indices) {
            val curContact = phoneContactList[i]
            if (curContact.mobile == contact.mobile && curContact.name == contact.name) {
                return true
            }
        }
        return false
    }

    /**
     * 获取手机通讯录联系人
     */
    private fun getPhoneContacts(context: Context): MutableList<PinYinUtils.Companion.PhoneContact> {
        val list = ArrayList<PinYinUtils.Companion.PhoneContact>()
        try {
            val contentResolver = context.contentResolver
            val contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val cursor = contentResolver.query(contentUri, PHONES_PROJECTION, null, null, null)

            while (cursor != null && cursor.moveToNext()) {
                val name = cursor.getString(0).replace("'".toRegex(), "’").replace("\"".toRegex(), "”")
                val mobile = cursor.getString(1).replace("+86", "").replace(" ", "").replace("-", "")
                val namePinYin = PinYinUtils.getPinYin(name).toUpperCase(Locale.getDefault())
                val contactInfo = PinYinUtils.Companion.PhoneContact(name, mobile, namePinYin)
                if (RegexUtils.checkMobile(contactInfo.mobile)) {
                    list.add(contactInfo)
                }
            }
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    /**
     * 得到手机SIM卡联系人人信息
     */
    private fun getSIMContacts(context: Context): List<PinYinUtils.Companion.PhoneContact> {
        val list = ArrayList<PinYinUtils.Companion.PhoneContact>()
        try {
            val contentResolver = context.contentResolver
            val uri = Uri.parse("content://icc/adn")
            val cursor = contentResolver.query(uri, PHONES_PROJECTION, null, null, null)
            while (cursor != null && cursor.moveToNext()) {
                val name = cursor.getString(PHONES_DISPLAY_NAME_INDEX).replace("'".toRegex(), "’").replace("\"".toRegex(), "”")
                val mobile = cursor.getString(PHONES_NUMBER_INDEX).replace("+86", "").replace(" ", "").replace("-", "")
                val namePinYin = PinYinUtils.getPinYin(name).toUpperCase(Locale.getDefault())
                val contactInfo = PinYinUtils.Companion.PhoneContact(name, mobile, namePinYin)
                if (RegexUtils.checkMobile(contactInfo.mobile)) {
                    list.add(contactInfo)
                }
            }
            cursor?.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return list
    }

    /**
     * 打开通讯录选择一个联系人
     */
    fun selectContactPersonFromBook(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 从选择的联系人Intent中解析手机号码
     */
    fun getPhoneNumberFromIntent(activity: Activity, intent: Intent?): String? {
        if (intent == null) {
            return null
        }
        var cursor: Cursor? = null
        var phone: Cursor? = null
        try {
            val projections = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER)
            cursor = activity.contentResolver.query(intent.data!!, projections, null, null, null)
            if (cursor == null || !cursor.moveToFirst()) {
                return null
            }
            val _id = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val id = cursor.getString(_id)
            val has_phone_number = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
            val hasPhoneNumber = cursor.getInt(has_phone_number)
            var phoneNumber: String? = null
            if (hasPhoneNumber > 0) {
                phone = activity.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null)
                if (phone == null) {
                    return null
                }
                while (phone.moveToNext()) {
                    val index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    phoneNumber = phone.getString(index)
                    phoneNumber = phoneNumber!!.replace(" ", "").replace("-", "").replace("+86", "")
                }
            }
            return phoneNumber
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            phone?.close()
        }
        return null
    }

    /**
     * 获取手机号码对应的名字
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    fun getContactName(context: Context, phoneNumber: String): String? {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null
        }
        val cr = context.contentResolver
        //通过电话号码查找联系人
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val cursor = cr.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        var contactName: String? = null
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
        }

        cursor?.close()

        return if (TextUtils.isEmpty(contactName)) phoneNumber else contactName
    }
}
