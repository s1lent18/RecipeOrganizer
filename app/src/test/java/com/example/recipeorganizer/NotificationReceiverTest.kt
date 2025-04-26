package com.example.recipeorganizer

import android.app.Notification
import com.example.recipeorganizer.viewmodel.NotificationReceiver
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNotificationManager
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28],
    manifest = Config.NONE
)
class NotificationContentTest {

    private lateinit var receiver: NotificationReceiver
    private lateinit var context: Context
    private lateinit var shadowNotificationManager: ShadowNotificationManager

    @Before
    fun setup() {
        receiver = NotificationReceiver()
        context = ApplicationProvider.getApplicationContext()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        shadowNotificationManager = Shadows.shadowOf(notificationManager)

    }

    @Test
    fun `verify notification content matches provided values`() {
        val testTitle = "Recipe Ready!"
        val testMessage = "Your pizza is ready to eat!"

        val intent = Intent().apply {
            putExtra("title", testTitle)
            putExtra("message", testMessage)
        }

        receiver.onReceive(context, intent)

        val notifications = shadowNotificationManager.allNotifications

        assertFalse("A notification should be posted", notifications.isEmpty())

        val notification = notifications[0]
        val shadowNotification = Shadows.shadowOf(notification)

        assertEquals("Notification title should match", testTitle, shadowNotification.contentTitle)
        assertEquals("Notification text should match", testMessage, shadowNotification.contentText)

        assertNotNull("Notification should have an icon", notification.smallIcon)

        assertEquals("Notification should have default priority",
            Notification.PRIORITY_DEFAULT, notification.priority)
    }
    @Test
    fun `notification should show default message when message extra is missing`() {
        val intent = Intent().apply {
            putExtra("title", "Test title")
        }

        receiver.onReceive(context, intent)

        val notifications = shadowNotificationManager.allNotifications
        assertFalse("Notification should be posted", notifications.isEmpty())

        val shadowNotification = Shadows.shadowOf(notifications[0])
        assertEquals("Test title", shadowNotification.contentTitle)
        assertEquals("Default Message", shadowNotification.contentText)
    }

}