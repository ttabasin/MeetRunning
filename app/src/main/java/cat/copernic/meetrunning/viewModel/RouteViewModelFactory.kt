package cat.copernic.meetrunning.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.copernic.meetrunning.dataClass.DataRoute

class RouteViewModelFactory(private val route: DataRoute?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            return RouteViewModel(route) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}