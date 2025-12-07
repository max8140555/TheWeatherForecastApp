# TheWeatherForecastApp

這是一款以 Jetpack Compose 打造的天氣預報 Android 應用程式

## 主要功能

* **探索天氣**：使用者可以在主畫面 (`:feature:home`) 輸入任何想查詢的城市，應用程式會透過 API 取得即時天氣資料
* **詳細預報**：在天氣資訊頁 (`:feature:weather`)，使用者可以一目了然地看到包含溫度、風速、濕度在內的詳細天氣狀況以及未來幾天的預報
* **歷史紀錄**：主畫面會自動記錄您的查詢歷史，讓您無需重複輸入，即可快速查看關心的城市

## 專案架構

採用多模組架構，將不同職責分層，提升維護性、擴增性與建置效率

* `:app` — 主應用模組，負責整合 Feature 與 Core
* `:core:data` — 資料來源管理，包含 Repository、Local、Remote
* `:core:domain` — Use Case、Domain Model 與商業邏輯
* `:core:network` — API Service、HTTP 傳輸與 DTO
* `:core:ui` — 共用 UI Components、主題設定、錯誤字串映射與 UI 工具
* `:feature:home` — 搜尋與歷史紀錄畫面
* `:feature:weather` — 天氣資訊顯示畫面

## 使用技術與函式庫

- Kotlin / Coroutines / Flow — 非同步流程處理
- Hilt — 依賴注入
- Jetpack Compose — 宣告式 UI
- ViewModel — UI 狀態管理
- Navigation Compose — 畫面導航
- Retrofit + OkHttp — HTTP 請求與 API 呼叫
- Room + DataStore — 本地資料儲存
- Moshi — JSON 與 Data Class 轉換
- JUnit + MockK + Compose Test — 測試工具

## AI 協作

開發過程中使用 AI 作為協作工具，用於提升開發效率，架構設計、程式品質、重構與最終決策仍由開發者負責

* **Android Studio Gemini**
    - 建立初始模組結構
    - 生成 API Data Class 與 UI / ViewModel 樣板
    - 產生部分測試樣板

* **ChatGPT**
    - 協助處理跨模組與 DI 設計問題
    - 複雜 UI 與測試情境討論
