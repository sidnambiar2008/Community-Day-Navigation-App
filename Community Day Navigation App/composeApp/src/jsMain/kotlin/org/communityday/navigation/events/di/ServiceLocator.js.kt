package org.communityday.navigation.events.di

import org.communityday.navigation.events.data.EventServiceFactory

actual class PlatformServiceLocator : ServiceLocator {
    actual override fun createEventService(): EventService {
        return EventServiceFactory.createEventService()
    }
}
