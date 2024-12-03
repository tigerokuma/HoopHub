# **HoopHub**

HoopHub is a basketball-focused mobile application designed to connect basketball enthusiasts by allowing users to explore nearby courts, manage player profiles, and communicate with others in their network. The app leverages **MVVM (Model-View-ViewModel)** architecture to ensure scalability, testability, and clean separation of concerns.

---

## **Features**
- **Players Section**: Explore and manage players in your network.
- **Courts Section**: Discover nearby basketball courts using an interactive map.
- **Profile Section**: Update and view personalized user profiles.
- **Messaging**: Chat with other users and plan basketball activities.
- **Dynamic Data**: Real-time basketball court data fetched using the Google Places API.
- **Intuitive Navigation**: User-friendly navigation powered by top and bottom tab menus.

---

## **App Overview**

### **MainActivity**
The `MainActivity` serves as the entry point and primary navigation hub for the app. It manages:
1. **Top and Bottom Tab Layouts**:
    - **Top Tab Menu**: Switches between the Players, Courts, and Profile sections.
    - **Bottom Tab Menu**: Displays additional navigation options.
    - Icons and labels are dynamically generated using the `MenuIconCreator` utility class.

2. **Fragment Management**:
    - Fragments such as `PlayersFragment`, `MapFragment`, and `ProfileFragment` are initialized via the `fragmentSetup()` method.
    - `ViewPager2` enables seamless swiping between fragments.
    - Tabs and fragments are synchronized for a smooth and consistent UI experience.

---

## **Architecture**

### **MVVM (Model-View-ViewModel)**
HoopHub follows the **MVVM architecture** to streamline app development by separating business logic from the UI. This architecture ensures testability and scalability.

#### **Components**:
- **Model**: Represents data sources, including the Google Places API and local database entities.
- **View**: Composable UI components (e.g., `PlayersView`, `CourtsView`, `ProfileView`) display data to the user.
- **ViewModel**: Manages UI-related data, handles business logic, and serves as a bridge between the Model and View.

#### **MVVM Flow Diagram**:
```mermaid
graph TD
    View["View (UI - Composables)"] --> ViewModel["ViewModel"]
    ViewModel --> View["UI State (LiveData/StateFlow)"]
    ViewModel --> Model["Model (API & Database)"]
    Model --> ViewModel["Provide Data (Repository)"]
```

---

### **Thread Management**
To handle operations efficiently, HoopHub uses background threads for API calls and database interactions, ensuring the main thread remains responsive for user actions.

#### **Thread Management Diagram**:
```mermaid
graph TD
    MainThread["Main UI Thread"] -->|UI Updates| ViewModel
    ViewModel -->|Background Task| BackgroundThread["Background Thread"]
    BackgroundThread -->|Fetch Data| Model
    Model -->|Return Data| ViewModel
    ViewModel -->|Send Updates| MainThread
```

---

## **App Structure**

```plaintext
com.example.hoophub
├── data
│   ├── BasketballCourt.kt   # Data class for court information
│   ├── PlaceApiResponse.kt  # Data class for API response
├── network
│   ├── GooglePlacesAPI.kt   # Retrofit API interface
├── ui
│   ├── PlayersFragment.kt   # Players section UI
│   ├── MapFragment.kt       # Map showing basketball courts
│   ├── ProfileFragment.kt   # User profile UI
│   ├── MainActivity.kt      # Main Activity managing navigation and fragments
├── viewmodel
│   ├── CourtsViewModel.kt   # ViewModel for managing court data
│   ├── PlayersViewModel.kt  # ViewModel for managing player data
│   ├── ProfileViewModel.kt  # ViewModel for managing user profile data
```

---

## **Getting Started**

### **1. Prerequisites**
- **Android Studio**: Install the latest version.
- **Google Maps API Key**: Obtain your API key and configure it in the project.

### **2. Setting up `local.properties`**
1. **Create the `local.properties` file** in the root directory of the project.
2. Add the following entries to the file:
   ```properties
   MAPS_API_KEY=your_api_key_here   # Replace with the API key provided in Canvas
   sdk.dir=/Users/<your_username>/Library/Android/sdk   # Update this path based on your system
   ```

   **Note**:
    - macOS: `/Users/<your_username>/Library/Android/sdk`
    - Windows: `C:\\Users\\<your_username>\\AppData\\Local\\Android\\sdk`
    - Linux: `/home/<your_username>/Android/Sdk`

### **3. Clone the Repository**
Clone the repository using the following command:
```bash
git clone <repository-url>
```

### **4. Open in Android Studio**
- Launch Android Studio and open the project directory.

### **5. Build and Run**
- Add your Google Maps API key in `local.properties`.
- Build the project and run it on an emulator or a physical device.

---

## **Team Contributions**
| Team Member          | Contributions                                                                 |
|-----------------------|-------------------------------------------------------------------------------|
| **Jeffrey Loverock**  | Profile page, Themes, Backend                                                |
| **Kristina Tretiakova** | Messaging, Backend, Login/Registration, Navigation, Games page              |
| **Taiga Okuma**       | MVVM Design, Map Integration, Nearest Court Feature, Backend                 |
| **Paul Atwal**        | Players page, Invites, Bookings Page, Backend                                |

---
