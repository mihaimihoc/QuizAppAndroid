# QuizAppAndroid

This is a Quiz Game Android application with a PHP backend and MySQL database.

## Features:
* User Authentication (Registration, Login)
* Quiz Gameplay with various categories
* Leaderboard based on user scores and response times
* Coin system
* Chest opening feature with rarity-based item drops for profile customization

## Technologies Used:
* **Frontend:** Android (Java)
* **Backend:** PHP
* **Database:** MySQL (via XAMPP)

## Demo Video:
Check out a short demo of the app in action on YouTube:
[Youtube Video]
(https://www.youtube.com/watch?v=MQ7TWuAYnl0)

## Setup Instructions:

Follow these steps to get the Quiz Game app up and running on your local machine.

### 1. Backend Setup (XAMPP & MySQL)

1.  **Install XAMPP:**
    If you don't have XAMPP installed, download and install it from the official website: [https://www.apachefriends.org/index.html](https://www.apachefriends.org/index.html)

2.  **Start XAMPP Services:**
    Open the XAMPP Control Panel and start the `Apache` and `MySQL` services.

3.  **Setup PHP Backend Files:**
    * Navigate to your XAMPP installation directory (e.g., `C:\xampp` on Windows).
    * Go to the `htdocs` folder.
    * Create a new folder inside `htdocs` named `quiz_api`.
    * Copy all the PHP files from the `php-backend/` directory of this repository into `C:\xampp\htdocs\quiz_api\`.

4.  **Import MySQL Database:**
    * Open your web browser and go to `http://localhost/phpmyadmin/`.
    * In phpMyAdmin, click on the "Databases" tab.
    * In the "Create database" section, enter `quiz_game` as the database name and click "Create".
    * Select the newly created `quiz_game` database from the left sidebar.
    * Click on the "Import" tab.
    * Click "Choose File" and select the `quiz_game.sql` file from the `php-backend/` directory of this repository.
    * Scroll down and click "Go" to import the database.

    *Expected Tables after import:*
    * `accounts`
    * `chests`
    * `profile_items`
    * `questions`
    * `user_inventory`
    * `user_progress`

### 2. Android App Setup (Android Studio)

1.  **Install Android Studio:**
    If you don't have Android Studio installed, download and install it from the official website: [https://developer.android.com/studio](https://developer.android.com/studio)

2.  **Open Project in Android Studio:**
    * Open Android Studio.
    * Select "Open an Existing Project".
    * Navigate to the `android-app/` directory of this repository and select it.
    * Android Studio will now set up the project. Wait for Gradle to sync completely.

3.  **Update Base URL in Android App:**
    * You need to update the base URL in your Android app to point to your local PHP backend.
    * Locate the file in your Android project where you define your API base URL (e.g., a `Constants.java` file, or within your `Retrofit` client setup).
    * Change the URL to point to your XAMPP server. If you are running the app on an emulator, use `http://10.0.2.2/quiz_api/`. If you are running on a physical device connected to the same network as your XAMPP server, use your PC's local IP address (e.g., `http://192.168.1.X/quiz_api/`).

    *Example (assuming a `Constants.java` file):*
    ```java
    public class Constants {
        public static final String BASE_URL = "[http://10.0.2.2/quiz_api/](http://10.0.2.2/quiz_api/)"; // For Android Emulator
        // public static final String BASE_URL = "http://YOUR_PC_IP_ADDRESS/quiz_api/"; // For Physical Device
    }
    ```
    *Replace `YOUR_PC_IP_ADDRESS` with your actual local IP address if using a physical device.*

4.  **Run the Android Application:**
    * Connect an Android device or start an Android Emulator.
    * Click the "Run" button (green play icon) in Android Studio to build and run the app on your selected device/emulator.

## Troubleshooting:

* **"Could not connect to server" or Network Errors:**
    * Ensure Apache and MySQL are running in XAMPP.
    * Double-check the `BASE_URL` in your Android app.
    * If using a physical device, ensure your phone and PC are on the same Wi-Fi network and that your PC's firewall isn't blocking incoming connections to Apache.
    * Try pinging your PC's IP address from your Android device using a terminal app if you have one.

* **PHP Errors:**
    * Check your Apache error logs in XAMPP (`xampp\apache\logs\error.log`).
    * Ensure all PHP files are correctly placed in `C:\xampp\htdocs\quiz_api\`.

* **Database Connection Errors:**
    * Verify the database name (`quiz_game`) is correct in your PHP connection files.
    * Ensure the MySQL service is running in XAMPP.

## Contributing:

Feel free to fork this repository, make improvements, and submit pull requests.
