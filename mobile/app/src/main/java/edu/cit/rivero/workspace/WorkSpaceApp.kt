package edu.cit.rivero.workspace

import android.app.Application
import edu.cit.rivero.workspace.api.ApiClient

class WorkSpaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Restore persisted JWT on cold start so all API calls are authenticated immediately
        ApiClient.init(SessionManager.getToken(this))
    }
}
