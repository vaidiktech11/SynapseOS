package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.SecurityManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SynapseOS", appName)
  }

  @Test
  fun `security manager pin verification`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val securityManager = SecurityManager(context)
    val isValid = securityManager.verifyPin("123456", null)
    assertTrue(isValid)
  }
}
