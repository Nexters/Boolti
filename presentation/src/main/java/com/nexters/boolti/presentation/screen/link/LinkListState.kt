package com.nexters.boolti.presentation.screen.link

import com.nexters.boolti.domain.model.Link

data class LinkListState(
    val loading: Boolean = false,
    val links: List<Link> = emptyList(),
)
