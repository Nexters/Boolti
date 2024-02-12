package com.nexters.boolti.presentation.screen.refund

import androidx.annotation.DrawableRes
import com.nexters.boolti.presentation.R

enum class BankInfo(val code: String, val bankName: String, @DrawableRes val icon: Int) {
    IBK("003", "기업은행", R.drawable.bank_ibk),
    KB("004", "국민은행", R.drawable.bank_kb),
    SUHYUP("007", "수협중앙회", R.drawable.bank_suhyup),
    NH("011", "농협은행", R.drawable.bank_nh),
    WOORI("020", "우리은행", R.drawable.bank_woori),
    SC("023", "SC제일은행", R.drawable.bank_sc),
    CITI("027", "한국씨티은행", R.drawable.bank_citi),
    DGB("031", "대구은행", R.drawable.bank_dgb),
    KJ("034", "광주은행", R.drawable.bank_kj),
    JEJU("035", "제주은행", R.drawable.bank_jeju),
    JB("037", "전북은행", R.drawable.bank_jb),
    KN("039", "경남은행", R.drawable.bank_kn),
    NEWTOWN("045", "새마을금고", R.drawable.bank_new_town),
    SHINHYUP("048", "신협은행", R.drawable.bank_shinhyup),
    EPOST("071", "우체국", R.drawable.bank_epost),
    HANA("081", "하나은행", R.drawable.bank_hana),
    SHINHAN("088", "신한은행", R.drawable.bank_shinhan),
    K("089", "케이뱅크", R.drawable.bank_k),
    KAKAO("090", "카카오뱅크", R.drawable.bank_kakao),
    TOSS("092", "토스뱅크", R.drawable.bank_toss),
}
