package dev.gaphunter.cmakecompanion.commands

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CMakeCommandIndexTest {
    @Test
    fun `recognizes common commands case-insensitively`() {
        assertTrue(CMakeCommandIndex.isKnownCommand("add_executable"))
        assertTrue(CMakeCommandIndex.isKnownCommand("ADD_EXECUTABLE"))
        assertTrue(CMakeCommandIndex.isKnownCommand("Add_Executable"))
        assertTrue(CMakeCommandIndex.isKnownCommand("target_link_libraries"))
        assertTrue(CMakeCommandIndex.isKnownCommand("cmake_minimum_required"))
    }

    @Test
    fun `rejects arbitrary words`() {
        assertFalse(CMakeCommandIndex.isKnownCommand("myapp"))
        assertFalse(CMakeCommandIndex.isKnownCommand("main.cpp"))
        assertFalse(CMakeCommandIndex.isKnownCommand(""))
    }
}
