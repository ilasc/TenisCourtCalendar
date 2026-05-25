package com.apulum.tenis

import android.app.Application
import com.apulum.tenis.data.local.SessionStore
import com.apulum.tenis.data.repository.TenisRepository

class ApulumTenisApp : Application() {
    lateinit var sessionStore: SessionStore
        private set
    lateinit var repository: TenisRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionStore = SessionStore(this)
        repository = TenisRepository(sessionStore)
    }
}
