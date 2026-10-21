from pathlib import Path
r=Path('/workspace/russian-companion');w=Path(__file__).parent;p=r/'app/src/test/java/dev/russiancompanion/v2/review/UnitReviewPipelineTest.kt';s=p.read_text().replace('import dev.russiancompanion.v2.curriculum.V2PackLoader','import dev.russiancompanion.v2.RecordedIntakeCorrectionScope\nimport dev.russiancompanion.v2.curriculum.V2PackLoader')
needle='''        assertEquals("1ebcde1ebabf867b08e137a96bf30f56cccbb0ea85cfd8ecb54cee463a79bf11", V2Validator.unitContentReviewDigest(pack, 4))''';assert s.count(needle)==1
s=s.replace(needle,'''        val priorUnit4 = RecordedIntakeCorrectionScope.load(repo(), "49a926c5ace0459edf7712ca5d90db4a63ff9d31")
        assertEquals("1ebcde1ebabf867b08e137a96bf30f56cccbb0ea85cfd8ecb54cee463a79bf11", V2Validator.unitContentReviewDigest(priorUnit4, 4))
        val currentUnit4Source = RecordedIntakeCorrectionScope.load(repo(), "e6db0bdfe0bbb40edf190623e2123014946f305b")
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(pack, currentUnit4Source, 4)
        assertNotEquals(V2Validator.unitContentReviewDigest(priorUnit4, 4), V2Validator.unitContentReviewDigest(pack, 4))''')
(w/'UnitReviewPipelineTest.followup.kt').write_text(s)
