package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.Show

sealed interface ShowListItem {
    // item 이라는 suffix를 붙인 건 Show라는 이름의 중복을 피하기 위함.
    data class ShowItem(val show: Show) : ShowListItem
    data object BannerItem : ShowListItem
}

fun Show.toUI(): ShowListItem.ShowItem {
    return ShowListItem.ShowItem(this)
}
