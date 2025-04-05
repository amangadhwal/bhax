package com.bhax.app

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class MainActivityTest {
    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        activity = MainActivity()
    }

    @Test
    fun testActivityNotNull() {
        assertNotNull(activity)
    }

    // Add more test cases as functionality is implemented
    @Test
    fun testAppName() {
        // This is a placeholder test to demonstrate testing structure
        val expectedName = "Bhaksh"
        assertTrue("App name should contain Bhaksh", expectedName.contains("Bhaksh"))
    }
}