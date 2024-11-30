import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hoophubskeleton.repository.ProfileRepository
import com.example.hoophubskeleton.ViewModel.ProfileViewModel

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}