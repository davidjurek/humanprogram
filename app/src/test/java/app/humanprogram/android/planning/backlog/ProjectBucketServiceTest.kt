package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectBucketServiceTest {
    private val service = ProjectBucketService()

    @Test
    fun projectNamesAreUniqueCaseInsensitively() {
        assertFalse(service.isUniqueName("health", listOf("Health")))
        assertTrue(service.isUniqueName("School", listOf("Health")))
    }

    @Test
    fun blankProjectDisplaysAsUnorganized() {
        assertEquals("Unorganized", service.displayName(""))
    }

    @Test
    fun deleteProjectOnlyMovesItemsToUnorganized() {
        val result = service.deleteProject(
            projectName = "Health",
            items = listOf(BacklogItem(title = "Doctor", projectBucket = "Health")),
            mode = ProjectDeleteMode.DELETE_PROJECT_ONLY
        )

        assertEquals("", result.remainingItems.first().projectBucket)
        assertEquals(BacklogStatus.BACKLOG, result.remainingItems.first().status)
    }

    @Test
    fun deleteProjectAndItemsMarksItemsDone() {
        val result = service.deleteProject(
            projectName = "Health",
            items = listOf(BacklogItem(title = "Doctor", projectBucket = "Health")),
            mode = ProjectDeleteMode.DELETE_PROJECT_AND_ITEMS
        )

        assertEquals(BacklogStatus.DONE, result.remainingItems.first().status)
    }
}
