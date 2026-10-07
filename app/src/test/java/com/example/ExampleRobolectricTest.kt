package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LeadCapture Pro", appName)
  }

  @Test
  fun `auth repository default credentials`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authRepo = AuthRepository(context)
    val isValid = authRepo.authenticate("admin@leadflow.io", "admin123")
    assertTrue(isValid)
  }
}
