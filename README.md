# DragRacing-2D

Ứng dụng game đua xe 2D trên Android. Người chơi có thể chọn xe, nâng cấp xe,
tham gia các chặng đua và theo dõi tiến trình chế độ Career.

## Cấu trúc dự án

```text
DragRacing-2D/
├── app/
│   ├── build.gradle.kts             # Cấu hình module Android và dependency
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  # Khai báo application, activity và permission
│           ├── java/com/dragracing/game/
│           │   ├── MainActivity.java
│           │   ├── audio/
│           │   │   └── SoundManager.java
│           │   ├── data/            # Dữ liệu xe và dữ liệu người chơi
│           │   │   ├── Car.java
│           │   │   ├── CarDatabase.java
│           │   │   └── PlayerData.java
│           │   ├── engine/          # Logic vật lý và vòng đời cuộc đua
│           │   │   ├── CarPhysics.java
│           │   │   └── RaceEngine.java
│           │   ├── render/          # Vẽ xe và đường đua
│           │   │   ├── CarRenderer.java
│           │   │   └── TrackRenderer.java
│           │   └── ui/              # Activity, View và dialog của giao diện
│           │       ├── CarPreviewView.java
│           │       ├── CareerActivity.java
│           │       ├── GameActivity.java
│           │       ├── GameView.java
│           │       ├── GarageActivity.java
│           │       ├── PauseDialog.java
│           │       └── ResultDialog.java
│           └── res/
│               ├── drawable/        # Hình ảnh xe, background và drawable XML
│               ├── font/            # Font pixel của game
│               ├── layout/          # Layout XML cho Activity, item và dialog
│               └── values/          # String, color, ID và theme
├── gradle/
│   ├── libs.versions.toml           # Quản lý version dependency/plugin
│   └── wrapper/                     # Gradle Wrapper
├── build.gradle.kts                 # Cấu hình plugin cấp project
├── settings.gradle.kts              # Tên project và module được include
├── gradle.properties
├── gradlew / gradlew.bat            # Chạy Gradle không cần cài Gradle riêng
└── map tilesets/                    # Tài nguyên tileset của bản đồ
```

### Các thành phần chính

- `data`: mô hình xe, danh sách xe và dữ liệu tiến trình người chơi.
- `engine`: xử lý vật lý xe và logic điều khiển cuộc đua.
- `render`: chịu trách nhiệm vẽ xe, đường đua và các thành phần đồ họa.
- `ui`: các màn hình chính, gara, Career, màn hình đua và hộp thoại kết quả/tạm dừng.
- `res`: tài nguyên giao diện, hình ảnh, font và cấu hình hiển thị Android.

## Yêu cầu môi trường

- Android Studio phiên bản hỗ trợ Android Gradle Plugin `9.2.1`.
- JDK 11.
- Android SDK Platform 36.1 (compile SDK).
- Thiết bị thật hoặc Android Emulator có Android API 24 trở lên.

## Mở và chạy ứng dụng

1. Mở Android Studio và chọn **Open**.
2. Chọn thư mục gốc `DragRacing-2D` (thư mục chứa `settings.gradle.kts`).
3. Chờ Android Studio hoàn tất **Gradle Sync**. Nếu được hỏi, chọn JDK 11 cho Gradle.
4. Chọn cấu hình chạy `app`, chọn một Android Emulator hoặc thiết bị thật.
5. Nhấn **Run** (nút tam giác màu xanh) để build và cài ứng dụng.

Có thể build từ Terminal tại thư mục gốc bằng Gradle Wrapper:

```powershell
.\gradlew.bat assembleDebug
```

APK debug được tạo trong `app/build/outputs/apk/debug/`.

## Chạy test trên Android Studio

### 1. Chuẩn bị

- Mở project và chờ Gradle Sync hoàn tất.
- Kết nối thiết bị Android đã bật **USB debugging**, hoặc khởi động một Emulator.
- Nếu tạo test mới, đặt test Java trong:
  - `app/src/test/java/` cho **local unit test** chạy trên JVM.
  - `app/src/androidTest/java/` cho **instrumented test** chạy trên thiết bị/Emulator.

Hiện project đã khai báo JUnit cho local unit test và AndroidX test runner; các thư
mục test chỉ cần tạo khi bổ sung test case.

### 2. Chạy local unit test

Trong Android Studio:

1. Mở cửa sổ **Project** và tìm một file test trong `app/src/test`.
2. Nhấp chuột phải vào class hoặc method test.
3. Chọn **Run '<Tên_test>'**.

Hoặc chạy toàn bộ unit test của module bằng Terminal:

```powershell
.\gradlew.bat testDebugUnitTest
```

Kết quả HTML nằm tại `app/build/reports/tests/testDebugUnitTest/index.html`.

### 3. Chạy instrumented test trên Emulator/thiết bị

1. Bảo đảm Emulator/thiết bị đã khởi động và xuất hiện trong danh sách thiết bị
   của Android Studio.
2. Mở file test trong `app/src/androidTest`.
3. Nhấp chuột phải vào class hoặc method test, chọn **Run**.
4. Chọn thiết bị đích nếu Android Studio yêu cầu.

Có thể chạy bằng Terminal:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Kết quả báo cáo nằm tại `app/build/reports/androidTests/connected/`.

### 4. Chạy test bằng cấu hình Android Studio

Chọn **Run > Edit Configurations...**, nhấn **+**, chọn **Android Instrumented
Tests**, đặt module là `app`, chọn test class/package và thiết bị đích, sau đó
nhấn **Run**. Với unit test, chọn loại cấu hình **JUnit** và đặt
**Test kind** là class/package tương ứng trong `app/src/test`.

Nếu test không chạy, kiểm tra lại Gradle Sync, JDK 11, SDK đã cài và thiết bị
đã kết nối bằng cách chạy:

```powershell
adb devices
```
