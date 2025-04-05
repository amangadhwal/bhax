package com.bhax.app

import android.app.Application
import com.bhax.app.data.LocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BhakshApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    
    val database by lazy {
        LocationDatabase.getDatabase(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize other components if needed
    }
}
