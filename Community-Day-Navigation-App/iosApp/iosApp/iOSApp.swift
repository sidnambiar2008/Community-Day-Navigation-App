import SwiftUI
import FirebaseCore // 1. Add this

@main
struct iOSApp: App {
    
    init() {
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
