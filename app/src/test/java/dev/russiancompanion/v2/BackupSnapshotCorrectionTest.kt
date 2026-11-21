package dev.russiancompanion.v2
import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2BackupCoordinator
import dev.russiancompanion.v2.ui.V2BackupPhase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class BackupSnapshotCorrectionTest {
    @Test fun concurrentLearnerWritesCannotBeClaimedAsPartOfTheWrittenArchive() {
        val p=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
        val context:Context=ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            val temp=File(context.cacheDir,"snapshot-export-${System.nanoTime()}").apply { mkdirs() }
            val coordinator=V2BackupCoordinator(store,"dev.russiancompanion",p.packId,p.packVersion,AppearanceStore.memory(AppearancePreference.DARK),temp)
            val entered=CountDownLatch(1);val release=CountDownLatch(1);val once=AtomicBoolean(false)
            val bytes=ByteArrayOutputStream()
            val output=object:OutputStream() {
                private fun gate() {
                    if(once.compareAndSet(false,true)) { entered.countDown();check(release.await(10,TimeUnit.SECONDS)) }
                }
                override fun write(b:Int) { gate();bytes.write(b) }
                override fun write(b:ByteArray,off:Int,len:Int) { gate();bytes.write(b,off,len) }
            }
            try {
                assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
                coordinator.createBackup({output},"2026-10-12T19:00:00")
                await { entered.count==0L }
                // The first ZIP write occurs after the immutable learner document was captured.
                val unit=p.unit(1)!!;val step=unit.sessions.flatMap { it.steps }.first { it.itemIds.isNotEmpty() }
                val item=p.item(step.itemIds.first())!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,item.id,item.answer,PRESCRIBED_CHECK,step.id,"2026-10-12T19:01:00")!!)
                store.v2AddNote("u1-st-orient","Added after archive capture","2026-10-12T19:01:00")
                release.countDown()
                await { coordinator.phase==V2BackupPhase.COMPLETED || coordinator.phase==V2BackupPhase.FAILED }
                assertEquals(V2BackupPhase.COMPLETED,coordinator.phase)
                val read=readGovernedBackup(bytes.toByteArray().inputStream(),"dev.russiancompanion",p.packId,p.packVersion,File(temp,"read"))
                assertTrue(read is BackupReadResult.Valid)
                val document=(read as BackupReadResult.Valid).backup.document
                assertTrue(document.attempts.isEmpty());assertTrue(document.notes.isEmpty())
                assertEquals(1,store.v2Attempts().size);assertEquals(1,store.v2Notes().size)
                val record=store.exports().single()
                assertEquals("Export record must describe archive bytes, not the later live state",document.attempts.size+document.evidence.size,record.evidenceCount)
                assertTrue("Newer writes must be visibly excluded from protection",coordinator.message.contains("Newer learner changes were not included"))
            } finally {
                release.countDown();coordinator.teardownForTests();temp.deleteRecursively()
            }
        }
    }
    private fun await(condition:()->Boolean) {
        val deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(10)
        while(System.nanoTime()<deadline) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            if(condition())return
            Thread.sleep(10)
        }
        fail("Timed out waiting for backup boundary")
    }
}
