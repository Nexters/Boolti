package com.nexters.boolti.presentation.screen.navigation.decorator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

@Composable
fun <T : Any> rememberSharedViewModelStoreNavEntryDecorator(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    removeViewModelStoreOnPop: () -> Boolean = { true },
): SharedViewModelStoreNavEntryDecorator<T> {
    val currentRemoveViewModelStoreOnPop = rememberUpdatedState(removeViewModelStoreOnPop)
    return remember(viewModelStoreOwner, currentRemoveViewModelStoreOnPop) {
        SharedViewModelStoreNavEntryDecorator(
            viewModelStoreOwner.viewModelStore,
            removeViewModelStoreOnPop,
        )
    }
}

class SharedViewModelStoreNavEntryDecorator<T : Any>(
    viewModelStore: ViewModelStore,
    removeViewModelStoreOnPop: () -> Boolean,
) :
    NavEntryDecorator<T>(
        onPop = ({ key ->
            if (removeViewModelStoreOnPop()) {
                viewModelStore.getEntryViewModel().clearViewModelStoreOwnerForKey(key)
            }
        }),
        decorate = { entry ->
            val contentKey = entry.metadata[PARENT_CONTENT_KEY] ?: entry.contentKey
            val viewModelStore =
                viewModelStore.getEntryViewModel().viewModelStoreForKey(contentKey)

            val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
            val childViewModelStoreOwner = remember {
                object :
                    ViewModelStoreOwner,
                    SavedStateRegistryOwner by savedStateRegistryOwner,
                    HasDefaultViewModelProviderFactory {
                    override val viewModelStore: ViewModelStore
                        get() = viewModelStore

                    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
                        get() = SavedStateViewModelFactory()

                    override val defaultViewModelCreationExtras: CreationExtras
                        get() =
                            MutableCreationExtras().also {
                                it[SAVED_STATE_REGISTRY_OWNER_KEY] = this
                                it[VIEW_MODEL_STORE_OWNER_KEY] = this
                            }

                    init {
                        require(this.lifecycle.currentState == Lifecycle.State.INITIALIZED) {
                            "The Lifecycle state is already beyond INITIALIZED. The " +
                                    "SharedViewModelStoreNavEntryDecorator requires adding the " +
                                    "SavedStateNavEntryDecorator to ensure support for " +
                                    "SavedStateHandles."
                        }
                        enableSavedStateHandles()
                    }
                }
            }
            CompositionLocalProvider(LocalViewModelStoreOwner provides childViewModelStoreOwner) {
                entry.Content()
            }
        },
    ) {

    companion object {

        private const val PARENT_CONTENT_KEY = "shared_decorator_parent_content_key"

        fun parent(contentKey: Any) = mapOf(PARENT_CONTENT_KEY to contentKey)
    }
}

private class EntryViewModel : ViewModel() {
    private val owners = mutableMapOf<Any, ViewModelStore>()

    fun viewModelStoreForKey(key: Any): ViewModelStore = owners.getOrPut(key) { ViewModelStore() }

    fun clearViewModelStoreOwnerForKey(key: Any) {
        owners.remove(key)?.clear()
    }

    override fun onCleared() {
        owners.forEach { (_, store) -> store.clear() }
    }
}

private fun ViewModelStore.getEntryViewModel(): EntryViewModel {
    val provider =
        ViewModelProvider.create(
            store = this,
            factory = viewModelFactory { initializer { EntryViewModel() } },
        )
    return provider[EntryViewModel::class]
}
